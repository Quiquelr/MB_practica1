package com.solr.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.stereotype.Service;

import com.solr.dto.DocumentoDTO;


@Service
public class SolrService {
	
	private final HttpSolrClient solrClient;

    public SolrService(HttpSolrClient solrClient) {
        this.solrClient = solrClient;
    }
    
    
    /**
     * 
     * @param queryString string con la query para obtener los documentos
     * @return devuelve los documentos encontrados en una lista de DocumentoDTO
     * @throws SolrServerException
     * @throws IOException
     */
    public List<DocumentoDTO> buscar(String queryString) throws SolrServerException, IOException {
    	
		String queryFinal = queryString;
		boolean todo = false; // si es una búsqueda "ver todo"
		
    	SolrQuery query = new SolrQuery();        
        query.set("df", "texto"); //campo por defecto para buscar
		query.set("defType", "edismax");
		query.setFields("id, titulo, texto, score");
		query.setRows(1300);

		if (queryString == null || queryString.trim().isEmpty() || queryString.trim().equals("*:*")) {
			queryFinal = "*:*";
			todo = true;
		}	
		
		query.setQuery(queryFinal);    		
		
		//ordenar según busca total o normal
		if (todo) {			
			query.setSort("id", SolrQuery.ORDER.asc); //busca total ordenado por id
		} else {		
			query.setSort("score", SolrQuery.ORDER.desc); //busca normal ordena por relevancia
		}
        
        QueryResponse respuesta = solrClient.query(query);
        SolrDocumentList documentos = respuesta.getResults();
        
        List<DocumentoDTO> resultado = new ArrayList<>();
        for (SolrDocument doc : documentos) {        	          

            DocumentoDTO dto = new DocumentoDTO(
                (String) doc.getFieldValue("id"),
                (String) doc.getFieldValue("titulo"),
                (String) doc.getFieldValue("texto")
            );
            
            resultado.add(dto);
        }
    	
        return resultado;
    }
    
    /**
     * Indexa una lista de archivos en Solr.
     */
    public void escribirArchivos(List<DocumentoDTO> documentos) throws SolrServerException, IOException {
    	
    	for(DocumentoDTO doc: documentos) {
    		
    		SolrInputDocument solrDoc = new SolrInputDocument();
    		
    		solrDoc.addField("id",  doc.getId());
    		solrDoc.addField("titulo", doc.getTitulo());
    		solrDoc.addField("texto", doc.getCuerpo());
    		
    		solrClient.add(solrDoc);
    		
    	}
    	
    	solrClient.commit();
    	
    }
    
    /**
     * Borra todos los documentos de la colección.
     */
    public void vaciarColeccion() throws SolrServerException, IOException {
       
        solrClient.deleteByQuery("*:*");
        solrClient.commit();
     
    }           
        
}
