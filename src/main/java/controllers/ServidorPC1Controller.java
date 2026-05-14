    package controllers;

    import conexion.Configuracion;
    import conexion.DataRepository;
    import conexion.ManejadorCliente;
    import java.io.IOException;
    import java.net.ServerSocket;
    import java.net.Socket;
    import java.net.URL;
    import java.util.ArrayList;
    import java.util.HashMap;
    import java.util.List;
    import java.util.Map;
    import java.util.ResourceBundle;
    import javafx.application.Platform;
    import javafx.event.ActionEvent;
    import javafx.fxml.FXML;
    import javafx.fxml.Initializable;
    import javafx.scene.control.Button;
    import javafx.scene.control.Label;
    import javafx.scene.control.TextArea;
    import javafx.scene.Node;
    import javafx.scene.layout.VBox;
    import javafx.scene.shape.Circle;
    import sistema.sistemadesoportetecnicoit.shared.protocolo.Mensaje;

    public class ServidorPC1Controller implements Initializable {

        public enum EstadoPC { OFFLINE, ONLINE, ERROR }

        private static class PcCardRefs {
            final VBox card;
            final Node icon;
            final Circle led;
            final Label ip;
            final Label status;

            PcCardRefs(VBox card, Node icon, Circle led, Label ip, Label status) {
                this.card = card;
                this.icon = icon;
                this.led = led;
                this.ip = ip;
                this.status = status;
            }
        }

        private List<ManejadorCliente> clientesActivos = new ArrayList<>();
        private final List<ManejadorCliente> hilosClientes = new ArrayList<>();

        @FXML private TextArea txtLog;
        @FXML private Button btnStart;
        @FXML private Button btnStop;
        @FXML private Circle ledEstado;
        @FXML private Label lblColaNormal;
        @FXML private Label lblColaPrioridad;

        @FXML private VBox cardPC2; @FXML private Node iconPC2; @FXML private Circle ledPC2; @FXML private Label ipPC2; @FXML private Label statusPC2;
        @FXML private VBox cardPC3; @FXML private Node iconPC3; @FXML private Circle ledPC3; @FXML private Label ipPC3; @FXML private Label statusPC3;
        @FXML private VBox cardPC4; @FXML private Node iconPC4; @FXML private Circle ledPC4; @FXML private Label ipPC4; @FXML private Label statusPC4;
        @FXML private VBox cardPC5; @FXML private Node iconPC5; @FXML private Circle ledPC5; @FXML private Label ipPC5; @FXML private Label statusPC5;

        private final Map<String, PcCardRefs> tarjetas = new HashMap<>();

        private DataRepository repositorio = new DataRepository();
        private ServerSocket servidor;
        private boolean ejecutando = false;
        private final int PUERTO = Configuracion.PUERTO;
        private final String IP_LOCAL = Configuracion.HOST;

        @Override
        public void initialize(URL url, ResourceBundle rb) {
            actualizarLog("Sistema de Control de Servidor listo...");
            btnStop.setDisable(true);

            tarjetas.put("PC2", new PcCardRefs(cardPC2, iconPC2, ledPC2, ipPC2, statusPC2));
            tarjetas.put("PC3", new PcCardRefs(cardPC3, iconPC3, ledPC3, ipPC3, statusPC3));
            tarjetas.put("PC4", new PcCardRefs(cardPC4, iconPC4, ledPC4, ipPC4, statusPC4));
            tarjetas.put("PC5", new PcCardRefs(cardPC5, iconPC5, ledPC5, ipPC5, statusPC5));
        }

        public void actualizarEstadoPC(String nombre, EstadoPC estado, String ip) {
            Platform.runLater(() -> {
                PcCardRefs r = tarjetas.get(nombre);
                if (r == null) return;

                r.card.getStyleClass().removeAll("pc-card-offline", "pc-card-online", "pc-card-error");
                r.icon.getStyleClass().removeAll("pc-icon-offline", "pc-icon-online", "pc-icon-error");
                r.led.getStyleClass().removeAll("led-offline", "led-online", "led-error");
                r.status.getStyleClass().removeAll("pc-status-offline", "pc-status-online", "pc-status-error");

                if (estado == EstadoPC.ONLINE) {
                    r.card.getStyleClass().add("pc-card-online");
                    r.icon.getStyleClass().add("pc-icon-online");
                    r.led.getStyleClass().add("led-online");
                    r.status.getStyleClass().add("pc-status-online");
                    r.ip.setText(ip);
                    r.status.setText("ONLINE");
                } else if (estado == EstadoPC.ERROR) {
                    r.card.getStyleClass().add("pc-card-error");
                    r.icon.getStyleClass().add("pc-icon-error");
                    r.led.getStyleClass().add("led-error");
                    r.status.getStyleClass().add("pc-status-error");
                    r.ip.setText("---");
                    r.status.setText("PERDIDO");
                } else {
                    r.card.getStyleClass().add("pc-card-offline");
                    r.icon.getStyleClass().add("pc-icon-offline");
                    r.led.getStyleClass().add("led-offline");
                    r.status.getStyleClass().add("pc-status-offline");
                    r.ip.setText("---");
                    r.status.setText("OFFLINE");
                }
            });
        }

        @FXML
        private void iniciarServidor(ActionEvent event) {
            if (ejecutando) return;

            Thread hiloServidor = new Thread(() -> {
                try {
                    servidor = new ServerSocket(PUERTO, 50, java.net.InetAddress.getByName(IP_LOCAL));
                    ejecutando = true;

                    Platform.runLater(() -> {
                        ledEstado.setStyle("-fx-fill: #22ff00;");
                        btnStart.setDisable(true);
                        btnStop.setDisable(false);
                        actualizarLog("Servidor iniciado en puerto " + PUERTO);
                        actualizarLog("Esperando conexiones de PC2, PC3, PC4 y PC5...");
                    });

                    while (ejecutando) {
                        Socket cliente = servidor.accept();
                        actualizarLog("Nueva conexion detectada: " + cliente.getInetAddress());

                        ManejadorCliente manejador = new ManejadorCliente(cliente, repositorio, this);
                        synchronized (hilosClientes) {
                            hilosClientes.add(manejador);
                        }
                        Thread hilo = new Thread(manejador);
                        hilo.start();
                    }
                } catch (IOException e) {
                    if (!ejecutando) {
                        actualizarLog("Servidor detenido por el usuario");
                    } else {
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

        @FXML
        private void detenerServidor(ActionEvent event) {
            try {
                ejecutando = false;

                if (servidor != null && !servidor.isClosed()) {
                    servidor.close();
                }

                synchronized (hilosClientes) {
                    actualizarLog("Cerrando " + hilosClientes.size() + " conexiones activas...");
                    for (ManejadorCliente manejador : hilosClientes) {
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
                    for (String nombre : tarjetas.keySet()) {
                        actualizarEstadoPC(nombre, EstadoPC.OFFLINE, "---");
                    }
                });

            } catch (IOException e) {
                actualizarLog("Error al detener: " + e.getMessage());
            }
        }

        public void actualizarContadores() {
            Platform.runLater(() -> {
                try {
                    int normal = repositorio.getContadorNormal();
                    int prioridad = repositorio.getContadorPrioridad();
                    lblColaNormal.setText(String.valueOf(normal));
                    lblColaPrioridad.setText(String.valueOf(prioridad));
                } catch (Exception e) {
                    actualizarLog("Error al actualizar contadores: " + e.getMessage());
                }
            });
        }

        public void actualizarLog(String mensaje) {
            Platform.runLater(() -> {
                txtLog.appendText("[" + java.time.LocalTime.now().withNano(0) + "] " + mensaje + "\n");
            });
        }

        public void difundirMensajeChat(Mensaje mensajeRecibido) {
            synchronized (hilosClientes) {
                for (ManejadorCliente cliente : hilosClientes) {
                    try {
                        cliente.enviarRespuesta(mensajeRecibido);
                    } catch (Exception e) {
                        System.err.println("Error al difundir mensaje a un cliente: " + e.getMessage());
                    }
                }
            }
        }

        public void reportarIntruso(String mensaje) {
            Platform.runLater(() -> {
                txtLog.appendText("[" + java.time.LocalTime.now().withNano(0) + "] !!! ALERTA: " + mensaje.toUpperCase() + " !!!\n");
                System.err.println("SEGURIDAD: " + mensaje);
            });
        }
    }
