package com.maersk.shoppingcart.jpa;

import com.maersk.shoppingcart.entity.Product;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Sumit Kumar
 */
@Repository
public interface ProductRepository extends CrudRepository<Product, Integer> {

}
