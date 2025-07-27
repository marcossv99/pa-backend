package com.example.backclub.controller;

import com.example.backclub.dto.request.ReservaRequestDto;
import com.example.backclub.mapper.ReservaMapper;
import com.example.backclub.service.ReservaService;
import com.example.backclub.repository.ReservaRepository;
import com.example.backclub.domain.entity.Quadra;
import com.example.backclub.domain.entity.Associado;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.HashMap;
import java.util.Collections;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reservas")
@CrossOrigin(origins = "http://localhost:4200")
public class ReservaController {
    private final ReservaService reservaService;
    private final ReservaMapper reservaMapper;
    private final ReservaRepository reservaRepository;

    public ReservaController(ReservaService reservaService, ReservaMapper reservaMapper, ReservaRepository reservaRepository) {
        this.reservaService = reservaService;
        this.reservaMapper = reservaMapper;
        this.reservaRepository = reservaRepository;
    }

    @PostMapping
    public ResponseEntity<?> cadastrar(@RequestBody ReservaRequestDto dto) {
        try {
            System.out.println("Recebendo reserva: " + dto);
            var reserva = reservaService.cadastrarReserva(dto);
            var response = reservaMapper.toResponse(reserva);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            System.err.println("Erro de argumento: " + e.getMessage());
            return ResponseEntity.badRequest().body(java.util.Collections.singletonMap("error", e.getMessage()));
        } catch (Exception e) {
            System.err.println("Erro interno: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(java.util.Collections.singletonMap("error", e.getMessage()));
        }
    }

    @PostMapping("/anonima")
    public ResponseEntity<?> cadastrarReservaAnonima(@RequestBody ReservaRequestDto dto) {
        try {
            System.out.println("Recebendo reserva anônima: " + dto);
            // Force usuarioId to null para usar usuário padrão
            dto.setUsuarioId(null);
            var reserva = reservaService.cadastrarReserva(dto);
            var response = reservaMapper.toResponse(reserva);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            System.err.println("Erro de argumento: " + e.getMessage());
            return ResponseEntity.badRequest().body(java.util.Collections.singletonMap("error", e.getMessage()));
        } catch (Exception e) {
            System.err.println("Erro interno: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(java.util.Collections.singletonMap("error", e.getMessage()));
        }
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<?> listarPorUsuario(@PathVariable Long usuarioId) {
        try {
            var reservas = reservaService.listarPorUsuario(usuarioId).stream()
                    .map(reservaMapper::toResponse)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(reservas);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(java.util.Collections.singletonMap("error", e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        try {
            var reserva = reservaService.buscarPorId(id);
            var response = reservaMapper.toResponse(reserva);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(java.util.Collections.singletonMap("error", "Reserva não encontrada"));
        }
    }

    @GetMapping
    public ResponseEntity<?> listar(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            // SEGURANÇA CRÍTICA: Este endpoint não deve retornar todas as reservas do sistema!
            // Frontend deve usar /usuario/{usuarioId} para reservas específicas
            // ou /admin/todas apenas para administradores autenticados
            
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of(
                        "error", "Endpoint não permitido",
                        "message", "Use /api/reservas/usuario/{userId} ou /api/reservas/admin/todas"
                    ));
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(java.util.Collections.singletonMap("error", e.getMessage()));
        }
    }

    @GetMapping("/admin/todas")
    public ResponseEntity<?> listarTodasParaAdmin(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            // TODO: Implementar verificação de autorização admin aqui
            // Por ora, retornar todas as reservas para administradores
            
            var reservas = reservaService.listarTodas().stream()
                    .map(reservaMapper::toResponse)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(reservas);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(java.util.Collections.singletonMap("error", e.getMessage()));
        }
    }




    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable Long id, @RequestBody ReservaRequestDto dto) {
        try {
            var reserva = reservaService.atualizar(id, dto.getHoraInicio(), dto.getHoraFim(), dto.getMembros());
            var response = reservaMapper.toResponse(reserva);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(java.util.Collections.singletonMap("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(java.util.Collections.singletonMap("error", "Erro interno do servidor"));
        }
    }

    // Endpoint de teste para validar regras de negócio antes de criar reserva
    @PostMapping("/validar")
    public ResponseEntity<?> validarReserva(@RequestBody ReservaRequestDto dto) {
        try {
            System.out.println("=== VALIDANDO RESERVA (TESTE) ===");
            System.out.println("Dados recebidos: " + dto);
            
            Map<String, Object> resultado = new HashMap<>();
            resultado.put("dadosRecebidos", dto);
            
            // Testar todas as validações sem criar a reserva
            try {
                // Validações básicas
                if (dto.getQuadraId() == null) {
                    throw new IllegalArgumentException("ID da quadra é obrigatório");
                }
                if (dto.getData() == null) {
                    throw new IllegalArgumentException("Data da reserva é obrigatória");
                }
                if (dto.getHoraInicio() <= 0 || dto.getHoraFim() <= 0) {
                    throw new IllegalArgumentException("Horário de início e fim são obrigatórios e devem ser maiores que zero");
                }
                
                resultado.put("validacoesBasicas", "✅ OK");
                
                // Buscar entidades
                Associado usuario;
                if (dto.getUsuarioId() == null) {
                    usuario = reservaService.obterUsuarioPadrao(); // Método helper
                } else {
                    usuario = reservaService.buscarUsuario(dto.getUsuarioId()); // Método helper
                }
                
                Quadra quadra = reservaService.buscarQuadra(dto.getQuadraId()); // Método helper
                
                resultado.put("usuario", Map.of("id", usuario.getId(), "nome", usuario.getNome()));
                resultado.put("quadra", Map.of("id", quadra.getId(), "numero", quadra.getNumero(), "modalidade", quadra.getModalidade()));
                
                // Testar regras de negócio
                boolean regra1 = reservaRepository.existsByUsuarioIdAndDataAndHorario(
                    usuario.getId(), dto.getData(), dto.getHoraInicio(), dto.getHoraFim()
                );
                
                boolean regra2 = reservaRepository.existsByQuadraIdAndDataAndHorario(
                    dto.getQuadraId(), dto.getData(), dto.getHoraInicio(), dto.getHoraFim()
                );
                
                resultado.put("regra1_usuarioJaTemReservaNoHorario", regra1);
                resultado.put("regra2_quadraJaReservada", regra2);
                
                if (regra1) {
                    resultado.put("erro", "Você já possui uma reserva no horário " + dto.getHoraInicio() + ":00 - " + dto.getHoraFim() + ":00. Não é possível ter duas reservas no mesmo horário.");
                } else if (regra2) {
                    resultado.put("erro", "Esta quadra já está reservada neste horário");
                } else {
                    resultado.put("status", "✅ Reserva pode ser criada");
                }
                
            } catch (Exception e) {
                resultado.put("erro", e.getMessage());
            }
            
            return ResponseEntity.ok(resultado);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletarReserva(@PathVariable Long id) {
        try {
            reservaService.deletarReserva(id);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "Erro interno do servidor"));
        }
    }

    @PostMapping("/{id}/cancelar-admin")
    public ResponseEntity<?> cancelarReservaPorAdmin(@PathVariable Long id, @RequestBody Map<String, String> request) {
        try {
            String motivo = request.get("motivo");
            if (motivo == null || motivo.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Collections.singletonMap("error", "Motivo do cancelamento é obrigatório"));
            }
            
            reservaService.cancelarReservaPorAdmin(id, motivo.trim());
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "Erro interno do servidor"));
        }
    }
}
