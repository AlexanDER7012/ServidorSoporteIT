package estructuraDatos;

import java.util.ArrayList;
import sistema.sistemadesoportetecnicoit.shared.models.Ticket;

public class NodoArbol {
    private boolean esHoja;
    private ArrayList<String> llaves; 
    private ArrayList<NodoArbol> hijos;  
    private ArrayList<Ticket> tickets; 
    private NodoArbol  siguiente;

    public NodoArbol (boolean esHoja) {
        this.esHoja = esHoja;
        this.llaves = new ArrayList<>();
        this.hijos = new ArrayList<>();
        this.tickets = new ArrayList<>();
        this.siguiente = null;
    }    

    public boolean isEsHoja() {
        return esHoja;
    }

    public void setEsHoja(boolean esHoja) {
        this.esHoja = esHoja;
    }

    public ArrayList<String> getLlaves() {
        return llaves;
    }

    public void setLlaves(ArrayList<String> llaves) {
        this.llaves = llaves;
    }

    public ArrayList<NodoArbol> getHijos() {
        return hijos;
    }

    public void setHijos(ArrayList<NodoArbol> hijos) {
        this.hijos = hijos;
    }

    public ArrayList<Ticket> getTickets() {
        return tickets;
    }

    public void setTickets(ArrayList<Ticket> tickets) {
        this.tickets = tickets;
    }

    public NodoArbol getSiguiente() {
        return siguiente;
    }

    public void setSiguiente(NodoArbol siguiente) {
        this.siguiente = siguiente;
    }
}
