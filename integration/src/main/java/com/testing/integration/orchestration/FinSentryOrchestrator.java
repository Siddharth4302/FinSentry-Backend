package com.testing.integration.orchestration;

import com.testing.integration.agents.MonitoringAgent;
import com.testing.integration.agents.InvestigationAgent;
import com.testing.integration.agents.PolicyAgent;
import com.testing.integration.agents.RecommendationAgent;

import com.testing.integration.dtos.CustomerData;
import com.testing.integration.dtos.FinancialRiskAssessment;

import com.testing.integration.model.Customer;
import com.testing.integration.service.CustomerService;
import com.testing.integration.tools.FinSentryTool;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class FinSentryOrchestrator {

    private final MonitoringAgent monitoringAgent;
    private final InvestigationAgent investigationAgent;
    private final PolicyAgent policyAgent;
    private final RecommendationAgent recommendationAgent;
    private final FinSentryTool finSentryTool;
    private final CustomerService customerService;

    public FinSentryOrchestrator(
            MonitoringAgent monitoringAgent,
            InvestigationAgent investigationAgent,
            PolicyAgent policyAgent,
            RecommendationAgent recommendationAgent,
            FinSentryTool finSentryTool,
            CustomerService customerService
    ) {
        this.monitoringAgent = monitoringAgent;
        this.investigationAgent = investigationAgent;
        this.policyAgent = policyAgent;
        this.recommendationAgent = recommendationAgent;
        this.finSentryTool = finSentryTool;
        this.customerService = customerService;
    }

    // Analyze customer data directly
    public FinancialRiskAssessment analyze(CustomerData customerData) {

        return finSentryTool.analyzeCustomerFinancialRisk(customerData);
    }

    // Analyze customer stored in PostgreSQL
    public FinancialRiskAssessment analyzeStoredCustomer(Long customerId) {

        CustomerData customerData = getCustomerData(customerId);

        return policyAgent.validate(
                customerData,
                "No additional investigation findings. "
                        + "Validate the customer data directly against "
                        + "the connected financial policies."
        );
    }

    // Run Monitoring Agent and Investigation Agent
    public String investigateCustomer(Long customerId) {

        CustomerData customerData = getCustomerData(customerId);

        List<String> riskSignals =
                monitoringAgent.monitor(customerData);

        return investigationAgent.investigate(
                customerData,
                riskSignals
        );
    }

    // Run Monitoring, Investigation and Policy pipeline
    public FinancialRiskAssessment fullAnalysis(Long customerId) {

        CustomerData customerData = getCustomerData(customerId);

        // Step 1: Monitoring
        List<String> riskSignals =
                monitoringAgent.monitor(customerData);

        // Step 2: Investigation
        String investigationFindings =
                investigationAgent.investigate(
                        customerData,
                        riskSignals
                );

        // Step 3: Policy validation and financial scoring
        return policyAgent.validate(
                customerData,
                investigationFindings
        );
    }

    // Run complete four-agent pipeline
    public Map<String, Object> completeAnalysis(Long customerId) {

        CustomerData customerData = getCustomerData(customerId);

        // Step 1: Monitoring
        List<String> riskSignals =
                monitoringAgent.monitor(customerData);

        // Step 2: Investigation
        String investigationFindings =
                investigationAgent.investigate(
                        customerData,
                        riskSignals
                );

        // Step 3: Policy validation and financial scoring
        FinancialRiskAssessment assessment =
                policyAgent.validate(
                        customerData,
                        investigationFindings
                );

        // Step 4: Recommendation refinement
        String refinedRecommendations =
                recommendationAgent.refineRecommendations(assessment);

        return Map.of(
                "policyAssessment", assessment,
                "refinedRecommendations", refinedRecommendations
        );
    }

    // Test Policy Agent independently
    public FinancialRiskAssessment testPolicyAgent(Long customerId) {

        CustomerData customerData = getCustomerData(customerId);

        return policyAgent.validate(
                customerData,
                "No additional investigation findings. "
                        + "Validate the customer data directly against "
                        + "the financial policies."
        );
    }

    // Fetch customer and convert entity to DTO
    private CustomerData getCustomerData(Long customerId) {

        Customer customer =
                customerService.getCustomerById(customerId);

        return new CustomerData(
                customer.getSalaryTrend(),
                customer.getCreditUtilization(),
                customer.getMissedEmis(),
                customer.getBalanceChange()
        );
    }
}
