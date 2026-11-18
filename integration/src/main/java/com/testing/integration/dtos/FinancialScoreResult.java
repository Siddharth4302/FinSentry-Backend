
package com.testing.integration.dtos;

import java.util.List;

public record FinancialScoreResult(
        int financialHealthScore,
        String riskLevel,
        int totalDeductions,
        List<String> deductions
) {
}
