package com.microservices.order.order_service.config;

import feign.RequestTemplate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class FeignMdcInterceptorTest {

    private FeignMdcInterceptor interceptor;

    @BeforeEach
    void setUp() {
        interceptor = new FeignMdcInterceptor();
        MDC.clear();
    }

    @AfterEach
    void tearDown() {
        MDC.clear();
    }

    @Test
    void testAppliesCorrelationIdFromMdcToFeignRequest() {
        String testCorrId = "feign-corr-1234";
        MDC.put(FeignMdcInterceptor.CORRELATION_ID_KEY, testCorrId);

        RequestTemplate template = new RequestTemplate();
        interceptor.apply(template);

        Collection<String> headers = template.headers().get(FeignMdcInterceptor.CORRELATION_ID_HEADER);
        assertNotNull(headers);
        assertTrue(headers.contains(testCorrId));
    }
}
