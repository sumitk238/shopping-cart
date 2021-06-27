package com.maersk.shoppingcart.jpa;

import com.maersk.shoppingcart.entity.Cart;
import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartRepository extends CrudRepository<Cart, Integer> {

    Optional<Cart> findByUserIdAndProductId(Integer userId, Integer productId);
    void deleteByUserIdAndProductId(Integer userId, Integer productId);
    List<Cart> findAllByUserId(Integer userId);
}
