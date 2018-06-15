/*
 * Esta clase lee el archivo (cual nombre se le pasa al constructor).
 * De esta lectura crea un facil acceso a las validaciones que se tienen
 * que hacer en cada perfil como tambien el nombre del script para validar
 * 
 * This class reads the file which name is passed to the constructor.
 * This reading creates an easy access to the validations that should be
 * done in each profile as well as the name of the script for the validation
 */
package backend;

import frontend.FrontEnd;
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
import javafx.util.Pair;
import javax.swing.JOptionPane;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

/**
 *
 * @authors npalavec, Ezequiel Martin Zarza
 */
public class Validations {
        
    // Lista de caracteristicas del perfil del producto
    List<Pair> profileConf;
    List<Pair> mandatoryFields;
    
    // Arbol que tiene todas las configuraciones posibles de los perfiles
    NodeList profile;
    NodeList validation;
    
    // Contructor
    public Validations(FrontEnd frame, String pathAndName) {
        // Inicializa variables de la clase   
        this.profileConf = new ArrayList<>();
        this.mandatoryFields = new ArrayList<>();
        try {            
            // Lee archivo XML y lo interpreta en DOM
            File xmlFile = new File(pathAndName);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc;
            try {
                doc = dBuilder.parse(xmlFile);
            } catch(SAXException ex) {
                Logger.getLogger(Validations.class.getName()).log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(frame, "Error validating XML file, program may be prone to errors:\n" + ex.toString().replace(";", ";\n"));
                return;
            }
            doc.getDocumentElement().normalize();
            
            // Extrae la lista de opciones de perfil
            this.profile = doc.getElementsByTagName("profile");
            this.validation = doc.getElementsByTagName("validation");
            
            
            // Extrae las opciones una por una
            NodeList options = getNodeListByTagName(this.profile,"options");
            for (int optionIndex = 0; optionIndex < options.getLength(); optionIndex++) { 
                
                String tagName = options.item(optionIndex).getNodeName();
                
                if (hasValidOptionsTag(tagName) && options.item(optionIndex).hasAttributes()) {
                    // Dentro de cada opcion extrae las subopciones una por una
                    NodeList items = options.item(optionIndex).getChildNodes();
                    
                    String id = getAttribute(options.item(optionIndex),"id");
                    //Boolean mandatory = ;   
                    
                    for(int itemsIndex = 0; itemsIndex < items.getLength(); itemsIndex++){
                        if(hasValidOptionsTag(items.item(itemsIndex).getNodeName())){
                            if(items.item(itemsIndex).getNodeName().compareTo("title") == 0) {
                                this.profileConf.add(new Pair("title_"+id,getValue(items.item(itemsIndex))));
                                if(isMandatory(options.item(optionIndex))) this.mandatoryFields.add(new Pair(tagName+"_"+id,getValue(items.item(itemsIndex))));
                            } else if(items.item(itemsIndex).getNodeName().compareTo("description") == 0)
                                this.profileConf.add(new Pair("description_"+id,getValue(items.item(itemsIndex))));
                            else {
                                this.profileConf.add(new Pair(tagName+"_"+id,getValue(items.item(itemsIndex))));
                            }
                        }
                    }
                }
            }
        } catch (ParserConfigurationException | IOException ex) {
            Logger.getLogger(Validations.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // Devuelve una lista de Pair<String> que contiene el tipo de tag 
    // concatenado con su ID en el "key" y su valor en el "value"
    // key = "tag_id" value="valor"
    // Ej: key="title_mastercard" value="Is a MasterCard?"
    //     key="radio_mastercard" value="Yes"
    //     key="radio_mastercard" value="No"
    public List<Pair> getProfileOptions(){ return this.profileConf; }
    
    
    public List<Pair> getMandatoryFields() { return this.mandatoryFields; }
    
    
    // Devuelve una lista de Pair<String> que contiene el tipo de tag 
    // concatenado con su ID en el "key" y su valor en el "value" de las
    // opciones extras dependientes de la primera seleccion del perfil
    // key = "tag_id" value="valor"
    // Ej: key="title_magstripe" value="Does it have a magstripe?"
    //     key="radio_magstripe" value="Yes"
    //     key="radio_magstripe" value="No"
    public List<Pair> getExtraProfileOptions(List<Pair> selectedOptions){
        List<Pair> returnExtraOptions = new ArrayList<>();
        
        // Busca si el item en el profile configuration no fue seleccionado
        String searched = "";
        for(Pair myItem : this.profileConf){
            // Si no busco el tipo de item antes
            String itemKey = myItem.getKey().toString();
            if(!searched.contains(itemKey)){
                // Busca en cada opcion seleccionada el profile conf
                boolean globalFound = false;
                for(Pair mySelected : selectedOptions){
                    if(mySelected.getKey().toString().contains(itemKey))
                        globalFound = true;
                }

                // Si al final no lo encuentra agrega el item con label None
                if(!globalFound && hasSelectableTag(itemKey))
                    selectedOptions.add(new Pair(itemKey,"None"));
                
                // Agrega el tipo de item a los buscados
                searched+=itemKey;
            }
        }
        
        // Extrae las opciones una por una
        NodeList extraOptions = getNodeListByTagName(this.profile,"extraOptions");
        for (int optionIndex = 0; optionIndex < extraOptions.getLength(); optionIndex++) { 
            String tagName;
            if (hasValidOptionsTag(tagName = extraOptions.item(optionIndex).getNodeName()) && extraOptions.item(optionIndex).hasAttributes()) {
                if(hasAllAttributes(extraOptions.item(optionIndex),selectedOptions)){
                    // Dentro de cada opcion extrae las subopciones una por una
                    NodeList items = extraOptions.item(optionIndex).getChildNodes();
                    String id = getAttribute(extraOptions.item(optionIndex),"id");
                    for(int itemsIndex = 0; itemsIndex < items.getLength(); itemsIndex++){
                        if(hasValidOptionsTag(items.item(itemsIndex).getNodeName())){
                            if(items.item(itemsIndex).getNodeName().compareTo("title") == 0)
                                returnExtraOptions.add(new Pair("title_"+id,getValue(items.item(itemsIndex))));
                            else if(items.item(itemsIndex).getNodeName().compareTo("description") == 0)
                                returnExtraOptions.add(new Pair("description_"+id,getValue(items.item(itemsIndex))));
                            else
                                returnExtraOptions.add(new Pair(tagName+"_"+id,getValue(items.item(itemsIndex))));                        
                        }
                    }
                }
            }
        }
        
        return returnExtraOptions;
    }
    
    // Returns a list of Pair<String> which contain the name of the test
    // in the key and the description of the test in the value
    // key="name_of_test" value="description_of_test"
    public List<Pair> getCheckListDescription(List<Pair> profileSelected){return this.getCheckListItem(profileSelected, false);}
    
    // Returns a list of Pair<String> which contain the name of the test
    // in the key and the location of the validation script of the test in the value
    // key="name_of_test" value="script_of_test"
    public List<Pair> getCheckListScripts(List<Pair> profileSelected){return this.getCheckListItem(profileSelected, true);}
    
    //********************* PRIVATE FUNCTIONS *********************//
    
    // Devuelve un item del checklist. Si "isScript" es true, devuelve el nombre del
    // test con la ubicacion del script. Si es false devuelve el nombre del test con
    // la descripcion del test
    @SuppressWarnings("StringEquality")
    private List<Pair> getCheckListItem(List<Pair> profileSelected, boolean isScript){
        
        // Extrae las opciones una por una
        List<Pair> checklist = new ArrayList<>();
        for (int validationIndex = 0; validationIndex < this.validation.getLength(); validationIndex++) { 
            if (this.validation.item(validationIndex).hasAttributes() && 
                    attributesContainNodeAtt(this.validation.item(validationIndex), profileSelected) ) {
                // Dentro de cada validacion extrae el titulo y la descripcion
                NodeList items = this.validation.item(validationIndex).getChildNodes();
                Pair myValidation = new Pair("","");
                for(int itemsIndex = 0; itemsIndex < items.getLength(); itemsIndex++){
                    if(hasValidOptionsTag(items.item(itemsIndex).getNodeName())){
                        String valueToAdd = items.item(itemsIndex).getTextContent();
                        if(items.item(itemsIndex).getNodeName().compareTo("title") == 0)
                            myValidation = new Pair(valueToAdd,myValidation.getValue().toString());
                        else if(items.item(itemsIndex).getNodeName().compareTo("description") == 0) {
                            if(!isScript)
                                myValidation = new Pair(myValidation.getKey().toString(),valueToAdd);
                        } else if(items.item(itemsIndex).getNodeName().compareTo("script") == 0){
                            if(isScript)
                                myValidation = new Pair(myValidation.getKey().toString(),valueToAdd);
                        }
                    }
                }
                
                // Agrega la validacion extraida a la lista
                checklist.add(myValidation);
            }
        }
        return checklist;
    }
    
    private NodeList getNodeListByTagName(NodeList parent, String tag){
        return this.getNodeListByTagName(parent,tag,0);
    }
    private NodeList getNodeListByTagName(NodeList parent,String tag, int index){
        Element myEl = (parent.item(index).getNodeType() == Node.ELEMENT_NODE) ? (Element) parent.item(index) : null;
        if(myEl!=null) parent = myEl.getElementsByTagName(tag);
        if(parent!=null) return parent.item(index).getChildNodes();
        else return null;
    }
    
    private String getAttribute(Node myNode,String att){
        return (myNode.getNodeType() == Node.ELEMENT_NODE)?((Element)myNode).getAttribute(att):"";
    }
    
    private Boolean isMandatory(Node myNode) {
        if(myNode.getNodeType() == Node.ELEMENT_NODE) 
            return ((Element)myNode).hasAttribute("mandatory") && (!((Element)myNode).getAttribute("mandatory").equals("false"));
        return false;
    }
    
    private String getValue(Node myNode){
        return (myNode.getNodeType() == Node.ELEMENT_NODE)?((Element)myNode).getTextContent():"";
    }
    
    private boolean hasValidOptionsTag(String tag){
        return tag.contains("combobox") || tag.contains("check") 
                || tag.contains("radio") || tag.contains("title") 
                || tag.contains("item") || tag.contains("description")
                || tag.contains("script");
    }
    
    private boolean hasSelectableTag(String tag){
        return tag.contains("combobox") || tag.contains("check") || tag.contains("radio");
    }
    
    private boolean hasAllAttributes(Node myNode,List<Pair> myAtt){
        /*List<String> attTag = new ArrayList<>();
        
        // Rellena el attTag con todos los atributos que se buscan
        myAtt.forEach((attTagValue) -> { attTag.add(attTagValue.getKey().toString()); });*/
        
        //  Devuelve los atributos que tiene el documento de configuracion
        List<Pair> attTagValueDoc = getAttributes(myNode);//, attTag);
        
        // Por cada atributo de busqueda se fija en todos los elementos definidos
        // por el docuemento
        boolean globalFound = !myAtt.isEmpty();
        boolean foundCurrent = false;
        for(Pair attTagValue : attTagValueDoc){  
            for(Pair attTagValueSearch : myAtt){              
                if( attTagValueSearch.equals(attTagValue) )
                    foundCurrent = true;
            }
            
            // Si encontro el elemento actual en el ciclo lo denota en el global
            // y reinicia el flag
            globalFound = globalFound && foundCurrent;                
            foundCurrent = false;
        }
                    
        return globalFound;
    }
    
    private boolean attributesContainNodeAtt(Node myNode,List<Pair> myAtt){
        /*List<String> attTag = new ArrayList<>();
        
        // Rellena el attTag con todos los atributos que se buscan
        myAtt.forEach((attTagValue) -> { attTag.add(attTagValue.getKey().toString()); });*/
        
        //  Devuelve los atributos que tiene el documento de configuracion
        List<Pair> attTagValueDoc = getAttributes(myNode);//, attTag);
        
        // Por cada atributo de busqueda se fija en todos los elementos definidos
        // por el docuemento
        boolean globalFound = !attTagValueDoc.isEmpty();
        boolean foundCurrent = false;
        for(Pair attTagValueSearch : attTagValueDoc){
            for(Pair attTagValue : myAtt) {
                if( attTagValueSearch.equals(attTagValue) )
                    foundCurrent = true;
            }
            
            // Si encontro el elemento actual en el ciclo lo denota en el global
            // y reinicia el flag
            globalFound = globalFound && foundCurrent;                
            foundCurrent = false;
        }
                    
        return globalFound;
    }
    
    private List<Pair> getAttributes(Node myNode) {
        List<Pair> myAttValues = new ArrayList<>();
        for(int i = 0; i < myNode.getAttributes().getLength(); i++) {
            myAttValues.add(new Pair(myNode.getAttributes().item(i).getNodeName(),myNode.getAttributes().item(i).getNodeValue()));
        }
        return myAttValues;
    }
    
    /*private List<Pair> getAttributes(Node myNode, List<String> myAtt){
        List<Pair> myAttValues = new ArrayList<>();
        myAtt.forEach((att) -> {
            myAttValues.add(new Pair(att, getAttribute(myNode, att)));
        });
        return myAttValues;
    }*/
}
