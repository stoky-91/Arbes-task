package com.phonecompany;

import com.phonecompany.billing.TelephoneBillCalculator;
import com.phonecompany.billing.TelephoneBillCalculatorImpl;
import com.phonecompany.service.TelephoneBillService;

import java.math.BigDecimal;

public class Main {
    public static void main(String[] args) {

        String filePath = "calls.csv";
        TelephoneBillCalculator calculator = new TelephoneBillCalculatorImpl();
        TelephoneBillService billService = new TelephoneBillService(calculator);
        BigDecimal totalCost = billService.calculateTotalCost(filePath);

        System.out.println("Total amount to be paid: " + totalCost + " Kč");
    }

}
