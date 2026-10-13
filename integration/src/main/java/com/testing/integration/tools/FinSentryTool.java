package com.testing.integration.tools;

import com.testing.integration.dtos.CustomerData;
import com.testing.integration.dtos.FinancialRiskAssessment;
import com.testing.integration.service.FinSentryService;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

@Component
public class FinSentryTool {

    private final FinSentryService finSentryService;

    public FinSentryTool(FinSentryService finSentryService) {
        this.finSentryService = finSentryService;
    }

    @Tool(
            name = "analyzeCustomerFinancialRisk",
            description = """
                Analyze a banking customer's financial health and identify
                financial distress using the FinSentry financial policies.

                The tool analyzes salary trend, credit utilization, missed EMIs,
                and account balance changes.

                It returns the financial health score, risk level, risk factors,
                root cause, recommended actions, source policies, and whether
                human review is required.
                """
    )
    public FinancialRiskAssessment analyzeCustomerFinancialRisk(
            CustomerData customer
    ) {
        return finSentryService.analyze(customer);
    }
}