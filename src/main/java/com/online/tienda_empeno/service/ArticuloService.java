package com.online.tienda_empeno.service;

import com.online.tienda_empeno.dto.*;
import com.online.tienda_empeno.entity.*;
import com.online.tienda_empeno.repository.*;
import com.online.tienda_empeno.utils.JwtUtil;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ArticuloService {

    private final ArticulosRepository articulosRepository;
    private final ImagenesArticulosRepository imagenesArticulosRepository;
    private final TiposArticulosRepository tiposArticulosRepository;
    private final ClienteRepository clienteRepository;
    private final JwtUtil jwtUtil;

    public ArticuloService(ArticulosRepository articulosRepository,
                           ImagenesArticulosRepository imagenesArticulosRepository,
                           TiposArticulosRepository tiposArticulosRepository,
                           ClienteRepository clienteRepository,
                           JwtUtil jwtUtil) {
        this.articulosRepository = articulosRepository;
        this.imagenesArticulosRepository = imagenesArticulosRepository;
        this.tiposArticulosRepository = tiposArticulosRepository;
        this.clienteRepository = clienteRepository;
        this.jwtUtil = jwtUtil;
    }

    public List<TipoArticuloResponseDTO> listarTiposActivos() {
        List<TiposArticulos> tipos = tiposArticulosRepository.findAllActivos();
        return tipos.stream().map(this::mapTipoArticuloToDto).collect(Collectors.toList());
    }

    public ArticuloResponseDTO registrarArticulo(ArticuloRegistroDTO dto, Integer idCliente) {
        Cliente cliente = clienteRepository.findById(idCliente)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado con id " + idCliente));

        TiposArticulos tipoArticulo = tiposArticulosRepository.findById(dto.getIdTipoArticulo())
                .orElseThrow(() -> new RuntimeException("Tipo de artículo no encontrado con id " + dto.getIdTipoArticulo()));

        if (tipoArticulo.getEstadoArticulo().getIdEstadoArticulo() != 1) {
            throw new RuntimeException("El tipo de artículo seleccionado no está activo");
        }

        int estadoFisico = Integer.parseInt(dto.getEstadoArticulo());
        if (estadoFisico < 1 || estadoFisico > 10) {
            throw new RuntimeException("El estado del artículo debe ser un valor entre 1 y 10");
        }

        // ========== CALCULAR PRECIO DE AVALÚO ==========
        BigDecimal porcentajeMin = tipoArticulo.getParametroAvaluo().getPorcentajeMin();
        BigDecimal porcentajeMax = tipoArticulo.getParametroAvaluo().getPorcentajeMax();

        // Porcentaje base (promedio entre min y max)
        BigDecimal porcentajeBase = porcentajeMin.add(porcentajeMax)
                .divide(new BigDecimal("2"), 4, RoundingMode.HALF_UP)
                .divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP);

        // Factor del estado físico (1-10 convertido a 0.1 - 1.0)
        BigDecimal factorEstado = new BigDecimal(estadoFisico)
                .divide(new BigDecimal("10"), 2, RoundingMode.HALF_UP);

        // Precio avalúo = precio_articulo × porcentaje_base × factor_estado
        BigDecimal precioAvaluo = dto.getPrecioArticulo()
                .multiply(porcentajeBase)
                .multiply(factorEstado)
                .setScale(2, RoundingMode.HALF_UP);
        // ===============================================

        Articulos articulo = new Articulos();
        articulo.setCliente(cliente);
        articulo.setTipoArticulo(tipoArticulo);
        articulo.setIdEstado(1);
        articulo.setEstadoArticulo(dto.getEstadoArticulo());
        articulo.setNombreArticulo(dto.getNombreArticulo());
        articulo.setDescripcion(dto.getDescripcion());
        articulo.setPrecioArticulo(dto.getPrecioArticulo());
        articulo.setPrecioAvaluo(precioAvaluo); // ← GUARDAR PRECIO AVALUADO

        Articulos articuloGuardado = articulosRepository.save(articulo);

        ImagenesArticulos imagen = new ImagenesArticulos();
        imagen.setArticulo(articuloGuardado);
        imagen.setUrlImagen(dto.getUrlImagen());
        ImagenesArticulos imagenGuardada = imagenesArticulosRepository.save(imagen);

        return mapArticuloToDto(articuloGuardado, imagenGuardada.getUrlImagen());
    }

    public List<ArticuloResponseDTO> obtenerArticulosPorCliente(Integer idCliente) {
        List<Articulos> articulos = articulosRepository.findByClienteId(idCliente);
        return articulos.stream().map(a -> {
            String urlImagen = obtenerPrimeraImagenArticulo(a.getIdArticulo());
            return mapArticuloToDto(a, urlImagen);
        }).collect(Collectors.toList());
    }

    public ArticuloResponseDTO obtenerArticuloPorId(Integer idArticulo) {
        Articulos articulo = articulosRepository.findById(idArticulo)
                .orElseThrow(() -> new RuntimeException("Artículo no encontrado con id " + idArticulo));

        String urlImagen = obtenerPrimeraImagenArticulo(idArticulo);
        return mapArticuloToDto(articulo, urlImagen);
    }

    public List<ImagenResponseDTO> obtenerImagenesArticulo(Integer idArticulo) {
        List<ImagenesArticulos> imagenes = imagenesArticulosRepository.findByArticuloId(idArticulo);
        return imagenes.stream().map(this::mapImagenToDto).collect(Collectors.toList());
    }

    private String obtenerPrimeraImagenArticulo(Integer idArticulo) {
        List<ImagenesArticulos> imagenes = imagenesArticulosRepository.findByArticuloId(idArticulo);
        return imagenes.isEmpty() ? null : imagenes.get(0).getUrlImagen();
    }

    private TipoArticuloResponseDTO mapTipoArticuloToDto(TiposArticulos t) {
        TipoArticuloResponseDTO dto = new TipoArticuloResponseDTO();
        dto.setIdTipoArticulo(t.getIdTipoArticulo());
        dto.setNombreTipoArticulo(t.getNombreTipoArticulo());
        dto.setEstado(t.getEstadoArticulo().getTipoEstadoArticulo());
        dto.setPorcentajeMin(t.getParametroAvaluo().getPorcentajeMin());
        dto.setPorcentajeMax(t.getParametroAvaluo().getPorcentajeMax());
        return dto;
    }

    private TipoArticuloSimpleDTO mapTipoArticuloSimpleToDto(TiposArticulos t) {
        TipoArticuloSimpleDTO dto = new TipoArticuloSimpleDTO();
        dto.setIdTipoArticulo(t.getIdTipoArticulo());
        dto.setNombreTipoArticulo(t.getNombreTipoArticulo());
        dto.setPorcentajeMin(t.getParametroAvaluo().getPorcentajeMin());
        dto.setPorcentajeMax(t.getParametroAvaluo().getPorcentajeMax());
        return dto;
    }

    // ← MAPPER PARA CLIENTES: NO INCLUYE precio_avaluo
    private ArticuloResponseDTO mapArticuloToDto(Articulos a, String urlImagen) {
        ArticuloResponseDTO dto = new ArticuloResponseDTO();
        dto.setIdArticulo(a.getIdArticulo());
        dto.setIdCliente(a.getCliente().getIdCliente());
        dto.setNombreCliente(a.getCliente().getNombreCliente() + " " + a.getCliente().getApellidoCliente());
        dto.setIdEstado(a.getIdEstado());
        dto.setEstadoArticulo(a.getEstadoArticulo());
        dto.setNombreArticulo(a.getNombreArticulo());
        dto.setDescripcion(a.getDescripcion());
        dto.setPrecioArticulo(a.getPrecioArticulo());
        // NO incluimos precio_avaluo aquí
        dto.setTipoArticulo(mapTipoArticuloSimpleToDto(a.getTipoArticulo()));
        dto.setUrlImagen(urlImagen);
        return dto;
    }

    private ImagenResponseDTO mapImagenToDto(ImagenesArticulos i) {
        ImagenResponseDTO dto = new ImagenResponseDTO();
        dto.setIdImagen(i.getIdImagen());
        dto.setIdArticulo(i.getArticulo().getIdArticulo());
        dto.setUrlImagen(i.getUrlImagen());
        return dto;
    }
}