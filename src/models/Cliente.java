package models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import models.enums.EstadoCliente;

public class Cliente {

    private String dni;
    private String nombre;
    private String apellido;
    private String telefono;
    private String email;
    private EstadoCliente estado;
    private double creditoAFavor;
    private List<Descuento> descuentos;

    public Cliente(String dni, String nombre, String apellido,
                   String telefono, String email, EstadoCliente estado) {
        this.dni = dni;
        this.nombre = nombre;
        this.apellido = apellido;
        this.telefono = telefono;
        this.email = email;
        this.estado = estado;
        this.creditoAFavor = 0.0;
        this.descuentos = new ArrayList<>();
    }

    public boolean estaActivo() {
        return estado == EstadoCliente.ACTIVO;
    }

    public void agregarCredito(double importe) {
        creditoAFavor += importe;
    }

    public Descuento obtenerDescuentoVigente(Date fecha) {
        for (Descuento descuento : descuentos) {
            if (descuento.estaVigente(fecha)) return descuento;
        }
        return null;
    }

    public void agregarDescuento(Descuento descuento) {
        descuentos.add(descuento);
    }

    public boolean coincideDni(String dni) {
        return this.dni.equals(dni);
    }

    public String getDni() { return dni; }
    public String getNombre() { return nombre; }
    public String getApellido() { return apellido; }
    public String getTelefono() { return telefono; }
    public String getEmail() { return email; }
    public EstadoCliente getEstado() { return estado; }
    public void setEstado(EstadoCliente estado) { this.estado = estado; }
    public double getCreditoAFavor() { return creditoAFavor; }
}
