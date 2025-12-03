package indexacion;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.apache.solr.client.solrj.SolrServerException;

import Solr.EditorSolr;

public class Main {

	public static void main(String[] args) throws FileNotFoundException, SolrServerException, IOException {

		Lector lector = new Lector();
		EditorSolr editor = new EditorSolr();
		
		editor.escribirArchivos(lector.extraerArchivos("Corpus/MED.ALL"), "Corpus4");
		
		//editor.vaciarColeccion("Corpus4");
		
		//HACER CONSULTAS
		//List<Archivo> queries = lector.extraerQuery("Corpus/MED.QRY");
		//editor.buscarYGuardarResultados(queries, "Corpus4", "resultados_v4.txt");
            
	}

}
