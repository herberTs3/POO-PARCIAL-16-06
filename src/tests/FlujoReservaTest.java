package tests;

import java.sql.Time;
import java.util.Calendar;
import java.util.Date;

import controllers.ClienteController;
import controllers.ComplejoDeportivoController;
import controllers.ReservaController;
import models.Cliente;
import models.ComplejoDeportivo;
import models.EspacioDeportivo;
import models.Reserva;
import models.ReservaClaseGrupal;
import models.ReservaTorneo;
import models.enums.EstadoCliente;
import models.enums.EstadoComplejo;
import models.enums.EstadoEspacio;
import models.enums.EstadoReserva;
import models.enums.MedioPago;
import models.enums.TipoEspacio;

public class FlujoReservaTest {

    private static String codigoReservaPrincipal;

    public static void main(String[] args) {
        ClienteController.getInstance().cargarDatosDePrueba();
        ComplejoDeportivoController.getInstance().cargarDatosDePrueba();

        int passed = 0;

        passed += run("testTorneoAplicaRecargoDel20Porciento",       FlujoReservaTest::testTorneoAplicaRecargoDel20Porciento);
        passed += run("testClaseGrupalAplicaRecargoDel10Porciento",  FlujoReservaTest::testClaseGrupalAplicaRecargoDel10Porciento);
        passed += run("testSolicitarReservaEstadoIngresada",         FlujoReservaTest::testSolicitarReservaEstadoIngresada);
        passed += run("testConfirmarIniciarYFinalizarReserva",       FlujoReservaTest::testConfirmarIniciarYFinalizarReserva);
        passed += run("testCancelarMenos24HsPermitidoSinCredito",       FlujoReservaTest::testCancelarMenos24HsPermitidoSinCredito);

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

    private static void testTorneoAplicaRecargoDel20Porciento() {
        ReservaTorneo reserva = new ReservaTorneo();
        reserva.inicializar(crearCliente(), crearComplejo(), crearEspacio(),
                new Date(), Time.valueOf("10:00:00"), Time.valueOf("11:00:00"), "TORNEO");

        double precioBase = reserva.calcularPrecioBase();
        double recargo    = reserva.calcularRecargo();
        double esperado   = precioBase * 0.20;

        if (Math.abs(recargo - esperado) >= 0.01)
            throw new AssertionError("Recargo torneo esperado " + esperado + " pero fue " + recargo);
    }

    private static void testClaseGrupalAplicaRecargoDel10Porciento() {
        ReservaClaseGrupal reserva = new ReservaClaseGrupal();
        reserva.inicializar(crearCliente(), crearComplejo(), crearEspacio(),
                new Date(), Time.valueOf("10:00:00"), Time.valueOf("11:00:00"), "CLASE_GRUPAL");

        double precioBase = reserva.calcularPrecioBase();
        double recargo    = reserva.calcularRecargo();
        double esperado   = precioBase * 0.10;

        if (Math.abs(recargo - esperado) >= 0.01)
            throw new AssertionError("Recargo clase grupal esperado " + esperado + " pero fue " + recargo);
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

    private static void testConfirmarIniciarYFinalizarReserva() {
        ReservaController.getInstance().confirmarReservaConSena(
                codigoReservaPrincipal, 500.0, MedioPago.TRANSFERENCIA, "admin");

        Reserva reserva = buscar(codigoReservaPrincipal);
        if (reserva.getEstado() != EstadoReserva.CONFIRMADA)
            throw new AssertionError("Estado esperado CONFIRMADA pero fue " + reserva.getEstado());

        ReservaController.getInstance().iniciarUso(codigoReservaPrincipal, "admin");
        if (reserva.getEstado() != EstadoReserva.EN_CURSO)
            throw new AssertionError("Estado esperado EN_CURSO pero fue " + reserva.getEstado());

        double saldo = ReservaController.getInstance().finalizarReserva(codigoReservaPrincipal, "admin");
        if (reserva.getEstado() != EstadoReserva.FINALIZADA)
            throw new AssertionError("Estado esperado FINALIZADA pero fue " + reserva.getEstado());

        double saldoEsperado = reserva.calcularPrecioBase() - 500.0;
        if (Math.abs(saldo - saldoEsperado) >= 0.01)
            throw new AssertionError("Saldo esperado " + saldoEsperado + " pero fue " + saldo);
    }

    private static void testCancelarMenos24HsPermitidoSinCredito() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.HOUR_OF_DAY, 10);
        Date fechaReserva = cal.getTime();
        Time horaInicio = Time.valueOf(String.format("%02d:%02d:00",
                cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE)));
        Time horaFin = new Time(horaInicio.getTime() + 3_600_000L);

        Reserva reserva = ReservaController.getInstance().solicitarReserva(
                "11223344", "C02", "E04",
                fechaReserva, horaInicio, horaFin,
                "COMUN", "admin");

        ReservaController.getInstance().confirmarReservaConSena(
                reserva.getCodigo(), 300.0, MedioPago.EFECTIVO, "admin");

        double creditoAntes = reserva.obtenerCliente().getCreditoAFavor();

        double devuelto = ReservaController.getInstance().cancelarReserva(
                reserva.getCodigo(), new Date(), "admin");

        if (reserva.getEstado() != EstadoReserva.CANCELADA)
            throw new AssertionError("Deberia poder cancelarse con <24h pero estado fue " + reserva.getEstado());

        if (Math.abs(devuelto - 0.0) >= 0.01)
            throw new AssertionError("Sin reembolso esperado con <24h pero devolvio " + devuelto);

        if (Math.abs(reserva.obtenerCliente().getCreditoAFavor() - creditoAntes) >= 0.01)
            throw new AssertionError("No deberia acreditarse sena con menos de 24hs de anticipacion");
    }

    private static Cliente crearCliente() {
        return new Cliente("99999999", "Test", "Usuario", "123", "t@t.com", EstadoCliente.ACTIVO);
    }

    private static ComplejoDeportivo crearComplejo() {
        return new ComplejoDeportivo("T01", "Complejo Test", "Calle 0", "000", "t@t.com", EstadoComplejo.ACTIVO);
    }

    private static EspacioDeportivo crearEspacio() {
        return new EspacioDeportivo("ET1", "Espacio Test", 10, 100.0,
                EstadoEspacio.DISPONIBLE, TipoEspacio.FUTBOL, "Cesped");
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
