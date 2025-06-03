package domain.dto.request;

import lombok.Data;

@Data
public class AssociadoRequestDto {
    private String nome;
    private String email;
    private String cpf;
    private String senha;
    private String telefone;
    private boolean isAdmin;
}