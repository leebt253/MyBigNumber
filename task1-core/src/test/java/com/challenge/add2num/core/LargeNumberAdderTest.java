package com.challenge.add2num.core;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LargeNumberAdderTest {
    @Test
    void addsNumbersWithDifferentLengths() {
        AdditionResult result = new LargeNumberAdder().add("123", "9876");
        assertEquals("9999", result.getValue());
        assertEquals(4, result.getSteps().size());
        assertNull(result.getFinalCarry());
    }

    @Test
    void preservesArbitraryLengthPrecision() {
        AdditionResult result = new LargeNumberAdder().add("999999999999999999999999999999", "1");
        assertEquals("1000000000000000000000000000000", result.getValue());
        assertEquals(Integer.valueOf(1), result.getFinalCarry());
    }

    @Test
    void reportsEveryCalculationStep() {
        AtomicInteger callbacks = new AtomicInteger();
        AdditionResult result = new LargeNumberAdder().add("95", "7",
                step -> callbacks.incrementAndGet());
        assertEquals("102", result.getValue());
        assertEquals(2, callbacks.get());
        assertEquals(1, result.getSteps().get(0).getCarryOut());
        assertEquals(1, result.getSteps().get(1).getCarryOut());
    }

    @Test
    void writesEachStepAndFinalCarryToTheLog() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        new LargeNumberAdder(new PrintStream(output)).add("999", "2");
        String log = output.toString();
        assertTrue(log.contains("step 1:"));
        assertTrue(log.contains("step 3:"));
        assertTrue(log.contains("final carry: 1"));
    }
}
