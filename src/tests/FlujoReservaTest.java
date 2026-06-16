package tests;

import java.sql.Time;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import controllers.ClienteController;
import controllers.ComplejoDeportivoController;
import controllers.ReservaController;
import models.Cliente;
import models.ComplejoDeportivo;
import models.Descuento;
import models.EspacioDeportivo;
import models.Reserva;
import models.ReservaClaseGrupal;
import models.ReservaComun;
import models.ReservaTorneo;
import models.enums.EstadoCliente;
import models.enums.EstadoComplejo;
import models.enums.EstadoEspacio;
import models.enums.EstadoReserva;
import models.enums.MedioPago;
import models.enums.TipoEspacio;

public class FlujoReservaTest {

    private static String codigoReservaPrincipal;
    private static final int TOTAL_TESTS = 11;

    public static void main(String[] args) {
        ClienteController.getInstance().cargarDatosDePrueba();
        ComplejoDeportivoController.getInstance().cargarDatosDePrueba();

        int passed = 0;

        passed += run("testTorneoAplicaRecargoDel20Porciento",         FlujoReservaTest::testTorneoAplicaRecargoDel20Porciento);
        passed += run("testClaseGrupalAplicaRecargoDel10Porciento",    FlujoReservaTest::testClaseGrupalAplicaRecargoDel10Porciento);
        passed += run("testReservaComunNoAplicaRecargo",                FlujoReservaTest::testReservaComunNoAplicaRecargo);
        passed += run("testSolicitarReservaEstadoIngresada",           FlujoReservaTest::testSolicitarReservaEstadoIngresada);
        passed += run("testEspacioOcupadoLanzaExcepcion",              FlujoReservaTest::testEspacioOcupadoLanzaExcepcion);
        passed += run("testConfirmarIniciarYFinalizarReserva",         FlujoReservaTest::testConfirmarIniciarYFinalizarReserva);
        passed += run("testCancelarConMas24HsDevuelveSenaComoCredito", FlujoReservaTest::testCancelarConMas24HsDevuelveSenaComoCredito);
        passed += run("testCancelarConMenos24HsNoDevuelveSena",        FlujoReservaTest::testCancelarConMenos24HsNoDevuelveSena);
        passed += run("testFinalizarConDescuentoReduceSaldo",          FlujoReservaTest::testFinalizarConDescuentoReduceSaldo);
        passed += run("testConsultarEspaciosSinFiltroRetornaTodos",    FlujoReservaTest::testConsultarEspaciosSinFiltroRetornaTodos);
        passed += run("testConsultarEspaciosFiltrandoSlotOcupado",     FlujoReservaTest::testConsultarEspaciosFiltrandoSlotOcupado);

        System.out.println("\nResultado: " + passed + "/" + TOTAL_TESTS + " pasaron, " + (TOTAL_TESTS - passed) + " fallaron.");
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

    private static void testReservaComunNoAplicaRecargo() {
        ReservaComun reserva = new ReservaComun();
        reserva.inicializar(crearCliente(), crearComplejo(), crearEspacio(),
                new Date(), Time.valueOf("10:00:00"), Time.valueOf("11:00:00"), "COMUN");

        double recargo = reserva.calcularRecargo();

        if (Math.abs(recargo) >= 0.01)
            throw new AssertionError("Recargo común esperado 0.0 pero fue " + recargo);
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

    private static void testEspacioOcupadoLanzaExcepcion() {
        try {
            ReservaController.getInstance().solicitarReserva(
                    "87654321", "C01", "E01",
                    diasDesdeHoy(2), Time.valueOf("10:00:00"), Time.valueOf("11:00:00"),
                    "COMUN", "admin");
            throw new AssertionError("Se esperaba IllegalStateException por espacio ya ocupado");
        } catch (IllegalStateException e) {
            // comportamiento esperado
        }
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

    private static void testCancelarConMas24HsDevuelveSenaComoCredito() {
        Reserva reserva = ReservaController.getInstance().solicitarReserva(
                "87654321", "C01", "E02",
                diasDesdeHoy(5), Time.valueOf("14:00:00"), Time.valueOf("15:00:00"),
                "COMUN", "admin");

        ReservaController.getInstance().confirmarReservaConSena(
                reserva.getCodigo(), 300.0, MedioPago.EFECTIVO, "admin");

        Cliente cliente     = reserva.obtenerCliente();
        double creditoAntes = cliente.getCreditoAFavor();

        double devuelto = ReservaController.getInstance().cancelarReserva(
                reserva.getCodigo(), new Date(), "admin");

        if (Math.abs(devuelto - 300.0) >= 0.01)
            throw new AssertionError("Devuelto esperado 300.0 pero fue " + devuelto);

        if (Math.abs((cliente.getCreditoAFavor() - creditoAntes) - 300.0) >= 0.01)
            throw new AssertionError("CreditoAFavor no se incrementó correctamente");
    }

    private static void testCancelarConMenos24HsNoDevuelveSena() {
        // diasDesdeHoy(1) at 00:00 siempre es < 24h desde ahora
        Reserva reserva = ReservaController.getInstance().solicitarReserva(
                "12345678", "C01", "E02",
                diasDesdeHoy(1), Time.valueOf("00:00:00"), Time.valueOf("01:00:00"),
                "COMUN", "admin");

        ReservaController.getInstance().confirmarReservaConSena(
                reserva.getCodigo(), 200.0, MedioPago.EFECTIVO, "admin");

        Cliente cliente     = reserva.obtenerCliente();
        double creditoAntes = cliente.getCreditoAFavor();

        double devuelto = ReservaController.getInstance().cancelarReserva(
                reserva.getCodigo(), new Date(), "admin");

        if (Math.abs(devuelto) >= 0.01)
            throw new AssertionError("Devuelto esperado 0.0 pero fue " + devuelto);

        if (Math.abs(cliente.getCreditoAFavor() - creditoAntes) >= 0.01)
            throw new AssertionError("CreditoAFavor no debería cambiar al cancelar con menos de 24h");
    }

    private static void testFinalizarConDescuentoReduceSaldo() {
        Cliente carlos = ClienteController.getInstance().buscarPorDni("11223344");
        Calendar ayer = Calendar.getInstance();
        ayer.add(Calendar.DAY_OF_MONTH, -1);
        Calendar proxMes = Calendar.getInstance();
        proxMes.add(Calendar.DAY_OF_MONTH, 30);
        carlos.agregarDescuento(new Descuento(10.0, ayer.getTime(), proxMes.getTime()));

        // E03 (Pádel, 1200/h), 1h → precioBase=1200, recargo=0, descuento=120 → total=1080, saldo=680
        Reserva reserva = ReservaController.getInstance().solicitarReserva(
                "11223344", "C01", "E03",
                diasDesdeHoy(4), Time.valueOf("10:00:00"), Time.valueOf("11:00:00"),
                "COMUN", "admin");

        ReservaController.getInstance().confirmarReservaConSena(
                reserva.getCodigo(), 400.0, MedioPago.TRANSFERENCIA, "admin");
        ReservaController.getInstance().iniciarUso(reserva.getCodigo(), "admin");

        double saldo = ReservaController.getInstance().finalizarReserva(reserva.getCodigo(), "admin");

        double precioBase     = 1200.0;
        double descuento      = precioBase * 10.0 / 100;
        double saldoEsperado  = (precioBase - descuento) - 400.0;

        if (Math.abs(saldo - saldoEsperado) >= 0.01)
            throw new AssertionError("Saldo con descuento esperado " + saldoEsperado + " pero fue " + saldo);
    }

    private static void testConsultarEspaciosSinFiltroRetornaTodos() {
        // Con fecha=null se saltan todos los chequeos de disponibilidad → retorna los 3 espacios de C01
        List<EspacioDeportivo> resultado = ReservaController.getInstance()
                .consultarEspaciosDisponibles("C01", null, null, null, null);

        if (resultado.size() != 3)
            throw new AssertionError("Se esperaban 3 espacios en C01 pero se obtuvieron " + resultado.size());
    }

    private static void testConsultarEspaciosFiltrandoSlotOcupado() {
        // E01 está ocupado en diasDesdeHoy(2) 10:00-11:00 (reservado en testSolicitarReservaEstadoIngresada)
        // E02 y E03 están libres en ese horario → se esperan 2 resultados
        List<EspacioDeportivo> resultado = ReservaController.getInstance()
                .consultarEspaciosDisponibles("C01",
                        diasDesdeHoy(2), Time.valueOf("10:00:00"), Time.valueOf("11:00:00"), null);

        if (resultado.size() != 2)
            throw new AssertionError("Se esperaban 2 espacios disponibles pero se obtuvieron " + resultado.size());

        boolean contieneE01 = false;
        for (EspacioDeportivo e : resultado) {
            if (e.getCodigo().equals("E01")) { contieneE01 = true; break; }
        }
        if (contieneE01)
            throw new AssertionError("E01 no debería estar disponible en el slot ya ocupado");
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
