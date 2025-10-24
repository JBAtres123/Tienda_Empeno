package com.online.tienda_empeno.controller;

import com.online.tienda_empeno.dto.CrearPromocionDTO;
import com.online.tienda_empeno.dto.PromocionDTO;
import com.online.tienda_empeno.service.PromocionesService;
import com.online.tienda_empeno.utils.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/promociones")
@CrossOrigin(origins = "*")
public class PromocionesController {

    private final PromocionesService promocionesService;
    private final JwtUtil jwtUtil;

    public PromocionesController(PromocionesService promocionesService, JwtUtil jwtUtil) {
        this.promocionesService = promocionesService;
        this.jwtUtil = jwtUtil;
    }

    // ==================== ADMIN: CREAR PROMOCIÓN ====================

    @PostMapping("/crear")
    public ResponseEntity<?> crearPromocion(
            @RequestBody CrearPromocionDTO dto,
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");

            if (!jwtUtil.validateToken(token)) {
                return ResponseEntity.status(401).body(Map.of("error", "Token inválido"));
            }

            String tipoUsuario = jwtUtil.extractTipoUsuario(token);
            if (!"Administrador".equals(tipoUsuario)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Solo administradores pueden crear promociones"));
            }

            Integer idAdmin = jwtUtil.extractIdUsuario(token);
            PromocionDTO promocion = promocionesService.crearPromocion(dto, idAdmin);

            Map<String, Object> response = new HashMap<>();
            response.put("mensaje", "Promoción creada exitosamente");
            response.put("promocion", promocion);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // ==================== ADMIN: ACTUALIZAR PROMOCIÓN ====================

    @PutMapping("/{idPromocion}")
    public ResponseEntity<?> actualizarPromocion(
            @PathVariable Integer idPromocion,
            @RequestBody CrearPromocionDTO dto,
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");

            if (!jwtUtil.validateToken(token)) {
                return ResponseEntity.status(401).body(Map.of("error", "Token inválido"));
            }

            String tipoUsuario = jwtUtil.extractTipoUsuario(token);
            if (!"Administrador".equals(tipoUsuario)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Solo administradores pueden actualizar promociones"));
            }

            PromocionDTO promocion = promocionesService.actualizarPromocion(idPromocion, dto);

            Map<String, Object> response = new HashMap<>();
            response.put("mensaje", "Promoción actualizada exitosamente");
            response.put("promocion", promocion);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // ==================== ADMIN: DESACTIVAR PROMOCIÓN ====================

    @PutMapping("/{idPromocion}/desactivar")
    public ResponseEntity<?> desactivarPromocion(
            @PathVariable Integer idPromocion,
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");

            if (!jwtUtil.validateToken(token)) {
                return ResponseEntity.status(401).body(Map.of("error", "Token inválido"));
            }

            String tipoUsuario = jwtUtil.extractTipoUsuario(token);
            if (!"Administrador".equals(tipoUsuario)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Solo administradores pueden desactivar promociones"));
            }

            promocionesService.desactivarPromocion(idPromocion);

            return ResponseEntity.ok(Map.of("mensaje", "Promoción desactivada exitosamente"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // ==================== ADMIN: ELIMINAR PROMOCIÓN ====================

    @DeleteMapping("/{idPromocion}")
    public ResponseEntity<?> eliminarPromocion(
            @PathVariable Integer idPromocion,
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");

            if (!jwtUtil.validateToken(token)) {
                return ResponseEntity.status(401).body(Map.of("error", "Token inválido"));
            }

            String tipoUsuario = jwtUtil.extractTipoUsuario(token);
            if (!"Administrador".equals(tipoUsuario)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Solo administradores pueden eliminar promociones"));
            }

            promocionesService.eliminarPromocion(idPromocion);

            return ResponseEntity.ok(Map.of("mensaje", "Promoción eliminada exitosamente"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // ==================== ADMIN: LISTAR TODAS LAS PROMOCIONES ====================

    @GetMapping("/admin/todas")
    public ResponseEntity<?> listarTodasPromociones(
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");

            if (!jwtUtil.validateToken(token)) {
                return ResponseEntity.status(401).body(Map.of("error", "Token inválido"));
            }

            String tipoUsuario = jwtUtil.extractTipoUsuario(token);
            if (!"Administrador".equals(tipoUsuario)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Solo administradores pueden ver todas las promociones"));
            }

            List<PromocionDTO> promociones = promocionesService.listarTodasPromociones();
            return ResponseEntity.ok(promociones);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // ==================== PÚBLICO: VER PROMOCIONES VIGENTES ====================

    @GetMapping("/vigentes")
    public ResponseEntity<?> verPromocionesVigentes() {
        try {
            List<PromocionDTO> promociones = promocionesService.listarPromocionesVigentes();
            return ResponseEntity.ok(promociones);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // ==================== OBTENER PROMOCIÓN POR ID ====================

    @GetMapping("/{idPromocion}")
    public ResponseEntity<?> obtenerPromocionPorId(@PathVariable Integer idPromocion) {
        try {
            PromocionDTO promocion = promocionesService.obtenerPromocionPorId(idPromocion);
            return ResponseEntity.ok(promocion);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}