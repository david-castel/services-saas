package com.davidcastel.services_saas.web.dto;

import com.davidcastel.services_saas.web.dto.WorkOrderStatusCount;

import java.util.List;

public record CustomerWorkOrderSummaryResponse(
        Long customerId,
        long total,
        List<WorkOrderStatusCount> byStatus
) {}