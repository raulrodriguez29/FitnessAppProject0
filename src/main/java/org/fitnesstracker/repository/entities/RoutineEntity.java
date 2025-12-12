package org.fitnesstracker.repository.entities;

import org.fitnesstracker.service.models.RoutineExerciseDetails;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RoutineEntity {
    private int routineId;
    private int userId;
    private String routineName;
    private String difficultyLevel;

    private List<RoutineExerciseDetails> exercises = new ArrayList<>();

    public RoutineEntity(int routineId, int userId, String difficultyLevel, String routineName, List<RoutineExerciseDetails> exercises) {
        this.routineId = routineId;
        this.userId = userId;
        this.difficultyLevel = difficultyLevel;
        this.routineName = routineName;
        this.exercises = exercises;
    }

    public RoutineEntity() {
    }

    public int getRoutineId() {
        return routineId;
    }

    public void setRoutineId(int routineId) {
        this.routineId = routineId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getRoutineName() {
        return routineName;
    }

    public void setRoutineName(String routineName) {
        this.routineName = routineName;
    }

    public String getDifficultyLevel() {
        return difficultyLevel;
    }

    public void setDifficultyLevel(String difficultyLevel) {
        this.difficultyLevel = difficultyLevel;
    }

    public List<RoutineExerciseDetails> getExercises() {
        return exercises;
    }

    public void setExercises(List<RoutineExerciseDetails> exercises) {
        this.exercises = exercises;
    }

    @Override
    public String toString() {
        return "RoutineEntity{" +
                "routineId=" + routineId +
                ", userId=" + userId +
                ", routineName='" + routineName + '\'' +
                ", difficultyLevel='" + difficultyLevel + '\'' +
                ", exercises=" + exercises +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        RoutineEntity that = (RoutineEntity) o;
        return routineId == that.routineId && userId == that.userId && Objects.equals(routineName, that.routineName) && Objects.equals(difficultyLevel, that.difficultyLevel) && Objects.equals(exercises, that.exercises);
    }

    @Override
    public int hashCode() {
        return Objects.hash(routineId, userId, routineName, difficultyLevel, exercises);
    }
}
