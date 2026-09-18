package com.challenge.add2num;

import com.challenge.add2num.core.AdditionResult;
import com.challenge.add2num.core.MyBigNumber;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
@RequestMapping("/api/calculations")
public class CalculationController {
    private final Map<String, Calculation> calculations = new ConcurrentHashMap<String, Calculation>();
    private final ExecutorService executor = Executors.newCachedThreadPool();
    private final MyBigNumber adder = new MyBigNumber();

    @PostMapping
    public Map<String, String> create(@RequestBody CalculationRequest request) {
        String first = request == null ? null : request.getFirst();
        String second = request == null ? null : request.getSecond();
        if (!isDecimal(first) || !isDecimal(second)) {
            throw new IllegalArgumentException("Please enter non-negative decimal integers only.");
        }
        String id = UUID.randomUUID().toString();
        calculations.put(id, new Calculation(first.trim(), second.trim()));
        return Collections.singletonMap("id", id);
    }

    @GetMapping(value = "/{id}/progress", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter progress(@PathVariable String id) {
        Calculation calculation = calculations.get(id);
        if (calculation == null) {
            throw new IllegalArgumentException("Calculation was not found.");
        }

        SseEmitter emitter = new SseEmitter(0L);
        executor.submit(() -> calculate(id, calculation, emitter));
        return emitter;
    }

    private void calculate(String id, Calculation calculation, SseEmitter emitter) {
        try {
            AdditionResult result = adder.calculate(calculation.first, calculation.second,
                    step -> send(emitter, ProgressEvent.step(step)));
            send(emitter, ProgressEvent.complete(result.getValue(), result.getFinalCarry(),
                    result.getSteps().size()));
            emitter.complete();
        } catch (Exception exception) {
            try {
                send(emitter, ProgressEvent.failure(exception.getMessage()));
            } finally {
                emitter.completeWithError(exception);
            }
        } finally {
            calculations.remove(id);
        }
    }

    private void send(SseEmitter emitter, ProgressEvent event) {
        try {
            emitter.send(SseEmitter.event().name(event.getType()).data(event));
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to send progress update", exception);
        }
    }

    private boolean isDecimal(String value) {
        return value != null && value.trim().matches("[0-9]+");
    }

    private static final class Calculation {
        private final String first;
        private final String second;

        private Calculation(String first, String second) {
            this.first = first;
            this.second = second;
        }
    }
}
