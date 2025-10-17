package Solr;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;

import indexacion.Archivo;

public class EditorSolr {

	public void escribirArchivos(List<Archivo> archivos, String coleccion) throws SolrServerException, IOException {

		final SolrClient cliente = new HttpSolrClient.Builder("http://localhost:8983/solr").build();

		for (Archivo archivo : archivos) {

			SolrInputDocument documento = new SolrInputDocument();

			documento.addField("id", archivo.getId());
			documento.addField("texto", archivo.getCuerpo());

			cliente.add(coleccion, documento);
		}

		cliente.commit(coleccion);
		cliente.close();
	}

	public void vaciarColeccion(String coleccion) throws SolrServerException, IOException {

		final SolrClient cliente = new HttpSolrClient.Builder("http://localhost:8983/solr").build();

		cliente.deleteByQuery(coleccion, "*:*");

		cliente.commit(coleccion);

		cliente.close();
	}

	// Dada una lista de queries, busca en una colección documentos con las 5
	// primeras palabras de cada query
	public void buscarYGuardarResultados(List<Archivo> queries, String coleccion, String archivoSalida)
			throws SolrServerException, IOException {
		final SolrClient cliente = new HttpSolrClient.Builder("http://localhost:8983/solr").build();

		try (PrintWriter salida = new PrintWriter(new FileWriter(archivoSalida))) {
			for (Archivo query : queries) {
				String textoQuery = query.getCuerpo();
				String[] palabras = textoQuery.split("\\s+"); // texto dividido por palabras

				// Coge 5 primeras palabras no vacías
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

				// Hacer query en solr

				if (!palabrasLimpias.isEmpty()) {
					
					System.out.println("ID de Query a ejecutar: " + query.getId() + ": " + palabrasLimpias.toString());
					
					String queryString = String.join(" AND ", palabrasLimpias);

					SolrQuery solrQuery = new SolrQuery();
					solrQuery.setQuery("texto: (" + queryString + ")");
					solrQuery.setRows(1000);

					QueryResponse respuesta = cliente.query(coleccion, solrQuery);
					SolrDocumentList idDocumentos = respuesta.getResults();

					// Rellenar archivo de salida
					if (idDocumentos.size() != 0) {
						for (int i = 0; i < idDocumentos.size(); i++) {
							salida.println(query.getId() + " " + idDocumentos.get(i).getFieldValue("id"));
						}

						System.out.println("	Se han encontrado " + idDocumentos.size() + " para la query " + query.getId());
					} else {
						System.out.println("	No se han encontrado documentos para la query " + query.getId());
					}
				}else {
					System.out.println(" !! No hay palabras para buscar");
				}

			}

			System.out.println("Proceso completado. Resultados en: " + archivoSalida);
		}

		cliente.close();
	}

}
