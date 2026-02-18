package com.davidcastel.services_saas.web.controller;

import com.davidcastel.services_saas.repository.WorkOrderRepository;
import com.davidcastel.services_saas.service.WorkOrderService;
import com.davidcastel.services_saas.web.dto.CreateWorkOrderRequest;
import com.davidcastel.services_saas.web.dto.WorkOrderListItemResponse;
import com.davidcastel.services_saas.web.dto.WorkOrderResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/work-orders")
public class WorkOrderController {

    private final WorkOrderService workOrderService;

    public WorkOrderController(WorkOrderService workOrderService) {
        this.workOrderService = workOrderService;
    }

    @GetMapping
    public List<WorkOrderListItemResponse> listItems() {
        return workOrderService.listItems();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public WorkOrderResponse createWorkOrder(CreateWorkOrderRequest workOrderRequest) {
        return workOrderService.create(workOrderRequest);
    }

}