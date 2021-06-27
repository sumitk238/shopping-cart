package com.maersk.shoppingcart.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;

/**
 * @author Sumit Kumar
 */
@IdClass(CartPrimaryKey.class)
@Entity(name = "carts")
@SuppressWarnings("unused")
public class Cart {

    @Id
    private Integer userId;

    @Id
    private Integer productId;

    private Integer quantity;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
