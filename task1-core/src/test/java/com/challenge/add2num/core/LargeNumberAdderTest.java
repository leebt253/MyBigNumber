package com.challenge.add2num.core;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MyBigNumberTest {
    @Test
    void addsNumbersWithDifferentLengths() {
        AdditionResult result = new MyBigNumber().calculate("123", "9876", null);
        assertEquals("9999", result.getValue());
        assertEquals(4, result.getSteps().size());
        assertNull(result.getFinalCarry());
    }

    @Test
    void preservesArbitraryLengthPrecision() {
        MyBigNumber adder = new MyBigNumber();
        AdditionResult result = adder.calculate("999999999999999999999999999999", "1", null);
        assertEquals("1000000000000000000000000000000", result.getValue());
        assertEquals(Integer.valueOf(1), result.getFinalCarry());
        assertEquals("1000000000000000000000000000000",
            adder.sum("999999999999999999999999999999", "1"));
    }

    @Test
    void reportsEveryCalculationStep() {
        AtomicInteger callbacks = new AtomicInteger();
        AdditionResult result = new MyBigNumber().calculate("95", "7",
                step -> callbacks.incrementAndGet());
        assertEquals("102", result.getValue());
        assertEquals(2, callbacks.get());
        assertEquals(1, result.getSteps().get(0).getCarryOut());
        assertEquals(1, result.getSteps().get(1).getCarryOut());
    }

    @Test
    void writesEachStepAndFinalCarryToTheLog() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        new MyBigNumber(new PrintStream(output)).sum("999", "2");
        String log = output.toString();
        assertTrue(log.contains("step 1:"));
        assertTrue(log.contains("step 3:"));
        assertTrue(log.contains("final carry: 1"));
    }
}
