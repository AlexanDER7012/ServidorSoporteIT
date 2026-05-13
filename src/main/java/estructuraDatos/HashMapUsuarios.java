package estructuraDatos;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import sistema.sistemadesoportetecnicoit.shared.models.Ticket;

public class HashMapUsuarios {
private static class Entry<K, V> {
        K key;
        V value;

        Entry(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }

    private LinkedList<Entry<String, LinkedList<Ticket>>>[] table;
    private int size;
    private final int CAPACITY = 100;

    @SuppressWarnings("unchecked")
    public HashMapUsuarios() {
        table = new LinkedList[CAPACITY];
        for (int i = 0; i < CAPACITY; i++) {
            table[i] = new LinkedList<>();
        }
    }

    private int hash(String key) {
        return Math.abs(key.hashCode()) % CAPACITY;
    }

    // Insertar ticket al historial de un DPI
    // Si ya existe el DPI → acumula, no sobreescribe
    public void agregarTicket(String dpi, Ticket ticket) {
        int index = hash(dpi);
        for (Entry<String, LinkedList<Ticket>> entry : table[index]) {
            if (entry.key.equals(dpi)) {
                entry.value.add(ticket);
                return;
            }
        }
        LinkedList<Ticket> historial = new LinkedList<>();
        historial.add(ticket);
        table[index].add(new Entry<>(dpi, historial));
        size++;
    }

    // Obtener historial por DPI
    public LinkedList<Ticket> get(String dpi) {
        int index = hash(dpi);
        for (Entry<String, LinkedList<Ticket>> entry : table[index]) {
            if (entry.key.equals(dpi)) {
                return entry.value;
            }
        }
        return null;
    }

    // Verificar si un DPI ya existe
    public boolean containsKey(String dpi) {
        int index = hash(dpi);
        for (Entry<String, LinkedList<Ticket>> entry : table[index]) {
            if (entry.key.equals(dpi)) {
                return true;
            }
        }
        return false;
    }

    // Visualizar estructura completa
    public void display() {
        System.out.println("\n--- Estado Actual de la Tabla Hash (Usuarios) ---");
        for (int i = 0; i < CAPACITY; i++) {
            if (!table[i].isEmpty()) {
                System.out.print("Indice " + i + ": ");
                for (Entry<String, LinkedList<Ticket>> entry : table[i]) {
                    System.out.print("[DPI: " + entry.key
                            + " | Tickets: " + entry.value.size() + "] -> ");
                }
                System.out.println("null");
            }
        }
        System.out.println("Total de usuarios registrados: " + size);
    }


    public List<Ticket> obtenerHistorialFiltrado(String dpi) {
    LinkedList<Ticket> historialOriginal = get(dpi);
    List<Ticket> historialProcesado = new ArrayList<>();

    if (historialOriginal != null){
        for (Ticket t : historialOriginal){
            Ticket resumen = new Ticket();
            resumen.setTicketId(t.getTicketId());
            resumen.setTipo(t.getTipo());
            resumen.setMotivo(t.getMotivo());
            resumen.setUsuarioAtendio(t.getUsuarioAtendio());
            resumen.setDpi(t.getDpi());
            resumen.setNombreApellido(t.getNombreApellido());
            resumen.setTiempoAtencion(t.getTiempoAtencion());
            resumen.setTiempoFinal(t.getTiempoFinal());
            historialProcesado.add(resumen);
        }
    }
    return historialProcesado;
}
}
