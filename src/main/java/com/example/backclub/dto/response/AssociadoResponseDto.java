package com.example.backclub.dto.response;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
public class AssociadoResponseDto {
    private Long id;
    private String nome;
    private String email;
    private String cpf;
    private String telefone;
    
    @JsonProperty("isAdmin")
    private boolean isAdmin;
}
