package estructuraDatos;

import sistema.sistemadesoportetecnicoit.shared.models.Ticket;

public class NodoTicket {
private Ticket Dato;
private NodoTicket siguiente;


public NodoTicket(Ticket Dato){
    this.Dato = Dato;
    this.siguiente = null;
}

    public Ticket getDato() {
        return Dato;
    }

    public void setDato(Ticket Dato) {
        this.Dato = Dato;
    }

    public NodoTicket getSiguiente() {
        return siguiente;
    }

    public void setSiguiente(NodoTicket siguiente) {
        this.siguiente = siguiente;
    }

}
