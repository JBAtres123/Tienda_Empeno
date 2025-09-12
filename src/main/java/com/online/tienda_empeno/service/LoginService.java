package com.online.tienda_empeno.service;

import com.online.tienda_empeno.dto.LoginRequest;
import com.online.tienda_empeno.entity.Cliente;
import com.online.tienda_empeno.entity.Contraseñas;
import com.online.tienda_empeno.repository.ClienteRepository;
import com.online.tienda_empeno.repository.ContraseñaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LoginService {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private ContraseñaRepository contraseñaRepository;

    public String login(LoginRequest request) {

        Cliente cliente = clienteRepository.findByEmailCliente(request.getUsuario());
        if (cliente == null) return "Usuario no encontrado";

        Contraseñas contraseña = contraseñaRepository.findById(cliente.getIdContraseña())
                .orElse(null);
        if (contraseña == null) return "Contraseña no encontrada";

        if (!contraseña.getContraseña().equals(request.getContraseña()))
            return "Contraseña incorrecta";

        return "Login exitoso: " + cliente.getNombreCliente() + " " + cliente.getApellidoCliente();
    }
}
