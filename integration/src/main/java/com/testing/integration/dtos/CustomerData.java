package com.testing.integration.dtos;


public record CustomerData(
        String salaryTrend,
        int creditUtilization,
        int missedEmis,
        int balanceChange
) {
}
