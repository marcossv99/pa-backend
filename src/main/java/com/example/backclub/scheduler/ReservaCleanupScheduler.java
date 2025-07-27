package com.example.backclub.scheduler;

import com.example.backclub.service.ReservaService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ReservaCleanupScheduler {

    private final ReservaService reservaService;

    public ReservaCleanupScheduler(ReservaService reservaService) {
        this.reservaService = reservaService;
    }

    // Executa todo dia às 3h da manhã
    @Scheduled(cron = "0 0 3 * * *")
    public void limparReservasCanceladasAntigas() {
        int removidas = reservaService.limparReservasCanceladasAntigas(30); // 30 dias
        if (removidas > 0) {
            System.out.println("[ReservaCleanupScheduler] Reservas canceladas removidas: " + removidas);
        }
    }
}

