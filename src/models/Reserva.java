package models;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import models.enums.EstadoReserva;

public abstract class Reserva {

    private String codigo;
    private Date fecha;
    private Time horaInicio;
    private Time horaFin;
    private EstadoReserva estado;
    private double importeSena;
    private double descuentoAplicado;
    private Cliente cliente;
    private ComplejoDeportivo complejo;
    private EspacioDeportivo espacio;
    private List<Pago> pagos;

    public Reserva() {}

    public void inicializar(Cliente cliente, ComplejoDeportivo complejo, EspacioDeportivo espacio,
                             Date fecha, Time horaInicio, Time horaFin, String tipoReserva) {
        this.codigo = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        this.cliente = cliente;
        this.complejo = complejo;
        this.espacio = espacio;
        this.fecha = fecha;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.importeSena = 0.0;
        this.descuentoAplicado = 0.0;
        this.pagos = new ArrayList<>();
    }

    public abstract double calcularRecargo();

    public double calcularPrecioBase() {
        return espacio.calcularPrecioBase(calcularCantidadHoras());
    }

    public double calcularTotal() {
        double precioBase = calcularPrecioBase();
        double recargo = calcularRecargo();
        double descuento = precioBase * descuentoAplicado / 100;
        return precioBase + recargo - descuento;
    }

    public double calcularSaldoPendiente() {
        return calcularTotal() - importeSena;
    }

    public void aplicarDescuento(double porcentaje) {
        this.descuentoAplicado = porcentaje;
    }

    public void cambiarEstado(EstadoReserva nuevoEstado, String usuario) {
        this.estado = nuevoEstado;
    }

    public void registrarImporteSena(double importe) {
        this.importeSena = importe;
    }

    public void agregarPago(Pago pago) {
        pagos.add(pago);
    }

    public boolean estaConfirmada() {
        return estado == EstadoReserva.CONFIRMADA;
    }

    public boolean coincideCodigo(String codigo) {
        return this.codigo.equals(codigo);
    }

    public long calcularHorasAnticipacion(Date fechaCancelacion) {
        Calendar reservaCal = Calendar.getInstance();
        reservaCal.setTime(fecha);
        Calendar horaCal = Calendar.getInstance();
        horaCal.setTime(horaInicio);
        reservaCal.set(Calendar.HOUR_OF_DAY, horaCal.get(Calendar.HOUR_OF_DAY));
        reservaCal.set(Calendar.MINUTE, horaCal.get(Calendar.MINUTE));
        reservaCal.set(Calendar.SECOND, 0);
        reservaCal.set(Calendar.MILLISECOND, 0);
        long diffMillis = reservaCal.getTimeInMillis() - fechaCancelacion.getTime();
        return diffMillis / (1000 * 3600);
    }

    private double calcularCantidadHoras() {
        long diffMillis = horaFin.getTime() - horaInicio.getTime();
        return diffMillis / (1000.0 * 3600);
    }

    public Cliente obtenerCliente() { return cliente; }
    public double obtenerImporteSena() { return importeSena; }
    public String getCodigo() { return codigo; }
    public Date getFecha() { return fecha; }
    public Time getHoraInicio() { return horaInicio; }
    public Time getHoraFin() { return horaFin; }
    public EstadoReserva getEstado() { return estado; }
    public ComplejoDeportivo getComplejo() { return complejo; }
    public EspacioDeportivo getEspacio() { return espacio; }
    public List<Pago> getPagos() { return pagos; }
}
