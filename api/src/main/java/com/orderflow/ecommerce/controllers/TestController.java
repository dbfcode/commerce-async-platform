package com.orderflow.ecommerce.controllers;

import com.orderflow.ecommerce.controllers.docs.TestControllerDocs;
import com.orderflow.ecommerce.dtos.OrderItemDto;
import com.orderflow.ecommerce.dtos.PingResponse;
import com.orderflow.ecommerce.dtos.PublishSampleOrderResponse;
import com.orderflow.ecommerce.entities.enums.OrderStatus;
import com.orderflow.ecommerce.messaging.events.OrderCreatedEvent;
import com.orderflow.ecommerce.messaging.publisher.OrderEventPublisher;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/test")
public class TestController implements TestControllerDocs {

    private static final long DEFAULT_START_ORDER_ID = 1000L;
    private final OrderEventPublisher orderEventPublisher;
    private final AtomicLong sampleOrderSequence = new AtomicLong(resolveInitialOrderId());

    public TestController(OrderEventPublisher orderEventPublisher) {
        this.orderEventPublisher = orderEventPublisher;
    }

    @Override
    @GetMapping("/ping")
    public PingResponse ping() {
        return new PingResponse(
                "ok",
                "versão 1.",
                LocalDateTime.now().toString()
        );
    }

    @Override
    @GetMapping("/publish-sample-order")
    public PublishSampleOrderResponse publishSampleOrder() {
        long nextOrderId = nextSampleOrderId();
        var event = new OrderCreatedEvent(
                nextOrderId,
                LocalDateTime.now(),
                OrderStatus.PENDING,
                new BigDecimal("42.00"),
                List.of(new OrderItemDto(1L, "Produto teste", 1, new BigDecimal("42.00"), new BigDecimal("42.00")))
        );
        orderEventPublisher.publishOrderCreated(event);
        return new PublishSampleOrderResponse(
                true,
                event.orderId(),
                "Vê os logs: EmailConsumer e InventoryConsumer devem registar uma linha cada."
        );
    }

    private long nextSampleOrderId() {
        long current = sampleOrderSequence.getAndUpdate(previous ->
                previous >= Long.MAX_VALUE ? resolveFallbackOrderId() : previous + 1
        );
        if (current <= 0) {
            return resolveFallbackOrderId();
        }
        return current;
    }

    /**
     * Reads an optional initial value and falls back to DEFAULT_START_ORDER_ID when missing or invalid.
     */
    private long resolveInitialOrderId() {
        String configured = System.getProperty("orderflow.sample-order.start-id");
        if (configured == null || configured.isBlank()) {
            return DEFAULT_START_ORDER_ID;
        }
        try {
            long parsed = Long.parseLong(configured.trim());
            return parsed > 0 ? parsed : DEFAULT_START_ORDER_ID;
        } catch (NumberFormatException ignored) {
            return DEFAULT_START_ORDER_ID;
        }
    }

    private long resolveFallbackOrderId() {
        long fallback = Math.abs(System.currentTimeMillis());
        return fallback == 0 ? DEFAULT_START_ORDER_ID : fallback;
    }
}
