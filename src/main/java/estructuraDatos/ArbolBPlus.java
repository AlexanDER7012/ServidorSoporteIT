package estructuraDatos;


import sistema.sistemadesoportetecnicoit.shared.models.Ticket;

public class ArbolBPlus {
    private NodoArbol raiz;
    private final int ORDEN = 4; 
    
    public ArbolBPlus() {
        this.raiz = new NodoArbol(true);
    }
    
    private Ticket buscarEnNodo(NodoArbol actual, String dpi) {
        int i=0;
        while(i<actual.getLlaves().size() && dpi.compareTo(actual.getLlaves().get(i)) > 0){
            i++;
        }

        if(actual.isEsHoja()){
            if (i<actual.getLlaves().size() && actual.getLlaves().get(i).equals(dpi)) {
                return actual.getTickets().get(i);
            }
            return null;
        }
        
        return buscarEnNodo(actual.getHijos().get(i),dpi);
    }
    
    public Ticket buscar(String dpi) {
        return buscarEnNodo(raiz, dpi);
    }
    public void insertar(Ticket nuevoTicket){
    NodoArbol r = raiz;

    if(r.getLlaves().size() == ORDEN){
        NodoArbol s = new NodoArbol(false); 
        raiz = s;
        s.getHijos().add(r);
        dividirNodo(s, 0, r); 
        insertarNoLleno(s, nuevoTicket);
    }else{
        insertarNoLleno(r, nuevoTicket);
    }
}

private void insertarNoLleno(NodoArbol x, Ticket ticket) {
    int i = x.getLlaves().size() - 1;
    if(x.isEsHoja()){
        while (i >= 0 && ticket.getDpi().compareTo(x.getLlaves().get(i)) < 0) {
            i--;
        }
        x.getLlaves().add(i + 1, ticket.getDpi());
        x.getTickets().add(i + 1, ticket);
    }else{
        // Buscar a qué hijo ir
        while (i >= 0 && ticket.getDpi().compareTo(x.getLlaves().get(i)) < 0) {
            i--;
        }
        i++;
        NodoArbol hijo = x.getHijos().get(i);
        if (hijo.getLlaves().size() == ORDEN) {
            dividirNodo(x, i, hijo);
            if (ticket.getDpi().compareTo(x.getLlaves().get(i)) > 0) {
                i++;
            }
        }
        insertarNoLleno(x.getHijos().get(i), ticket);
    }
}

private void dividirNodo(NodoArbol padre, int indice, NodoArbol hijoLleno){
    NodoArbol nuevoNodo = new NodoArbol(hijoLleno.isEsHoja());
    int mitad = ORDEN / 2;

    if (hijoLleno.isEsHoja()){
        for (int j = 0; j < mitad; j++) {
            nuevoNodo.getLlaves().add(hijoLleno.getLlaves().remove(mitad));
            nuevoNodo.getTickets().add(hijoLleno.getTickets().remove(mitad));
        }
        nuevoNodo.setSiguiente(hijoLleno.getSiguiente());
        hijoLleno.setSiguiente(nuevoNodo);
        padre.getLlaves().add(indice, nuevoNodo.getLlaves().get(0));
        
    }else{
        String llaveQueSube = hijoLleno.getLlaves().remove(mitad);
        
        for(int j = 0; j < mitad - 1; j++) {
            nuevoNodo.getLlaves().add(hijoLleno.getLlaves().remove(mitad));
        }
        for (int j = 0; j < mitad; j++) {
            nuevoNodo.getHijos().add(hijoLleno.getHijos().remove(mitad));
        }
        
        padre.getLlaves().add(indice, llaveQueSube);
    }
    
    padre.getHijos().add(indice + 1, nuevoNodo);
}
}
