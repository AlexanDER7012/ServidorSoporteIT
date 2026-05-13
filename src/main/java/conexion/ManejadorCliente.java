package conexion;

import controllers.ServidorPC1Controller;
import java.io.*;
import java.net.*;
import java.util.List;
import sistema.sistemadesoportetecnicoit.shared.protocolo.Mensaje;
import sistema.sistemadesoportetecnicoit.shared.models.Ticket;
import sistema.sistemadesoportetecnicoit.shared.protocolo.TipoMensaje;

public class ManejadorCliente implements Runnable {

    private Socket socket;
    private DataRepository repositorio;
    private ServidorPC1Controller controlador;
    private String nombrePC = "Desconocido";
    private ObjectOutputStream out;
    private final List<String> PCS_AUTORIZADAS = List.of("PC2", "PC3", "PC4", "PC5");
    private volatile boolean cierreOrdenado = false;

    public ManejadorCliente(Socket socket, DataRepository repositorio, ServidorPC1Controller controlador) {
        this.socket = socket;
        this.repositorio = repositorio;
        this.controlador = controlador;
    }

    @Override
    public void run() {
        try (ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {
            out = new ObjectOutputStream(socket.getOutputStream());
            out.flush();

            while (!socket.isClosed()){
                try{
                    Object objetoRecibido = in.readObject();
                    if (objetoRecibido == null) break;
                    
                    if (!(objetoRecibido instanceof Mensaje)) {
                        if (controlador != null) {
                            controlador.reportarIntruso("Paquete no reconocido desde " + socket.getInetAddress());
                        }
                        return; 
                    }
                    Mensaje mensajeRecibido = (Mensaje) objetoRecibido;
                    
                    if(this.nombrePC.equals("Desconocido")){
                        String origen = mensajeRecibido.getOrigen();
                        
                        if (origen == null || !PCS_AUTORIZADAS.contains(origen)){
                            if (controlador != null) {
                                controlador.reportarIntruso("Usuario nuevo no detectado: " + socket.getInetAddress() + " como: " + origen);
                            }
                            try { Thread.sleep(100); } catch (InterruptedException e) {}
    
                            return;
                        }

                        this.nombrePC = origen;
                        if (controlador != null) {
                            controlador.actualizarLog("Estacion conectada e identificada como: " + nombrePC);
                            controlador.actualizarEstadoPC(nombrePC, ServidorPC1Controller.EstadoPC.ONLINE,
                                    socket.getInetAddress().getHostAddress());
                        }
                    }
                    
                    TipoMensaje comando = mensajeRecibido.getTipo();

                    switch(comando){
                        case CHAT_MENSAJE:
                            if (controlador != null) {
                                controlador.difundirMensajeChat(mensajeRecibido);
                            }
                            break;

                        case REGISTRAR_TICKET:
                            Ticket nuevo = (Ticket) mensajeRecibido.getPayload();
                            repositorio.clasificarYEncolar(nuevo);
                            if (controlador != null) controlador.actualizarContadores();
                            break;

                        case SOLICITAR_TICKET:
                            String cola = (String) mensajeRecibido.getPayload();
                            Ticket paraPC = "URGENTE".equals(cola) 
                                            ? repositorio.extraerPC4Prioridad() 
                                            : repositorio.extraerPC3Normal();

                            enviarRespuesta(new Mensaje(TipoMensaje.ENTREGAR_TICKET, paraPC, "PC1"));
                            if (controlador != null) controlador.actualizarContadores();
                            break;

                        case BUSCAR_DPI:
                            String dpiABuscar = (String) mensajeRecibido.getPayload();
                            List<Ticket> historial = repositorio.buscarHistorialPorDPI(dpiABuscar);
                            enviarRespuesta(new Mensaje(TipoMensaje.RESPUESTA_DPI, historial, "PC1_SERVER"));
                            break;

                        case FINALIZAR_ATENCION:
                            Ticket terminado = (Ticket) mensajeRecibido.getPayload();
                            repositorio.guardarTicketFinalizado(terminado);
                            break;

                        case DESCONECTAR:
                            return;
                    }
                } catch (EOFException e) {
                    break;
                }
            }
        } catch (Exception e) {
            if (controlador != null) {
                controlador.actualizarLog("Conexión perdida con: " + nombrePC);
            }
        } finally {
            if (!cierreOrdenado && !nombrePC.equals("Desconocido") && controlador != null) {
                controlador.actualizarEstadoPC(nombrePC, ServidorPC1Controller.EstadoPC.ERROR, "---");
            }
            cerrarConexion();
        }
    }

    public void enviarRespuesta(Mensaje msg){
        try{
            if (out != null && !socket.isClosed()){
                out.writeObject(msg);
                out.flush();
                out.reset();
            }
        }catch(IOException e){
            System.err.println("Error al enviar mensaje a cliente.");
        }
    }
    
    

    public void cerrarConexion(){
        try{
            if (out != null) out.close();
            if (socket != null && !socket.isClosed()) socket.close();
        }catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void avisarCierreServidor() {
        cierreOrdenado = true;
        Mensaje alerta = new Mensaje(TipoMensaje.SERVIDOR_DETENIDO, "El servidor se ha desconectado.", "PC1");
        enviarRespuesta(alerta);
        try { Thread.sleep(100); } catch (InterruptedException e) {}
        cerrarConexion();
    }
}