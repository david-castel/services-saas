package com.davidcastel.services_saas.web.dto;

import com.davidcastel.services_saas.domain.Customer;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record CreateWorkOrderRequest(
        @NotBlank @Size(max = 150) String title,
        @Size(max = 1000) String description,
        @NotNull @FutureOrPresent LocalDate scheduledDate,
        @NotNull Long customerId
) {
}