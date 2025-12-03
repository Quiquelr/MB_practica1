package indexacion;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Lector {

    /**
     * Extrae una lista de Archivos desde una ruta de fichero,
     * extrayendo ID, Título y Cuerpo.
     */
    public List<Archivo> extraerArchivos(String ruta) throws FileNotFoundException {

        List<Archivo> archivos = new ArrayList<>();
        Scanner scan = new Scanner(new File(ruta));

        Archivo archivoActual = null;
        StringBuilder cuerpo = new StringBuilder();
        boolean leyendoCuerpo = false;

        while (scan.hasNextLine()) {
            String linea = scan.nextLine().trim();

            if (linea.startsWith(".I")) {

                if (archivoActual != null) { // ya se estaba procesando un archivo, se guarda                    
                    procesarYGuardarArchivo(archivoActual, cuerpo.toString(), archivos);
                }

                // nuevo archivo
                archivoActual = new Archivo();
                cuerpo.setLength(0); // limpiar cuerpo
                leyendoCuerpo = false;

                // ID
                String id[] = linea.split(" ");
                if (id.length > 1) { // si tiene id
                    // Usamos String para el ID para que coincida con Solr/DTO
                    archivoActual.setId(Long.parseLong(id[1].trim()));
                }

            } else if (linea.startsWith(".W")) {
                leyendoCuerpo = true;
                // No añadimos la propia línea .W al cuerpo
            } else if (leyendoCuerpo && archivoActual != null) {
                // leer cuerpo linea a linea
                cuerpo.append(linea).append(" "); // Añadimos espacio entre líneas
            }
        }

        // Añadir último documento si existe
        if (archivoActual != null) {
            // Procesar y guardar el último archivo
            procesarYGuardarArchivo(archivoActual, cuerpo.toString(), archivos);
        }

        scan.close();
        return archivos;
    }
    
	public List<Archivo> extraerQuery(String ruta) throws FileNotFoundException {
		
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
					archivoActual.setId(Long.parseLong(id[1].trim()));
				}
						
			} else if (linea.startsWith(".W")) {

				leyendoCuerpo = true;
			} else if (leyendoCuerpo && archivoActual != null) {

				// leer cuerpo linea a linea
				cuerpo.append(linea).append(" ");
			}
		}

		// Añadir último documento si existe
		if (archivoActual != null) {
			archivoActual.setCuerpo(cuerpo.toString().trim());
			archivos.add(archivoActual);
		}

		scan.close();
		return archivos;
	}

    /**
     * Método helper para procesar el texto del cuerpo, extraer el título
     * y añadir el archivo a la lista.
     */
    private void procesarYGuardarArchivo(Archivo archivo, String cuerpoCompleto, List<Archivo> listaArchivos) {

        String cuerpoLimpio = cuerpoCompleto.trim().replaceAll("\\s+", " ");
        String titulo = "";
        String cuerpoSinTitulo = ""; // Empezar vacío por defecto

        if (cuerpoLimpio.isEmpty()) {
            archivo.setTitulo(titulo);
            archivo.setCuerpo(cuerpoSinTitulo);
            listaArchivos.add(archivo);
            return;
        }

        // Dividimos por el delimitador de frase (punto)
        // El 3 significa: "frase1", "frase2", "todo lo demás"
        String[] frases = cuerpoLimpio.split("\\.", 3);

        if (frases.length > 0) {
            String primeraFrase = frases[0].trim();

            // Comprobamos si la primera "frase" es solo un número
            if (primeraFrase.matches("\\d+") && frases.length > 1) {
                
                // REGLA 2: Es un número. Cogemos la siguiente frase también.
                String segundaFrase = frases[1].trim();
                titulo = primeraFrase + ". " + segundaFrase + ".";

                // El cuerpo es la tercera parte (si existe)
                if (frases.length > 2) {
                    cuerpoSinTitulo = frases[2].trim();
                }
            
            } else {
                // REGLA 1: La primera frase es el título.
                titulo = primeraFrase + ".";
                
                // El cuerpo es la segunda parte (si existe)
                if (frases.length > 1) {
                    cuerpoSinTitulo = frases[1].trim();
                    
                    // Si había una tercera parte, la volvemos a unir
                    if (frases.length > 2) {
                        // (El punto se perdió en el split, lo reponemos)
                        cuerpoSinTitulo += ". " + frases[2].trim();
                    }
                }
            }
        }

        archivo.setTitulo(titulo);
        archivo.setCuerpo(cuerpoSinTitulo); // Asignamos el cuerpo ya limpio
        listaArchivos.add(archivo);
    }
}