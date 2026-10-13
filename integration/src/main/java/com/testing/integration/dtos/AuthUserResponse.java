package com.testing.integration.dtos;

public record AuthUserResponse(
        String username,
        String role
) {
}