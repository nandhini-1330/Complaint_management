package com.complaintms.complaint_management_system.controller;

import com.complaintms.complaint_management_system.entity.Complaint;
import com.complaintms.complaint_management_system.entity.ComplaintTimeline;
import com.complaintms.complaint_management_system.service.ComplaintService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/complaints")
@CrossOrigin(origins = "*")
public class ComplaintController {

    @Autowired
    private ComplaintService complaintService;

    @GetMapping
    public List<Complaint> getAllComplaints() {
        return complaintService.getAllComplaints();
    }

    @GetMapping("/{id}")
    public Complaint getComplaintById(@PathVariable Long id) {
        return complaintService.getComplaintById(id);
    }

    @PostMapping
    public Complaint createComplaint(@RequestBody Complaint complaint) {
        return complaintService.createComplaint(complaint);
    }

    @PutMapping("/{id}/status")
    public Complaint updateStatus(
            @PathVariable Long id,
            @RequestParam String status,
            @RequestParam(required = false, defaultValue = "") String remarks,
            @RequestParam(required = false, defaultValue = "AGENT") String updatedBy) {
        return complaintService.updateComplaintStatus(id, status, remarks, updatedBy);
    }

    @GetMapping("/{id}/timeline")
    public List<ComplaintTimeline> getTimeline(@PathVariable Long id) {
        return complaintService.getTimeline(id);
    }

    @GetMapping("/status/{status}")
    public List<Complaint> getByStatus(@PathVariable String status) {
        return complaintService.getComplaintsByStatus(status);
    }

    @GetMapping("/priority/{priorityLabel}")
    public List<Complaint> getByPriority(@PathVariable String priorityLabel) {
        return complaintService.getComplaintsByPriority(priorityLabel);
    }

    @DeleteMapping("/{id}")
    public void deleteComplaint(@PathVariable Long id) {
        complaintService.deleteComplaint(id);
    }
}