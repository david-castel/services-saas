package com.davidcastel.services_saas.customer;

import com.davidcastel.services_saas.domain.Customer;
import com.davidcastel.services_saas.domain.WorkOrder;
import com.davidcastel.services_saas.repository.CustomerRepository;
import com.davidcastel.services_saas.repository.WorkOrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import static org.hamcrest.Matchers.contains;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CustomerWorkOrderSummaryIT {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    WorkOrderRepository workOrderRepository;

    @BeforeEach
    void cleanDb() {
        workOrderRepository.deleteAll();
        customerRepository.deleteAll();
    }

    @Test
    void summary_returns_counts_grouped_by_status() throws Exception {
        var customer = customerRepository.save(
                new Customer("John Doe", "john@test.com", "123", "Street 1")
        );

        workOrderRepository.save(new WorkOrder("WO1", "d1", LocalDate.now().plusDays(1), customer));
        workOrderRepository.save(new WorkOrder("WO2", "d2", LocalDate.now().plusDays(1), customer));
        workOrderRepository.save(new WorkOrder("WO3", "d3", LocalDate.now().plusDays(1), customer));

        // Act + Assert
        mockMvc.perform(get("/api/customers/{id}/work-orders/summary", customer.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerId").value(customer.getId()))
                .andExpect(jsonPath("$.total").value(3))
                .andExpect(jsonPath("$.byStatus[?(@.status=='PENDING')].count").value(contains(1)))
                .andExpect(jsonPath("$.byStatus[?(@.status=='IN_PROGRESS')].count").value(contains(1)))
                .andExpect(jsonPath("$.byStatus[?(@.status=='COMPLETED')].count").value(contains(1)));
    }

    @Test
    void summary_returns_404_when_customer_not_found() throws Exception {
        mockMvc.perform(get("/api/customers/{id}/work-orders/summary", 999999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }
}