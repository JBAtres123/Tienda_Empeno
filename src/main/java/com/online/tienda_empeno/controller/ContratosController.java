// ==================== ContratosController.java ====================
package com.online.tienda_empeno.controller;

import com.online.tienda_empeno.dto.*;
import com.online.tienda_empeno.service.ContratosYPagosService;
import com.online.tienda_empeno.utils.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contratos")
@CrossOrigin(origins = "*")
public class ContratosController {

    private final ContratosYPagosService service;
    private final JwtUtil jwtUtil;

    public ContratosController(ContratosYPagosService service, JwtUtil jwtUtil) {
        this.service = service;
        this.jwtUtil = jwtUtil;
    }

    // Ver contratos pendientes del cliente
    @GetMapping("/mis-contratos-pendientes")
    public ResponseEntity<?> listarContratosPendientes(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");

            if (!jwtUtil.validateToken(token)) {
                return ResponseEntity.status(401).body("Token inválido");
            }

            String tipoUsuario = jwtUtil.extractTipoUsuario(token);
            if (!"Cliente".equals(tipoUsuario)) {
                return ResponseEntity.status(403).body("Solo clientes pueden ver contratos");
            }

            Integer idCliente = jwtUtil.extractIdUsuario(token);
            List<ContratoResponseDTO> contratos = service.listarContratosPendientes(idCliente);
            return ResponseEntity.ok(contratos);

        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    // Ver detalle de un contrato
    @GetMapping("/{idContrato}")
    public ResponseEntity<?> obtenerContratoPorId(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Integer idContrato) {
        try {
            String token = authHeader.replace("Bearer ", "");

            if (!jwtUtil.validateToken(token)) {
                return ResponseEntity.status(401).body("Token inválido");
            }

            String tipoUsuario = jwtUtil.extractTipoUsuario(token);
            if (!"Cliente".equals(tipoUsuario)) {
                return ResponseEntity.status(403).body("Solo clientes pueden ver contratos");
            }

            Integer idCliente = jwtUtil.extractIdUsuario(token);
            ContratoResponseDTO contrato = service.obtenerContratoPorId(idContrato, idCliente);
            return ResponseEntity.ok(contrato);

        } catch (Exception e) {
            return ResponseEntity.status(400).body("Error: " + e.getMessage());
        }
    }

    // Firmar contrato
    @PutMapping("/{idContrato}/firmar")
    public ResponseEntity<?> firmarContrato(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Integer idContrato,
            @RequestBody FirmarContratoDTO firmaDTO) {
        try {
            String token = authHeader.replace("Bearer ", "");

            if (!jwtUtil.validateToken(token)) {
                return ResponseEntity.status(401).body("Token inválido");
            }

            String tipoUsuario = jwtUtil.extractTipoUsuario(token);
            if (!"Cliente".equals(tipoUsuario)) {
                return ResponseEntity.status(403).body("Solo clientes pueden firmar contratos");
            }

            Integer idCliente = jwtUtil.extractIdUsuario(token);
            ContratoResponseDTO contrato = service.firmarContrato(idContrato, firmaDTO, idCliente);
            return ResponseEntity.ok(contrato);

        } catch (Exception e) {
            return ResponseEntity.status(400).body("Error: " + e.getMessage());
        }
    }
}


