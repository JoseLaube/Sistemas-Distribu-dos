import sys
from zeep import Client
from zeep.transports import Transport
import requests

# Use 127.0.0.1 para garantir que ele vá pelo IPv4 que o seu Java abriu
WSDL_URL = "http://127.0.0.1:8080/wsporto?wsdl"

def conectar_ws():
   
    try:
        # Criamos uma sessão simples do requests
        session = requests.Session()
        # O segredo é passar a session dentro do objeto Transport do Zeep
        transport = Transport(session=session, timeout=10)
        
        cliente = Client(wsdl=WSDL_URL, transport=transport)
        
        print("Conectado com sucesso!")
        return cliente
    except Exception as e:
        print(f"Erro ao carregar WSDL: {e}")
        print("\nDica: Verifique se o Middleware Java imprimiu 'Middleware SOAP Rodando'.")
        sys.exit(1)

def menu():
    cliente = conectar_ws()
    
    while True:
        print("\n" + "="*40)
        print("      SISTEMA PORTUÁRIO - CLIENTE")
        print("="*40)
        print("1. Otimizar Embarque")
        print("2. Ver Relatório")
        print("3. Sair")
        print("="*40)
        
        opcao = input("Escolha uma opção: ").strip()

        if opcao == '1':
            print("\nEnviando comando OTIMIZAR...")
            try:
                # Chama o método embarcar do seu Java
                resultado = cliente.service.embarcar("OTIMIZAR")
                print(f"Eficiência da Otimização: {resultado * 100:.2f}%")
            except Exception as e:
                print(f"Erro na requisição: {e}")
                
        elif opcao == '2':
            print("\nBuscando relatório...")
            try:
                relatorio = cliente.service.relatorio_embarque()
                print(f"\nSTATUS DO PORTO:\n{relatorio}")
            except Exception as e:
                print(f"Erro ao buscar relatório: {e}")
                
        elif opcao == '3':
            print("Saindo...")
            break
        else:
            print("Opção inválida!")

if __name__ == "__main__":
    menu()
