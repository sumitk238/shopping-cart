package com.maersk.shoppingcart.rest;

import com.maersk.shoppingcart.dto.CartDetails;
import com.maersk.shoppingcart.exception.InvalidDataException;
import com.maersk.shoppingcart.service.CartService;
import com.maersk.shoppingcart.service.ProductService;
import com.maersk.shoppingcart.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Sumit Kumar
 */
@RequestMapping("/cart")
@RestController
public class ShoppingCartEndpoint {

    private static final Logger logger = LoggerFactory.getLogger(ShoppingCartEndpoint.class);

    private final CartService cartService;
    private final UserService userService;
    private final ProductService productService;

    @Value("${cart.item.max.allowed:5}")
    private int itemMaxAllowed;

    @Autowired
    public ShoppingCartEndpoint(CartService cartService, UserService userService,
            ProductService productService) {
        this.cartService = cartService;
        this.userService = userService;
        this.productService = productService;
    }

    /**
     *  @returns
     *      all items in users cart with cumulative cost
     */
    @GetMapping("/{userId}")
    public CartDetails getCartDetailsForUser(@PathVariable Integer userId) {
        logger.debug("Fetching full cart for user: {}", userId);
        validateUser(userId);
        return cartService.getCartDetails(userId);
    }

    /**
     * @returns
     *      Quantity of products of given Id in users cart, zero if not present
     */
    @GetMapping("/{userId}/{productId}/")
    public Integer getCountOfItemInCart(@PathVariable Integer userId, @PathVariable Integer productId) {
        logger.debug("Fetching count of product: {} in cart of user : {}", productId, userId);
        validateUserAndProduct(userId, productId);
        return cartService.getCountOfItem(userId, productId);
    }

    /**
     * clears the given product from users cart
     * throws exception if not present
     */
    @DeleteMapping("/{userId}/{productId}/")
    public void deleteItemFromCart(@PathVariable Integer userId, @PathVariable Integer productId) {
        logger.debug("Delete product: {} from user: {} cart", productId, userId);
        validateUserAndProduct(userId, productId);

        cartService.deleteItemFromCart(userId, productId);
    }

    /**
     * updates quantity of items in cart, ensures number of items remains valid post update
     */
    @PutMapping("/{userId}/{productId}/")
    public void updateItemInCart(@PathVariable Integer userId, @PathVariable Integer productId,
            @RequestParam Integer changed) {
        logger.debug("Updating product : {} count by {} in user : {} cart", productId, changed, userId);
        validateUserAndProduct(userId, productId);
        if(changed == 0) {
            throw new InvalidDataException("Changed should not be zero !!");
        }
        cartService.updateCart(userId, productId, changed);

    }

    /**
     * adds new item to cart
     */
    @PostMapping("/{userId}/{productId}/")
    public void addItemToCart(@PathVariable Integer userId, @PathVariable Integer productId,
            @RequestParam Integer quantity) {

        logger.debug("Adding product : {} with quantity : {} to user : {} cart", productId, quantity, userId);
        // perform validations : ensure user and product exists and valid quantity value
        if (quantity <= 0 && quantity > itemMaxAllowed) {
            throw new InvalidDataException("Quantity should be greater than zero and less than " + itemMaxAllowed + " !!");
        }
        validateUserAndProduct(userId, productId);

        cartService.addProductToCart(userId, productId, quantity);
    }

    private void validateUser(Integer userId) {
        if (userService.getUserById(userId) == null) {
            throw new InvalidDataException("User corresponding to id " + userId + " doesn't exist !!");
        }
    }

    private void validateUserAndProduct(Integer userId, Integer productId) {

        validateUser(userId);

        if (productService.getProductById(productId) == null) {
            throw new InvalidDataException("Product corresponding to id " + productId + " doesn't exist !!");
        }
    }
}
