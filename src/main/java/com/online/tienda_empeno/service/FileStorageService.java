package com.online.tienda_empeno.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class FileStorageService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    // Tipos MIME permitidos para imágenes
    private static final List<String> TIPOS_PERMITIDOS = Arrays.asList(
        "image/jpeg",
        "image/jpg",
        "image/png",
        "image/webp",
        "image/gif"
    );

    // Extensiones permitidas
    private static final List<String> EXTENSIONES_PERMITIDAS = Arrays.asList(
        ".jpg", ".jpeg", ".png", ".webp", ".gif"
    );

    // Tamaño máximo: 10MB
    private static final long TAMANO_MAXIMO = 10 * 1024 * 1024;

    public String guardarImagen(MultipartFile archivo) throws IOException {
        // Validar que el archivo no esté vacío
        if (archivo.isEmpty()) {
            throw new IOException("El archivo está vacío");
        }

        // Validar tamaño
        if (archivo.getSize() > TAMANO_MAXIMO) {
            throw new IOException("El archivo excede el tamaño máximo permitido de 10MB");
        }

        // Validar tipo MIME
        String contentType = archivo.getContentType();
        if (contentType == null || !TIPOS_PERMITIDOS.contains(contentType.toLowerCase())) {
            throw new IOException("Tipo de archivo no permitido. Solo se aceptan imágenes (JPEG, PNG, WebP, GIF)");
        }

        // Validar extensión
        String nombreOriginal = archivo.getOriginalFilename();
        if (nombreOriginal == null || !nombreOriginal.contains(".")) {
            throw new IOException("El archivo no tiene una extensión válida");
        }

        String extension = nombreOriginal.substring(nombreOriginal.lastIndexOf(".")).toLowerCase();
        if (!EXTENSIONES_PERMITIDAS.contains(extension)) {
            throw new IOException("Extensión de archivo no permitida. Solo se aceptan: " + String.join(", ", EXTENSIONES_PERMITIDAS));
        }

        // Crear directorio si no existe
        Path directorioSubida = Paths.get(uploadDir);
        if (!Files.exists(directorioSubida)) {
            Files.createDirectories(directorioSubida);
        }

        // Generar nombre único para el archivo
        String nombreUnico = UUID.randomUUID().toString() + extension;

        // Guardar archivo
        Path rutaArchivo = directorioSubida.resolve(nombreUnico);
        Files.copy(archivo.getInputStream(), rutaArchivo, StandardCopyOption.REPLACE_EXISTING);

        // Retornar la ruta relativa para guardar en BD
        return "/uploads/imagenes/" + nombreUnico;
    }
}
