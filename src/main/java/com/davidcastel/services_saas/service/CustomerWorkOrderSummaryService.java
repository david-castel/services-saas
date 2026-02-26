package com.davidcastel.services_saas.service;

import com.davidcastel.services_saas.domain.OrderStatus;
import com.davidcastel.services_saas.domain.exception.ResourceNotFoundException;
import com.davidcastel.services_saas.repository.CustomerRepository;
import com.davidcastel.services_saas.repository.WorkOrderRepository;
import com.davidcastel.services_saas.web.dto.CustomerWorkOrderSummaryResponse;
import com.davidcastel.services_saas.web.dto.WorkOrderStatusCount;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CustomerWorkOrderSummaryService {

    private final CustomerRepository customerRepository;
    private final WorkOrderRepository workOrderRepository;

    public CustomerWorkOrderSummaryResponse getSummary(Long customerId) {
        // 404 si no existe (sin traer el grafo completo)
        if (!customerRepository.existsById(customerId)) {
            throw new ResourceNotFoundException("Customer not found: " + customerId);
        }

        var rows = workOrderRepository.countByStatusForCustomer(customerId);

        // Convertimos projection -> DTO
        var byStatus = rows.stream()
                .map(r -> new WorkOrderStatusCount(r.getStatus(), r.getCnt()))
                .toList();

        // Obtenemos la suma de todos los WorkOrder existentes:
        long total = byStatus.stream().mapToLong(WorkOrderStatusCount::count).sum();

        // (Opcional senior) garantizar que salgan todos los estados, incluso con 0:
         byStatus = fillMissingStatuses(byStatus);

        return new CustomerWorkOrderSummaryResponse(customerId, total, byStatus);
    }

    private List<WorkOrderStatusCount> fillMissingStatuses(List<WorkOrderStatusCount> current) {
        var map = current.stream()
                .collect(Collectors.toMap(WorkOrderStatusCount::status, WorkOrderStatusCount::count));

        return Arrays.stream(OrderStatus.values())
                .map(s -> new WorkOrderStatusCount(s, map.getOrDefault(s, 0L)))
                .toList();
    }

}