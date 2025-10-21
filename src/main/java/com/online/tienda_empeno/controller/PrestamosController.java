package com.online.tienda_empeno.controller;

import com.online.tienda_empeno.dto.*;
import com.online.tienda_empeno.service.PrestamosService;
import com.online.tienda_empeno.utils.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/prestamos")
@CrossOrigin(origins = "*")
public class PrestamosController {

    private final PrestamosService prestamosService;
    private final JwtUtil jwtUtil;

    public PrestamosController(PrestamosService prestamosService, JwtUtil jwtUtil) {
        this.prestamosService = prestamosService;
        this.jwtUtil = jwtUtil;
    }

    // ========== VALIDAR ACCESO ADMIN ROL 1 Y 3 ==========
    private ResponseEntity<?> validarAccesoAdmin(String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");

            if (!jwtUtil.validateToken(token)) {
                return ResponseEntity.status(401).body("Token inválido o expirado");
            }

            String tipoUsuario = jwtUtil.extractTipoUsuario(token);
            if (!"Administrador".equals(tipoUsuario)) {
                return ResponseEntity.status(403).body("Acceso denegado: Solo administradores");
            }

            return null; // Acceso válido
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Error de autenticación: " + e.getMessage());
        }
    }

    // ========== 1. LISTAR ARTÍCULOS SOLICITADOS (estado = 1) ==========
    @GetMapping("/articulos-solicitados")
    public ResponseEntity<?> listarArticulosSolicitados(@RequestHeader("Authorization") String authHeader) {
        ResponseEntity<?> error = validarAccesoAdmin(authHeader);
        if (error != null) return error;

        List<ArticuloSolicitadoDTO> articulos = prestamosService.listarArticulosSolicitados();
        return ResponseEntity.ok(articulos);
    }

    // ========== 2. CREAR PRÉSTAMO (iniciar evaluación) ==========
    @PostMapping("/crear")
    public ResponseEntity<?> crearPrestamo(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody PrestamoCrearDTO prestamoDTO) {

        ResponseEntity<?> error = validarAccesoAdmin(authHeader);
        if (error != null) return error;

        try {
            String token = authHeader.replace("Bearer ", "");
            Integer idAdmin = jwtUtil.extractIdUsuario(token);

            PrestamoResponseDTO prestamo = prestamosService.crearPrestamo(prestamoDTO, idAdmin);
            return ResponseEntity.ok(prestamo);
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Error: " + e.getMessage());
        }
    }

    // ========== 3. EVALUAR PRÉSTAMO (aprobar/rechazar) ==========
    @PutMapping("/{idPrestamo}/evaluar")
    public ResponseEntity<?> evaluarPrestamo(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Integer idPrestamo,
            @RequestBody PrestamoEvaluarDTO evaluacionDTO) {

        ResponseEntity<?> error = validarAccesoAdmin(authHeader);
        if (error != null) return error;

        try {
            PrestamoResponseDTO prestamo = prestamosService.evaluarPrestamo(idPrestamo, evaluacionDTO);
            return ResponseEntity.ok(prestamo);
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Error: " + e.getMessage());
        }
    }

    // ========== 4. LISTAR TODOS LOS PRÉSTAMOS ==========
    @GetMapping
    public ResponseEntity<?> listarPrestamos(@RequestHeader("Authorization") String authHeader) {
        ResponseEntity<?> error = validarAccesoAdmin(authHeader);
        if (error != null) return error;

        List<PrestamoResponseDTO> prestamos = prestamosService.listarPrestamos();
        return ResponseEntity.ok(prestamos);
    }

    // ========== 5. OBTENER PRÉSTAMO POR ID ==========
    @GetMapping("/{idPrestamo}")
    public ResponseEntity<?> obtenerPrestamoPorId(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Integer idPrestamo) {

        try {
            String token = authHeader.replace("Bearer ", "");

            if (!jwtUtil.validateToken(token)) {
                return ResponseEntity.status(401).body(Map.of("error", "Token inválido o expirado"));
            }

            String tipoUsuario = jwtUtil.extractTipoUsuario(token);
            Integer idUsuario = jwtUtil.extractIdUsuario(token);

            PrestamoResponseDTO prestamo = prestamosService.obtenerPrestamoPorId(idPrestamo);

            // Si es cliente, verificar que el préstamo le pertenece
            if ("Cliente".equals(tipoUsuario)) {
                if (!prestamo.getIdCliente().equals(idUsuario)) {
                    return ResponseEntity.status(403)
                            .body(Map.of("error", "No tienes permiso para ver este préstamo"));
                }
            }
            // Si es administrador, puede ver cualquier préstamo
            else if (!"Administrador".equals(tipoUsuario)) {
                return ResponseEntity.status(403)
                        .body(Map.of("error", "Acceso denegado"));
            }

            return ResponseEntity.ok(prestamo);

        } catch (Exception e) {
            return ResponseEntity.status(404).body(Map.of("error", "Préstamo no encontrado"));
        }
    }

    // ========== 6. LISTAR MIS PRÉSTAMOS ACTIVOS (cliente autenticado) ==========
    @GetMapping("/mis-prestamos-activos")
    public ResponseEntity<?> listarMisPrestamoActivos(
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");

            if (!jwtUtil.validateToken(token)) {
                return ResponseEntity.status(401)
                        .body(Map.of("error", "Token inválido o expirado"));
            }

            String tipoUsuario = jwtUtil.extractTipoUsuario(token);
            if (!"Cliente".equals(tipoUsuario)) {
                return ResponseEntity.status(403)
                        .body(Map.of("error", "Solo clientes pueden ver sus préstamos"));
            }

            Integer idCliente = jwtUtil.extractIdUsuario(token);
            List<PrestamoResponseDTO> prestamos = prestamosService.listarMisPrestamoActivos(idCliente);
            return ResponseEntity.ok(prestamos);

        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(Map.of("error", "Error al obtener préstamos: " + e.getMessage()));
        }
    }
}