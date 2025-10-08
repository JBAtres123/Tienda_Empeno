package com.online.tienda_empeno.controller;

import com.online.tienda_empeno.entity.Ciudad;
import com.online.tienda_empeno.repository.CiudadRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/ciudades")
@CrossOrigin(origins = "*")
public class CiudadController {

    private final CiudadRepository ciudadRepository;

    public CiudadController(CiudadRepository ciudadRepository) {
        this.ciudadRepository = ciudadRepository;
    }

    @GetMapping("/departamento/{departamentoId}")
    public ResponseEntity<List<Map<String, Object>>> obtenerCiudadesPorDepartamento(@PathVariable Integer departamentoId) {
        List<Ciudad> ciudades = ciudadRepository.findByDepartamentoIdDepartamento(departamentoId);

        List<Map<String, Object>> response = ciudades.stream()
                .map(ciudad -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", ciudad.getIdCiudad());
                    map.put("nombre", ciudad.getNombreCiudad());
                    map.put("departamento_id", departamentoId);
                    return map;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }
}
