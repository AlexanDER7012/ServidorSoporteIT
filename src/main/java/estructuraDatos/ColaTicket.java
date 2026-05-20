package estructuraDatos;

import java.util.ArrayList;
import java.util.List;
import sistema.sistemadesoportetecnicoit.shared.models.Ticket;

public class ColaTicket {
private NodoTicket head;
private NodoTicket ultimo;
private int tamano;

public ColaTicket(){
        this.head = null;
        this.ultimo = null;
        this.tamano = 0;
    }

public void insertarNodo(Ticket t){
    NodoTicket nuevo = new NodoTicket(t);
    if(head == null) head = ultimo = nuevo;
    else{
        ultimo.setSiguiente(nuevo);
        ultimo = nuevo;
    }
    tamano++;
    System.out.println("Ticket " + t.getTicketId() + " agregado a la Cola Normal");
}

public Ticket eliminarCola(){
        if (head == null){
            System.out.println("La cola esta vacia");
            return null;
        }else{
            Ticket ticketAtender = head.getDato();
            head = head.getSiguiente();
            if (head == null){
                ultimo = null;
            }
            tamano--;
            System.out.println("Ticket de " + ticketAtender.getNombreApellido() + " removido para atencion");
            return ticketAtender;
        }
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

public void mostrarCola(){
        if(head == null){
            System.out.println("La cola esta vacia");
        }else{
            NodoTicket recorrer = head;
            System.out.println("--- Tickets en Espera ---");
            while(recorrer != null) {
                System.out.print("[" + recorrer.getDato().getDpi() + "] - ");
                recorrer = recorrer.getSiguiente();
            }
            System.out.println("FIN");
        }
    }
}
