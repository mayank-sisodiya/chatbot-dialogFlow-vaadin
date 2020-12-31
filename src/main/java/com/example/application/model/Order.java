package com.example.application.model;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    private Long orderId;

    private PaymentStatus paymentStatus;

    private ShipmentStatus shipmentStatus;

    private OrderStatus orderStatus;

    private List<Feedback> feedbacks = new ArrayList<>();

    public enum PaymentStatus{
        PENDING, SUCCESS, FAILURE
    }

    public enum ShipmentStatus{
        DELIVERED, DISPATCHED, PENDING
    }

    public enum OrderStatus{
        NEW, CANCELLED, EXCHANGED, RETURNED
    }

}
