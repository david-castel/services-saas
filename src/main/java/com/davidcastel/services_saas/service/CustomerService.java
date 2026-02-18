package com.davidcastel.services_saas.service;

import com.davidcastel.services_saas.domain.Customer;
import com.davidcastel.services_saas.domain.exception.DuplicateEmailException;
import com.davidcastel.services_saas.domain.exception.ResourceNotFoundException;
import com.davidcastel.services_saas.repository.CustomerRepository;
import com.davidcastel.services_saas.web.dto.CreateCustomerRequest;
import com.davidcastel.services_saas.web.dto.CustomerResponse;
import com.davidcastel.services_saas.web.dto.UpdateCustomerRequest;
import org.springframework.dao.DataIntegrityViolationException;
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
        String email = blankToNull(customerRequest.email());

        if (email != null && customerRepository.existsByEmail(email)) {
            throw new DuplicateEmailException("Email already in use.");
        }

//        try {
            Customer customer = new Customer(customerRequest.name(), blankToNull(customerRequest.email()), blankToNull(customerRequest.phone()), blankToNull(customerRequest.address()));
            Customer saved = customerRepository.save(customer);
            return toResponse(saved);
//        } catch (org.springframework.dao.DataIntegrityViolationException ex) {
//            // respaldo ante concurrencia (race condition) o constraint DB
//            throw new DuplicateEmailException("Email already in use");
//        }
    }

    @Transactional(readOnly = true)
    public CustomerResponse getById(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found: " + id));

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
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found: " + id));

        String email = blankToNull(customerRequest.email());
        if (email != null && !email.equals(customer.getEmail()) && customerRepository.existsByEmail(email)) {
            throw new DuplicateEmailException("Email already in use");
        }

//        try {
            customer.update(customerRequest.name(), email, blankToNull(customerRequest.phone()), blankToNull(customerRequest.address()));

            // No hace falta save()
            // JPA hará dirty checking al cerrar la transacción

            return toResponse(customer);

//        } catch (DataIntegrityViolationException ex) {
//            // respaldo ante race condition
//            throw new DuplicateEmailException("Email already in use");
//        }
    }

    public void delete(Long id) {
        if (!customerRepository.existsById(id)) {
            throw new ResourceNotFoundException("Customer not found: " + id);
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
