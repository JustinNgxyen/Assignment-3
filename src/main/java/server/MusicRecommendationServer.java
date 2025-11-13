package server;

import config.config;
import model.APIClient;
import config.config;
import org.apache.hc.core5.http.ParseException;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Multi-threaded server that handles multiple client connections
 * Demonstrates Server Socket programming and Thread Pool
 */
public class MusicRecommendationServer {
    private final int port;
    private final APIClient apiClient;
    private final ExecutorService threadPool;
    private volatile boolean running;

    public MusicRecommendationServer(int port) {
        this.port = port;
        this.apiClient = new APIClient();
        this.threadPool = Executors.newFixedThreadPool(10); // Pool of 10 threads
        this.running = false;
    }

    /**
     * Start the server and listen for client connections
     */
    public void start() throws IOException, ParseException {
        // Authenticate with Spotify API
        System.out.println("Authenticating with Spotify API...");
        apiClient.authenticate();

        Thread refresher = new Thread(() -> {
            while (running) {
                try {
                    Thread.sleep(55 * 60 * 1000); // refresh every 55 minutes
                    System.out.println("🔄 Refreshing Spotify token...");
                    apiClient.authenticate();
                } catch (Exception e) {
                    System.err.println("Failed to refresh token: " + e.getMessage());
                }
            }
        });
        refresher.setDaemon(true); // ✅ Daemon thread won’t block shutdown
        refresher.start();
        running = true;

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Music Recommendation Server started on port " + port);
            System.out.println("Waiting for client connections...");

            while (running) {
                // Accept incoming client connection
                Socket clientSocket = serverSocket.accept();

                // Handle client in separate thread from pool
                ClientHandler handler = new ClientHandler(clientSocket, apiClient);
                threadPool.execute(handler);
            }
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
            throw e;
        } finally {
            shutdown();
        }
    }

    /**
     * Shutdown the server gracefully
     */
    public void shutdown() {
        running = false;
        threadPool.shutdown();
        try {
            apiClient.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Server shutdown complete");
    }

    public static void main(String[] args) {
        MusicRecommendationServer server = new MusicRecommendationServer(config.SERVER_PORT);

        // Add shutdown hook for graceful termination
        Runtime.getRuntime().addShutdownHook(new Thread(server::shutdown));

        try {
            server.start();
        } catch (IOException e) {
            System.err.println("Failed to start server: " + e.getMessage());
            e.printStackTrace();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}