package com.example.backclub.service;

import com.example.backclub.domain.entity.Quadra;
import com.example.backclub.dto.response.HorarioDisponivelDto;
import com.example.backclub.repository.QuadraRepository;
import com.example.backclub.repository.ReservaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class QuadraService {

    private final QuadraRepository quadraRepository;
    private final ReservaRepository reservaRepository;

    public QuadraService(QuadraRepository quadraRepository, ReservaRepository reservaRepository) {
        this.quadraRepository = quadraRepository;
        this.reservaRepository = reservaRepository;
    }

    public Quadra cadastrar(Quadra quadra) {
        // Normalizar modalidade: primeira letra maiúscula, resto minúscula
        normalizarModalidade(quadra);
        return quadraRepository.save(quadra);
    }

    public Quadra editar(Quadra quadra) {
        // Normalizar modalidade: primeira letra maiúscula, resto minúscula
        normalizarModalidade(quadra);
        return quadraRepository.save(quadra);
    }

    private void normalizarModalidade(Quadra quadra) {
        if (quadra.getModalidade() != null) {
            String modalidadeNormalizada = quadra.getModalidade().trim();
            if (!modalidadeNormalizada.isEmpty()) {
                modalidadeNormalizada = modalidadeNormalizada.substring(0, 1).toUpperCase() + 
                                      modalidadeNormalizada.substring(1).toLowerCase();
                quadra.setModalidade(modalidadeNormalizada);
            }
        }
    }

    public List<Quadra> listar() {
        return quadraRepository.findAll();
    }

    public Quadra buscarPorId(Long id) {
        return quadraRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Quadra não encontrada"));
    }

    public void deletar(Long id) {
        if (!quadraRepository.existsById(id)) {
            throw new RuntimeException("Quadra não encontrada");
        }

        // Verifica se há reservas futuras (data >= hoje)
        boolean temReservaFutura = reservaRepository.existsByQuadraIdAndDataGreaterThanEqual(id, LocalDate.now().plusDays(1));
        if (temReservaFutura) {
            throw new IllegalStateException("Não é possível excluir a quadra pois existem reservas futuras não concluídas. Só é permitido excluir quadras sem reservas futuras.");
        }

        quadraRepository.deleteById(id);
    }

    public List<HorarioDisponivelDto> listarHorariosDisponiveis(Long quadraId, String dataStr) {
        try {
            // Validar se a quadra existe
            if (!quadraRepository.existsById(quadraId)) {
                throw new IllegalArgumentException("Quadra não encontrada");
            }

            LocalDate data;
            try {
                data = LocalDate.parse(dataStr);
            } catch (Exception e) {
                throw new IllegalArgumentException("Formato de data inválido. Use YYYY-MM-DD");
            }

            // Não permitir reservas em datas passadas
            if (data.isBefore(LocalDate.now())) {
                throw new IllegalArgumentException("Não é possível fazer reservas em datas passadas");
            }

            // Gerar horários do dia (6:00 às 23:00, intervalos de 1 hora)
            List<HorarioDisponivelDto> horariosDisponiveis = new ArrayList<>();
            LocalDateTime agora = LocalDateTime.now();
            LocalDate hoje = LocalDate.now();
            
            System.out.println("=== DEBUG HORÁRIOS DISPONÍVEIS ===");
            System.out.println("Quadra ID: " + quadraId);
            System.out.println("Data solicitada: " + data);
            System.out.println("Data atual (hoje): " + hoje);

            for (int hora = 6; hora <= 22; hora++) {
                LocalDateTime inicioHorario = data.atTime(hora, 0);

                boolean horarioPassou = false;
                if (data.equals(hoje)) {
                    // Adicionar margem de 30 minutos para evitar reservas muito próximas
                    LocalDateTime horarioComMargem = inicioHorario.minusMinutes(30);
                    horarioPassou = agora.isAfter(horarioComMargem);
                }

                // Verificar se já existe reserva neste horário - usando try-catch para capturar erros
                boolean jaReservado = false;
                try {
                    jaReservado = reservaRepository.existsByQuadraIdAndDataAndHorario(
                        quadraId, data, (float) hora, (float) (hora + 1)
                    );
                } catch (Exception e) {
                    System.err.println("Erro ao verificar reserva para horário " + hora + ": " + e.getMessage());
                    // Em caso de erro, assume que não está reservado para permitir o funcionamento
                    jaReservado = false;
                }
                
                String status;
                boolean disponivel = !horarioPassou && !jaReservado;
                
                if (horarioPassou) {
                    status = "Horário já passou (margem 30min)";
                } else if (jaReservado) {
                    status = "Já reservado";
                } else {
                    status = "Disponível";
                }

                horariosDisponiveis.add(new HorarioDisponivelDto(
                    hora,
                    hora + 1,
                    data,
                    disponivel,
                    status
                ));
            }
            
            System.out.println("=== FIM DEBUG HORÁRIOS ===");
            System.out.println("Total de horários gerados: " + horariosDisponiveis.size());
            
            return horariosDisponiveis;
            
        } catch (IllegalArgumentException e) {
            System.err.println("Erro de validação: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            System.err.println("Erro inesperado ao gerar horários: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Erro interno ao gerar horários disponíveis", e);
        }
    }
    
}