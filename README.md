# E-Commerce REST API

This project is an e-commerce REST API built using Java and Spring Boot.
The API allows users to interact with products, manage a shopping cart, handle user authentication,
and perform other essential e-commerce functionalities.

<p align="center">
  <img src="images/image.png" alt="ecommerce-image">
</p>

### Technologies Used

- Java 17
- Spring Boot
- Spring Security with JWT Authentication
- MySQL
- H2 (for testing purposes)
- Maven
- Docker and Docker Compose
- JUnit 5, Mockito and AssertJ for testing
- OpenAPI 3 for API documentation

### Role-Based Access Control

There are three predefined roles for role-based access control:

- **ROLE_USER:** Standard users with access to basic shopping features, including:
    - Managing their shopping cart.
    - Viewing and purchasing products.

- **ROLE_ADMIN:** Administrators responsible for managing the platform's inventory, with permissions to:
    - Add, update, and delete products.
    - Manage product categories.
    - *Note:* Admins cannot access cart or order-related functionalities.

- **ROLE_SUPER_ADMIN:** Super admin is only responsible for managing admins.

### How to Run

Clone the project and navigate to the root directory:

```shell
git clone git@github.com:yusufemrebilgin/e-commerce.git && cd e-commerce
```

**Option 1: Using Maven**

- Run the application:
  ```shell
  mvn spring-boot:run
  ```

- Run the application with sample data:
  ```shell
  mvn spring-boot:run -Dspring-boot.run.arguments="--createDummyData=true"
  ```

**Option 2: Using Docker Compose**

- Define variables in `.env` file:
  ```
  DB_USERNAME={DB_USERNAME}
  DB_PASSWORD={DB_PASSWORD}
  ```

- Build and run the application:
  ```shell
  docker compose up
  ```
- To start the application with sample data:
  ```shell
  CREATE_DUMMY_DATA=true docker compose up
  ```

- To stop the application:
  ```shell
  docker compose down
  ```

### Explore the Application

- API Documentation: http://localhost:8080/swagger-ui.html
- **Note:** The application runs on port `8080`. Make sure it is not being used by
  other services.
  <br><br>

- [Endpoints](#endpoints)
- [Example Calls](#example-calls)

### Endpoints

Base URL: `http://localhost:8080`

- **AuthController**

  | **Endpoint**            | **Method** | **Description**                                         | **Accessible By** |
  |-------------------------|------------|---------------------------------------------------------|-------------------|
  | `/api/v1/auth/login`    | POST       | Authenticates a user and returns authentication details | Everyone          |
  | `/api/v1/auth/register` | POST       | Registers a new user and returns a success message      | Everyone          |

- **AddressController**

  | **Endpoint**                    | **Method** | **Description**                                        | **Accessible By** |
  |---------------------------------|------------|--------------------------------------------------------|-------------------|
  | `/api/v1/addresses`             | GET        | Retrieve all addresses for the authenticated user      | ROLE_USER         |
  | `/api/v1/addresses`             | POST       | Creates a new address for the authenticated user       | ROLE_USER         |
  | `/api/v1/addresses/{addressId}` | PUT        | Updates an existing address for the authenticated user | ROLE_USER         |
  | `/api/v1/addresses/{addressId}` | DELETE     | Deletes an address for the authenticated user          | ROLE_USER         |

- **CartController**

  | **Endpoint**           | **Method** | **Description**                                                    | **Accessible By** |
  |------------------------|------------|--------------------------------------------------------------------|-------------------|
  | `/api/v1/cart/current` | GET        | Retrieves the current cart for the authenticated user              | ROLE_USER         |
  | `/api/v1/cart/summary` | GET        | Retrieves a summary of the current cart (total items, total price) | ROLE_USER         |
  | `/api/v1/cart/clear`   | DELETE     | Clears all items from the current cart for the authenticated user  | ROLE_USER         |

- **CartItemController**

  | **Endpoint**            | **Method** | **Description**                                          | **Accessible By** |
  |-------------------------|------------|----------------------------------------------------------|-------------------|
  | `/api/v1/cart/items`    | POST       | Adds a new item to the cart for the authenticated user   | ROLE_USER         |
  | `/api/v1/cart/{itemId}` | PUT        | Updates the quantity of an existing item in the cart     | ROLE_USER         |
  | `/api/v1/cart/{itemId}` | DELETE     | Removes an item from the cart for the authenticated user | ROLE_USER         |

- **OrderController**

  | **Endpoint**                      | **Method** | **Description**                                                 | **Accessible By** |
  |-----------------------------------|------------|-----------------------------------------------------------------|-------------------|
  | `/api/v1/orders`                  | GET        | Retrieves all orders with pagination for the authenticated user | ROLE_USER         |
  | `/api/v1/orders/{orderId}`        | GET        | Retrieves an order by its unique identifier                     | ROLE_USER         |
  | `/api/v1/orders/checkout`         | POST       | Places a new order for the authenticated user                   | ROLE_USER         |
  | `/api/v1/orders/{orderId}/cancel` | DELETE     | Cancels an existing order for the authenticated user            | ROLE_USER         |

- **CategoryController**

  | **Endpoint**                      | **Method** | **Description**                          | **Accessible By**   |
  |-----------------------------------|------------|------------------------------------------|---------------------|
  | `/api/v1/categories`              | GET        | Retrieves all categories with pagination | Authenticated Users |
  | `/api/v1/categories`              | POST       | Creates a new category                   | ROLE_ADMIN          |
  | `/api/v1/categories/{categoryId}` | PUT        | Updates an existing category             | ROLE_ADMIN          |
  | `/api/v1/categories/{categoryId}` | DELETE     | Deletes a category                       | ROLE_ADMIN          |

- **ProductController**

  | **Endpoint**                             | **Method** | **Description**                                           | **Accessible By**   |
  |------------------------------------------|------------|-----------------------------------------------------------|---------------------|
  | `/api/v1/products/{productId}`           | GET        | Retrieves a product by its unique identifier              | Authenticated Users |
  | `/api/v1/products`                       | GET        | Retrieves all products with pagination                    | Authenticated Users |
  | `/api/v1/products/search`                | GET        | Retrieves products by their name with pagination          | Authenticated Users |
  | `/api/v1/products/category/{categoryId}` | GET        | Retrieves products by category identifier with pagination | Authenticated Users |
  | `/api/v1/products`                       | POST       | Creates a new product                                     | ROLE_ADMIN          |
  | `/api/v1/products/{productId}`           | PUT        | User Updates an existing product                          | ROLE_ADMIN          |
  | `/api/v1/products/{productId}`           | DELETE     | Deletes a product by its unique identifier                | ROLE_ADMIN          |

- **ProductImageController**

  | **Endpoint**                                     | **Method** | **Description**                                                                   | **Accessible By**   |
  |--------------------------------------------------|------------|-----------------------------------------------------------------------------------|---------------------|
  | `/api/v1/products/{productId}/images`            | POST       | Uploads images for a specific product and returns the URLs of the uploaded images | ROLE_ADMIN          |
  | `/api/v1/products/{productId}/images/{filename}` | GET        | Retrieves an image by its filename for a specific product                         | Authenticated Users |
  | `/api/v1/products/{productId}/images`            | GET        | Retrieves all image URLs associated with a specific product                       | Authenticated Users |
  | `/api/v1/products/{productId}/images/delete`     | DELETE     | Deletes specified images for a specific product                                   | ROLE_ADMIN          |
  | `/api/v1/products/{productId}/images/delete-all` | DELETE     | Deletes all images associated with a specific product                             | ROLE_ADMIN          |

### Example Calls