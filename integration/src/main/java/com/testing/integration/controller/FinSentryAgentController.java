
package com.testing.integration.controller;

import com.testing.integration.dtos.CustomerData;
import com.testing.integration.dtos.FinancialRiskAssessment;
import com.testing.integration.orchestration.FinSentryOrchestrator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/finsentry/agent")
public class FinSentryAgentController {

    private final FinSentryOrchestrator orchestrator;
    private static final Logger log = LoggerFactory.getLogger(FinSentryOrchestrator.class);

    public FinSentryAgentController(
            FinSentryOrchestrator orchestrator
    ) {
        this.orchestrator = orchestrator;
    }

    // Analyze customer data directly
    @PostMapping("/analyze")
    public ResponseEntity<FinancialRiskAssessment> analyze(
            @RequestBody CustomerData customer
    ) {

        return ResponseEntity.ok(
                orchestrator.analyze(customer)
        );
    }

    // Analyze customer stored in PostgreSQL
    @PostMapping("/customer/{customerId}")
    public ResponseEntity<FinancialRiskAssessment> analyzeStoredCustomer(
            @PathVariable Long customerId
    ) {

        return ResponseEntity.ok(
                orchestrator.analyzeStoredCustomer(customerId)
        );
    }

    // Run Monitoring Agent and Investigation Agent
    @GetMapping("/investigate/{customerId}")
    public ResponseEntity<String> investigateCustomer(
            @PathVariable Long customerId
    ) {

        return ResponseEntity.ok(
                orchestrator.investigateCustomer(customerId)
        );
    }

    // Run Monitoring, Investigation and Policy pipeline
    @PostMapping("/full-analysis/{customerId}")
    public ResponseEntity<FinancialRiskAssessment> fullAnalysis(
            @PathVariable Long customerId
    ) {

        return ResponseEntity.ok(
                orchestrator.fullAnalysis(customerId)
        );
    }

    // Run complete four-agent pipeline
    @PostMapping("/complete-analysis/{customerId}")
    public ResponseEntity<Map<String, Object>> completeAnalysis(
            @PathVariable Long customerId
    ) {
        log.info(">>> COMPLETE ANALYSIS CONTROLLER HIT <<<");
        return ResponseEntity.ok(
                orchestrator.completeAnalysis(customerId)
        );
    }

    // Test Policy Agent independently
    @PostMapping("/policy-test/{customerId}")
    public ResponseEntity<FinancialRiskAssessment> testPolicyAgent(
            @PathVariable Long customerId
    ) {
        log.info(">>> COMPLETE ANALYSIS CONTROLLER HIT <<<");
        return ResponseEntity.ok(
                orchestrator.testPolicyAgent(customerId)
        );
    }
}
