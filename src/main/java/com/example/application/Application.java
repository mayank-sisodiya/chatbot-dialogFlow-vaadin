package com.example.application;

import com.example.application.model.Order;
import com.example.application.model.Order.PaymentStatus;
import com.example.application.model.Order.ShipmentStatus;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.vaadin.artur.helpers.LaunchUtil;

/**
 * The entry point of the Spring Boot application.
 */
@SpringBootApplication
public class Application extends SpringBootServletInitializer {

    public static void main(String[] args) {
        LaunchUtil.launchBrowserInDevelopmentMode(SpringApplication.run(Application.class, args));
    }

    @Bean
    public ScheduledExecutorService executorService() {
        return Executors.newScheduledThreadPool(10);
    }

    @Bean
    public Order newOrder(){
        Order order = new Order();
        order.setPaymentStatus(PaymentStatus.PENDING);
        order.setShipmentStatus(ShipmentStatus.PENDING);
        return order;
    }

}
