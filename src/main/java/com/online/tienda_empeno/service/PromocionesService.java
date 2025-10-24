package com.online.tienda_empeno.service;

import com.online.tienda_empeno.dto.CrearPromocionDTO;
import com.online.tienda_empeno.dto.PromocionDTO;
import com.online.tienda_empeno.entity.*;
import com.online.tienda_empeno.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PromocionesService {

    private final PromocionesRepository promocionesRepository;
    private final AdministradorRepository administradorRepository;
    private final TiposArticulosRepository tiposArticulosRepository;
    private final ProductosTiendaRepository productosTiendaRepository;

    public PromocionesService(PromocionesRepository promocionesRepository,
                              AdministradorRepository administradorRepository,
                              TiposArticulosRepository tiposArticulosRepository,
                              ProductosTiendaRepository productosTiendaRepository) {
        this.promocionesRepository = promocionesRepository;
        this.administradorRepository = administradorRepository;
        this.tiposArticulosRepository = tiposArticulosRepository;
        this.productosTiendaRepository = productosTiendaRepository;
    }

    // ==================== CREAR PROMOCIÓN ====================

    @Transactional
    public PromocionDTO crearPromocion(CrearPromocionDTO dto, Integer idAdmin) {
        Administradores admin = administradorRepository.findById(idAdmin)
                .orElseThrow(() -> new RuntimeException("Administrador no encontrado"));

        if (admin.getIdRol() != 1 && admin.getIdRol() != 3) {
            throw new RuntimeException("No tienes permiso para crear promociones");
        }

        // Validaciones
        if (dto.getFechaInicio().isAfter(dto.getFechaFin())) {
            throw new RuntimeException("La fecha de inicio no puede ser posterior a la fecha de fin");
        }

        if (dto.getValorDescuento().compareTo(java.math.BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("El valor del descuento debe ser mayor a 0");
        }

        Promociones promocion = new Promociones();
        promocion.setNombrePromocion(dto.getNombrePromocion());
        promocion.setDescripcion(dto.getDescripcion());
        promocion.setTipoPromocion(dto.getTipoPromocion());
        promocion.setTipoDescuento(dto.getTipoDescuento());
        promocion.setValorDescuento(dto.getValorDescuento());
        promocion.setFechaInicio(dto.getFechaInicio());
        promocion.setFechaFin(dto.getFechaFin());

        // Según el tipo de promoción
        if ("CATEGORIA".equals(dto.getTipoPromocion())) {
            if (dto.getIdTipoArticulo() == null) {
                throw new RuntimeException("Debe especificar la categoría para este tipo de promoción");
            }
            TiposArticulos tipo = tiposArticulosRepository.findById(dto.getIdTipoArticulo())
                    .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));
            promocion.setTipoArticulo(tipo);

        } else if ("PRODUCTO".equals(dto.getTipoPromocion())) {
            if (dto.getIdProductoTienda() == null) {
                throw new RuntimeException("Debe especificar el producto para este tipo de promoción");
            }
            ProductosTienda producto = productosTiendaRepository.findById(dto.getIdProductoTienda())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
            promocion.setProductoTienda(producto);

        } else if (!"GENERAL".equals(dto.getTipoPromocion())) {
            throw new RuntimeException("Tipo de promoción inválido. Use: CATEGORIA, PRODUCTO o GENERAL");
        }

        // Validar tipo de descuento
        if (!"PORCENTAJE".equals(dto.getTipoDescuento()) && !"MONTO_FIJO".equals(dto.getTipoDescuento())) {
            throw new RuntimeException("Tipo de descuento inválido. Use: PORCENTAJE o MONTO_FIJO");
        }

        if ("PORCENTAJE".equals(dto.getTipoDescuento()) && dto.getValorDescuento().compareTo(java.math.BigDecimal.valueOf(100)) > 0) {
            throw new RuntimeException("El porcentaje no puede ser mayor a 100");
        }

        Promociones promocionGuardada = promocionesRepository.save(promocion);

        return mapPromocionToDTO(promocionGuardada);
    }

    // ==================== ACTUALIZAR PROMOCIÓN ====================

    @Transactional
    public PromocionDTO actualizarPromocion(Integer idPromocion, CrearPromocionDTO dto) {
        Promociones promocion = promocionesRepository.findById(idPromocion)
                .orElseThrow(() -> new RuntimeException("Promoción no encontrada"));

        if (dto.getFechaInicio().isAfter(dto.getFechaFin())) {
            throw new RuntimeException("La fecha de inicio no puede ser posterior a la fecha de fin");
        }

        promocion.setNombrePromocion(dto.getNombrePromocion());
        promocion.setDescripcion(dto.getDescripcion());
        promocion.setTipoDescuento(dto.getTipoDescuento());
        promocion.setValorDescuento(dto.getValorDescuento());
        promocion.setFechaInicio(dto.getFechaInicio());
        promocion.setFechaFin(dto.getFechaFin());

        Promociones promocionActualizada = promocionesRepository.save(promocion);

        return mapPromocionToDTO(promocionActualizada);
    }

    // ==================== DESACTIVAR PROMOCIÓN ====================

    @Transactional
    public void desactivarPromocion(Integer idPromocion) {
        Promociones promocion = promocionesRepository.findById(idPromocion)
                .orElseThrow(() -> new RuntimeException("Promoción no encontrada"));

        promocion.setActivo(false);
        promocionesRepository.save(promocion);
    }

    // ==================== ELIMINAR PROMOCIÓN ====================

    @Transactional
    public void eliminarPromocion(Integer idPromocion) {
        Promociones promocion = promocionesRepository.findById(idPromocion)
                .orElseThrow(() -> new RuntimeException("Promoción no encontrada"));

        promocionesRepository.delete(promocion);
    }

    // ==================== LISTAR TODAS LAS PROMOCIONES ====================

    public List<PromocionDTO> listarTodasPromociones() {
        List<Promociones> promociones = promocionesRepository.findAll();
        return promociones.stream().map(this::mapPromocionToDTO).collect(Collectors.toList());
    }

    // ==================== LISTAR PROMOCIONES VIGENTES ====================

    public List<PromocionDTO> listarPromocionesVigentes() {
        List<Promociones> promociones = promocionesRepository.findPromocionesVigentes(LocalDateTime.now());
        return promociones.stream().map(this::mapPromocionToDTO).collect(Collectors.toList());
    }

    // ==================== OBTENER PROMOCIÓN POR ID ====================

    public PromocionDTO obtenerPromocionPorId(Integer idPromocion) {
        Promociones promocion = promocionesRepository.findById(idPromocion)
                .orElseThrow(() -> new RuntimeException("Promoción no encontrada"));

        return mapPromocionToDTO(promocion);
    }

    // ==================== MAPPER ====================

    private PromocionDTO mapPromocionToDTO(Promociones p) {
        PromocionDTO dto = new PromocionDTO();
        dto.setIdPromocion(p.getIdPromocion());
        dto.setNombrePromocion(p.getNombrePromocion());
        dto.setDescripcion(p.getDescripcion());
        dto.setTipoDescuento(p.getTipoDescuento());
        dto.setValorDescuento(p.getValorDescuento());
        dto.setFechaInicio(p.getFechaInicio());
        dto.setFechaFin(p.getFechaFin());

        return dto;
    }
}