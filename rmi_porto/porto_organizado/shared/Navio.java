package shared;

import java.io.Serializable;

public class Navio implements Serializable{
    public Integer id;
    public String descricao;
    public double capacidadeOriginal; // Adicionado
    public double capacidadeAtual;    // O que sobra

    public Navio(Integer id, String descricao, Double capacidade) {
        this.id = id;
        this.descricao = descricao;
        this.capacidadeOriginal = capacidade;
        this.capacidadeAtual = capacidade;
    }
}
