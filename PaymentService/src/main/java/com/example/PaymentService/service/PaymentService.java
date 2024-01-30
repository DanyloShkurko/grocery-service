package com.example.PaymentService.service;

import com.example.PaymentService.model.request.PaymentRequest;
import com.example.PaymentService.model.response.PaymentResponse;

public interface PaymentService {
    Long doPayment(PaymentRequest paymentRequest);

    PaymentResponse getPaymentDetailsByOrderId(String id);
}
