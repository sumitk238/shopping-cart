package com.maersk.shoppingcart;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * @author Sumit Kumar
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, properties = {"spring.profiles.active:test"})
class ShoppingCartApplicationTest {

    private static final String USERNAME = "admin";
    private static final String PASSWORD = "password";
    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void testBaseUrl() {
        // ARRANGE
        HttpEntity<Void> request = new HttpEntity<>(null, null);

        // ACT
        ResponseEntity<String> response = restTemplate.withBasicAuth(USERNAME, PASSWORD)
                .exchange("/", HttpMethod.GET, request, String.class);

        // ASSERT
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertEquals("Shopping Cart Application", response.getBody());
    }

}
