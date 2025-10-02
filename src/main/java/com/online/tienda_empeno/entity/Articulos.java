package com.online.tienda_empeno.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "Articulos")
public class Articulos {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_articulo")
    private Integer idArticulo;

    @ManyToOne
    @JoinColumn(name = "id_cliente", nullable = false)
    private Cliente cliente;

    @Column(name = "id_estado", nullable = false)
    private Integer idEstado; // 1 = Solicitado por defecto

    @ManyToOne
    @JoinColumn(name = "id_tipo_articulo", nullable = false)
    private TiposArticulos tipoArticulo;

    @Column(name = "estado_articulo", nullable = false, length = 20)
    private String estadoArticulo; // Del 1 al 10 (estado físico del artículo)

    @Column(name = "nombre_articulo", nullable = false, length = 60)
    private String nombreArticulo;

    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "precio_articulo", nullable = false, precision = 12, scale = 2)
    private BigDecimal precioArticulo;

    // Getters y Setters
    public Integer getIdArticulo() { return idArticulo; }
    public void setIdArticulo(Integer idArticulo) { this.idArticulo = idArticulo; }

    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }

    public Integer getIdEstado() { return idEstado; }
    public void setIdEstado(Integer idEstado) { this.idEstado = idEstado; }

    public TiposArticulos getTipoArticulo() { return tipoArticulo; }
    public void setTipoArticulo(TiposArticulos tipoArticulo) { this.tipoArticulo = tipoArticulo; }

    public String getEstadoArticulo() { return estadoArticulo; }
    public void setEstadoArticulo(String estadoArticulo) { this.estadoArticulo = estadoArticulo; }

    public String getNombreArticulo() { return nombreArticulo; }
    public void setNombreArticulo(String nombreArticulo) { this.nombreArticulo = nombreArticulo; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public BigDecimal getPrecioArticulo() { return precioArticulo; }
    public void setPrecioArticulo(BigDecimal precioArticulo) { this.precioArticulo = precioArticulo; }
}
