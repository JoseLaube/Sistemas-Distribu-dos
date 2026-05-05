package porto;

import java.rmi.registry.*;
import java.util.Scanner;

public class Cliente {
    public static void main(String[] args) {
        
        String host = (args.length < 1) ? "10.20.221.235" : args[0];
        Scanner leitor = new Scanner(System.in);
        
        try {
            // 1. Conexão com o Servidor
            Registry registry = LocateRegistry.getRegistry(host, 6600);
            IServico porto = (IServico) registry.lookup("Servico");
            
            int opcao = -1;
            
            System.out.println("======= SISTEMA DE DOCAS - PORTO RMI =======");
            
            while (opcao != 0) {
                System.out.println("\n--- MENU DE OPERAÇÕES ---");
                System.out.println("1. Cadastrar Novo Navio");
                System.out.println("2. Cadastrar Nova Carga");
                System.out.println("3. Remover Navio");
                System.out.println("4. Remover Carga");
                System.out.println("5. Listar Navios no Porto");
                System.out.println("6. Listar Cargas no Pátio");
                System.out.println("7. Realizar Embarque (Carga -> Navio)");
                System.out.println("8. Ver Relatório Geral");
                System.out.println("0. Sair");
                System.out.print("Escolha uma opção: ");
                
                opcao = Integer.parseInt(leitor.nextLine()); // Evita problemas com o buffer do Scanner

                switch (opcao) {
                    case 1:
                        System.out.print("Descrição do Navio: ");
                        String descN = leitor.nextLine();
                        System.out.print("Capacidade de Carga (ton): ");
                        int cap = Integer.parseInt(leitor.nextLine());
                        int idN = porto.cadastrar_navio(descN, cap);
                        System.out.println(">>> Navio cadastrado com ID: " + idN);
                        break;

                    case 2:
                        System.out.print("Descrição da Carga: ");
                        String descC = leitor.nextLine();
                        System.out.print("Volume da Carga (ton): ");
                        int vol = Integer.parseInt(leitor.nextLine());
                        int idC = porto.cadastrar_carga(descC, vol);
                        System.out.println(">>> Carga cadastrada com ID: " + idC);
                        break;

                    case 3:
                        System.out.print("Remover um navio: ");
                        int idRemover = Integer.parseInt(leitor.nextLine());
                        porto.remover_navio(idRemover);
                        break;

                    case 4:
                        System.out.print("Remover um carga: ");
                        int idRemover_carga = Integer.parseInt(leitor.nextLine());
                        porto.remover_carga(idRemover_carga);
                        break;

                    case 5:
                        System.out.println(porto.relatorio_navio());
                        break;

                    case 6:
                        System.out.println(porto.relatorio_carga());
                        break;

                    case 7:
                        // System.out.print("ID da Carga: ");
                        // String idCarga = leitor.nextLine();
                        // System.out.print("ID do Navio: ");
                        // String idNavio = leitor.nextLine();
                        
                        // // Enviamos no formato "ID_CARGA,ID_NAVIO" para respeitar a String da interface
                        // String comando = idCarga + "," + idNavio;
                        // double saldo = porto.embarcar(comando);
                        
                        // if (saldo >= 0) {
                        //     System.out.println(">>> Embarque realizado! Capacidade restante no navio: " + saldo);
                        // } else {
                        //     System.out.println(">>> ERRO: Falha no embarque. Verifique IDs ou Capacidade.");
                        // }
                        
                        System.out.print("Realizando o carregamento: ");
                        double retorno = porto.embarcar("OTIMIZAR");
                        if (retorno >= 0) {
                            System.out.println(">>> Embarque realizado!");
                        } else {
                            System.out.println(">>> ERRO: Falha no embarque. Verifique IDs ou Capacidade.");
                        }

                        break;

                    case 8:
                        System.out.println(porto.relatorio_embarque());
                        break;

                    case 0:
                        System.out.println("Encerrando Doca...");
                        break;

                    default:
                        System.out.println("Opção inválida!");
                        break;
                }
            }

        } catch (Exception ex) {
            System.err.println("Erro na comunicação: " + ex.getMessage());
            ex.printStackTrace();
        } finally {
            leitor.close();
        }
    }
}