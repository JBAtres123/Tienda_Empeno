package com.online.tienda_empeno.controller;

import com.online.tienda_empeno.dto.*;
import com.online.tienda_empeno.service.ArticuloService;
import com.online.tienda_empeno.utils.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    // ← ENDPOINT MODIFICADO CON JWT
    @PostMapping("/registrar")
    public ResponseEntity<?> registrarArticulo(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody ArticuloRegistroDTO articuloDTO) {

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

            ArticuloResponseDTO articulo = articuloService.registrarArticulo(articuloDTO, idCliente);
            return ResponseEntity.ok(articulo);

        } catch (Exception e) {
            return ResponseEntity.status(401).body("Error de autenticación: " + e.getMessage());
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