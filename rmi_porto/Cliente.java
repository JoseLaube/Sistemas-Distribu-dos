import java.rmi.registry.*;

public class Cliente {
    public static void main(String[] args) {
        
        // Define o endereço do servidor
        String host = (args.length < 1) ? "localhost" : args[0];
        
        try {
            System.out.println("=== Doca Iniciada ===");
            System.out.println("Procurando o Porto Central na rede...");

            // Obtém a referência do Registro RMI
            Registry registry = LocateRegistry.getRegistry(host, 6600);

            IServico porto = (IServico) registry.lookup("ServicoPorto");

            System.out.println("Conexão estabelecida com sucesso!\n");

            // Simulações:

            System.out.println("Solicitando relatório inicial de navios...");
            String relatorio = porto.relatorio_navio();
            System.out.println("Resposta do Servidor: " + relatorio);
            System.out.println("------------------------------------------------");

            System.out.println("Tentando atracar (cadastrar) um novo navio (Cargueiro Alfa, cap: 5000)...");
            
            Integer idNovoNavio = porto.cadastrar_navio("Cargueiro Alfa", 5000);
            System.out.println("Resposta do Servidor -> ID do Navio: " + idNovoNavio);
            System.out.println("------------------------------------------------");

            System.out.println("Solicitando relatório de cargas...");
            String relatorioCarga = porto.relatorio_carga();
            System.out.println("Resposta do Servidor: " + relatorioCarga);

        } catch (Exception ex) {
            System.err.println("Erro na Doca (Cliente): " + ex.toString());
            ex.printStackTrace();
        }
    }
}