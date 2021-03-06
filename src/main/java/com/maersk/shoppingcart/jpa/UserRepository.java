package com.maersk.shoppingcart.jpa;

import com.maersk.shoppingcart.entity.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Sumit Kumar
 */
@Repository
public interface UserRepository extends CrudRepository<User, Integer> {

}
