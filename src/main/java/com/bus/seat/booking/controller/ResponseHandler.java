package com.bus.seat.booking.controller;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class ResponseHandler {

    private final Gson gson = new Gson();

    /**
     * Send Json response with given status code and body
     *
     * @param httpExchange
     * @param statusCode
     * @param body
     * @throws IOException
     */
    public void sendResponse(final HttpExchange httpExchange, final int statusCode,
    final Object body) throws IOException {

        final String jsonString = gson.toJson(body);
        final byte[] bytes = jsonString.getBytes(StandardCharsets.UTF_8);

        httpExchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        httpExchange.sendResponseHeaders(statusCode, bytes.length);

        try (final OutputStream os = httpExchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    /**
     * Send error Json response
     *
     * @param httpExchange
     * @param statusCode
     * @param message
     * @throws IOException
     */
    public void sendErrorResponse(final HttpExchange httpExchange, final int statusCode,
    final String message) throws IOException {

        final Map<String, String> errorMap = new HashMap<>();
        errorMap.put("error", message);

        this.sendResponse(httpExchange, statusCode, errorMap);
    }

    /**
     * Read Request body String from HttpExchange
     *
     * @param httpExchange
     * @return Request body String
     */
    public String readRequestBody(final HttpExchange httpExchange) {
        return new BufferedReader(new InputStreamReader(httpExchange.getRequestBody(), StandardCharsets.UTF_8))
                .lines().collect(Collectors.joining(System.lineSeparator()));
    }
}
