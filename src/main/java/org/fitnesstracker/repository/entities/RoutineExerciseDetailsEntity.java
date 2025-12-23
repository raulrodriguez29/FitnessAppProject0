package org.fitnesstracker.repository.entities;

import java.util.Objects;

public class RoutineExerciseDetailsEntity {
    private ExerciseEntity exercise;

    private int sets;
    private int reps;

    public RoutineExerciseDetailsEntity() {
    }

    public RoutineExerciseDetailsEntity(ExerciseEntity exercise, int sets, int reps) {
        this.exercise = exercise;
        this.sets = sets;
        this.reps = reps;
    }

    public ExerciseEntity getExercise() {
        return exercise;
    }

    public void setExercise(ExerciseEntity exercise) {
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
        RoutineExerciseDetailsEntity that = (RoutineExerciseDetailsEntity) o;
        return sets == that.sets && reps == that.reps && Objects.equals(exercise, that.exercise);
    }

    @Override
    public int hashCode() {
        return Objects.hash(exercise, sets, reps);
    }
}
