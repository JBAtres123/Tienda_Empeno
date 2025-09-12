package com.online.tienda_empeno.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "Cliente")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cliente")
    private Integer idCliente;

    @Column(name = "id_contraseña")
    private Integer idContraseña;

    @Column(name = "id_tipo_usuario")
    private Integer idTipoUsuario;

    @Column(name = "numero_documento")
    private String numeroDocumento;

    @Column(name = "Email_cliente")
    private String emailCliente;

    @Column(name = "nombre_cliente")
    private String nombreCliente;

    @Column(name = "apellido_Cliente")
    private String apellidoCliente;

    // getters y setters
    public Integer getIdCliente() { return idCliente; }
    public void setIdCliente(Integer idCliente) { this.idCliente = idCliente; }

    public Integer getIdContraseña() {
        return idContraseña;
    }
    public void setIdContraseña(Integer idContraseña) {
        this.idContraseña = idContraseña;
    }

    public String getNombreCliente() { return nombreCliente; }
    public void setNombreCliente(String nombreCliente) { this.nombreCliente = nombreCliente; }

    public String getApellidoCliente() { return apellidoCliente; }
    public void setApellidoCliente(String apellidoCliente) { this.apellidoCliente = apellidoCliente; }

    public String getEmailCliente() { return emailCliente; }
    public void setEmailCliente(String emailCliente) { this.emailCliente = emailCliente; }
}

