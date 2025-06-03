package domain.dto.response;

import lombok.Data;

@Data
public class AssociadoResponseDto {
    private Long id;
    private String nome;
    private String email;
    private String cpf;
    private String telefone;
    private boolean isAdmin;
}
