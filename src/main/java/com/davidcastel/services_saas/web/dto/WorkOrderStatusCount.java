package com.davidcastel.services_saas.web.dto;

import com.davidcastel.services_saas.domain.OrderStatus;

public record WorkOrderStatusCount(OrderStatus status, long count) {}
