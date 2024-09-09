package com.example.ecommerce.service;

import com.example.ecommerce.exception.cart.CartItemNotFoundException;
import com.example.ecommerce.exception.cart.InsufficientStockException;
import com.example.ecommerce.mapper.CartItemMapper;
import com.example.ecommerce.mapper.CartMapper;
import com.example.ecommerce.model.Cart;
import com.example.ecommerce.model.CartItem;
import com.example.ecommerce.model.Product;
import com.example.ecommerce.payload.dto.CartDto;
import com.example.ecommerce.payload.dto.CartItemDto;
import com.example.ecommerce.payload.request.cart.CreateCartItemRequest;
import com.example.ecommerce.payload.request.cart.UpdateCartItemRequest;
import com.example.ecommerce.repository.CartItemRepository;
import com.example.ecommerce.repository.CartRepository;
import com.example.ecommerce.util.AuthUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CartService {

    private final AuthUtils authUtils;
    private final ProductService productService;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final CartMapper cartMapper;
    private final CartItemMapper cartItemMapper;

    protected Cart getCartByAuthenticatedUser() {
        return cartRepository
                .findByUser(authUtils.getCurrentUsername())
                .orElseGet(this::createCart);
    }

    protected CartItem getCartItemById(UUID cartItemId) {
        return cartItemRepository
                .findById(cartItemId)
                .orElseThrow(() -> new CartItemNotFoundException(cartItemId));
    }

    public CartDto getCart() {
        return cartMapper.mapToDto(getCartByAuthenticatedUser());
    }

    @Transactional
    public CartItemDto addItemToCart(CreateCartItemRequest request) {

        Cart cart = getCartByAuthenticatedUser();
        Product product = productService.getProductById(UUID.fromString(request.productId()));

        int quantity = request.quantity();

        checkStock(product.getId(), quantity);

        boolean hasDiscount = product.isDiscountAvailable();

        BigDecimal itemPrice, discountPerItem = null, totalDiscountAmount = null;
        if (hasDiscount) {
            itemPrice = product.getDiscountedPrice();
            discountPerItem = calculateDiscountedAmount(product.getDiscountPercentage(), product.getPrice());
            totalDiscountAmount = discountPerItem.multiply(BigDecimal.valueOf(quantity));
        } else {
            itemPrice = product.getPrice();
        }

        CartItem cartItem;
        BigDecimal amountToBeAdded = itemPrice.multiply(BigDecimal.valueOf(quantity));
        // If product exists in cart, update quantity
        if (cartItemRepository.existsByCartIdAndProductId(cart.getId(), product.getId())) {
            cartItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), product.getId());
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
            cartItem.setTotalPrice(cartItem.getTotalPrice().add(amountToBeAdded));

            if (hasDiscount) {
                BigDecimal currentDiscountAmount = cartItem.getTotalDiscountAmount();
                cartItem.setTotalDiscountAmount(currentDiscountAmount.add(totalDiscountAmount));
            }

        } else {
            cartItem = CartItem.builder()
                    .cart(cart)
                    .product(product)
                    .quantity(quantity)
                    .discountPerItem(discountPerItem)
                    .totalDiscountAmount(totalDiscountAmount)
                    .isDiscountApplied(hasDiscount)
                    .unitPrice(itemPrice)
                    .totalPrice(amountToBeAdded)
                    .build();
        }

        BigDecimal currentTotalPrice = cart.getTotalPrice();
        BigDecimal updatedTotalPrice = currentTotalPrice.add(amountToBeAdded);
        cart.setTotalPrice(updatedTotalPrice);

        return cartItemMapper.mapToDto(cartItemRepository.save(cartItem));
    }

    @Transactional
    public CartItemDto updateItemInCart(UUID cartItemId, UpdateCartItemRequest request) {

        Cart cart = getCartByAuthenticatedUser();
        CartItem cartItem = getCartItemById(cartItemId);

        int currentQuantity = cartItem.getQuantity();
        int updatedQuantity = request.quantity();

        checkStock(cartItem.getProduct().getId(), updatedQuantity);

        BigDecimal unitPrice = cartItem.getUnitPrice();
        BigDecimal oldTotalPrice = cartItem.getTotalPrice();
        BigDecimal newTotalPrice = unitPrice.multiply(BigDecimal.valueOf(updatedQuantity));

        // Changes applied to CartItem
        cartItem.setQuantity(updatedQuantity);
        cartItem.setTotalPrice(unitPrice.multiply(BigDecimal.valueOf(updatedQuantity)));

        BigDecimal priceDifference = newTotalPrice.subtract(oldTotalPrice).abs();
        BigDecimal discountAdjustment;

        if (cartItem.isDiscountApplied()) {
            int diff = Math.abs(updatedQuantity - currentQuantity);
            BigDecimal discountPerItem = cartItem.getDiscountPerItem();
            discountAdjustment = discountPerItem.multiply(BigDecimal.valueOf(diff));
            if (updatedQuantity < currentQuantity) {
                // If quantity decreases, values changed to negative
                priceDifference = priceDifference.negate(); // -(priceDifference)
                discountAdjustment = discountAdjustment.negate(); // -(discountAdjustment)
            }
            // Updating total discount amount
            cartItem.setTotalDiscountAmount(cartItem.getTotalDiscountAmount().add(discountAdjustment));
        }

        // Amount of change is applied to cart
        cart.setTotalPrice(cart.getTotalPrice().add(priceDifference));

        return cartItemMapper.mapToDto(cartItemRepository.save(cartItem));
    }

    @Transactional
    public void deleteItemFromCart(UUID cartItemId) {
        Cart cart = getCartByAuthenticatedUser();
        CartItem cartItem = getCartItemById(cartItemId);

        BigDecimal amountToBeDeducted = cartItem.getTotalPrice();

        BigDecimal currentCartTotalPrice = cart.getTotalPrice();
        cart.setTotalPrice(currentCartTotalPrice.subtract(amountToBeDeducted));

        cartItemRepository.delete(cartItem);
    }

    @Transactional
    void emptyCartAndUpdateStocks() {
        Cart cart = getCartByAuthenticatedUser();
        List<CartItem> cartItems = cart.getCartItems();

        for (CartItem cartItem : cartItems) {
            Product product = cartItem.getProduct();
            productService.decreaseStockForOrder(product.getId(), cartItem.getQuantity());
        }

        cart.getCartItems().clear();
        cart.setTotalPrice(BigDecimal.ZERO);
    }

    private void checkStock(UUID productId, int requestedQuantity) {
        int availableStockQuantity = productService.getAvailableStockQuantity(productId);
        if (availableStockQuantity < requestedQuantity) {
            throw new InsufficientStockException(availableStockQuantity, requestedQuantity);
        }
    }

    private BigDecimal calculateDiscountedAmount(double percentage, BigDecimal itemPrice) {
        return itemPrice.multiply(BigDecimal.valueOf(percentage / 100));
    }

    private Cart createCart() {
        Cart cart = Cart.builder()
                .user(authUtils.getCurrentUser())
                .cartItems(new ArrayList<>())
                .totalPrice(BigDecimal.ZERO)
                .build();

        return cartRepository.save(cart);
    }

}
