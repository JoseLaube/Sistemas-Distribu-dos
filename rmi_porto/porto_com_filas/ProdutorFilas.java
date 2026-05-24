package porto_com_filas;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class ProdutorFilas {

    private final static String FILA_NAVIOS = "fila_navios";
    private final static String FILA_CARGAS = "fila_cargas";
    private final static String FILA_COMANDOS = "fila_comandos";

    public static void main(String[] argv) {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost"); // Conecta no RabbitMQ do labp2d
        
        Connection connection = null;
        Channel channel = null;

        try {
            // conexão no modo tradicional
            connection = factory.newConnection();
            channel = connection.createChannel();

            // Cria as 3 filas no RabbitMQ
            channel.queueDeclare(FILA_NAVIOS, false, false, false, null);
            channel.queueDeclare(FILA_CARGAS, false, false, false, null);
            channel.queueDeclare(FILA_COMANDOS, false, false, false, null);

            System.out.println("=== GERANDO DADOS (DESACOPLAMENTO TEMPORAL) ===");

            // Gera 10 Navios e joga na Fila
            for (int i = 1; i <= 10; i++) {
                String mensagem = "Cargueiro Modelo " + i + "," + (1000 * i);
                channel.basicPublish("", FILA_NAVIOS, null, mensagem.getBytes("UTF-8"));
                System.out.println(" [x] Enviado Navio: '" + mensagem + "'");
            }

            // Gera 30 Cargas e joga na Fila
            for (int i = 1; i <= 30; i++) {
                String mensagem = "Container " + i + "," + 200;
                channel.basicPublish("", FILA_CARGAS, null, mensagem.getBytes("UTF-8"));
                System.out.println(" [x] Enviado Carga: '" + mensagem + "'");
            }

            System.out.println(" [*] Aguardando ...");
            Thread.sleep(4000); // delay em ms

            // Manda o comando de otimização no final
            String comando = "OTIMIZAR";
            channel.basicPublish("", FILA_COMANDOS, null, comando.getBytes("UTF-8"));
            System.out.println(" [x] Enviado Comando: '" + comando + "'");

            System.out.println("=== PRODUTOR FINALIZADO COM SUCESSO! ===");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Fecha as conexões manualmente de forma segura
            try {
                if (channel != null) channel.close();
                if (connection != null) connection.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}