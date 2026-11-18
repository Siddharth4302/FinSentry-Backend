
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
                potential financial distress using the FinSentry financial policies.

                The tool evaluates salary trends, credit utilization,
                missed EMIs, and account balance changes against the
                connected financial policy knowledge base.

                It returns the financial health score when an approved
                scoring formula is available, risk level, risk factors,
                root cause, policy-supported recommended actions,
                source policies, and human review requirements.
                """
    )
    public FinancialRiskAssessment analyzeCustomerFinancialRisk(
            CustomerData customer
    ) {
        return finSentryService.analyze(customer);
    }
}