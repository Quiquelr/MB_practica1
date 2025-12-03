package com.solr.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import com.solr.dto.DocumentoDTO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class OllamaService {

    private final WebClient webClient;
    private final String modelName;

    public OllamaService(
        WebClient.Builder webClientBuilder,
        @Value("${ollama.api.url}") String ollamaApiUrl,
        @Value("${ollama.model.name}") String modelName) {
        
        //Configurar API ollama
        this.webClient = webClientBuilder.baseUrl(ollamaApiUrl).build();
        this.modelName = modelName;
    }

    /**
     * Genera resumen basado en contexto de documentos y query original.
     * @param documentos Lista de documentos que devuelve Solr para el contexto RAG (5 primeros)
     * @param query query original
     * @param incluirQuery Si se incluiye query en el prompt
     * @return resumen de ia
     */
    public String generarResumen( List<DocumentoDTO> documentos, String query, boolean incluirQuery) {

        if (documentos == null || documentos.isEmpty()) {
            return "No se encontraron documentos para generar un resumen.";
        }
        
        //CONSTRUCCIÓN DEL CONTEXTO (unir titulos y cuerpos de los documentos encontrados)
        StringBuilder contexto = new StringBuilder();
        for (int i = 0; i < documentos.size(); i++) {
            DocumentoDTO doc = documentos.get(i);
            contexto.append(String.format("--- Documento %d (ID: %s, Titulo: %s) ---\n", i + 1, doc.getId(), doc.getTitulo() != null ? doc.getTitulo() : "N/A"));
            contexto.append(doc.getCuerpo()).append("\n");
        }

        //PROMPT
        String instruction = "Eres un asistente de resumen. Basándote ÚNICAMENTE en el siguiente CONTEXTO, genera un resumen breve y coherente en ESPAÑOL. "
                           + "No inventes información que no esté en el contexto. Si no hay suficiente información en el contexto para responder, indícalo.";

        //Si queremos incluir la búsqueda al prompt
        if (incluirQuery) {
            instruction += "\n\nLa búsqueda original del usuario fue: \"" + query + "\". Utiliza esta información como guía para el resumen.";
        }
        
        String prompt = instruction 
                      + "\n\nCONTEXTO:\n" 
                      + contexto.toString() 
                      + "\n\nRESUMEN:";

        //API DE OLLAMA
        try {
            //request para /api/generate de Ollama
            String requestCuerpo = new ObjectMapper().writeValueAsString(
                java.util.Map.of(
                    "model", modelName,
                    "prompt", prompt,
                    "stream", false, //respuesta completa
                    "options", java.util.Map.of("temperature", 0.1) //Baja temperatura para resúmenes, para que sea poco creativo y solo se base en los documentos
                )
            );

            //petición POST a Ollama
            String responseBody = webClient.post()
                .body(BodyInserters.fromValue(requestCuerpo))
                .retrieve()
                .bodyToMono(String.class)
                .block();

            //Coger JSON de Ollama para extraer resumen
            JsonNode raiz = new ObjectMapper().readTree(responseBody);
            String resumen = raiz.path("response").asText();
            
            return resumen;

        } catch (Exception e) {
            System.err.println("Error al comunicarse con Ollama: " + e.getMessage());
            return "Error: No se pudo generar el resumen. Verifique la conexión con Ollama.";
        }
    }
    
    
}