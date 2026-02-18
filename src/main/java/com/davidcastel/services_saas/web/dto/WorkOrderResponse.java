package com.davidcastel.services_saas.web.dto;

import com.davidcastel.services_saas.domain.OrderStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record WorkOrderResponse(
        Long id,
        String title,
        String description,
        OrderStatus status,
        LocalDate scheduledDate,
        LocalDateTime createdAt,
        Long customerId,
        String customerName
) {}