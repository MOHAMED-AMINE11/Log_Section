package com.example.backend_pfm.dto;

import lombok.Data;

@Data
public class SignupRequest {
    private String nom;
    private String email;
    private String password;
    private String numero;
}