package com.complaintms.complaint_management_system.repository;

import com.complaintms.complaint_management_system.entity.Faq;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FaqRepository extends JpaRepository<Faq, Long> {
}