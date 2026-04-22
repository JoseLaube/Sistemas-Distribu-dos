from zeep import Client

wsdl = 'http://localhost:8080/wsporto?wsdl'
client = Client(wsdl=wsdl)

# O Zeep lê o WSDL em tempo de execução e cria a função 'embarcar'
print("--- Teste via Python ---")
resultado = client.service.embarcar("OTIMIZAR")

print(f"Resultado da Otimização vindo do Middleware: {resultado * 100}%")

relatorio = client.service.relatorio_embarque()
print(f"\nRelatório:\n{relatorio}")