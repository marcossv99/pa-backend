package com.example.backclub.service;

import com.example.backclub.domain.entity.Associado;
import com.example.backclub.dto.request.CadastroAssociadoRequestDto;
import com.example.backclub.dto.response.AuthResponseDto;
import com.example.backclub.repository.AssociadoRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AuthService {
    
    private final AssociadoRepository associadoRepository;
    
    // Diretório para upload de imagens de perfil (pasta pública do frontend)
    private final String uploadDir = System.getProperty("user.dir") + "/frontend/public/home-socio/";
    
    // Armazena tokens ativos na memória (em produção, usar Redis ou BD)
    private final Map<String, TokenInfo> tokensAtivos = new ConcurrentHashMap<>();
    
    public AuthService(AssociadoRepository associadoRepository) {
        this.associadoRepository = associadoRepository;
        // Criar diretório de upload se não existir
        try {
            Files.createDirectories(Paths.get(uploadDir));
        } catch (IOException e) {
            System.err.println("Erro ao criar diretório de upload: " + e.getMessage());
        }
    }
    
    public AuthResponseDto login(String email, String senha) {
        System.out.println("Tentando login para: " + email);
        
        // Buscar usuário por email
        Associado usuario = associadoRepository.findByEmail(email)
            .orElseThrow(() -> new IllegalArgumentException("Email ou senha incorretos"));
        
        // Verificar senha (em produção, usar hash)
        if (!usuario.getSenha().equals(senha)) {
            throw new IllegalArgumentException("Email ou senha incorretos");
        }
        
        // Gerar token
        String token = gerarToken(usuario);
        
        // Armazenar token ativo
        tokensAtivos.put(token, new TokenInfo(usuario.getId(), usuario.isAdmin(), System.currentTimeMillis()));
        
        System.out.println("Login realizado com sucesso para: " + usuario.getNome() + " (isAdmin: " + usuario.isAdmin() + ")");
        
        return new AuthResponseDto(
            token,
            usuario.getId(),
            usuario.getNome(),
            usuario.getEmail(),
            usuario.isAdmin(),
            usuario.getTelefone(),
            usuario.getFotoPerfil()
        );
    }
    
    public AuthResponseDto cadastrarAssociado(CadastroAssociadoRequestDto dto) {
        // Verificar se email já existe
        if (associadoRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email já cadastrado no sistema");
        }
        
        // Verificar se CPF já existe
        if (associadoRepository.findByCpf(dto.getCpf()).isPresent()) {
            throw new IllegalArgumentException("CPF já cadastrado no sistema");
        }
        
        // Criar novo associado
        Associado novoAssociado = new Associado();
        novoAssociado.setNome(dto.getNome());
        novoAssociado.setEmail(dto.getEmail());
        novoAssociado.setSenha(dto.getSenha()); // Em produção, usar hash
        novoAssociado.setTelefone(dto.getTelefone());
        novoAssociado.setCpf(dto.getCpf());
        novoAssociado.setAdmin(false); // Associado comum
        
        Associado associadoSalvo = associadoRepository.save(novoAssociado);
        
        System.out.println("Associado cadastrado com sucesso: " + associadoSalvo.getNome());
        
        return new AuthResponseDto(
            null, // Não retorna token no cadastro
            associadoSalvo.getId(),
            associadoSalvo.getNome(),
            associadoSalvo.getEmail(),
            associadoSalvo.isAdmin(),
            associadoSalvo.getTelefone(),
            associadoSalvo.getFotoPerfil()
        );
    }
    
    public AuthResponseDto cadastrarAdmin(CadastroAssociadoRequestDto dto) {
        // Verificar se email já existe
        if (associadoRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email já cadastrado no sistema");
        }
        
        // Verificar se CPF já existe
        if (associadoRepository.findByCpf(dto.getCpf()).isPresent()) {
            throw new IllegalArgumentException("CPF já cadastrado no sistema");
        }
        
        // Criar novo admin
        Associado novoAdmin = new Associado();
        novoAdmin.setNome(dto.getNome());
        novoAdmin.setEmail(dto.getEmail());
        novoAdmin.setSenha(dto.getSenha()); // Em produção, usar hash
        novoAdmin.setTelefone(dto.getTelefone());
        novoAdmin.setCpf(dto.getCpf());
        novoAdmin.setAdmin(true); // É admin
        
        System.out.println("Criando admin com isAdmin = " + novoAdmin.isAdmin());
        
        Associado adminSalvo = associadoRepository.save(novoAdmin);
        
        System.out.println("Admin cadastrado com sucesso: " + adminSalvo.getNome());
        System.out.println("Admin salvo com isAdmin = " + adminSalvo.isAdmin());
        System.out.println("Admin salvo com getTipo() = " + adminSalvo.getTipo());
        
        return new AuthResponseDto(
            null, // Não retorna token no cadastro
            adminSalvo.getId(),
            adminSalvo.getNome(),
            adminSalvo.getEmail(),
            adminSalvo.isAdmin(),
            adminSalvo.getTelefone(),
            adminSalvo.getFotoPerfil()
        );
    }
    
    public AuthResponseDto validarToken(String token) {
        token = extrairToken(token);
        TokenInfo tokenInfo = tokensAtivos.get(token);
        
        if (tokenInfo == null) {
            throw new IllegalArgumentException("Token inválido ou expirado");
        }
        
        Associado usuario = associadoRepository.findById(tokenInfo.getUserId())
            .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));
        
        return new AuthResponseDto(
            token,
            usuario.getId(),
            usuario.getNome(),
            usuario.getEmail(),
            usuario.isAdmin(),
            usuario.getTelefone(),
            usuario.getFotoPerfil()
        );
    }
    
    public AuthResponseDto obterPerfil(String token) {
        token = extrairToken(token);
        TokenInfo tokenInfo = tokensAtivos.get(token);
        
        if (tokenInfo == null) {
            throw new IllegalArgumentException("Token inválido ou expirado");
        }
        
        Associado usuario = associadoRepository.findById(tokenInfo.getUserId())
            .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));
        
        return new AuthResponseDto(
            token,
            usuario.getId(),
            usuario.getNome(),
            usuario.getEmail(),
            usuario.isAdmin(),
            usuario.getTelefone(),
            usuario.getFotoPerfil()
        );
    }
    
    public AuthResponseDto atualizarPerfil(String token, CadastroAssociadoRequestDto dto) {
        token = extrairToken(token);
        TokenInfo tokenInfo = tokensAtivos.get(token);
        
        if (tokenInfo == null) {
            throw new IllegalArgumentException("Token inválido ou expirado");
        }
        
        Associado usuario = associadoRepository.findById(tokenInfo.getUserId())
            .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));
        
        // Verificar se email já existe (exceto o próprio usuário)
        associadoRepository.findByEmail(dto.getEmail()).ifPresent(existente -> {
            if (!existente.getId().equals(usuario.getId())) {
                throw new IllegalArgumentException("Email já está em uso por outro usuário");
            }
        });
        
        // Atualizar dados do usuário
        usuario.setNome(dto.getNome());
        usuario.setEmail(dto.getEmail());
        usuario.setTelefone(dto.getTelefone());
        
        // Atualizar senha apenas se fornecida
        if (dto.getSenha() != null && !dto.getSenha().trim().isEmpty()) {
            usuario.setSenha(dto.getSenha());
        }
        
        Associado usuarioAtualizado = associadoRepository.save(usuario);
        
        return new AuthResponseDto(
            token,
            usuarioAtualizado.getId(),
            usuarioAtualizado.getNome(),
            usuarioAtualizado.getEmail(),
            usuarioAtualizado.isAdmin(),
            usuarioAtualizado.getTelefone(),
            usuarioAtualizado.getFotoPerfil()
        );
    }
    
    public String uploadFotoPerfil(String token, MultipartFile file) {
        token = extrairToken(token);
        TokenInfo tokenInfo = tokensAtivos.get(token);
        
        if (tokenInfo == null) {
            throw new IllegalArgumentException("Token inválido ou expirado");
        }
        
        Associado usuario = associadoRepository.findById(tokenInfo.getUserId())
            .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));
        
        // Validações do arquivo
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Arquivo não pode estar vazio");
        }
        
        // Verificar tipo de arquivo
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("Arquivo deve ser uma imagem");
        }
        
        // Verificar tamanho (máximo 20MB)
        if (file.getSize() > 20 * 1024 * 1024) {
            throw new IllegalArgumentException("Arquivo deve ter no máximo 20MB");
        }
        
        try {
            // Gerar nome único para o arquivo
            String extensao = getFileExtension(file.getOriginalFilename());
            String nomeArquivo = "perfil_" + usuario.getId() + "_" + System.currentTimeMillis() + extensao;
            
            // Salvar arquivo
            Path caminho = Paths.get(uploadDir + nomeArquivo);
            Files.copy(file.getInputStream(), caminho, StandardCopyOption.REPLACE_EXISTING);

            // Atualizar usuário com nova foto
            String urlFoto = "/home-socio/" + nomeArquivo;
            usuario.setFotoPerfil(urlFoto);
            associadoRepository.save(usuario);

            return urlFoto;
            
        } catch (IOException e) {
            throw new RuntimeException("Erro ao salvar arquivo: " + e.getMessage());
        }
    }
    
    public boolean isAdmin(String token) {
        try {
            token = extrairToken(token);
            TokenInfo tokenInfo = tokensAtivos.get(token);
            return tokenInfo != null && tokenInfo.isAdmin();
        } catch (Exception e) {
            return false;
        }
    }
    
    public boolean isAssociado(String token) {
        try {
            token = extrairToken(token);
            TokenInfo tokenInfo = tokensAtivos.get(token);
            return tokenInfo != null && !tokenInfo.isAdmin();
        } catch (Exception e) {
            return false;
        }
    }
    
    public void logout(String token) {
        token = extrairToken(token);
        tokensAtivos.remove(token);
        System.out.println("Logout realizado, token removido");
    }
    
    public Long obterUsuarioIdDoToken(String token) {
        token = extrairToken(token);
        TokenInfo tokenInfo = tokensAtivos.get(token);
        if (tokenInfo == null) {
            throw new IllegalArgumentException("Token inválido ou expirado");
        }
        return tokenInfo.getUserId();
    }
    
    private String gerarToken(Associado usuario) {
        String dadosToken = usuario.getId() + ":" + usuario.getEmail() + ":" + System.currentTimeMillis() + ":" + UUID.randomUUID();
        return Base64.getEncoder().encodeToString(dadosToken.getBytes());
    }
    
    private String getFileExtension(String filename) {
        if (filename == null || filename.lastIndexOf('.') == -1) {
            return ".jpg"; // Extensão padrão
        }
        return filename.substring(filename.lastIndexOf('.'));
    }
    
    private String extrairToken(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return authHeader;
    }
    
    // Classe interna para armazenar informações do token
    private static class TokenInfo {
        private final Long userId;
        private final boolean isAdmin;
        private final long criadoEm;
        
        public TokenInfo(Long userId, boolean isAdmin, long criadoEm) {
            this.userId = userId;
            this.isAdmin = isAdmin;
            this.criadoEm = criadoEm;
        }
        
        public Long getUserId() {
            return userId;
        }
        
        public boolean isAdmin() {
            return isAdmin;
        }
        
    }
}