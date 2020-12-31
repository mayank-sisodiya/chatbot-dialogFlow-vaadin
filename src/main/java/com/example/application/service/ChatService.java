package com.example.application.service;

import com.example.application.dao.DataRepository;
import com.example.application.dialog.flow.DetectIntentTexts;
import com.example.application.dto.IntentType;
import com.example.application.dto.OperationType;
import com.example.application.dto.StatusQueryType;
import com.example.application.model.Feedback.FeedbackType;
import com.example.application.model.Order;
import com.example.application.model.Order.OrderStatus;
import com.google.cloud.dialogflow.v2.QueryResult;
import com.google.protobuf.Value;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService {

    private final DataRepository dataRepository;

    public String getReply(String input) {
        try {
            QueryResult queryResult = DetectIntentTexts.detectIntentTexts(input);
            if (!ObjectUtils.isEmpty(queryResult.getFulfillmentText())) {
                return queryResult.getFulfillmentText();
            }
            return replyHelper(queryResult);
        } catch (Exception e) {
            log.error("exception e: {}", e);
            return "Error fetching reply, try rephrasing";
        }
    }

    private String replyHelper(QueryResult queryResult) {
        String intent = queryResult.getIntent().getDisplayName();
        IntentType intentType = IntentType.valueOf(intent);
        String reply, feedback;
        Value orderId, operationType, queryType;
        switch (intentType) {
            case ORDER_QUERY:
                orderId = queryResult.getParameters()
                    .getFieldsOrDefault("orderId", Value.newBuilder().setStringValue("-1").build());
                queryType = queryResult.getParameters().getFieldsOrDefault("queryType", null);
                reply = getStatus(Long.valueOf(orderId.getStringValue()),
                    Objects.isNull(queryType) ? null : queryType.getStringValue().toUpperCase());
                break;
            case ORDER_OPERATION:
                orderId = queryResult.getParameters()
                    .getFieldsOrDefault("orderId", Value.newBuilder().setStringValue("-1").build());
                operationType = queryResult.getParameters()
                    .getFieldsOrDefault("operationType", null);
                reply = performOperation(
                    OperationType.valueOf(operationType.getStringValue().toUpperCase()),
                    Long.valueOf(orderId.getStringValue()));
                break;
            case NEW_ORDER:
                reply = "Ordered placed successfully with orderId: " + dataRepository.createOrder()
                    .getOrderId();
                break;
            case RATING:
                orderId = queryResult.getParameters()
                    .getFieldsOrDefault("orderId", Value.newBuilder().setStringValue("-1").build());
                Value rating = queryType = queryResult.getParameters().getFieldsOrDefault("rating", null);
                reply = dataRepository.recordFeedBack(Long.valueOf(orderId.getStringValue()), FeedbackType.RATING, null, rating.getStringValue());
                break;
            case REVIEW:
                orderId = queryResult.getParameters()
                    .getFieldsOrDefault("orderId", Value.newBuilder().setStringValue("-1").build());
                feedback = queryResult.getQueryText();
                reply = dataRepository.recordFeedBack(Long.valueOf(orderId.getStringValue()), FeedbackType.REVIEW, feedback, null);
                break;
            case COMPLAINT:
                orderId = queryResult.getParameters()
                    .getFieldsOrDefault("orderId", Value.newBuilder().setStringValue("-1").build());
                feedback = queryResult.getQueryText();
                reply = dataRepository.recordFeedBack(Long.valueOf(orderId.getStringValue()), FeedbackType.COMPLAINT, feedback, null);
                break;
            default:
                return "Sorry, I didn't get that. Can you rephrase?";
        }
        return reply;
    }

    private String performOperation(OperationType operationType, Long orderId) {
        switch (operationType) {
            case CANCEL:
                dataRepository.updateOrder(orderId, OrderStatus.CANCELLED);
                return "Order cancelled successfully";
            case RETURN:
                dataRepository.updateOrder(orderId, OrderStatus.RETURNED);
                return "Order returned successfully";
            case EXCHANGE:
                dataRepository.updateOrder(orderId, OrderStatus.EXCHANGED);
                return "Order exchanged successfully and new order placed with id "
                    + dataRepository.createOrder().getOrderId();
            default:
                return "Sorry, I didn't get that. Can you rephrase?";

        }
    }

    private String getStatus(Long orderId, String queryType) {
        Order order = dataRepository.getOrder(orderId);
        if (Objects.isNull(order)) {
            return "invalid orderId passed";
        }

        if (ObjectUtils.isEmpty(queryType)) {
            return dataRepository.getOrderStatus(orderId);
        }

        StatusQueryType statusQueryType = StatusQueryType.valueOf(queryType);
        switch (statusQueryType) {
            case PAYMENT:
                return "Payment Status: " + order.getPaymentStatus().name();
            case SHIPPING:
                return "Shipping Status: " + order.getPaymentStatus().name();
        }

        return "Sorry, I didn't get that. Can you rephrase?";
    }

}
