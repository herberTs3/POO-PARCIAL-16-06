package controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import models.ComplejoDeportivo;
import models.EspacioDeportivo;
import models.enums.EstadoComplejo;
import models.enums.EstadoEspacio;
import models.enums.TipoEspacio;

public class ComplejoDeportivoController {

    private static ComplejoDeportivoController instance;
    private HashMap<String, ComplejoDeportivo> complejos;

    private ComplejoDeportivoController() {
        this.complejos = new HashMap<>();
    }

    public static ComplejoDeportivoController getInstance() {
        if (instance == null) {
            instance = new ComplejoDeportivoController();
        }
        return instance;
    }

    public void registrarComplejo(String codigo, String nombre, String direccion,
                                   String telefono, String email) {
        ComplejoDeportivo complejo = new ComplejoDeportivo(
            codigo, nombre, direccion, telefono, email, EstadoComplejo.ACTIVO
        );
        complejos.put(codigo, complejo);
    }

    public void agregarEspacioAComplejo(String codigoComplejo, EspacioDeportivo espacio) {
        ComplejoDeportivo complejo = buscarPorCodigo(codigoComplejo);
        if (complejo != null) {
            complejo.agregarEspacio(espacio);
        }
    }

    public ComplejoDeportivo buscarPorCodigo(String codigo) {
        for (ComplejoDeportivo complejo : complejos.values()) {
            if (complejo.coincideCodigo(codigo)) return complejo;
        }
        return null;
    }

    public List<ComplejoDeportivo> listarTodos() {
        return new ArrayList<>(complejos.values());
    }

    public void cargarDatosDePrueba() {
        registrarComplejo("C01", "Complejo Norte", "Av. Corrientes 1234", "1145001234", "norte@mail.com");
        agregarEspacioAComplejo("C01", new EspacioDeportivo("E01", "Cancha Fútbol 1", 22, 2000.0, EstadoEspacio.DISPONIBLE, TipoEspacio.FUTBOL, "Césped sintético"));
        agregarEspacioAComplejo("C01", new EspacioDeportivo("E02", "Cancha Tenis 1", 4, 1500.0, EstadoEspacio.DISPONIBLE, TipoEspacio.TENIS, "Polvo de ladrillo"));
        agregarEspacioAComplejo("C01", new EspacioDeportivo("E03", "Cancha Pádel 1", 4, 1200.0, EstadoEspacio.DISPONIBLE, TipoEspacio.PADEL, "Cristal"));

        registrarComplejo("C02", "Complejo Sur", "Av. Rivadavia 5678", "1156789012", "sur@mail.com");
        agregarEspacioAComplejo("C02", new EspacioDeportivo("E04", "Salón Multiuso A", 30, 3000.0, EstadoEspacio.DISPONIBLE, TipoEspacio.SALON_MULTIUSO, "Parquet"));
        agregarEspacioAComplejo("C02", new EspacioDeportivo("E05", "Cancha Fútbol 2", 22, 2500.0, EstadoEspacio.DISPONIBLE, TipoEspacio.FUTBOL, "Césped natural"));
    }
}
