package com.online.tienda_empeno.service;

import com.online.tienda_empeno.dto.*;
import com.online.tienda_empeno.entity.*;
import com.online.tienda_empeno.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TiendaService {

    private final ProductosTiendaRepository productosTiendaRepository;
    private final PedidosRepository pedidosRepository;
    private final DetallePedidoRepository detallePedidoRepository;
    private final PagoPedidoRepository pagoPedidoRepository;
    private final ValoracionesRepository valoracionesRepository;
    private final ArticulosRepository articulosRepository;
    private final ClienteRepository clienteRepository;
    private final AdministradorRepository administradorRepository;
    private final ImagenesArticulosRepository imagenesArticulosRepository;
    private final DireccionesRepository direccionesRepository;
    private final MetodoPagoRepository metodoPagoRepository;
    private final PromocionesRepository promocionesRepository;

    public TiendaService(ProductosTiendaRepository productosTiendaRepository,
                         PedidosRepository pedidosRepository,
                         DetallePedidoRepository detallePedidoRepository,
                         PagoPedidoRepository pagoPedidoRepository,
                         ValoracionesRepository valoracionesRepository,
                         ArticulosRepository articulosRepository,
                         ClienteRepository clienteRepository,
                         AdministradorRepository administradorRepository,
                         ImagenesArticulosRepository imagenesArticulosRepository,
                         DireccionesRepository direccionesRepository,
                         MetodoPagoRepository metodoPagoRepository,
                         PromocionesRepository promocionesRepository) {
        this.productosTiendaRepository = productosTiendaRepository;
        this.pedidosRepository = pedidosRepository;
        this.detallePedidoRepository = detallePedidoRepository;
        this.pagoPedidoRepository = pagoPedidoRepository;
        this.valoracionesRepository = valoracionesRepository;
        this.articulosRepository = articulosRepository;
        this.clienteRepository = clienteRepository;
        this.administradorRepository = administradorRepository;
        this.imagenesArticulosRepository = imagenesArticulosRepository;
        this.direccionesRepository = direccionesRepository;
        this.metodoPagoRepository = metodoPagoRepository;
        this.promocionesRepository = promocionesRepository;
    }

    // ==================== ADMIN: LISTAR ARTÍCULOS PARA PREPARAR (estado 9) ====================

    public List<ProductoTiendaResponseDTO> listarArticulosParaPreparar() {
        List<Articulos> articulos = articulosRepository.findAll().stream()
                .filter(a -> a.getIdEstado() == 9)
                .collect(Collectors.toList());

        return articulos.stream().map(this::mapArticuloToProductoDTO).collect(Collectors.toList());
    }

    // ==================== ADMIN: PREPARAR PRODUCTO PARA TIENDA ====================

    @Transactional
    public ProductoTiendaResponseDTO prepararProductoParaTienda(ProductoTiendaDTO dto, Integer idAdmin) {
        Articulos articulo = articulosRepository.findById(dto.getIdArticulo())
                .orElseThrow(() -> new RuntimeException("Artículo no encontrado"));

        if (articulo.getIdEstado() != 9) {
            throw new RuntimeException("El artículo no está disponible para preparar");
        }

        Administradores admin = administradorRepository.findById(idAdmin)
                .orElseThrow(() -> new RuntimeException("Administrador no encontrado"));

        if (admin.getIdRol() != 1 && admin.getIdRol() != 3) {
            throw new RuntimeException("No tienes permiso para preparar productos");
        }

        ProductosTienda producto = new ProductosTienda();
        producto.setArticulo(articulo);
        producto.setPrecioVentaTienda(dto.getPrecioVentaTienda());
        producto.setNombreEditado(dto.getNombreEditado());
        producto.setDescripcionEditada(dto.getDescripcionEditada());
        producto.setAdministrador(admin);

        ProductosTienda productoGuardado = productosTiendaRepository.save(producto);

        articulo.setIdEstado(17);
        articulosRepository.save(articulo);

        return mapProductoToDTO(productoGuardado);
    }

    // ==================== CLIENTE: VER CATÁLOGO CON PROMOCIONES ====================

    public List<ProductoTiendaResponseDTO> listarCatalogo() {
        List<ProductosTienda> productos = productosTiendaRepository.findProductosDisponibles();
        return productos.stream().map(this::mapProductoToDTO).collect(Collectors.toList());
    }

    // ==================== CLIENTE: VER DETALLE DE PRODUCTO CON PROMOCIÓN ====================

    public ProductoTiendaResponseDTO verDetalleProducto(Integer idProducto) {
        ProductosTienda producto = productosTiendaRepository.findById(idProducto)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        return mapProductoToDTO(producto);
    }

    // ==================== CLIENTE: CREAR PEDIDO CON PROMOCIONES ====================

    @Transactional
    public PedidoResponseDTO crearPedido(CrearPedidoDTO dto, Integer idCliente) {
        Cliente cliente = clienteRepository.findById(idCliente)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

        Pedidos pedido = new Pedidos();
        pedido.setCliente(cliente);
        pedido.setIdEstado(19);
        pedido.setTotal(BigDecimal.ZERO);

        Pedidos pedidoGuardado = pedidosRepository.save(pedido);

        BigDecimal total = BigDecimal.ZERO;
        for (ItemPedidoDTO item : dto.getItems()) {
            ProductosTienda producto = productosTiendaRepository.findByIdAndDisponible(item.getIdProductoTienda())
                    .orElseThrow(() -> new RuntimeException("Producto no disponible: " + item.getIdProductoTienda()));

            if (producto.getArticulo().getCliente().getIdCliente().equals(idCliente)) {
                throw new RuntimeException("No puedes comprar tu propio artículo: " + producto.getArticulo().getNombreArticulo());
            }

            // ✨ CALCULAR PRECIO CON PROMOCIÓN
            BigDecimal precioFinal = calcularPrecioConPromocion(producto);

            DetallePedido detalle = new DetallePedido();
            detalle.setPedido(pedidoGuardado);
            detalle.setProductoTienda(producto);
            detalle.setPrecioVenta(precioFinal); // ✨ Guardar precio con descuento
            detalle.setCantidad(item.getCantidad());
            detallePedidoRepository.save(detalle);

            total = total.add(precioFinal.multiply(BigDecimal.valueOf(item.getCantidad())));
        }

        pedidoGuardado.setTotal(total);
        pedidosRepository.save(pedidoGuardado);

        return mapPedidoToDTO(pedidoGuardado);
    }

    // ==================== CLIENTE: PAGAR PEDIDO ====================

    @Transactional
    public PedidoResponseDTO pagarPedido(PagarPedidoDTO dto, Integer idCliente) {
        Pedidos pedido = pedidosRepository.findById(dto.getIdPedido())
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));

        if (!pedido.getCliente().getIdCliente().equals(idCliente)) {
            throw new RuntimeException("Este pedido no te pertenece");
        }

        if (pedido.getIdEstado() != 19) {
            throw new RuntimeException("Este pedido ya fue procesado");
        }

        Cliente cliente = clienteRepository.findById(idCliente)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

        Direcciones direccion = direccionesRepository.findById(dto.getIdDireccion())
                .orElseThrow(() -> new RuntimeException("Dirección no encontrada"));

        MetodoPago metodoPago = metodoPagoRepository.findById(dto.getIdMetodoPago())
                .orElseThrow(() -> new RuntimeException("Método de pago no encontrado"));

        PagoPedido pago = new PagoPedido();
        pago.setPedido(pedido);
        pago.setCliente(cliente);
        pago.setDireccion(direccion);
        pago.setMetodoPago(metodoPago);
        pago.setMonto(pedido.getTotal());

        if (dto.getIdMetodoPago() == 1) {
            pago.setReferencia("****" + dto.getNumeroTarjeta().substring(dto.getNumeroTarjeta().length() - 4));
        } else {
            pago.setReferencia("EFECTIVO-" + System.currentTimeMillis());
        }

        pagoPedidoRepository.save(pago);

        pedido.setIdEstado(20);
        pedido.setFechaPago(LocalDateTime.now());
        pedido.setFechaEstimadaEntrega(LocalDate.now().plusDays(15));
        pedidosRepository.save(pedido);

        // ✨ CAMBIAR DUEÑO Y ESTADO DE ARTÍCULOS
        List<DetallePedido> detalles = detallePedidoRepository.findByPedidoId(pedido.getIdPedido());
        for (DetallePedido detalle : detalles) {
            Articulos articulo = detalle.getProductoTienda().getArticulo();
            articulo.setCliente(cliente);
            articulo.setIdEstado(18);
            articulosRepository.save(articulo);
        }

        return mapPedidoToDTO(pedido);
    }

    // ==================== CLIENTE: VER MIS PEDIDOS ====================

    public List<PedidoResponseDTO> listarMisPedidos(Integer idCliente) {
        List<Pedidos> pedidos = pedidosRepository.findByClienteId(idCliente);
        return pedidos.stream().map(this::mapPedidoToDTO).collect(Collectors.toList());
    }

    // ==================== CLIENTE: VER SEGUIMIENTO DE PEDIDO ====================

    public PedidoResponseDTO verSeguimientoPedido(Integer idPedido, Integer idCliente) {
        Pedidos pedido = pedidosRepository.findById(idPedido)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));

        if (!pedido.getCliente().getIdCliente().equals(idCliente)) {
            throw new RuntimeException("Este pedido no te pertenece");
        }

        return mapPedidoToDTO(pedido);
    }

    // ==================== CLIENTE: VALORAR PRODUCTO ====================

    @Transactional
    public ValoracionResponseDTO valorarProducto(ValoracionDTO dto, Integer idCliente) {
        ProductosTienda producto = productosTiendaRepository.findById(dto.getIdProductoTienda())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        Cliente cliente = clienteRepository.findById(idCliente)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

        // ✨ VERIFICAR QUE EL CLIENTE HAYA COMPRADO ESTE PRODUCTO
        boolean haComprado = verificarCompraProducto(idCliente, dto.getIdProductoTienda());
        if (!haComprado) {
            throw new RuntimeException("Solo puedes valorar productos que hayas comprado");
        }

        if (valoracionesRepository.existsByClienteAndProducto(idCliente, dto.getIdProductoTienda())) {
            throw new RuntimeException("Ya has valorado este producto");
        }

        if (dto.getCalificacion() < 1 || dto.getCalificacion() > 5) {
            throw new RuntimeException("La calificación debe ser entre 1 y 5 estrellas");
        }

        Valoraciones valoracion = new Valoraciones();
        valoracion.setProductoTienda(producto);
        valoracion.setCliente(cliente);
        valoracion.setCalificacion(dto.getCalificacion());
        valoracion.setComentario(dto.getComentario());

        Valoraciones valoracionGuardada = valoracionesRepository.save(valoracion);

        return mapValoracionToDTO(valoracionGuardada);
    }

    // ==================== VER VALORACIONES DE UN PRODUCTO ====================

    public List<ValoracionResponseDTO> verValoracionesProducto(Integer idProducto) {
        List<Valoraciones> valoraciones = valoracionesRepository.findByProductoId(idProducto);
        return valoraciones.stream().map(this::mapValoracionToDTO).collect(Collectors.toList());
    }

    // ==================== PROMOCIONES: LISTAR VIGENTES ====================

    public List<PromocionDTO> listarPromocionesVigentes() {
        List<Promociones> promociones = promocionesRepository.findPromocionesVigentes(LocalDateTime.now());
        return promociones.stream().map(this::mapPromocionToDTO).collect(Collectors.toList());
    }

    // ==================== HELPERS - PROMOCIONES ====================

    private BigDecimal calcularPrecioConPromocion(ProductosTienda producto) {
        BigDecimal precioOriginal = producto.getPrecioVentaTienda();
        LocalDateTime ahora = LocalDateTime.now();

        // 1. Buscar promoción específica del producto
        Promociones promocionProducto = promocionesRepository
                .findPromocionVigenteParaProducto(producto.getIdProductoTienda(), ahora)
                .orElse(null);

        if (promocionProducto != null) {
            return aplicarDescuento(precioOriginal, promocionProducto);
        }

        // 2. Buscar promoción de la categoría
        List<Promociones> promocionesCategoria = promocionesRepository
                .findPromocionesVigentesParaCategoria(
                        producto.getArticulo().getTipoArticulo().getIdTipoArticulo(),
                        ahora
                );

        if (!promocionesCategoria.isEmpty()) {
            return aplicarDescuento(precioOriginal, promocionesCategoria.get(0));
        }

        // 3. Buscar promoción general
        List<Promociones> promocionesGenerales = promocionesRepository
                .findPromocionesGeneralesVigentes(ahora);

        if (!promocionesGenerales.isEmpty()) {
            return aplicarDescuento(precioOriginal, promocionesGenerales.get(0));
        }

        return precioOriginal;
    }

    private BigDecimal aplicarDescuento(BigDecimal precioOriginal, Promociones promocion) {
        if ("PORCENTAJE".equals(promocion.getTipoDescuento())) {
            BigDecimal descuento = precioOriginal
                    .multiply(promocion.getValorDescuento())
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            return precioOriginal.subtract(descuento);
        } else { // MONTO_FIJO
            BigDecimal precioFinal = precioOriginal.subtract(promocion.getValorDescuento());
            return precioFinal.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : precioFinal;
        }
    }

    private Promociones obtenerMejorPromocion(ProductosTienda producto) {
        LocalDateTime ahora = LocalDateTime.now();

        // 1. Promoción específica
        Promociones promocionProducto = promocionesRepository
                .findPromocionVigenteParaProducto(producto.getIdProductoTienda(), ahora)
                .orElse(null);

        if (promocionProducto != null) {
            return promocionProducto;
        }

        // 2. Promoción de categoría
        List<Promociones> promocionesCategoria = promocionesRepository
                .findPromocionesVigentesParaCategoria(
                        producto.getArticulo().getTipoArticulo().getIdTipoArticulo(),
                        ahora
                );

        if (!promocionesCategoria.isEmpty()) {
            return promocionesCategoria.get(0);
        }

        // 3. Promoción general
        List<Promociones> promocionesGenerales = promocionesRepository
                .findPromocionesGeneralesVigentes(ahora);

        if (!promocionesGenerales.isEmpty()) {
            return promocionesGenerales.get(0);
        }

        return null;
    }

    // ==================== HELPERS ====================

    private boolean verificarCompraProducto(Integer idCliente, Integer idProducto) {
        List<Pedidos> pedidos = pedidosRepository.findByClienteId(idCliente);

        for (Pedidos pedido : pedidos) {
            if (pedido.getIdEstado() >= 20) {
                List<DetallePedido> detalles = detallePedidoRepository.findByPedidoId(pedido.getIdPedido());
                for (DetallePedido detalle : detalles) {
                    if (detalle.getProductoTienda().getIdProductoTienda().equals(idProducto)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private SeguimientoPedidoDTO crearSeguimiento(Pedidos pedido) {
        SeguimientoPedidoDTO seguimiento = new SeguimientoPedidoDTO();

        int estado = pedido.getIdEstado();

        seguimiento.setPedidoPagado(estado >= 20);
        seguimiento.setEnPreparacion(estado == 20);
        seguimiento.setEnCamino(estado == 21);
        seguimiento.setEntregado(estado == 22);
        seguimiento.setCancelado(estado == 23);

        seguimiento.setFechaEstimadaEntrega(pedido.getFechaEstimadaEntrega());

        if (pedido.getFechaEstimadaEntrega() != null && estado != 22 && estado != 23) {
            long dias = ChronoUnit.DAYS.between(LocalDate.now(), pedido.getFechaEstimadaEntrega());
            seguimiento.setDiasRestantes((int) dias);
        }

        switch (estado) {
            case 19:
                seguimiento.setMensajeEstado("Pedido pendiente de pago");
                break;
            case 20:
                seguimiento.setMensajeEstado("Tu pedido está en preparación. Llegará en aproximadamente 15 días.");
                break;
            case 21:
                seguimiento.setMensajeEstado("Tu pedido está en camino");
                break;
            case 22:
                seguimiento.setMensajeEstado("Tu pedido ha sido entregado");
                break;
            case 23:
                seguimiento.setMensajeEstado("Pedido cancelado");
                break;
            default:
                seguimiento.setMensajeEstado("Estado desconocido");
        }

        return seguimiento;
    }

    // ==================== MAPPERS ====================

    private ProductoTiendaResponseDTO mapArticuloToProductoDTO(Articulos a) {
        ProductoTiendaResponseDTO dto = new ProductoTiendaResponseDTO();
        dto.setIdArticulo(a.getIdArticulo());
        dto.setNombreProducto(a.getNombreArticulo());
        dto.setDescripcion(a.getDescripcion());
        dto.setPrecioVentaTienda(a.getPrecioArticulo());
        dto.setTipoArticulo(a.getTipoArticulo().getNombreTipoArticulo());
        dto.setEstadoFisico(a.getEstadoArticulo());

        List<ImagenesArticulos> imagenes = imagenesArticulosRepository.findByArticuloId(a.getIdArticulo());
        dto.setImagenes(imagenes.stream().map(ImagenesArticulos::getUrlImagen).collect(Collectors.toList()));

        return dto;
    }

    private ProductoTiendaResponseDTO mapProductoToDTO(ProductosTienda p) {
        ProductoTiendaResponseDTO dto = new ProductoTiendaResponseDTO();
        dto.setIdProductoTienda(p.getIdProductoTienda());
        dto.setIdArticulo(p.getArticulo().getIdArticulo());

        dto.setNombreProducto(p.getNombreEditado() != null ? p.getNombreEditado() : p.getArticulo().getNombreArticulo());
        dto.setDescripcion(p.getDescripcionEditada() != null ? p.getDescripcionEditada() : p.getArticulo().getDescripcion());

        // ✨ PRECIOS CON PROMOCIÓN
        BigDecimal precioOriginal = p.getPrecioVentaTienda();
        BigDecimal precioConDescuento = calcularPrecioConPromocion(p);

        dto.setPrecioOriginal(precioOriginal);
        dto.setPrecioVentaTienda(precioConDescuento);
        dto.setPrecioConDescuento(precioConDescuento);

        // ✨ PROMOCIÓN ACTIVA
        Promociones promocionActiva = obtenerMejorPromocion(p);
        if (promocionActiva != null) {
            PromocionDTO promocionDTO = mapPromocionToDTO(promocionActiva);
            promocionDTO.setDescuentoAplicado(precioOriginal.subtract(precioConDescuento));
            dto.setPromocionActiva(promocionDTO);
        }

        dto.setTipoArticulo(p.getArticulo().getTipoArticulo().getNombreTipoArticulo());
        dto.setEstadoFisico(p.getArticulo().getEstadoArticulo());
        dto.setFechaPublicacion(p.getFechaPublicacion());

        List<ImagenesArticulos> imagenes = imagenesArticulosRepository.findByArticuloId(p.getArticulo().getIdArticulo());
        dto.setImagenes(imagenes.stream().map(ImagenesArticulos::getUrlImagen).collect(Collectors.toList()));

        Double promedio = valoracionesRepository.calcularPromedioCalificacion(p.getIdProductoTienda());
        dto.setCalificacionPromedio(promedio != null ? promedio : 0.0);

        Long total = valoracionesRepository.contarValoraciones(p.getIdProductoTienda());
        dto.setTotalValoraciones(total != null ? total : 0L);

        return dto;
    }

    private PedidoResponseDTO mapPedidoToDTO(Pedidos p) {
        PedidoResponseDTO dto = new PedidoResponseDTO();
        dto.setIdPedido(p.getIdPedido());
        dto.setIdCliente(p.getCliente().getIdCliente());
        dto.setNombreCliente(p.getCliente().getNombreCliente() + " " + p.getCliente().getApellidoCliente());
        dto.setIdEstado(p.getIdEstado());
        dto.setNombreEstado(obtenerNombreEstado(p.getIdEstado()));
        dto.setTotal(p.getTotal());
        dto.setFechaPedido(p.getFechaPedido());
        dto.setFechaPago(p.getFechaPago());
        dto.setFechaEstimadaEntrega(p.getFechaEstimadaEntrega());

        List<DetallePedido> detalles = detallePedidoRepository.findByPedidoId(p.getIdPedido());
        dto.setItems(detalles.stream().map(this::mapDetalleToDTO).collect(Collectors.toList()));

        dto.setSeguimiento(crearSeguimiento(p));

        return dto;
    }

    private DetallePedidoDTO mapDetalleToDTO(DetallePedido d) {
        DetallePedidoDTO dto = new DetallePedidoDTO();
        dto.setIdDetalle(d.getIdDetalle());
        dto.setIdProductoTienda(d.getProductoTienda().getIdProductoTienda());

        String nombre = d.getProductoTienda().getNombreEditado() != null
                ? d.getProductoTienda().getNombreEditado()
                : d.getProductoTienda().getArticulo().getNombreArticulo();
        dto.setNombreProducto(nombre);

        dto.setPrecioVenta(d.getPrecioVenta());
        dto.setCantidad(d.getCantidad());
        dto.setSubtotal(d.getPrecioVenta().multiply(BigDecimal.valueOf(d.getCantidad())));

        return dto;
    }

    private ValoracionResponseDTO mapValoracionToDTO(Valoraciones v) {
        ValoracionResponseDTO dto = new ValoracionResponseDTO();
        dto.setIdValoracion(v.getIdValoracion());
        dto.setIdProductoTienda(v.getProductoTienda().getIdProductoTienda());
        dto.setIdCliente(v.getCliente().getIdCliente());
        dto.setNombreCliente(v.getCliente().getNombreCliente() + " " + v.getCliente().getApellidoCliente());
        dto.setCalificacion(v.getCalificacion());
        dto.setComentario(v.getComentario());
        dto.setFechaValoracion(v.getFechaValoracion());

        return dto;
    }

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

    private String obtenerNombreEstado(Integer idEstado) {
        switch (idEstado) {
            case 19: return "Pendiente";
            case 20: return "En Preparación";
            case 21: return "En Camino";
            case 22: return "Entregado";
            case 23: return "Cancelado";
            default: return "Desconocido";
        }
    }
}