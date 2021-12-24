package ca.utoronto.utm.mcs;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public class App {
   static int PORT = 8000;

   public static void main(String[] args) throws IOException {
      HttpServer server = HttpServer.create(new InetSocketAddress("0.0.0.0", PORT), 0);
      server.createContext("/location", new Microservice("http://locationmicroservice:8000"));
      server.createContext("/user", new Microservice("http://usermicroservice:8000"));
      server.createContext("/trip", new Microservice("http://tripinfomicroservice:8000"));
      server.setExecutor(Executors.newCachedThreadPool());
      server.start();

      System.out.printf("Server started on port %d...\n", PORT);
   }
}
