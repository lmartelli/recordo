package com.cariochi.recordo.core;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.*;

import java.util.List;
import java.util.ServiceLoader.Provider;

import static com.cariochi.recordo.core.utils.Exceptions.tryAccept;
import static java.util.ServiceLoader.load;
import static java.util.stream.Collectors.toList;

@Slf4j
public class RecordoExtension implements BeforeAllCallback, BeforeEachCallback, AfterEachCallback, AfterAllCallback, ParameterResolver {

    private final List<Extension> handlers;

    @SneakyThrows
    public RecordoExtension() {
        handlers = load(Extension.class).stream().map(Provider::get).collect(toList());
    }

    @Override
    public void beforeAll(ExtensionContext context) {
        handlers.stream()
                .filter(i -> BeforeAllCallback.class.isAssignableFrom(i.getClass()))
                .map(BeforeAllCallback.class::cast)
                .forEach(tryAccept(processor -> processor.beforeAll(context)));
    }

    @Override
    public void beforeEach(ExtensionContext context) {
        handlers.stream()
                .filter(i -> BeforeEachCallback.class.isAssignableFrom(i.getClass()))
                .map(BeforeEachCallback.class::cast)
                .forEach(tryAccept(processor -> processor.beforeEach(context)));
    }

    @Override
    public void afterEach(ExtensionContext context) {
        handlers.stream()
                .filter(i -> AfterEachCallback.class.isAssignableFrom(i.getClass()))
                .map(AfterEachCallback.class::cast)
                .forEach(tryAccept(processor -> processor.afterEach(context)));
    }

    @Override
    public void afterAll(ExtensionContext context) {
        handlers.stream()
                .filter(i -> AfterAllCallback.class.isAssignableFrom(i.getClass()))
                .map(AfterAllCallback.class::cast)
                .forEach(tryAccept(processor -> processor.afterAll(context)));
    }

    @Override
    public boolean supportsParameter(ParameterContext parameter,
                                     ExtensionContext extension) throws ParameterResolutionException {
        return handlers.stream()
                .filter(i -> ParameterResolver.class.isAssignableFrom(i.getClass()))
                .map(ParameterResolver.class::cast)
                .anyMatch(r -> r.supportsParameter(parameter, extension));
    }

    @Override
    public Object resolveParameter(ParameterContext parameter,
                                   ExtensionContext extension) throws ParameterResolutionException {
        return handlers.stream()
                .filter(i -> ParameterResolver.class.isAssignableFrom(i.getClass()))
                .map(ParameterResolver.class::cast)
                .filter(r -> r.supportsParameter(parameter, extension))
                .findFirst()
                .map(r -> r.resolveParameter(parameter, extension))
                .orElse(null);
    }

}
