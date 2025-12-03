package com.solr.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.solr.client.solrj.SolrServerException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.solr.service.SolrService;

@RestController
@RequestMapping("/api/query")
@CrossOrigin(origins = "*")
public class SolrQueryController {

	private final SolrService searchService;

	public SolrQueryController(SolrService searchService) {
		this.searchService = searchService;
	}

	@GetMapping
	public List<?> query(@RequestParam("q") String query) throws SolrServerException, IOException {

		if (query.equals("") || query.equals("")) {

			// Si se quiere mostrar todos los documentos
			query = "*:*";

		} else {

			String[] palabras = query.split("\\s+"); // texto dividido por palabras

			// Coge 5 primeras palabras
			List<String> palabrasLimpias = new ArrayList<>();
			for (String palabra : palabras) {
				// Si ya tenemos 5 palabras, paramos de buscar.
				if (palabrasLimpias.size() >= 5) {
					break;
				}

				String palabraLimpia = palabra.replaceAll("[^a-zA-Z0-9]", "");

				if (!palabraLimpia.isEmpty()) {
					palabrasLimpias.add(palabraLimpia);
				}

			}
			
			for(String palabra : palabrasLimpias) {
				query = query + " " + palabra;
			}
			
		}

		return searchService.buscar(query);
	}

}