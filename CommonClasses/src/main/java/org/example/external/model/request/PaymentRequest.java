package org.example.external.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.external.model.PaymentMethod;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentRequest {
    private long orderId;
    private long amount;
    private String referenceNumber;
    private PaymentMethod paymentMethod;
}