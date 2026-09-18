package com.challenge.add2num.core;

import java.util.Collections;
import java.util.List;

public final class AdditionResult {
    private final String value;
    private final List<AdditionStep> steps;
    private final Integer finalCarry;

    public AdditionResult(String value, List<AdditionStep> steps, Integer finalCarry) {
        this.value = value;
        this.steps = Collections.unmodifiableList(steps);
        this.finalCarry = finalCarry;
    }

    public String getValue() { return value; }
    public List<AdditionStep> getSteps() { return steps; }
    public Integer getFinalCarry() { return finalCarry; }
}
