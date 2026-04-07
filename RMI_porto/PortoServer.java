
import java.rmi.*;
import java.rmi.server.*;
import java.rmi.registry.*;
import java.util.ArrayList; // Para guardar os dados temporariamente
import java.util.List;

public class PortoServer implements IServico {

    private List<String> navios;
    private List<String> cargas;

    // Construtor do Servidor
    public PortoServer() {
        this.navios = new ArrayList<>();
        this.cargas = new ArrayList<>();
    }

    public static void main(String[] args) {
        try {
            // Instancia o objeto que contém a lógica (o Porto)
            PortoServer server = new PortoServer();

            IServico stub = (IServico) UnicastRemoteObject.exportObject(server, 0);

            // Inicia o Registro RMI na porta desejada
            Registry registry = LocateRegistry.createRegistry(6600);

            registry.bind("ServicoPorto", stub);

            System.out.println("### Porto Centralizado Pronto para Operações ###");
            System.out.println("### Aguardando conexões das Docas...         ###");

        } catch (Exception ex) {
            System.err.println("Erro no servidor: " + ex.toString());
            ex.printStackTrace();
        }
    }


    @Override
    public double embarcar(String descricao) throws RemoteException {
        return 0.0;
    }

    @Override
    public String relatorio_embarque() throws RemoteException {
        return "Relatório vazio";
    }

    @Override
    public Integer cadastrar_navio(String descricao, Integer capacidade) throws RemoteException {
        return null; 
    }

    @Override
    public void remover_navio(Integer id) throws RemoteException {
    }

    @Override
    public String relatorio_navio() throws RemoteException {
        System.out.println("Teste de Debug!");
        return "Relatório de navios vazio";
    }

    @Override
    public Integer cadastrar_carga(String descricao, Integer volume) throws RemoteException {
        return null;
    }

    @Override
    public void remover_carga(Integer id) throws RemoteException {
    }

    @Override
    public String relatorio_carga() throws RemoteException {
        return "Relatório de cargas vazio";
    }

}