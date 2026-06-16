package models;

import java.util.Date;

public class HistorialCambioEstado {

    private Date fechaCambio;
    private String estadoAnterior;
    private String estadoNuevo;
    private String tipoEntidad;
    private String referencia;
    private String usuarioResponsable;

    public HistorialCambioEstado(Date fechaCambio, String estadoAnterior, String estadoNuevo,
                                  String tipoEntidad, String referencia, String usuarioResponsable) {
        this.fechaCambio = fechaCambio;
        this.estadoAnterior = estadoAnterior;
        this.estadoNuevo = estadoNuevo;
        this.tipoEntidad = tipoEntidad;
        this.referencia = referencia;
        this.usuarioResponsable = usuarioResponsable;
    }

    public Date getFechaCambio() { return fechaCambio; }
    public String getEstadoAnterior() { return estadoAnterior; }
    public String getEstadoNuevo() { return estadoNuevo; }
    public String getTipoEntidad() { return tipoEntidad; }
    public String getReferencia() { return referencia; }
    public String getUsuarioResponsable() { return usuarioResponsable; }
}
