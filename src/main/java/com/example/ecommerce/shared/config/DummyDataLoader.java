package com.example.ecommerce.shared.config;

import com.example.ecommerce.category.payload.request.CreateCategoryRequest;
import com.example.ecommerce.product.payload.request.CreateProductRequest;
import com.example.ecommerce.category.service.CategoryService;
import com.example.ecommerce.product.service.ProductService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.function.Consumer;

@Configuration
@RequiredArgsConstructor
public class DummyDataLoader {

    private static final Logger logger = LoggerFactory.getLogger(DummyDataLoader.class);

    @Value("${createDummyData:false}")
    private boolean createDummyData;

    private final ObjectMapper mapper;
    private final ProductService productService;
    private final CategoryService categoryService;

    @Bean
    CommandLineRunner load() {
        return args -> {
            if (!createDummyData) {
                logger.info("Dummy data loading is disabled. Set '--createDummyData=true' to enable.");
                return;
            }

            logger.info("Starting to load dummy data...");
            TypeReference<List<CreateCategoryRequest>> categoryRequestReference = new TypeReference<>() {};
            loadData("/data/categories.json", categoryRequestReference, categoryService::createCategory);

            TypeReference<List<CreateProductRequest>> createProductReference = new TypeReference<>() {};
            loadData("/data/products.json", createProductReference, productService::createProduct);
            logger.info("Dummy data loading is completed.");
        };
    }

    private <T> void loadData(String filename, TypeReference<List<T>> typeReference, Consumer<T> action) {
        // read json data and write it to db
        try (InputStream inputStream = TypeReference.class.getResourceAsStream(filename)) {
            if (inputStream == null) {
                logger.error("File not found {}", filename);
                return;
            }

            List<T> dataList = mapper.readValue(inputStream, typeReference);
            logger.info("Found {} records in {}", dataList.size(), filename);

            // Apply the given action (creating categories or products)
            dataList.forEach(action);
            logger.info("{} records from {} have been successfully saved.", dataList.size(), filename);

        } catch (IOException ex) {
            logger.error("Error occurred while loading data from {}", filename);
            throw new RuntimeException("Error occurred while loading data from " + filename, ex);
        }
    }

}
