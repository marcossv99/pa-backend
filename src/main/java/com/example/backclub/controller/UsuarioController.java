package com.example.backclub.controller;

import com.example.backclub.domain.entity.Reserva;
import com.example.backclub.dto.ReservaResponseDTO;
import com.example.backclub.service.AuthService;
import com.example.backclub.service.ReservaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/usuario")
@CrossOrigin(origins = "http://localhost:4200")
public class UsuarioController {
    
    private final ReservaService reservaService;
    private final AuthService authService;
    
    public UsuarioController(ReservaService reservaService, AuthService authService) {
        this.reservaService = reservaService;
        this.authService = authService;
    }
    
    @GetMapping("/teste")
    public ResponseEntity<?> teste() {
        return ResponseEntity.ok(Collections.singletonMap("status", "Backend funcionando"));
    }
    
    @GetMapping("/reservas")
    public ResponseEntity<?> obterReservasUsuario(@RequestHeader("Authorization") String token) {
        try {
            System.out.println("=== INÍCIO obterReservasUsuario ===");
            
            // Extrair ID do usuário do token
            String tokenLimpo = token.replace("Bearer ", "");
            System.out.println("Token recebido: " + (tokenLimpo.length() > 10 ? tokenLimpo.substring(0, 10) + "..." : tokenLimpo));
            
            Long usuarioId = authService.obterUsuarioIdDoToken(tokenLimpo);
            System.out.println("ID do usuário extraído do token: " + usuarioId);
            
            List<Reserva> reservas = reservaService.listarPorUsuario(usuarioId);
            System.out.println("Número de reservas encontradas: " + reservas.size());
            
            // Converter para DTO para evitar problemas de serialização
            List<ReservaResponseDTO> reservasDTO = new ArrayList<>();
            for (Reserva reserva : reservas) {
                try {
                    System.out.println("Processando reserva ID: " + reserva.getId());
                    
                    ReservaResponseDTO dto = new ReservaResponseDTO();
                    dto.setId(reserva.getId());
                    
                    if (reserva.getUsuario() != null) {
                        dto.setUsuarioId(reserva.getUsuario().getId());
                        dto.setUsuarioNome(reserva.getUsuario().getNome());
                    }
                    
                    if (reserva.getQuadra() != null) {
                        dto.setQuadraId(reserva.getQuadra().getId());
                        dto.setQuadraNome("Quadra " + reserva.getQuadra().getNumero());
                        dto.setQuadraModalidade(reserva.getQuadra().getModalidade());
                    }
                    
                    if (reserva.getHorario() != null) {
                        dto.setData(reserva.getHorario().getData());
                        dto.setHoraInicio(reserva.getHorario().getHoraInicio());
                        dto.setHoraFim(reserva.getHorario().getHoraFim());
                    }
                    
                    dto.setStatus("CONFIRMADA"); // Status padrão por enquanto
                    
                    // Tratar membros com cuidado
                    if (reserva.getMembros() != null) {
                        dto.setMembros(new ArrayList<>(reserva.getMembros()));
                        System.out.println("Membros da reserva " + reserva.getId() + ": " + reserva.getMembros());
                    } else {
                        dto.setMembros(new ArrayList<>());
                        System.out.println("Reserva " + reserva.getId() + " sem membros (null)");
                    }
                    
                    reservasDTO.add(dto);
                    System.out.println("Reserva " + reserva.getId() + " processada com sucesso");
                    
                } catch (Exception e) {
                    System.err.println("Erro ao processar reserva " + reserva.getId() + ": " + e.getMessage());
                    e.printStackTrace();
                    // Continuar com as outras reservas mesmo se uma falhar
                }
            }
            
            System.out.println("=== FIM obterReservasUsuario - Retornando " + reservasDTO.size() + " reservas ===");
            return ResponseEntity.ok(reservasDTO);
            
        } catch (Exception e) {
            System.err.println("Erro geral ao buscar reservas: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500)
                .body(Collections.singletonMap("error", "Erro interno do servidor: " + e.getMessage()));
        }
    }
}
