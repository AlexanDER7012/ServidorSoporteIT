package sistema.sistemadesoportetecnicoit.shared.models;


import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Ticket implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String ticketId;
    private String dpi;
    private String nombreApellido;
    private String motivo;
    private String tipo;
    private String usuarioAtendio;
    private long tiempoEntrada;
    private long tiempoAtencion;
    private long tiempoFinal;
    private boolean prioridad;

    public Ticket() {
    }
    
    public Ticket(String dpi, String nombreApellido, String motivo, String tipo, Boolean prioridad) {
        this(null, dpi, nombreApellido, motivo, tipo, prioridad);
    }   
    public Ticket(String ticketId, String dpi, String nombreApellido, String motivo, String tipo, Boolean prioridad) {
        this.ticketId = ticketId;
        this.dpi = dpi;
        this.nombreApellido = nombreApellido;
        this.motivo = motivo;
        this.tipo = tipo;
        this.tiempoEntrada = System.currentTimeMillis();
        this.prioridad = prioridad;
    }
    public Ticket(String ticketId,String tipo, String motivo, String usuarioAtendio) {
        this.ticketId = ticketId;
        this.motivo = motivo;
        this.tipo = tipo;
        this.usuarioAtendio = usuarioAtendio;
    }
    
    public String getFechaHoraAtencion(){
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        return sdf.format(new Date(this.tiempoAtencion));
    }
    
    public String getFechaHoraEntrada() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        return sdf.format(new Date(this.tiempoEntrada));
    }
        
    public double getDuracionAtencionMinutos(){
        return (double) (tiempoFinal - tiempoAtencion)/60000;
    }
    public void marcarInicioAtencion(){ this.tiempoAtencion = System.currentTimeMillis();}
    
    public void marcarFinalAtencion(String tecnico) { 
        this.tiempoFinal = System.currentTimeMillis(); 
        this.usuarioAtendio = tecnico;
    }
    
    public void reconstruirTiemposDesdeMinutos(double minutos){
        this.tiempoFinal = System.currentTimeMillis();
        this.tiempoAtencion = this.tiempoFinal - (long)(minutos * 60000);
    }
    public String getTicketId() {
        return ticketId;
    }

    public void setTicketId(String ticketId) {
        this.ticketId = ticketId;
    }

    public boolean isPrioridad() {
        return prioridad;
    }

    public void setPrioridad(boolean prioridad) {
        this.prioridad = prioridad;
    }

    public String getDpi() {
        return dpi;
    }

    public void setDpi(String dpi) {
        this.dpi = dpi;
    }

    public String getNombreApellido() {
        return nombreApellido;
    }

    public void setNombreApellido(String nombreApellido) {
        this.nombreApellido = nombreApellido;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getUsuarioAtendio() {
        return usuarioAtendio;
    }

    public void setUsuarioAtendio(String usuarioAtendio) {
        this.usuarioAtendio = usuarioAtendio;
    }

    public long getTiempoEntrada() {
        return tiempoEntrada;
    }

    public void setTiempoEntrada(long tiempoEntrada) {
        this.tiempoEntrada = tiempoEntrada;
    }

    public long getTiempoAtencion() {
        return tiempoAtencion;
    }

    public void setTiempoAtencion(long tiempoAtencion) {
        this.tiempoAtencion = tiempoAtencion;
    }

    public long getTiempoFinal() {
        return tiempoFinal;
    }

    public void setTiempoFinal(long tiempoFinal) {
        this.tiempoFinal = tiempoFinal;
    }
    
    
}



