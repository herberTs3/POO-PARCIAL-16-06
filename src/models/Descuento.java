package models;

import java.util.Date;

public class Descuento {

    private double porcentaje;
    private Date fechaDesde;
    private Date fechaHasta;

    public Descuento(double porcentaje, Date fechaDesde, Date fechaHasta) {
        this.porcentaje = porcentaje;
        this.fechaDesde = fechaDesde;
        this.fechaHasta = fechaHasta;
    }

    public boolean estaVigente(Date fecha) {
        return !fecha.before(fechaDesde) && !fecha.after(fechaHasta);
    }

    public double getPorcentaje() { return porcentaje; }
    public Date getFechaDesde() { return fechaDesde; }
    public Date getFechaHasta() { return fechaHasta; }
}
