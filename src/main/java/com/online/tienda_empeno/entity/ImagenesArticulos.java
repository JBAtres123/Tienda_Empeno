package com.online.tienda_empeno.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "Imagenes_Articulos")
public class ImagenesArticulos {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_imagen")
    private Integer idImagen;

    @ManyToOne
    @JoinColumn(name = "id_Articulo", nullable = false)
    private Articulos articulo;

    @Column(name = "url_imagen", nullable = false, length = 200)
    private String urlImagen;

    // Getters y Setters
    public Integer getIdImagen() { return idImagen; }
    public void setIdImagen(Integer idImagen) { this.idImagen = idImagen; }

    public Articulos getArticulo() { return articulo; }
    public void setArticulo(Articulos articulo) { this.articulo = articulo; }

    public String getUrlImagen() { return urlImagen; }
    public void setUrlImagen(String urlImagen) { this.urlImagen = urlImagen; }
}