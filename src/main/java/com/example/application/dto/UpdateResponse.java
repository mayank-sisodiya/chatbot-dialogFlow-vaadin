package com.example.application.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
public class UpdateResponse {

    private boolean success;

    private String message;

}
