package com.pationImage.com.reg; // <-- use your package name

import com.sun.net.httpserver.HttpServer;
import java.io.*;
import java.net.*;
import java.nio.file.*;

public class SimplePdfServer {

    private static boolean isRunning = false;

    public static void startServer() {
        if (isRunning) return;

        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(8081), 0);

            server.createContext("/download", exchange -> {
                String query = exchange.getRequestURI().getQuery();
                String userId = (query != null && query.contains("="))
                        ? query.split("=")[1]
                        : "unknown";

                File file = new File("Blood_Report_" + userId + ".pdf");

                if (!file.exists()) {
                    String msg = "<h2>❌ Report not found for User ID: " + userId + "</h2>";
                    exchange.getResponseHeaders().add("Content-Type", "text/html");
                    exchange.sendResponseHeaders(404, msg.length());
                    exchange.getResponseBody().write(msg.getBytes());
                } else {
                    byte[] bytes = Files.readAllBytes(file.toPath());
                    exchange.getResponseHeaders().add("Content-Type", "application/pdf");
                    exchange.getResponseHeaders().add("Content-Disposition",
                            "inline; filename=" + file.getName());
                    exchange.sendResponseHeaders(200, bytes.length);
                    exchange.getResponseBody().write(bytes);
                }
                exchange.close();
            });

            server.start();
            isRunning = true;
            System.out.println("✅ PDF Server started on http://localhost:8081/download?userId=123");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getLocalIp() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            return "localhost";
        }
    }
}

