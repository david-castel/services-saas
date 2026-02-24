package com.davidcastel.services_saas.web.controller;

import com.davidcastel.services_saas.domain.OrderStatus;
import com.davidcastel.services_saas.service.CustomerService;
import com.davidcastel.services_saas.service.WorkOrderService;
import com.davidcastel.services_saas.web.dto.CreateCustomerRequest;
import com.davidcastel.services_saas.web.dto.CustomerResponse;
import com.davidcastel.services_saas.web.dto.UpdateCustomerRequest;
import com.davidcastel.services_saas.web.dto.WorkOrderListItemResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService customerService;
    private final WorkOrderService workOrderService;

    public CustomerController(CustomerService customerService, WorkOrderService workOrderService) {
        this.customerService = customerService;
        this.workOrderService = workOrderService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CustomerResponse create(@Valid @RequestBody CreateCustomerRequest req) {
        return customerService.create(req);
    }

    @GetMapping("/{id}")
    public CustomerResponse getById(@PathVariable Long id) {
        return customerService.getById(id);
    }

    @GetMapping
    public List<CustomerResponse> list() {
        return customerService.getAll();
    }

    @PutMapping("/{id}")
    public CustomerResponse update(@PathVariable Long id, @Valid @RequestBody UpdateCustomerRequest customerRequest) {
        return customerService.update(id, customerRequest);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        customerService.delete(id);
    }

    @GetMapping("/{id}/work-orders")
    @ResponseStatus(HttpStatus.OK)
    public List<WorkOrderListItemResponse> listWorkOrdersByCustomer(
            @PathVariable("id") Long customerId,
            @RequestParam(required = false) OrderStatus status
    ) {
        return workOrderService.listItems(status, customerId);
    }

}
