package com.maersk.shoppingcart.dto;

/**
 * @author Sumit Kumar
 */
@SuppressWarnings("unused")
public class ProductDetails {

    private Integer productId;
    private Integer quantity;
    private double cost;

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

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    @Override
    public String toString() {
        return "ProductDetails{" +
                "productId=" + productId +
                ", quantity=" + quantity +
                ", cost=" + cost +
                '}';
    }
}
