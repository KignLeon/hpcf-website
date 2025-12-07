package com.hpcf;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import static spark.Spark.port;
import static spark.Spark.post;
import static spark.Spark.staticFiles;

/**
 * File: Main.java Higher Praise Christian Fellowship — Website Server
 * ------------------------------------------------------ Purpose: Serves static
 * HPCF website files and handles form submissions such as contact forms or
 * prayer requests using the SparkJava framework.
 */
public class Main {

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);
    private static final Gson gson = new Gson();

    public static void main(String[] args) {

        // --- Server Configuration ---
        port(getAssignedPort());

        // Serve all static content from src/main/resources/public
        staticFiles.location("/public");

        // --- ROUTES ---
        // Example: POST /contact - handle prayer/contact form submissions
        post("/contact", (req, res) -> {
            res.type("application/json");

            try {
                JsonObject body = gson.fromJson(req.body(), JsonObject.class);

                if (body == null || body.get("name") == null || body.get("email") == null) {
                    res.status(400);
                    return jsonError("Malformed request: name and email are required.");
                }

                String name = body.get("name").getAsString();
                String email = body.get("email").getAsString();
                String message = body.has("message") ? body.get("message").getAsString() : "(no message)";
                String type = body.has("type") ? body.get("type").getAsString() : "general";

                LOGGER.info("=========================================");
                LOGGER.info("===   NEW HPCF FORM SUBMISSION   ===");
                LOGGER.info("=========================================");
                LOGGER.info("Type: {}", type);
                LOGGER.info("Name: {}", name);
                LOGGER.info("Email: {}", email);
                LOGGER.info("Message: {}", message);
                LOGGER.info("=========================================");

                // Future: forward this info to email or database.
                return jsonSuccess("Your message has been received! Thank you, and God bless.");

            } catch (JsonSyntaxException e) {
                LOGGER.error("Invalid JSON input", e);
                res.status(400);
                return jsonError("Invalid data format.");
            } catch (Exception e) {
                LOGGER.error("Server error while handling contact form", e);
                res.status(500);
                return jsonError("An internal server error occurred.");
            }
        });

        LOGGER.info("HPCF server started. Access at http://localhost:{}", getAssignedPort());
    }

    // --- Utility Methods ---
    private static int getAssignedPort() {
        String port = System.getenv("PORT");
        return (port != null) ? Integer.parseInt(port) : 8080;
    }

    private static String jsonSuccess(String message) {
        return String.format("{\"status\":\"success\",\"message\":\"%s\"}", message);
    }

    private static String jsonError(String message) {
        return String.format("{\"status\":\"error\",\"message\":\"%s\"}", message);
    }
}
