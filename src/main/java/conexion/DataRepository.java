package conexion;

import estructuraDatos.ArbolBPlus;
import estructuraDatos.ColaPrioridad;
import estructuraDatos.ColaTicket;
import estructuraDatos.HashMapUsuarios;
import java.io.*;
import java.util.List;
import sistema.sistemadesoportetecnicoit.shared.models.Ticket;

public class DataRepository {
    private ArbolBPlus arbolB;
    private HashMapUsuarios tablaHash;
    private ColaTicket filaNormal;
    private ColaPrioridad filaUrgente;
    
    public DataRepository() {
        this.arbolB = new ArbolBPlus(); 
        this.tablaHash = new HashMapUsuarios();
        this.filaNormal = new ColaTicket();
        this.filaUrgente = new ColaPrioridad();
        cargarDatosDesdeArchivo();
    }
    
    public synchronized void clasificarYEncolar(Ticket t){
    if (t.isPrioridad()){ 
        filaUrgente.insertarConPrioridad(t);
        System.out.println(" Ticket " + t.getTicketId() + " añadido a cola de prioridad");
    }else{
        filaNormal.insertarNodo(t);
        System.out.println("Ticket " + t.getTicketId() + " aniadido a cola normal");
    }
}
    public synchronized Ticket extraerPC3Normal(){
        return filaNormal.eliminarCola();
    }
    public synchronized Ticket extraerPC4Prioridad(){
        return filaUrgente.eliminar();
    }

    public synchronized void guardarTicketFinalizado(Ticket t){
        arbolB.insertar(t);
        tablaHash.agregarTicket(t.getDpi(), t);
        guardarEnArchivo(t);
        System.out.println("Repositorio: Datos guardados para DPI: " + t.getDpi());
    }
    public synchronized Ticket buscarPorDPI(String dpi){
        return arbolB.buscar(dpi);
    }

    private void guardarEnArchivo(Ticket t){
    String nombreArchivo = "tickets_atendidos.txt";
    try(FileWriter fw = new FileWriter(nombreArchivo, true);
        PrintWriter pw = new PrintWriter(fw)){
        String linea = String.format("%s|%s|%s|%s|%s|%b|%s|%.2f",
            t.getTicketId(),
            t.getDpi(),
            t.getNombreApellido(),
            t.getMotivo(),
            t.getTipo(),
            t.isPrioridad(),
            t.getUsuarioAtendio(),
            t.getDuracionAtencionMinutos()
        );
        pw.println(linea);
    } catch(IOException e){
        System.err.println("Error al escribir archivo: " + e.getMessage());
    }
}
    
    public synchronized List<Ticket> buscarHistorialPorDPI(String dpi) {
    return tablaHash.obtenerHistorialFiltrado(dpi); 
    }
    
    public void cargarDatosDesdeArchivo() {
    String nombreArchivo = "tickets_atendidos.txt";
    File archivo = new File(nombreArchivo);
    if(!archivo.exists()) return;

    try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
        String linea;
        while ((linea = br.readLine()) != null) {
            String[] datos = linea.split("\\|");
            if (datos.length >= 8){ 
                String id = datos[0];
                String dpi = datos[1];
                String nombre = datos[2];
                String motivo = datos[3];
                String tipo = datos[4];
                boolean prioridad= Boolean.parseBoolean(datos[5]);
                String tecnico= datos[6];
                double duracion = Double.parseDouble(datos[7].replace(",", ".")); 

                Ticket t = new Ticket(id, dpi, nombre, motivo, tipo, prioridad);
                t.setUsuarioAtendio(tecnico);
                t.reconstruirTiemposDesdeMinutos(duracion);
                
                arbolB.insertar(t);
                tablaHash.agregarTicket(dpi, t);
            }
        }
        System.out.println("Persistencia cargada correctamente.");
    } catch(IOException | NumberFormatException e){
        System.err.println("Error al cargar persistencia: " + e.getMessage());
    }
}
    public synchronized int getContadorNormal(){
        return filaNormal.getTamano();
    }

    public synchronized int getContadorPrioridad(){
        return filaUrgente.getTamano();
    }
}
    