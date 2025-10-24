package com.online.tienda_empeno.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "Promociones")
public class Promociones {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_promocion")
    private Integer idPromocion;

    @Column(name = "nombre_promocion", nullable = false)
    private String nombrePromocion;

    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "tipo_promocion", nullable = false)
    private String tipoPromocion; // CATEGORIA, PRODUCTO, GENERAL

    @Column(name = "tipo_descuento", nullable = false)
    private String tipoDescuento; // PORCENTAJE, MONTO_FIJO

    @Column(name = "valor_descuento", precision = 10, scale = 2, nullable = false)
    private BigDecimal valorDescuento;

    @ManyToOne
    @JoinColumn(name = "id_tipo_articulo")
    private TiposArticulos tipoArticulo;

    @ManyToOne
    @JoinColumn(name = "id_producto_tienda")
    private ProductosTienda productoTienda;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDateTime fechaInicio;

    @Column(name = "fecha_fin", nullable = false)
    private LocalDateTime fechaFin;

    @Column(name = "activo")
    private Boolean activo;

    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime fechaCreacion;

    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
        if (activo == null) {
            activo = true;
        }
    }

    // Método para verificar si la promoción está vigente
    public boolean estaVigente() {
        LocalDateTime ahora = LocalDateTime.now();
        return activo &&
                ahora.isAfter(fechaInicio) &&
                ahora.isBefore(fechaFin);
    }

    // Getters y Setters
    public Integer getIdPromocion() {
        return idPromocion;
    }

    public void setIdPromocion(Integer idPromocion) {
        this.idPromocion = idPromocion;
    }

    public String getNombrePromocion() {
        return nombrePromocion;
    }

    public void setNombrePromocion(String nombrePromocion) {
        this.nombrePromocion = nombrePromocion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getTipoPromocion() {
        return tipoPromocion;
    }

    public void setTipoPromocion(String tipoPromocion) {
        this.tipoPromocion = tipoPromocion;
    }

    public String getTipoDescuento() {
        return tipoDescuento;
    }

    public void setTipoDescuento(String tipoDescuento) {
        this.tipoDescuento = tipoDescuento;
    }

    public BigDecimal getValorDescuento() {
        return valorDescuento;
    }

    public void setValorDescuento(BigDecimal valorDescuento) {
        this.valorDescuento = valorDescuento;
    }

    public TiposArticulos getTipoArticulo() {
        return tipoArticulo;
    }

    public void setTipoArticulo(TiposArticulos tipoArticulo) {
        this.tipoArticulo = tipoArticulo;
    }

    public ProductosTienda getProductoTienda() {
        return productoTienda;
    }

    public void setProductoTienda(ProductosTienda productoTienda) {
        this.productoTienda = productoTienda;
    }

    public LocalDateTime getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDateTime fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDateTime getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDateTime fechaFin) {
        this.fechaFin = fechaFin;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
}