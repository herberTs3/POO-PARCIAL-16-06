package models;

import java.util.Date;

import models.enums.EstadoPago;
import models.enums.MedioPago;

public class Pago {

    private Date fecha;
    private double importe;
    private MedioPago medioPago;
    private EstadoPago estado;
    private String usuarioRegistro;

    public Pago() {}

    public void inicializar(Date fecha, double importe, MedioPago medioPago,
                             EstadoPago estado, String usuarioRegistro) {
        this.fecha = fecha;
        this.importe = importe;
        this.medioPago = medioPago;
        this.estado = estado;
        this.usuarioRegistro = usuarioRegistro;
    }

    public void confirmar() {
        estado = EstadoPago.CONFIRMADO;
    }

    public void anular() {
        estado = EstadoPago.ANULADO;
    }

    public Date getFecha() { return fecha; }
    public double getImporte() { return importe; }
    public MedioPago getMedioPago() { return medioPago; }
    public EstadoPago getEstado() { return estado; }
    public String getUsuarioRegistro() { return usuarioRegistro; }
}
