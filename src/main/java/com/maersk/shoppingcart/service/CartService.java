package com.maersk.shoppingcart.service;

import com.maersk.shoppingcart.dto.CartDetails;
import com.maersk.shoppingcart.dto.ProductDetails;
import com.maersk.shoppingcart.entity.Cart;
import com.maersk.shoppingcart.entity.Product;
import com.maersk.shoppingcart.exception.DuplicateDataException;
import com.maersk.shoppingcart.exception.InvalidDataException;
import com.maersk.shoppingcart.jpa.CartRepository;
import com.maersk.shoppingcart.jpa.ProductRepository;
import java.util.ArrayList;
import java.util.List;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * @author Sumit Kumar
 */
@Service
public class CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;


    @Value("${cart.item.max.allowed:5}")
    private int itemMaxAllowed;

    @Autowired
    public CartService(CartRepository cartRepository, ProductRepository productRepository) {
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
    }

    public void addProductToCart(Integer userId, Integer productId, int quantity) {
        cartRepository.findByUserIdAndProductId(userId, productId).ifPresent(s -> {
            throw new DuplicateDataException("This item is already present in users cart !!");
        });

        Cart cart = new Cart();
        cart.setProductId(productId);
        cart.setUserId(userId);
        cart.setQuantity(quantity);

        cartRepository.save(cart);
    }

    @Transactional
    public void deleteItemFromCart(Integer userId, Integer productId) {
        cartRepository.findByUserIdAndProductId(userId, productId).orElseThrow(() ->
                new InvalidDataException("Item with id: " + productId + " is not present in user's cart !"));

        cartRepository.deleteByUserIdAndProductId(userId, productId);
    }

    @Transactional
    public void updateCart(Integer userId, Integer productId, Integer changed) {
        Cart cart = cartRepository.findByUserIdAndProductId(userId, productId).orElseThrow(() ->
                new InvalidDataException("Item with id: " + productId + " is not present in user's cart !"));

        // make sure quantity remains within the allowed range ie. 0 to maxAllowed after change
        int updatedQuantity = cart.getQuantity() + changed;
        if (updatedQuantity < 0 || updatedQuantity > itemMaxAllowed) {
            throw new InvalidDataException("Updated item quantity should be within 0 and " + itemMaxAllowed);
        }

        // delete cart row for current item if updated quantity is zero
        if (updatedQuantity == 0) {
            deleteItemFromCart(userId, productId);
        } else {
            cart.setQuantity(cart.getQuantity() + changed);
            cartRepository.save(cart);
        }
    }

    public int getCountOfItem(Integer userId, Integer productId) {
        Cart cart = cartRepository.findByUserIdAndProductId(userId, productId).orElse(null);
        if (cart == null) {
            return 0;
        }
        return cart.getQuantity();
    }

    public CartDetails getCartDetails(Integer userId) {
        List<Cart> carts = cartRepository.findAllByUserId(userId);

        CartDetails cartDetails = new CartDetails();
        if (carts == null) {
            return cartDetails;
        }

        double totalCost = 0.0;

        List<ProductDetails> productDetailsList = new ArrayList<>();
        for (Cart cart : carts) {
            ProductDetails productDetails = new ProductDetails();
            productDetails.setProductId(cart.getProductId());
            productDetails.setQuantity(cart.getQuantity());
            // Exception should never happen as we maintain data integrity at DB level via constraints
            // However we need to add this exception as Spring JPA returns Optional
            Product product = productRepository.findById(cart.getProductId()).orElseThrow(
                    () -> new InvalidDataException("Found product in cart which does not exist in system !!"));
            productDetails.setCost(product.getCost());
            productDetailsList.add(productDetails);
            totalCost += cart.getQuantity() * product.getCost();
        }

        cartDetails.setProductDetails(productDetailsList);
        cartDetails.setTotalCost(totalCost);
        return cartDetails;
    }
}
