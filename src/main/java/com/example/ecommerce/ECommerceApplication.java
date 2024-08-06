package com.example.ecommerce;

import com.example.ecommerce.model.Category;
import com.example.ecommerce.repository.CategoryRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class ECommerceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ECommerceApplication.class, args);
	}

	@Bean
	CommandLineRunner initDatabase(CategoryRepository repository) {
		return args -> {
			List<Category> categories = new ArrayList<>();
			for (int i = 0; i < 35; i++) {
				categories.add(new Category((long) i, "Test" + i));
			}
			repository.saveAll(categories);
		};
	}

}
