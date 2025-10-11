package com.online.tienda_empeno.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    public String guardarImagen(MultipartFile archivo) throws IOException {
        // Crear directorio si no existe
        Path directorioSubida = Paths.get(uploadDir);
        if (!Files.exists(directorioSubida)) {
            Files.createDirectories(directorioSubida);
        }

        // Generar nombre único para el archivo
        String nombreOriginal = archivo.getOriginalFilename();
        String extension = nombreOriginal.substring(nombreOriginal.lastIndexOf("."));
        String nombreUnico = UUID.randomUUID().toString() + extension;

        // Guardar archivo
        Path rutaArchivo = directorioSubida.resolve(nombreUnico);
        Files.copy(archivo.getInputStream(), rutaArchivo, StandardCopyOption.REPLACE_EXISTING);

        // Retornar la ruta relativa para guardar en BD
        return "/uploads/imagenes/" + nombreUnico;
    }
}
