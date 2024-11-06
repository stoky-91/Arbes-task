package com.phonecompany.service;

import com.phonecompany.billing.TelephoneBillCalculator;
import com.phonecompany.utils.FileUtils;

import java.math.BigDecimal;

public class TelephoneBillService {
    private final TelephoneBillCalculator calculator;

    public TelephoneBillService(TelephoneBillCalculator calculator) {
        this.calculator = calculator;
    }

    /**
     * Calculates the total cost of calls from a log file specified by the file path.
     *
     * @param filePath path to the file containing call log data
     * @return total cost of calls as BigDecimal
     * @throws IllegalArgumentException if filePath is null or empty
     */
    public BigDecimal calculateTotalCost(String filePath) {
        if (filePath == null || filePath.trim().isEmpty()) {
            throw new IllegalArgumentException("File path must not be null or empty");
        }

        String phoneLog = FileUtils.readFileAsString(filePath);
        return calculator.calculate(phoneLog);
    }
}
