package org.fitnesstracker.service.models;

import java.util.Objects;

public class Exercise {
    private int exerciseId;
    private String name;
    private String targetMuscle;
    private int caloriesPerMin;

    public int getExerciseId() {
        return exerciseId;
    }

    public void setExerciseId(int exerciseId) {
        this.exerciseId = exerciseId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTargetMuscle() {
        return targetMuscle;
    }

    public void setTargetMuscle(String targetMuscle) {
        this.targetMuscle = targetMuscle;
    }

    public int getCaloriesPerMin() {
        return caloriesPerMin;
    }

    public void setCaloriesPerMin(int caloriesPerMin) {
        this.caloriesPerMin = caloriesPerMin;
    }

    @Override
    public String toString() {
        return "Exercise{" +
                "exerciseId=" + exerciseId +
                ", name='" + name + '\'' +
                ", targetMuscle='" + targetMuscle + '\'' +
                ", caloriesPerMin=" + caloriesPerMin +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Exercise exercise = (Exercise) o;
        return exerciseId == exercise.exerciseId && caloriesPerMin == exercise.caloriesPerMin && Objects.equals(name, exercise.name) && Objects.equals(targetMuscle, exercise.targetMuscle);
    }

    @Override
    public int hashCode() {
        return Objects.hash(exerciseId, name, targetMuscle, caloriesPerMin);
    }
}
