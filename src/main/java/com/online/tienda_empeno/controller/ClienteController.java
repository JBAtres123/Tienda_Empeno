package com.online.tienda_empeno.controller;

import com.online.tienda_empeno.dto.ClienteRegistroDTO;
import com.online.tienda_empeno.dto.DireccionDTO;
import com.online.tienda_empeno.dto.DireccionResponseDTO;
import com.online.tienda_empeno.dto.ClienteResponseDTO;
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

    @PostMapping("/direccion")
    public ResponseEntity<DireccionResponseDTO> registrarDireccion(@RequestBody DireccionDTO direccionDTO) {
        DireccionResponseDTO direccion = clienteService.registrarDireccion(direccionDTO);
        return ResponseEntity.ok(direccion);
    }

    @PostMapping("/registro")
    public ResponseEntity<ClienteResponseDTO> registrarCliente(@RequestBody ClienteRegistroDTO clienteDTO) {
        ClienteResponseDTO cliente = clienteService.registrarCliente(clienteDTO);
        return ResponseEntity.ok(cliente);
    }
}
