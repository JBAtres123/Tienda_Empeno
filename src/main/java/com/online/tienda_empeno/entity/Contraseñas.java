
package com.online.tienda_empeno.entity;

import jakarta.persistence.*;
        import java.util.Date;

@Entity
@Table(name = "Contraseñas")
public class Contraseñas {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_contraseña")
    private Integer idContraseña;

    @Column(name = "id_tipo_usuario")
    private Integer idTipoUsuario;

    @Column(name = "contraseña")
    private String contraseña;

    @Column(name = "fecha_creacion")
    private Date fechaCreacion;

    // getters y setters
    public Integer getIdContraseña() { return idContraseña; }
    public void setIdContraseña(Integer idContraseña) { this.idContraseña = idContraseña; }

    public String getContraseña() { return contraseña; }
    public void setContraseña(String contraseña) { this.contraseña = contraseña; }
}
