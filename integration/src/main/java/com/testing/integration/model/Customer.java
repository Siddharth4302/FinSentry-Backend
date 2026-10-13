
package com.testing.integration.model;

import jakarta.persistence.*;

@Entity
@Table(name = "customers")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "customer_code", nullable = false, unique = true)
    private String customerCode;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "salary_trend", nullable = false)
    private String salaryTrend;

    @Column(name = "credit_utilization", nullable = false)
    private Integer creditUtilization;

    @Column(name = "missed_emis", nullable = false)
    private Integer missedEmis;

    @Column(name = "balance_change", nullable = false)
    private Integer balanceChange;

    public Customer() {
    }

    public Long getId() {
        return id;
    }

    public String getCustomerCode() {
        return customerCode;
    }

    public void setCustomerCode(String customerCode) {
        this.customerCode = customerCode;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getSalaryTrend() {
        return salaryTrend;
    }

    public void setSalaryTrend(String salaryTrend) {
        this.salaryTrend = salaryTrend;
    }

    public Integer getCreditUtilization() {
        return creditUtilization;
    }

    public void setCreditUtilization(Integer creditUtilization) {
        this.creditUtilization = creditUtilization;
    }

    public Integer getMissedEmis() {
        return missedEmis;
    }

    public void setMissedEmis(Integer missedEmis) {
        this.missedEmis = missedEmis;
    }

    public Integer getBalanceChange() {
        return balanceChange;
    }

    public void setBalanceChange(Integer balanceChange) {
        this.balanceChange = balanceChange;
    }
}
