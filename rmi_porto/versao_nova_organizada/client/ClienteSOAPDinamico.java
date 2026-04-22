package client;

import java.net.URL;
import java.security.Provider.Service;

import javax.xml.namespace.QName;

import middleware.WSPortoServer;

// Esse codigo para operar precisa de WSDL's (isso é chato)

public class ClienteSOAPDinamico {
    public static void main(String[] args) {
        System.out.println("=== CLIENTE SOAP DINÂMICO ===");

        try {
            // URL do WSDL
            URL url = new URL("http://localhost:8080/wsporto?wsdl");

            // Define os nomes técnicos (QName). 
            // Eles seguem o padrão: (Namespace/Pacote invertido, Nome da Classe + "Service")
            QName qname = new QName("http://middleware/", "WSPortoServerImplService");

            Service service = Service.create(url, qname);
            
            // Pega a interface
            WSPortoServer ponte = service.getPort(WSPortoServer.class);

            System.out.println("Conexão estabelecida! Testando métodos...");

            System.out.println("Relatório:");
            System.out.println(ponte.relatorio_embarque());

            System.out.println("Otimizando:");
            double eficiencia = ponte.embarcar("OTIMIZAR");
            
            System.out.println("Resultado da Otimização: " + (eficiencia * 100) + "%");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}