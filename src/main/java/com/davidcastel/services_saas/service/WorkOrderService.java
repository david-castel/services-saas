package com.davidcastel.services_saas.service;

import com.davidcastel.services_saas.domain.Customer;
import com.davidcastel.services_saas.domain.WorkOrder;
import com.davidcastel.services_saas.repository.CustomerRepository;
import com.davidcastel.services_saas.repository.WorkOrderRepository;
import com.davidcastel.services_saas.web.dto.CreateWorkOrderRequest;
import com.davidcastel.services_saas.web.dto.WorkOrderListItemResponse;
import com.davidcastel.services_saas.web.dto.WorkOrderResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class WorkOrderService {

    private final CustomerRepository customerRepository;
    private final WorkOrderRepository workOrderRepository;

    public WorkOrderService(CustomerRepository customerRepository, WorkOrderRepository workOrderRepository) {
        this.customerRepository = customerRepository;
        this.workOrderRepository = workOrderRepository;
    }

    @Transactional(readOnly = true)
    public List<WorkOrderListItemResponse> listItems() {
        return workOrderRepository.listItems();
    }

    @Transactional
    public WorkOrderResponse create(CreateWorkOrderRequest workOrderRequest) {
        Customer customer = customerRepository.findById(workOrderRequest.customerId())
                .orElseThrow(() -> new IllegalArgumentException("Customer not found: " + workOrderRequest.customerId()));

        WorkOrder wo = new WorkOrder(workOrderRequest.title(), workOrderRequest.description(), workOrderRequest.scheduledDate(), customer);
        WorkOrder saved = workOrderRepository.save(wo);

        return new WorkOrderResponse(
                saved.getId(),
                saved.getTitle(),
                saved.getDescription(),
                saved.getStatus(),
                saved.getScheduledDate(),
                saved.getCreatedAt(),
                customer.getId(),
                customer.getName()
        );
    }

    @Transactional
    public void start(Long id) {
        WorkOrder wo = workOrderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Work order not found"));

        wo.start();
    }

    @Transactional
    public void complete(Long id) {
        WorkOrder wo = workOrderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Work order not found"));

        wo.complete();
    }


    @Transactional
    public void cancel(Long id) {
        WorkOrder wo = workOrderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Work order not found"));

        wo.cancel();
    }

}
