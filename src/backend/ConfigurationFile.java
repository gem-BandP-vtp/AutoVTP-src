/*
 * Esta clase levanta el archivo de configuracion del programa permitiendo leerlo
 * y escribirlo
 */
package backend;

import frontend.FrontEnd;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @authors npalavec, Ezequiel Martin Zarza
 */
public class ConfigurationFile {

    public ConfigurationFile() {
   
    }
    
    public HashMap<String,String> read(String pathAndName) throws FileNotFoundException, IOException {
        
        // Inicializa variables
        HashMap<String,String> map = new HashMap<>();
        BufferedReader reader = new BufferedReader(new FileReader(pathAndName));
        String currentLine;
        
        // Pone todos los valores en el archivo
        while ((currentLine = reader.readLine()) != null) {
            // Si no es comentario analiza la linea
            if(!currentLine.trim().startsWith("#")){
                String[] readText = currentLine.split(" ");
                if(readText.length >= 2)
                    map.put(readText[0],readText[1]);
            }
        }
            
        return map;
    }
    
    public boolean write(String[] lines, String pathAndName){
        try {
            Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(pathAndName)));
            for(String line : lines)
                writer.write(line+"\n");
            
            writer.close();
            return true;
        } catch (IOException ex) {
            Logger.getLogger(FrontEnd.class.getName()).log(Level.SEVERE, "Error writing configuration file", ex);
            return false;
        }
    }
}
