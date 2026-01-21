package com.didit.server.api.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
@AllArgsConstructor
@Builder
public class ErrorResponse {
    private HttpStatus StatusCode;
    private String Message;
}
