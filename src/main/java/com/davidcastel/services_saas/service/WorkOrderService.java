package com.davidcastel.services_saas.service;

import com.davidcastel.services_saas.domain.Customer;
import com.davidcastel.services_saas.domain.OrderStatus;
import com.davidcastel.services_saas.domain.WorkOrder;
import com.davidcastel.services_saas.domain.exception.ResourceNotFoundException;
import com.davidcastel.services_saas.repository.CustomerRepository;
import com.davidcastel.services_saas.repository.WorkOrderRepository;
import com.davidcastel.services_saas.web.dto.CreateWorkOrderRequest;
import com.davidcastel.services_saas.web.dto.WorkOrderListItemResponse;
import com.davidcastel.services_saas.web.dto.WorkOrderResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    public Page<WorkOrderListItemResponse> listItems(
            OrderStatus status,
            Long customerId,
            Pageable pageable) {

        Page<WorkOrder> page;

        if (customerId != null && !customerRepository.existsById(customerId)) {
            throw new ResourceNotFoundException("Customer not found: " + customerId);
        }

        if (status != null && customerId != null) {
            page = workOrderRepository.findByStatusAndCustomerId(status, customerId, pageable);
        } else if (status != null) {
            page = workOrderRepository.findByStatus(status, pageable);
        } else if (customerId != null) {
            page = workOrderRepository.findByCustomerId(customerId, pageable);
        } else {
            page = workOrderRepository.findAll(pageable);
        }

        return page.map(this::toListItem);
    }

    @Transactional
    public WorkOrderResponse create(CreateWorkOrderRequest workOrderRequest) {
        Customer customer = customerRepository.findById(workOrderRequest.customerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found: " + workOrderRequest.customerId()));

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
                .orElseThrow(() -> new ResourceNotFoundException("Work order not found"));

        wo.start();
    }

    @Transactional
    public void complete(Long id) {
        WorkOrder wo = workOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Work order not found"));

        wo.complete();
    }


    @Transactional
    public void cancel(Long id) {
        WorkOrder wo = workOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Work order not found"));

        wo.cancel();
    }

    @Transactional(readOnly = true)
    public boolean existsByCustomerId(Long customerId) {
        return workOrderRepository.existsByCustomerId(customerId);
    }

    private WorkOrderListItemResponse toListItem(WorkOrder wo) {
        return new WorkOrderListItemResponse(
                wo.getId(),
                wo.getTitle(),
                wo.getStatus(),
                wo.getScheduledDate(),
                wo.getCustomer().getName()
        );
    }



}
