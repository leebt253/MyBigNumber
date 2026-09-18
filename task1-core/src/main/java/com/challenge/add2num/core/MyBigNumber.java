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
        int totalSteps = Math.max(stn1.length(), stn2.length());
        StringBuilder reversed = new StringBuilder(totalSteps + 1);
        List<AdditionStep> steps = new ArrayList<AdditionStep>(totalSteps);
        int carry = 0;

        for (int offset = 0; offset < totalSteps; offset++) {
            int index1 = stn1.length() - 1 - offset;
            int index2 = stn2.length() - 1 - offset;
            int digit1 = index1 >= 0 ? stn1.charAt(index1) - '0' : 0;
            int digit2 = index2 >= 0 ? stn2.charAt(index2) - '0' : 0;
            int carryIn = carry;
            int total = digit1 + digit2 + carryIn;
            int resultDigit = total % 10;
            carry = total / 10;
            reversed.append((char) ('0' + resultDigit));

            AdditionStep step = new AdditionStep(offset + 1, totalSteps, digit1, digit2,
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