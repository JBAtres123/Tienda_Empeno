package com.online.tienda_empeno.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class ProductoTiendaResponseDTO {
    private Integer idProductoTienda;
    private Integer idArticulo;
    private String nombreProducto; // nombre_editado o nombre_articulo
    private String descripcion; // descripcion_editada o descripcion original
    private BigDecimal precioVentaTienda;
    private BigDecimal precioCompra; // Precio con el que se compró al cliente
    private String tipoArticulo;
    private String estadoFisico;
    private List<String> imagenes;
    private LocalDateTime fechaPublicacion;

    // Valoraciones
    private Double calificacionPromedio;
    private Long totalValoraciones;
    // Agregar estos campos al DTO existente:
    private BigDecimal precioOriginal; // Precio sin descuento
    private BigDecimal precioConDescuento; // Precio con descuento aplicado
    private PromocionDTO promocionActiva; // Promoción aplicada

    // Getters y Setters
    public BigDecimal getPrecioOriginal() {
        return precioOriginal;
    }

    public void setPrecioOriginal(BigDecimal precioOriginal) {
        this.precioOriginal = precioOriginal;
    }

    public BigDecimal getPrecioConDescuento() {
        return precioConDescuento;
    }

    public void setPrecioConDescuento(BigDecimal precioConDescuento) {
        this.precioConDescuento = precioConDescuento;
    }

    public PromocionDTO getPromocionActiva() {
        return promocionActiva;
    }

    public void setPromocionActiva(PromocionDTO promocionActiva) {
        this.promocionActiva = promocionActiva;
    }

    // Getters y Setters
    public Integer getIdProductoTienda() {
        return idProductoTienda;
    }

    public void setIdProductoTienda(Integer idProductoTienda) {
        this.idProductoTienda = idProductoTienda;
    }

    public Integer getIdArticulo() {
        return idArticulo;
    }

    public void setIdArticulo(Integer idArticulo) {
        this.idArticulo = idArticulo;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public BigDecimal getPrecioVentaTienda() {
        return precioVentaTienda;
    }

    public void setPrecioVentaTienda(BigDecimal precioVentaTienda) {
        this.precioVentaTienda = precioVentaTienda;
    }

    public String getTipoArticulo() {
        return tipoArticulo;
    }

    public void setTipoArticulo(String tipoArticulo) {
        this.tipoArticulo = tipoArticulo;
    }

    public String getEstadoFisico() {
        return estadoFisico;
    }

    public void setEstadoFisico(String estadoFisico) {
        this.estadoFisico = estadoFisico;
    }

    public List<String> getImagenes() {
        return imagenes;
    }

    public void setImagenes(List<String> imagenes) {
        this.imagenes = imagenes;
    }

    public LocalDateTime getFechaPublicacion() {
        return fechaPublicacion;
    }

    public void setFechaPublicacion(LocalDateTime fechaPublicacion) {
        this.fechaPublicacion = fechaPublicacion;
    }

    public Double getCalificacionPromedio() {
        return calificacionPromedio;
    }

    public void setCalificacionPromedio(Double calificacionPromedio) {
        this.calificacionPromedio = calificacionPromedio;
    }

    public Long getTotalValoraciones() {
        return totalValoraciones;
    }

    public void setTotalValoraciones(Long totalValoraciones) {
        this.totalValoraciones = totalValoraciones;
    }

    public BigDecimal getPrecioCompra() {
        return precioCompra;
    }

    public void setPrecioCompra(BigDecimal precioCompra) {
        this.precioCompra = precioCompra;
    }
}