package com.example.demo.security.dto;

public record AuthResponseDto(

        Long id,
        String email,
        String jwt

) {
}
