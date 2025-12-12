package org.fitnesstracker.service.models;

import java.util.Objects;

public class RoutineExerciseDetails {
    private Exercise exercise;
    private int sets;
    private int reps;

    public RoutineExerciseDetails(Exercise exercise, int sets, int reps) {
        this.exercise = exercise;
        this.sets = sets;
        this.reps = reps;
    }

    public Exercise getExercise() {
        return exercise;
    }

    public void setExercise(Exercise exercise) {
        this.exercise = exercise;
    }

    public int getSets() {
        return sets;
    }

    public void setSets(int sets) {
        this.sets = sets;
    }

    public int getReps() {
        return reps;
    }

    public void setReps(int reps) {
        this.reps = reps;
    }

    @Override
    public String toString() {
        return "RoutineExerciseDetails{" +
                "exercise=" + exercise +
                ", sets=" + sets +
                ", reps=" + reps +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        RoutineExerciseDetails that = (RoutineExerciseDetails) o;
        return sets == that.sets && reps == that.reps && Objects.equals(exercise, that.exercise);
    }

    @Override
    public int hashCode() {
        return Objects.hash(exercise, sets, reps);
    }
}
