
import java.rmi.*;
import java.rmi.server.*;
import java.rmi.registry.*;
import java.util.ArrayList; // Para guardar os dados temporariamente
import java.util.List;

public class PortoServer implements IServico {

    private List<String> navios;
    private List<String> cargas;
    //aqui eu acho q precisava ser Map pra guardar os objetos Navio e Carga
    // private Map<Integer, Navio> navios;
    // private Map<Integer, Carga> cargas;

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
    public Integer cadastrar_navio(String descricao_navio, Integer capacidade, Integer id_navio) throws RemoteException {
        navios.put(id_navio, new Navio(id_navio, descricao_navio, Double.valueOf(capacidade)));
        return id_navio; 
    }

    @Override
    public void remover_navio(Integer id_navio) throws RemoteException {
        navios.remove(id_navio); 
    }

    @Override
    public Integer cadastrar_carga(String descricao, Integer volume, Integer id_carga) throws RemoteException {
        cargas.put(id_carga, new Carga(id_carga, descricao, volume));
        return id_carga;
    }

    @Override
    public void remover_carga(Integer id) throws RemoteException {
        cargas.remove(id);
    }

    @Override
    public double embarcar_carga(String descricao, Integer id_carga, Integer id_navio, Double capacidade, String descricao_navio, Integer volume) throws RemoteException {
        Navio navio = navios.get(id_navio);
        Carga carga = cargas.get(id_carga);

        if (navio == null) {
            System.out.println("Navio com ID " + id_navio + " não encontrado.");
            return -1.0; 
        }

        if (carga == null) {
            System.out.println("Carga com ID " + id_carga + " não encontrada.");
            return -1.0; 
        }

        if (navio.capacidade < volume) {
            System.out.println("Capacidade insuficiente no navio.");
            return -1.0;
        }

        System.out.println("Embarcando carga '" + carga.descricao + "' no navio '" + navio.descricao + "' (ID: " + id_navio + ")");

        navio.capacidade -= volume;
        // cargas.remove(id_carga);
        return navio.capacidade;
    }

    @Override
    public String relatorio_navio() throws RemoteException {
        StringBuilder sb = new StringBuilder("--- Relatório de Navios ---\n");
        for (Navio n : navios.values()) {
            sb.append("ID: ").append(n.id).append(" | Descrição: ").append(n.descricao).append(" | Cap. Restante: ").append(n.capacidade).append("\n");
        }
        return sb.toString();
    }

    @Override
    public String relatorio_carga() throws RemoteException { 
        StringBuilder sb = new StringBuilder("--- Relatório de Cargas ---\n");
        for (Carga c : cargas.values()) {
            sb.append("ID: ").append(c.id).append(" | Descrição: ").append(c.descricao).append(" | Volume: ").append(c.volume).append("\n");
        }
        return sb.toString();
    }

    @Override
    public String relatorio_embarque() throws RemoteException {
        return "Relatório vazio";
    }

}
