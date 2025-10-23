package com.online.tienda_empeno.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "Productos_Tienda")
public class ProductosTienda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_producto_tienda")
    private Integer idProductoTienda;

    @OneToOne
    @JoinColumn(name = "id_articulo", nullable = false, unique = true)
    private Articulos articulo;

    @Column(name = "precio_venta_tienda", precision = 12, scale = 2, nullable = false)
    private BigDecimal precioVentaTienda;

    @Column(name = "nombre_editado")
    private String nombreEditado;

    @Column(name = "descripcion_editada", columnDefinition = "TEXT")
    private String descripcionEditada;

    @ManyToOne
    @JoinColumn(name = "id_admin", nullable = false)
    private Administradores administrador;

    @Column(name = "fecha_publicacion", updatable = false)
    private LocalDateTime fechaPublicacion;

    @PrePersist
    protected void onCreate() {
        fechaPublicacion = LocalDateTime.now();
    }

    // Getters y Setters
    public Integer getIdProductoTienda() {
        return idProductoTienda;
    }

    public void setIdProductoTienda(Integer idProductoTienda) {
        this.idProductoTienda = idProductoTienda;
    }

    public Articulos getArticulo() {
        return articulo;
    }

    public void setArticulo(Articulos articulo) {
        this.articulo = articulo;
    }

    public BigDecimal getPrecioVentaTienda() {
        return precioVentaTienda;
    }

    public void setPrecioVentaTienda(BigDecimal precioVentaTienda) {
        this.precioVentaTienda = precioVentaTienda;
    }

    public String getNombreEditado() {
        return nombreEditado;
    }

    public void setNombreEditado(String nombreEditado) {
        this.nombreEditado = nombreEditado;
    }

    public String getDescripcionEditada() {
        return descripcionEditada;
    }

    public void setDescripcionEditada(String descripcionEditada) {
        this.descripcionEditada = descripcionEditada;
    }

    public Administradores getAdministrador() {
        return administrador;
    }

    public void setAdministrador(Administradores administrador) {
        this.administrador = administrador;
    }

    public LocalDateTime getFechaPublicacion() {
        return fechaPublicacion;
    }

    public void setFechaPublicacion(LocalDateTime fechaPublicacion) {
        this.fechaPublicacion = fechaPublicacion;
    }
}