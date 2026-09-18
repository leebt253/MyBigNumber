package com.challenge.add2num.core;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public final class LargeNumberAdder {
    public interface StepListener {
        void onStep(AdditionStep step);
    }

    private final PrintStream log;

    public LargeNumberAdder() {
        this(null);
    }

    public LargeNumberAdder(PrintStream log) {
        this.log = log;
    }

    public AdditionResult add(String first, String second) {
        return add(first, second, null);
    }

    public AdditionResult add(String first, String second, StepListener listener) {
        int totalSteps = Math.max(first.length(), second.length());
        StringBuilder reversed = new StringBuilder(totalSteps + 1);
        List<AdditionStep> steps = new ArrayList<AdditionStep>(totalSteps);
        int carry = 0;

        for (int offset = 0; offset < totalSteps; offset++) {
            int index1 = first.length() - 1 - offset;
            int index2 = second.length() - 1 - offset;
            int digit1 = index1 >= 0 ? first.charAt(index1) - '0' : 0;
            int digit2 = index2 >= 0 ? second.charAt(index2) - '0' : 0;
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
