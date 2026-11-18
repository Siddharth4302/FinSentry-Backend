
package com.testing.integration.service;

import com.testing.integration.dtos.CustomerData;
import com.testing.integration.dtos.FinancialScoreResult;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class FinancialScoringService {

    public FinancialScoreResult calculateScore(CustomerData customer) {

        if (customer == null) {
            throw new IllegalArgumentException(
                    "Customer data cannot be null"
            );
        }

        int deductions = 0;
        List<String> deductionDetails = new ArrayList<>();

        // 1. Salary trend
        if ("Declining".equalsIgnoreCase(customer.salaryTrend())) {
            deductions += 20;
            deductionDetails.add("Declining salary: -20");
        }

        // 2. Credit utilization
        int utilization = customer.creditUtilization();

        if (utilization > 80) {
            deductions += 25;
            deductionDetails.add("Credit utilization above 80%: -25");
        } else if (utilization > 70) {
            deductions += 15;
            deductionDetails.add("Credit utilization 71-80%: -15");
        } else if (utilization > 50) {
            deductions += 10;
            deductionDetails.add("Credit utilization 51-70%: -10");
        } else if (utilization > 30) {
            deductions += 5;
            deductionDetails.add("Credit utilization 31-50%: -5");
        }

        // 3. Missed EMIs
        int missedEmis = customer.missedEmis();

        if (missedEmis >= 3) {
            deductions += 25;
            deductionDetails.add("Three or more missed EMIs: -25");
        } else if (missedEmis == 2) {
            deductions += 20;
            deductionDetails.add("Two missed EMIs: -20");
        } else if (missedEmis == 1) {
            deductions += 10;
            deductionDetails.add("One missed EMI: -10");
        }

        // 4. Account balance decrease
        int balanceChange = customer.balanceChange();

        if (balanceChange < -30) {
            deductions += 20;
            deductionDetails.add("Balance decreased by more than 30%: -20");
        } else if (balanceChange < -20) {
            deductions += 10;
            deductionDetails.add("Balance decreased by more than 20%: -10");
        } else if (balanceChange <= -10) {
            deductions += 5;
            deductionDetails.add("Balance decreased by 10-20%: -5");
        }

        // 5. Calculate final score
        int score = Math.max(0, 100 - deductions);

        // 6. Classify score using Knowledge Base bands
        String riskLevel;

        if (score >= 80) {
            riskLevel = "Low Risk";
        } else if (score >= 60) {
            riskLevel = "Medium Risk";
        } else if (score >= 40) {
            riskLevel = "High Risk";
        } else {
            riskLevel = "Critical Risk";
        }

        return new FinancialScoreResult(
                score,
                riskLevel,
                100 - score,
                List.copyOf(deductionDetails)
        );
    }
}
