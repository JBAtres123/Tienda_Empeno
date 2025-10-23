package com.online.tienda_empeno.controller;

import com.online.tienda_empeno.dto.*;
import com.online.tienda_empeno.service.TiendaService;
import com.online.tienda_empeno.utils.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tienda")
@CrossOrigin(origins = "*")
public class TiendaController {

    private final TiendaService tiendaService;
    private final JwtUtil jwtUtil;

    public TiendaController(TiendaService tiendaService, JwtUtil jwtUtil) {
        this.tiendaService = tiendaService;
        this.jwtUtil = jwtUtil;
    }

    // ==================== ADMIN: LISTAR ARTÍCULOS PARA PREPARAR (estado 9) ====================

    @GetMapping("/admin/articulos-preparar")
    public ResponseEntity<?> listarArticulosParaPreparar(
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");

            if (!jwtUtil.validateToken(token)) {
                return ResponseEntity.status(401).body(Map.of("error", "Token inválido"));
            }

            String tipoUsuario = jwtUtil.extractTipoUsuario(token);
            if (!"Administrador".equals(tipoUsuario)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Solo administradores pueden ver artículos para preparar"));
            }

            List<ProductoTiendaResponseDTO> articulos = tiendaService.listarArticulosParaPreparar();
            return ResponseEntity.ok(articulos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // ==================== ADMIN: PREPARAR PRODUCTO PARA TIENDA ====================

    @PostMapping("/admin/preparar-producto")
    public ResponseEntity<?> prepararProductoParaTienda(
            @RequestBody ProductoTiendaDTO dto,
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");

            if (!jwtUtil.validateToken(token)) {
                return ResponseEntity.status(401).body(Map.of("error", "Token inválido"));
            }

            String tipoUsuario = jwtUtil.extractTipoUsuario(token);
            if (!"Administrador".equals(tipoUsuario)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Solo administradores pueden preparar productos"));
            }

            Integer idAdmin = jwtUtil.extractIdUsuario(token);
            ProductoTiendaResponseDTO producto = tiendaService.prepararProductoParaTienda(dto, idAdmin);

            Map<String, Object> response = new HashMap<>();
            response.put("mensaje", "Producto preparado exitosamente y publicado en la tienda");
            response.put("producto", producto);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // ==================== CLIENTE: VER CATÁLOGO (público o con token) ====================

    @GetMapping("/catalogo")
    public ResponseEntity<?> verCatalogo() {
        try {
            List<ProductoTiendaResponseDTO> productos = tiendaService.listarCatalogo();
            return ResponseEntity.ok(productos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // ==================== CLIENTE: VER DETALLE DE PRODUCTO ====================

    @GetMapping("/producto/{idProducto}")
    public ResponseEntity<?> verDetalleProducto(@PathVariable Integer idProducto) {
        try {
            ProductoTiendaResponseDTO producto = tiendaService.verDetalleProducto(idProducto);
            return ResponseEntity.ok(producto);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // ==================== CLIENTE: CREAR PEDIDO ====================

    @PostMapping("/pedido/crear")
    public ResponseEntity<?> crearPedido(
            @RequestBody CrearPedidoDTO dto,
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");

            if (!jwtUtil.validateToken(token)) {
                return ResponseEntity.status(401).body(Map.of("error", "Token inválido"));
            }

            String tipoUsuario = jwtUtil.extractTipoUsuario(token);
            if (!"Cliente".equals(tipoUsuario)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Solo clientes pueden crear pedidos"));
            }

            Integer idCliente = jwtUtil.extractIdUsuario(token);
            PedidoResponseDTO pedido = tiendaService.crearPedido(dto, idCliente);

            Map<String, Object> response = new HashMap<>();
            response.put("mensaje", "Pedido creado exitosamente. Procede al pago.");
            response.put("pedido", pedido);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // ==================== CLIENTE: PAGAR PEDIDO ====================

    @PostMapping("/pedido/pagar")
    public ResponseEntity<?> pagarPedido(
            @RequestBody PagarPedidoDTO dto,
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");

            if (!jwtUtil.validateToken(token)) {
                return ResponseEntity.status(401).body(Map.of("error", "Token inválido"));
            }

            String tipoUsuario = jwtUtil.extractTipoUsuario(token);
            if (!"Cliente".equals(tipoUsuario)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Solo clientes pueden pagar pedidos"));
            }

            Integer idCliente = jwtUtil.extractIdUsuario(token);
            PedidoResponseDTO pedido = tiendaService.pagarPedido(dto, idCliente);

            Map<String, Object> response = new HashMap<>();
            response.put("mensaje", "¡Pago exitoso! Tu pedido está en preparación.");
            response.put("pedido", pedido);
            response.put("seguimiento", pedido.getSeguimiento());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // ==================== CLIENTE: VER MIS PEDIDOS ====================

    @GetMapping("/mis-pedidos")
    public ResponseEntity<?> verMisPedidos(
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");

            if (!jwtUtil.validateToken(token)) {
                return ResponseEntity.status(401).body(Map.of("error", "Token inválido"));
            }

            String tipoUsuario = jwtUtil.extractTipoUsuario(token);
            if (!"Cliente".equals(tipoUsuario)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Solo clientes pueden ver sus pedidos"));
            }

            Integer idCliente = jwtUtil.extractIdUsuario(token);
            List<PedidoResponseDTO> pedidos = tiendaService.listarMisPedidos(idCliente);

            return ResponseEntity.ok(pedidos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // ==================== CLIENTE: VER SEGUIMIENTO DE PEDIDO ====================

    @GetMapping("/pedido/{idPedido}/seguimiento")
    public ResponseEntity<?> verSeguimientoPedido(
            @PathVariable Integer idPedido,
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");

            if (!jwtUtil.validateToken(token)) {
                return ResponseEntity.status(401).body(Map.of("error", "Token inválido"));
            }

            String tipoUsuario = jwtUtil.extractTipoUsuario(token);
            if (!"Cliente".equals(tipoUsuario)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Solo clientes pueden ver seguimiento de pedidos"));
            }

            Integer idCliente = jwtUtil.extractIdUsuario(token);
            PedidoResponseDTO pedido = tiendaService.verSeguimientoPedido(idPedido, idCliente);

            return ResponseEntity.ok(pedido);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // ==================== CLIENTE: VALORAR PRODUCTO ====================

    @PostMapping("/producto/valorar")
    public ResponseEntity<?> valorarProducto(
            @RequestBody ValoracionDTO dto,
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");

            if (!jwtUtil.validateToken(token)) {
                return ResponseEntity.status(401).body(Map.of("error", "Token inválido"));
            }

            String tipoUsuario = jwtUtil.extractTipoUsuario(token);
            if (!"Cliente".equals(tipoUsuario)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Solo clientes pueden valorar productos"));
            }

            Integer idCliente = jwtUtil.extractIdUsuario(token);
            ValoracionResponseDTO valoracion = tiendaService.valorarProducto(dto, idCliente);

            Map<String, Object> response = new HashMap<>();
            response.put("mensaje", "¡Gracias por tu valoración!");
            response.put("valoracion", valoracion);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // ==================== VER VALORACIONES DE UN PRODUCTO (público) ====================

    @GetMapping("/producto/{idProducto}/valoraciones")
    public ResponseEntity<?> verValoracionesProducto(@PathVariable Integer idProducto) {
        try {
            List<ValoracionResponseDTO> valoraciones = tiendaService.verValoracionesProducto(idProducto);
            return ResponseEntity.ok(valoraciones);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}