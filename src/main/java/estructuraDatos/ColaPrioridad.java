package estructuraDatos;

import java.util.ArrayList;
import java.util.List;
import sistema.sistemadesoportetecnicoit.shared.models.Ticket;

public class ColaPrioridad {
private NodoTicket head; 
private int tamano;

public ColaPrioridad() {
        this.head = null;
        this.tamano = 0;
    }

private boolean esCritico(Ticket t){
        return t.getTipo().equalsIgnoreCase("CRITICO") || t.getTipo().equalsIgnoreCase("URGENTE");
    }

public void insertarConPrioridad(Ticket nuevoTicket){
        NodoTicket nuevo = new NodoTicket(nuevoTicket);

        if (head == null || esCritico(nuevoTicket)) {
            nuevo.setSiguiente(head);
            head = nuevo;
        }else{
            NodoTicket recorrer = head;
            while(recorrer.getSiguiente() != null && esCritico(recorrer.getSiguiente().getDato())) {
                recorrer = recorrer.getSiguiente();
            }
            nuevo.setSiguiente(recorrer.getSiguiente());
            recorrer.setSiguiente(nuevo);
        }
        tamano++;
        System.out.println("Ticket prioritario " + nuevoTicket.getTicketId() + " agregado");
    }
public Ticket eliminar(){
        if(head == null) return null;
        Ticket dato = head.getDato();
        head = head.getSiguiente();
        tamano--;
        return dato;
    }
public int getTamano() {
        return tamano;
    }

public List<Ticket> getTickets() {
    List<Ticket> lista = new ArrayList<>();
    NodoTicket recorrer = head;
    while (recorrer != null) {
        lista.add(recorrer.getDato());
        recorrer = recorrer.getSiguiente();
    }
    return lista;
}
}
