package com.complaintms.complaint_management_system.repository;

import com.complaintms.complaint_management_system.entity.ComplaintTimeline;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ComplaintTimelineRepository extends JpaRepository<ComplaintTimeline, Long> {
    List<ComplaintTimeline> findByComplaintIdOrderByTimestampAsc(Long complaintId);
}