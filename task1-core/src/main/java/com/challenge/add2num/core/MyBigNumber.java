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
        final int len1 = stn1.length();
        final int len2 = stn2.length();
        final int totalSteps = Math.max(len1, len2);
        final char[] digits1 = stn1.toCharArray();
        final char[] digits2 = stn2.toCharArray();
        final StringBuilder reversed = new StringBuilder(totalSteps + 1);
        final List<AdditionStep> steps = new ArrayList<AdditionStep>(totalSteps);
        final boolean notifyListener = listener != null;

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
            index1 = len1 - 1 - offset;
            index2 = len2 - 1 - offset;
            digit1 = index1 >= 0 ? digits1[index1] - '0' : 0;
            digit2 = index2 >= 0 ? digits2[index2] - '0' : 0;
            carryIn = carry;
            total = digit1 + digit2 + carryIn;
            resultDigit = total % 10;
            carry = total / 10;
            reversed.append((char) ('0' + resultDigit));

            step = new AdditionStep(offset + 1, totalSteps, digit1, digit2, carryIn, total, resultDigit, carry);
            steps.add(step);
            writeLog(step);
            if (notifyListener) {
                listener.onStep(step);
            }
        }

        final Integer finalCarry = carry == 0 ? null : carry;
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