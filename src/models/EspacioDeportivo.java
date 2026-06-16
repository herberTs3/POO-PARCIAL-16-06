package models;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import models.enums.EstadoEspacio;
import models.enums.TipoEspacio;

public class EspacioDeportivo {

    private String codigo;
    private String nombre;
    private int capacidad;
    private double precioBaseHora;
    private EstadoEspacio estado;
    private TipoEspacio tipoEspacio;
    private String superficie;
    private List<long[]> occupiedSlots;

    public EspacioDeportivo(String codigo, String nombre, int capacidad, double precioBaseHora,
                             EstadoEspacio estado, TipoEspacio tipoEspacio, String superficie) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.capacidad = capacidad;
        this.precioBaseHora = precioBaseHora;
        this.estado = estado;
        this.tipoEspacio = tipoEspacio;
        this.superficie = superficie;
        this.occupiedSlots = new ArrayList<>();
    }

    public boolean estaDisponible(Date fecha, Time horaInicio, Time horaFin) {
        if (estado != EstadoEspacio.DISPONIBLE) return false;
        long start = toMillis(fecha, horaInicio);
        long end = toMillis(fecha, horaFin);
        for (long[] slot : occupiedSlots) {
            if (start < slot[1] && end > slot[0]) return false;
        }
        return true;
    }

    public void ocuparSlot(Date fecha, Time horaInicio, Time horaFin) {
        occupiedSlots.add(new long[]{toMillis(fecha, horaInicio), toMillis(fecha, horaFin)});
    }

    public double calcularPrecioBase(double cantidadHoras) {
        return precioBaseHora * cantidadHoras;
    }

    public boolean coincideCodigo(String codigo) {
        return this.codigo.equals(codigo);
    }

    public boolean coincideTipoActividad(TipoEspacio tipoActividad) {
        return this.tipoEspacio == tipoActividad;
    }

    private long toMillis(Date fecha, Time hora) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(fecha);
        Calendar horaCal = Calendar.getInstance();
        horaCal.setTime(hora);
        cal.set(Calendar.HOUR_OF_DAY, horaCal.get(Calendar.HOUR_OF_DAY));
        cal.set(Calendar.MINUTE, horaCal.get(Calendar.MINUTE));
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }

    public String getCodigo() { return codigo; }
    public String getNombre() { return nombre; }
    public int getCapacidad() { return capacidad; }
    public double getPrecioBaseHora() { return precioBaseHora; }
    public EstadoEspacio getEstado() { return estado; }
    public void setEstado(EstadoEspacio estado) { this.estado = estado; }
    public TipoEspacio getTipoEspacio() { return tipoEspacio; }
    public String getSuperficie() { return superficie; }
}
