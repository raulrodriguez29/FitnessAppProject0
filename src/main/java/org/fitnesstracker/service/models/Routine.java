package org.fitnesstracker.service.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Routine {
    private int routineId;
    private int userId; // Foreign Key: Links this routine to its creator (User)
    private String routineName;
    private String difficultyLevel;

    // This transient field holds the many-to-many relationship data
    // when a routine is fully loaded from the database.
    private List<RoutineExerciseDetails> exercises = new ArrayList<>();

    // Constructor for LOADING from the database
    public Routine(int routineId, int userId, String routineName, String difficultyLevel) {
        this.routineId = routineId;
        this.userId = userId;
        this.routineName = routineName;
        this.difficultyLevel = difficultyLevel;
    }

    // Constructor for CREATING a NEW routine
    public Routine(int userId, String routineName, String difficultyLevel) {
        this.userId = userId;
        this.routineName = routineName;
        this.difficultyLevel = difficultyLevel;
    }

    // --- Getters and Setters ---

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

