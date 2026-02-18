package com.davidcastel.services_saas.repository;

import com.davidcastel.services_saas.domain.WorkOrder;
import com.davidcastel.services_saas.web.dto.WorkOrderListItemResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface WorkOrderRepository extends JpaRepository<WorkOrder, Long> {

    @Query("""
        select new com.davidcastel.services_saas.web.dto.WorkOrderListItemResponse(
            wo.id, wo.title, wo.status, wo.scheduledDate, c.name
        )
        from WorkOrder wo
        join wo.customer c
        order by wo.createdAt desc
    """)
    public List<WorkOrderListItemResponse> listItems();

}
