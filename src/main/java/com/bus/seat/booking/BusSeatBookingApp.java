package com.bus.seat.booking;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public class BusSeatBookingApp {

    private static final int PORT = 8080;

    public static void main(String[] args) throws IOException {

        // --- HTTP server ---
        final HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);

        // Use a thread pool so concurrent requests don't block each other
        server.setExecutor(Executors.newFixedThreadPool(10));
        
        server.start();
    }
}