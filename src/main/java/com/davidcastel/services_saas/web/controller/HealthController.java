package com.davidcastel.services_saas.web.controller;

import com.davidcastel.services_saas.web.dto.HealthResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class HealthController {

    @GetMapping("/health")
    public HealthResponse health() {
        return new HealthResponse("OK");
    }

//    POST /api/customers
//    @PostMapping("/customers")
//    public

//    GET /api/customers/{id}
//    @GetMapping
//    public
//
//    GET /api/customers
//
//    PUT /api/customers/{id}
//
//    DELETE /api/customers/{id}

}