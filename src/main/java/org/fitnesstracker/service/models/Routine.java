package org.fitnesstracker.service.models;

import org.fitnesstracker.repository.entities.RoutineExerciseDetailsEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Routine {
    private int routineId;
    private int userId;
    private String routineName;
    private String difficultyLevel;

    private List<RoutineExerciseDetails> exercises = new ArrayList<>();

    public Routine(int routineId, int userId, String difficultyLevel, String routineName, List<RoutineExerciseDetails> exercises) {
        this.routineId = routineId;
        this.userId = userId;
        this.difficultyLevel = difficultyLevel;
        this.routineName = routineName;
        this.exercises = exercises;
    }

    public Routine() {
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
        return "Routine{" +
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
        Routine routine = (Routine) o;
        return routineId == routine.routineId && userId == routine.userId && Objects.equals(routineName, routine.routineName) && Objects.equals(difficultyLevel, routine.difficultyLevel) && Objects.equals(exercises, routine.exercises);
    }

    @Override
    public int hashCode() {
        return Objects.hash(routineId, userId, routineName, difficultyLevel, exercises);
    }
}

