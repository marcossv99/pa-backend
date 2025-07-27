package com.example.backclub.dto.request;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
public class AssociadoRequestDto {
    private String nome;
    private String email;
    private String cpf;
    private String senha;
    private String telefone;
    
    @JsonProperty("isAdmin")
    private boolean isAdmin;
}