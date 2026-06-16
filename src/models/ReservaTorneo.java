package models;

public class ReservaTorneo extends Reserva {

    private double porcentajeRecargo;

    public ReservaTorneo() {
        this.porcentajeRecargo = 20.0;
    }

    public ReservaTorneo(double porcentajeRecargo) {
        this.porcentajeRecargo = porcentajeRecargo;
    }

    @Override
    public double calcularRecargo() {
        return calcularPrecioBase() * porcentajeRecargo / 100;
    }

    public double getPorcentajeRecargo() { return porcentajeRecargo; }
    public void setPorcentajeRecargo(double porcentajeRecargo) { this.porcentajeRecargo = porcentajeRecargo; }
}
