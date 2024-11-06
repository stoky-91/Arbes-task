package com.phonecompany.billing;

import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.junit.Assert.assertEquals;

public class TelephoneBillCalculatorImplTest {

    private TelephoneBillCalculatorImpl calculator;

    @Before
    public void setUp() {
        calculator = new TelephoneBillCalculatorImpl();
    }

    @Test
    public void testCalculateEmptyLog() {
        String phoneLog = "";
        BigDecimal result = calculator.calculate(phoneLog);
        assertEquals("Expected zero for empty log", BigDecimal.ZERO, result);
    }

    @Test
    public void testCalculationForSingleCall() {
        String phoneLog = "420607607607,07-07-2023 13:01:00,07-07-2023 13:04:58";
        assertEquals("Expected correct calculation for single call", BigDecimal.valueOf(0).setScale(2, RoundingMode.HALF_UP), calculator.calculate(phoneLog));
    }

    @Test
    public void testCalculationWithTwoNumbers() {
        String phoneLog = ("420607607607,18-11-2021 12:56:00,18-11-2021 13:13:13\n" +
                "420721721721,25-10-2021 19:44:00,26-10-2021 01:05:01");
        assertEquals("Expected correct calculation for multiple calls", BigDecimal.valueOf(7.6).setScale(2, RoundingMode.HALF_UP), calculator.calculate(phoneLog));
    }

    @Test
    public void testCallDurationLessThanFiveMinutesInPeak() {
        String phoneLog = "420607607607,18-11-2021 14:21:21,18-11-2021 14:23:57\n420721721721,25-10-2021 09:45:25,25-10-2021 09:48:00";
        assertEquals("Expected correct calculation for calls under 5 minutes", BigDecimal.valueOf(3).setScale(2, RoundingMode.HALF_UP), calculator.calculate(phoneLog));
    }

    @Test
    public void testShortCallDurationLessThanFiveMinutesNonPeak() {
        String phoneLog = "420607607607,18-11-2021 21:21:21,18-11-2021 21:23:25\n420721721721,25-10-2021 22:59:20,25-10-2021 23:03:00";
        assertEquals("Expected correct cost for short non-peak call", BigDecimal.valueOf(1.5).setScale(2, RoundingMode.HALF_UP), calculator.calculate(phoneLog));
    }

    @Test
    public void testLongCallDurationUnderFiveMinutesNonPeak() {
        String phoneLog = "420607607607,18-11-2021 20:20:20,18-11-2021 21:21:00\n420721721721,25-10-2021 19:08:18,25-10-2021 19:22:00";
        assertEquals("Expected correct cost for long non-peak call", BigDecimal.valueOf(13.7).setScale(2, RoundingMode.HALF_UP), calculator.calculate(phoneLog));
    }

    @Test
    public void testInvalidPhoneNumber() {
        String phoneLog = "15612,18-11-2021 19:09:55,18-11-2021 22:11:22";
        assertEquals("Expected zero for invalid phone number format", BigDecimal.valueOf(0).setScale(2, RoundingMode.HALF_UP), calculator.calculate(phoneLog));
    }

    @Test
    public void testInvalidDateFormat() {
        String phoneLog = "420607607607,18-11-2021 25:10:15,18-11-2021 18:12:57";
        // Test for invalid date format, should return BigDecimal.ZERO
        assertEquals("Expected zero for invalid date format", BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP), calculator.calculate(phoneLog));
    }
}
