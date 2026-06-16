package views;

import java.text.SimpleDateFormat;

public class Validador {

    public static String dni(String v) {
        if (!v.matches("\\d+") || v.length() <= 4)
            return "DNI: solo números, más de 4 dígitos.";
        return null;
    }

    public static String soloLetras(String campo, String v) {
        if (!v.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑüÜ ]+") || v.trim().length() <= 3)
            return campo + ": solo letras, más de 3 caracteres.";
        return null;
    }

    public static String telefono(String v) {
        if (!v.matches("\\d+"))
            return "Teléfono: solo números.";
        return null;
    }

    public static String email(String v) {
        if (!v.matches("^[\\w.+\\-]+@[\\w\\-]+(\\.[\\w\\-]+)*\\.[a-zA-Z]{2,}$"))
            return "Email: formato inválido (ej: usuario@dominio.com).";
        return null;
    }

    public static String fecha(String v) {
        if (!v.matches("\\d{4}-\\d{2}-\\d{2}"))
            return "Fecha: use el formato yyyy-MM-dd.";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setLenient(false);
        try {
            sdf.parse(v);
        } catch (Exception e) {
            return "Fecha: fecha inválida (ej: 2026-07-20).";
        }
        return null;
    }

    public static String hora(String campo, String v) {
        if (!v.matches("^([01]\\d|2[0-3]):[0-5]\\d$"))
            return campo + ": hora inválida, use HH:mm en formato 24hs (ej: 14:30).";
        return null;
    }

    public static String horasOrdenadas(String inicioStr, String finStr) {
        try {
            String[] ini = inicioStr.split(":");
            String[] fin = finStr.split(":");
            int minIni = Integer.parseInt(ini[0]) * 60 + Integer.parseInt(ini[1]);
            int minFin = Integer.parseInt(fin[0]) * 60 + Integer.parseInt(fin[1]);
            if (minFin <= minIni)
                return "Hora fin debe ser posterior a hora inicio.";
        } catch (Exception ignored) {}
        return null;
    }

    public static String enteroPositivo(String campo, String v) {
        try {
            if (Integer.parseInt(v) <= 0)
                return campo + ": debe ser un número entero mayor a 0.";
        } catch (NumberFormatException e) {
            return campo + ": solo números enteros.";
        }
        return null;
    }

    public static String decimalPositivo(String campo, String v) {
        try {
            if (Double.parseDouble(v) <= 0)
                return campo + ": debe ser un valor mayor a 0.";
        } catch (NumberFormatException e) {
            return campo + ": solo números (use punto como separador, ej: 1500.0).";
        }
        return null;
    }

    public static String codigo(String campo, String v) {
        if (!v.matches("[a-zA-Z0-9]+"))
            return campo + ": solo letras y números, sin espacios ni símbolos.";
        return null;
    }

    public static String primerError(String... errores) {
        for (String e : errores) {
            if (e != null) return e;
        }
        return null;
    }
}
