package com.cariochi.recordo.core.utils;

import com.cariochi.recordo.core.RecordoError;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static java.util.stream.Collectors.joining;
import static lombok.AccessLevel.PROTECTED;

@RequiredArgsConstructor(staticName = "of", access = PROTECTED)
public final class ExceptionsCollector implements AutoCloseable {

    private final Class<? extends Throwable> exceptionType;

    private final List<Throwable> exceptions = new ArrayList<>();

    public <T> Consumer<T> consuming(Consumer<T> consumer) {
        return t -> {
            try {
                consumer.accept(t);
            } catch (RuntimeException e) {
                if (exceptionType.isAssignableFrom(e.getClass())) {
                    exceptions.add(e);
                } else {
                    throw e;
                }
            }
        };
    }

    public Runnable running(Runnable runnable) {
        return () -> {
            try {
                runnable.run();
            } catch (RuntimeException e) {
                if (exceptionType.isAssignableFrom(e.getClass())) {
                    exceptions.add(e);
                } else {
                    throw e;
                }
            }
        };
    }

    public boolean hasExceptions() {
        return !exceptions.isEmpty();
    }

    public String getMessage() {
        return exceptions.stream()
                .map(Throwable::getMessage)
                .collect(joining("\n\n"));
    }

    @Override
    public void close() {
        if (hasExceptions()) {
            throw new RecordoError(getMessage());
        }
    }

}
