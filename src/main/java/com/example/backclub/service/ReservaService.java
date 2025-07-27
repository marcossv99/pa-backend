package com.example.backclub.service;

import com.example.backclub.domain.entity.Reserva;
import com.example.backclub.domain.entity.Horario;
import com.example.backclub.dto.request.ReservaRequestDto;
import com.example.backclub.domain.entity.Quadra;
import com.example.backclub.domain.entity.Associado;
import com.example.backclub.repository.ReservaRepository;
import com.example.backclub.repository.HorarioRepository;
import com.example.backclub.repository.QuadraRepository;
import com.example.backclub.repository.AssociadoRepository;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Service
public class ReservaService {

    private final ReservaRepository reservaRepository;
    private final HorarioRepository horarioRepository;

    @Autowired
    private QuadraRepository quadraRepository;
    @Autowired
    private AssociadoRepository associadoRepository;

    public ReservaService(ReservaRepository reservaRepository, HorarioRepository horarioRepository) {
        this.reservaRepository = reservaRepository;
        this.horarioRepository = horarioRepository;
    }

    public Reserva cadastrar(Reserva reserva) {
        // Garante que a reserva está ativa ao ser criada
        reserva.setCancelada(false);
        reserva.setMotivoCancelamento(null);
        reserva.setCanceladaPor(null);
        reserva.setDataCancelamento(null);
        return reservaRepository.save(reserva);
    }

    public List<Reserva> listar() {
        return reservaRepository.findAllAtivas(); // Apenas reservas não canceladas
    }

    public List<Reserva> listarTodas() {
        return reservaRepository.findAll(); // Todas as reservas, incluindo canceladas
    }


    public List<Reserva> listarPorUsuario(Long usuarioId) {
        // Retorna todas, inclusive canceladas
        return reservaRepository.findByUsuarioId(usuarioId);
    }



    public Reserva buscarPorId(Long id) {
        return reservaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reserva não encontrada"));
    }








    public Reserva atualizar(Long id, float novaHoraInicio, float novaHoraFim, List<String> novosMembros) {
        Reserva reserva = buscarPorId(id);
        
        // Validações básicas
        if (novaHoraInicio <= 0 || novaHoraFim <= 0) {
            throw new IllegalArgumentException("Horário de início e fim são obrigatórios e devem ser maiores que zero");
        }
        if (novaHoraInicio >= 24 || novaHoraFim >= 24) {
            throw new IllegalArgumentException("Horários devem estar entre 0 e 23:59");
        }
        
        boolean cruzaMeiaNoite = novaHoraInicio > novaHoraFim && novaHoraFim <= 12;
        if (!cruzaMeiaNoite && novaHoraInicio >= novaHoraFim) {
            throw new IllegalArgumentException("Horário de início deve ser anterior ao horário de fim");
        }

        // Verificar se o novo horário está disponível (não conflita com outras reservas)
        boolean quadraJaReservada = reservaRepository.existsByQuadraIdAndDataAndHorario(
            reserva.getQuadra().getId(), reserva.getHorario().getData(), novaHoraInicio, novaHoraFim
        );
        if (quadraJaReservada) {
            // Verificar se é a própria reserva que está causando o conflito
            List<Reserva> reservasConflitantes = reservaRepository.findAll().stream()
                .filter(r -> r.getQuadra().getId().equals(reserva.getQuadra().getId()) &&
                           r.getHorario().getData().equals(reserva.getHorario().getData()) &&
                           !r.getId().equals(id))
                .toList();
            
            boolean temConflito = reservasConflitantes.stream()
                .anyMatch(r -> !(novaHoraFim <= r.getHorario().getHoraInicio() || 
                               novaHoraInicio >= r.getHorario().getHoraFim()));
            
            if (temConflito) {
                throw new IllegalArgumentException("O novo horário conflita com uma reserva existente");
            }
        }

        // Atualizar o horário
        Horario horario = reserva.getHorario();
        horario.setHoraInicio(novaHoraInicio);
        horario.setHoraFim(novaHoraFim);
        horarioRepository.save(horario);
        
        // Atualizar os membros/convidados
        if (novosMembros != null) {
            reserva.setMembros(novosMembros);
            reservaRepository.save(reserva);
        }
        
        return reserva;
    }

