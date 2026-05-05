package porto;

import java.io.Serializable;

public class Carga implements Serializable{
    public Integer id;
    public String descricao;
    public Integer volume;

    public Carga(Integer id, String descricao, Integer volume) {
        this.id = id;
        this.descricao = descricao;
        this.volume = volume;
    }
}
