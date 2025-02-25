package com.cariochi.recordo.mockserver.interceptors.okhttp;

import com.cariochi.recordo.mockserver.interceptors.MockServerInterceptor;
import com.cariochi.recordo.mockserver.interceptors.RecordoRequestHandler;
import com.cariochi.recordo.mockserver.model.MockRequest;
import com.cariochi.recordo.mockserver.model.MockResponse;
import lombok.SneakyThrows;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.List;

import static com.cariochi.reflecto.Reflecto.reflect;
import static java.util.stream.Collectors.toList;

public class OkMockServerInterceptor implements Interceptor, MockServerInterceptor {

    private final OkHttpMapper mapper = new OkHttpMapper();

    private RecordoRequestHandler handler;

    public static OkMockServerInterceptor attachTo(OkHttpClient httpClient) {
        final OkMockServerInterceptor interceptor = new OkMockServerInterceptor();
        final List<Interceptor> interceptors = httpClient.interceptors().stream()
                .filter(interceptor1 -> !(interceptor1 instanceof OkMockServerInterceptor))
                .collect(toList());
        interceptors.add(interceptor);
        reflect(httpClient).get("interceptors").setValue(interceptors);
        return interceptor;
    }

    @Override
    public void init(RecordoRequestHandler handler) {
        this.handler = handler;
    }

    @Override
    public okhttp3.Response intercept(Chain chain) throws IOException {
        final okhttp3.Request request = chain.request();
        final MockRequest recordoRequest = mapper.toRecordoRequest(request);
        final MockResponse response = handler.onRequest(recordoRequest)
                .orElseGet(() -> handler.onResponse(recordoRequest, proceed(request, chain)));
        return mapper.toOkHttpResponse(request, response);
    }

    @SneakyThrows
    private MockResponse proceed(Request request, Chain chain) {
        final Response response = chain.proceed(request);
        return mapper.toRecordoResponse(response);
    }
}
