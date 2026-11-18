
package com.testing.integration.service;

import com.testing.integration.dtos.CustomerData;
import com.testing.integration.dtos.FinancialRiskAssessment;
import com.testing.integration.dtos.FinancialScoreResult;
import com.testing.integration.dtos.LlmInvokeRequest;
import com.testing.integration.dtos.LlmInvokeResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClient;

import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class FinSentryService {

    private static final Logger log =
            LoggerFactory.getLogger(FinSentryService.class);

    private final RestClient restClient;
    private final ObjectMapper objectMapper;
    private final FinancialScoringService financialScoringService;

    @Value("${finsentry.api-url}")
    private String apiUrl;

    @Value("${finsentry.api-key}")
    private String apiKey;

    @Value("${finsentry.knowledge-base-id}")
    private String knowledgeBaseId;

    public FinSentryService(
            RestClient restClient,
            ObjectMapper objectMapper,
            FinancialScoringService financialScoringService
    ) {
        this.restClient = restClient;
        this.objectMapper = objectMapper;
        this.financialScoringService = financialScoringService;
    }

    // Backward-compatible method
    public FinancialRiskAssessment analyze(CustomerData customer) {
        return analyze(
                customer,
                "No investigation findings provided."
        );
    }

    // Calculate score automatically for existing callers
    public FinancialRiskAssessment analyze(
            CustomerData customer,
            String investigationFindings
    ) {
        FinancialScoreResult scoreResult =
                financialScoringService.calculateScore(customer);

        return analyze(
                customer,
                investigationFindings,
                scoreResult
        );
    }

    // Validate customer data and Java-calculated score against policies
    public FinancialRiskAssessment analyze(
            CustomerData customer,
            String investigationFindings,
            FinancialScoreResult scoreResult
    ) {

        if (customer == null || scoreResult == null) {
            throw new IllegalArgumentException(
                    "Customer data and score result cannot be null"
            );
        }

        String findings = (investigationFindings == null
                || investigationFindings.isBlank())
                ? "No investigation findings provided."
                : investigationFindings;

        String scoreBreakdown = scoreResult.deductions().isEmpty()
                ? "No deductions applied."
                : String.join("\n", scoreResult.deductions());

        String customerText = """
                Customer Financial Data

                Salary Trend: %s
                Credit Utilization: %d%%
                Missed EMI: %d
                Balance Change: %d%%

                Java Financial Scoring Results

                Financial Health Score: %d/100
                Score-Based Risk Classification: %s
                Total Deductions: %d

                Deduction Breakdown:
                %s

                Investigation Agent Findings:
                %s

                Validate the customer information, calculated financial
                score, and investigation findings against the connected
                financial policy knowledge base.

                The financial score has already been calculated by the
                Java scoring engine. Validate its classification against
                the knowledge base score bands.
                """.formatted(
                customer.salaryTrend(),
                customer.creditUtilization(),
                customer.missedEmis(),
                customer.balanceChange(),
                scoreResult.financialHealthScore(),
                scoreResult.riskLevel(),
                scoreResult.totalDeductions(),
                scoreBreakdown,
                findings
        );

        String systemPrompt = """
                You are FinSentry AI, a financial policy validation assistant.

                Your role is to validate financial distress findings
                using the connected financial policy knowledge base.

                Analyze:
                - Salary trends
                - Credit card utilization
                - Account balance changes
                - Missed EMIs
                - Investigation Agent findings
                - Java-calculated financial health score

                Use the connected knowledge base as the authoritative
                source for financial policies, thresholds, risk levels
                and recommended actions.

                Do not invent financial rules or thresholds.

                Policy validation requirements:

                1. Validate Investigation Agent findings against the
                   authoritative knowledge base.

                2. Do not accept hypotheses as confirmed facts.

                3. Clearly distinguish observed customer data from
                   possible explanations.

                4. Do not invent policy thresholds or recommendations.

                Exact policy interpretation rules:

                1. High-risk distress trigger:
                   Credit utilization must be strictly greater than 80%
                   AND salary trend must be declining.

                2. EMI restructuring:
                   Recommend only when missed EMIs are greater than
                   or equal to 2.

                3. Financial assistance review:
                   Recommend only when account balance decreases by
                   strictly more than 30% over 3 months.

                4. A balance decrease of exactly 30% does not satisfy
                   the financial assistance review threshold.

                5. Distinguish between:
                   - Policy-triggered recommendations
                   - Preventive monitoring suggestions
                   - Information requiring human review

                6. All recommendations require human review
                   before execution.

                Financial Health Scoring Rules:

                - Score 80-100: Low Risk
                - Score 60-79: Medium Risk
                - Score 40-59: High Risk
                - Score below 40: Critical Risk

                Scoring requirements:

                - The financial score has already been calculated
                  by the Java scoring engine.
                - Do not recalculate, modify, estimate or invent
                  the financial health score.
                - Use the exact score and classification supplied
                  in the customer data.
                - Validate that the supplied classification matches
                  the knowledge base score bands.
                - Keep score-based classification distinct from
                  policy-triggered financial distress alerts.
                - Do not allow investigation findings to override
                  the calculated score.

                Return ONLY valid JSON.
                Do not use Markdown.
                Do not use code fences.
                Do not include explanations outside the JSON.

                The JSON must have exactly this structure:

                {
                  "financialHealthScore": null,
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

                - Return the supplied financial health score.
                - Return the supplied score-based risk classification.
                - riskFactors must explain applicable policy thresholds
                  and whether they were breached.
                - rootCause must summarize likely financial distress
                  based on customer data and investigation findings.
                - Do not present possible explanations as confirmed facts.
                - recommendedActions must come from or be supported
                  by the knowledge base.
                - Clearly distinguish policy-triggered actions from
                  preventive monitoring suggestions.
                - sources must contain the titles of the knowledge base
                  documents used.
                - Do not fabricate policy document titles.
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

        long startTime = System.currentTimeMillis();

        try {

            log.info("Starting FinSentry policy validation API call");

            LlmInvokeResponse response = restClient.post()
                    .uri(apiUrl)
                    .header("Accept", "application/json")
                    .header("x-api-key", apiKey)
                    .header("Content-Type", "application/json")
                    .body(request)
                    .retrieve()
                    .body(LlmInvokeResponse.class);

            long duration = System.currentTimeMillis() - startTime;

            log.info(
                    "FinSentry policy validation API call completed in {} ms",
                    duration
            );

            if (response == null
                    || response.content() == null
                    || response.content().isBlank()) {

                throw new RuntimeException(
                        "FinSentry API returned an empty response"
                );
            }

            try {

                String cleanedResponse =
                        extractJson(response.content());

                FinancialRiskAssessment assessment =
                        objectMapper.readValue(
                                cleanedResponse,
                                FinancialRiskAssessment.class
                        );

                // Keep Java scoring results authoritative
                List<String> riskFactors = new ArrayList<>();

                if (assessment.riskFactors() != null) {
                    riskFactors.addAll(assessment.riskFactors());
                }

                riskFactors.add(
                        "Java Financial Scoring: "
                                + scoreResult.financialHealthScore()
                                + "/100"
                );

                riskFactors.addAll(scoreResult.deductions());

                FinancialRiskAssessment finalAssessment =
                        new FinancialRiskAssessment(
                                scoreResult.financialHealthScore(),
                                scoreResult.riskLevel(),
                                riskFactors,
                                assessment.rootCause(),
                                assessment.recommendedActions(),
                                assessment.sources(),
                                true
                        );

                log.info(
                        "FinSentry assessment completed. Score: {}, Risk level: {}",
                        finalAssessment.financialHealthScore(),
                        finalAssessment.riskLevel()
                );

                return finalAssessment;

            } catch (Exception e) {

                log.error(
                        "Failed to parse FinSentry API response after {} ms",
                        duration,
                        e
                );

                throw new RuntimeException(
                        "FinSentry returned invalid JSON",
                        e
                );
            }

        } catch (HttpServerErrorException e) {

            long duration = System.currentTimeMillis() - startTime;

            log.error(
                    "FinSentry API returned HTTP {} after {} ms",
                    e.getStatusCode(),
                    duration
            );

            throw e;
        }
    }

    // Removes Markdown code fences before JSON deserialization
    private String extractJson(String content) {

        String cleaned = content.trim();

        if (cleaned.startsWith("```")) {

            cleaned = cleaned.replaceFirst(
                    "^```(?:json)?\\s*",
                    ""
            );

            cleaned = cleaned.replaceFirst(
                    "\\s*```$",
                    ""
            );
        }

        return cleaned.trim();
    }
}