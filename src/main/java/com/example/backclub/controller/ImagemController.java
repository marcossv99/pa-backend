package com.example.backclub.controller;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/imagens/perfil")
public class ImagemController {
    private final Path diretorioImagens = Paths.get("frontend/public/home-socio");

    @GetMapping("/{nomeImagem}")
    public ResponseEntity<Resource> servirImagem(@PathVariable String nomeImagem) {
        try {
            Path arquivo = diretorioImagens.resolve(nomeImagem).normalize();
            if (!Files.exists(arquivo)) {
                return ResponseEntity.notFound().build();
            }
            Resource resource = new UrlResource(arquivo.toUri());
            String contentType;
            try {
                contentType = Files.probeContentType(arquivo);
            } catch (Exception ex) {
                contentType = null;
            }
            if (contentType == null) {
                contentType = "application/octet-stream";
            }
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
        } catch (MalformedURLException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
