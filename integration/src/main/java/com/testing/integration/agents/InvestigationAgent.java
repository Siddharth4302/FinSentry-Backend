package com.testing.integration.agents;

import com.testing.integration.dtos.CustomerData;
import com.testing.integration.service.GroqService;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InvestigationAgent {

    private final GroqService groqService;

    public InvestigationAgent(GroqService groqService) {
        this.groqService = groqService;
    }

    public String investigate(
            CustomerData customer,
            List<String> riskSignals) {

        if (customer == null) {
            throw new IllegalArgumentException(
                    "Customer data cannot be null"
            );
        }

        if (riskSignals == null || riskSignals.isEmpty()) {
            return "No significant financial risk signals detected. "
                    + "Further investigation is not required.";
        }

        String systemPrompt = """
                You are FinSentry's Financial Investigation Agent.

                Your responsibility is to investigate potential causes
                of a customer's financial distress.

                You will receive customer financial data and risk signals
                detected by a separate Monitoring Agent.

                Analyze the information and provide:

                1. Risk Overview
                2. Potential Root Causes
                3. Relationships Between Risk Signals
                4. Financial Distress Assessment
                5. Missing Information

                Important guidelines:
                - Base your analysis on the provided information.
                - Do not invent customer information.
                - Clearly distinguish facts from hypotheses.
                - Do not claim a root cause is confirmed without evidence.
                - Do not generate financial recommendations.
                - Keep the analysis professional and concise.

                Additional constraints:
                - Never infer a debt-to-income ratio unless explicitly provided.
                - Do not assume when missed payments occurred.
                - Do not present potential default as an established outcome.
                - Clearly label all causal explanations as hypotheses.
                - Base conclusions strictly on the supplied customer data.
                - Do not interpret policy thresholds or override the
                  Monitoring Agent's detected signals.
                """;

        String userPrompt = """
                Investigate the following customer:

                Customer Financial Data:
                - Salary Trend: %s
                - Credit Utilization: %d%%
                - Missed EMIs: %d
                - Account Balance Change: %d%%

                Risk Signals Detected by Monitoring Agent:
                %s

                Analyze these signals and identify potential causes
                of financial distress.
                """.formatted(
                customer.salaryTrend(),
                customer.creditUtilization(),
                customer.missedEmis(),
                customer.balanceChange(),
                String.join("\n", riskSignals)
        );

        return groqService.chat(systemPrompt, userPrompt);
    }
}