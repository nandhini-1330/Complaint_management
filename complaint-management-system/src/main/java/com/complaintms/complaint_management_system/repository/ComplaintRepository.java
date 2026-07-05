package com.complaintms.complaint_management_system.repository;
import com.complaintms.complaint_management_system.entity.Complaint;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ComplaintRepository extends JpaRepository<Complaint, Long> {
    List<Complaint> findByStatus(String status);
    List<Complaint> findByPriorityLabel(String priorityLabel);
}
