
package com.testing.integration.agents;

import com.testing.integration.dtos.CustomerData;
import com.testing.integration.dtos.FinancialRiskAssessment;
import com.testing.integration.dtos.FinancialScoreResult;
import com.testing.integration.service.FinancialScoringService;
import com.testing.integration.service.FinSentryService;

import org.springframework.stereotype.Service;

@Service
public class PolicyAgent {

    private final FinSentryService finSentryService;
    private final FinancialScoringService financialScoringService;

    public PolicyAgent(
            FinSentryService finSentryService,
            FinancialScoringService financialScoringService
    ) {
        this.finSentryService = finSentryService;
        this.financialScoringService = financialScoringService;
    }

    public FinancialRiskAssessment validate(
            CustomerData customer,
            String investigationFindings
    ) {

        FinancialScoreResult scoreResult =
                financialScoringService.calculateScore(customer);

        return finSentryService.analyze(
                customer,
                investigationFindings,
                scoreResult
        );
    }
}
