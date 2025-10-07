package com.online.tienda_empeno.service;

import com.online.tienda_empeno.dto.*;
import com.online.tienda_empeno.entity.*;
import com.online.tienda_empeno.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public PrestamosService(PrestamosRepository prestamosRepository,
                            ArticulosRepository articulosRepository,
                            ClienteRepository clienteRepository,
                            AdministradorRepository administradorRepository,
                            ImagenesArticulosRepository imagenesArticulosRepository) {
        this.prestamosRepository = prestamosRepository;
        this.articulosRepository = articulosRepository;
        this.clienteRepository = clienteRepository;
        this.administradorRepository = administradorRepository;
        this.imagenesArticulosRepository = imagenesArticulosRepository;
    }

    // ========== LISTAR ARTÍCULOS SOLICITADOS (id_estado = 1) ==========
    public List<ArticuloSolicitadoDTO> listarArticulosSolicitados() {
        // Buscar artículos con estado = 1 (Solicitado)
        List<Articulos> articulos = articulosRepository.findAll().stream()
                .filter(a -> a.getIdEstado() == 1)
                .collect(Collectors.toList());

        return articulos.stream().map(this::mapArticuloToSolicitadoDto).collect(Collectors.toList());
    }

    // ========== CREAR PRÉSTAMO (iniciar evaluación) ==========
    @Transactional
    public PrestamoResponseDTO crearPrestamo(PrestamoCrearDTO dto, Integer idAdmin) {
        // 1. Validar que el artículo exista
        Articulos articulo = articulosRepository.findById(dto.getIdArticulo())
                .orElseThrow(() -> new RuntimeException("Artículo no encontrado con id " + dto.getIdArticulo()));

        // 2. Validar que el artículo esté en estado "Solicitado" (1)
        if (articulo.getIdEstado() != 1) {
            throw new RuntimeException("El artículo no está en estado Solicitado");
        }

        // 3. Validar que no exista ya un préstamo para este artículo
        Prestamos prestamoExistente = prestamosRepository.findByArticuloId(dto.getIdArticulo());
        if (prestamoExistente != null) {
            throw new RuntimeException("Ya existe un préstamo para este artículo");
        }

        // 4. Cambiar estado del artículo: 1 (Solicitado) → 2 (En Evaluación)
        articulo.setIdEstado(2);
        articulosRepository.save(articulo);

        // 5. Obtener cliente y administrador
        Cliente cliente = articulo.getCliente();
        Administradores admin = administradorRepository.findById(idAdmin)
                .orElseThrow(() -> new RuntimeException("Administrador no encontrado"));

        // 6. Crear el préstamo
        Prestamos prestamo = new Prestamos();
        prestamo.setArticulo(articulo);
        prestamo.setCliente(cliente);
        prestamo.setAdministrador(admin);
        prestamo.setIdEstado(2); // En Evaluación

        Prestamos prestamoGuardado = prestamosRepository.save(prestamo);

        return mapPrestamoToDto(prestamoGuardado);
    }

    // ========== EVALUAR PRÉSTAMO (aprobar/rechazar) ==========
    @Transactional
    public PrestamoResponseDTO evaluarPrestamo(Integer idPrestamo, PrestamoEvaluarDTO dto) {
        // 1. Buscar el préstamo
        Prestamos prestamo = prestamosRepository.findById(idPrestamo)
                .orElseThrow(() -> new RuntimeException("Préstamo no encontrado con id " + idPrestamo));

        // 2. Validar que esté en estado "En Evaluación" (2)
        if (prestamo.getIdEstado() != 2) {
            throw new RuntimeException("El préstamo no está en evaluación");
        }

        // 3. Validar estado destino (3 = Aprobado, 4 = Rechazado)
        if (dto.getIdEstado() != 3 && dto.getIdEstado() != 4) {
            throw new RuntimeException("Estado inválido. Use 3 para Aprobar o 4 para Rechazar");
        }

        // 4. Si es APROBADO, validar campos obligatorios
        if (dto.getIdEstado() == 3) {
            if (dto.getTasaInteres() == null || dto.getMontoPrestamo() == null ||
                    dto.getPorcentajeAvaluo() == null || dto.getPlazoMeses() == null) {
                throw new RuntimeException("Para aprobar debe completar: tasa_interes, monto_prestamo, porcentaje_avaluo y plazo_meses");
            }

            // Calcular fechas
            LocalDate fechaInicio = LocalDate.now();
            LocalDate fechaVencimiento = fechaInicio.plusMonths(dto.getPlazoMeses());

            prestamo.setTasaInteres(dto.getTasaInteres());
            prestamo.setMontoPrestamo(dto.getMontoPrestamo());
            prestamo.setPorcentajeAvaluo(dto.getPorcentajeAvaluo());
            prestamo.setPlazoMeses(dto.getPlazoMeses());
            prestamo.setFechaInicio(fechaInicio);
            prestamo.setFechaVencimiento(fechaVencimiento);
        }

        // 5. Actualizar estado del préstamo
        prestamo.setIdEstado(dto.getIdEstado());

        // 6. Actualizar estado del artículo
        Articulos articulo = prestamo.getArticulo();
        articulo.setIdEstado(dto.getIdEstado()); // 3 = Aprobado o 4 = Rechazado
        articulosRepository.save(articulo);

        Prestamos prestamoActualizado = prestamosRepository.save(prestamo);

        return mapPrestamoToDto(prestamoActualizado);
    }

    // ========== LISTAR TODOS LOS PRÉSTAMOS ==========
    public List<PrestamoResponseDTO> listarPrestamos() {
        List<Prestamos> prestamos = prestamosRepository.findAll();
        return prestamos.stream().map(this::mapPrestamoToDto).collect(Collectors.toList());
    }

    // ========== OBTENER PRÉSTAMO POR ID ==========
    public PrestamoResponseDTO obtenerPrestamoPorId(Integer idPrestamo) {
        Prestamos prestamo = prestamosRepository.findById(idPrestamo)
                .orElseThrow(() -> new RuntimeException("Préstamo no encontrado"));
        return mapPrestamoToDto(prestamo);
    }

    // ========== MAPPERS ==========
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

        // Obtener imagen
        List<ImagenesArticulos> imagenes = imagenesArticulosRepository.findByArticuloId(a.getIdArticulo());
        if (!imagenes.isEmpty()) {
            dto.setUrlImagen(imagenes.get(0).getUrlImagen());
        }

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

    private String obtenerNombreEstado(Integer idEstado) {
        switch (idEstado) {
            case 1: return "Solicitado";
            case 2: return "En Evaluación";
            case 3: return "Aprobado";
            case 4: return "Rechazado";
            default: return "Desconocido";
        }
    }
}