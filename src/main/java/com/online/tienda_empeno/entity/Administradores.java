package com.online.tienda_empeno.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "Administradores")
public class Administradores {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_admin")
    private Integer idAdmin;

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

    // getters y setters
    public Integer getIdAdmin() { return idAdmin; }
    public void setIdAdmin(Integer idAdmin) { this.idAdmin = idAdmin; }

    public Integer getIdContraseña() { return idContraseña; }
    public void setIdContraseña(Integer idContraseña) { this.idContraseña = idContraseña; }

    public String getNombreAdmin() { return nombreAdmin; }
    public void setNombreAdmin(String nombreAdmin) { this.nombreAdmin = nombreAdmin; }

    public String getApellidoAdmin() { return apellidoAdmin; }
    public void setApellidoAdmin(String apellidoAdmin) { this.apellidoAdmin = apellidoAdmin; }

    public String getEmailAdmin() { return emailAdmin; }
    public void setEmailAdmin(String emailAdmin) { this.emailAdmin = emailAdmin; }
}

