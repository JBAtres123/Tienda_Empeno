package com.online.tienda_empeno.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "Administradores")
public class Administradores {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_admin")
    private Integer idAdmin;

    @Column(name = "id_rol", nullable = false)
    private Integer idRol; // ← AGREGAR ESTE CAMPO

    @Column(name = "id_contraseña")
    private Integer idContraseña;

    @Column(name = "id_tipo_usuario")
    private Integer idTipoUsuario;

    @Column(name = "nombre_admin")
    private String nombreAdmin;

    @Column(name = "apellido_admin")
    private String apellidoAdmin;

    @Column(name = "email_admin")
    private String emailAdmin;

    // Getters y Setters
    public Integer getIdAdmin() { return idAdmin; }
    public void setIdAdmin(Integer idAdmin) { this.idAdmin = idAdmin; }

    public Integer getIdRol() { return idRol; }
    public void setIdRol(Integer idRol) { this.idRol = idRol; }

    public Integer getIdContraseña() { return idContraseña; }
    public void setIdContraseña(Integer idContraseña) { this.idContraseña = idContraseña; }

    public Integer getIdTipoUsuario() { return idTipoUsuario; }
    public void setIdTipoUsuario(Integer idTipoUsuario) { this.idTipoUsuario = idTipoUsuario; }

    public String getNombreAdmin() { return nombreAdmin; }
    public void setNombreAdmin(String nombreAdmin) { this.nombreAdmin = nombreAdmin; }

    public String getApellidoAdmin() { return apellidoAdmin; }
    public void setApellidoAdmin(String apellidoAdmin) { this.apellidoAdmin = apellidoAdmin; }

    public String getEmailAdmin() { return emailAdmin; }
    public void setEmailAdmin(String emailAdmin) { this.emailAdmin = emailAdmin; }
}