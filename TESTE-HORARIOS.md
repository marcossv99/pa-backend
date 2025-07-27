## Como testar os horários disponíveis

### 1. Iniciar o servidor:
```bash
.\mvnw.cmd spring-boot:run
```

### 2. Listar horários disponíveis para uma quadra:

**Para hoje (02/07/2025):**
```powershell
Invoke-RestMethod -Uri "http://localhost:8080/api/quadras/1/horarios-disponiveis?data=2025-07-02" -Method Get
```

**Para amanhã (03/07/2025):**
```powershell
Invoke-RestMethod -Uri "http://localhost:8080/api/quadras/1/horarios-disponiveis?data=2025-07-03" -Method Get
```

### 3. Fazer uma reserva para testar o bloqueio:
```powershell
$json = '{"quadraId": 1, "membros": ["João", "Maria"], "data": "2025-07-03", "horaInicio": 10.0, "horaFim": 11.0}'
Invoke-RestMethod -Uri "http://localhost:8080/api/reservas" -Method Post -Body $json -ContentType "application/json"
```

### 4. Verificar se o horário 10:00-11:00 agora aparece como "Já reservado":
```powershell
Invoke-RestMethod -Uri "http://localhost:8080/api/quadras/1/horarios-disponiveis?data=2025-07-03" -Method Get
```

### Exemplo de resposta:
```json
[
  {
    "horaInicio": 6,
    "horaFim": 7,
    "data": "2025-07-02",
    "disponivel": false,
    "status": "Horário já passou",
    "horarioFormatado": "06:00 - 07:00"
  },
  {
    "horaInicio": 14,
    "horaFim": 15,
    "data": "2025-07-02",
    "disponivel": true,
    "status": "Disponível",
    "horarioFormatado": "14:00 - 15:00"
  }
]
```
