package middleware;

import javax.jws.WebService;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import shared.IServico;

@WebService(endpointInterface = "middleware.WSPortoServer")
public class WSPortoServerImpl implements WSPortoServer {

    private IServico portoRMI; 
    private boolean conectado = false; // Flag para segurança

    public WSPortoServerImpl() {
        conectarAoRMI();
    }

    public void conectarAoRMI() {
        try {
            System.out.println("[Middleware] Buscando Backend RMI na porta 6600...");
            Registry registry = LocateRegistry.getRegistry("localhost", 6600);
            portoRMI = (IServico) registry.lookup("ServicoPorto");
            conectado = true;
            System.out.println("[Middleware] CONECTADO AO RMI COM SUCESSO!");
        } catch (Exception e) {
            conectado = false;
            System.err.println("[Middleware] AVISO: Backend RMI não está online.");
        }
    }

    public boolean isConectado() {
        return conectado;
    }

    @Override
    public String relatorio_embarque() {
        if (!conectado) return "ERRO: O Middleware perdeu a conexão com o Backend RMI.";
        try {
            System.out.println("[Middleware] Repassando relatorio_embarque()...");
            return portoRMI.relatorio_embarque(); 
        } catch (Exception e) {
            return "Erro RMI: " + e.getMessage();
        }
    }

    @Override
    public double embarcar(String descricao) {
        if (!conectado) {
            System.err.println("[Middleware] Operação abortada. RMI offline.");
            return -1.0;
        }
        try {
            System.out.println("[Middleware] Repassando embarcar(" + descricao + ")...");
            return portoRMI.embarcar(descricao);
        } catch (Exception e) {
            System.err.println("[Middleware] Erro RMI: " + e.getMessage());
            return -1.0;
        }
    }
}