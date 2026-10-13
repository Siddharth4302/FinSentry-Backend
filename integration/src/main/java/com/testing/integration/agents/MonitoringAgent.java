package com.testing.integration.agents;

import com.testing.integration.dtos.CustomerData;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MonitoringAgent {

    public List<String> monitor(CustomerData customer) {

        List<String> riskSignals = new ArrayList<>();

        if (customer.salaryTrend() != null
                && customer.salaryTrend().equalsIgnoreCase("Declining")) {
            riskSignals.add("Salary trend is declining.");
        }

        if (customer.creditUtilization() > 80) {
            riskSignals.add(
                    "Credit card utilization exceeds 80%."
            );
        }

        if (customer.missedEmis() >= 2) {
            riskSignals.add(
                    "Customer has missed 2 or more EMIs."
            );
        }

        if (customer.balanceChange() <= -30) {
            riskSignals.add(
                    "Account balance has decreased by 30% or more."
            );
        }

        return riskSignals;
    }
}
