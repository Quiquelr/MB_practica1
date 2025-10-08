package indexacion;

import java.util.*;
import java.io.*;

public class Lector {	
	
	public List<Archivo> extraerArchivos(String ruta) throws FileNotFoundException {
		
		List<Archivo> archivos = new ArrayList<>();
		Scanner scan = new Scanner(new File(ruta));
		
		Archivo archivoActual = null;
		
		StringBuilder cuerpo = new StringBuilder();
		boolean leyendoCuerpo = false;
		
		while(scan.hasNextLine()) {
			String linea = scan.nextLine().trim();
			
			if(linea.startsWith(".I")) {
				
				if(archivoActual != null) { //ya se estaba procesando un archivo, se guarda
					
					archivoActual.setCuerpo(cuerpo.toString().trim());
					archivos.add(archivoActual);
				
				}
				
				//Crear nuevo archivo
				archivoActual = new Archivo();
				cuerpo.setLength(0); //limpiar cuerpo
				leyendoCuerpo = false;
				
				//Coger ID
				String id[] = linea.split(" ");
				if(id.length > 1) { //si tiene id
					archivoActual.setId(Integer.parseInt(id[1]));
				}
						
			} else if (linea.startsWith(".W")) {
				
				leyendoCuerpo = true;
			} else if (leyendoCuerpo && archivoActual != null) {
				
				//leer cuerpo linea a linea
				cuerpo.append(linea).append(" ");				
			}
		}
		
		//Añadir último documento si existe
		if(archivoActual != null) {
			archivoActual.setCuerpo(cuerpo.toString().trim());
			archivos.add(archivoActual);
		}
				
		scan.close();
		return archivos;
		
	}

}
