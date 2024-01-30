package com.example.OrderService.service;

import com.example.OrderService.entity.Order;
import com.example.OrderService.exception.CustomException;
import com.example.OrderService.external.client.PaymentService;
import com.example.OrderService.external.client.ProductService;
import com.example.OrderService.external.model.request.PaymentRequest;
import com.example.OrderService.external.model.response.PaymentResponse;
import com.example.OrderService.external.model.response.ProductResponse;
import com.example.OrderService.model.OrderRequest;
import com.example.OrderService.model.OrderResponse;
import com.example.OrderService.repository.OrderRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;

@Service
@Log4j2
public class OrderServiceImpl implements OrderService{

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductService productService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public long placeOrder(OrderRequest orderRequest) {
        log.info("Placing Order Request: {}", orderRequest);

        productService.reduceQuantity(orderRequest.getProductId(), orderRequest.getQuantity());

        log.info("Creating Order with Status CREATED");
        Order order = Order.builder()
                .amount(orderRequest.getTotalAmount())
                .orderStatus("CREATED")
                .productId(orderRequest.getProductId())
                .orderDate(Instant.now())
                .quantity(orderRequest.getQuantity())
                .build();

        order = orderRepository.save(order);

        log.info("Calling Payment Service to complete the payment");
        PaymentRequest paymentRequest
                = PaymentRequest.builder()
                .orderId(order.getId())
                .paymentMethod(orderRequest.getPaymentMethod())
                .amount(orderRequest.getTotalAmount())
                .build();

        String orderStatus;
        try {
            paymentService.doPayment(paymentRequest);
            log.info("Payment done Successfully. Changing the Oder status to PLACED");
            orderStatus = "PLACED";
        } catch (Exception e) {
            log.error("Error occurred in payment. Changing order status to PAYMENT_FAILED");
            orderStatus = "PAYMENT_FAILED";
        }

        order.setOrderStatus(orderStatus);
        orderRepository.save(order);

        log.info("Order Places successfully with Order Id: {}", order.getId());
        return order.getId();
    }

    @Override
    public OrderResponse getOrderDetails(long orderId) {
        log.info("Get order details for Order Id : {}", orderId);

        Order order
                = orderRepository.findById(orderId)
                .orElseThrow(() -> new CustomException("Order not found for the order Id:" + orderId,
                        "NOT_FOUND",
                        404));

        log.info("Invoking Product service to fetch the product for id: {}", order.getProductId());
        ProductResponse productResponse
                = restTemplate.getForObject(
                "http://localhost:8080/product/" + order.getProductId(),
                ProductResponse.class
        );

        log.info("Getting payment information form the payment Service");
        PaymentResponse paymentResponse
                = restTemplate.getForObject(
                "http://localhost:8081/payment/order/" + order.getId(),
                PaymentResponse.class
        );

        OrderResponse.ProductDetails productDetails
                = OrderResponse.ProductDetails
                .builder()
                .productName(productResponse.getProductName())
                .productId(productResponse.getProductId())
                .price(productResponse.getPrice())
                .quantity(productResponse.getQuantity())
                .build();

        OrderResponse.PaymentDetails paymentDetails
                = OrderResponse.PaymentDetails
                .builder()
                .paymentId(paymentResponse.getPaymentId())
                .status(paymentResponse.getStatus())
                .paymentDate(paymentResponse.getPaymentDate())
                .paymentMethod(paymentResponse.getPaymentMethod())
                .amount(paymentResponse.getAmount())
                .orderId(paymentResponse.getOrderId())
                .build();

        OrderResponse orderResponse
                = OrderResponse.builder()
                .orderId(order.getId())
                .orderStatus(order.getOrderStatus())
                .amount(order.getAmount())
                .orderDate(order.getOrderDate())
                .productDetails(productDetails)
                .paymentDetails(paymentDetails)
                .build();

        return orderResponse;
    }
}