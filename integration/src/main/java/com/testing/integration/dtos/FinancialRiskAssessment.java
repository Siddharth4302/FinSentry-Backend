
package com.testing.integration.dtos;

import java.util.List;

public record FinancialRiskAssessment(
        Integer financialHealthScore,
        String riskLevel,
        List<String> riskFactors,
        String rootCause,
        List<String> recommendedActions,
        List<String> sources,
        boolean humanReviewRequired
) {
}
