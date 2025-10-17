package indexacion;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.solr.client.solrj.SolrServerException;

import Solr.EditorSolr;

public class Main {

	public static void main(String[] args) throws FileNotFoundException, SolrServerException, IOException {

		Lector lector = new Lector();
		EditorSolr editor = new EditorSolr();
		
		//editor.escribirArchivos(lector.extraerArchivos("Corpus/MED.ALL"), "Corpus");
		
		//escritor.vaciarColeccion("Corpus_MEDLARS");
		
		//HACER CONSULTAS
		List<Archivo> queries = lector.extraerArchivos("Corpus/MED.QRY");

		editor.buscarYGuardarResultados(queries, "Corpus", "resultados.txt");
            
	}

}
