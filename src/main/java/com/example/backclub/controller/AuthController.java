package com.example.backclub.controller;

import com.example.backclub.dto.request.LoginRequestDto;
import com.example.backclub.dto.request.CadastroAssociadoRequestDto;
import com.example.backclub.dto.response.AuthResponseDto;
import com.example.backclub.service.AuthService;
import com.example.backclub.domain.entity.Associado;
import com.example.backclub.repository.AssociadoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;

import java.util.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:4200")
public class AuthController {
    
    private final AuthService authService;
    private final AssociadoRepository associadoRepository;
    
    public AuthController(AuthService authService, AssociadoRepository associadoRepository) {
        this.authService = authService;
        this.associadoRepository = associadoRepository;
    }
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDto dto) {
        try {
            System.out.println("Tentativa de login: " + dto.getEmail());
            AuthResponseDto response = authService.login(dto.getEmail(), dto.getSenha());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            System.err.println("Erro de login: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Collections.singletonMap("error", e.getMessage()));
        } catch (Exception e) {
            System.err.println("Erro interno no login: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Collections.singletonMap("error", "Erro interno do servidor"));
        }
    }
    
    @PostMapping("/cadastrar-associado")
    public ResponseEntity<?> cadastrarAssociado(@RequestBody CadastroAssociadoRequestDto dto, 
                                               @RequestHeader("Authorization") String token) {
        try {
            System.out.println("Cadastro de associado por admin");
            
            // Verificar se quem está fazendo a requisição é admin
            if (!authService.isAdmin(token)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Collections.singletonMap("error", "Apenas administradores podem cadastrar associados"));
            }
            
            AuthResponseDto response = authService.cadastrarAssociado(dto);
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(Collections.singletonMap("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Collections.singletonMap("error", "Erro interno do servidor"));
        }
    }
    
    @PostMapping("/cadastrar-admin")
    public ResponseEntity<?> cadastrarAdmin(@RequestBody CadastroAssociadoRequestDto dto,
                                          @RequestHeader("Authorization") String token) {
        try {
            System.out.println("Cadastro de admin por admin");
            
            // Verificar se quem está fazendo a requisição é admin
            if (!authService.isAdmin(token)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Collections.singletonMap("error", "Apenas administradores podem cadastrar outros administradores"));
            }
            
            AuthResponseDto response = authService.cadastrarAdmin(dto);
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(Collections.singletonMap("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Collections.singletonMap("error", "Erro interno do servidor"));
        }
    }
    
    @PostMapping("/validar-token")
    public ResponseEntity<?> validarToken(@RequestHeader("Authorization") String token) {
        try {
            AuthResponseDto response = authService.validarToken(token);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Collections.singletonMap("error", "Token inválido"));
        }
    }
    
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String token) {
        try {
            authService.logout(token);
            return ResponseEntity.ok(Collections.singletonMap("message", "Logout realizado com sucesso"));
        } catch (Exception e) {
            return ResponseEntity.ok(Collections.singletonMap("message", "Logout realizado"));
        }
    }
    
    @GetMapping("/perfil")
    public ResponseEntity<?> obterPerfil(@RequestHeader("Authorization") String token) {
        try {
            AuthResponseDto response = authService.obterPerfil(token);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Collections.singletonMap("error", "Token inválido"));
        }
    }
    
    @PutMapping("/perfil")
    public ResponseEntity<?> atualizarPerfil(@RequestHeader("Authorization") String token,
                                            @RequestBody CadastroAssociadoRequestDto dto) {
        try {
            AuthResponseDto response = authService.atualizarPerfil(token, dto);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(Collections.singletonMap("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Collections.singletonMap("error", "Erro interno do servidor"));
        }
    }
    
    @PostMapping("/perfil/foto")
    public ResponseEntity<?> uploadFotoPerfil(@RequestHeader("Authorization") String token,
                                             @RequestParam("file") MultipartFile file) {
        try {
            String urlFoto = authService.uploadFotoPerfil(token, file);
            return ResponseEntity.ok(Collections.singletonMap("fotoUrl", urlFoto));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(Collections.singletonMap("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Collections.singletonMap("error", "Erro interno do servidor"));
        }
    }
    
    @PostMapping("/criar-usuarios-teste")
    public ResponseEntity<?> criarUsuariosTeste() {
        try {
            // Verificar se já existem usuários
            if (associadoRepository.count() > 0) {
                // Mostrar usuários existentes
                List<Map<String, String>> usuarios = new ArrayList<>();
                associadoRepository.findAll().forEach(user -> {
                    Map<String, String> userData = new HashMap<>();
                    userData.put("tipo", user.getTipo());
                    userData.put("email", user.getEmail());
                    userData.put("senha", user.getSenha());
                    userData.put("nome", user.getNome());
                    userData.put("isAdmin", String.valueOf(user.isAdmin()));
                    usuarios.add(userData);
                });
                
                Map<String, Object> response = new HashMap<>();
                response.put("message", "Usuários já existem no sistema");
                response.put("usuarios", usuarios);
                return ResponseEntity.ok(response);
            }
            
            // Criar Admin
            Associado admin = new Associado();
            admin.setNome("Admin Sistema");
            admin.setCpf("12345678901");
            admin.setEmail("admin@clubesportivo.com");
            admin.setSenha("admin123");
            admin.setTelefone("(11) 98765-4321");
            admin.setAdmin(true); // É admin
            
            admin = associadoRepository.save(admin);
            
            // Criar Associado
            Associado associado = new Associado();
            associado.setNome("João Silva Santos");
            associado.setCpf("98765432100");
            associado.setEmail("joao.silva@email.com");
            associado.setSenha("joao123");
            associado.setTelefone("(11) 99999-8888");
            associado.setAdmin(false); // Não é admin
            
            associado = associadoRepository.save(associado);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Usuários de teste criados com sucesso!");
            response.put("admin", Map.of(
                "email", "admin@clubesportivo.com",
                "senha", "admin123",
                "nome", admin.getNome()
            ));
            response.put("associado", Map.of(
                "email", "joao.silva@email.com",
                "senha", "joao123",
                "nome", associado.getNome()
            ));
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Collections.singletonMap("error", "Erro ao criar usuários: " + e.getMessage()));
        }
    }
    
    @GetMapping("/perfil/imagens/{filename:.+}")
    public ResponseEntity<Resource> servirImagemPerfil(@PathVariable String filename) {
        try {
            Path file = Paths.get("pa-backend-novo-cod/uploads/perfil/").resolve(filename);
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
}

