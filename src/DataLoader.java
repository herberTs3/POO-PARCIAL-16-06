import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Date;

import controllers.ClienteController;
import controllers.ComplejoDeportivoController;
import controllers.ReservaController;
import models.Descuento;
import models.EspacioDeportivo;
import models.Reserva;
import models.enums.EstadoEspacio;
import models.enums.MedioPago;
import models.enums.TipoEspacio;

public class DataLoader {

    public static void cargar() {
        cargarClientes();
        cargarComplejos();
        cargarReservasDePrueba();
        imprimirResumen();
    }

    private static void cargarClientes() {
        ClienteController cc = ClienteController.getInstance();

        cc.registrarCliente("12345678", "Juan",   "Pérez",    "1134567890", "juan@mail.com");
        cc.registrarCliente("87654321", "María",  "García",   "1198765432", "maria@mail.com");
        cc.registrarCliente("11223344", "Carlos", "López",    "1145678901", "carlos@mail.com");
        cc.registrarCliente("55667788", "Ana",    "Martínez", "1156789012", "ana@mail.com");

        try {
            Date desde = new SimpleDateFormat("yyyy-MM-dd").parse("2026-06-01");
            Date hasta = new SimpleDateFormat("yyyy-MM-dd").parse("2026-12-31");
            cc.buscarPorDni("87654321").agregarDescuento(new Descuento(15.0, desde, hasta));
        } catch (Exception ignored) {}
    }

    private static void cargarComplejos() {
        ComplejoDeportivoController cdc = ComplejoDeportivoController.getInstance();

        cdc.registrarComplejo("C01", "Complejo Norte", "Av. Corrientes 1234", "1145001234", "norte@mail.com");
        cdc.agregarEspacioAComplejo("C01", new EspacioDeportivo("E01", "Cancha Fútbol 1",    22, 2000.0, EstadoEspacio.DISPONIBLE, TipoEspacio.FUTBOL,         "Césped sintético"));
        cdc.agregarEspacioAComplejo("C01", new EspacioDeportivo("E02", "Cancha Tenis 1",      4, 1500.0, EstadoEspacio.DISPONIBLE, TipoEspacio.TENIS,          "Polvo de ladrillo"));
        cdc.agregarEspacioAComplejo("C01", new EspacioDeportivo("E03", "Cancha Pádel 1",      4, 1200.0, EstadoEspacio.DISPONIBLE, TipoEspacio.PADEL,          "Cristal"));
        cdc.agregarEspacioAComplejo("C01", new EspacioDeportivo("E04", "Cancha Fútbol 2",    22, 2500.0, EstadoEspacio.DISPONIBLE, TipoEspacio.FUTBOL,         "Césped natural"));

        cdc.registrarComplejo("C02", "Complejo Sur", "Av. Rivadavia 5678", "1156789012", "sur@mail.com");
        cdc.agregarEspacioAComplejo("C02", new EspacioDeportivo("E05", "Salón Multiuso A",   30, 3000.0, EstadoEspacio.DISPONIBLE, TipoEspacio.SALON_MULTIUSO, "Parquet"));
        cdc.agregarEspacioAComplejo("C02", new EspacioDeportivo("E06", "Cancha Pádel 2",      4, 1300.0, EstadoEspacio.DISPONIBLE, TipoEspacio.PADEL,          "Moqueta"));
        cdc.agregarEspacioAComplejo("C02", new EspacioDeportivo("E07", "Cancha Tenis 2",      4, 1600.0, EstadoEspacio.DISPONIBLE, TipoEspacio.TENIS,          "Cemento"));
        cdc.agregarEspacioAComplejo("C02", new EspacioDeportivo("E08", "Cancha Fútbol 3",    22, 1800.0, EstadoEspacio.DISPONIBLE, TipoEspacio.FUTBOL,         "Sintético"));
    }

    private static void cargarReservasDePrueba() {
        ReservaController rc = ReservaController.getInstance();
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

            rc.solicitarReserva("12345678", "C01", "E01",
                sdf.parse("2026-07-10"), Time.valueOf("10:00:00"), Time.valueOf("12:00:00"),
                "TORNEO", "admin");

            rc.solicitarReserva("87654321", "C01", "E02",
                sdf.parse("2026-07-10"), Time.valueOf("09:00:00"), Time.valueOf("10:00:00"),
                "COMUN", "admin");

            Reserva reservaConfirmada = rc.solicitarReserva("11223344", "C02", "E05",
                sdf.parse("2026-07-15"), Time.valueOf("14:00:00"), Time.valueOf("17:00:00"),
                "CLASE_GRUPAL", "admin");
            rc.confirmarReservaConSena(reservaConfirmada.getCodigo(), 2000.0, MedioPago.TRANSFERENCIA, "admin");

        } catch (Exception ignored) {}
    }

    private static void imprimirResumen() {
        System.out.println("╔══════════════════════════════════════════════════════╗");
        System.out.println("║           DATOS DE PRUEBA CARGADOS                  ║");
        System.out.println("╠══════════════════════════════════════════════════════╣");
        System.out.println("║ CLIENTES                                             ║");
        System.out.println("║  DNI 12345678  Juan Pérez        (sin descuento)    ║");
        System.out.println("║  DNI 87654321  María García      (15% descuento)    ║");
        System.out.println("║  DNI 11223344  Carlos López      (sin descuento)    ║");
        System.out.println("║  DNI 55667788  Ana Martínez      (sin descuento)    ║");
        System.out.println("╠══════════════════════════════════════════════════════╣");
        System.out.println("║ COMPLEJOS  C01:E01-E04   C02:E05-E08               ║");
        System.out.println("╠══════════════════════════════════════════════════════╣");
        System.out.println("║ PRUEBA CU1: DNI 55667788 | C01 | E04                ║");
        System.out.println("║   Fecha 2026-07-20 | 10:00 a 12:00 | TORNEO        ║");
        System.out.println("╚══════════════════════════════════════════════════════╝");
    }
}
