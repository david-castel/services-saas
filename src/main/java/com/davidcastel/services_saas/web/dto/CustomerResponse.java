package com.davidcastel.services_saas.web.dto;

public record CustomerResponse(
        Long id,
        String name,
        String email,
        String phone,
        String address
) { }