package models;

public class ReservaClaseGrupal extends Reserva {

    private double porcentajeRecargo;

    public ReservaClaseGrupal() {
        this.porcentajeRecargo = 10.0;
    }

    public ReservaClaseGrupal(double porcentajeRecargo) {
        this.porcentajeRecargo = porcentajeRecargo;
    }

    @Override
    public double calcularRecargo() {
        return calcularPrecioBase() * porcentajeRecargo / 100;
    }

    public double getPorcentajeRecargo() { return porcentajeRecargo; }
    public void setPorcentajeRecargo(double porcentajeRecargo) { this.porcentajeRecargo = porcentajeRecargo; }
}
