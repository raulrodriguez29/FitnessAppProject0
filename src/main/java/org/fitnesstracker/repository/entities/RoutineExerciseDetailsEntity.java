package org.fitnesstracker.repository.entities;

import org.fitnesstracker.service.models.Exercise;
import org.fitnesstracker.service.models.RoutineExerciseDetails;

import java.util.Objects;

public class RoutineExerciseDetailsEntity {
    private Exercise exercise;
    private int sets;
    private int reps;

    public RoutineExerciseDetailsEntity(Exercise exercise, int sets, int reps) {
        this.exercise = exercise;
        this.sets = sets;
        this.reps = reps;
    }

    public RoutineExerciseDetailsEntity() {
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
        return "RoutineExerciseDetailsEntity{" +
                "exercise=" + exercise +
                ", sets=" + sets +
                ", reps=" + reps +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        RoutineExerciseDetailsEntity that = (RoutineExerciseDetailsEntity) o;
        return sets == that.sets && reps == that.reps && Objects.equals(exercise, that.exercise);
    }

    @Override
    public int hashCode() {
        return Objects.hash(exercise, sets, reps);
    }
}
