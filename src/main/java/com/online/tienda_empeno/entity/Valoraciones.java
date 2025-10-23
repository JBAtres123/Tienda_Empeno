package com.online.tienda_empeno.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "Valoraciones")
public class Valoraciones {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_valoracion")
    private Integer idValoracion;

    @ManyToOne
    @JoinColumn(name = "id_producto_tienda", nullable = false)
    private ProductosTienda productoTienda;

    @ManyToOne
    @JoinColumn(name = "id_cliente", nullable = false)
    private Cliente cliente;

    @Column(name = "calificacion", nullable = false)
    private Integer calificacion; // 1-5

    @Column(name = "comentario", columnDefinition = "TEXT")
    private String comentario;

    @Column(name = "fecha_valoracion", updatable = false)
    private LocalDateTime fechaValoracion;

    @PrePersist
    protected void onCreate() {
        fechaValoracion = LocalDateTime.now();
    }

    // Getters y Setters
    public Integer getIdValoracion() {
        return idValoracion;
    }

    public void setIdValoracion(Integer idValoracion) {
        this.idValoracion = idValoracion;
    }

    public ProductosTienda getProductoTienda() {
        return productoTienda;
    }

    public void setProductoTienda(ProductosTienda productoTienda) {
        this.productoTienda = productoTienda;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Integer getCalificacion() {
        return calificacion;
    }

    public void setCalificacion(Integer calificacion) {
        this.calificacion = calificacion;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public LocalDateTime getFechaValoracion() {
        return fechaValoracion;
    }

    public void setFechaValoracion(LocalDateTime fechaValoracion) {
        this.fechaValoracion = fechaValoracion;
    }
}