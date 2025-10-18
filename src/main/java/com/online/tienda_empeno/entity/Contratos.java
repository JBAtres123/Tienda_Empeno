package com.online.tienda_empeno.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "Contratos")
public class Contratos {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_contrato")
    private Integer idContrato;

    @ManyToOne
    @JoinColumn(name = "id_prestamo", nullable = false)
    private Prestamos prestamo;

    @Column(name = "id_estado", nullable = false) // ← CAMBIAR: ahora es INT
    private Integer idEstado;

    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_firma")
    private LocalDateTime fechaFirma;

    @Column(name = "firma_cliente", length = 255)
    private String firmaCliente;

    @Column(name = "Documento_contrato", length = 255)
    private String documentoContrato;

    // Getters y Setters
    public Integer getIdContrato() { return idContrato; }
    public void setIdContrato(Integer idContrato) { this.idContrato = idContrato; }

    public Prestamos getPrestamo() { return prestamo; }
    public void setPrestamo(Prestamos prestamo) { this.prestamo = prestamo; }

    public Integer getIdEstado() { return idEstado; } // ← CAMBIAR
    public void setIdEstado(Integer idEstado) { this.idEstado = idEstado; } // ← CAMBIAR

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public LocalDateTime getFechaFirma() { return fechaFirma; }
    public void setFechaFirma(LocalDateTime fechaFirma) { this.fechaFirma = fechaFirma; }

    public String getFirmaCliente() { return firmaCliente; }
    public void setFirmaCliente(String firmaCliente) { this.firmaCliente = firmaCliente; }

    public String getDocumentoContrato() { return documentoContrato; }
    public void setDocumentoContrato(String documentoContrato) { this.documentoContrato = documentoContrato; }

    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
        if (idEstado == null) {
            idEstado = 10; // 10 = Pendiente (según tu tabla Estados)
        }
    }
}