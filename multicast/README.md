# Monitoramento de Falhas via Multicast

### **EQUIPE:**
- Gustavo de Souza.
- José Augusto Laube.

## Descrição do Projeto
Este projeto implementa um sistema distribuído de monitoramento de presença e detecção de falhas utilizando o protocolo **UDP Multicast**. O sistema é composto por nós que anunciam sua presença (Heartbeat) e monitores que analisam a frequência dessas mensagens para calcular um **Índice de Suspeito**.

O objetivo é identificar nós que possam ter caído ou que estejam com instabilidade de rede dentro de uma janela de tempo específica.

## Arquitetura do Sistema

O sistema divide-se em dois componentes principais:

1.  **Monitor (reciever_complete.py):**
    *   Utiliza **Threads** para realizar duas tarefas simultâneas:
        *   **Thread de Escuta:** Captura pacotes no grupo multicast, ignorando as próprias mensagens e pacotes de relatório para evitar contagem duplicada.
        *   **Gerente/Sender:** A cada intervalo definido, envia sua presença e, ao final de uma janela (Delta), processa o mapa de nós detectados.
    *   **Cálculo de Suspeito:** Baseia-se na fórmula `max(0, 1 - (recebidas / esperado))`.
    *   **Reset de Janela:** Implementa uma janela fixa onde os contadores são zerados após cada análise para garantir que a detecção seja atualizada.

2.  **Nó Emissor (sender.py):**
    *   Um script simplificado que atua como um nó da rede, enviando pacotes de presença (`datetime, IP`) em um intervalo fixo.

## Como Executar

### 1. Pré-requisitos
*   Python 3.x instalado.
*   Conexão de rede que suporte tráfego Multicast (redes locais Wi-Fi/Cabo geralmente suportam).

### 2. Configuração de IP e Porta
Para que os códigos se comuniquem, certifique-se de que as variáveis no topo de ambos os arquivos coincidam:
*   `GRUPO_MULTICAST`: Deve ser o mesmo (ex: `224.0.0.2`).
*   `PORTA`: Deve ser a mesma (ex: `8888`).
*   `INTERVALO_ENVIO`: No monitor, deve ser igual ao `delta` definido no `sender.py`.

### 3. Execução

1.  **Inicie o monitor principal:**
    ```bash
    python reciever_complete.py
    ```
    *O monitor exibirá o log de mensagens recebidas e, a cada 30 segundos, imprimirá a tabela de análise.*

2.  **Inicie um ou mais emissores (em outros terminais ou máquinas):**
    ```bash
    python sender.py
    ```
    *O emissor começará a "gritar" sua presença na rede.*

## Formato da Saída (Mapa)
Conforme a solicitação do trabalho, o mapa gerado e enviado para a rede segue o padrão:
`datetime, delta, ip_dest, suspeito`

*   **Exemplo de log no terminal:**
    ```text
    ========================================
    --- ANÁLISE DA JANELA (30.0s) ---
    Horário: 14:30:05 | Esperado: 3.0
    ========================================
    IP: 192.168.1.15 | Recebidas: 3 | Suspeito: 0.0000
    IP: 192.168.1.20 | Recebidas: 1 | Suspeito: 0.6667
    ========================================
    ```

---
**Desenvolvido para a disciplina de Sistemas Distribuídos.**
