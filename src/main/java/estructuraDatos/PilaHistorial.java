package estructuraDatos;

import sistema.sistemadesoportetecnicoit.shared.models.Ticket;

public class PilaHistorial {
    private NodoTicket top;

    public PilaHistorial(){
        this.top = null;
    }

    public void registrarAccion(Ticket t) {
        NodoTicket nuevo = new NodoTicket(t);
        nuevo.setSiguiente(top);
        top = nuevo;
    }

    public Ticket deshacerAccion(){
        if(top == null){
            return null;
        }
        Ticket dato = top.getDato();
        top = top.getSiguiente();
        return dato;
    }

    public boolean estaVacia(){
        return top == null;
    }
} 
