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

    // ---------------- Registrar direccion ----------------
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

    // ---------------- Validar disponibilidad de email ----------------
    @GetMapping("/validar-email")
    public ResponseEntity<java.util.Map<String, Boolean>> validarEmail(@RequestParam String email) {
        boolean disponible = clienteService.validarEmailDisponibilidad(email);
        return ResponseEntity.ok(java.util.Map.of("disponible", disponible));
    }
    // ---------------- Obtener cliente por ID ----------------
    @GetMapping("/{idCliente}")
    public ResponseEntity<ClienteDetalleDTO> obtenerClientePorId(@PathVariable Integer idCliente) {
        ClienteDetalleDTO cliente = clienteService.obtenerClienteDetalle(idCliente);
        return ResponseEntity.ok(cliente);
    }

    // ---------------- Cambiar contraseña ----------------
    @PutMapping("/{idCliente}/cambiar-contraseña")
    public ResponseEntity<java.util.Map<String, String>> cambiarContraseña(
            @PathVariable Integer idCliente,
            @RequestBody CambioContraseñaDTO dto) {
        try {
            clienteService.cambiarContraseña(idCliente, dto);
            return ResponseEntity.ok(java.util.Map.of("message", "Contraseña actualizada correctamente"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(java.util.Map.of("message", e.getMessage()));
        }
    }
}