    public Reserva cadastrarReserva(ReservaRequestDto dto) {
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
        if (dto.getHoraInicio() >= 24 || dto.getHoraFim() >= 24) {
            throw new IllegalArgumentException("Horários devem estar entre 0 e 23:59");
        }
        // Para horários que cruzam meia-noite, considerar válido se hora fim < 12 (madrugada)
        boolean cruzaMeiaNoite = dto.getHoraInicio() > dto.getHoraFim() && dto.getHoraFim() <= 12;
        if (!cruzaMeiaNoite && dto.getHoraInicio() >= dto.getHoraFim()) {
            throw new IllegalArgumentException("Horário de início deve ser anterior ao horário de fim, ou a reserva deve terminar na madrugada (até 12h)");
        }

        // Se não há usuário logado, usa o primeiro usuário disponível como padrão
        Associado usuario;
        if (dto.getUsuarioId() == null) {
            // Busca o primeiro usuário disponível para reservas anônimas
            usuario = associadoRepository.findAll().stream()
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Nenhum usuário disponível no sistema"));
        } else {
            usuario = associadoRepository.findById(dto.getUsuarioId())
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));
        }

        // Buscar quadra
        Quadra quadra = quadraRepository.findById(dto.getQuadraId())
            .orElseThrow(() -> new IllegalArgumentException("Quadra não encontrada"));

        // NOVA REGRA: Verificar se usuário já possui reserva para o mesmo tipo de quadra no mesmo dia
        System.out.println("=== VERIFICANDO REGRA: MESMO TIPO DE QUADRA NO MESMO DIA ===");
        System.out.println("Modalidade da quadra solicitada: " + quadra.getModalidade());
        
        boolean jaTemReservaDaModalidadeNoDia = reservaRepository.existsByUsuarioIdAndDataAndModalidade(
            usuario.getId(), dto.getData(), quadra.getModalidade()
        );
        
        System.out.println("Usuário já tem reserva desta modalidade no dia? " + jaTemReservaDaModalidadeNoDia);
        
        if (jaTemReservaDaModalidadeNoDia) {
            System.out.println("❌ REGRA VIOLADA: Usuário já possui uma reserva para " + quadra.getModalidade() + " neste dia");
            throw new IllegalArgumentException("Você já possui uma reserva para quadra de " + quadra.getModalidade() + " neste dia. Não é possível fazer múltiplas reservas do mesmo tipo de quadra no mesmo dia.");
        }
        System.out.println("✅ REGRA OK: Usuário não tem reserva desta modalidade no dia");

        // REGRA 1: Verificar se usuário já possui reserva no mesmo horário (qualquer quadra)
        System.out.println("=== VERIFICANDO REGRA 1: MESMO HORÁRIO (QUALQUER QUADRA) ===");
        System.out.println("Usuário ID: " + usuario.getId() + " (" + usuario.getNome() + ")");
        System.out.println("Data: " + dto.getData());
        System.out.println("Horário solicitado: " + dto.getHoraInicio() + ":00 - " + dto.getHoraFim() + ":00");
        System.out.println("Quadra solicitada: " + quadra.getNumero() + " - " + quadra.getModalidade());
        
        boolean usuarioJaTemReservaNoHorario = reservaRepository.existsByUsuarioIdAndDataAndHorario(
            usuario.getId(), dto.getData(), dto.getHoraInicio(), dto.getHoraFim()
        );
        
        System.out.println("Usuário já tem reserva neste horário? " + usuarioJaTemReservaNoHorario);
        
        if (usuarioJaTemReservaNoHorario) {
            System.out.println("❌ REGRA 1 VIOLADA: Usuário já possui uma reserva neste horário");
            throw new IllegalArgumentException("Você já possui uma reserva no horário " + dto.getHoraInicio() + ":00 - " + dto.getHoraFim() + ":00. Não é possível ter duas reservas no mesmo horário.");
        }
        System.out.println("✅ REGRA 1 OK: Usuário não tem conflito de horário");

        // REGRA 2: Verificar se já existe reserva para esta quadra no mesmo horário (qualquer usuário)
        System.out.println("=== VERIFICANDO REGRA 2: MESMA QUADRA/HORÁRIO (QUALQUER USUÁRIO) ===");
        System.out.println("Quadra ID: " + dto.getQuadraId());
        System.out.println("Data: " + dto.getData());
        System.out.println("Horário: " + dto.getHoraInicio() + ":00 - " + dto.getHoraFim() + ":00");
        
        boolean quadraJaReservada = reservaRepository.existsByQuadraIdAndDataAndHorario(
            dto.getQuadraId(), dto.getData(), dto.getHoraInicio(), dto.getHoraFim()
        );
        
        System.out.println("Quadra já reservada neste horário? " + quadraJaReservada);
        
        if (quadraJaReservada) {
            System.out.println("❌ REGRA 2 VIOLADA: Quadra já está reservada neste horário");
            throw new IllegalArgumentException("Esta quadra já está reservada neste horário");
        }
        System.out.println("✅ REGRA 2 OK: Quadra disponível neste horário");

        // Criar ou buscar horário
        Horario horario;
        if (dto.getHorarioId() != null) {
            horario = horarioRepository.findById(dto.getHorarioId())
                .orElseThrow(() -> new IllegalArgumentException("Horário não encontrado"));
        } else {
            horario = new Horario();
            horario.setData(dto.getData());
            horario.setHoraInicio(dto.getHoraInicio());
            horario.setHoraFim(dto.getHoraFim());
            horario.setQuadra(quadra);
            horario = horarioRepository.save(horario);
        }

        // Criar reserva
        Reserva reserva = new Reserva();
        reserva.setUsuario(usuario);
        reserva.setQuadra(quadra);
        reserva.setHorario(horario);
        reserva.setMembros(dto.getMembros() != null ? dto.getMembros() : java.util.Collections.emptyList());
        
        return cadastrar(reserva);
    }

    // Métodos helper para debug e validação
    public Associado obterUsuarioPadrao() {
        return associadoRepository.findAll().stream()
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Nenhum usuário disponível no sistema"));
    }
    
    public Associado buscarUsuario(Long usuarioId) {
        return associadoRepository.findById(usuarioId)
            .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));
    }
    
    public Quadra buscarQuadra(Long quadraId) {
        return quadraRepository.findById(quadraId)
            .orElseThrow(() -> new IllegalArgumentException("Quadra não encontrada"));
    }

    public void deletarReserva(Long id) {
        Reserva reserva = reservaRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Reserva não encontrada"));
        
        // Soft delete: marcar como cancelada ao invés de deletar
        reserva.setCancelada(true);
        reserva.setCanceladaPor("ASSOCIADO");
        reserva.setDataCancelamento(java.time.LocalDateTime.now());
        
        reservaRepository.save(reserva);
    }

    public void cancelarReservaPorAdmin(Long id, String motivoCancelamento) {
        Reserva reserva = reservaRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Reserva não encontrada"));
        
        // Soft delete com motivo do admin
        reserva.setCancelada(true);
        reserva.setMotivoCancelamento(motivoCancelamento);
        reserva.setCanceladaPor("ADMIN");
        reserva.setDataCancelamento(java.time.LocalDateTime.now());
        
        reservaRepository.save(reserva);
    }
    /**
     * Remove reservas canceladas há mais de X dias.
     * @param dias Quantidade de dias para considerar reserva antiga
     * @return Quantidade de reservas removidas
     */
    public int limparReservasCanceladasAntigas(int dias) {
        java.time.LocalDateTime limite = java.time.LocalDateTime.now().minusDays(dias);
        List<Reserva> reservas = reservaRepository.findAll();
        int removidas = 0;
        for (Reserva reserva : reservas) {
            if (Boolean.TRUE.equals(reserva.getCancelada()) && reserva.getDataCancelamento() != null && reserva.getDataCancelamento().isBefore(limite)) {
                reservaRepository.delete(reserva);
                removidas++;
            }
        }
        return removidas;
    }
}
