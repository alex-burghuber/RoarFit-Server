package data.dto.addworkoutplan;

import java.util.List;

public class WorkoutDTO {

    private int day;
    private int week = 0;
    private String info;
    private List<ExerciseSpecificationDTO> specifications;

    public WorkoutDTO() {
    }

    public WorkoutDTO(int day, int week, String info, List<ExerciseSpecificationDTO> specifications) {
        this.day = day;
        this.week = week;
        this.info = info;
        this.specifications = specifications;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getWeek() {
        return week;
    }

    public void setWeek(int week) {
        this.week = week;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public List<ExerciseSpecificationDTO> getSpecifications() {
        return specifications;
    }

    public void setSpecifications(List<ExerciseSpecificationDTO> specifications) {
        this.specifications = specifications;
    }
}
