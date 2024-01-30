package com.example.PaymentService.model.response;

import com.example.PaymentService.model.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentResponse {
    private long paymentId;
    private String status;
    private PaymentMethod paymentMethod;
    private long amount;
    private Instant paymentDate;
    private long orderId;
}
