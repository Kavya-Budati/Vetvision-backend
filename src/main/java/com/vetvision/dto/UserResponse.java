package com.vetvision.dto;

import com.vetvision.entity.Role;
import com.vetvision.entity.User;

public record UserResponse(Long id, String email, String firstName, String lastName, Role role) {
    public static UserResponse from(User user) {
        return new UserResponse(user.getId(), user.getEmail(), user.getFirstName(), user.getLastName(), user.getRole());
    }
}