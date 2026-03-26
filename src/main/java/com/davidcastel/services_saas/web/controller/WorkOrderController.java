package com.davidcastel.services_saas.web.controller;

import com.davidcastel.services_saas.domain.OrderStatus;
import com.davidcastel.services_saas.repository.WorkOrderRepository;
import com.davidcastel.services_saas.service.WorkOrderService;
import com.davidcastel.services_saas.web.dto.CreateWorkOrderRequest;
import com.davidcastel.services_saas.web.dto.WorkOrderListItemResponse;
import com.davidcastel.services_saas.web.dto.WorkOrderResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public Page<WorkOrderListItemResponse> listItems(
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(required = false) Long customerId,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return workOrderService.listItems(status, customerId, pageable);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN')")
    public WorkOrderResponse create(@Valid @RequestBody CreateWorkOrderRequest workOrderRequest) {
        return workOrderService.create(workOrderRequest);
    }

    @PatchMapping("/{id}/start")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void start(@PathVariable Long id) {
        workOrderService.start(id);
    }

    @PatchMapping("/{id}/complete")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void complete(@PathVariable Long id) {
        workOrderService.complete(id);
    }

    @PatchMapping("/{id}/cancel")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void cancel(@PathVariable Long id) {
        workOrderService.cancel(id);
    }


}