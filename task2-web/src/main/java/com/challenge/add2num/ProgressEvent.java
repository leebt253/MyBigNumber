package com.challenge.add2num;

import com.challenge.add2num.core.AdditionStep;

public final class ProgressEvent {
    private final String type;
    private final int completed;
    private final int total;
    private final int percent;
    private final AdditionStep step;
    private final String result;
    private final Integer finalCarry;
    private final String error;

    private ProgressEvent(String type, int completed, int total, int percent,
                          AdditionStep step, String result, Integer finalCarry, String error) {
        this.type = type;
        this.completed = completed;
        this.total = total;
        this.percent = percent;
        this.step = step;
        this.result = result;
        this.finalCarry = finalCarry;
        this.error = error;
    }

    public static ProgressEvent step(AdditionStep step) {
        int percent = step.getTotalSteps() == 0 ? 100
                : step.getPosition() * 100 / step.getTotalSteps();
        return new ProgressEvent("step", step.getPosition(), step.getTotalSteps(),
                percent, step, null, null, null);
    }

    public static ProgressEvent complete(String result, Integer finalCarry, int total) {
        return new ProgressEvent("complete", total, total, 100,
                null, result, finalCarry, null);
    }

    public static ProgressEvent failure(String message) {
        return new ProgressEvent("error", 0, 0, 0, null, null, null, message);
    }

    public String getType() { return type; }
    public int getCompleted() { return completed; }
    public int getTotal() { return total; }
    public int getPercent() { return percent; }
    public AdditionStep getStep() { return step; }
    public String getResult() { return result; }
    public Integer getFinalCarry() { return finalCarry; }
    public String getError() { return error; }
}
