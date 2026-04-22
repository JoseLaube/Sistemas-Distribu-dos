package middleware;

import javax.jws.WebService;
import javax.jws.WebMethod;

@WebService
public interface WSPortoServer {

    @WebMethod
    public String relatorio_embarque();

    @WebMethod
    public double embarcar(String descricao);

}