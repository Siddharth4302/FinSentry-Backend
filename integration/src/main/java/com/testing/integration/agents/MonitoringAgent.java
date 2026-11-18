package com.testing.integration.agents;

import com.testing.integration.dtos.CustomerData;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MonitoringAgent {

    public List<String> monitor(CustomerData customer) {

        if (customer == null) {
            throw new IllegalArgumentException(
                    "Customer data cannot be null"
            );
        }

        List<String> riskSignals = new ArrayList<>();

        // Declining salary
        if (customer.salaryTrend() != null
                && customer.salaryTrend().equalsIgnoreCase("Declining")) {

            riskSignals.add("Salary trend is declining.");
        }

        // Credit utilization above 80%
        if (customer.creditUtilization() > 80) {

            riskSignals.add(
                    "Credit card utilization exceeds 80%."
            );
        }

        // Two or more missed EMIs
        if (customer.missedEmis() >= 2) {

            riskSignals.add(
                    "Customer has missed 2 or more EMIs."
            );
        }

        // Balance decrease strictly greater than 30%
        if (customer.balanceChange() < -30) {

            riskSignals.add(
                    "Account balance has decreased by more than 30%."
            );
        }

        return riskSignals;
    }
}