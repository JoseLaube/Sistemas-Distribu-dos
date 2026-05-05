package porto;

import java.io.*;
import java.util.*;

public class Embarcar {

    private static final String LOG_FILE = "log_embarques.txt";

    public double executarOtimizacao(Map<Integer, Navio> navios, Map<Integer, Carga> cargas) {
        System.out.println("[Logística] Iniciando otimização inteligente (Best Fit Decreasing)...");

        if (cargas.isEmpty() || navios.isEmpty()) return 0.0;

        // 1. Ordena as cargas da MAIOR para a MENOR (Item crucial do Problema da Mochila)
        List<Carga> listaCargas = new ArrayList<>(cargas.values());
        listaCargas.sort((c1, c2) -> c2.volume.compareTo(c1.volume));

        List<Navio> listaNavios = new ArrayList<>(navios.values());

        // Ordena navios do MENOR para o MAIOR.
        listaNavios.sort((n1, n2) -> Double.compare(n1.capacidadeOriginal, n2.capacidadeOriginal));

        // 2. Algoritmo de Otimização
        for (Carga c : listaCargas) {
            Navio melhorNavio = null;
            double menorEspacoSobra = Double.MAX_VALUE;

            // Busca qual navio acomoda esta carga deixando o MENOR buraco possível
            for (Navio n : listaNavios) {
                if (n.capacidadeAtual >= c.volume) {
                    double sobra = n.capacidadeAtual - c.volume;
                    
                    // Se o espaço que vai sobrar for menor do que o que achamos até agora, este é o melhor navio
                    if (sobra < menorEspacoSobra) {
                        menorEspacoSobra = sobra;
                        melhorNavio = n;
                    }
                    
                    // Se a sobra for zero (encaixe perfeito), não precisamos procurar mais!
                    if (sobra == 0) break; 
                }
            }

            // 3. Se encontrou um navio viável, efetua o embarque
            if (melhorNavio != null) {
                melhorNavio.capacidadeAtual -= c.volume;
                cargas.remove(c.id);
                
                salvarLog(c, melhorNavio);
            }
        }

        // 4. Retorna o KPI de eficiência exigido pelo professor
        return calcularEficiencia(navios);
    }

    private void salvarLog(Carga c, Navio n) {
        try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(LOG_FILE, true)))) {
            out.printf("EMBARQUE: Carga %d [%s] -> Navio %d [%s]. Cap. Restante: %.2f%n",
                       c.id, c.descricao, n.id, n.descricao, n.capacidadeAtual);
        } catch (IOException e) {
            System.err.println("Erro ao gravar log: " + e.getMessage());
        }
    }

    private double calcularEficiencia(Map<Integer, Navio> navios) {
        double cargaTotalEmbarcada = 0;
        double capTotalUtilizada = 0;

        for (Navio n : navios.values()) {
            double ocupado = n.capacidadeOriginal - n.capacidadeAtual;
            
            // A regra de ouro da métrica: só entra no cálculo se o navio foi USADO.
            if (ocupado > 0) {
                cargaTotalEmbarcada += ocupado;
                capTotalUtilizada += n.capacidadeOriginal;
            }
        }
        
        return (capTotalUtilizada == 0) ? 0 : (cargaTotalEmbarcada / capTotalUtilizada);
    }
}