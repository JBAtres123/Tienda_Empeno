package com.online.tienda_empeno.service;

import com.online.tienda_empeno.dto.*;
import com.online.tienda_empeno.entity.*;
import com.online.tienda_empeno.repository.*;
import com.online.tienda_empeno.utils.JwtUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PrestamosService {

    private final PrestamosRepository prestamosRepository;
    private final ArticulosRepository articulosRepository;
    private final ClienteRepository clienteRepository;
    private final AdministradorRepository administradorRepository;
    private final ImagenesArticulosRepository imagenesArticulosRepository;
    private final ContratosRepository contratosRepository;
    private final TiposArticulosRepository tiposArticulosRepository; // ← NUEVO
    private final JwtUtil jwtUtil;

    public PrestamosService(PrestamosRepository prestamosRepository,
                            ArticulosRepository articulosRepository,
                            ClienteRepository clienteRepository,
                            AdministradorRepository administradorRepository,
                            ImagenesArticulosRepository imagenesArticulosRepository,
                            ContratosRepository contratosRepository,
                            TiposArticulosRepository tiposArticulosRepository, // ← NUEVO
                            JwtUtil jwtUtil) {
        this.prestamosRepository = prestamosRepository;
        this.articulosRepository = articulosRepository;
        this.clienteRepository = clienteRepository;
        this.administradorRepository = administradorRepository;
        this.imagenesArticulosRepository = imagenesArticulosRepository;
        this.contratosRepository = contratosRepository;
        this.tiposArticulosRepository = tiposArticulosRepository; // ← NUEVO
        this.jwtUtil = jwtUtil;
    }

    public List<TipoArticuloResponseDTO> listarTiposActivos() {
        List<TiposArticulos> tipos = tiposArticulosRepository.findAllActivos();
        return tipos.stream().map(this::mapTipoArticuloToDto).collect(Collectors.toList());
    }

    // ========== LISTAR ARTÍCULOS SOLICITADOS (id_estado = 1) ==========
    public List<ArticuloSolicitadoDTO> listarArticulosSolicitados() {
        List<Articulos> articulos = articulosRepository.findAll().stream()
                .filter(a -> a.getIdEstado() == 1)
                .collect(Collectors.toList());

        return articulos.stream().map(this::mapArticuloToSolicitadoDto).collect(Collectors.toList());
    }

    @Transactional
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

        BigDecimal porcentajeMin = tipoArticulo.getParametroAvaluo().getPorcentajeMin();
        BigDecimal porcentajeMax = tipoArticulo.getParametroAvaluo().getPorcentajeMax();

        BigDecimal porcentajeBase = porcentajeMin.add(porcentajeMax)
                .divide(new BigDecimal("2"), 4, RoundingMode.HALF_UP)
                .divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP);

        BigDecimal factorEstado = new BigDecimal(estadoFisico)
                .divide(new BigDecimal("10"), 2, RoundingMode.HALF_UP);

        BigDecimal precioAvaluo = dto.getPrecioArticulo()
                .multiply(porcentajeBase)
                .multiply(factorEstado)
                .setScale(2, RoundingMode.HALF_UP);

        Articulos articulo = new Articulos();
        articulo.setCliente(cliente);
        articulo.setTipoArticulo(tipoArticulo);
        articulo.setIdEstado(1);
        articulo.setEstadoArticulo(dto.getEstadoArticulo());
        articulo.setNombreArticulo(dto.getNombreArticulo());
        articulo.setDescripcion(dto.getDescripcion());
        articulo.setPrecioArticulo(dto.getPrecioArticulo());
        articulo.setPrecioAvaluo(precioAvaluo);

        Articulos articuloGuardado = articulosRepository.save(articulo);

        ImagenesArticulos imagen = new ImagenesArticulos();
        imagen.setArticulo(articuloGuardado);
        imagen.setUrlImagen(dto.getUrlImagen());
        ImagenesArticulos imagenGuardada = imagenesArticulosRepository.save(imagen);

        return mapArticuloToDto(articuloGuardado, imagenGuardada.getUrlImagen());
    }

    @Transactional
    public PrestamoResponseDTO crearPrestamo(PrestamoCrearDTO dto, Integer idAdmin) {
        Articulos articulo = articulosRepository.findById(dto.getIdArticulo())
                .orElseThrow(() -> new RuntimeException("Artículo no encontrado con id " + dto.getIdArticulo()));

        if (articulo.getIdEstado() != 1) {
            throw new RuntimeException("El artículo no está en estado Solicitado");
        }

        Prestamos prestamoExistente = prestamosRepository.findByArticuloId(dto.getIdArticulo());
        if (prestamoExistente != null) {
            throw new RuntimeException("Ya existe un préstamo para este artículo");
        }

        articulo.setIdEstado(2);
        articulosRepository.save(articulo);

        Cliente cliente = articulo.getCliente();
        Administradores admin = administradorRepository.findById(idAdmin)
                .orElseThrow(() -> new RuntimeException("Administrador no encontrado"));

        Prestamos prestamo = new Prestamos();
        prestamo.setArticulo(articulo);
        prestamo.setCliente(cliente);
        prestamo.setAdministrador(admin);
        prestamo.setIdEstado(2);

        Prestamos prestamoGuardado = prestamosRepository.save(prestamo);

        return mapPrestamoToDto(prestamoGuardado);
    }

    // ========== MÉTODO MODIFICADO: Crear contrato al aprobar ==========
    @Transactional
    public PrestamoResponseDTO evaluarPrestamo(Integer idPrestamo, PrestamoEvaluarDTO dto) {
        Prestamos prestamo = prestamosRepository.findById(idPrestamo)
                .orElseThrow(() -> new RuntimeException("Préstamo no encontrado con id " + idPrestamo));

        if (prestamo.getIdEstado() != 2) {
            throw new RuntimeException("El préstamo no está en evaluación");
        }

        if (dto.getIdEstado() != 3 && dto.getIdEstado() != 4) {
            throw new RuntimeException("Estado inválido. Use 3 para Aprobar o 4 para Rechazar");
        }

        if (dto.getIdEstado() == 3) {
            if (dto.getTasaInteres() == null || dto.getMontoPrestamo() == null ||
                    dto.getPorcentajeAvaluo() == null || dto.getPlazoMeses() == null) {
                throw new RuntimeException("Para aprobar debe completar: tasa_interes, monto_prestamo, porcentaje_avaluo y plazo_meses");
            }

            LocalDate fechaInicio = LocalDate.now();
            LocalDate fechaVencimiento = fechaInicio.plusMonths(dto.getPlazoMeses());

            prestamo.setTasaInteres(dto.getTasaInteres());
            prestamo.setMontoPrestamo(dto.getMontoPrestamo());
            prestamo.setPorcentajeAvaluo(dto.getPorcentajeAvaluo());
            prestamo.setPlazoMeses(dto.getPlazoMeses());
            prestamo.setFechaInicio(fechaInicio);
            prestamo.setFechaVencimiento(fechaVencimiento);

            // ========== CREAR CONTRATO AUTOMÁTICAMENTE ==========
            Contratos contrato = new Contratos();
            contrato.setPrestamo(prestamo);
            contrato.setIdEstado(10);
            contrato.setDocumentoContrato(generarUrlDocumentoContrato(prestamo));
            contratosRepository.save(contrato);
            // ====================================================
        }

        prestamo.setIdEstado(dto.getIdEstado());

        Articulos articulo = prestamo.getArticulo();
        articulo.setIdEstado(dto.getIdEstado());
        articulosRepository.save(articulo);

        Prestamos prestamoActualizado = prestamosRepository.save(prestamo);

        return mapPrestamoToDto(prestamoActualizado);
    }

    public List<PrestamoResponseDTO> listarPrestamos() {
        List<Prestamos> prestamos = prestamosRepository.findAll();
        return prestamos.stream().map(this::mapPrestamoToDto).collect(Collectors.toList());
    }

    public PrestamoResponseDTO obtenerPrestamoPorId(Integer idPrestamo) {
        Prestamos prestamo = prestamosRepository.findById(idPrestamo)
                .orElseThrow(() -> new RuntimeException("Préstamo no encontrado"));
        return mapPrestamoToDto(prestamo);
    }

    // ========== MÉTODO NUEVO: Generar URL del documento del contrato ==========
    private String generarUrlDocumentoContrato(Prestamos prestamo) {
        // Aquí puedes generar un PDF real o simplemente una URL
        return "https://casadeempeno.com/contratos/contrato_" +
                prestamo.getIdPrestamo() + "_" +
                prestamo.getCliente().getIdCliente() + ".pdf";
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
        dto.setTipoArticulo(mapTipoArticuloSimpleToDto(a.getTipoArticulo()));
        dto.setUrlImagen(urlImagen);
        return dto;
    }

    private PrestamoResponseDTO mapPrestamoToDto(Prestamos p) {
        PrestamoResponseDTO dto = new PrestamoResponseDTO();
        dto.setIdPrestamo(p.getIdPrestamo());
        dto.setIdArticulo(p.getArticulo().getIdArticulo());
        dto.setNombreArticulo(p.getArticulo().getNombreArticulo());
        dto.setIdCliente(p.getCliente().getIdCliente());
        dto.setNombreCliente(p.getCliente().getNombreCliente() + " " + p.getCliente().getApellidoCliente());
        dto.setIdAdmin(p.getAdministrador().getIdAdmin());
        dto.setNombreAdmin(p.getAdministrador().getNombreAdmin() + " " + p.getAdministrador().getApellidoAdmin());
        dto.setIdEstado(p.getIdEstado());
        dto.setNombreEstado(obtenerNombreEstado(p.getIdEstado()));
        dto.setTasaInteres(p.getTasaInteres());
        dto.setMontoPrestamo(p.getMontoPrestamo());
        dto.setPorcentajeAvaluo(p.getPorcentajeAvaluo());
        dto.setPlazoMeses(p.getPlazoMeses());
        dto.setFechaInicio(p.getFechaInicio());
        dto.setFechaVencimiento(p.getFechaVencimiento());
        dto.setPrecioArticulo(p.getArticulo().getPrecioArticulo());
        dto.setPrecioAvaluo(p.getArticulo().getPrecioAvaluo());
        return dto;
    }

    private ImagenResponseDTO mapImagenToDto(ImagenesArticulos i) {
        ImagenResponseDTO dto = new ImagenResponseDTO();
        dto.setIdImagen(i.getIdImagen());
        dto.setIdArticulo(i.getArticulo().getIdArticulo());
        dto.setUrlImagen(i.getUrlImagen());
        return dto;
    }

    private String obtenerNombreEstado(Integer idEstado) {
        switch (idEstado) {
            case 1: return "Solicitado";
            case 2: return "En Evaluación";
            case 3: return "Aprobado";
            case 4: return "Rechazado";
            case 5: return "En Préstamo";
            default: return "Desconocido";
        }
    }

    // Mapper para ArticuloSolicitadoDTO
    private ArticuloSolicitadoDTO mapArticuloToSolicitadoDto(Articulos a) {
        ArticuloSolicitadoDTO dto = new ArticuloSolicitadoDTO();
        dto.setIdArticulo(a.getIdArticulo());
        dto.setNombreArticulo(a.getNombreArticulo());
        dto.setDescripcion(a.getDescripcion());
        dto.setPrecioArticulo(a.getPrecioArticulo());
        dto.setPrecioAvaluo(a.getPrecioAvaluo());
        dto.setEstadoArticulo(a.getEstadoArticulo());
        dto.setNombreCliente(a.getCliente().getNombreCliente() + " " + a.getCliente().getApellidoCliente());
        dto.setEmailCliente(a.getCliente().getEmailCliente());
        dto.setIdCliente(a.getCliente().getIdCliente());
        dto.setTipoArticulo(a.getTipoArticulo().getNombreTipoArticulo());

        List<ImagenesArticulos> imagenes = imagenesArticulosRepository.findByArticuloId(a.getIdArticulo());
        if (!imagenes.isEmpty()) {
            dto.setUrlImagen(imagenes.get(0).getUrlImagen());
        }

        return dto;
    }
}