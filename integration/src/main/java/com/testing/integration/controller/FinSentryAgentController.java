
package com.testing.integration.controller;

import com.testing.integration.agents.MonitoringAgent;
import com.testing.integration.agents.InvestigationAgent;
import com.testing.integration.agents.PolicyAgent;
import com.testing.integration.agents.RecommendationAgent;
import com.testing.integration.dtos.CustomerData;
import com.testing.integration.dtos.FinancialRiskAssessment;
import com.testing.integration.model.Customer;
import com.testing.integration.service.CustomerService;
import com.testing.integration.tools.FinSentryTool;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/finsentry/agent")
public class FinSentryAgentController {

    private final MonitoringAgent monitoringAgent;
    private final InvestigationAgent investigationAgent;
    private final PolicyAgent policyAgent;
    private final FinSentryTool finSentryTool;
    private final CustomerService customerService;
    private final RecommendationAgent recommendationAgent;

    public FinSentryAgentController(
            FinSentryTool finSentryTool,
            CustomerService customerService,
            MonitoringAgent monitoringAgent,
            InvestigationAgent investigationAgent,
            PolicyAgent policyAgent,
            RecommendationAgent recommendationAgent) {

        this.finSentryTool = finSentryTool;
        this.customerService = customerService;
        this.monitoringAgent = monitoringAgent;
        this.investigationAgent = investigationAgent;
        this.policyAgent = policyAgent;
        this.recommendationAgent = recommendationAgent;
    }

    // Analyze customer data directly
    @PostMapping("/analyze")
    public ResponseEntity<FinancialRiskAssessment> analyze(
            @RequestBody CustomerData customer) {

        FinancialRiskAssessment result =
                finSentryTool.analyzeCustomerFinancialRisk(customer);

        return ResponseEntity.ok(result);
    }

    // Analyze customer stored in PostgreSQL
    @PostMapping("/customer/{customerId}")
    public ResponseEntity<FinancialRiskAssessment> analyzeStoredCustomer(
            @PathVariable Long customerId) {

        Customer customer = customerService.getCustomerById(customerId);

        CustomerData customerData = toCustomerData(customer);

        FinancialRiskAssessment assessment =
                policyAgent.validate(
                        customerData,
                        "No additional investigation findings. "
                                + "Validate the customer data directly against "
                                + "the connected financial policies."
                );

        return ResponseEntity.ok(assessment);
    }

    // Run Monitoring Agent and Investigation Agent
    @GetMapping("/investigate/{customerId}")
    public ResponseEntity<String> investigateCustomer(
            @PathVariable Long customerId) {

        Customer customer = customerService.getCustomerById(customerId);

        CustomerData customerData = toCustomerData(customer);

        List<String> riskSignals = monitoringAgent.monitor(customerData);

        String investigation =
                investigationAgent.investigate(customerData, riskSignals);

        return ResponseEntity.ok(investigation);
    }

    // Run the complete Monitoring, Investigation and Policy pipeline
    @PostMapping("/full-analysis/{customerId}")
    public ResponseEntity<FinancialRiskAssessment> fullAnalysis(
            @PathVariable Long customerId) {

        // Step 1: Fetch customer data
        Customer customer = customerService.getCustomerById(customerId);

        CustomerData customerData = toCustomerData(customer);

        // Step 2: Monitoring Agent
        List<String> riskSignals = monitoringAgent.monitor(customerData);

        // Step 3: Investigation Agent
        String investigationFindings =
                investigationAgent.investigate(customerData, riskSignals);

        // Step 4: Policy Agent
        FinancialRiskAssessment assessment =
                policyAgent.validate(customerData, investigationFindings);

        return ResponseEntity.ok(assessment);
    }

    // Run the complete four-agent pipeline
    @PostMapping("/complete-analysis/{customerId}")
    public ResponseEntity<Map<String, Object>> completeAnalysis(
            @PathVariable Long customerId) {

        // Step 1: Fetch customer data
        Customer customer = customerService.getCustomerById(customerId);

        CustomerData customerData = toCustomerData(customer);

        // Step 2: Monitoring Agent
        List<String> riskSignals = monitoringAgent.monitor(customerData);

        // Step 3: Investigation Agent
        String investigationFindings =
                investigationAgent.investigate(customerData, riskSignals);

        // Step 4: Policy Agent
        FinancialRiskAssessment assessment =
                policyAgent.validate(customerData, investigationFindings);

        // Step 5: Recommendation Agent
        String refinedRecommendations =
                recommendationAgent.refineRecommendations(assessment);

        // Step 6: Return results
        Map<String, Object> result = Map.of(
                "policyAssessment", assessment,
                "refinedRecommendations", refinedRecommendations
        );

        return ResponseEntity.ok(result);
    }

    // Test Policy Agent independently
    @PostMapping("/policy-test/{customerId}")
    public ResponseEntity<FinancialRiskAssessment> testPolicyAgent(
            @PathVariable Long customerId) {

        Customer customer = customerService.getCustomerById(customerId);

        CustomerData customerData = toCustomerData(customer);

        FinancialRiskAssessment assessment =
                policyAgent.validate(
                        customerData,
                        "No additional investigation findings. "
                                + "Validate the customer data directly against "
                                + "the financial policies."
                );

        return ResponseEntity.ok(assessment);
    }

    // Convert database Customer entity into CustomerData DTO
    private CustomerData toCustomerData(Customer customer) {

        return new CustomerData(
                customer.getSalaryTrend(),
                customer.getCreditUtilization(),
                customer.getMissedEmis(),
                customer.getBalanceChange()
        );
    }
}