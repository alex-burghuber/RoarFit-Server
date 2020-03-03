import helper.EntityManagerHelper;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import repositories.MemberRepository;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import static utils.Constants.BASE_URI;

public class Main {

    private static Logger LOG = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {
        // reset the database if the reset argument was given
        if (args.length > 0) {
            String arg = args[0];
            if (arg.equals("reset")) {
                LOG.info("Resetting database...");

                // set persistence.xml schema-generation to drop-and-create
                Map<String, String> properties = new HashMap<>();
                properties.put("javax.persistence.schema-generation.database.action", "drop-and-create");
                EntityManagerHelper.setProperties(properties);

                // fill the database with templates and placeholders
                MemberRepository.getInstance().fillDatabase();
            }
        }

        ResourceConfig rc = new ResourceConfig().packages("services", "filters");
        HttpServer server = GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);

        // activate error logging in the console
        Logger l = Logger.getLogger("org.glassfish.grizzly.http.server.HttpHandler");
        l.setLevel(Level.FINE);
        l.setUseParentHandlers(false);
        ConsoleHandler ch = new ConsoleHandler();
        ch.setLevel(Level.FINE);
        l.addHandler(ch);

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
