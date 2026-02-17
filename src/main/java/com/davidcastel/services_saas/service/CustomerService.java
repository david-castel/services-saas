package com.davidcastel.services_saas.service;

import com.davidcastel.services_saas.domain.Customer;
import com.davidcastel.services_saas.repository.CustomerRepository;
import com.davidcastel.services_saas.web.dto.CreateCustomerRequest;
import com.davidcastel.services_saas.web.dto.CustomerResponse;
import com.davidcastel.services_saas.web.dto.UpdateCustomerRequest;
import org.springframework.transaction.annotation.Transactional;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service (business logic and rules)
 */
@Service
@Transactional
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public CustomerResponse create(CreateCustomerRequest customerRequest) {
        if (customerRequest.email() != null && !customerRequest.email().isBlank()
                && customerRepository.existsByEmail(customerRequest.email())) {
            throw new IllegalArgumentException("Email already in use.");
        }

        Customer newCustomer = new Customer(customerRequest.name(), customerRequest.email(),
                customerRequest.phone(), customerRequest.address());

        Customer savedCustomer = customerRepository.save(newCustomer);

        return toResponse(savedCustomer);
    }

    @Transactional(readOnly = true)
    public CustomerResponse getById(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found: " + id));

        return toResponse(customer);
    }

    @Transactional(readOnly = true)
    public List<CustomerResponse> getAll() {
        return customerRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public CustomerResponse update(Long id, UpdateCustomerRequest customerRequest) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found: " + id));

        String email = blankToNull(customerRequest.email());
        if (email != null && !email.equals(customer.getEmail()) && customerRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already in use");
        }

        customer.update(customerRequest.name(), email, blankToNull(customerRequest.phone()), blankToNull(customerRequest.address()));

        return toResponse(customer);

    }

    public void delete(Long id) {
        if (!customerRepository.existsById(id)) {
            throw new IllegalArgumentException("Customer not found: " + id);
        }

        customerRepository.deleteById(id);
    }



    private String blankToNull(String email) {
        if (email == null) return null;
        return email.isBlank() ? null : email;
    }

    private CustomerResponse toResponse(Customer customer) {
        return new CustomerResponse(customer.getId(), customer.getName(), customer.getEmail(), customer.getPhone(), customer.getAddress());
    }


}
