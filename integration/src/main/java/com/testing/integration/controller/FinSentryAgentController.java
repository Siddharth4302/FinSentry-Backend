package com.testing.integration.controller;

import com.testing.integration.dtos.CustomerData;
import com.testing.integration.dtos.FinancialRiskAssessment;
import com.testing.integration.tools.FinSentryTool;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.testing.integration.dtos.CustomerData;
import com.testing.integration.dtos.FinancialRiskAssessment;
import com.testing.integration.model.Customer;
import com.testing.integration.service.CustomerService;
import com.testing.integration.tools.FinSentryTool;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@RestController
@RequestMapping("/api/finsentry/agent")
public class FinSentryAgentController {

    private final FinSentryTool finSentryTool;
    private final CustomerService customerService;

    public FinSentryAgentController(
            FinSentryTool finSentryTool,
            CustomerService customerService) {
        this.finSentryTool = finSentryTool;
        this.customerService = customerService;
    }

    @PostMapping("/analyze")
    public ResponseEntity<FinancialRiskAssessment> analyze(
            @RequestBody CustomerData customer
    ) {

        FinancialRiskAssessment result =
                finSentryTool.analyzeCustomerFinancialRisk(customer);

        return ResponseEntity.ok(result);
    }

    @PostMapping("/customer/{customerId}")
    public ResponseEntity<FinancialRiskAssessment> analyzeStoredCustomer(
            @PathVariable Long customerId) {

        Customer customer = customerService.getCustomerById(customerId);

        CustomerData customerData = new CustomerData(
                customer.getSalaryTrend(),
                customer.getCreditUtilization(),
                customer.getMissedEmis(),
                customer.getBalanceChange()
        );

        FinancialRiskAssessment assessment =
                finSentryTool.analyzeCustomerFinancialRisk(customerData);

        return ResponseEntity.ok(assessment);
    }
}