package models;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import models.enums.EstadoComplejo;
import models.enums.TipoEspacio;

public class ComplejoDeportivo {

    private String codigo;
    private String nombre;
    private String direccion;
    private String telefono;
    private String email;
    private EstadoComplejo estado;
    private List<EspacioDeportivo> espacios;

    public ComplejoDeportivo(String codigo, String nombre, String direccion,
                              String telefono, String email, EstadoComplejo estado) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.direccion = direccion;
        this.telefono = telefono;
        this.email = email;
        this.estado = estado;
        this.espacios = new ArrayList<>();
    }

    public void agregarEspacio(EspacioDeportivo espacio) {
        espacios.add(espacio);
    }

    public EspacioDeportivo buscarEspacio(String codigoEspacio) {
        for (EspacioDeportivo espacio : espacios) {
            if (espacio.coincideCodigo(codigoEspacio)) return espacio;
        }
        return null;
    }

    public List<EspacioDeportivo> obtenerEspacios() {
        return espacios;
    }

    public List<EspacioDeportivo> obtenerEspaciosDisponibles(Date fecha, Time horaInicio,
                                                              Time horaFin, TipoEspacio tipoActividad) {
        List<EspacioDeportivo> disponibles = new ArrayList<>();
        for (EspacioDeportivo espacio : espacios) {
            if (espacio.coincideTipoActividad(tipoActividad) && espacio.estaDisponible(fecha, horaInicio, horaFin)) {
                disponibles.add(espacio);
            }
        }
        return disponibles;
    }

    public boolean coincideCodigo(String codigo) {
        return this.codigo.equals(codigo);
    }

    public String getCodigo() { return codigo; }
    public String getNombre() { return nombre; }
    public String getDireccion() { return direccion; }
    public String getTelefono() { return telefono; }
    public String getEmail() { return email; }
    public EstadoComplejo getEstado() { return estado; }
    public void setEstado(EstadoComplejo estado) { this.estado = estado; }
}
