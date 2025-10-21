package com.online.tienda_empeno.controller;

import com.online.tienda_empeno.dto.*;
import com.online.tienda_empeno.service.CompraArticulosService;
import com.online.tienda_empeno.utils.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/compras")
@CrossOrigin(origins = "*")
public class CompraArticulosController {

    private final CompraArticulosService compraArticulosService;
    private final JwtUtil jwtUtil;

    public CompraArticulosController(CompraArticulosService compraArticulosService, JwtUtil jwtUtil) {
        this.compraArticulosService = compraArticulosService;
        this.jwtUtil = jwtUtil;
    }

    // ==================== CLIENTE: REGISTRAR ARTÍCULO PARA VENDER ====================

    @PostMapping("/registrar-articulo-vender")
    public ResponseEntity<?> registrarArticuloParaVender(
            @RequestParam("idTipoArticulo") Integer idTipoArticulo,
            @RequestParam("nombreArticulo") String nombreArticulo,
            @RequestParam("descripcion") String descripcion,
            @RequestParam("estadoArticulo") String estadoArticulo,
            @RequestParam("precioArticulo") java.math.BigDecimal precioArticulo,
            @RequestParam("imagenes") MultipartFile[] imagenes,
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");

            if (!jwtUtil.validateToken(token)) {
                return ResponseEntity.status(401).body(Map.of("error", "Token inválido o expirado"));
            }

            String tipoUsuario = jwtUtil.extractTipoUsuario(token);
            if (!"Cliente".equals(tipoUsuario)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Solo los clientes pueden registrar artículos para vender"));
            }

            Integer idCliente = jwtUtil.extractIdUsuario(token);

            // Crear DTO con los parámetros recibidos
            ArticuloVenderDTO dto = new ArticuloVenderDTO();
            dto.setIdTipoArticulo(idTipoArticulo);
            dto.setNombreArticulo(nombreArticulo);
            dto.setDescripcion(descripcion);
            dto.setEstadoArticulo(estadoArticulo);
            dto.setPrecioArticulo(precioArticulo);

            ArticuloResponseDTO resultado = compraArticulosService.registrarArticuloParaVender(dto, idCliente, imagenes);

            Map<String, Object> response = new HashMap<>();
            response.put("mensaje", "Artículo registrado exitosamente. Será evaluado por nuestro equipo.");
            response.put("articulo", resultado);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // ==================== ADMIN: LISTAR ARTÍCULOS PENDIENTES DE COMPRA ====================

    @GetMapping("/articulos-pendientes")
    public ResponseEntity<?> listarArticulosPendientesCompra(
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");

            if (!jwtUtil.validateToken(token)) {
                return ResponseEntity.status(401).body(Map.of("error", "Token inválido"));
            }

            String tipoUsuario = jwtUtil.extractTipoUsuario(token);
            if (!"Administrador".equals(tipoUsuario)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Solo administradores pueden ver artículos pendientes"));
            }

            // Verificar rol específico (1 o 3)
            Integer idAdmin = jwtUtil.extractIdUsuario(token);
            // Aquí podrías validar el rol específico si lo necesitas

            List<ArticuloVenderListadoDTO> articulos = compraArticulosService.listarArticulosPendientesCompra();
            return ResponseEntity.ok(articulos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // ==================== ADMIN: CREAR SOLICITUD DE COMPRA ====================

    @PostMapping("/crear/{idArticulo}")
    public ResponseEntity<?> crearSolicitudCompra(
            @PathVariable Integer idArticulo,
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");

            if (!jwtUtil.validateToken(token)) {
                return ResponseEntity.status(401).body(Map.of("error", "Token inválido"));
            }

            String tipoUsuario = jwtUtil.extractTipoUsuario(token);
            if (!"Administrador".equals(tipoUsuario)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Solo administradores pueden crear solicitudes de compra"));
            }

            Integer idAdmin = jwtUtil.extractIdUsuario(token);
            CompraResponseDTO resultado = compraArticulosService.crearSolicitudCompra(idArticulo, idAdmin);

            Map<String, Object> response = new HashMap<>();
            response.put("mensaje", "Solicitud de compra creada exitosamente");
            response.put("compra", resultado);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // ==================== ADMIN: EVALUAR COMPRA ====================

    @PutMapping("/{idCompra}/evaluar")
    public ResponseEntity<?> evaluarCompra(
            @PathVariable Integer idCompra,
            @RequestBody CompraEvaluarDTO dto,
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");

            if (!jwtUtil.validateToken(token)) {
                return ResponseEntity.status(401).body(Map.of("error", "Token inválido"));
            }

            String tipoUsuario = jwtUtil.extractTipoUsuario(token);
            if (!"Administrador".equals(tipoUsuario)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Solo administradores pueden evaluar compras"));
            }

            Integer idAdmin = jwtUtil.extractIdUsuario(token);
            CompraResponseDTO resultado = compraArticulosService.evaluarCompra(idCompra, dto, idAdmin);

            String mensajeRespuesta = dto.getIdEstado() == 14
                    ? "Compra aprobada exitosamente. El artículo ahora está en venta."
                    : "Compra rechazada. El artículo ha sido marcado como obsoleto.";

            Map<String, Object> response = new HashMap<>();
            response.put("mensaje", mensajeRespuesta);
            response.put("compra", resultado);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // ==================== CLIENTE: VER MIS SOLICITUDES DE VENTA ====================

    @GetMapping("/mis-solicitudes")
    public ResponseEntity<?> listarMisSolicitudesVenta(
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");

            if (!jwtUtil.validateToken(token)) {
                return ResponseEntity.status(401).body(Map.of("error", "Token inválido"));
            }

            String tipoUsuario = jwtUtil.extractTipoUsuario(token);
            if (!"Cliente".equals(tipoUsuario)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Esta función es solo para clientes"));
            }

            Integer idCliente = jwtUtil.extractIdUsuario(token);
            List<CompraResponseDTO> compras = compraArticulosService.listarMisSolicitudesVenta(idCliente);
            return ResponseEntity.ok(compras);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // ==================== ADMIN: VER TODAS LAS COMPRAS ====================

    @GetMapping("/todas")
    public ResponseEntity<?> listarTodasLasCompras(
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");

            if (!jwtUtil.validateToken(token)) {
                return ResponseEntity.status(401).body(Map.of("error", "Token inválido"));
            }

            String tipoUsuario = jwtUtil.extractTipoUsuario(token);
            if (!"Administrador".equals(tipoUsuario)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Solo administradores pueden ver todas las compras"));
            }

            List<CompraResponseDTO> compras = compraArticulosService.listarTodasLasCompras();
            return ResponseEntity.ok(compras);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // ==================== OBTENER COMPRA POR ID ====================

    @GetMapping("/{idCompra}")
    public ResponseEntity<?> obtenerCompraPorId(
            @PathVariable Integer idCompra,
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");

            if (!jwtUtil.validateToken(token)) {
                return ResponseEntity.status(401).body(Map.of("error", "Token inválido"));
            }

            String tipoUsuario = jwtUtil.extractTipoUsuario(token);
            if (!"Cliente".equals(tipoUsuario) && !"Administrador".equals(tipoUsuario)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Acceso denegado"));
            }

            List<CompraResponseDTO> todasLasCompras = compraArticulosService.listarTodasLasCompras();
            CompraResponseDTO compra = todasLasCompras.stream()
                    .filter(c -> c.getIdCompra().equals(idCompra))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Compra no encontrada"));

            return ResponseEntity.ok(compra);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}