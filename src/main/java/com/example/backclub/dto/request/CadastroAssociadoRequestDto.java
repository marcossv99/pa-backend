package com.example.backclub.dto.request;

public class CadastroAssociadoRequestDto {
    private String nome;
    private String email;
    private String senha;
    private String telefone;
    private String cpf;
    
    // Constructors
    public CadastroAssociadoRequestDto() {}
    
    public CadastroAssociadoRequestDto(String nome, String email, String senha, String telefone, String cpf) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.telefone = telefone;
        this.cpf = cpf;
    }
    
    // Getters and Setters
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
    
    public String getSenha() {
        return senha;
    }
    
    public void setSenha(String senha) {
        this.senha = senha;
    }
    
    public String getTelefone() {
        return telefone;
    }
    
    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }
    
    public String getCpf() {
        return cpf;
    }
    
    public void setCpf(String cpf) {
        this.cpf = cpf;
    }
    
    @Override
    public String toString() {
        return "CadastroAssociadoRequestDto{" +
                "nome='" + nome + '\'' +
                ", email='" + email + '\'' +
                ", senha='[PROTEGIDA]'" +
                ", telefone='" + telefone + '\'' +
                ", cpf='" + cpf + '\'' +
                '}';
    }
}
