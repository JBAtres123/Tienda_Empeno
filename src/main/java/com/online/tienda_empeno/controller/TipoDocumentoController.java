package com.online.tienda_empeno.controller;

import com.online.tienda_empeno.entity.TipoDeDocumento;
import com.online.tienda_empeno.repository.TipoDeDocumentoRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tipos-documento")
@CrossOrigin(origins = "*")
public class TipoDocumentoController {

    private final TipoDeDocumentoRepository tipoDeDocumentoRepository;

    public TipoDocumentoController(TipoDeDocumentoRepository tipoDeDocumentoRepository) {
        this.tipoDeDocumentoRepository = tipoDeDocumentoRepository;
    }

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> obtenerTiposDocumento() {
        List<TipoDeDocumento> tiposDocumento = tipoDeDocumentoRepository.findAll();

        List<Map<String, Object>> response = tiposDocumento.stream()
                .map(tipo -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", tipo.getIdTipoDocumento());
                    map.put("nombre", tipo.getNombre());
                    return map;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }
}
