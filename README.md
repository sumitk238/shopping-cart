# Shopping Cart Application

### Assumptions

1. Shopping cart application will be a backend microservice that will expose endpoints to perform CRUD operations to support the use cases
   defined in Use cases section.
   
2. No UI involved. However Swagger will be integrated to provide an interface to view and test REST Apis.

### Use Cases supported and corresponding REST APIs :
	User should be able to add items in shopping cart
	POST : /cart/{userId}/{itemId}?quantity=x

	User should be able to delete items from cart:
	DELETE : /cart/{userId}/{itemId}

	User can update quantity of items:
	PUT : /cart/{userId}/{itemId}?changed=x : 
    x could be positive for increase -ve for decrease, if new-value is more than maximum 
    allowed items per user return error, also return error if new value less than zero

	User should be able to view current items :
	GET : /cart/{userId} : return a list of productId and quantity and also total cost of cart

    User should be able to see whether a particular product is present in cart, 
    if yes number of quantity of that product 
    GET : /cart/{userId}/{productId}

### Non Functional Requirements supported :
1. Swagger Integration to view/test REST APIs exposed by application.
2. Spring Actuator/Micrometer integration to see basic app metrics.
3. Junits with more than 90% line coverage.

### Steps to run MySQL DB :
1. We are running mysql inside docker container.
2. Prerequisite : Have docker runtime running on the host where this app will be run.
3. `docker-compose.yml` is present in workspace root dir which creates a mysql DB instance inside container 
   and runs `src/init.sql`. `init.sql` takes care of the following items :
   1. Creates DB for shopping cart application.
    2. Gives permission to our application user to access DB.
    3. Creates tables required in our app.
    4. Inserts seed data in tables.
4. To run mySql and perform start-up steps just execute `docker compose up`

### Steps to run app : 
1. Checkout code from github repo.
2. Run mysql DB as explained in last section. 
3. Run the following command to run the application, 
   this will also run the junits `./gradlew clean build && java -jar build/libs/shopping-cart-0.0.1-SNAPSHOT.jar`
4. All the APIs are secured using Basic Auth and Credentials are `admin/password`
5. Go to Swagger URL to test APIs: http://localhost:8080/swagger-ui.html
5. Application also exposes some metrics at a separate port 9001 for security reasons, metrics can be seen at 
   Spring actuator endpoint : http://localhost:9001/actuator/prometheus
   
### Enhancements :
1. Expose app specific metrics via prometheus, currently only generic metrics like heapUsage etc is supported.
2. Add more Swagger Annotation so that Swagger UI can guide users with sample data, all possible return HTTP codes etc.
3. Make app run inside container by adding a `DockerFile` and include this as second container 
   after mysql in `docker-compose.yml`
   
## References :
* https://spring.io/guides/gs/accessing-data-mysql/
* https://medium.com/@chrischuck35/how-to-create-a-mysql-instance-with-docker-compose-1598f3cc1bee#:~:text=In%20your%20command%20line%20or,instance%20running%20on%20localhost%3A3306%20.
* https://www.baeldung.com/spring-boot-actuators


