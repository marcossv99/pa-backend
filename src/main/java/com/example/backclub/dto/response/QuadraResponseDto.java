package com.example.backclub.dto.response;

import lombok.Data;

@Data
public class QuadraResponseDto {
    private Long id;
    private int numero;
    private String modalidade;
    private int qtdPessoas;
    private String img;
    private boolean isDisponivel;
}