package com.challenge.add2num.core;

public final class AdditionStep {
    private final int position;
    private final int totalSteps;
    private final int digit1;
    private final int digit2;
    private final int carryIn;
    private final int total;
    private final int resultDigit;
    private final int carryOut;

    public AdditionStep(int position, int totalSteps, int digit1, int digit2,
                        int carryIn, int total, int resultDigit, int carryOut) {
        this.position = position;
        this.totalSteps = totalSteps;
        this.digit1 = digit1;
        this.digit2 = digit2;
        this.carryIn = carryIn;
        this.total = total;
        this.resultDigit = resultDigit;
        this.carryOut = carryOut;
    }

    public int getPosition() { return position; }
    public int getTotalSteps() { return totalSteps; }
    public int getDigit1() { return digit1; }
    public int getDigit2() { return digit2; }
    public int getCarryIn() { return carryIn; }
    public int getTotal() { return total; }
    public int getResultDigit() { return resultDigit; }
    public int getCarryOut() { return carryOut; }
}
