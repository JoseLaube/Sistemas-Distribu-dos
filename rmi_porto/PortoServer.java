
import java.rmi.*;
import java.rmi.server.*;
import java.rmi.registry.*;
import java.util.HashMap; // Import necessário
import java.util.Map;     // Import necessário
import java.util.concurrent.atomic.AtomicInteger;

public class PortoServer implements IServico {

    private Map<Integer, Navio> navios;
    private Map<Integer, Carga> cargas;

    private AtomicInteger idNavioContador = new AtomicInteger(1);
    private AtomicInteger idCargaContador = new AtomicInteger(1);
    
    private Embarcar logistica = new Embarcar();

    // Construtor do Servidor
    public PortoServer() {
        this.navios = new HashMap<>();
        this.cargas = new HashMap<>();
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
    public Integer cadastrar_navio(String descricao, Integer capacidade) throws RemoteException {
        int id = idNavioContador.getAndIncrement();
        navios.put(id, new Navio(id, descricao, capacidade.doubleValue()));
        System.out.println("Navio cadastrado: " + descricao + " (ID: " + id + ")");
        return id; 
    }

    @Override
    public void remover_navio(Integer id) throws RemoteException {
        navios.remove(id);
        System.out.println("Navio removido ID: " + id);
    }

    @Override
    public String relatorio_navio() throws RemoteException {
        if (navios.isEmpty()) return "Nenhum navio cadastrado.";
        StringBuilder sb = new StringBuilder("--- Relatório de Navios ---\n");
        for (Navio n : navios.values()) {
            sb.append("ID: ").append(n.id).append(" | Desc: ").append(n.descricao)
              .append(" | Cap: ").append(n.capacidadeAtual).append("\n");
        }
        return sb.toString();
    }
 
    @Override
    public Integer cadastrar_carga(String descricao, Integer volume) throws RemoteException {
        int id = idCargaContador.getAndIncrement();
        cargas.put(id, new Carga(id, descricao, volume));
        System.out.println("Carga cadastrada: " + descricao + " (ID: " + id + ")");
        return id;
    }

    @Override
    public void remover_carga(Integer id) throws RemoteException {
        cargas.remove(id);
    }

    @Override
    public String relatorio_carga() throws RemoteException {
        if (cargas.isEmpty()) return "Nenhuma carga cadastrada.";
        StringBuilder sb = new StringBuilder("--- Relatório de Cargas ---\n");
        for (Carga c : cargas.values()) {
            sb.append("ID: ").append(c.id).append(" | Desc: ").append(c.descricao)
              .append(" | Vol: ").append(c.volume).append("\n");
        }
        return sb.toString();
    }

    @Override
    public synchronized double embarcar(String comando) throws RemoteException {
        // if ("BOOTSTRAP".equalsIgnoreCase(comando)) {
        //     // Código para gerar dados fakes (opcional, para testes)
        //     gerarDadosTeste();
        //     return 0.0;
        // }

        if ("OTIMIZAR".equalsIgnoreCase(comando)) {
            return logistica.executarOtimizacao(this.navios, this.cargas);
        }

        return -1.0;
    }



    @Override
    public String relatorio_embarque() throws RemoteException {
        return "Resumo: " + navios.size() + " navios no porto e " + cargas.size() + " cargas pendentes.";
    }


}
