package org.fitnesstracker.repository.entities;

import org.fitnesstracker.service.models.User;

import java.time.LocalDateTime;
import java.util.Objects;

public class UserEntity {
    private int userId;
    private String username;
    private String email;
    private LocalDateTime joinDate;

    public UserEntity(int userId, String username, String email, LocalDateTime joinDate) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.joinDate = joinDate;
    }

    public UserEntity() {
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDateTime getJoinDate() {
        return joinDate;
    }

    public void setJoinDate(LocalDateTime joinDate) {
        this.joinDate = joinDate;
    }

    @Override
    public String toString() {
        return "UserEntity{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", joinDate=" + joinDate +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        UserEntity that = (UserEntity) o;
        return userId == that.userId && Objects.equals(username, that.username) && Objects.equals(email, that.email) && Objects.equals(joinDate, that.joinDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, username, email, joinDate);
    }
}
