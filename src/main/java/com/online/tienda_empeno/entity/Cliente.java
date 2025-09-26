package com.online.tienda_empeno.entity;
import jakarta.persistence.*;

@Entity
@Table(name = "Cliente")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cliente")
    private Integer idCliente;

    @ManyToOne
    @JoinColumn(name = "id_direccion")
    private Direcciones direccion;

    @ManyToOne
    @JoinColumn(name = "id_tipo_documento", nullable = false)
    private TipoDeDocumento tipoDocumento;

    @ManyToOne
    @JoinColumn(name = "id_contraseña_cliente", nullable = false)
    private Contraseña contraseña;

    @ManyToOne
    @JoinColumn(name = "id_tipo_usuario", nullable = false)
    private TipoUsuario tipoUsuario;

    @Column(name = "numero_documento", nullable = false, length = 20)
    private String numeroDocumento;

    @Column(name = "nombre_cliente", nullable = false, length = 30)
    private String nombreCliente;

    @Column(name = "apellido_cliente", nullable = false, length = 30)
    private String apellidoCliente;

    @Column(name = "email_cliente", nullable = false, length = 30)
    private String emailCliente;

    // Getters y Setters
    public Integer getIdCliente() { return idCliente; }
    public void setIdCliente(Integer idCliente) { this.idCliente = idCliente; }

    public Direcciones getDireccion() { return direccion; }
    public void setDireccion(Direcciones direccion) { this.direccion = direccion; }

    public TipoDeDocumento getTipoDocumento() { return tipoDocumento; }
    public void setTipoDocumento(TipoDeDocumento tipoDocumento) { this.tipoDocumento = tipoDocumento; }

    public Contraseña getContraseña() { return contraseña; }
    public void setContraseña(Contraseña contraseña) { this.contraseña = contraseña; }

    public TipoUsuario getTipoUsuario() { return tipoUsuario; }
    public void setTipoUsuario(TipoUsuario tipoUsuario) { this.tipoUsuario = tipoUsuario; }

    public String getNumeroDocumento() { return numeroDocumento; }
    public void setNumeroDocumento(String numeroDocumento) { this.numeroDocumento = numeroDocumento; }

    public String getNombreCliente() { return nombreCliente; }
    public void setNombreCliente(String nombreCliente) { this.nombreCliente = nombreCliente; }

    public String getApellidoCliente() { return apellidoCliente; }
    public void setApellidoCliente(String apellidoCliente) { this.apellidoCliente = apellidoCliente; }

    public String getEmailCliente() { return emailCliente; }
    public void setEmailCliente(String emailCliente) { this.emailCliente = emailCliente; }
}



