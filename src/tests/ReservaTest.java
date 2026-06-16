package tests;

import java.sql.Time;
import java.util.Calendar;
import java.util.Date;

import models.Cliente;
import models.ComplejoDeportivo;
import models.EspacioDeportivo;
import models.ReservaClaseGrupal;
import models.ReservaComun;
import models.ReservaTorneo;
import models.enums.EstadoCliente;
import models.enums.EstadoComplejo;
import models.enums.EstadoEspacio;
import models.enums.TipoEspacio;

public class ReservaTest {

    public static void main(String[] args) {
        int passed = 0;
        int failed = 0;

        passed += run("testReservaTorneoRecargo20",        ReservaTest::testReservaTorneoRecargo20);
        passed += run("testReservaClaseGrupalRecargo10",   ReservaTest::testReservaClaseGrupalRecargo10);
        passed += run("testReservaComunSinRecargo",         ReservaTest::testReservaComunSinRecargo);
        passed += run("testCancelacionMas24HorasDevuelveCredito",
                       ReservaTest::testCancelacionMas24HorasDevuelveCredito);
        passed += run("testCancelacionMenos24HorasNoDevuelveCredito",
                       ReservaTest::testCancelacionMenos24HorasNoDevuelveCredito);

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
        }
    }

    private static EspacioDeportivo crearEspacio() {
        return new EspacioDeportivo("E01", "Cancha 1", 10, 100.0,
                EstadoEspacio.DISPONIBLE, TipoEspacio.FUTBOL, "Cesped");
    }

    private static ComplejoDeportivo crearComplejo() {
        return new ComplejoDeportivo("C01", "Complejo A", "Calle 1", "123", "a@a.com",
                EstadoComplejo.ACTIVO);
    }

    private static Cliente crearCliente() {
        return new Cliente("12345678", "Juan", "Perez", "123", "j@p.com", EstadoCliente.ACTIVO);
    }

    private static void testReservaTorneoRecargo20() {
        ReservaTorneo reserva = new ReservaTorneo();
        reserva.inicializar(crearCliente(), crearComplejo(), crearEspacio(),
                new Date(), Time.valueOf("10:00:00"), Time.valueOf("11:00:00"), "TORNEO");
        double recargo = reserva.calcularRecargo();
        if (Math.abs(recargo - 20.0) >= 0.01) {
            throw new AssertionError("Esperado 20.0 pero fue " + recargo);
        }
    }

    private static void testReservaClaseGrupalRecargo10() {
        ReservaClaseGrupal reserva = new ReservaClaseGrupal();
        reserva.inicializar(crearCliente(), crearComplejo(), crearEspacio(),
                new Date(), Time.valueOf("10:00:00"), Time.valueOf("11:00:00"), "CLASE_GRUPAL");
        double recargo = reserva.calcularRecargo();
        if (Math.abs(recargo - 10.0) >= 0.01) {
            throw new AssertionError("Esperado 10.0 pero fue " + recargo);
        }
    }

    private static void testReservaComunSinRecargo() {
        ReservaComun reserva = new ReservaComun();
        reserva.inicializar(crearCliente(), crearComplejo(), crearEspacio(),
                new Date(), Time.valueOf("10:00:00"), Time.valueOf("11:00:00"), "COMUN");
        double recargo = reserva.calcularRecargo();
        if (Math.abs(recargo - 0.0) >= 0.01) {
            throw new AssertionError("Esperado 0.0 pero fue " + recargo);
        }
    }

    private static void testCancelacionMas24HorasDevuelveCredito() {
        Cliente cliente = crearCliente();
        ReservaComun reserva = new ReservaComun();

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, 2);
        Date fechaReserva = cal.getTime();

        reserva.inicializar(cliente, crearComplejo(), crearEspacio(),
                fechaReserva, Time.valueOf("10:00:00"), Time.valueOf("11:00:00"), "COMUN");
        reserva.registrarImporteSena(500.0);

        long horasAnticipacion = reserva.calcularHorasAnticipacion(new Date());
        if (horasAnticipacion > 24) {
            cliente.agregarCredito(reserva.obtenerImporteSena());
        }

        if (Math.abs(cliente.getCreditoAFavor() - 500.0) >= 0.01) {
            throw new AssertionError("Esperado creditoAFavor=500.0 pero fue " + cliente.getCreditoAFavor());
        }
    }

    private static void testCancelacionMenos24HorasNoDevuelveCredito() {
        Cliente cliente = crearCliente();
        ReservaComun reserva = new ReservaComun();

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.HOUR_OF_DAY, 10);

        Date fechaReserva = cal.getTime();
        int h = cal.get(Calendar.HOUR_OF_DAY);
        int m = cal.get(Calendar.MINUTE);
        Time horaInicio = Time.valueOf(String.format("%02d:%02d:00", h, m));

        reserva.inicializar(cliente, crearComplejo(), crearEspacio(),
                fechaReserva, horaInicio, new Time(cal.getTimeInMillis() + 3_600_000L), "COMUN");
        reserva.registrarImporteSena(500.0);

        long horasAnticipacion = reserva.calcularHorasAnticipacion(new Date());
        if (horasAnticipacion > 24) {
            cliente.agregarCredito(reserva.obtenerImporteSena());
        }

        if (Math.abs(cliente.getCreditoAFavor() - 0.0) >= 0.01) {
            throw new AssertionError("Esperado creditoAFavor=0.0 pero fue " + cliente.getCreditoAFavor());
        }
    }
}
