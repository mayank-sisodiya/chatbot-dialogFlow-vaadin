package com.example.application.dao;

import com.example.application.dto.UpdateResponse;
import com.example.application.model.Feedback;
import com.example.application.model.Feedback.FeedbackType;
import com.example.application.model.Order;
import com.example.application.model.Order.OrderStatus;
import com.example.application.model.Order.PaymentStatus;
import com.example.application.model.Order.ShipmentStatus;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class DataRepository {

    private AtomicLong totalOrders = new AtomicLong(0);

    private Map<Long, Order> orders = new HashMap<>();

    private final String overallOrderStatus = "Payment Status: %s, Shipment Status: %s and Order Status: %s";

    @PostConstruct
    private void initialize(){
        Order order = new Order();
        order.setOrderId(totalOrders.addAndGet(1));
        order.setPaymentStatus(PaymentStatus.PENDING);
        order.setShipmentStatus(ShipmentStatus.PENDING);
        order.setOrderStatus(OrderStatus.NEW);
        this.orders.put(order.getOrderId(), order);
    }

    public Order createOrder(){
        Order order = new Order();
        order.setOrderId(totalOrders.addAndGet(1));
        order.setPaymentStatus(PaymentStatus.PENDING);
        order.setShipmentStatus(ShipmentStatus.PENDING);
        order.setOrderStatus(OrderStatus.NEW);
        this.orders.put(order.getOrderId(), order);
        return order;
    }

    public String getOrderStatus(Long orderId){
        Order order = this.orders.getOrDefault(orderId, null);
        if(Objects.isNull(order)){
            return "No order with Id found";
        }
        return String.format(overallOrderStatus, order.getPaymentStatus(), order.getShipmentStatus(), order.getOrderStatus());
    }

    public UpdateResponse updateOrder(Long orderId, OrderStatus status){
        Order order = this.orders.getOrDefault(orderId, null);
        if(Objects.isNull(order)){
            return UpdateResponse.builder().success(false).message("No order with Id found").build();
        }
        return UpdateResponse.builder().success(true).build();
    }

    public Order getOrder(Long orderId){
        return this.orders.getOrDefault(orderId, null);
    }

    public String recordFeedBack(Long orderId, FeedbackType type, String feedback, String rating){
        Order order = this.orders.getOrDefault(orderId, null);
        if(Objects.isNull(order)){
            return "No order with Id found";
        }
        StringBuilder stringBuilder = new StringBuilder("");
        order.getFeedbacks().forEach(pastFeedback -> stringBuilder.append(pastFeedback.toString() + ", "));
        if(stringBuilder.length()>=2) {
            stringBuilder.delete(stringBuilder.length() - 2, stringBuilder.length() - 1);
        }
        Feedback feedbackObj = new Feedback(feedback, type, rating);
        order.getFeedbacks().add(feedbackObj);

        return "Thank You, Previous feedback: "+stringBuilder.toString();
    }

}
