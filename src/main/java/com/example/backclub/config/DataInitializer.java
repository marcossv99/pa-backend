package com.example.backclub.config;

import com.example.backclub.domain.entity.Associado;
import com.example.backclub.repository.AssociadoRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final AssociadoRepository associadoRepository;

    public DataInitializer(AssociadoRepository associadoRepository) {
        this.associadoRepository = associadoRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // Verificar se já existem usuários no sistema
        if (associadoRepository.count() == 0) {
            System.out.println("Inicializando dados de teste...");
            
            // Criar Admin padrão
            Associado admin = new Associado();
            admin.setNome("Admin Sistema");
            admin.setCpf("12345678901");
            admin.setEmail("admin@clubesportivo.com");
            admin.setSenha("admin123");
            admin.setTelefone("(11) 98765-4321");
            admin.setAdmin(true); // É admin
            
            associadoRepository.save(admin);
            System.out.println("✅ Admin criado: " + admin.getEmail());
            
            // Criar Associado padrão
            Associado associado = new Associado();
            associado.setNome("João Silva Santos");
            associado.setCpf("98765432100");
            associado.setEmail("joao.silva@email.com");
            associado.setSenha("joao123");
            associado.setTelefone("(11) 99999-8888");
            associado.setAdmin(false); // Não é admin
            
            associadoRepository.save(associado);
            System.out.println("✅ Associado criado: " + associado.getEmail());
            
            System.out.println("=== DADOS DE LOGIN CRIADOS ===");
            System.out.println("ADMIN:");
            System.out.println("  Email: admin@clubesportivo.com");
            System.out.println("  Senha: admin123");
            System.out.println("");
            System.out.println("ASSOCIADO:");
            System.out.println("  Email: joao.silva@email.com");
            System.out.println("  Senha: joao123");
            System.out.println("=============================");
        } else {
            System.out.println("Dados já existem no banco. Pulando inicialização.");
            
            // Mostrar usuários existentes
            System.out.println("=== USUÁRIOS DISPONÍVEIS NO SISTEMA ===");
            associadoRepository.findAll().forEach(user -> {
                System.out.println(user.getTipo() + ": " + user.getEmail() + " (Senha: " + user.getSenha() + ")");
            });
            System.out.println("=====================================");
        }
    }
}
