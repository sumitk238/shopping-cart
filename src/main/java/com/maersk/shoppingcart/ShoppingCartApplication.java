package com.maersk.shoppingcart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication(scanBasePackages = {ShoppingCartApplication.ALL}, exclude = {
        SecurityAutoConfiguration.class})
@EnableJpaRepositories(basePackages = {ShoppingCartApplication.ALL})
@EntityScan(basePackages = {ShoppingCartApplication.ALL})
@RestController
public class ShoppingCartApplication {

    public static final String ALL = "com.maersk.shoppingcart";

    public static void main(String[] args) {
        SpringApplication.run(ShoppingCartApplication.class, args);
    }

    @GetMapping("/")
    public String index() {
        return "Shopping Cart Application";
    }
}
