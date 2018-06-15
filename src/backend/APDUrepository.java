/*
 * Esta clase es un repositorio de los APDUs definidos en el archivo que se pasa
 * como parametro de inicializacion (se pasa el path y el nombre del archivo)
 * Es parte de la pestaña que actualmente esta en (deprecated)
 */
package backend;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

/**
 *
 * @author npalavec
 * @developmentContinuator 10053892 Ezequiel Martin Zarza
 */

public class APDUrepository {
    
    // Lista de los parametros de cada APDU
    List<String> apduNorms;
    List<String> apduNames;
    List<String> apduBytes;
    
    // Estos parametros contienen listas con todas las opciones de cada APDU
    List<List<String>> apduOptionsNames;
    List<List<String>> apduOptionsBytes;
    
    
    public APDUrepository(String pathAndName) {
        // Inicializa variables de la clase
        apduNorms = new ArrayList<>();
        apduNames = new ArrayList<>();
        apduBytes = new ArrayList<>();
        apduOptionsNames = new ArrayList<>();
        apduOptionsBytes = new ArrayList<>();
        
        try {            
            // Lee archivo XML y lo interpreta en DOM
            File xmlFile = new File(pathAndName);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);
            doc.getDocumentElement().normalize();

            
            // Extrae la lista de comandos APDU
            NodeList apduList = doc.getElementsByTagName("apdu");
            for (int apduIndex = 0; apduIndex < apduList.getLength(); apduIndex++) {                
                List<String> namesList = new ArrayList<>();
                List<String> bytesList = new ArrayList<>();
                
                // Si el apdu es un nodo
                Node apduNode = apduList.item(apduIndex);
                if (apduNode.getNodeType() == Node.ELEMENT_NODE) {
                    
                    // Extrae los elementos del APDU
                    Element apduElement = (Element) apduNode;
                    
                    // Agrega la norma
                    apduNorms.add(apduElement.getAttribute("class"));
                    //System.out.println("APDU norm : " + apduElement.getAttribute("class"));
                    
                    // Agrega el nombre del APDU
                    apduNames.add(apduElement.getElementsByTagName("name").item(0).getTextContent());
                    //System.out.println("APDU tag name : " + apduElement.getElementsByTagName("name").item(0).getTextContent());
                    
                    // Agrega el header fijo del APDU
                    apduBytes.add(apduElement.getElementsByTagName("bytes").item(0).getTextContent());
                    //System.out.println("APDU fixed header : " + apduElement.getElementsByTagName("bytes").item(0).getTextContent());
                    
                    // Extrae la lista de opciones del APDU
                    NodeList optionsList = apduElement.getElementsByTagName("option");
                    for (int optionIndex = 0; optionIndex < optionsList.getLength(); optionIndex++) {
                        // Si el apdu es un nodo
                        Node optionNode = optionsList.item(optionIndex);
                        if (optionNode.getNodeType() == Node.ELEMENT_NODE) {

                            // Extrae la opcion del APDU
                            Element optionElement = (Element) optionNode;
                            
                            // Extrae el nombre de la opcion
                            String name = optionElement.getElementsByTagName("name").item(0).getTextContent();
                            namesList.add(name);
                            //System.out.println("APDU option name"+(optionIndex+1)+" : " + name);
                            
                            // Extrae los bytes de la opcion
                            String bytes = optionElement.getElementsByTagName("bytes").item(0).getTextContent();
                            bytesList.add(bytes);
                            //System.out.println("APDU option bytes"+(optionIndex+1)+" : " + bytes);
                        }
                        
                    }
                    
                    // Agrega las opciones al APDU
                    apduOptionsNames.add(namesList);
                    apduOptionsBytes.add(bytesList);

                    //System.out.println("----------------------------");
                }
            }
        } catch (ParserConfigurationException | SAXException | IOException ex) {
            Logger.getLogger(APDUrepository.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public List<String> getAll(){
        return apduNames;        
    }
    
    public String get(int apduIndex){
        return apduBytes.get(apduIndex);
    }
    
    public String getNorm(int apduIndex){
        return apduNorms.get(apduIndex);
    }
    
    public List<String> getAllOptions(int apduIndex){
        return apduOptionsNames.get(apduIndex);        
    }
    
    public String getOption(int apduIndex, int optionIndex){
        return apduOptionsBytes.get(apduIndex).get(optionIndex);
    }
}
