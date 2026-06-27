package com.staynest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * StayNestApplication is the entry point of the entire Spring Boot app.
 *
 * WHY THIS CLASS EXISTS:
 * Every Spring Boot project needs exactly one class annotated with
 * @SpringBootApplication. This single annotation actually combines THREE
 * annotations behind the scenes:
 *
 *   1. @Configuration      -> marks this class as a source of bean definitions
 *   2. @EnableAutoConfiguration -> tells Spring Boot to auto-configure beans
 *                                   based on the jars on the classpath
 *                                   (e.g. it sees MySQL driver + JPA starter,
 *                                   so it auto-configures a DataSource for us)
 *   3. @ComponentScan      -> tells Spring to scan this package (com.staynest)
 *                              and all sub-packages for @Component, @Service,
 *                              @Repository, @Controller classes and register
 *                              them as Spring Beans automatically.
 *
 * The main() method calls SpringApplication.run(), which:
 *   - Starts an embedded Tomcat server (so you don't need to install Tomcat)
 *   - Creates the Spring ApplicationContext (the container holding all beans)
 *   - Wires every bean together (dependency injection)
 *   - Starts listening on the port from application.properties (8080)
 */
@SpringBootApplication
public class StayNestApplication {

    public static void main(String[] args) {
        SpringApplication.run(StayNestApplication.class, args);
        System.out.println("\n==============================================");
        System.out.println(" StayNest is running -> http://localhost:8080");
        System.out.println("==============================================\n");
    }
}
