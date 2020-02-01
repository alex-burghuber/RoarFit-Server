import java.net.URI;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;

public class Main {

    public static final String BASE_URI = "http://localhost:8080";

    public static void main(String[] args) {

        ResourceConfig rc = new ResourceConfig().packages("services", "filters");
        HttpServer server = GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);

        try {
            System.out.println("Server is listening at " + BASE_URI);
            System.out.println("Press enter to stop the server...");
            System.in.read();
            server.shutdown();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
