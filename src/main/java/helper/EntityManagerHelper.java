package helper;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.Map;

public class EntityManagerHelper {

    private static EntityManager em;
    private static Map<String, String> properties;
    private static final String PU_NAME = "RoarFitPU";

    public static EntityManager getInstance() {
        if (em == null) {
            EntityManagerFactory emf;
            if (properties != null) {
                emf = Persistence.createEntityManagerFactory(PU_NAME, properties);
            } else {
                emf = Persistence.createEntityManagerFactory(PU_NAME);
            }
            em = emf.createEntityManager();
        }
        return em;
    }

    public static void setProperties(Map<String, String> props) {
        properties = props;
    }
}
