package com.example.application.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Feedback {

    private String feedback;

    private FeedbackType feedbackType;

    private String rating;

    public enum FeedbackType{
        COMPLAINT, REVIEW, RATING
    }

}
