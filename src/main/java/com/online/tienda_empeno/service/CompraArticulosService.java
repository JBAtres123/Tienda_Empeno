package com.online.tienda_empeno.service;

import com.online.tienda_empeno.dto.*;
import com.online.tienda_empeno.entity.*;
import com.online.tienda_empeno.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CompraArticulosService {

    private final CompraArticuloDelClienteRepository compraArticuloRepository;
    private final ArticulosRepository articulosRepository;
    private final ClienteRepository clienteRepository;
    private final AdministradorRepository administradorRepository;
    private final ImagenesArticulosRepository imagenesArticulosRepository;
    private final TiposArticulosRepository tiposArticulosRepository;
    private final FileStorageService fileStorageService;

    public CompraArticulosService(CompraArticuloDelClienteRepository compraArticuloRepository,
                                  ArticulosRepository articulosRepository,
                                  ClienteRepository clienteRepository,
                                  AdministradorRepository administradorRepository,
                                  ImagenesArticulosRepository imagenesArticulosRepository,
                                  TiposArticulosRepository tiposArticulosRepository,
                                  FileStorageService fileStorageService) {
        this.compraArticuloRepository = compraArticuloRepository;
        this.articulosRepository = articulosRepository;
        this.clienteRepository = clienteRepository;
        this.administradorRepository = administradorRepository;
        this.imagenesArticulosRepository = imagenesArticulosRepository;
        this.tiposArticulosRepository = tiposArticulosRepository;
        this.fileStorageService = fileStorageService;
    }

    // ==================== CLIENTE: REGISTRAR ARTÍCULO PARA VENDER ====================

    @Transactional
    public ArticuloResponseDTO registrarArticuloParaVender(ArticuloVenderDTO dto, Integer idCliente,
                                                            MultipartFile[] imagenes) {
        Cliente cliente = clienteRepository.findById(idCliente)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado con id " + idCliente));

        TiposArticulos tipoArticulo = tiposArticulosRepository.findById(dto.getIdTipoArticulo())
                .orElseThrow(() -> new RuntimeException("Tipo de artículo no encontrado"));

        if (tipoArticulo.getEstadoArticulo().getIdEstadoArticulo() != 1) {
            throw new RuntimeException("El tipo de artículo seleccionado no está activo");
        }

        int estadoFisico = Integer.parseInt(dto.getEstadoArticulo());
        if (estadoFisico < 1 || estadoFisico > 10) {
            throw new RuntimeException("El estado del artículo debe ser un valor entre 1 y 10");
        }

        // Validar que se envíen al menos 3 imágenes
        if (imagenes == null || imagenes.length < 3) {
            throw new RuntimeException("Debes agregar al menos 3 imágenes del artículo");
        }

        if (imagenes.length > 10) {
            throw new RuntimeException("El máximo de imágenes permitidas es 10");
        }

        // Crear el artículo con estado 12 (A Confirmar Venta)
        Articulos articulo = new Articulos();
        articulo.setCliente(cliente);
        articulo.setTipoArticulo(tipoArticulo);
        articulo.setIdEstado(12); // A Confirmar Venta
        articulo.setEstadoArticulo(dto.getEstadoArticulo());
        articulo.setNombreArticulo(dto.getNombreArticulo());
        articulo.setDescripcion(dto.getDescripcion());
        articulo.setPrecioArticulo(dto.getPrecioArticulo());
        articulo.setPrecioAvaluo(dto.getPrecioArticulo()); // Para ventas, precio = avalúo

        Articulos articuloGuardado = articulosRepository.save(articulo);

        // Guardar múltiples imágenes
        String primeraUrlImagen = null;
        for (MultipartFile imagen : imagenes) {
            try {
                String urlImagen = fileStorageService.guardarImagen(imagen);

                ImagenesArticulos imagenEntity = new ImagenesArticulos();
                imagenEntity.setArticulo(articuloGuardado);
                imagenEntity.setUrlImagen(urlImagen);
                imagenesArticulosRepository.save(imagenEntity);

                if (primeraUrlImagen == null) {
                    primeraUrlImagen = urlImagen;
                }
            } catch (Exception e) {
                throw new RuntimeException("Error al guardar imagen: " + e.getMessage());
            }
        }

        // Crear registro de compra con estado 13 (En Espera)
        CompraArticuloDelCliente compra = new CompraArticuloDelCliente();
        compra.setArticulo(articuloGuardado);
        compra.setIdEstado(13); // En Espera
        compraArticuloRepository.save(compra);

        return mapArticuloToDto(articuloGuardado, primeraUrlImagen);
    }

    // ==================== ADMIN: LISTAR ARTÍCULOS PENDIENTES DE COMPRA ====================

    public List<ArticuloVenderListadoDTO> listarArticulosPendientesCompra() {
        List<Articulos> articulos = articulosRepository.findAll().stream()
                .filter(a -> a.getIdEstado() == 12) // A Confirmar Venta
                .collect(Collectors.toList());

        return articulos.stream()
                .map(this::mapArticuloToListadoDto)
                .collect(Collectors.toList());
    }

    // ==================== ADMIN: CREAR SOLICITUD DE COMPRA ====================

    @Transactional
    public CompraResponseDTO crearSolicitudCompra(Integer idArticulo, Integer idAdmin) {
        Articulos articulo = articulosRepository.findById(idArticulo)
                .orElseThrow(() -> new RuntimeException("Artículo no encontrado"));

        if (articulo.getIdEstado() != 12) {
            throw new RuntimeException("El artículo no está disponible para compra");
        }

        if (compraArticuloRepository.existsByArticuloId(idArticulo)) {
            throw new RuntimeException("Ya existe una solicitud de compra para este artículo");
        }

        Administradores admin = administradorRepository.findById(idAdmin)
                .orElseThrow(() -> new RuntimeException("Administrador no encontrado"));

        // Validar que el admin tenga rol 1 o 3
        if (admin.getIdRol() != 1 && admin.getIdRol() != 3) {
            throw new RuntimeException("No tienes permiso para evaluar compras de artículos");
        }

        CompraArticuloDelCliente compra = new CompraArticuloDelCliente();
        compra.setArticulo(articulo);
        compra.setIdEstado(13); // En Espera

        CompraArticuloDelCliente compraGuardada = compraArticuloRepository.save(compra);

        return mapCompraToDto(compraGuardada);
    }

    // ==================== ADMIN: EVALUAR COMPRA ====================

    @Transactional
    public CompraResponseDTO evaluarCompra(Integer idCompra, CompraEvaluarDTO dto, Integer idAdmin) {
        CompraArticuloDelCliente compra = compraArticuloRepository.findById(idCompra)
                .orElseThrow(() -> new RuntimeException("Solicitud de compra no encontrada"));

        if (compra.getIdEstado() != 13) {
            throw new RuntimeException("Esta solicitud de compra ya fue evaluada");
        }

        Administradores admin = administradorRepository.findById(idAdmin)
                .orElseThrow(() -> new RuntimeException("Administrador no encontrado"));

        // Validar que el admin tenga rol 1 o 3
        if (admin.getIdRol() != 1 && admin.getIdRol() != 3) {
            throw new RuntimeException("No tienes permiso para evaluar compras de artículos");
        }

        if (dto.getIdEstado() != 14 && dto.getIdEstado() != 15) {
            throw new RuntimeException("Estado inválido. Use 14 para Comprado o 15 para No Aceptado");
        }

        Articulos articulo = compra.getArticulo();
        compra.setAdministrador(admin);

        if (dto.getIdEstado() == 14) { // ✅ COMPRADO
            if (dto.getPrecioCompra() == null || dto.getPrecioCompra().compareTo(java.math.BigDecimal.ZERO) <= 0) {
                throw new RuntimeException("Debe especificar el precio de compra");
            }

            compra.setIdEstado(14); // Comprado
            compra.setPrecioCompra(dto.getPrecioCompra());
            compra.setFechaCompra(LocalDate.now());

            String mensajePositivo = dto.getMensaje() != null ? dto.getMensaje() :
                    String.format("¡Felicidades! Hemos aceptado la compra de tu artículo '%s' por un monto de $%.2f. " +
                                    "Puedes pasar a recoger tu pago en nuestras oficinas. ¡Gracias por confiar en nosotros!",
                            articulo.getNombreArticulo(), dto.getPrecioCompra());

            compra.setMensaje(mensajePositivo);

            // Cambiar artículo a estado 9 (En Venta)
            articulo.setIdEstado(9);

        } else if (dto.getIdEstado() == 15) { // ❌ NO ACEPTADO
            compra.setIdEstado(15); // No Aceptado

            String mensajeRechazo = dto.getMensaje() != null ? dto.getMensaje() :
                    String.format("Lamentamos informarte que no podemos aceptar la compra de tu artículo '%s' " +
                                    "en este momento. Puede que el artículo no cumpla con nuestros estándares o ya tengamos " +
                                    "suficiente inventario de este tipo. Te invitamos a intentarlo nuevamente en el futuro.",
                            articulo.getNombreArticulo());

            compra.setMensaje(mensajeRechazo);

            // Cambiar artículo a estado 16 (Obsoleto)
            articulo.setIdEstado(16);
        }

        compraArticuloRepository.save(compra);
        articulosRepository.save(articulo);

        return mapCompraToDto(compra);
    }

    // ==================== CLIENTE: VER MIS SOLICITUDES DE VENTA ====================

    public List<CompraResponseDTO> listarMisSolicitudesVenta(Integer idCliente) {
        List<CompraArticuloDelCliente> compras = compraArticuloRepository.findByClienteId(idCliente);
        return compras.stream()
                .map(this::mapCompraToDto)
                .collect(Collectors.toList());
    }

    // ==================== ADMIN: VER TODAS LAS COMPRAS ====================

    public List<CompraResponseDTO> listarTodasLasCompras() {
        return compraArticuloRepository.findAll().stream()
                .map(this::mapCompraToDto)
                .collect(Collectors.toList());
    }

    // ==================== MAPPERS ====================

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

        TipoArticuloSimpleDTO tipoDto = new TipoArticuloSimpleDTO();
        tipoDto.setIdTipoArticulo(a.getTipoArticulo().getIdTipoArticulo());
        tipoDto.setNombreTipoArticulo(a.getTipoArticulo().getNombreTipoArticulo());
        dto.setTipoArticulo(tipoDto);

        dto.setUrlImagen(urlImagen);
        return dto;
    }

    private ArticuloVenderListadoDTO mapArticuloToListadoDto(Articulos a) {
        ArticuloVenderListadoDTO dto = new ArticuloVenderListadoDTO();
        dto.setIdArticulo(a.getIdArticulo());
        dto.setNombreArticulo(a.getNombreArticulo());
        dto.setDescripcion(a.getDescripcion());
        dto.setPrecioArticulo(a.getPrecioArticulo());
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

    private CompraResponseDTO mapCompraToDto(CompraArticuloDelCliente c) {
        CompraResponseDTO dto = new CompraResponseDTO();
        dto.setIdCompra(c.getIdCompra());
        dto.setIdArticulo(c.getArticulo().getIdArticulo());
        dto.setNombreArticulo(c.getArticulo().getNombreArticulo());
        dto.setIdCliente(c.getArticulo().getCliente().getIdCliente());
        dto.setNombreCliente(c.getArticulo().getCliente().getNombreCliente() + " " +
                c.getArticulo().getCliente().getApellidoCliente());

        if (c.getAdministrador() != null) {
            dto.setIdAdmin(c.getAdministrador().getIdAdmin());
            dto.setNombreAdmin(c.getAdministrador().getNombreAdmin() + " " +
                    c.getAdministrador().getApellidoAdmin());
        }

        dto.setIdEstado(c.getIdEstado());
        dto.setNombreEstado(obtenerNombreEstado(c.getIdEstado()));
        dto.setMensaje(c.getMensaje());
        dto.setFechaCompra(c.getFechaCompra());
        dto.setPrecioCompra(c.getPrecioCompra());
        dto.setFechaCreacion(c.getFechaCreacion());

        // Agregar información del artículo
        dto.setDescripcionArticulo(c.getArticulo().getDescripcion());
        dto.setPrecioOfrecido(c.getArticulo().getPrecioArticulo());

        // Obtener todas las imágenes del artículo
        List<ImagenesArticulos> imagenes = imagenesArticulosRepository.findByArticuloId(c.getArticulo().getIdArticulo());
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
            case 12: return "A Confirmar Venta";
            case 13: return "En Espera";
            case 14: return "Comprado";
            case 15: return "No Aceptado";
            case 16: return "Obsoleto";
            default: return "Desconocido";
        }
    }
}