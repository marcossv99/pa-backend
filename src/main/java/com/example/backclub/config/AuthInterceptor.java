package com.example.backclub.config;

import com.example.backclub.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthInterceptor implements HandlerInterceptor {
    
    private final AuthService authService;
    
    public AuthInterceptor(AuthService authService) {
        this.authService = authService;
    }
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String path = request.getRequestURI();
        String method = request.getMethod();
        
        // Permitir OPTIONS (CORS preflight)
        if ("OPTIONS".equals(method)) {
            return true;
        }
        
        // Rotas públicas - não precisam de autenticação
        if (isPublicRoute(path)) {
            return true;
        }
        
        // Verificar token
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(401);
            response.getWriter().write("{\"error\":\"Token de autenticação necessário\"}");
            response.setContentType("application/json");
            return false;
        }
        
        try {
            // Validar token
            authService.validarToken(authHeader);
            
            // Verificar permissões específicas
            if (isAdminRoute(path)) {
                if (!authService.isAdmin(authHeader)) {
                    response.setStatus(403);
                    response.getWriter().write("{\"error\":\"Acesso negado. Apenas administradores podem acessar esta rota\"}");
                    response.setContentType("application/json");
                    return false;
                }
            }
            
            return true;
            
        } catch (Exception e) {
            response.setStatus(401);
            response.getWriter().write("{\"error\":\"Token inválido ou expirado\"}");
            response.setContentType("application/json");
            return false;
        }
    }
    
    private boolean isPublicRoute(String path) {
        return path.startsWith("/api/auth/login") ||
               path.startsWith("/api/auth/validar-token") ||
               path.startsWith("/api/auth/criar-usuarios-teste") ||
               path.startsWith("/api/imagens/perfil/") ||
               path.startsWith("/uploads/") ||
               path.startsWith("/pa-backend-novo-cod/uploads/") ||
               path.startsWith("/static/") ||
               path.startsWith("/public/") ||
               path.equals("/") ||
               path.startsWith("/favicon.ico");
    }
    
    private boolean isAdminRoute(String path) {
        return path.startsWith("/api/auth/cadastrar-") ||
               path.startsWith("/api/admin/") ||
               (path.startsWith("/api/quadras") && 
                (path.contains("/cadastrar") || path.contains("/atualizar") || path.contains("/deletar")));
    }
}
