package com.phonecompany.billing;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of a telephone bill calculator.
 * This class processes telephone logs, calculates the total costs based on calls,
 * and provides various utility methods for validation, parsing, and calculations.
 */
public class TelephoneBillCalculatorImpl implements TelephoneBillCalculator {

    private static final BigDecimal PEAK_RATE = BigDecimal.valueOf(1.0);
    private static final BigDecimal OFF_PEAK_RATE = BigDecimal.valueOf(0.5);
    private static final BigDecimal ADDITIONAL_MINUTE_RATE = BigDecimal.valueOf(0.2);
    private static final int FREE_MINUTES = 5;

    private static final String PHONE_NUMBER_REGEX = "\\d{12}";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    /**
     * Calculates the total cost based on the telephone log.
     *
     * @param phoneLog The telephone log as a string
     * @return The total amount to be paid
     */
    @Override
    public BigDecimal calculate(String phoneLog) {
        if (phoneLog == null || phoneLog.trim().isEmpty()) return BigDecimal.ZERO;

        List<CallDetail> calls = parsePhoneLog(phoneLog);
        BigDecimal totalCost = calculateTotalCost(calls, findMostFrequentNumber(calls));
        return totalCost.setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Calculates the total cost based on the list of calls, excluding the most frequent number.
     *
     * @param calls              The list of telephone calls
     * @param mostFrequentNumber The most frequent phone number to be excluded
     * @return The total amount to be paid
     */
    private BigDecimal calculateTotalCost(List<CallDetail> calls, String mostFrequentNumber) {
        return calls.stream()
                .filter(call -> !call.getPhoneNumber().equals(mostFrequentNumber))
                .map(this::calculateCallCost)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Parses the telephone log into a list of telephone call details.
     *
     * @param phoneLog The telephone log as a string
     * @return A list of {@link CallDetail} objects
     */
    private List<CallDetail> parsePhoneLog(String phoneLog) {
        return Arrays.stream(phoneLog.split("\n"))
                .map(line -> line.trim().replaceAll("^[^\\p{Print}]+", ""))
                .filter(line -> !line.isEmpty() && isValidLine(line))
                .map(this::createCallDetail)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * Creates a {@link CallDetail} object from a log line.
     * This method splits the log line into its respective fields, validates the phone number,
     * and parses the start and end times. If valid, it returns a new {@link CallDetail}.
     *
     * @param line The line from the telephone log
     * @return A {@link CallDetail} object or {@code null} if the line is invalid
     */
    private CallDetail createCallDetail(String line) {
        String[] fields = line.split(",");
        String phoneNumber = fields[0].trim();

        if (!isValidPhoneNumber(phoneNumber)) {
            System.err.println("Invalid phone number format, skipping line: " + phoneNumber);
            return null;
        }
        LocalDateTime[] times = parseTimes(fields);
        return (times != null && times[1].isAfter(times[0])) ? new CallDetail(phoneNumber, times[0], times[1]) : null;
    }

    /**
     * Validates if the log line contains exactly three fields (phone number, start time, end time).
     *
     * @param line The line from the telephone log
     * @return {@code true} if the line has exactly three fields, {@code false} otherwise
     */
    private boolean isValidLine(String line) {
        String[] fields = line.split(",");
        if (fields.length != 3) {
            System.err.println("Invalid log format, skipping line: " + line);
            return false;
        }
        return true;
    }

    /**
     * Parses the start and end times from the fields of a log line.
     * The method assumes the times are in a valid format (based on the provided formatter).
     * If an exception occurs while parsing, the method will return {@code null}.
     *
     * @param fields The array of fields from the log line
     * @return An array of two {@link LocalDateTime} objects: the start and end times of the call,
     * or {@code null} if the times are invalid or parsing fails
     */
    private LocalDateTime[] parseTimes(String[] fields) {
        try {
            return new LocalDateTime[]{
                    LocalDateTime.parse(fields[1].trim(), formatter),
                    LocalDateTime.parse(fields[2].trim(), formatter)
            };
        } catch (Exception e) {
            System.err.println("Invalid date format, skipping line: " + fields[0]);
            return null;
        }
    }

    /**
     * Validates a phone number by checking if it matches the predefined regular expression.
     * This method checks if the phone number contains between 9 and 13 digits.
     *
     * @param phoneNumber The phone number to validate
     * @return {@code true} if the phone number is valid, {@code false} otherwise
     */
    private boolean isValidPhoneNumber(String phoneNumber) {
        return phoneNumber.matches(PHONE_NUMBER_REGEX);
    }

    /**
     * Calculates the total cost of a phone call based on its duration and the time of day.
     * The method takes into account free minutes, peak hours, and off-peak hours.
     *
     * @param call The {@link CallDetail} object representing a single call
     * @return The total cost of the call
     */
    private BigDecimal calculateCallCost(CallDetail call) {
        BigDecimal cost = BigDecimal.ZERO;
        LocalDateTime start = call.getStartDateTime();
        LocalDateTime end = call.getEndDateTime();
        int minutesCounted = 0;

        while (start.isBefore(end)) {
            minutesCounted++;
            boolean isPeakHour = start.toLocalTime().isAfter(LocalTime.of(7, 59)) && start.toLocalTime().isBefore(LocalTime.of(16, 0));
            cost = cost.add(minutesCounted > FREE_MINUTES ? ADDITIONAL_MINUTE_RATE : isPeakHour ? PEAK_RATE : OFF_PEAK_RATE);
            start = start.plusMinutes(1);
        }

        return cost;
    }

    /**
     * Finds the most frequently occurring phone number from a list of calls.
     * This method uses a frequency map to count the occurrences of each phone number and
     * returns the number that appears the most.
     *
     * @param calls The list of {@link CallDetail} objects representing the phone calls
     * @return The most frequent phone number, or an empty string if the list is empty
     */
    private String findMostFrequentNumber(List<CallDetail> calls) {
        if (calls.isEmpty()) return "";

        Map<String, Integer> frequencyMap = buildFrequencyMap(calls);
        return findMaxFrequencyNumber(frequencyMap);
    }

    /**
     * Builds a frequency map that counts the occurrences of each phone number in the list of calls.
     *
     * @param calls The list of {@link CallDetail} objects representing the phone calls
     * @return A map where the key is the phone number, and the value is the frequency of occurrences
     */
    private Map<String, Integer> buildFrequencyMap(List<CallDetail> calls) {
        Map<String, Integer> frequencyMap = new HashMap<>();
        for (CallDetail call : calls) {
            frequencyMap.put(call.getPhoneNumber(), frequencyMap.getOrDefault(call.getPhoneNumber(), 0) + 1);
        }
        return frequencyMap;
    }

    /**
     * Finds the phone number with the highest frequency from a map of phone numbers and their frequencies.
     * If multiple phone numbers have the same frequency, the one with the lexicographically smallest value is returned.
     *
     * @param frequencyMap A map containing phone numbers and their respective frequencies
     * @return The phone number with the highest frequency
     */
    private String findMaxFrequencyNumber(Map<String, Integer> frequencyMap) {
        return frequencyMap.entrySet().stream()
                .max(Comparator.comparingInt((Map.Entry<String, Integer> entry) -> entry.getValue())
                        .thenComparing(Map.Entry.comparingByKey()))
                .map(Map.Entry::getKey)
                .orElse("");
    }
}
