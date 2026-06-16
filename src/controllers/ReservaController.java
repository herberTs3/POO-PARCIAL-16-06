package controllers;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import models.EspacioDeportivo;
import models.enums.TipoEspacio;

public class ReservaController {

    private static ReservaController instance;

    private ReservaController() {}

    public static ReservaController getInstance() {
        if (instance == null) {
            instance = new ReservaController();
        }
        return instance;
    }

    public String solicitarReserva(String dni, String codigoComplejo, String codigoEspacio,
                                    Date fecha, Time horaInicio, Time horaFin,
                                    String tipoReserva, String usuario) {
        throw new UnsupportedOperationException("Pendiente de implementación por Persona A");
    }

    public List<EspacioDeportivo> consultarEspaciosDisponibles(String codigoComplejo, Date fecha,
                                                                Time horaInicio, Time horaFin,
                                                                TipoEspacio tipoActividad) {
        return new ArrayList<>();
    }
}
