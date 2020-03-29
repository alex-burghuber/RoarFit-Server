package repositories;

import data.dto.PersonalExerciseDTO;
import data.dto.WorkoutExerciseDTO;
import data.entities.*;
import helper.EntityManagerHelper;
import helper.JwtHelper;
import helper.TimeFormatHelper;
import org.bouncycastle.util.encoders.Hex;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import javax.persistence.EntityManager;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static utils.Constants.VM_MEDIA_URI;

public class MemberRepository {

    private EntityManager em;
    private JwtHelper jwtHelper;

    private static MemberRepository repository;

    private MemberRepository() {
        em = EntityManagerHelper.getInstance();
        jwtHelper = new JwtHelper();
    }

    public static MemberRepository getInstance() {
        if (repository == null) {
            repository = new MemberRepository();
        }
        return repository;
    }

    public Response login(String username, String password) {
        // check if a member with this username exists
        List<StudioMember> results = em.createQuery("SELECT m FROM StudioMember m WHERE m.username = :username", StudioMember.class)
                .setParameter("username", username)
                .getResultList();

        if (results.size() != 0) {
            StudioMember member = results.get(0);

            // check if the password is correct
            try {
                MessageDigest md = MessageDigest.getInstance("SHA-256");
                md.update(Hex.decode(member.getSalt()));
                byte[] hash = md.digest(password.getBytes(StandardCharsets.UTF_8));

                if (new String(Hex.encode(hash)).equals(member.getPassword())) {
                    String jwt = jwtHelper.create(member.getId());
                    JSONObject json = new JSONObject()
                            .put("token", jwt);
                    return Response.ok(json.toString()).build();
                }
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }
        return Response.status(Response.Status.UNAUTHORIZED).build();
    }

    public Response getUser(String jwt) {
        StudioMember member = getMemberFromJwt(jwt);
        return Response.ok(member.toJson().toString()).build();
    }

    public Response getWorkoutPlans(String jwt) {
        StudioMember member = getMemberFromJwt(jwt);

        JSONArray plansJA = new JSONArray();
        for (WorkoutPlan plan : member.getWorkoutPlans()) {
            if (!plan.isArchived()) {
                JSONObject planJO = plan.toJson();

                JSONArray workoutsJA = new JSONArray();
                for (Workout workout : plan.getWorkouts()) {
                    JSONObject workoutJO = workout.toJson();

                    JSONArray specificationsJA = new JSONArray();
                    for (ExerciseSpecification specification : workout.getSpecifications()) {
                        specificationsJA.put(specification.toJson());
                    }
                    workoutJO.put("specifications", specificationsJA);
                    workoutsJA.put(workoutJO);
                }
                planJO.put("workouts", workoutsJA);
                plansJA.put(planJO);
            }
        }
        return Response.ok(plansJA.toString()).build();
    }

    public Response getEquipment(String jwt) {
        // for jwt validation
        getMemberFromJwt(jwt);

        String query = "SELECT DISTINCT t.equipment FROM ExerciseTemplate t where t.equipment != null";
        List<String> equipment = em.createQuery(query, String.class)
                .getResultList();

        return Response.ok(equipment).build();
    }

    public Response getExerciseTemplates(String jwt, String equipment) {
        // for jwt validation
        getMemberFromJwt(jwt);

        String query = "SELECT t FROM ExerciseTemplate t " +
                "WHERE LOWER(t.equipment) = LOWER(:equipment) " +
                "AND t.id != ALL (" +
                "SELECT s.exercise.template.id FROM ExerciseSpecification s" +
                ")";
        List<ExerciseTemplate> templates = em.createQuery(query, ExerciseTemplate.class)
                .setParameter("equipment", equipment)
                .getResultList();

        JSONArray templatesJA = new JSONArray();
        for (ExerciseTemplate template : templates) {
            templatesJA.put(template.toJson());
        }
        return Response.ok(templatesJA.toString()).build();
    }


    public Response addPersonalExercise(String jwt, PersonalExerciseDTO exerciseDTO) {
        StudioMember member = getMemberFromJwt(jwt);

        // get template with query to check if it doesn't belong to a exercise of a specification
        String query = "SELECT t FROM ExerciseTemplate t WHERE t.id = :templateId " +
                "AND t.id != ANY (SELECT e.template.id FROM Exercise e JOIN ExerciseSpecification s ON s.exercise.id = e.id)";
        List<ExerciseTemplate> templates = em.createQuery(query, ExerciseTemplate.class)
                .setParameter("templateId", exerciseDTO.getTemplateId())
                .getResultList();

        if (templates.size() == 1) {
            ExerciseTemplate template = templates.get(0);

            SimpleDateFormat formatter = TimeFormatHelper.getInstance().formatter;
            try {
                Date time = formatter.parse(exerciseDTO.getTime());

                Exercise exercise = new Exercise(
                        template, time, exerciseDTO.getSets(),
                        exerciseDTO.getReps(), exerciseDTO.getWeight(), exerciseDTO.getCompletedDate()
                );

                em.getTransaction().begin();
                member.getPersonalExercises().add(exercise);
                em.getTransaction().commit();

                return Response.ok().build();
            } catch (ParseException e) {
                System.out.println(exerciseDTO.getTime() + " could not be parsed");
            }
        }

        return Response.status(Response.Status.BAD_REQUEST).build();
    }

    public Response addWorkoutExercise(String jwt, WorkoutExerciseDTO exerciseDTO) {
        StudioMember member = getMemberFromJwt(jwt);

        // get exercise with query to check if it belongs to this user
        String query = "SELECT e FROM Exercise e " +
                "JOIN ExerciseSpecification s ON s.exercise = e " +
                "JOIN Workout w JOIN WorkoutPlan p JOIN StudioMember m " +
                "WHERE m = :member AND e.id = :exerciseId " +
                "AND s MEMBER OF w.specifications " +
                "AND w MEMBER OF p.workouts " +
                "AND p MEMBER OF m.workoutPlans";
        List<Exercise> exercises = em.createQuery(query, Exercise.class)
                .setParameter("member", member)
                .setParameter("exerciseId", exerciseDTO.getExerciseId())
                .getResultList();

        if (exercises.size() == 1) {
            Exercise exercise = exercises.get(0);

            if (exercise.getCompletedDate() == null) {
                SimpleDateFormat formatter = TimeFormatHelper.getInstance().formatter;
                try {
                    Date time = formatter.parse(exerciseDTO.getTime());

                    em.getTransaction().begin();
                    exercise.setTime(time);
                    exercise.setSets(exerciseDTO.getSets());
                    exercise.setReps(exerciseDTO.getReps());
                    exercise.setWeight(exerciseDTO.getWeight());
                    exercise.setCompletedDate(exerciseDTO.getCompletedDate());
                    em.getTransaction().commit();

                    return Response.ok().build();
                } catch (ParseException e) {
                    System.out.println(exerciseDTO.getTime() + " could not be parsed");
                }
            } else {
                return Response.status(Response.Status.CONFLICT).build();
            }
        }

        return Response.status(Response.Status.BAD_REQUEST).build();
    }

    public Response getExerciseHistory(String jwt, int count) {
        StudioMember member = getMemberFromJwt(jwt);

        // get exercises of studio member
        String query = "SELECT e FROM Exercise e " +
                "WHERE e IN (" +
                "SELECT e FROM Exercise e JOIN StudioMember m ON m.id = :memberId " +
                "WHERE e MEMBER OF m.personalExercises) " +
                "OR e IN (" +
                "SELECT e FROM Exercise e " +
                "JOIN ExerciseSpecification s ON s.exercise = e " +
                "JOIN Workout w " +
                "JOIN WorkoutPlan p " +
                "JOIN StudioMember m ON m.id = :memberId " +
                "WHERE s MEMBER OF w.specifications " +
                "AND w MEMBER OF p.workouts " +
                "AND p MEMBER OF m.workoutPlans " +
                "AND e.completedDate != null) ORDER BY e.completedDate DESC";
        List<Exercise> exercises = em.createQuery(query, Exercise.class)
                .setParameter("memberId", member.getId())
                .setFirstResult(count * 15)
                .setMaxResults(15)
                .getResultList();

        JSONArray exerciseJA = new JSONArray();
        for (Exercise exercise : exercises) {
            exerciseJA.put(exercise.toJson());
        }

        return Response.ok(exerciseJA.toString()).build();
    }

    /* Part of Medt-Android Project */

    public Response getExercisesOfMonth(String jwt, Date date) {
        StudioMember member = getMemberFromJwt(jwt);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        calendar.set(Calendar.DAY_OF_MONTH, 1);
        Date monthStart = calendar.getTime();

        int monthMax = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        calendar.set(Calendar.DAY_OF_MONTH, monthMax);
        Date monthEnd = calendar.getTime();

        System.out.println("MonthStart: " + monthStart + "MonthEnd: " + monthEnd);

        String query = "SELECT e FROM Exercise e " +
                "WHERE e IN (" +
                "SELECT e FROM Exercise e JOIN StudioMember m ON m.id = :memberId " +
                "WHERE e MEMBER OF m.personalExercises) " +
                "OR e IN (" +
                "SELECT e FROM Exercise e " +
                "JOIN ExerciseSpecification s ON s.exercise = e " +
                "JOIN Workout w " +
                "JOIN WorkoutPlan p " +
                "JOIN StudioMember m ON m.id = :memberId " +
                "WHERE s MEMBER OF w.specifications " +
                "AND w MEMBER OF p.workouts " +
                "AND p MEMBER OF m.workoutPlans " +
                "AND e.completedDate != null)";
        List<Exercise> exercises = em.createQuery(query, Exercise.class)
                .setParameter("memberId", member.getId())
                .getResultList();

        exercises = exercises.stream()
                .filter(exercise -> monthStart.compareTo(exercise.getCompletedDate()) < 0)
                .filter(exercise -> monthEnd.compareTo(exercise.getCompletedDate()) > 0)
                .collect(Collectors.toList());

        JSONArray exerciseJA = new JSONArray();
        for (Exercise exercise : exercises) {
            exerciseJA.put(exercise.toJson());
        }

        return Response.ok(exerciseJA.toString()).build();
    }

    private StudioMember getMemberFromJwt(String jwt) {
        long id = jwtHelper.getUserId(jwt);
        StudioMember member = em.find(StudioMember.class, id);
        if (member == null) {
            throw new WebApplicationException(Response.Status.UNAUTHORIZED);
        }
        return member;
    }

    public void fillDatabase() {
        // load templates
        URL url = Thread.currentThread()
                .getContextClassLoader()
                .getResource("templates/templates.json");

        if (url != null) {
            try (InputStream inputStream = url.openStream()) {
                JSONArray jsonArray = new JSONArray(new JSONTokener(inputStream));
                System.out.println(jsonArray.toString());
                for (int i = 0; i < jsonArray.length(); ++i) {
                    JSONObject json = jsonArray.getJSONObject(i);

                    JSONArray bodyPartsJA = json.getJSONArray("bodyParts");
                    int length = bodyPartsJA.length();
                    String[] bodyParts = new String[length];
                    for (int j = 0; j < length; ++j) {
                        bodyParts[j] = bodyPartsJA.getString(j);
                    }
                    ExerciseTemplate template = new ExerciseTemplate(json.getString("name"), bodyParts);

                    if (!json.isNull("equipment")) {
                        template.setEquipment(json.getString("equipment"));
                    }

                    if (!json.isNull("desc")) {
                        template.setDescription(json.getString("desc"));
                    }

                    em.getTransaction().begin();
                    em.persist(template);
                    em.getTransaction().commit();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            throw new RuntimeException("Exercise templates file not found");
        }

        // create members & trainer
        StudioMember member1 = new StudioMember("Alex123", "123ALEXtest", "Alex", "Burg");
        StudioMember member2 = new StudioMember("Pfeff", "PfeffPwd", "Mr", "Pepper");
        StudioMember member3 = new StudioMember("GhostMember", "spookyPwd", "TheStudioElite", "BornToCompete");

        Trainer trainer1 = new Trainer("Trainer1", "trainPwd1", "Genericus", "Trainerus");
        trainer1.getClients().add(member1);
        trainer1.getClients().add(member2);

        Trainer trainer2 = new Trainer("Trainer2", "trainPwd2", "Ilike", "Trains");
        trainer2.getClients().add(member3);

        em.getTransaction().begin();
        em.persist(trainer1);
        em.persist(trainer2);
        em.getTransaction().commit();

        List<ExerciseTemplate> templates
                = em.createQuery("SELECT t FROM ExerciseTemplate t", ExerciseTemplate.class).getResultList();

        WorkoutPlan workoutPlan1 = createWorkoutPlan();
        WorkoutPlan workoutPlan2 = createWorkoutPlan();

        em.getTransaction().begin();
        member1.getWorkoutPlans().add(workoutPlan1);
        member2.getWorkoutPlans().add(workoutPlan2);
        em.getTransaction().commit();
    }

    private WorkoutPlan createWorkoutPlan() {
        // warmup/cooldown
        Date warmup1, cooldown1;
        try {
            SimpleDateFormat formatter = TimeFormatHelper.getInstance().formatter;
            warmup1 = formatter.parse("10:00");
            cooldown1 = formatter.parse("20:00");
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        WorkoutPlan workoutPlan1 = new WorkoutPlan("Starting Strength", warmup1, cooldown1);

        // create workouts
        Workout workout1 = new Workout(1);

        ExerciseTemplate template1 = new ExerciseTemplate("Kniebeuge", "Beine");
        template1.setDescription("Die Kniebeuge ist eine sportliche Übung zur Kräftigung der Muskulatur, insbesondere der Oberschenkelmuskulatur.");
        template1.setVideoUrl(VM_MEDIA_URI + "tea.mp4");
        Exercise exercise1 = new Exercise(template1);
        ExerciseSpecification specification1 = new ExerciseSpecification(exercise1, 3, 5, 45f);
        specification1.setInfo("Never skip leg day.");

        ExerciseTemplate template2 = new ExerciseTemplate("Bankdrücken (20)", "Brust");
        template2.setEquipment("Langhantel");
        Exercise exercise2 = new Exercise(template2);
        ExerciseSpecification specification2 = new ExerciseSpecification(exercise2, 3, 5, 32.5f);

        ExerciseTemplate template3 = new ExerciseTemplate("KH - Rudern im Stütz + Superman", "Rücken");
        template3.setEquipment("Kurzhanteln");
        Exercise exercise3 = new Exercise(template3);
        ExerciseSpecification specification3 = new ExerciseSpecification(exercise3, 3, 15, 10f);

        workout1.getSpecifications().addAll(Arrays.asList(specification1, specification2, specification3));
        workoutPlan1.getWorkouts().add(workout1);

        Workout workout2 = new Workout(2);

        ExerciseTemplate template4 = new ExerciseTemplate("Military Press", "Schulter");
        template4.setEquipment("Langhantel");
        Exercise exercise4 = new Exercise(template4);
        ExerciseSpecification specification4 = new ExerciseSpecification(exercise4, 3, 5, 22.5f);

        ExerciseTemplate template5 = new ExerciseTemplate("Planksaw", "Bauch");
        Exercise exercise5 = new Exercise(template5);
        ExerciseSpecification specification5 = new ExerciseSpecification(exercise5, 3, 10);

        workout2.getSpecifications().addAll(Arrays.asList(specification4, specification5));
        workoutPlan1.getWorkouts().add(workout2);

        return workoutPlan1;
    }
}
