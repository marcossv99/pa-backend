package com.example.backclub.dto.response;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
public class QuadraResponseDto {
    private Long id;
    private int numero;
    private String modalidade;
    private int qtdPessoas;
    private String img;
    
    @JsonProperty("isDisponivel")
    private boolean isDisponivel;
}