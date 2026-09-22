package com.challenge.add2num.core;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public final class MyBigNumber {
    public interface StepListener {
        void onStep(AdditionStep step);
    }

    private final PrintStream log;

    public MyBigNumber() {
        this(null);
    }

    public MyBigNumber(PrintStream log) {
        this.log = log;
    }

    public String sum(String stn1, String stn2) {
        return calculate(stn1, stn2, null).getValue();
    }

    public String sum(String stn1, String stn2, StepListener listener) {
        return calculate(stn1, stn2, listener).getValue();
    }

    public AdditionResult calculate(String stn1, String stn2, StepListener listener) {
        int length1 = stn1.length();
        int length2 = stn2.length();
        int totalSteps = Math.max(length1, length2);
        StringBuilder reversed = new StringBuilder(totalSteps + 1);
        List<AdditionStep> steps = new ArrayList<AdditionStep>(totalSteps);
        int carry = 0;
        int offset;
        int index1;
        int index2;
        int digit1;
        int digit2;
        int carryIn;
        int total;
        int resultDigit;
        AdditionStep step;

        for (offset = 0; offset < totalSteps; offset++) {
            index1 = length1 - 1 - offset;
            index2 = length2 - 1 - offset;
            digit1 = index1 >= 0 ? stn1.charAt(index1) - '0' : 0;
            digit2 = index2 >= 0 ? stn2.charAt(index2) - '0' : 0;
            carryIn = carry;
            total = digit1 + digit2 + carryIn;
            resultDigit = total % 10;
            carry = total / 10;
            reversed.append((char) ('0' + resultDigit));

            step = new AdditionStep(offset + 1, totalSteps, digit1, digit2,
                    carryIn, total, resultDigit, carry);
            steps.add(step);
            writeLog(step);
            if (listener != null) {
                listener.onStep(step);
            }
        }

        Integer finalCarry = carry == 0 ? null : carry;
        if (carry != 0) {
            reversed.append((char) ('0' + carry));
            if (log != null) {
                log.println("final carry: " + carry);
            }
        }
        return new AdditionResult(reversed.reverse().toString(), steps, finalCarry);
    }

    private void writeLog(AdditionStep step) {
        if (log != null) {
            log.println("step " + step.getPosition() + ": "
                    + step.getDigit1() + " + " + step.getDigit2()
                    + " + carry = " + step.getTotal()
                    + ", digit = " + step.getResultDigit()
                    + ", next carry = " + step.getCarryOut());
        }
    }
}