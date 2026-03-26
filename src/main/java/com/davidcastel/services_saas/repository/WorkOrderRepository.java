package com.davidcastel.services_saas.repository;

import com.davidcastel.services_saas.domain.OrderStatus;
import com.davidcastel.services_saas.domain.WorkOrder;
import com.davidcastel.services_saas.web.dto.WorkOrderListItemResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface WorkOrderRepository extends JpaRepository<WorkOrder, Long> {

//    @Query("""
//        select new com.davidcastel.services_saas.web.dto.WorkOrderListItemResponse(
//            wo.id, wo.title, wo.status, wo.scheduledDate, c.name
//        )
//        from WorkOrder wo
//        join wo.customer c
//        order by wo.createdAt desc
//    """)
//    public List<WorkOrderListItemResponse> listItems();

    @Override
    @EntityGraph(attributePaths = "customer")
    Page<WorkOrder> findAll(Pageable pageable);

    @EntityGraph(attributePaths = "customer")   // Para traer el customer en la query.
    Page<WorkOrder> findByStatus(OrderStatus status, Pageable pageable);

    @EntityGraph(attributePaths = "customer")
    Page<WorkOrder> findByCustomerId(Long customerId, Pageable pageable);

    @EntityGraph(attributePaths = "customer")
    Page<WorkOrder> findByStatusAndCustomerId(OrderStatus status, Long customerId, Pageable pageable);

    @Query("""
        select wo.status as status, count(wo.id) as cnt
        from WorkOrder wo
        where wo.customer.id = :customerId
        group by wo.status
    """)
    List<WorkOrderStatusCountProjection> countByStatusForCustomer(Long customerId);

    interface WorkOrderStatusCountProjection {
        OrderStatus getStatus();
        long getCnt();
    }

    boolean existsByCustomerId(Long customerId);

}
