# O Zeep é o equivalente ao javax.xml.ws.Service do Java
from zeep import Client

try:
    print("[Cliente] Conectando ao porto via SOAP...")
    
    wsdl_url = 'http://localhost:8080/wsporto?wsdl'
    
    # 2. Equivalente ao 'Service.create(url, qname)' e 'getPort(...)'
    # O Zeep faz isso em uma linha só!
    cliente = Client(wsdl=wsdl_url)
    
    print("[Cliente] Conexão estabelecida! Solicitando otimização...")
    
    # 3. Equivalente ao 'server.sayHello(name)' ou 'porto.embarcar("OTIMIZAR")'
    resultado = cliente.service.embarcar("OTIMIZAR")
    
    print(f"[Cliente] Sucesso! Eficiência: {resultado * 100}%")
    
    print("\n[Cliente] Solicitando relatório de embarque...")
    relatorio = cliente.service.relatorio_embarque()
    print(relatorio)

except Exception as e:
    print(f"[Cliente] Erro na comunicação SOAP: {e}")
