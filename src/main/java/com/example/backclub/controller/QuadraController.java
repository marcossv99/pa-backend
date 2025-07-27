package com.example.backclub.controller;

import com.example.backclub.dto.request.QuadraRequestDto;
import com.example.backclub.dto.request.DisponibilidadeRequestDto;
import com.example.backclub.dto.response.QuadraResponseDto;
import com.example.backclub.domain.entity.Quadra;
import com.example.backclub.domain.entity.Reserva;
import com.example.backclub.mapper.QuadraMapper;
import com.example.backclub.service.QuadraService;
import com.example.backclub.repository.ReservaRepository;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Map;
import java.util.HashMap;
import java.time.LocalDate;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.net.MalformedURLException;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/quadras")
@CrossOrigin(origins = "http://localhost:4200")
public class QuadraController {

    private final QuadraService quadraService;
    private final QuadraMapper quadraMapper;
    private final ReservaRepository reservaRepository;

    public QuadraController(QuadraService quadraService, QuadraMapper quadraMapper, ReservaRepository reservaRepository) {
        this.quadraService = quadraService;
        this.quadraMapper = quadraMapper;
        this.reservaRepository = reservaRepository;
    }

    private String salvarImagem(MultipartFile imagem) throws IOException {
        // Diretório onde as imagens serão salvas (diretório público do frontend)
        String uploadDir = "C:/Users/marco/OneDrive/Documentos/site 2/frontend/public/quadras/";

        // Criar diretório se não existir
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Gerar nome único para o arquivo
        String fileName = System.currentTimeMillis() + "_" + imagem.getOriginalFilename();
        Path filePath = uploadPath.resolve(fileName);

        // Salvar o arquivo
        Files.copy(imagem.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return fileName;
    }

    private String normalizarModalidade(String modalidade) {
        if (modalidade == null || modalidade.trim().isEmpty()) {
            return modalidade;
        }
        
        String modalidadeLimpa = modalidade.trim();
        // Primeira letra maiúscula, resto minúscula
        return modalidadeLimpa.substring(0, 1).toUpperCase() + 
               modalidadeLimpa.substring(1).toLowerCase();
    }

    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<?> cadastrar(
            @RequestParam("numero") int numero,
            @RequestParam("modalidade") String modalidade,
            @RequestParam("qtdPessoas") int qtdPessoas,
            @RequestPart(value = "imagens", required = false) List<MultipartFile> imagens
    ) {
        try {
            String img = null;
            
            // Salvar a imagem se foi fornecida
            if (imagens != null && !imagens.isEmpty()) {
                MultipartFile primeiraImagem = imagens.get(0);
                img = salvarImagem(primeiraImagem);
                System.out.println("Imagem salva: " + img);
            }
            
            Quadra quadra = new Quadra();
            quadra.setNumero(numero);
            // Normalizar modalidade: primeira letra maiúscula, resto minúscula
            quadra.setModalidade(normalizarModalidade(modalidade));
            quadra.setQtdPessoas(qtdPessoas);
            quadra.setImg(img);
            quadra.setDisponivel(true);
            
            Quadra quadraSalva = quadraService.cadastrar(quadra);
            return ResponseEntity.ok(quadraMapper.toResponse(quadraSalva));
        } catch (Exception e) {
            System.err.println("Erro ao cadastrar quadra: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(java.util.Collections.singletonMap("error", "Erro ao cadastrar quadra: " + e.getMessage()));
        }
    }

    @PostMapping("/cadastrar-simples")
    public QuadraResponseDto cadastrarSimples(@RequestBody QuadraRequestDto dto) {
        Quadra quadra = new Quadra();
        quadra.setNumero(dto.getNumero());
        quadra.setModalidade(normalizarModalidade(dto.getModalidade()));
        quadra.setQtdPessoas(dto.getQtdPessoas());
        quadra.setImg(null); // Sem imagem para o cadastro simples
        quadra.setDisponivel(true);
        Quadra quadraSalva = quadraService.cadastrar(quadra);
        return quadraMapper.toResponse(quadraSalva);
    }

    @GetMapping
    public List<QuadraResponseDto> listar() {
        return quadraService.listar().stream()
                .map(quadraMapper::toResponse)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<QuadraResponseDto> buscarPorId(@PathVariable Long id) {
        try {
            Quadra quadra = quadraService.buscarPorId(id);
            return ResponseEntity.ok(quadraMapper.toResponse(quadra));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(null);
        }
    }

    @PutMapping(value = "/{id}", consumes = {"multipart/form-data"})
    public ResponseEntity<?> editar(
            @PathVariable Long id,
            @RequestParam("numero") int numero,
            @RequestParam("modalidade") String modalidade,
            @RequestParam("qtdPessoas") int qtdPessoas,
            @RequestPart(value = "imagens", required = false) List<MultipartFile> imagens
    ) {
        try {
            System.out.println("Editando quadra ID: " + id);
            System.out.println("Dados recebidos - numero: " + numero + ", modalidade: " + modalidade + ", qtdPessoas: " + qtdPessoas);
            System.out.println("Imagens recebidas: " + (imagens != null ? imagens.size() : 0));
            
            Quadra quadra = quadraService.buscarPorId(id);
            
            // Atualizar dados básicos
            quadra.setNumero(numero);
            quadra.setModalidade(normalizarModalidade(modalidade));
            quadra.setQtdPessoas(qtdPessoas);
            
            // Atualizar imagem se uma nova foi fornecida
            if (imagens != null && !imagens.isEmpty()) {
                MultipartFile primeiraImagem = imagens.get(0);
                System.out.println("Processando imagem: " + primeiraImagem.getOriginalFilename() + " - Tamanho: " + primeiraImagem.getSize());
                
                String img = salvarImagem(primeiraImagem);
                quadra.setImg(img);
                System.out.println("Nova imagem salva: " + img);
            }
            
            Quadra quadraAtualizada = quadraService.editar(quadra);
            System.out.println("Quadra editada com sucesso!");
            return ResponseEntity.ok(quadraMapper.toResponse(quadraAtualizada));
        } catch (Exception e) {
            System.err.println("Erro ao editar quadra: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(java.util.Collections.singletonMap("error", "Erro ao editar quadra: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editarJson(@PathVariable Long id, @RequestBody QuadraRequestDto dto) {
        try {
            Quadra quadra = quadraService.buscarPorId(id);
            quadraMapper.updateEntityFromDto(dto, quadra);
            // Normalizar modalidade após o mapeamento
            quadra.setModalidade(normalizarModalidade(quadra.getModalidade()));
            Quadra quadraAtualizada = quadraService.editar(quadra);
            return ResponseEntity.ok(quadraMapper.toResponse(quadraAtualizada));
        } catch (RuntimeException e) {
            // Quadra não encontrada ou erro de negócio
            String msg = e instanceof IllegalStateException ? e.getMessage() : "Quadra não encontrada: " + e.getMessage();
            HttpStatus status = e instanceof IllegalStateException ? HttpStatus.CONFLICT : HttpStatus.NOT_FOUND;
            return ResponseEntity.status(status)
                    .body(java.util.Collections.singletonMap("error", msg));
        } catch (Exception e) {
            // Erro inesperado
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(java.util.Collections.singletonMap("error", "Erro ao editar quadra: " + e.getMessage()));
        }
    }

    @PatchMapping("/{id}/disponibilidade")
    public ResponseEntity<?> alterarDisponibilidade(@PathVariable Long id, @RequestBody DisponibilidadeRequestDto request) {
        try {
            Quadra quadra = quadraService.buscarPorId(id);
            quadra.setDisponivel(request.isDisponivel());
            Quadra quadraAtualizada = quadraService.editar(quadra);
            return ResponseEntity.ok(quadraMapper.toResponse(quadraAtualizada));
        } catch (RuntimeException e) {
            // Quadra não encontrada ou erro de negócio
            String msg = e instanceof IllegalStateException ? e.getMessage() : "Quadra não encontrada: " + e.getMessage();
            HttpStatus status = e instanceof IllegalStateException ? HttpStatus.CONFLICT : HttpStatus.NOT_FOUND;
            return ResponseEntity.status(status)
                    .body(java.util.Collections.singletonMap("error", msg));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(java.util.Collections.singletonMap("error", "Erro ao alterar disponibilidade: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletar(@PathVariable Long id) {
        try {
            quadraService.deletar(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalStateException e) {
            // Quadra não pode ser excluída por reservas ativas/futuras
            return ResponseEntity.status(HttpStatus.CONFLICT).body(java.util.Collections.singletonMap("error", e.getMessage()));
        } catch (RuntimeException e) {
            // Quadra não encontrada
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(java.util.Collections.singletonMap("error", "Quadra não encontrada: " + e.getMessage()));
        } catch (Exception e) {
            // Erro inesperado
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(java.util.Collections.singletonMap("error", "Erro interno do servidor: " + e.getMessage()));
        }
    }

    @GetMapping("/{quadraId}/horarios-disponiveis")
    public ResponseEntity<?> listarHorariosDisponiveis(
            @PathVariable Long quadraId,
            @RequestParam String data
    ) {
        try {
            System.out.println("=== ENDPOINT HORÁRIOS DISPONÍVEIS CHAMADO ===");
            System.out.println("Quadra ID: " + quadraId);
            System.out.println("Data: " + data);
            
            var horarios = quadraService.listarHorariosDisponiveis(quadraId, data);
            
            System.out.println("Retornando " + horarios.size() + " horários");
            return ResponseEntity.ok(horarios);
            
        } catch (IllegalArgumentException e) {
            System.err.println("Erro de validação: " + e.getMessage());
            return ResponseEntity.badRequest().body(java.util.Collections.singletonMap("error", e.getMessage()));
        } catch (Exception e) {
            System.err.println("Erro interno: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(java.util.Collections.singletonMap("error", "Erro interno: " + e.getMessage()));
        }
    }

    @GetMapping("/imagens/{filename:.+}")
    public ResponseEntity<Resource> servirImagem(@PathVariable String filename) {
        try {
            Path file = Paths.get("C:/Users/marco/OneDrive/Documentos/site 2/frontend/public/quadras/").resolve(filename);
            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() && resource.isReadable()) {
                // Detectar o tipo de conteúdo baseado na extensão
                String contentType = Files.probeContentType(file);
                if (contentType == null) {
                    // Fallback para detectar por extensão
                    String fileName = filename.toLowerCase();
                    if (fileName.endsWith(".png")) {
                        contentType = "image/png";
                    } else if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
                        contentType = "image/jpeg";
                    } else if (fileName.endsWith(".gif")) {
                        contentType = "image/gif";
                    } else if (fileName.endsWith(".svg")) {
                        contentType = "image/svg+xml";
                    } else {
                        contentType = "application/octet-stream";
                    }
                }

                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (MalformedURLException e) {
            return ResponseEntity.badRequest().build();
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/test")
    public ResponseEntity<String> testeConexao(@RequestBody String teste) {
        return ResponseEntity.ok("Conexão funcionando: " + teste);
    }
    
    @GetMapping("/debug/reservas/{quadraId}")
    public ResponseEntity<?> debugReservas(@PathVariable Long quadraId, @RequestParam String data) {
        try {
            LocalDate dataReserva = LocalDate.parse(data);
            
            // Buscar todas as reservas para esta quadra nesta data
            List<Reserva> reservas = reservaRepository.findAll().stream()
                .filter(r -> r.getQuadra().getId().equals(quadraId) && 
                           r.getHorario().getData().equals(dataReserva))
                .collect(Collectors.toList());
            
            Map<String, Object> debug = new HashMap<>();
            debug.put("quadraId", quadraId);
            debug.put("data", data);
            debug.put("dataAtual", LocalDate.now().toString());
            debug.put("horaAtual", LocalDateTime.now().toString());
            debug.put("totalReservas", reservas.size());
            debug.put("reservas", reservas.stream().map(r -> {
                Map<String, Object> reservaInfo = new HashMap<>();
                reservaInfo.put("id", r.getId());
                reservaInfo.put("usuarioId", r.getUsuario().getId());
                reservaInfo.put("usuarioNome", r.getUsuario().getNome());
                reservaInfo.put("horaInicio", r.getHorario().getHoraInicio());
                reservaInfo.put("horaFim", r.getHorario().getHoraFim());
                reservaInfo.put("dataReserva", r.getHorario().getData().toString());
                return reservaInfo;
            }).collect(Collectors.toList()));
            
            // Testar consultas específicas para cada horário
            Map<String, Boolean> testesHorarios = new HashMap<>();
            for (int hora = 6; hora <= 22; hora++) {
                boolean ocupado = reservaRepository.existsByQuadraIdAndDataAndHorario(
                    quadraId, dataReserva, (float) hora, (float) (hora + 1)
                );
                testesHorarios.put(hora + ":00-" + (hora + 1) + ":00", ocupado);
            }
            debug.put("testesHorarios", testesHorarios);
            
            return ResponseEntity.ok(debug);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro: " + e.getMessage());
        }
    }

    // Novo endpoint para debug de regras de negócio (NOVA REGRA: apenas 1 reserva por dia)
    @GetMapping("/debug/regras-usuario/{usuarioId}")
    public ResponseEntity<?> debugRegrasUsuario(@PathVariable Long usuarioId, @RequestParam String data, @RequestParam(required = false) String modalidade) {
        try {
            LocalDate dataReserva = LocalDate.parse(data);
            
            // NOVA REGRA: Testar se usuário já tem QUALQUER reserva na data
            boolean jaTemReservaNaData = reservaRepository.existsByUsuarioIdAndData(
                usuarioId, dataReserva
            );
            
            // REGRA ANTIGA: Testar regra por tipo de quadra (manter para comparação)
            boolean jaTemReservaDoTipo = false;
            if (modalidade != null) {
                jaTemReservaDoTipo = reservaRepository.existsByUsuarioIdAndDataAndModalidade(
                    usuarioId, dataReserva, modalidade
                );
            }
            
            // Buscar todas as reservas do usuário nesta data
            List<Reserva> reservasUsuario = reservaRepository.findByUsuarioIdAndData(usuarioId, dataReserva);
            
            Map<String, Object> debug = new HashMap<>();
            debug.put("usuarioId", usuarioId);
            debug.put("data", data);
            debug.put("modalidadeTeste", modalidade);
            debug.put("NOVA_REGRA_jaTemReservaNaData", jaTemReservaNaData);
            debug.put("REGRA_ANTIGA_jaTemReservaDoTipo", jaTemReservaDoTipo);
            debug.put("totalReservasNaData", reservasUsuario.size());
            debug.put("podeReservar", !jaTemReservaNaData ? "✅ SIM - Primeira reserva do dia" : "❌ NÃO - Já tem reserva hoje");
            debug.put("reservasNaData", reservasUsuario.stream().map(r -> {
                Map<String, Object> info = new HashMap<>();
                info.put("id", r.getId());
                info.put("quadraId", r.getQuadra().getId());
                info.put("quadraNumero", r.getQuadra().getNumero());
                info.put("modalidadeQuadra", r.getQuadra().getModalidade());
                info.put("horaInicio", r.getHorario().getHoraInicio());
                info.put("horaFim", r.getHorario().getHoraFim());
                return info;
            }).collect(Collectors.toList()));
            
            return ResponseEntity.ok(debug);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro: " + e.getMessage());
        }
    }
}