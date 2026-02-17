package com.davidcastel.services_saas.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateCustomerRequest(
    @NotBlank @Size(max = 120) String name,
    @Email @Size(max = 160) String email,
    @Size(max = 30) String phone,
    @Size(max = 255) String address
) { }