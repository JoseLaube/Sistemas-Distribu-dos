package porto;

import javax.xml.ws.Endpoint;

public class PublicadorWS {
    public static void main(String[] args) {
        
        String url = "http://10.20.221.235:8080/porto";
        // String url = "http://localhost:8080/porto";
        System.out.println("Iniciando o Middleware (SOAP <-> RMI)...");
        
        try {
            WSPortoServerImpl implementacao = new WSPortoServerImpl();
            
            // Tenta se conectar com o PortoServer 3 vezes
            int tentativas = 0;
            int maxTentativas = 3;
            
            while (!implementacao.isConectado() && tentativas < maxTentativas) {
                tentativas++;
                System.out.println("[Middleware] Retentativa " + tentativas + " de " + maxTentativas + " em 5 segundos...");
                Thread.sleep(15000);     // Aguarda 5 segundos
                implementacao.conectarAoRMI(); // Tenta de novo
            }
            
            // Se não conseguiu aborta
            if (!implementacao.isConectado()) {
                System.err.println("==================================================");
                System.err.println("FALHA CRÍTICA: O Backend RMI não está respondendo.");
                System.err.println("O Middleware SOAP NÃO será iniciado por segurança.");
                System.err.println("Inicie o 'PortoServer' primeiro e tente novamente.");
                System.err.println("==================================================");
                System.exit(1);
            }
            
            // Se chegou aqui, é porque a conexão deu certo. Publica a porta 8080.
            Endpoint.publish(url, implementacao);
            
            System.out.println("==================================================");
            System.out.println("Middleware SOAP Rodando e Pronto para Uso!");
            System.out.println("Contrato WSDL: " + url + "?wsdl");
            System.out.println("==================================================");
            
        } catch (Exception e) {
            System.err.println("Erro fatal no Middleware: " + e.getMessage());
            e.printStackTrace();
        }
    }
}