package com.example.ecommerce.product.service;

import com.example.ecommerce.product.exception.EmptyFileException;
import com.example.ecommerce.product.exception.FileStorageException;
import com.example.ecommerce.product.exception.InvalidFileTypeException;
import com.example.ecommerce.product.exception.ProductImageNotFoundException;
import com.example.ecommerce.product.payload.response.ProductImageResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;

/**
 * Service interface for handling product image-related operations such as uploading, retrieving,
 * deleting, and validating product images in an e-commerce system.
 */
public interface ProductImageService {

    /**
     * Uploads multiple product images to the server and associates them with a given product.
     *
     * @param productId   the ID of the product to associate the images with
     * @param files       the array of files to upload
     * @param urlTemplate the URL template for generating image URLs (with placeholder for filename)
     * @return a list of URLs for the uploaded images
     * @throws EmptyFileException       if any file is empty
     * @throws InvalidFileTypeException if any file has an invalid type
     * @throws FileStorageException     if there is an error during file storage
     */
    List<String> uploadProductImages(String productId, MultipartFile[] files, String urlTemplate);

    /**
     * Retrieves a product image by its filename for a specific product.
     *
     * @param productId the ID of the product for which the image is requested
     * @param filename  the filename of the image to retrieve
     * @return a {@link ProductImageResponse} containing the image's content type and data
     * @throws ProductImageNotFoundException if the image is not found for the given product and filename
     */
    ProductImageResponse getProductImage(String productId, String filename);

    /**
     * Retrieves all image URLs associated with a specific product.
     *
     * @param productId the ID of the product for which the image URLs are requested
     * @return a list of image URLs for the product
     */
    List<String> getAllProductImageUrls(String productId);

    /**
     * Deletes multiple product images associated with a specific product.
     *
     * @param productId the ID of the product for which images are to be deleted
     * @param filenames a set of filenames for the images to be deleted
     * @throws ProductImageNotFoundException if no images are found to delete it for the product
     */
    void deleteProductImages(String productId, Set<String> filenames);

    /**
     * Deletes all product images associated with a specific product.
     *
     * @param productId the ID of the product for which all images are to be deleted
     */
    void deleteAllProductImages(String productId);

}
