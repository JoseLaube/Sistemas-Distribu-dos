import java.io.Serializable;

public class Carga implements Serializable{
    Integer id;
    String descricao;
    Integer volume;

    public Carga(Integer id, String descricao, Integer volume) {
        this.id = id;
        this.descricao = descricao;
        this.volume = volume;
    }
}
