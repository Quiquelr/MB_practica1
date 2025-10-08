package Solr;

import java.io.IOException;
import java.util.*;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrInputDocument;

import indexacion.Archivo;

public class EditorSolr {
	
    public void escribirArchivos(List<Archivo> archivos, String coleccion) throws SolrServerException, IOException{
    	    	    	     
		final SolrClient cliente = new HttpSolrClient.Builder("http://localhost:8983/solr").build();                                                    
        
        for (Archivo archivo: archivos) {
        	
        	SolrInputDocument documento = new SolrInputDocument(); 
        	
        	documento.addField("id", archivo.getId());
        	documento.addField("cuepo", archivo.getCuerpo());
        	
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

}
