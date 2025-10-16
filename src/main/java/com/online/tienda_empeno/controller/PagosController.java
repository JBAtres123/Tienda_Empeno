// ==================== PagosController.java ====================
package com.online.tienda_empeno.controller;

import com.online.tienda_empeno.dto.*;
import com.online.tienda_empeno.service.ContratosYPagosService;
import com.online.tienda_empeno.utils.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pagos")
@CrossOrigin(origins = "*")
public class PagosController {

    private final ContratosYPagosService service;
    private final JwtUtil jwtUtil;

    public PagosController(ContratosYPagosService service, JwtUtil jwtUtil) {
        this.service = service;
        this.jwtUtil = jwtUtil;
    }

    // Pagar con tarjeta
    @PostMapping("/tarjeta")
    public ResponseEntity<?> pagarConTarjeta(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody PagoTarjetaDTO pagoDTO) {
        try {
            String token = authHeader.replace("Bearer ", "");

            if (!jwtUtil.validateToken(token)) {
                return ResponseEntity.status(401).body("Token inválido");
            }

            String tipoUsuario = jwtUtil.extractTipoUsuario(token);
            if (!"Cliente".equals(tipoUsuario)) {
                return ResponseEntity.status(403).body("Solo clientes pueden realizar pagos");
            }

            Integer idCliente = jwtUtil.extractIdUsuario(token);
            FacturaResponseDTO factura = service.pagarConTarjeta(pagoDTO, idCliente);
            return ResponseEntity.ok(factura);

        } catch (Exception e) {
            return ResponseEntity.status(400).body("Error: " + e.getMessage());
        }
    }

    // Pagar en efectivo
    @PostMapping("/efectivo")
    public ResponseEntity<?> pagarEnEfectivo(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody PagoEfectivoDTO pagoDTO) {
        try {
            String token = authHeader.replace("Bearer ", "");

            if (!jwtUtil.validateToken(token)) {
                return ResponseEntity.status(401).body("Token inválido");
            }

            String tipoUsuario = jwtUtil.extractTipoUsuario(token);
            if (!"Cliente".equals(tipoUsuario)) {
                return ResponseEntity.status(403).body("Solo clientes pueden realizar pagos");
            }

            Integer idCliente = jwtUtil.extractIdUsuario(token);
            CobranzaResponseDTO cobranza = service.pagarEnEfectivo(pagoDTO, idCliente);
            return ResponseEntity.ok(cobranza);

        } catch (Exception e) {
            return ResponseEntity.status(400).body("Error: " + e.getMessage());
        }
    }

    // Ver cobranzas del cliente
    @GetMapping("/mis-cobranzas")
    public ResponseEntity<?> listarMisCobranzas(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");

            if (!jwtUtil.validateToken(token)) {
                return ResponseEntity.status(401).body("Token inválido");
            }

            String tipoUsuario = jwtUtil.extractTipoUsuario(token);
            if (!"Cliente".equals(tipoUsuario)) {
                return ResponseEntity.status(403).body("Solo clientes pueden ver cobranzas");
            }

            Integer idCliente = jwtUtil.extractIdUsuario(token);
            List<CobranzaResponseDTO> cobranzas = service.listarCobranzasCliente(idCliente);
            return ResponseEntity.ok(cobranzas);

        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }
}