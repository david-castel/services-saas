package com.davidcastel.services_saas.config;

import com.davidcastel.services_saas.domain.Customer;
import com.davidcastel.services_saas.domain.WorkOrder;
import com.davidcastel.services_saas.repository.CustomerRepository;
import com.davidcastel.services_saas.repository.WorkOrderRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Random;

@Component
@Profile("dev")
public class DataSeeder implements CommandLineRunner {

    private final CustomerRepository customerRepository;
    private final WorkOrderRepository workOrderRepository;

    public DataSeeder(CustomerRepository customerRepository, WorkOrderRepository workOrderRepository) {
        this.customerRepository = customerRepository;
        this.workOrderRepository = workOrderRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        // Evita duplicar en cada arranque
        if (customerRepository.count() > 0) return;

        Customer c1 = new Customer("Customer One", "c1@test.com", "600000001", "Address 1");
        Customer c2 = new Customer("Customer Two", "c2@test.com", "600000002", "Address 2");

        c1 = customerRepository.save(c1);
        c2 = customerRepository.save(c2);

        seedWorkOrdersFor(c1, 15);
        seedWorkOrdersFor(c2, 15);
    }

    private void seedWorkOrdersFor(Customer customer, int count) {
        Random random = new Random();

        for (int i = 1; i <= count; i++) {
            LocalDate date = LocalDate.now().plusDays(random.nextInt(15)); // hoy..+14

            WorkOrder wo = new WorkOrder(
                    "WO " + customer.getName() + " #" + i,
                    "Description for " + customer.getName() + " work order " + i,
                    date,
                    customer
            );

            // Opcional: repartir estados (si tu constructor siempre pone PENDING, déjalo)
            // Si tienes setter o método interno NO rompas invariantes.
            // Mejor: deja PENDING para no saltarte reglas de transición.

            workOrderRepository.save(wo);
        }
    }
}