package com.complaintms.complaint_management_system.service;

import com.complaintms.complaint_management_system.entity.Complaint;
import com.complaintms.complaint_management_system.entity.ComplaintTimeline;
import com.complaintms.complaint_management_system.entity.Department;
import com.complaintms.complaint_management_system.repository.ComplaintRepository;
import com.complaintms.complaint_management_system.repository.ComplaintTimelineRepository;
import com.complaintms.complaint_management_system.repository.DepartmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ComplaintService {

    @Autowired
    private ComplaintRepository complaintRepository;

    @Autowired
    private ComplaintTimelineRepository timelineRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    public List<Complaint> getAllComplaints() {
        return complaintRepository.findAll();
    }

    public Complaint getComplaintById(Long id) {
        return complaintRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Complaint not found with id: " + id));
    }

    public Complaint createComplaint(Complaint complaint) {
        if (complaint.getPriorityLabel() == null) {
            complaint.setPriorityLabel("MEDIUM");
        }
        if (complaint.getCategory() == null) {
            complaint.setCategory("OTHER");
        }

        Optional<Department> matchedDept = departmentRepository.findAll().stream()
                .filter(d -> d.getName().equalsIgnoreCase(complaint.getCategory()))
                .findFirst();

        matchedDept.ifPresent(complaint::setDepartment);

        Complaint saved = complaintRepository.save(complaint);

        String deptInfo = matchedDept.map(d -> " Routed to " + d.getName() + " department.").orElse("");
        ComplaintTimeline entry = new ComplaintTimeline(saved, "NEW", "Complaint created." + deptInfo, "SYSTEM");
        timelineRepository.save(entry);

        return saved;
    }

    public Complaint updateComplaintStatus(Long id, String status, String remarks, String updatedBy) {
        Complaint complaint = getComplaintById(id);
        complaint.setStatus(status);
        Complaint updated = complaintRepository.save(complaint);

        ComplaintTimeline entry = new ComplaintTimeline(updated, status, remarks, updatedBy);
        timelineRepository.save(entry);

        return updated;
    }

    public List<ComplaintTimeline> getTimeline(Long complaintId) {
        return timelineRepository.findByComplaintIdOrderByTimestampAsc(complaintId);
    }

    public List<Complaint> getComplaintsByStatus(String status) {
        return complaintRepository.findByStatus(status);
    }

    public List<Complaint> getComplaintsByPriority(String priorityLabel) {
        return complaintRepository.findByPriorityLabel(priorityLabel);
    }

    public void deleteComplaint(Long id) {
        complaintRepository.deleteById(id);
    }
}