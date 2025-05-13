package com.example.back.dto;

import lombok.Data;

@Data
public class SocioCadastroDTO {
    private String nomeCompleto;
    private String cpf;
    private String email;
    private String telefone;
    private String senha;
}
