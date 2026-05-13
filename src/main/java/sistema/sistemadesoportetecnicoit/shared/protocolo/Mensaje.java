package sistema.sistemadesoportetecnicoit.shared.protocolo;


import java.io.Serializable;

public class Mensaje implements Serializable {

    private static final long serialVersionUID = 1L;

    private final  TipoMensaje tipo;
    private final Object payload;
    private final String origen;

    public Mensaje(TipoMensaje tipo, Object payload) {
        this(tipo, payload,null);
    }

    public Mensaje(TipoMensaje tipo, Object payload, String origen) {
        this.tipo = tipo;
        this.payload = payload;
        this.origen = origen;
    }

    public TipoMensaje getTipo() {
        return tipo;
    }

    public Object getPayload() {
        return payload;
    }

    public String getOrigen() {
        return origen;
    }

    @Override
    public String toString() {
        return "Mensaje{tipo=" + tipo + ", origen='" + origen + "', payload=" + payload + "}";
    }
    
}
