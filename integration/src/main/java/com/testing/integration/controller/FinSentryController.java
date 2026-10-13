package com.testing.integration.controller;

import com.testing.integration.dtos.CustomerData;
import com.testing.integration.dtos.FinancialRiskAssessment;
import com.testing.integration.service.FinSentryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/finsentry")
public class FinSentryController {

    private final FinSentryService finSentryService;

    public FinSentryController(FinSentryService finSentryService) {
        this.finSentryService = finSentryService;
    }

    @PostMapping("/analyze")
    public ResponseEntity<FinancialRiskAssessment> analyze(
            @RequestBody CustomerData customer
    ) {

        FinancialRiskAssessment result =
                finSentryService.analyze(customer);

        return ResponseEntity.ok(result);
    }


}