package com.example.trx.support.filter;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class RequestServletWrapper extends HttpServletRequestWrapper {

    private final byte[] requestBody;

    public RequestServletWrapper(HttpServletRequest request) {
        super(request);
        try {
            this.requestBody = request.getInputStream().readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException("Failed to read request body", e);
        }
    }

    @Override
    public ServletInputStream getInputStream() {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(requestBody);
        return new ServletInputStream() {
            @Override
            public boolean isFinished() {
                return byteArrayInputStream.available() == 0;
            }

            @Override
            public boolean isReady() {
                return true;
            }

            @Override
            public void setReadListener(ReadListener readListener) {
                // Not used
            }

            @Override
            public int read() {
                return byteArrayInputStream.read();
            }
        };
    }

    // optionally expose requestBody as String
    public String getRequestBodyString() {
        return new String(requestBody, StandardCharsets.UTF_8);
    }
}
