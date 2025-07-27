package com.example.backclub.mapper;

import org.springframework.stereotype.Component;

import com.example.backclub.dto.request.AssociadoRequestDto;
import com.example.backclub.dto.response.AssociadoResponseDto;
import com.example.backclub.domain.entity.Associado;

@Component
public class AssociadoMapper {

    public Associado toEntity(AssociadoRequestDto dto) {
        Associado associado = new Associado();
        associado.setNome(dto.getNome());
        associado.setEmail(dto.getEmail());
        associado.setCpf(dto.getCpf());
        associado.setTelefone(dto.getTelefone());
        associado.setSenha(dto.getSenha());
        associado.setAdmin(dto.isAdmin());
        return associado;
    }

    public AssociadoResponseDto toResponse(Associado entity) {
        AssociadoResponseDto dto = new AssociadoResponseDto();
        dto.setId(entity.getId());
        dto.setNome(entity.getNome());
        dto.setEmail(entity.getEmail());
        dto.setCpf(entity.getCpf());
        dto.setTelefone(entity.getTelefone());
        dto.setAdmin(entity.isAdmin());
        
        return dto;
    }

    public void updateEntityFromDto(AssociadoRequestDto dto, Associado entity) {
        if (dto.getNome() != null) entity.setNome(dto.getNome());
        if (dto.getEmail() != null) entity.setEmail(dto.getEmail());
        if (dto.getCpf() != null) entity.setCpf(dto.getCpf());
        if (dto.getTelefone() != null) entity.setTelefone(dto.getTelefone());
        if (dto.getSenha() != null) entity.setSenha(dto.getSenha());
        entity.setAdmin(dto.isAdmin()); // Sempre atualiza isAdmin
    }
}
