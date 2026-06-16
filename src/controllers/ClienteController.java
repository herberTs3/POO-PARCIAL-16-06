package controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import models.Cliente;
import models.enums.EstadoCliente;

public class ClienteController {

    private static ClienteController instance;
    private HashMap<String, Cliente> clientes;

    private ClienteController() {
        this.clientes = new HashMap<>();
    }

    public static ClienteController getInstance() {
        if (instance == null) {
            instance = new ClienteController();
        }
        return instance;
    }

    public void registrarCliente(String dni, String nombre, String apellido,
                                  String telefono, String email) {
        Cliente cliente = new Cliente(dni, nombre, apellido, telefono, email, EstadoCliente.ACTIVO);
        clientes.put(dni, cliente);
    }

    public Cliente buscarPorDni(String dni) {
        for (Cliente cliente : clientes.values()) {
            if (cliente.coincideDni(dni)) return cliente;
        }
        return null;
    }

    public List<Cliente> listarTodos() {
        return new ArrayList<>(clientes.values());
    }

    public void cargarDatosDePrueba() {
        registrarCliente("12345678", "Juan", "Pérez", "1134567890", "juan@mail.com");
        registrarCliente("87654321", "María", "García", "1198765432", "maria@mail.com");
        registrarCliente("11223344", "Carlos", "López", "1145678901", "carlos@mail.com");
    }
}
