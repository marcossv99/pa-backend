package com.example.backclub.dto.request;

public class LoginRequestDto {
    private String email;
    private String senha;
    
    // Constructors
    public LoginRequestDto() {}
    
    public LoginRequestDto(String email, String senha) {
        this.email = email;
        this.senha = senha;
    }
    
    // Getters and Setters
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getSenha() {
        return senha;
    }
    
    public void setSenha(String senha) {
        this.senha = senha;
    }
    
    @Override
    public String toString() {
        return "LoginRequestDto{" +
                "email='" + email + '\'' +
                ", senha='[PROTEGIDA]'" +
                '}';
    }
}
