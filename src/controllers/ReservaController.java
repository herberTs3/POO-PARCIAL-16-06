package controllers;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import models.Cliente;
import models.ComplejoDeportivo;
import models.Descuento;
import models.EspacioDeportivo;
import models.Pago;
import models.Reserva;
import models.ReservaClaseGrupal;
import models.ReservaComun;
import models.ReservaTorneo;
import models.enums.EstadoPago;
import models.enums.EstadoReserva;
import models.enums.MedioPago;
import models.enums.TipoEspacio;

public class ReservaController {

    private static ReservaController instance;
    private HashMap<String, Reserva> reservas;

    private ReservaController() {
        this.reservas = new HashMap<>();
    }

    public static ReservaController getInstance() {
        if (instance == null) {
            instance = new ReservaController();
        }
        return instance;
    }

    public Reserva solicitarReserva(String dni, String codigoComplejo, String codigoEspacio,
                                     Date fecha, Time horaInicio, Time horaFin,
                                     String tipoReserva, String usuario) {
        Cliente clienteEncontrado = ClienteController.getInstance().buscarPorDni(dni);
        if (clienteEncontrado == null) throw new IllegalArgumentException("Cliente no encontrado con DNI: " + dni);

        ComplejoDeportivo complejoEncontrado = ComplejoDeportivoController.getInstance().buscarPorCodigo(codigoComplejo);
        if (complejoEncontrado == null) throw new IllegalArgumentException("Complejo no encontrado con código: " + codigoComplejo);

        EspacioDeportivo espacioEncontrado = complejoEncontrado.buscarEspacio(codigoEspacio);
        if (espacioEncontrado == null) throw new IllegalArgumentException("Espacio no encontrado con código: " + codigoEspacio);

        if (!espacioEncontrado.estaDisponible(fecha, horaInicio, horaFin)) {
            throw new IllegalStateException("El espacio no está disponible en la fecha y horario solicitado.");
        }

        Reserva reserva = crearInstanciaReserva(tipoReserva);
        reserva.inicializar(clienteEncontrado, complejoEncontrado, espacioEncontrado, fecha, horaInicio, horaFin);
        reserva.cambiarEstado(EstadoReserva.INGRESADA, usuario);
        espacioEncontrado.ocuparSlot(fecha, horaInicio, horaFin);
        reservas.put(reserva.getCodigo(), reserva);

        HistorialController.getInstance().registrarCambio(
            null, "INGRESADA", "Reserva", reserva.getCodigo(), usuario
        );
        return reserva;
    }

    public void confirmarReservaConSena(String codigoReserva, double importeSena,
                                         MedioPago medioPago, String usuario) {
        Reserva reserva = buscarPorCodigo(codigoReserva);
        if (reserva == null) throw new IllegalArgumentException("Reserva no encontrada: " + codigoReserva);

        Pago pago = new Pago();
        pago.inicializar(new Date(), importeSena, medioPago, EstadoPago.REGISTRADO, usuario);
        pago.confirmar();

        reserva.agregarPago(pago);
        reserva.registrarImporteSena(importeSena);
        reserva.cambiarEstado(EstadoReserva.CONFIRMADA, usuario);

        HistorialController.getInstance().registrarCambio(
            "INGRESADA", "CONFIRMADA", "Reserva", codigoReserva, usuario
        );
    }

    public void cancelarReserva(String codigoReserva, Date fechaCancelacion, String usuario) {
        Reserva reserva = buscarPorCodigo(codigoReserva);
        if (reserva == null) throw new IllegalArgumentException("Reserva no encontrada: " + codigoReserva);

        Cliente cliente = reserva.obtenerCliente();
        long horasAnticipacion = reserva.calcularHorasAnticipacion(fechaCancelacion);
        double importeSena = reserva.obtenerImporteSena();

        if (horasAnticipacion > 24) {
            cliente.agregarCredito(importeSena);
        }

        reserva.cambiarEstado(EstadoReserva.CANCELADA, usuario);

        HistorialController.getInstance().registrarCambio(
            "CONFIRMADA", "CANCELADA", "Reserva", codigoReserva, usuario
        );
    }

    public double finalizarReserva(String codigoReserva, String usuario) {
        Reserva reserva = buscarPorCodigo(codigoReserva);
        if (reserva == null) throw new IllegalArgumentException("Reserva no encontrada: " + codigoReserva);

        reserva.cambiarEstado(EstadoReserva.FINALIZADA, usuario);

        Cliente cliente = reserva.obtenerCliente();
        Descuento descuento = cliente.obtenerDescuentoVigente(reserva.getFecha());

        reserva.calcularPrecioBase();
        reserva.calcularRecargo();

        if (descuento != null) {
            reserva.aplicarDescuento(descuento.getPorcentaje());
        }

        double saldoPendiente = reserva.calcularSaldoPendiente();

        HistorialController.getInstance().registrarCambio(
            "EN_CURSO", "FINALIZADA", "Reserva", codigoReserva, usuario
        );
        return saldoPendiente;
    }

    public List<EspacioDeportivo> consultarEspaciosDisponibles(String codigoComplejo, Date fecha,
                                                                Time horaInicio, Time horaFin,
                                                                TipoEspacio tipoActividad) {
        ComplejoDeportivo complejo = ComplejoDeportivoController.getInstance().buscarPorCodigo(codigoComplejo);
        if (complejo == null) throw new IllegalArgumentException("Complejo no encontrado: " + codigoComplejo);

        List<EspacioDeportivo> resultado = new ArrayList<>();
        for (EspacioDeportivo espacio : complejo.obtenerEspacios()) {
            if (espacio.coincideTipoActividad(tipoActividad)) {
                if (espacio.estaDisponible(fecha, horaInicio, horaFin)) {
                    resultado.add(espacio);
                }
            }
        }
        return resultado;
    }

    public double totalRecaudadoPorComplejo(String codigoComplejo, Date desde, Date hasta) {
        double total = 0.0;
        for (Reserva reserva : reservas.values()) {
            if (reserva.getEstado() == EstadoReserva.FINALIZADA
                    && reserva.getComplejo().getCodigo().equals(codigoComplejo)
                    && !reserva.getFecha().before(desde)
                    && !reserva.getFecha().after(hasta)) {
                total += reserva.calcularTotal();
            }
        }
        return total;
    }

    public List<Reserva> reservasConfirmadasPorCliente(String dni) {
        List<Reserva> resultado = new ArrayList<>();
        for (Reserva reserva : reservas.values()) {
            if (reserva.getEstado() == EstadoReserva.CONFIRMADA
                    && reserva.obtenerCliente().getDni().equals(dni)) {
                resultado.add(reserva);
            }
        }
        return resultado;
    }

    public List<Reserva> listarTodas() {
        return new ArrayList<>(reservas.values());
    }

    private Reserva buscarPorCodigo(String codigo) {
        for (Reserva reserva : reservas.values()) {
            if (reserva.coincideCodigo(codigo)) return reserva;
        }
        return null;
    }

    private Reserva crearInstanciaReserva(String tipoReserva) {
        switch (tipoReserva.toUpperCase()) {
            case "TORNEO": return new ReservaTorneo();
            case "CLASE_GRUPAL": return new ReservaClaseGrupal();
            default: return new ReservaComun();
        }
    }
}
