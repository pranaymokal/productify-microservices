package com.microservices.order.order_service.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class MdcFilterTest {

    private MdcFilter mdcFilter;

    @BeforeEach
    void setUp() {
        mdcFilter = new MdcFilter();
    }

    @Test
    void testMdcFilterGeneratesCorrelationIdAndCleansUp() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/order-service/v1/orders");
        MockHttpServletResponse response = new MockHttpServletResponse();

        AtomicReference<String> capturedMdcId = new AtomicReference<>();

        FilterChain chain = (req, res) -> {
            capturedMdcId.set(MDC.get(MdcFilter.CORRELATION_ID_KEY));
        };

        mdcFilter.doFilterInternal(request, response, chain);

        assertNotNull(capturedMdcId.get());
        assertEquals(capturedMdcId.get(), response.getHeader(MdcFilter.CORRELATION_ID_HEADER));
        // MDC must be cleared after filter execution
        assertNull(MDC.get(MdcFilter.CORRELATION_ID_KEY));
    }

    @Test
    void testMdcFilterPreservesCorrelationIdAndUser() throws ServletException, IOException {
        String existingCorrId = "order-corr-999";
        String testUser = "pranay";

        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/order-service/v1/orders");
        request.addHeader(MdcFilter.CORRELATION_ID_HEADER, existingCorrId);
        request.addHeader(MdcFilter.USER_ID_HEADER, testUser);

        MockHttpServletResponse response = new MockHttpServletResponse();

        AtomicReference<String> capturedMdcId = new AtomicReference<>();
        AtomicReference<String> capturedUserId = new AtomicReference<>();

        FilterChain chain = (req, res) -> {
            capturedMdcId.set(MDC.get(MdcFilter.CORRELATION_ID_KEY));
            capturedUserId.set(MDC.get(MdcFilter.USER_ID_KEY));
        };

        mdcFilter.doFilterInternal(request, response, chain);

        assertEquals(existingCorrId, capturedMdcId.get());
        assertEquals(testUser, capturedUserId.get());
        assertEquals(existingCorrId, response.getHeader(MdcFilter.CORRELATION_ID_HEADER));
        assertNull(MDC.get(MdcFilter.CORRELATION_ID_KEY));
    }
}
