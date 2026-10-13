package com.testing.integration.service;

import com.testing.integration.dtos.CustomerData;
import com.testing.integration.dtos.FinancialRiskAssessment;
import com.testing.integration.dtos.LlmInvokeRequest;
import com.testing.integration.dtos.LlmInvokeResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.UUID;

@Service
public class FinSentryService {

    private final RestClient restClient;
    private final tools.jackson.databind.ObjectMapper objectMapper;

    @Value("${finsentry.api-url}")
    private String apiUrl;

    @Value("${finsentry.api-key}")
    private String apiKey;

    @Value("${finsentry.knowledge-base-id}")
    private String knowledgeBaseId;

    public FinSentryService(
            RestClient restClient,
            ObjectMapper objectMapper
    ) {
        this.restClient = restClient;
        this.objectMapper = objectMapper;
    }

    public FinancialRiskAssessment analyze(CustomerData customer) {

        String customerText = """
                Customer Data

                Salary Trend: %s
                Credit Utilization: %d%%
                Missed EMI: %d
                Balance Change: %d%%

                Analyze this customer using the connected knowledge base.
                """.formatted(
                customer.salaryTrend(),
                customer.creditUtilization(),
                customer.missedEmis(),
                customer.balanceChange()
        );

        String systemPrompt = """
                You are FinSentry AI, a financial distress analysis assistant.

                Your role is to identify early financial distress in banking customers.

                Analyze:
                - Salary trends
                - Credit card utilization
                - Account balance changes
                - Missed EMIs
                - Late payments

                Use the connected knowledge base as the authoritative source
                for financial scoring rules, thresholds, risk levels, and recommended actions.

                Do not invent financial rules or thresholds that are not supported
                by the knowledge base.

                Return ONLY valid JSON.
                Do not use Markdown.
                Do not use code fences.
                Do not include explanations outside the JSON.

                The JSON must have exactly this structure:

                {
                  "financialHealthScore": 0,
                  "riskLevel": "string",
                  "riskFactors": [
                    "string"
                  ],
                  "rootCause": "string",
                  "recommendedActions": [
                    "string"
                  ],
                  "sources": [
                    "string"
                  ],
                  "humanReviewRequired": true
                }

                Requirements:

                - financialHealthScore must be a number from 0 to 100.
                - riskLevel must be based on the knowledge base.
                - riskFactors must explain the policy thresholds that were breached.
                - rootCause must summarize the likely financial distress based on the customer data.
                - recommendedActions must come from or be supported by the knowledge base.
                - sources must contain the titles of the knowledge base documents used.
                - humanReviewRequired must always be true.
                - All recommendations require human review before execution.
                """;

        LlmInvokeRequest.ModelKwargs modelKwargs =
                new LlmInvokeRequest.ModelKwargs(
                        2048,
                        0.2,
                        false,
                        0.9
                );

        LlmInvokeRequest.LlmData data =
                new LlmInvokeRequest.LlmData(
                        "chain",
                        customerText,
                        List.of(),
                        "anthropic.claude-sonnet-4-6",
                        "bedrock",
                        systemPrompt,
                        UUID.randomUUID().toString(),
                        knowledgeBaseId,
                        modelKwargs
                );

        LlmInvokeRequest request =
                new LlmInvokeRequest(
                        "run",
                        "langchain",
                        data
                );

        LlmInvokeResponse response = restClient.post()
                .uri(apiUrl)
                .header("Accept", "application/json")
                .header("x-api-key", apiKey)
                .header("Content-Type", "application/json")
                .body(request)
                .retrieve()
                .body(LlmInvokeResponse.class);

        if (response == null || response.content() == null) {
            throw new RuntimeException(
                    "FinSentry API returned an empty response"
            );
        }

        try {
            return objectMapper.readValue(
                    response.content(),
                    FinancialRiskAssessment.class//spring ai 2.0
            );

        } catch (Exception e) {

            throw new RuntimeException(
                    "FinSentry returned invalid JSON: "
                            + response.content(),
                    e
            );
        }
    }
}