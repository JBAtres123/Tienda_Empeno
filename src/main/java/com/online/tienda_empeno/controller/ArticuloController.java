package com.online.tienda_empeno.controller;

import com.online.tienda_empeno.dto.*;
import com.online.tienda_empeno.service.ArticuloService;
import com.online.tienda_empeno.utils.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/articulos")
@CrossOrigin(origins = "*")
public class ArticuloController {

    private final ArticuloService articuloService;
    private final JwtUtil jwtUtil; // ← NUEVO

    public ArticuloController(ArticuloService articuloService, JwtUtil jwtUtil) {
        this.articuloService = articuloService;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping("/tipos-activos")
    public ResponseEntity<List<TipoArticuloResponseDTO>> listarTiposActivos() {
        List<TipoArticuloResponseDTO> tipos = articuloService.listarTiposActivos();
        return ResponseEntity.ok(tipos);
    }

    // ← ENDPOINT MODIFICADO CON JWT Y MULTIPART
    @PostMapping("/registrar")
    public ResponseEntity<?> registrarArticulo(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam("idTipoArticulo") Integer idTipoArticulo,
            @RequestParam("nombreArticulo") String nombreArticulo,
            @RequestParam("descripcion") String descripcion,
            @RequestParam("estadoArticulo") String estadoArticulo,
            @RequestParam("precioArticulo") BigDecimal precioArticulo,
            @RequestParam("imagenes") MultipartFile[] imagenes) {

        try {
            String token = authHeader.replace("Bearer ", "");

            if (!jwtUtil.validateToken(token)) {
                return ResponseEntity.status(401).body("Token inválido o expirado");
            }

            Integer idCliente = jwtUtil.extractIdUsuario(token);
            String tipoUsuario = jwtUtil.extractTipoUsuario(token);

            // Solo clientes pueden empeñar
            if (!"Cliente".equals(tipoUsuario)) {
                return ResponseEntity.status(403).body("Solo los clientes pueden empeñar artículos");
            }

            // Crear DTO
            ArticuloRegistroDTO dto = new ArticuloRegistroDTO();
            dto.setIdTipoArticulo(idTipoArticulo);
            dto.setNombreArticulo(nombreArticulo);
            dto.setDescripcion(descripcion);
            dto.setEstadoArticulo(estadoArticulo);
            dto.setPrecioArticulo(precioArticulo);

            ArticuloResponseDTO articulo = articuloService.registrarArticuloConImagenes(dto, idCliente, imagenes);
            return ResponseEntity.ok(articulo);

        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/cliente/{idCliente}")
    public ResponseEntity<List<ArticuloResponseDTO>> obtenerArticulosPorCliente(@PathVariable Integer idCliente) {
        List<ArticuloResponseDTO> articulos = articuloService.obtenerArticulosPorCliente(idCliente);
        return ResponseEntity.ok(articulos);
    }

    @GetMapping("/{idArticulo}")
    public ResponseEntity<ArticuloResponseDTO> obtenerArticuloPorId(@PathVariable Integer idArticulo) {
        ArticuloResponseDTO articulo = articuloService.obtenerArticuloPorId(idArticulo);
        return ResponseEntity.ok(articulo);
    }

    @GetMapping("/{idArticulo}/imagenes")
    public ResponseEntity<List<ImagenResponseDTO>> obtenerImagenesArticulo(@PathVariable Integer idArticulo) {
        List<ImagenResponseDTO> imagenes = articuloService.obtenerImagenesArticulo(idArticulo);
        return ResponseEntity.ok(imagenes);
    }
}