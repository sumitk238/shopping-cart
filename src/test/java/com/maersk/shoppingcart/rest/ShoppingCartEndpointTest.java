package com.maersk.shoppingcart.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.maersk.shoppingcart.dto.CartDetails;
import com.maersk.shoppingcart.dto.ProblemDetails;
import com.maersk.shoppingcart.dto.ProductDetails;
import com.maersk.shoppingcart.entity.Cart;
import com.maersk.shoppingcart.entity.Product;
import com.maersk.shoppingcart.entity.User;
import com.maersk.shoppingcart.exception.handler.ShoppingCartExceptionHandler;
import com.maersk.shoppingcart.jpa.CartRepository;
import com.maersk.shoppingcart.jpa.ProductRepository;
import com.maersk.shoppingcart.jpa.UserRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * @author Sumit Kumar
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class ShoppingCartEndpointTest {

    private static final Integer TEST_USER_ID = 1;
    private static final Integer TEST_PRODUCT_ID = 10;
    private static final Integer TEST_QUANTITY = 5;

    private static final Integer TEST_SECOND_USER_ID = 2;
    private static final Integer TEST_SECOND_PRODUCT_ID = 20;
    private static final Integer TEST_SECOND_QUANTITY = 15;

    private static final double TEST_COST = 25.5;
    private static final String USERNAME = "admin";
    private static final String PASSWORD = "password";

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private ProductRepository productRepository;

    @MockBean
    private CartRepository cartRepository;

    @Autowired
    private TestRestTemplate restTemplate;

    /**
     * Auth Test Cases
     */
    @Test
    void testGetCartDetailsForUser_NoBasicAuthHeader_FailWith401() throws Exception {
        // ARRANGE
        HttpEntity<Void> request = new HttpEntity<>(null, null);

        // ACT
        ResponseEntity<ProblemDetails> response = restTemplate
                .exchange("/cart/1", HttpMethod.GET, request, ProblemDetails.class);

        // ASSERT
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());

        // verify mock interactions
        verify(userRepository, never()).findById(TEST_USER_ID);
        verify(productRepository, never()).findById(TEST_PRODUCT_ID);
        verify(productRepository, never()).findById(TEST_SECOND_PRODUCT_ID);
        verify(cartRepository, never()).findAllByUserId(TEST_USER_ID);
    }

    @Test
    void testGetCartDetailsForUser_InvalidAuthHeader_FailWith401() throws Exception {
        // ARRANGE
        HttpEntity<Void> request = new HttpEntity<>(null, null);

        // ACT
        ResponseEntity<ProblemDetails> response = restTemplate.withBasicAuth(USERNAME, "incorrect")
                .exchange("/cart/1", HttpMethod.GET, request, ProblemDetails.class);

        // ASSERT
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());

        // verify mock interactions
        verify(userRepository, never()).findById(TEST_USER_ID);
        verify(productRepository, never()).findById(TEST_PRODUCT_ID);
        verify(productRepository, never()).findById(TEST_SECOND_PRODUCT_ID);
        verify(cartRepository, never()).findAllByUserId(TEST_USER_ID);
    }

    /**
     * GetCartDetailsForUser Test Cases
     */

    @Test
    void testGetCartDetailsForUser_SuccessWith200() throws Exception {
        // ARRANGE
        HttpEntity<Void> request = new HttpEntity<>(null, null);
        when(userRepository.findById(TEST_USER_ID)).thenReturn(createUserById(TEST_USER_ID));
        when(cartRepository.findAllByUserId(TEST_USER_ID)).thenReturn(createCartList());
        when(productRepository.findById(TEST_PRODUCT_ID)).thenReturn(createProductById(TEST_PRODUCT_ID));
        when(productRepository.findById(TEST_SECOND_PRODUCT_ID)).thenReturn(createProductById(TEST_SECOND_PRODUCT_ID));

        // ACT
        ResponseEntity<CartDetails> response = restTemplate.withBasicAuth(USERNAME, PASSWORD)
                .exchange("/cart/1", HttpMethod.GET, request, CartDetails.class);

        // ASSERT
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());

        CartDetails cartDetails = response.getBody();
        assertNotNull(cartDetails);
        assertEquals(TEST_COST * (TEST_QUANTITY + TEST_SECOND_QUANTITY), cartDetails.getTotalCost());
        List<ProductDetails> productDetails = cartDetails.getProductDetails();
        assertEquals(2, productDetails.size());
        ProductDetails firstProductDetails = productDetails.get(0);
        assertEquals(TEST_PRODUCT_ID, firstProductDetails.getProductId());
        assertEquals(TEST_QUANTITY, firstProductDetails.getQuantity());
        ProductDetails secondProductDetails = productDetails.get(1);
        assertEquals(TEST_SECOND_PRODUCT_ID, secondProductDetails.getProductId());
        assertEquals(TEST_SECOND_QUANTITY, secondProductDetails.getQuantity());

        // verify mock interactions
        verify(userRepository, times(1)).findById(TEST_USER_ID);
        verify(productRepository, times(1)).findById(TEST_PRODUCT_ID);
        verify(productRepository, times(1)).findById(TEST_SECOND_PRODUCT_ID);
        verify(cartRepository, times(1)).findAllByUserId(TEST_USER_ID);
    }

    @Test
    void testGetCartDetailsForUser_FailWith500() throws Exception {
        // ARRANGE
        HttpEntity<Void> request = new HttpEntity<>(null, null);
        when(userRepository.findById(TEST_USER_ID)).thenThrow(new RuntimeException("Testing !!"));

        // ACT
        ResponseEntity<ProblemDetails> response = restTemplate.withBasicAuth(USERNAME, PASSWORD)
                .exchange("/cart/1", HttpMethod.GET, request, ProblemDetails.class);

        // ASSERT
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_PROBLEM_JSON, response.getHeaders().getContentType());
        assertNotNull(response.getBody());
        ProblemDetails problemDetails = response.getBody();
        assertEquals(ShoppingCartExceptionHandler.INTERNAL_SERVER_ERROR_MESSAGE, problemDetails.getReason());

        // verify mock interactions
        verify(userRepository, times(1)).findById(TEST_USER_ID);
        verify(productRepository, never()).findById(TEST_PRODUCT_ID);
        verify(productRepository, never()).findById(TEST_SECOND_PRODUCT_ID);
        verify(cartRepository, never()).findAllByUserId(TEST_USER_ID);
    }

    @Test
    void testGetCartDetailsForUser_FailWith400() throws Exception {
        // ARRANGE
        HttpEntity<Void> request = new HttpEntity<>(null, null);

        // ACT
        // user with userId 1 will not be found in system which will lead to BAD REQUEST EXCEPTION
        ResponseEntity<ProblemDetails> response = restTemplate.withBasicAuth(USERNAME, PASSWORD)
                .exchange("/cart/1", HttpMethod.GET, request, ProblemDetails.class);

        // ASSERT
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_PROBLEM_JSON, response.getHeaders().getContentType());
        assertNotNull(response.getBody());
        ProblemDetails problemDetails = response.getBody();
        assertEquals("User corresponding to id 1 doesn't exist !!", problemDetails.getReason());

        // verify mock interactions
        verify(userRepository, times(1)).findById(TEST_USER_ID);
        verify(productRepository, never()).findById(TEST_PRODUCT_ID);
        verify(productRepository, never()).findById(TEST_SECOND_PRODUCT_ID);
        verify(cartRepository, never()).findAllByUserId(TEST_USER_ID);
    }

    /**
     * GetCountOfItemInCart test cases
     */

    @Test
    void testGetCountOfItemInCart_ItemExistsInCart_SuccessWith200() throws Exception {
        // ARRANGE
        HttpEntity<Void> request = new HttpEntity<>(null, null);
        when(userRepository.findById(TEST_USER_ID)).thenReturn(createUserById(TEST_USER_ID));
        when(productRepository.findById(TEST_PRODUCT_ID)).thenReturn(createProductById(TEST_PRODUCT_ID));
        when(cartRepository.findByUserIdAndProductId(TEST_USER_ID, TEST_PRODUCT_ID))
                .thenReturn(createCartByUserIdAndProductId(TEST_USER_ID, TEST_PRODUCT_ID));

        // ACT
        ResponseEntity<String> response = restTemplate.withBasicAuth(USERNAME, PASSWORD)
                .exchange("/cart/1/10/", HttpMethod.GET, request, String.class);

        // ASSERT
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
        assertNotNull(response.getBody());
        String count = response.getBody();
        assertEquals(String.valueOf(TEST_QUANTITY), count);

        // verify mock interactions
        verify(userRepository, times(1)).findById(TEST_USER_ID);
        verify(productRepository, times(1)).findById(TEST_PRODUCT_ID);
        verify(cartRepository, times(1)).findByUserIdAndProductId(TEST_USER_ID, TEST_PRODUCT_ID);
    }

    @Test
    void testGetCountOfItemInCart_ItemDoesNotExistInCart_SuccessWith200() throws Exception {
        // ARRANGE
        HttpEntity<Void> request = new HttpEntity<>(null, null);
        when(userRepository.findById(TEST_USER_ID)).thenReturn(createUserById(TEST_USER_ID));
        when(productRepository.findById(TEST_PRODUCT_ID)).thenReturn(createProductById(TEST_PRODUCT_ID));

        // ACT
        ResponseEntity<String> response = restTemplate.withBasicAuth(USERNAME, PASSWORD)
                .exchange("/cart/1/10/", HttpMethod.GET, request, String.class);

        // ASSERT
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
        assertNotNull(response.getBody());
        String count = response.getBody();
        assertEquals("0", count);

        // verify mock interactions
        verify(userRepository, times(1)).findById(TEST_USER_ID);
        verify(productRepository, times(1)).findById(TEST_PRODUCT_ID);
        verify(cartRepository, times(1)).findByUserIdAndProductId(TEST_USER_ID, TEST_PRODUCT_ID);
    }

    @Test
    void testGetCountOfItemInCart_ItemDoesNotExistInCart_FailureWith400() throws Exception {
        // ARRANGE
        HttpEntity<Void> request = new HttpEntity<>(null, null);
        when(userRepository.findById(TEST_USER_ID)).thenReturn(createUserById(TEST_USER_ID));

        // ACT
        // product with productId 10 not present in DB, will lead to BAD REQUEST EXCEPTION
        ResponseEntity<ProblemDetails> response = restTemplate.withBasicAuth(USERNAME, PASSWORD)
                .exchange("/cart/1/10/", HttpMethod.GET, request, ProblemDetails.class);

        // ASSERT
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_PROBLEM_JSON, response.getHeaders().getContentType());
        assertNotNull(response.getBody());
        ProblemDetails problemDetails = response.getBody();
        assertEquals("Product corresponding to id 10 doesn't exist !!", problemDetails.getReason());

        // verify mock interactions
        verify(userRepository, times(1)).findById(TEST_USER_ID);
        verify(productRepository, times(1)).findById(TEST_PRODUCT_ID);
        verify(cartRepository, never()).findByUserIdAndProductId(TEST_USER_ID, TEST_PRODUCT_ID);
    }

    @Test
    void testGetCountOfItemInCart_FailureWith500() throws Exception {
        // ARRANGE
        HttpEntity<Void> request = new HttpEntity<>(null, null);
        when(userRepository.findById(TEST_USER_ID)).thenReturn(createUserById(TEST_USER_ID));
        when(productRepository.findById(TEST_PRODUCT_ID)).thenThrow(new RuntimeException("Testing !!"));

        // ACT
        ResponseEntity<ProblemDetails> response = restTemplate.withBasicAuth(USERNAME, PASSWORD)
                .exchange("/cart/1/10/", HttpMethod.GET, request, ProblemDetails.class);

        // ASSERT
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_PROBLEM_JSON, response.getHeaders().getContentType());
        assertNotNull(response.getBody());
        ProblemDetails problemDetails = response.getBody();
        assertEquals(ShoppingCartExceptionHandler.INTERNAL_SERVER_ERROR_MESSAGE, problemDetails.getReason());

        // verify mock interactions
        verify(userRepository, times(1)).findById(TEST_USER_ID);
        verify(productRepository, times(1)).findById(TEST_PRODUCT_ID);
        verify(cartRepository, never()).findByUserIdAndProductId(TEST_USER_ID, TEST_PRODUCT_ID);
    }

    /**
     * DeleteItemFromCart test cases
     */

    @Test
    void testDeleteItemFromCart_SuccessWith200() throws Exception {
        // ARRANGE
        HttpEntity<Void> request = new HttpEntity<>(null, null);
        when(userRepository.findById(TEST_USER_ID)).thenReturn(createUserById(TEST_USER_ID));
        when(productRepository.findById(TEST_PRODUCT_ID)).thenReturn(createProductById(TEST_PRODUCT_ID));
        when(cartRepository.findByUserIdAndProductId(TEST_USER_ID, TEST_PRODUCT_ID))
                .thenReturn(createCartByUserIdAndProductId(TEST_USER_ID, TEST_PRODUCT_ID));

        // ACT
        ResponseEntity<Void> response = restTemplate.withBasicAuth(USERNAME, PASSWORD)
                .exchange("/cart/1/10/", HttpMethod.DELETE, request, Void.class);

        // ASSERT
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNull(response.getHeaders().getContentType());
        assertNull(response.getBody());

        // verify mock interactions
        verify(userRepository, times(1)).findById(TEST_USER_ID);
        verify(productRepository, times(1)).findById(TEST_PRODUCT_ID);
        verify(cartRepository, times(1)).findByUserIdAndProductId(TEST_USER_ID, TEST_PRODUCT_ID);
    }

    @Test
    void testDeleteItemFromCart_FailureWith400() throws Exception {
        // ARRANGE
        HttpEntity<Void> request = new HttpEntity<>(null, null);
        when(userRepository.findById(TEST_USER_ID)).thenReturn(createUserById(TEST_USER_ID));

        // ACT
        // Product not present in DB, will lead to BAD REQUEST EXCEPTION
        ResponseEntity<ProblemDetails> response = restTemplate.withBasicAuth(USERNAME, PASSWORD)
                .exchange("/cart/1/10/", HttpMethod.DELETE, request, ProblemDetails.class);

        // ASSERT
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_PROBLEM_JSON, response.getHeaders().getContentType());
        assertNotNull(response.getBody());
        ProblemDetails problemDetails = response.getBody();
        assertEquals("Product corresponding to id 10 doesn't exist !!", problemDetails.getReason());

        // verify mock interactions
        verify(userRepository, times(1)).findById(TEST_USER_ID);
        verify(productRepository, times(1)).findById(TEST_PRODUCT_ID);
        verify(cartRepository, never()).findByUserIdAndProductId(TEST_USER_ID, TEST_PRODUCT_ID);

    }

    @Test
    void testDeleteItemFromCart_FailureWith500() throws Exception {
        // ARRANGE
        HttpEntity<Void> request = new HttpEntity<>(null, null);
        when(userRepository.findById(TEST_USER_ID)).thenReturn(createUserById(TEST_USER_ID));
        when(productRepository.findById(TEST_PRODUCT_ID)).thenThrow(new RuntimeException("Testing !!"));

        // ACT
        // Product not present in DB, will lead to BAD REQUEST EXCEPTION
        ResponseEntity<ProblemDetails> response = restTemplate.withBasicAuth(USERNAME, PASSWORD)
                .exchange("/cart/1/10/", HttpMethod.DELETE, request, ProblemDetails.class);

        // ASSERT
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_PROBLEM_JSON, response.getHeaders().getContentType());
        assertNotNull(response.getBody());
        ProblemDetails problemDetails = response.getBody();
        assertEquals(ShoppingCartExceptionHandler.INTERNAL_SERVER_ERROR_MESSAGE, problemDetails.getReason());

        // verify mock interactions
        verify(userRepository, times(1)).findById(TEST_USER_ID);
        verify(productRepository, times(1)).findById(TEST_PRODUCT_ID);
        verify(cartRepository, never()).findByUserIdAndProductId(TEST_USER_ID, TEST_PRODUCT_ID);
    }

    /**
     * AddItemToCart test cases
     */

    @Test
    void testAddItemToCart_FailureWith400() throws Exception {
        // ARRANGE
        HttpEntity<Void> request = new HttpEntity<>(null, null);
        when(userRepository.findById(TEST_USER_ID)).thenReturn(createUserById(TEST_USER_ID));
        when(productRepository.findById(TEST_PRODUCT_ID)).thenReturn(createProductById(TEST_PRODUCT_ID));
        when(cartRepository.findByUserIdAndProductId(TEST_USER_ID, TEST_PRODUCT_ID))
                .thenReturn(createCartByUserIdAndProductId(TEST_USER_ID, TEST_PRODUCT_ID));

        // ACT
        // Duplicate data being added will throw BAD REQUEST EXCEPTION
        ResponseEntity<ProblemDetails> response = restTemplate.withBasicAuth(USERNAME, PASSWORD)
                .exchange("/cart/1/10/?quantity=2", HttpMethod.POST, request, ProblemDetails.class);

        // ASSERT
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_PROBLEM_JSON, response.getHeaders().getContentType());
        assertNotNull(response.getBody());
        ProblemDetails problemDetails = response.getBody();
        assertEquals("This item is already present in users cart !!", problemDetails.getReason());

        // verify mock interactions
        verify(userRepository, times(1)).findById(TEST_USER_ID);
        verify(productRepository, times(1)).findById(TEST_PRODUCT_ID);
        verify(cartRepository, times(1)).findByUserIdAndProductId(TEST_USER_ID, TEST_PRODUCT_ID);
    }

    @Test
    void testAddItemToCart_FailureWith500() throws Exception {
        // ARRANGE
        HttpEntity<Void> request = new HttpEntity<>(null, null);
        when(userRepository.findById(TEST_USER_ID)).thenReturn(createUserById(TEST_USER_ID));
        when(productRepository.findById(TEST_PRODUCT_ID)).thenThrow(new RuntimeException("Testing !!"));

        // ACT
        ResponseEntity<ProblemDetails> response = restTemplate.withBasicAuth(USERNAME, PASSWORD)
                .exchange("/cart/1/10/?quantity=2", HttpMethod.POST, request, ProblemDetails.class);

        // ASSERT
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_PROBLEM_JSON, response.getHeaders().getContentType());
        assertNotNull(response.getBody());
        ProblemDetails problemDetails = response.getBody();
        assertEquals(ShoppingCartExceptionHandler.INTERNAL_SERVER_ERROR_MESSAGE, problemDetails.getReason());

        // verify mock interactions
        verify(userRepository, times(1)).findById(TEST_USER_ID);
        verify(productRepository, times(1)).findById(TEST_PRODUCT_ID);
        verify(cartRepository, never()).findByUserIdAndProductId(TEST_USER_ID, TEST_PRODUCT_ID);
    }

    @Test
    void testAddItemToCart_SuccessWith200() throws Exception {
        // ARRANGE
        HttpEntity<Void> request = new HttpEntity<>(null, null);
        when(userRepository.findById(TEST_USER_ID)).thenReturn(createUserById(TEST_USER_ID));
        when(productRepository.findById(TEST_PRODUCT_ID)).thenReturn(createProductById(TEST_PRODUCT_ID));

        // ACT
        ResponseEntity<Void> response = restTemplate.withBasicAuth(USERNAME, PASSWORD)
                .exchange("/cart/1/10/?quantity=2", HttpMethod.POST, request, Void.class);

        // ASSERT
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNull(response.getHeaders().getContentType());
        assertNull(response.getBody());

        // verify mock interactions
        verify(userRepository, times(1)).findById(TEST_USER_ID);
        verify(productRepository, times(1)).findById(TEST_PRODUCT_ID);
        verify(cartRepository, times(1)).findByUserIdAndProductId(TEST_USER_ID, TEST_PRODUCT_ID);
    }

    /**
     * UpdateItemInCart test cases
     */

    @Test
    void testUpdateItemInCart_addItemsWithinAllowedRange_SuccessWith200() throws Exception {
        // ARRANGE
        HttpEntity<Void> request = new HttpEntity<>(null, null);
        when(userRepository.findById(TEST_USER_ID)).thenReturn(createUserById(TEST_USER_ID));
        when(productRepository.findById(TEST_PRODUCT_ID)).thenReturn(createProductById(TEST_PRODUCT_ID));
        when(cartRepository.findByUserIdAndProductId(TEST_USER_ID, TEST_PRODUCT_ID))
                .thenReturn(createCartByUserIdAndProductId(TEST_USER_ID, TEST_PRODUCT_ID));

        // ACT
        ResponseEntity<Void> response = restTemplate.withBasicAuth(USERNAME, PASSWORD)
                .exchange("/cart/1/10/?changed=2", HttpMethod.PUT, request, Void.class);

        // ASSERT
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNull(response.getHeaders().getContentType());
        assertNull(response.getBody());

        // verify mock interactions
        verify(userRepository, times(1)).findById(TEST_USER_ID);
        verify(productRepository, times(1)).findById(TEST_PRODUCT_ID);
        verify(cartRepository, times(1)).findByUserIdAndProductId(TEST_USER_ID, TEST_PRODUCT_ID);
    }

    @Test
    void testUpdateItemInCart_removeItemsWithinAllowedRange_SuccessWith200() throws Exception {
        // ARRANGE
        HttpEntity<Void> request = new HttpEntity<>(null, null);
        when(userRepository.findById(TEST_USER_ID)).thenReturn(createUserById(TEST_USER_ID));
        when(productRepository.findById(TEST_PRODUCT_ID)).thenReturn(createProductById(TEST_PRODUCT_ID));
        when(cartRepository.findByUserIdAndProductId(TEST_USER_ID, TEST_PRODUCT_ID))
                .thenReturn(createCartByUserIdAndProductId(TEST_USER_ID, TEST_PRODUCT_ID));

        // ACT
        ResponseEntity<Void> response = restTemplate.withBasicAuth(USERNAME, PASSWORD)
                .exchange("/cart/1/10/?changed=-3", HttpMethod.PUT, request, Void.class);

        // ASSERT
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNull(response.getHeaders().getContentType());
        assertNull(response.getBody());

        // verify mock interactions
        verify(userRepository, times(1)).findById(TEST_USER_ID);
        verify(productRepository, times(1)).findById(TEST_PRODUCT_ID);
        verify(cartRepository, times(1)).findByUserIdAndProductId(TEST_USER_ID, TEST_PRODUCT_ID);
    }

    @Test
    void testUpdateItemInCart_removeItemsLeadsToDeletion_SuccessWith200() throws Exception {
        // ARRANGE
        HttpEntity<Void> request = new HttpEntity<>(null, null);
        when(userRepository.findById(TEST_USER_ID)).thenReturn(createUserById(TEST_USER_ID));
        when(productRepository.findById(TEST_PRODUCT_ID)).thenReturn(createProductById(TEST_PRODUCT_ID));
        when(cartRepository.findByUserIdAndProductId(TEST_USER_ID, TEST_PRODUCT_ID))
                .thenReturn(createCartByUserIdAndProductId(TEST_USER_ID, TEST_PRODUCT_ID));

        // ACT
        ResponseEntity<Void> response = restTemplate.withBasicAuth(USERNAME, PASSWORD)
                .exchange("/cart/1/10/?changed=-5", HttpMethod.PUT, request, Void.class);

        // ASSERT
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNull(response.getHeaders().getContentType());
        assertNull(response.getBody());

        // verify mock interactions
        verify(userRepository, times(1)).findById(TEST_USER_ID);
        verify(productRepository, times(1)).findById(TEST_PRODUCT_ID);
        verify(cartRepository, times(2)).findByUserIdAndProductId(TEST_USER_ID, TEST_PRODUCT_ID);
        verify(cartRepository, times(1)).deleteByUserIdAndProductId(TEST_USER_ID, TEST_PRODUCT_ID);
    }

    @Test
    void testUpdateItemInCart_addItemsOutsideAllowedRange_FailureWith400() throws Exception {
        // ARRANGE
        HttpEntity<Void> request = new HttpEntity<>(null, null);
        when(userRepository.findById(TEST_USER_ID)).thenReturn(createUserById(TEST_USER_ID));
        when(productRepository.findById(TEST_PRODUCT_ID)).thenReturn(createProductById(TEST_PRODUCT_ID));
        when(cartRepository.findByUserIdAndProductId(TEST_USER_ID, TEST_PRODUCT_ID))
                .thenReturn(createCartByUserIdAndProductId(TEST_USER_ID, TEST_PRODUCT_ID));

        // ACT
        ResponseEntity<ProblemDetails> response = restTemplate.withBasicAuth(USERNAME, PASSWORD)
                .exchange("/cart/1/10/?changed=6", HttpMethod.PUT, request, ProblemDetails.class);

        // ASSERT
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_PROBLEM_JSON, response.getHeaders().getContentType());
        assertNotNull(response.getBody());
        ProblemDetails problemDetails = response.getBody();
        assertEquals("Updated item quantity should be within 0 and 10", problemDetails.getReason());

        // verify mock interactions
        verify(userRepository, times(1)).findById(TEST_USER_ID);
        verify(productRepository, times(1)).findById(TEST_PRODUCT_ID);
        verify(cartRepository, times(1)).findByUserIdAndProductId(TEST_USER_ID, TEST_PRODUCT_ID);
    }

    @Test
    void testUpdateItemInCart_removeItemsUpdatedQuantityLessThanZero_FailureWith400() throws Exception {
        // ARRANGE
        HttpEntity<Void> request = new HttpEntity<>(null, null);
        when(userRepository.findById(TEST_USER_ID)).thenReturn(createUserById(TEST_USER_ID));
        when(productRepository.findById(TEST_PRODUCT_ID)).thenReturn(createProductById(TEST_PRODUCT_ID));
        when(cartRepository.findByUserIdAndProductId(TEST_USER_ID, TEST_PRODUCT_ID))
                .thenReturn(createCartByUserIdAndProductId(TEST_USER_ID, TEST_PRODUCT_ID));

        // ACT
        ResponseEntity<ProblemDetails> response = restTemplate.withBasicAuth(USERNAME, PASSWORD)
                .exchange("/cart/1/10/?changed=-6", HttpMethod.PUT, request, ProblemDetails.class);

        // ASSERT
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_PROBLEM_JSON, response.getHeaders().getContentType());
        assertNotNull(response.getBody());
        ProblemDetails problemDetails = response.getBody();
        assertEquals("Updated item quantity should be within 0 and 10", problemDetails.getReason());

        // verify mock interactions
        verify(userRepository, times(1)).findById(TEST_USER_ID);
        verify(productRepository, times(1)).findById(TEST_PRODUCT_ID);
        verify(cartRepository, times(1)).findByUserIdAndProductId(TEST_USER_ID, TEST_PRODUCT_ID);
    }

    @Test
    void testUpdateItemInCart_FailureWith500() throws Exception {
        // ARRANGE
        HttpEntity<Void> request = new HttpEntity<>(null, null);
        when(userRepository.findById(TEST_USER_ID)).thenReturn(createUserById(TEST_USER_ID));
        when(productRepository.findById(TEST_PRODUCT_ID)).thenReturn(createProductById(TEST_PRODUCT_ID));
        when(cartRepository.findByUserIdAndProductId(TEST_USER_ID, TEST_PRODUCT_ID))
                .thenThrow(new RuntimeException("Testing !!"));

        // ACT
        ResponseEntity<ProblemDetails> response = restTemplate.withBasicAuth(USERNAME, PASSWORD)
                .exchange("/cart/1/10/?changed=2", HttpMethod.PUT, request, ProblemDetails.class);

        // ASSERT
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_PROBLEM_JSON, response.getHeaders().getContentType());
        assertNotNull(response.getBody());
        ProblemDetails problemDetails = response.getBody();
        assertEquals(ShoppingCartExceptionHandler.INTERNAL_SERVER_ERROR_MESSAGE, problemDetails.getReason());

        // verify mock interactions
        verify(userRepository, times(1)).findById(TEST_USER_ID);
        verify(productRepository, times(1)).findById(TEST_PRODUCT_ID);
        verify(cartRepository, times(1)).findByUserIdAndProductId(TEST_USER_ID, TEST_PRODUCT_ID);
    }

    // helper methods follow

    private Optional<Cart> createCartByUserIdAndProductId(Integer userId, Integer productId) {
        Cart cart = new Cart();
        cart.setUserId(userId);
        cart.setProductId(productId);
        cart.setQuantity(TEST_QUANTITY);

        return Optional.of(cart);
    }


    private List<Cart> createCartList() {
        List<Cart> carts = new ArrayList<>();
        Cart cart = new Cart();
        cart.setUserId(TEST_USER_ID);
        cart.setProductId(TEST_PRODUCT_ID);
        cart.setQuantity(TEST_QUANTITY);
        carts.add(cart);

        Cart cart1 = new Cart();
        cart1.setUserId(TEST_USER_ID);
        cart1.setProductId(TEST_SECOND_PRODUCT_ID);
        cart1.setQuantity(TEST_SECOND_QUANTITY);
        carts.add(cart1);

        return carts;
    }

    private Optional<Product> createProductById(Integer productId) {
        Product product = new Product();
        product.setProductId(productId);
        product.setDetails("dummy");
        product.setCost(TEST_COST);
        return Optional.of(product);
    }

    private Optional<User> createUserById(Integer userId) {
        User user = new User();
        user.setUserId(userId);
        return Optional.of(user);
    }

}
