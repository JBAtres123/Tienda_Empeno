package com.online.tienda_empeno.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "Cobranza")
public class Cobranza {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cobranza")
    private Integer idCobranza;

    @ManyToOne
    @JoinColumn(name = "id_pago_prestamo", nullable = false)
    private PagosPrestamo pagoPrestamo;

    @Column(name = "comentario", columnDefinition = "TEXT")
    private String comentario;

    @Column(name = "fecha_de_Visita", nullable = false)
    private LocalDate fechaDeVisita;

    // Getters y Setters
    public Integer getIdCobranza() { return idCobranza; }
    public void setIdCobranza(Integer idCobranza) { this.idCobranza = idCobranza; }

    public PagosPrestamo getPagoPrestamo() { return pagoPrestamo; }
    public void setPagoPrestamo(PagosPrestamo pagoPrestamo) { this.pagoPrestamo = pagoPrestamo; }

    public String getComentario() { return comentario; }
    public void setComentario(String comentario) { this.comentario = comentario; }

    public LocalDate getFechaDeVisita() { return fechaDeVisita; }
    public void setFechaDeVisita(LocalDate fechaDeVisita) { this.fechaDeVisita = fechaDeVisita; }
}
