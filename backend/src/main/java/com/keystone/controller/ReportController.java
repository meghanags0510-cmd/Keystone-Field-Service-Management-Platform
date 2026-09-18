package com.keystone.controller;
import com.keystone.service.ReportService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
@RestController
@RequestMapping("/api/reports")
public class ReportController {
    private final ReportService service;
    public ReportController(ReportService service) { this.service = service; }
    @GetMapping("/summary") @PreAuthorize("hasAnyRole('MANAGER','DISPATCHER')")
    public Map<String,Object> summary() { return service.summary(); }
}
