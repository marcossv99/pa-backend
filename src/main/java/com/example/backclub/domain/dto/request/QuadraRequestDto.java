package com.example.backclub.domain.dto.request;

import lombok.Data;

@Data
public class QuadraRequestDto {
    private int numero;
    private String modalidade;
    private int qtdPessoas;
    private String img;
    private boolean isDisponivel;
}
