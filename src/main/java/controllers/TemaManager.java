package controllers;

import javafx.scene.Parent;

public class TemaManager {

    public enum Tema { OSCURO, CLARO }

    private static Tema temaActual = Tema.OSCURO;
    private static final String LIGHT_CSS = "/styles/sistema-light.css";

    private TemaManager() {}

    public static void toggle() {
        temaActual = (temaActual == Tema.OSCURO) ? Tema.CLARO : Tema.OSCURO;
    }

    public static void aplicar(Parent root) {
        String url = TemaManager.class.getResource(LIGHT_CSS).toExternalForm();
        if (temaActual == Tema.CLARO) {
            if (!root.getStylesheets().contains(url))
                root.getStylesheets().add(url);
        } else {
            root.getStylesheets().remove(url);
        }
    }

    public static Tema getTema() { return temaActual; }
}
