package com.solr.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.solr.client.solrj.SolrServerException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.solr.dto.DocumentoDTO;
import com.solr.dto.RespuestaRAG;
import com.solr.service.OllamaService;
import com.solr.service.SolrService;

@RestController
@RequestMapping("/api/query")
@CrossOrigin(origins = "*")
public class SolrQueryController {

	private final SolrService searchService;
	private final OllamaService ollamaService;

	public SolrQueryController(SolrService searchService, OllamaService ollamaService) {
        this.searchService = searchService;
        this.ollamaService = ollamaService;
    }

	@GetMapping
    public RespuestaRAG query(
        @RequestParam("q") String query,
        @RequestParam(value = "summarize", defaultValue = "false") boolean resumir,
        @RequestParam(value = "includeQueryInPrompt", defaultValue = "false") boolean incluirQuery
    ) throws SolrServerException, IOException {
    	
    	String solrQuery = query;
    	
    	if(solrQuery.trim().isEmpty() || solrQuery.trim().equals("*")) {
    		solrQuery = "*:*";      	
    	}
    	
        //búsqueda en Solr
        List<DocumentoDTO> resultados = searchService.buscar(solrQuery);

        String resumen = "Resumen RAG no solicitado.";

        if (resumir) {
            
            //contexto RAG: primeros 5 resultados
            List<DocumentoDTO> contextoRAG = resultados.stream()
                .limit(5)
                .collect(Collectors.toList());
            
            //resumen Ollama
            resumen = ollamaService.generarResumen(contextoRAG, query, incluirQuery);
            
        }
              
        return new RespuestaRAG(resumen, resultados);
    }
}