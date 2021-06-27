package com.maersk.shoppingcart.dto;

import java.util.List;

/**
 * @author Sumit Kumar
 */
@SuppressWarnings("unused")
public class CartDetails {

    private List<ProductDetails> productDetails;
    private double totalCost;

    public List<ProductDetails> getProductDetails() {
        return productDetails;
    }

    public void setProductDetails(List<ProductDetails> productDetails) {
        this.productDetails = productDetails;
    }

    public double getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(double totalCost) {
        this.totalCost = totalCost;
    }

    @Override
    public String toString() {
        return "CartDetails{" +
                "productDetails=" + productDetails +
                ", totalCost=" + totalCost +
                '}';
    }
}
