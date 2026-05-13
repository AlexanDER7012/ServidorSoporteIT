    package controllers;

    import conexion.DataRepository;
    import conexion.ManejadorCliente;
    import java.io.IOException;
    import java.net.ServerSocket;
    import java.net.Socket;
    import java.net.URL;
    import java.util.ArrayList;
    import java.util.List;
    import java.util.ResourceBundle;
    import javafx.application.Platform;
    import javafx.event.ActionEvent;
    import javafx.fxml.FXML;
    import javafx.fxml.Initializable;
    import javafx.scene.control.Button;
    import javafx.scene.control.Label;
    import javafx.scene.control.TextArea;
    import javafx.scene.shape.Circle;
    import sistema.sistemadesoportetecnicoit.shared.protocolo.Mensaje;

    /**
     * FXML Controller class
     *
     * @author ErickPalomoTrabajos
     */
    public class ServidorPC1Controller implements Initializable {
        private List<ManejadorCliente> clientesActivos = new ArrayList<>();
        private final List<ManejadorCliente> hilosClientes = new ArrayList<>();
        @FXML
        private TextArea txtLog;
        @FXML
        private Button btnStart; 
        @FXML
        private Button btnStop; 
        @FXML
        private Circle ledEstado;
        @FXML
        private Label lblColaNormal;
        @FXML
        private Label lblColaPrioridad;

        private DataRepository repositorio = new DataRepository();
        private ServerSocket servidor;
        private boolean ejecutando = false;
        private final int PUERTO = 5000; 
        private final String IP_LOCAL = "172.20.10.3";


        @Override
        public void initialize(URL url, ResourceBundle rb) {
            actualizarLog("Sistema de Control de Servidor listo...");
            btnStop.setDisable(true); 
        }  

        @FXML
        private void iniciarServidor(ActionEvent event){
            if (ejecutando) return;

            Thread hiloServidor = new Thread(() -> {
                try{
            servidor = new ServerSocket(PUERTO, 50, java.net.InetAddress.getByName(IP_LOCAL));
            ejecutando = true;

                    Platform.runLater(() -> {
                        ledEstado.setStyle("-fx-fill: #22ff00;");
                        btnStart.setDisable(true);
                        btnStop.setDisable(false);
                        actualizarLog("Servidor iniciado en puerto " + PUERTO);
                        actualizarLog("Esperando conexiones de PC2, PC3, PC4 y PC5...");
                    });

                    while(ejecutando){
                        Socket cliente = servidor.accept(); 
                        actualizarLog("Nueva conexion detectada: " + cliente.getInetAddress());

                        ManejadorCliente manejador = new ManejadorCliente(cliente, repositorio, this); 
                        synchronized (hilosClientes) {
                            hilosClientes.add(manejador);
                        }
                        Thread hilo = new Thread(manejador);
                        hilo.start(); 
                    }
                }catch(IOException e){
                    if (!ejecutando) {
                        actualizarLog("Servidor detenido por el usuario");
                    }else{
                        actualizarLog("ERROR CRITICO: " + e.getMessage());
                        Platform.runLater(() -> {
                            ledEstado.setStyle("-fx-fill: #ff0000;");
                            btnStart.setDisable(false);
                            btnStop.setDisable(true);
                        });
                        ejecutando = false; 
                    }
                }
            });

            hiloServidor.setDaemon(true);
            hiloServidor.start();
        }

        public void actualizarContadores() {
        Platform.runLater(() -> {
            try{
                int normal = repositorio.getContadorNormal();
                int prioridad = repositorio.getContadorPrioridad();

                lblColaNormal.setText(String.valueOf(normal));
                lblColaPrioridad.setText(String.valueOf(prioridad));

            }catch(Exception e){
                actualizarLog("Error al actualizar contadores: " + e.getMessage());
            }
        });
    }

        @FXML
        private void detenerServidor(ActionEvent event) {
            try{
                ejecutando = false;

                if (servidor != null && !servidor.isClosed()) {
                    servidor.close();
                }

                synchronized (hilosClientes){
                    actualizarLog("Cerrando " + hilosClientes.size() + " conexiones activas...");
                    for (ManejadorCliente manejador : hilosClientes){
                        manejador.avisarCierreServidor();
                        manejador.cerrarConexion();
                    }
                    hilosClientes.clear();
                }

                Platform.runLater(() -> {
                    actualizarLog("Servidor OFFLINE");
                    ledEstado.setStyle("-fx-fill: #ff0000;");
                    btnStart.setDisable(false);
                    btnStop.setDisable(true);
                });

            }catch(IOException e){
                actualizarLog("Error al detener: " + e.getMessage());
            }
        }

        public void actualizarLog(String mensaje){
            Platform.runLater(() -> {
                txtLog.appendText("[" + java.time.LocalTime.now().withNano(0) + "] " + mensaje + "\n");
            });
        }

        public void difundirMensajeChat(Mensaje mensajeRecibido) {
        synchronized (hilosClientes){
            for (ManejadorCliente cliente : hilosClientes) {
                try{
                    cliente.enviarRespuesta(mensajeRecibido); 
                }catch(Exception e) {
                    System.err.println("Error al difundir mensaje a un cliente: " + e.getMessage());
                }
            }
        }
    }
    public void reportarIntruso(String mensaje){
            Platform.runLater(() -> {
                txtLog.appendText("[" + java.time.LocalTime.now().withNano(0) + "] !!! ALERTA: " + mensaje.toUpperCase() + " !!!\n");
                System.err.println("SEGURIDAD: " + mensaje); 
            });
        }


    }
