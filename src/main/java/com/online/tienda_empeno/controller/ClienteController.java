package com.online.tienda_empeno.controller;

import com.online.tienda_empeno.dto.*;
import com.online.tienda_empeno.service.ClienteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/clientes")
@CrossOrigin(origins = "*")
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    // ---------------- Registrar dirección ----------------
    @PostMapping("/direccion")
    public ResponseEntity<DireccionResponseDTO> registrarDireccion(@RequestBody DireccionDTO direccionDTO) {
        DireccionResponseDTO direccion = clienteService.registrarDireccion(direccionDTO);
        return ResponseEntity.ok(direccion);
    }

    // ---------------- Registrar cliente ----------------
    @PostMapping("/registro")
    public ResponseEntity<ClienteResponseDTO> registrarCliente(@RequestBody ClienteRegistroDTO clienteDTO) {
        ClienteResponseDTO cliente = clienteService.registrarCliente(clienteDTO);
        return ResponseEntity.ok(cliente);
    }

    // ---------------- Login cliente/admin ----------------
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO loginDTO) {
        LoginResponseDTO response = clienteService.loginCliente(loginDTO);
        if (response.isSuccess()) {
            return ResponseEntity.ok(response); // 200 OK
        } else {
            return ResponseEntity.status(401).body(response); // 401 Unauthorized
        }
    }
}
