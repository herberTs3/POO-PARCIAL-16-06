import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import controllers.ClienteController;
import controllers.ComplejoDeportivoController;
import controllers.ReservaController;
import models.EspacioDeportivo;
import models.Reserva;
import models.enums.MedioPago;
import models.enums.TipoEspacio;

public class Main {

    public static void main(String[] args) {
        ClienteController.getInstance().cargarDatosDePrueba();
        ComplejoDeportivoController.getInstance().cargarDatosDePrueba();

        System.out.println("=== Sistema de Reservas Deportivas ===\n");

        probarCU1();
        probarCU2();
        probarCU5();
        probarCU3();
        probarCU4();

        System.out.println("\n=== Todas las pruebas completadas ===");
    }

    private static void probarCU1() {
        System.out.println("--- CU1: Solicitar Reserva ---");
        try {
            Date fecha = new SimpleDateFormat("yyyy-MM-dd").parse("2026-07-01");
            Time inicio = Time.valueOf("10:00:00");
            Time fin = Time.valueOf("12:00:00");

            Reserva reserva = ReservaController.getInstance()
                .solicitarReserva("12345678", "C01", "E01", fecha, inicio, fin, "TORNEO", "admin");

            System.out.println("Reserva creada: " + reserva.getCodigo());
            System.out.println("Estado: " + reserva.getEstado());
            System.out.println("Precio base: $" + reserva.calcularPrecioBase());
            System.out.println("Recargo (20%): $" + reserva.calcularRecargo());
            System.out.println();

            System.setProperty("reservaCodigo", reserva.getCodigo());
        } catch (Exception e) {
            System.out.println("ERROR CU1: " + e.getMessage());
        }
    }

    private static void probarCU2() {
        System.out.println("--- CU2: Confirmar Reserva con Seña ---");
        try {
            String codigo = System.getProperty("reservaCodigo");
            ReservaController.getInstance()
                .confirmarReservaConSena(codigo, 1500.0, MedioPago.TRANSFERENCIA, "admin");

            Reserva reserva = ReservaController.getInstance().listarTodas().get(0);
            System.out.println("Estado: " + reserva.getEstado());
            System.out.println("Seña registrada: $" + reserva.obtenerImporteSena());
            System.out.println();
        } catch (Exception e) {
            System.out.println("ERROR CU2: " + e.getMessage());
        }
    }

    private static void probarCU5() {
        System.out.println("--- CU5: Consultar Espacios Disponibles ---");
        try {
            Date fecha = new SimpleDateFormat("yyyy-MM-dd").parse("2026-07-01");
            Time inicio = Time.valueOf("14:00:00");
            Time fin = Time.valueOf("16:00:00");

            List<EspacioDeportivo> disponibles = ReservaController.getInstance()
                .consultarEspaciosDisponibles("C01", fecha, inicio, fin, TipoEspacio.FUTBOL);

            System.out.println("Espacios FUTBOL disponibles en C01 (14-16hs): " + disponibles.size());
            for (EspacioDeportivo e : disponibles) {
                System.out.println("  - " + e.getCodigo() + " | " + e.getNombre() + " | $" + e.getPrecioBaseHora() + "/hora");
            }
            System.out.println();
        } catch (Exception e) {
            System.out.println("ERROR CU5: " + e.getMessage());
        }
    }

    private static void probarCU3() {
        System.out.println("--- CU3: Cancelar Reserva (con más de 24hs) ---");
        try {
            Date fecha = new SimpleDateFormat("yyyy-MM-dd").parse("2026-07-03");
            Time inicio = Time.valueOf("10:00:00");
            Time fin = Time.valueOf("11:00:00");

            Reserva reserva2 = ReservaController.getInstance()
                .solicitarReserva("87654321", "C01", "E02", fecha, inicio, fin, "COMUN", "admin");
            ReservaController.getInstance()
                .confirmarReservaConSena(reserva2.getCodigo(), 800.0, MedioPago.EFECTIVO, "admin");

            double creditoAntes = reserva2.obtenerCliente().getCreditoAFavor();
            Date fechaCancelacion = new SimpleDateFormat("yyyy-MM-dd").parse("2026-07-01");
            ReservaController.getInstance()
                .cancelarReserva(reserva2.getCodigo(), fechaCancelacion, "admin");

            double creditoDespues = reserva2.obtenerCliente().getCreditoAFavor();
            System.out.println("Estado: " + reserva2.getEstado());
            System.out.println("Crédito antes: $" + creditoAntes + " | Después: $" + creditoDespues);
            System.out.println("Seña reintegrada: " + (creditoDespues > creditoAntes ? "SI" : "NO"));
            System.out.println();
        } catch (Exception e) {
            System.out.println("ERROR CU3: " + e.getMessage());
        }
    }

    private static void probarCU4() {
        System.out.println("--- CU4: Finalizar Reserva y Calcular Saldo ---");
        try {
            Date fecha = new SimpleDateFormat("yyyy-MM-dd").parse("2026-07-05");
            Time inicio = Time.valueOf("09:00:00");
            Time fin = Time.valueOf("11:00:00");

            Reserva reserva3 = ReservaController.getInstance()
                .solicitarReserva("11223344", "C02", "E04", fecha, inicio, fin, "CLASE_GRUPAL", "admin");
            ReservaController.getInstance()
                .confirmarReservaConSena(reserva3.getCodigo(), 2000.0, MedioPago.TARJETA, "admin");
            reserva3.cambiarEstado(models.enums.EstadoReserva.EN_CURSO, "admin");

            double saldo = ReservaController.getInstance()
                .finalizarReserva(reserva3.getCodigo(), "admin");

            System.out.println("Estado: " + reserva3.getEstado());
            System.out.println("Precio base: $" + reserva3.calcularPrecioBase());
            System.out.println("Recargo (10%): $" + reserva3.calcularRecargo());
            System.out.println("Total: $" + reserva3.calcularTotal());
            System.out.println("Seña: $" + reserva3.obtenerImporteSena());
            System.out.println("Saldo pendiente: $" + saldo);
            System.out.println();
        } catch (Exception e) {
            System.out.println("ERROR CU4: " + e.getMessage());
        }
    }
}
