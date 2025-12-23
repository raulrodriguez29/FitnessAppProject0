package org.fitnesstracker.repository.entities;

import java.util.Objects;

public class ExerciseEntity {
    private int exerciseId;
    private String name;
    private String targetMuscle;
    private int caloriesPerMin;

    public ExerciseEntity(int exerciseId, String name, String targetMuscle, int caloriesPerMin) {
        this.name = name;
        this.exerciseId = exerciseId;
        this.targetMuscle = targetMuscle;
        this.caloriesPerMin = caloriesPerMin;
    }

    public ExerciseEntity() {
    }

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
        return "ExerciseEntity{" +
                "exerciseId=" + exerciseId +
                ", name='" + name + '\'' +
                ", targetMuscle='" + targetMuscle + '\'' +
                ", caloriesPerMin=" + caloriesPerMin +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ExerciseEntity that = (ExerciseEntity) o;
        return exerciseId == that.exerciseId && caloriesPerMin == that.caloriesPerMin && Objects.equals(name, that.name) && Objects.equals(targetMuscle, that.targetMuscle);
    }

    @Override
    public int hashCode() {
        return Objects.hash(exerciseId, name, targetMuscle, caloriesPerMin);
    }
}
