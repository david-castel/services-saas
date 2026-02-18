package com.davidcastel.services_saas.domain;

import com.davidcastel.services_saas.domain.exception.InvalidWorkOrderStateException;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "work_orders")
public class WorkOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(length = 1000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Column(nullable = false)
    private LocalDate scheduledDate;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    protected WorkOrder() {
    }

    public WorkOrder(String title,
                     String description,
                     LocalDate scheduledDate,
                     Customer customer) {

        this.title = title;
        this.description = description;
        this.scheduledDate = scheduledDate;
        this.customer = customer;
        this.status = OrderStatus.PENDING;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public LocalDate getScheduledDate() {
        return scheduledDate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public Customer getCustomer() {
        return customer;
    }

//    public void changeStatus(OrderStatus newStatus) {
//        this.status = newStatus;
//    }

    public void start() {
        if (this.status != OrderStatus.PENDING) {
            throw new IllegalStateException("Only PENDING orders can be started");
        }

        this.status = OrderStatus.IN_PROGRESS;
    }

    public void complete() {
        if (this.status != OrderStatus.IN_PROGRESS) {
            throw new IllegalStateException("Only IN_PROGRESS orders can be completed");
        }

        this.status = OrderStatus.COMPLETED;
    }

    public void cancel() {
        if (this.status != OrderStatus.PENDING) {
            throw new InvalidWorkOrderStateException(
                    "Only PENDING orders can be cancelled"
            );
        }

        this.status = OrderStatus.CANCELLED;
    }


}
