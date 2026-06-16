package tests;

import java.sql.Time;
import java.util.Calendar;
import java.util.Date;

import controllers.ClienteController;
import controllers.ComplejoDeportivoController;
import controllers.ReservaController;
import models.Reserva;
import models.enums.EstadoReserva;
import models.enums.MedioPago;

public class FlujoReservaTest {

    private static String codigoReservaPrincipal;

    public static void main(String[] args) {
        ClienteController.getInstance().cargarDatosDePrueba();
        ComplejoDeportivoController.getInstance().cargarDatosDePrueba();

        int passed = 0;

        passed += run("testSolicitarReservaEstadoIngresada",    FlujoReservaTest::testSolicitarReservaEstadoIngresada);
        passed += run("testConfirmarReservaConSena",             FlujoReservaTest::testConfirmarReservaConSena);
        passed += run("testIniciarUsoReservaCambiaEstado",       FlujoReservaTest::testIniciarUsoReservaCambiaEstado);
        passed += run("testFinalizarReservaCalculaSaldo",        FlujoReservaTest::testFinalizarReservaCalculaSaldo);
        passed += run("testCancelarReservaDevuelveCredito",      FlujoReservaTest::testCancelarReservaDevuelveCredito);

        System.out.println("\nResultado: " + passed + "/5 pasaron, " + (5 - passed) + " fallaron.");
    }

    private static int run(String name, Runnable test) {
        try {
            test.run();
            System.out.println("PASS: " + name);
            return 1;
        } catch (AssertionError e) {
            System.out.println("FAIL: " + name + " — " + e.getMessage());
            return 0;
        } catch (Exception e) {
            System.out.println("FAIL: " + name + " — EXCEPCION: " + e.getMessage());
            return 0;
        }
    }

    private static void testSolicitarReservaEstadoIngresada() {
        Reserva reserva = ReservaController.getInstance().solicitarReserva(
                "12345678", "C01", "E01",
                diasDesdeHoy(2), Time.valueOf("10:00:00"), Time.valueOf("11:00:00"),
                "COMUN", "admin");

        codigoReservaPrincipal = reserva.getCodigo();

        if (reserva.getEstado() != EstadoReserva.INGRESADA)
            throw new AssertionError("Estado esperado INGRESADA pero fue " + reserva.getEstado());
    }

    private static void testConfirmarReservaConSena() {
        ReservaController.getInstance().confirmarReservaConSena(
                codigoReservaPrincipal, 500.0, MedioPago.TARJETA, "admin");

        Reserva reserva = buscar(codigoReservaPrincipal);

        if (reserva.getEstado() != EstadoReserva.CONFIRMADA)
            throw new AssertionError("Estado esperado CONFIRMADA pero fue " + reserva.getEstado());

        if (Math.abs(reserva.obtenerImporteSena() - 500.0) >= 0.01)
            throw new AssertionError("Sena esperada 500.0 pero fue " + reserva.obtenerImporteSena());
    }

    private static void testIniciarUsoReservaCambiaEstado() {
        ReservaController.getInstance().iniciarUso(codigoReservaPrincipal, "admin");

        Reserva reserva = buscar(codigoReservaPrincipal);

        if (reserva.getEstado() != EstadoReserva.EN_CURSO)
            throw new AssertionError("Estado esperado EN_CURSO pero fue " + reserva.getEstado());
    }

    private static void testFinalizarReservaCalculaSaldo() {
        double saldo = ReservaController.getInstance().finalizarReserva(codigoReservaPrincipal, "admin");

        Reserva reserva = buscar(codigoReservaPrincipal);

        if (reserva.getEstado() != EstadoReserva.FINALIZADA)
            throw new AssertionError("Estado esperado FINALIZADA pero fue " + reserva.getEstado());

        double esperado = 2000.0 - 500.0;
        if (Math.abs(saldo - esperado) >= 0.01)
            throw new AssertionError("Saldo pendiente esperado " + esperado + " pero fue " + saldo);
    }

    private static void testCancelarReservaDevuelveCredito() {
        Reserva reserva = ReservaController.getInstance().solicitarReserva(
                "87654321", "C01", "E02",
                diasDesdeHoy(5), Time.valueOf("14:00:00"), Time.valueOf("15:00:00"),
                "COMUN", "admin");

        ReservaController.getInstance().confirmarReservaConSena(
                reserva.getCodigo(), 300.0, MedioPago.EFECTIVO, "admin");

        double creditoAntes = reserva.obtenerCliente().getCreditoAFavor();

        double devuelto = ReservaController.getInstance().cancelarReserva(
                reserva.getCodigo(), new Date(), "admin");

        double creditoDespues = reserva.obtenerCliente().getCreditoAFavor();

        if (Math.abs(devuelto - 300.0) >= 0.01)
            throw new AssertionError("Credito devuelto esperado 300.0 pero fue " + devuelto);

        if (Math.abs((creditoDespues - creditoAntes) - 300.0) >= 0.01)
            throw new AssertionError("CreditoAFavor esperado +" + 300.0 + " pero fue " + (creditoDespues - creditoAntes));
    }

    private static Date diasDesdeHoy(int dias) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, dias);
        return cal.getTime();
    }

    private static Reserva buscar(String codigo) {
        for (Reserva r : ReservaController.getInstance().listarTodas()) {
            if (r.getCodigo().equals(codigo)) return r;
        }
        throw new AssertionError("Reserva no encontrada: " + codigo);
    }
}
