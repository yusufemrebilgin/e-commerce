package com.example.ecommerce.cart.service;

import com.example.ecommerce.cart.exception.CartItemNotFoundException;
import com.example.ecommerce.cart.mapper.CartItemMapper;
import com.example.ecommerce.cart.model.Cart;
import com.example.ecommerce.cart.model.CartItem;
import com.example.ecommerce.cart.payload.response.CartItemResponse;
import com.example.ecommerce.cart.payload.request.CreateCartItemRequest;
import com.example.ecommerce.cart.payload.request.UpdateCartItemRequest;
import com.example.ecommerce.cart.repository.CartItemRepository;
import com.example.ecommerce.product.exception.InsufficientStockException;
import com.example.ecommerce.product.model.Product;
import com.example.ecommerce.cart.model.embeddable.DiscountInfo;
import com.example.ecommerce.cart.model.embeddable.ProductInfo;
import com.example.ecommerce.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CartItemService {

    private final CartService cartService;
    private final ProductService productService;

    private final CartItemMapper cartItemMapper;
    private final CartItemRepository cartItemRepository;

    private static final Logger log = LoggerFactory.getLogger(CartItemService.class);

    /**
     * Retrieves a cart item by its ID.
     *
     * @param cartItemId UUID of the cart item to retrieve
     * @return found {@link CartItem}
     * @throws CartItemNotFoundException if cart item is not found
     */
    protected CartItem findCartItemById(UUID cartItemId) {
        return cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> {
                    log.error("Cart item not found with id {}", cartItemId);
                    return new CartItemNotFoundException(cartItemId);
                });
    }

    /**
     * Adds a new item to user's cart or updates the quantity of if it already exists.
     *
     * @param createCartItemRequest request containing details of the item to be added
     * @return {@link CartItemResponse} containing added or updated cart item
     * @throws InsufficientStockException if available stock is less than requested quantity (by ProductService)
     */
    @Transactional
    public CartItemResponse addItemToCart(CreateCartItemRequest createCartItemRequest) {

        Cart currentUserCart = cartService.getCartByAuthenticatedUser();
        Product requestedProduct = productService.findProductById(UUID.fromString(createCartItemRequest.productId()));

        CartItem cartItem = cartItemRepository
                .findByCartIdAndProductId(currentUserCart.getId(), requestedProduct.getId())
                .orElseGet(() -> createNewItem(currentUserCart, requestedProduct));

        final int itemQuantity = cartItem.getQuantity() + createCartItemRequest.quantity();
        checkStockAvailability(itemQuantity, requestedProduct);

        ProductInfo productInfo = ProductInfo.calculateProductInfo(itemQuantity, requestedProduct);
        cartItem.setProductInfo(productInfo);

        applyDiscountIfExists(itemQuantity, cartItem, requestedProduct);

        BigDecimal amountToBeAdded = calculateTotalItemPrice(
                cartItem.getProductInfo(),
                cartItem.getDiscountInfo()
        );

        cartService.updateCartTotalPrice(amountToBeAdded);
        cartItem = cartItemRepository.save(cartItem);

        log.info("Item {} added to cart {}", cartItem.getId(), currentUserCart.getId());
        return cartItemMapper.mapToResponse(cartItem);
    }

    /**
     * Updates quantity of an existing cart item in user's cart.
     *
     * @param cartItemId            ID of the cart item to update
     * @param updateCartItemRequest request containing the updated quantity
     * @return {@link CartItemResponse} containing the updated cart item
     * @throws CartItemNotFoundException  if cart item with given UUID is not found
     * @throws InsufficientStockException if available stock is less than requested quantity (by ProductService)
     */
    @Transactional
    public CartItemResponse updateItemQuantityInCart(UUID cartItemId, UpdateCartItemRequest updateCartItemRequest) {

        CartItem existingCartItem = findCartItemById(cartItemId);

        final int currentQuantity = existingCartItem.getQuantity();
        final int updatedQuantity = updateCartItemRequest.quantity();

        Product product = existingCartItem.getProduct();
        checkStockAvailability(updatedQuantity, product);

        // Calculate new product and discount info
        ProductInfo oldProductInfo = existingCartItem.getProductInfo();
        ProductInfo newProductInfo = ProductInfo.calculateProductInfo(updatedQuantity, product);

        DiscountInfo oldDiscountInfo = existingCartItem.getDiscountInfo();
        DiscountInfo newDiscountInfo = DiscountInfo.calculateDiscountInfo(updatedQuantity, product);

        BigDecimal oldTotalPrice = calculateTotalItemPrice(oldProductInfo, oldDiscountInfo);
        BigDecimal newTotalPrice = calculateTotalItemPrice(newProductInfo, newDiscountInfo);
        BigDecimal priceDifference = newTotalPrice.subtract(oldTotalPrice).abs();

        // apply changes
        existingCartItem.setProductInfo(newProductInfo);
        existingCartItem.setDiscountInfo(newDiscountInfo);

        // If quantity decreases, price difference changed to negative
        priceDifference = currentQuantity < updatedQuantity
                ? priceDifference
                : priceDifference.negate();

        cartService.updateCartTotalPrice(priceDifference);

        CartItem updatedCartItem = cartItemRepository.save(existingCartItem);
        log.info("Updated quantity of cart item {} to {}", cartItemId, updatedQuantity);

        return cartItemMapper.mapToResponse(updatedCartItem);
    }

    /**
     * Removes an item from user's cart by its ID.
     *
     * @param cartItemId ID of the cart item to remove
     * @throws CartItemNotFoundException if cart item with given UUID is not found
     */
    @Transactional
    public void removeItemFromCart(UUID cartItemId) {

        Cart currentUserCart = cartService.getCartByAuthenticatedUser();
        CartItem existingCartItem = findCartItemById(cartItemId);

        BigDecimal amountToBeDeducted = calculateTotalItemPrice(
                existingCartItem.getProductInfo(), existingCartItem.getDiscountInfo()
        );

        // Passing amount as (-amountToBeDeducted)
        cartService.updateCartTotalPrice(amountToBeDeducted.negate());

        cartItemRepository.delete(existingCartItem);
        log.info("Removed item {} from cart {}", cartItemId, currentUserCart.getId());
    }

    /**
     * Creates a new {@code CartItem} for the given user cart and requested product.
     *
     * @param userCart         user's cart
     * @param requestedProduct product to be added
     * @return newly created {@link CartItem}
     */
    private CartItem createNewItem(Cart userCart, Product requestedProduct) {
        CartItem newCartItem = CartItem.builder()
                .cart(userCart)
                .product(requestedProduct)
                .productInfo(new ProductInfo())
                .discountInfo(new DiscountInfo())
                .build();

        log.info("New cart item created for product {}", requestedProduct.getId());
        return newCartItem;
    }

    /**
     * Checks stock availability for the requested quantity of the specified product.
     *
     * @param requestedQuantity requested quantity
     * @param product           product to check
     * @throws InsufficientStockException if available stock is less than requested quantity (by ProductService)
     */
    private void checkStockAvailability(int requestedQuantity, Product product) {
        productService.checkStock(product.getId(), requestedQuantity);
    }

    /**
     * Applies discount to the cart item if it exists based on the quantity and product.
     *
     * @param quantity quantity of the cart item
     * @param cartItem cart item to which the discount is applied
     * @param product  product associated with the cart item
     */
    private void applyDiscountIfExists(int quantity, CartItem cartItem, Product product) {
        cartItem.setDiscountInfo(DiscountInfo.calculateDiscountInfo(quantity, product));
    }

    /**
     * Calculates total price for a cart item based on its product info and any applicable discount.
     *
     * @param productInfo  product info of the cart item
     * @param discountInfo discount info of the cart item
     * @return total price of the cart item after applying discount
     */
    private BigDecimal calculateTotalItemPrice(ProductInfo productInfo, DiscountInfo discountInfo) {
        if (discountInfo.isDiscountApplied()) {
            return productInfo.getTotalPrice().subtract(discountInfo.getTotalDiscountAmount());
        }
        return productInfo.getTotalPrice();
    }

}
