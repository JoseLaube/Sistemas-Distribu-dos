package porto_com_filas;

import com.rabbitmq.client.*;

import porto_com_filas.IServico;

import java.io.IOException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class WorkerRMI {

    private final static String FILA_NAVIOS = "fila_navios";
    private final static String FILA_CARGAS = "fila_cargas";
    private final static String FILA_COMANDOS = "fila_comandos";

    private static IServico portoRMI; // Interface RMI para conversar com o porto

    public static void main(String[] argv) {
        System.out.println("=== WORKER INICIADO ===");

        try {
            // Conecta no RMI (Porta 6061)
            System.out.println("[Worker] Conectando ao Backend RMI...");
            Registry registry = LocateRegistry.getRegistry("localhost", 6061);
            portoRMI = (IServico) registry.lookup("Servico");
            
            // Conecta no RabbitMQ
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost("localhost");
            Connection connection = factory.newConnection();

            System.out.println("[Worker] Conectado! Disparando Threads consumidoras...\n");

            // INICIANDO AS THREADS PARA CONCORRÊNCIA:
            new Thread(() -> consumirNavios(connection)).start();  // Thread 1: Só lê navios e cadastra no RMI
            new Thread(() -> consumirCargas(connection)).start();  // Thread 2: Só lê cargas e cadastra no RMI
            new Thread(() -> consumirComandos(connection)).start();  // Thread 3: Só escuta comandos e executa no RMI

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Os metodos das threads:

    private static void consumirNavios(Connection connection) {
        try {
            Channel channel = connection.createChannel();
            channel.queueDeclare(FILA_NAVIOS, false, false, false, null);

            Consumer consumer = new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    String mensagem = new String(body, "UTF-8");
                    String[] dados = mensagem.split(","); // Divide "Cargueiro A,1000"
                    try {
                        portoRMI.cadastrar_navio(dados[0], Integer.parseInt(dados[1]));
                        System.out.println("[Thread Navios] Salvo no RMI: " + dados[0]);
                    } catch (Exception e) { System.err.println("Erro RMI Navio: " + e.getMessage()); }
                }
            };
            channel.basicConsume(FILA_NAVIOS, true, consumer);
        } catch (Exception e) { e.printStackTrace(); }
    }

    private static void consumirCargas(Connection connection) {
        try {
            Channel channel = connection.createChannel();
            channel.queueDeclare(FILA_CARGAS, false, false, false, null);

            Consumer consumer = new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {

                    String mensagem = new String(body, "UTF-8");
                    String[] dados = mensagem.split(",");

                    try {
                        portoRMI.cadastrar_carga(dados[0], Integer.parseInt(dados[1]));
                        System.out.println("[Thread Cargas] Salvo no RMI: " + dados[0]);
                    } catch (Exception e) { System.err.println("Erro RMI Carga: " + e.getMessage()); }
                }
            };
            channel.basicConsume(FILA_CARGAS, true, consumer);
        } catch (Exception e) { e.printStackTrace(); }
    }

    private static void consumirComandos(Connection connection) {
        try {
            Channel channel = connection.createChannel();
            channel.queueDeclare(FILA_COMANDOS, false, false, false, null);

            Consumer consumer = new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {

                    String comando = new String(body, "UTF-8");

                    try {
                        System.out.println("[Thread Comandos] Disparando RMI: " + comando);
                        double resultado = portoRMI.embarcar(comando);
                        System.out.println("[Thread Comandos] OTIMIZAÇÃO CONCLUÍDA! Eficiência: " + (resultado * 100) + "%");
                    } catch (Exception e) { System.err.println("Erro RMI Comando: " + e.getMessage()); }
                }
            };
            channel.basicConsume(FILA_COMANDOS, true, consumer);
        } catch (Exception e) { e.printStackTrace(); }
    }
}