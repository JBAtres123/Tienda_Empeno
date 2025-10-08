package com.online.tienda_empeno.controller;

import com.online.tienda_empeno.entity.Departamento;
import com.online.tienda_empeno.repository.DepartamentoRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/departamentos")
@CrossOrigin(origins = "*")
public class DepartamentoController {

    private final DepartamentoRepository departamentoRepository;

    public DepartamentoController(DepartamentoRepository departamentoRepository) {
        this.departamentoRepository = departamentoRepository;
    }

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> obtenerDepartamentos() {
        List<Departamento> departamentos = departamentoRepository.findAll();

        List<Map<String, Object>> response = departamentos.stream()
                .map(dept -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", dept.getIdDepartamento());
                    map.put("nombre", dept.getNombreDepartamento());
                    return map;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }
}
