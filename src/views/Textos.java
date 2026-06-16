package views;

public final class Textos {

    private Textos() {}

    public static final class Titulo {
        public static final String MAIN              = "Sistema de Gestión de Reservas Deportivas";
        public static final String LOGIN             = "Inicio de sesión";
        public static final String SOLICITAR_RESERVA = "Solicitar Reserva";
        public static final String CONFIRMAR_RESERVA = "Confirmar Reserva con Seña";
        public static final String CANCELAR_RESERVA  = "Cancelar Reserva";
        public static final String GESTIONAR_RESERVA = "Gestionar Uso de Reserva";
        public static final String REGISTRAR_CLIENTE = "Registrar Cliente";
        public static final String REGISTRAR_COMPLEJO = "Registrar Complejo Deportivo";
        public static final String AGREGAR_ESPACIO   = "Agregar Espacio Deportivo";
        public static final String CONSULTAR_ESPACIOS = "Consultar Espacios Disponibles";
        public static final String CONSULTAS         = "Consultas del Sistema";
        public static final String EXITO             = "Éxito";
        public static final String ERROR             = "Error";
        public static final String ERROR_VALIDACION  = "Error de validación";
        public static final String SIN_RESERVAS      = "Sin reservas";
        public static final String SIN_RESULTADOS    = "Sin resultados";
        public static final String RESERVA_CANCELADA = "Reserva cancelada";
        public static final String RESERVA_FINALIZADA = "Reserva finalizada";
    }

    public static final class Btn {
        public static final String SOLICITAR    = "Solicitar Reserva";
        public static final String CONFIRMAR    = "Confirmar";
        public static final String CONFIRMAR_SENA = "Confirmar Seña";
        public static final String CANCELAR     = "Cancelar Reserva";
        public static final String INICIAR_USO  = "Iniciar Uso";
        public static final String FINALIZAR    = "Finalizar y Calcular Saldo";
        public static final String REGISTRAR    = "Registrar";
        public static final String REGISTRAR_COMPLEJO = "Registrar Complejo";
        public static final String AGREGAR      = "Agregar Espacio";
        public static final String BUSCAR       = "Buscar";
        public static final String CALCULAR     = "Calcular";
    }

    public static final class Lbl {
        public static final String CLIENTE      = "Cliente:";
        public static final String COMPLEJO     = "Complejo:";
        public static final String ESPACIO      = "Espacio:";
        public static final String CODIGO       = "Código:";
        public static final String CODIGO_ESPACIO = "Código espacio:";
        public static final String NOMBRE       = "Nombre:";
        public static final String APELLIDO     = "Apellido:";
        public static final String DNI          = "DNI:";
        public static final String TELEFONO     = "Teléfono:";
        public static final String EMAIL        = "Email:";
        public static final String FECHA        = "Fecha (yyyy-MM-dd):";
        public static final String FECHA_OPCIONAL = "Fecha (yyyy-MM-dd, opcional):";
        public static final String HORA_INICIO  = "Hora inicio (HH:mm):";
        public static final String HORA_FIN     = "Hora fin (HH:mm):";
        public static final String HORA_INICIO_OPCIONAL = "Hora inicio (HH:mm, opcional):";
        public static final String HORA_FIN_OPCIONAL    = "Hora fin (HH:mm, opcional):";
        public static final String TIPO_RESERVA = "Tipo reserva:";
        public static final String TIPO         = "Tipo:";
        public static final String CAPACIDAD    = "Capacidad:";
        public static final String PRECIO_BASE  = "Precio base/hora:";
        public static final String SUPERFICIE   = "Superficie:";
        public static final String IMPORTE_SENA = "Importe seña:";
        public static final String MEDIO_PAGO   = "Medio de pago:";
        public static final String RESERVA      = "Reserva:";
        public static final String DESDE_OPCIONAL = "Desde (yyyy-MM-dd, opcional):";
        public static final String HASTA_OPCIONAL = "Hasta (yyyy-MM-dd, opcional):";
        public static final String TOTAL_RECAUDADO = "Total recaudado:";
        public static final String TIPO_RESERVA_LABEL = "Tipo reserva:";
        public static final String PORCENTAJE_RECARGO = "% Recargo:";
        public static final String DESCUENTO_CLIENTE  = "% Descuento cliente:";
        public static final String TOTAL_ESTIMADO     = "Total estimado:";
        public static final String SEPARADOR_INICIAR  = "── Iniciar uso (CONFIRMADA → EN CURSO) ──";
        public static final String SEPARADOR_FINALIZAR = "── Finalizar uso (EN CURSO → FINALIZADA) ──";
        public static final String SIN_VALOR    = "—";
        public static final String SIN_DESCUENTO = "Sin descuento";
        public static final String TODOS        = "TODOS";
    }

    public static final class Tab {
        public static final String TOTAL_RECAUDADO    = "Total Recaudado";
        public static final String RESERVAS_CLIENTE   = "Reservas por Cliente";
        public static final String RECARGO_DESCUENTO  = "Recargo / Descuento";
    }

    public static final class Tabla {
        public static final String[] ESPACIOS  = {"Código", "Nombre", "Tipo", "Capacidad", "Precio/hora", "Superficie"};
        public static final String[] RESERVAS  = {"Código", "Espacio", "Fecha", "Hora inicio", "Hora fin", "Estado"};
    }

    public static final class Msg {
        public static final String SIN_COMPLEJOS          = "No hay complejos registrados.";
        public static final String SIN_CLIENTES           = "No hay clientes registrados.";
        public static final String SIN_RESERVAS           = "No hay reservas registradas.";
        public static final String SIN_RESERVAS_ACTIVAS   = "No hay reservas activas para cancelar.";
        public static final String SIN_CONFIRMADAS        = "No hay reservas en estado CONFIRMADA.";
        public static final String SIN_INGRESADAS         = "No hay reservas en estado INGRESADA.";
        public static final String SIN_EN_CURSO           = "No hay reservas en estado EN CURSO.";
        public static final String SIN_ESPACIOS_COMPLEJO  = "El complejo seleccionado no tiene espacios registrados.";
        public static final String SIN_ESPACIOS_RESULTADO = "No hay espacios disponibles con esos criterios.";
        public static final String SIN_RESERVAS_CLIENTE   = "El cliente no tiene reservas CONFIRMADAS.";
        public static final String USUARIO_VACIO          = "El usuario no puede estar vacío.";
        public static final String CAMPOS_OBLIGATORIOS    = "Todos los campos son obligatorios.";
        public static final String CAMPOS_OBLIGATORIOS_SUP = "Todos los campos son obligatorios (superficie opcional).";
        public static final String IMPORTE_OBLIGATORIO    = "El importe de la seña es obligatorio.";
        public static final String FILTRO_HORARIO_INCOMPLETO = "Si ingresa fecha u horario, los tres campos son obligatorios.";
        public static final String CAPACIDAD_MAXIMA       = "La capacidad no puede superar las 10.000 personas.";
        public static final String DNI_DUPLICADO          = "Ya existe un cliente con ese DNI.";
        public static final String CODIGO_DUPLICADO       = "Ya existe un complejo con ese código.";
    }
}
