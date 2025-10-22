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
    private final TiposArticulosRepository tiposArticulosRepository;
    private final JwtUtil jwtUtil;

    public PrestamosService(PrestamosRepository prestamosRepository,
                            ArticulosRepository articulosRepository,
                            ClienteRepository clienteRepository,
                            AdministradorRepository administradorRepository,
                            ImagenesArticulosRepository imagenesArticulosRepository,
                            ContratosRepository contratosRepository,
                            TiposArticulosRepository tiposArticulosRepository,
                            JwtUtil jwtUtil) {
        this.prestamosRepository = prestamosRepository;
        this.articulosRepository = articulosRepository;
        this.clienteRepository = clienteRepository;
        this.administradorRepository = administradorRepository;
        this.imagenesArticulosRepository = imagenesArticulosRepository;
        this.contratosRepository = contratosRepository;
        this.tiposArticulosRepository = tiposArticulosRepository;
        this.jwtUtil = jwtUtil;
    }

    // ==================== LISTAR TIPOS ====================

    public List<TipoArticuloResponseDTO> listarTiposActivos() {
        List<TiposArticulos> tipos = tiposArticulosRepository.findAllActivos();
        return tipos.stream().map(this::mapTipoArticuloToDto).collect(Collectors.toList());
    }

    // ==================== LISTAR ARTÍCULOS SOLICITADOS ====================

    public List<ArticuloSolicitadoDTO> listarArticulosSolicitados() {
        List<Articulos> articulos = articulosRepository.findAll().stream()
                .filter(a -> a.getIdEstado() == 1)
                .collect(Collectors.toList());
        return articulos.stream().map(this::mapArticuloToSolicitadoDto).collect(Collectors.toList());
    }

    // ==================== REGISTRAR ARTÍCULO ====================

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

    // ==================== CREAR PRÉSTAMO ====================

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

    // ==================== EVALUAR PRÉSTAMO (MODIFICADO) ====================

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

        Articulos articulo = prestamo.getArticulo();

        if (dto.getIdEstado() == 3) { // ✅ APROBADO
            if (dto.getTasaInteres() == null || dto.getMontoPrestamo() == null ||
                    dto.getPorcentajeAvaluo() == null || dto.getPlazoMeses() == null) {
                throw new RuntimeException("Debe completar todos los campos para aprobar el préstamo.");
            }

            LocalDate fechaInicio = LocalDate.now();
            LocalDate fechaVencimiento = fechaInicio.plusMonths(dto.getPlazoMeses());

            prestamo.setTasaInteres(dto.getTasaInteres());
            prestamo.setMontoPrestamo(dto.getMontoPrestamo());
            prestamo.setPorcentajeAvaluo(dto.getPorcentajeAvaluo());
            prestamo.setPlazoMeses(dto.getPlazoMeses());
            prestamo.setFechaInicio(fechaInicio);
            prestamo.setFechaVencimiento(fechaVencimiento);

            // ✅ Calcular saldo total con intereses
            BigDecimal montoTotal = calcularMontoConInteres(dto.getMontoPrestamo(), dto.getTasaInteres(), dto.getPlazoMeses());
            prestamo.setSaldoAdeudado(montoTotal);

            articulo.setIdEstado(3); // Aprobado

            // Crear contrato automáticamente
            Contratos contrato = new Contratos();
            contrato.setPrestamo(prestamo);
            contrato.setIdEstado(10);
            contrato.setDocumentoContrato(generarUrlDocumentoContrato(prestamo));
            contratosRepository.save(contrato);
        } else if (dto.getIdEstado() == 4) { // ❌ RECHAZADO
            articulo.setIdEstado(4);
        }

        prestamo.setIdEstado(dto.getIdEstado());
        prestamosRepository.save(prestamo);
        articulosRepository.save(articulo);

        return mapPrestamoToDto(prestamo);
    }

    // ==================== CÁLCULO DEL MONTO TOTAL ====================

    private BigDecimal calcularMontoConInteres(BigDecimal montoPrestamo, BigDecimal tasaInteres, Integer plazoMeses) {
        BigDecimal interes = montoPrestamo
                .multiply(tasaInteres)
                .multiply(BigDecimal.valueOf(plazoMeses))
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        return montoPrestamo.add(interes);
    }

    // ==================== OTROS MÉTODOS ====================

    public List<PrestamoResponseDTO> listarPrestamos() {
        return prestamosRepository.findAll().stream().map(this::mapPrestamoToDto).collect(Collectors.toList());
    }

    public PrestamoResponseDTO obtenerPrestamoPorId(Integer idPrestamo) {
        Prestamos prestamo = prestamosRepository.findById(idPrestamo)
                .orElseThrow(() -> new RuntimeException("Préstamo no encontrado"));
        return mapPrestamoToDto(prestamo);
    }

    private String generarUrlDocumentoContrato(Prestamos prestamo) {
        return "https://casadeempeno.com/contratos/contrato_" +
                prestamo.getIdPrestamo() + "_" +
                prestamo.getCliente().getIdCliente() + ".pdf";
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
        dto.setSaldoAdeudado(p.getSaldoAdeudado()); // ✨ AGREGAR ESTA LÍNEA
        dto.setPorcentajeAvaluo(p.getPorcentajeAvaluo());
        dto.setPlazoMeses(p.getPlazoMeses());
        dto.setFechaInicio(p.getFechaInicio());
        dto.setFechaVencimiento(p.getFechaVencimiento());
        dto.setPrecioArticulo(p.getArticulo().getPrecioArticulo());
        dto.setPrecioAvaluo(p.getArticulo().getPrecioAvaluo());

        // Agregar descripción del artículo
        dto.setDescripcionArticulo(p.getArticulo().getDescripcion());

        // Obtener todas las imágenes del artículo
        List<ImagenesArticulos> imagenes = imagenesArticulosRepository.findByArticuloId(p.getArticulo().getIdArticulo());
        if (!imagenes.isEmpty()) {
            // Primera imagen (compatibilidad)
            dto.setUrlImagen(imagenes.get(0).getUrlImagen());

            // Todas las imágenes para el carrusel
            List<String> urlImagenes = imagenes.stream()
                    .map(ImagenesArticulos::getUrlImagen)
                    .collect(java.util.stream.Collectors.toList());
            dto.setImagenes(urlImagenes);
        }

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
        if (!imagenes.isEmpty()) dto.setUrlImagen(imagenes.get(0).getUrlImagen());
        return dto;
    }

    // ==================== LISTAR MIS PRÉSTAMOS ACTIVOS ====================

    public List<PrestamoResponseDTO> listarMisPrestamoActivos(Integer idCliente) {
        List<Prestamos> prestamos = prestamosRepository.findPrestamosActivosCliente(idCliente);
        return prestamos.stream().map(this::mapPrestamoToDto).collect(Collectors.toList());
    }
}
