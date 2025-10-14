package indexacion;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.solr.client.solrj.SolrServerException;

import Solr.EditorSolr;

public class Main {

	public static void main(String[] args) throws FileNotFoundException, SolrServerException, IOException {

		Lector lector = new Lector();
		EditorSolr escritor = new EditorSolr();
		
		escritor.escribirArchivos(lector.extraerArchivos("Corpus/MED.ALL"), "Corpus");
		//escritor.vaciarColeccion("Corpus_MEDLARS");
	}

}
