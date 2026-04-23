---

# Sistema Distribuído de Logística Portuária (RMI & SOAP)

Este projeto é uma aplicação prática de **Sistemas Distribuídos** focada na otimização de embarque de cargas em navios. O sistema aplica conceitos do **Problema da Mochila (Knapsack Problem)** utilizando a heurística *Best Fit Decreasing*, e foi construído sob uma **Arquitetura Orientada a Serviços (SOA)** em 3 camadas (3-Tier), integrando tecnologias Java RMI e Web Services SOAP.

---

## Arquitetura do Sistema (Topologia em 3 Camadas)

O sistema foi desenhado para garantir **alto desempenho local** (via RMI) e **alta interoperabilidade global** (via SOAP HTTP). Isso garante que o motor logístico do porto (Java) possa ser consumido por clientes em qualquer linguagem de programação (Python, C#, etc).

```text
[ CAMADA 1: FRONTEND ]        [ CAMADA 2: MIDDLEWARE ]         [ CAMADA 3: BACKEND ]
(Cliente em Qualquer L.)      (Tradutor - Porta 8080)          (Motor - Porta 6600)
                              
+-----------------------+      +---------------------------+      +-----------------------+
|                       |      |                           |      |                       |
|  1. Dispara Comando   |      |  1. Intercepta HTTP/XML   |      |  1. Recebe Bytecode   |
|     (Ex: Embarcar)    | HTTP |  2. Extrai Parâmetros     | TCP  |  2. Executa Algoritmo |
|          |            |======|  3. Aciona Conexão Local  |======|     da Mochila (BFD)  |
|          v            | SOAP |     (portoRMI.embarcar)   | RMI  |          |            |
|  2. Aguarda Resposta  |      |            |              |      |          v            |
|          ^            |      |            v              |      |  3. Salva no HD (TXT) |
|          |            |======|  4. Recebe Retorno Java   |======|  4. Devolve o cálculo |
|  3. Lê Resposta XML   | HTTP |  5. Reempacota para XML   | TCP  |     de Eficiência     |
|                       | SOAP |  6. Devolve via HTTP      | RMI  |                       |
+-----------------------+      +---------------------------+      +-----------------------+
```

---

## Organização do Projeto

A estrutura de pastas reflete estritamente o princípio de **Separação de Responsabilidades (SRP)**.

```text
MeuProjetoPorto/
│
├── shared/                  ➡️ Contratos e Modelos de Dados Universais
│   ├── Carga.java           # Modelo (ID, descrição, volume).
│   ├── Navio.java           # Modelo (ID, capacidade atual e original).
│   └── IServico.java        # Interface original exigida pelo RMI (extends Remote).
│
├── backend/                 ➡️ Camada de Lógica de Negócios e Persistência
│   ├── PortoServer.java     # Servidor RMI (Abre a porta 6600 e gerencia os Maps em memória).
│   └── Embarcar.java        # Motor Logístico: Executa a heurística Best Fit Decreasing e salva logs.
│
├── middleware/              ➡️ Camada de Tradução (Adapter Pattern)
│   ├── WSPortoServer.java       # Contrato SOAP (@WebService).
│   ├── WSPortoServerImpl.java   # Implementação: Conecta-se como cliente RMI e atua como Servidor SOAP.
│   └── PublicadorWS.java        # Inicializador: Instancia a ponte, realiza Retries e abre a porta HTTP 8080.
│
├── scripts_teste/           ➡️ Provas de Interoperabilidade
│   ├── request.xml          # Envelope SOAP cru para testes de rede.
│   └── cliente_teste.py     # Script Python provando o desacoplamento tecnológico.
│
└── log_embarques.txt        ➡️ Audit Log gerado automaticamente pelo servidor em operações bem-sucedidas.
```

---

## Pré-requisitos

Para compilar e executar o projeto nativamente via terminal Linux, é necessário:
*   **Java Development Kit (JDK) 8** (A versão 8 é necessária pois contém as bibliotecas nativas `javax.jws` e `javax.xml.ws` para Web Services SOAP).
*   *(Opcional)* Python 3 e biblioteca `zeep` para testes de interoperabilidade.

---

## Como Executar o Projeto

Siga os passos abaixo em terminais separados, sempre executando a partir da **pasta raiz** do projeto.

### 1. Compilar todo o projeto
```bash
javac shared/*.java backend/*.java middleware/*.java
```

### 2. Iniciar o Backend (O Cérebro do Porto)
No Terminal 1, inicie o servidor RMI:
```bash
java backend.PortoServer
```
*Saída Esperada: `### Porto Centralizado Pronto para Operações ###`*

### 3. Iniciar o Middleware (A Ponte SOAP)
No Terminal 2, suba o tradutor HTTP:
```bash
java middleware.PublicadorWS
```
*Saída Esperada:* O middleware tentará se conectar ao RMI na porta 6600. Se obtiver sucesso, publicará o WSDL na porta 8080 e exibirá a mensagem: `Middleware SOAP Rodando e Pronto para Uso!`.

---

## Testando a Interoperabilidade (Sem Java)

O grande trunfo desta arquitetura é que o cliente final não precisa saber que o sistema foi feito em Java RMI. Com o servidor Backend e o Middleware rodando, você pode testar a conexão das seguintes formas:

### A) Teste Direto pelo Terminal (via cURL)
Enviando um XML cru diretamente para o Middleware HTTP:
```bash
curl -v -H "Content-Type: text/xml; charset=utf-8" -d @scripts_teste/request.xml http://localhost:8080/wsporto
```
*(O arquivo `request.xml` deve conter um `<soapenv:Envelope>` formatado corretamente chamando o método).*

### B) Teste com Cliente Python
Crie um arquivo `cliente_teste.py` e execute para provar que a ponte abstrai a linguagem:
```python
from zeep import Client
client = Client('http://localhost:8080/wsporto?wsdl')

print("Injetando dados (Bootstrap)...")
client.service.embarcar("BOOTSTRAP")

print("Acionando o algoritmo do RMI via Python...")
eficiencia = client.service.embarcar("OTIMIZAR")
print(f"Resultado de Otimização: {eficiencia * 100}%")
```
Execute com: `python3 cliente_teste.py`.

---

## Conceitos Acadêmicos Aplicados

*   **Padrão Adapter:** A camada `middleware` adapta a interface de comunicação do RMI (Java-to-Java via TCP) para o padrão de mercado SOAP (Agnóstico via HTTP).
*   **Problema da Mochila (Knapsack) & Bin Packing:** Implementado na classe `Embarcar`. O algoritmo ordena as cargas e busca preencher as menores lacunas nos navios primeiro (**Best Fit Decreasing**), maximizando a fórmula de eficiência de consolidação exigida pelo professor `(Carga Total / Capacidade Utilizada)`.
*   **Resiliência (Fail-Fast):** O inicializador do Web Service (`PublicadorWS`) possui mecanismo de *Retry*. Ele tenta se conectar ao RMI 3 vezes com intervalos de 5 segundos. Se o RMI estiver inativo, ele aborta a inicialização para evitar que o Middleware receba requisições que não possam ser processadas.
*   **Auditoria por Arquivo Textual:** Todas as operações que alteram o estado logístico do porto gravam um registro atômico no arquivo `.txt`, garantindo histórico e segurança em caso de falha do processo principal.
