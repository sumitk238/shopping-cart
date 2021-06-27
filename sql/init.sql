DROP DATABASE IF EXISTS shopping_cart_db;
create database shopping_cart_db;

drop user IF EXISTS 'springuser'@'%';
create user 'springuser'@'%' identified by 'password';
grant select, insert, delete, update on shopping_cart_db.* to 'springuser'@'%';

USE shopping_cart_db;

DROP TABLE IF EXISTS products;
CREATE TABLE products
(
    productid      BIGINT AUTO_INCREMENT PRIMARY KEY,
    name    VARCHAR(200)   NOT NULL,
    cost    DECIMAL(19, 4) NOT NULL,
    details VARCHAR(1000)
);

commit;

DROP TABLE IF EXISTS users;
CREATE TABLE users
(
    userid         BIGINT AUTO_INCREMENT PRIMARY KEY,
    email      VARCHAR(50)  NOT NULL,
    password   VARCHAR(200) NOT NULL,
    firstname VARCHAR(50),
    lastname  VARCHAR(50)
);

commit;

DROP TABLE IF EXISTS carts;
CREATE TABLE carts
(
    userid    BIGINT NOT NULL,
    productid BIGINT NOT NULL,
    quantity   INT    NOT NULL,
    PRIMARY KEY (userid, productid),
    FOREIGN KEY (productid) REFERENCES products (productid),
    FOREIGN KEY (userid) REFERENCES users (userid)
);

commit;

-- insert seed data
INSERT INTO users(email, password, firstname, lastname) values ('abc@xyz.com', 'xxxxxxxx', 'Sumit', 'Kumar');
INSERT INTO users(email, password, firstname, lastname) values ('aaa@xyz.com', 'xxxxxxxx', 'Rahul', 'Singh');
INSERT INTO users(email, password, firstname, lastname) values ('bbb@xyz.com', 'xxxxxxxx', 'Sam', 'DCosta');
INSERT INTO users(email, password, firstname, lastname) values ('ccc@xyz.com', 'xxxxxxxx', 'Saran', 'Kabir');
INSERT INTO users(email, password, firstname, lastname) values ('ddd@xyz.com', 'xxxxxxxx', 'Kuber', 'Mehrotra');
INSERT INTO users(email, password, firstname, lastname) values ('eee@xyz.com', 'xxxxxxxx', 'Gayle', 'McDowell');
INSERT INTO users(email, password, firstname, lastname) values ('fff@xyz.com', 'xxxxxxxx', 'Chris', 'Gayle');
INSERT INTO users(email, password, firstname, lastname) values ('ggg@xyz.com', 'xxxxxxxx', 'Mahi', 'Dhoni');
INSERT INTO users(email, password, firstname, lastname) values ('hhh@xyz.com', 'xxxxxxxx', 'Virat', 'Kohli');
INSERT INTO users(email, password, firstname, lastname) values ('iii@xyz.com', 'xxxxxxxx', 'Sumit', 'Kumar');

INSERT INTO products(name, cost, details) values ("Fan", 1200.5, "Ceiling Fan");
INSERT INTO products(name, cost, details) values ("Phone", 20000, "Oneplus Smartphone");
INSERT INTO products(name, cost, details) values ("TV", 30000, "Ceiling Fan");
INSERT INTO products(name, cost, details) values ("Car", 500000, "Maruti Car");
INSERT INTO products(name, cost, details) values ("Sugar", 20, null);

commit;

