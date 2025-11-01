package com.zelda.codex.controllers;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/cache")
@Tag(name = "Cache Management", description = "Operaciones para gestión del cache Redis")
public class CacheController {

    private static final Logger logger = LoggerFactory.getLogger(CacheController.class);

    @Autowired
    private CacheManager cacheManager;

    @Operation(summary = "Limpiar cache completo", description = "Elimina todas las entradas del cache Redis")
    @DeleteMapping("/clear")
    public ResponseEntity<Map<String, Object>> clearAllCache() {
        logger.info("Limpiando todo el cache Redis");
        
        Map<String, Object> response = new HashMap<>();
        int clearedCaches = 0;
        
        for (String cacheName : cacheManager.getCacheNames()) {
            var cache = cacheManager.getCache(cacheName);
            if (cache != null) {
                cache.clear();
                clearedCaches++;
                logger.debug("Cache '{}' limpiado", cacheName);
            }
        }
        
        response.put("message", "Cache limpiado exitosamente");
        response.put("clearedCaches", clearedCaches);
        response.put("cacheNames", cacheManager.getCacheNames());
        
        logger.info("Cache Redis limpiado completamente - {} caches afectados", clearedCaches);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Limpiar cache específico", description = "Elimina todas las entradas de un cache específico")
    @DeleteMapping("/clear/{cacheName}")
    public ResponseEntity<Map<String, Object>> clearSpecificCache(@PathVariable String cacheName) {
        logger.info("Limpiando cache específico: {}", cacheName);
        
        Map<String, Object> response = new HashMap<>();
        var cache = cacheManager.getCache(cacheName);
        
        if (cache != null) {
            cache.clear();
            response.put("message", "Cache '" + cacheName + "' limpiado exitosamente");
            response.put("cacheName", cacheName);
            logger.info("Cache '{}' limpiado exitosamente", cacheName);
            return ResponseEntity.ok(response);
        } else {
            response.put("error", "Cache '" + cacheName + "' no encontrado");
            response.put("availableCaches", cacheManager.getCacheNames());
            logger.warn("Intento de limpiar cache inexistente: {}", cacheName);
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Estado del cache", description = "Obtiene información sobre el estado de los caches")
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getCacheStatus() {
        logger.debug("Obteniendo estado del cache Redis");
        
        Map<String, Object> response = new HashMap<>();
        response.put("cacheManager", cacheManager.getClass().getSimpleName());
        response.put("availableCaches", cacheManager.getCacheNames());
        response.put("totalCaches", cacheManager.getCacheNames().size());
        
        Map<String, Object> cacheDetails = new HashMap<>();
        for (String cacheName : cacheManager.getCacheNames()) {
            var cache = cacheManager.getCache(cacheName);
            if (cache != null) {
                Map<String, Object> cacheInfo = new HashMap<>();
                cacheInfo.put("name", cacheName);
                cacheInfo.put("nativeCache", cache.getNativeCache().getClass().getSimpleName());
                cacheDetails.put(cacheName, cacheInfo);
            }
        }
        response.put("cacheDetails", cacheDetails);
        
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Estadísticas de cache", description = "Obtiene métricas básicas de uso del cache")
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getCacheStats() {
        logger.debug("Obteniendo estadísticas del cache Redis");
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Estadísticas de cache disponibles");
        response.put("note", "Para métricas detalladas, habilitar Spring Boot Actuator con micrometer");
        response.put("availableCaches", cacheManager.getCacheNames());
        
        Map<String, Object> config = new HashMap<>();
        config.put("cacheType", "Redis");
        config.put("defaultTTL", "10 minutos");
        config.put("serialization", "JSON");
        response.put("configuration", config);
        
        return ResponseEntity.ok(response);
    }
}