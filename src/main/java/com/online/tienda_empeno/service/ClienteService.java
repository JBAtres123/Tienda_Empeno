package com.online.tienda_empeno.service;

import com.online.tienda_empeno.dto.*;
import com.online.tienda_empeno.entity.*;
import com.online.tienda_empeno.repository.*;
import com.online.tienda_empeno.utils.JwtUtil;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class ClienteService {

    private final DireccionesRepository direccionesRepository;
    private final CiudadRepository ciudadRepository;
    private final ClienteRepository clienteRepository;
    private final AdministradorRepository administradorRepository;
    private final TipoDeDocumentoRepository tipoDeDocumentoRepository;
    private final ContraseñaRepository contraseñaRepository;
    private final TipoUsuarioRepository tipoUsuarioRepository;
    private final JwtUtil jwtUtil;

    public ClienteService(DireccionesRepository direccionesRepository,
                          CiudadRepository ciudadRepository,
                          ClienteRepository clienteRepository,
                          AdministradorRepository administradorRepository,
                          TipoDeDocumentoRepository tipoDeDocumentoRepository,
                          ContraseñaRepository contraseñaRepository,
                          TipoUsuarioRepository tipoUsuarioRepository,
                          JwtUtil jwtUtil) {
        this.direccionesRepository = direccionesRepository;
        this.ciudadRepository = ciudadRepository;
        this.clienteRepository = clienteRepository;
        this.administradorRepository = administradorRepository;
        this.tipoDeDocumentoRepository = tipoDeDocumentoRepository;
        this.contraseñaRepository = contraseñaRepository;
        this.tipoUsuarioRepository = tipoUsuarioRepository;
        this.jwtUtil = jwtUtil;
    }

    public DireccionResponseDTO registrarDireccion(DireccionDTO dto) {
        Ciudad ciudad = ciudadRepository.findById(dto.getIdCiudad())
                .orElseThrow(() -> new RuntimeException("Ciudad no encontrada con id " + dto.getIdCiudad()));

        Direcciones direccion = new Direcciones();
        direccion.setCiudad(ciudad);
        direccion.setDireccionCliente(dto.getDireccionCliente());
        direccion.setCodigoPostal(dto.getCodigoPostal());

        Direcciones saved = direccionesRepository.save(direccion);
        return mapDireccionToDto(saved);
    }

    public ClienteResponseDTO registrarCliente(ClienteRegistroDTO dto) {
        TipoDeDocumento tipoDoc = tipoDeDocumentoRepository.findById(dto.getIdTipoDocumento())
                .orElseThrow(() -> new RuntimeException("Tipo de documento no encontrado con id " + dto.getIdTipoDocumento()));

        TipoUsuario tipoUsuario = tipoUsuarioRepository.findById(1)
                .orElseThrow(() -> new RuntimeException("Tipo usuario Cliente no encontrado"));

        Contraseña contraseña = new Contraseña();
        contraseña.setTipoUsuario(tipoUsuario);
        contraseña.setContraseña(dto.getContraseña());
        contraseña.setFechaCreacion(new Date());
        contraseña = contraseñaRepository.save(contraseña);

        Direcciones direccion = null;
        if (dto.getIdDireccion() != null) {
            direccion = direccionesRepository.findById(dto.getIdDireccion())
                    .orElseThrow(() -> new RuntimeException("Dirección no encontrada con id " + dto.getIdDireccion()));
        }

        Cliente cliente = new Cliente();
        cliente.setDireccion(direccion);
        cliente.setTipoDocumento(tipoDoc);
        cliente.setContraseña(contraseña);
        cliente.setTipoUsuario(tipoUsuario);
        cliente.setNumeroDocumento(dto.getNumeroDocumento());
        cliente.setNombreCliente(dto.getNombreCliente());
        cliente.setApellidoCliente(dto.getApellidoCliente());
        cliente.setEmailCliente(dto.getEmailCliente());

        Cliente saved = clienteRepository.save(cliente);
        return mapClienteToDto(saved);
    }

    public LoginResponseDTO loginCliente(LoginRequestDTO dto) {
        Cliente cliente = clienteRepository.findByEmailCliente(dto.getEmailCliente());
        if (cliente != null) {
            if (cliente.getContraseña().getContraseña().equals(dto.getContraseña())) {
                String token = jwtUtil.generateToken(
                        cliente.getIdCliente(),
                        cliente.getEmailCliente(),
                        "Cliente"
                );

                return new LoginResponseDTO(
                        true,
                        "Login exitoso como Cliente",
                        cliente.getIdCliente(),
                        token,
                        "Cliente"
                );
            } else {
                return new LoginResponseDTO(false, "Contraseña incorrecta", null);
            }
        }

        Administradores admin = administradorRepository.findByEmailAndPassword(dto.getEmailCliente(), dto.getContraseña());
        if (admin != null) {
            String token = jwtUtil.generateToken(
                    admin.getIdAdmin(),
                    admin.getEmailAdmin(),  // ← CORREGIDO: getEmailAdmin()
                    "Administrador"
            );

            return new LoginResponseDTO(
                    true,
                    "Login exitoso como Administrador",
                    admin.getIdAdmin(),
                    token,
                    "Administrador"
            );
        }

        return new LoginResponseDTO(false, "Usuario no encontrado", null);
    }

    private DireccionResponseDTO mapDireccionToDto(Direcciones d) {
        DireccionResponseDTO dto = new DireccionResponseDTO();
        dto.setIdDireccion(d.getIdDireccion());
        dto.setDireccionCliente(d.getDireccionCliente());
        dto.setCodigoPostal(d.getCodigoPostal());

        Ciudad c = d.getCiudad();
        if (c != null) {
            CiudadSimpleDTO cDto = new CiudadSimpleDTO();
            cDto.setIdCiudad(c.getIdCiudad());
            cDto.setNombreCiudad(c.getNombreCiudad());
            dto.setCiudad(cDto);
        }
        return dto;
    }

    private ClienteResponseDTO mapClienteToDto(Cliente c) {
        ClienteResponseDTO dto = new ClienteResponseDTO();
        dto.setIdCliente(c.getIdCliente());
        dto.setNumeroDocumento(c.getNumeroDocumento());
        dto.setNombreCliente(c.getNombreCliente());
        dto.setApellidoCliente(c.getApellidoCliente());
        dto.setEmailCliente(c.getEmailCliente());

        if (c.getDireccion() != null) {
            dto.setDireccion(mapDireccionToDto(c.getDireccion()));
        }

        return dto;
    }
}