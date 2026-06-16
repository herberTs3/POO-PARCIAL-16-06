package controllers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import models.HistorialCambioEstado;

public class HistorialController {

    private static HistorialController instance;
    private List<HistorialCambioEstado> historiales;

    private HistorialController() {
        this.historiales = new ArrayList<>();
    }

    public static HistorialController getInstance() {
        if (instance == null) {
            instance = new HistorialController();
        }
        return instance;
    }

    public void registrarCambio(String estadoAnterior, String estadoNuevo,
                                 String tipoEntidad, String referencia, String usuario) {
        HistorialCambioEstado historial = new HistorialCambioEstado(
            new Date(), estadoAnterior, estadoNuevo, tipoEntidad, referencia, usuario
        );
        historiales.add(historial);
    }

    public List<HistorialCambioEstado> listarTodos() {
        return new ArrayList<>(historiales);
    }

    public List<HistorialCambioEstado> listarPorReferencia(String referencia) {
        List<HistorialCambioEstado> resultado = new ArrayList<>();
        for (HistorialCambioEstado h : historiales) {
            if (h.getReferencia().equals(referencia)) {
                resultado.add(h);
            }
        }
        return resultado;
    }
}
