package com.example.backclub.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AuthResponseDto {
    private String token;
    private Long userId;
    private String nome;
    private String email;
    @JsonProperty("isAdmin")
    private Boolean isAdmin;
    private String telefone;
    private String fotoPerfil;
    
    // Constructors
    public AuthResponseDto() {}
    
    public AuthResponseDto(String token, Long userId, String nome, String email, Boolean isAdmin, String telefone, String fotoPerfil) {
        this.token = token;
        this.userId = userId;
        this.nome = nome;
        this.email = email;
        this.isAdmin = isAdmin;
        this.telefone = telefone;
        this.fotoPerfil = fotoPerfil;
    }
    
    // Getters and Setters
    public String getToken() {
        return token;
    }
    
    public void setToken(String token) {
        this.token = token;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public String getNome() {
        return nome;
    }
    
    public void setNome(String nome) {
        this.nome = nome;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public Boolean getIsAdmin() {
        return isAdmin;
    }
    
    public void setIsAdmin(Boolean isAdmin) {
        this.isAdmin = isAdmin;
    }
    
    public String getTelefone() {
        return telefone;
    }
    
    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }
    
    public String getFotoPerfil() {
        return fotoPerfil;
    }
    
    public void setFotoPerfil(String fotoPerfil) {
        this.fotoPerfil = fotoPerfil;
    }
    
    @Override
    public String toString() {
        return "AuthResponseDto{" +
                "token='[PROTEGIDO]'" +
                ", userId=" + userId +
                ", nome='" + nome + '\'' +
                ", email='" + email + '\'' +
                ", isAdmin=" + isAdmin +
                ", telefone='" + telefone + '\'' +
                ", fotoPerfil='" + fotoPerfil + '\'' +
                '}';
    }
}
