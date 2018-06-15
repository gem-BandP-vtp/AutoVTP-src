/*
 * Esta clase es el pop-up encargado de permitir al usuario elegir el perfil
 * que va a ser validado. Se le pasa su elemento parent y un frame en el que sera
 * colocada toda la parte grafica de la seleccion. Ademas, se requiere el objeto
 * que define todas las validaciones (class Validations).
 *
 * This class is the pop-up that allows the user to choose the profile
 * which will be validated. You pass your parent element and a frame in which it will be
 * placed the entire graphic part of the selection. In addition, the object that 
 * defines all validations is required (class Validations)
 * 
 */
package frontend;

import backend.Validations;
import java.awt.Frame;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.List;
import java.awt.Color;
import javafx.util.Pair;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JRootPane;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.border.LineBorder;

/**
 *
 * @authors npalavec, Ezequiel Martin Zarza
 */
public class ProfileSelector {
    
    // Lista de caracteristicas del perfil del producto
    final private List<Pair> profileConf; // Variables que devuelve Validations con las opciones que el usuario tiene que responder
    final private List<Pair> mandatoryFields;
    
    // Objeto que determina las validaciones y los perfiles
    final private Validations validations;
    
    // Panel auxiliar para poner las opciones extras
    final private JPanel extraJPanel;
    final private JFrame jf;
    final private JPanel jp;
    final private Frame parent;
    final private JButton jb;
    
    
    public ProfileSelector(Frame parent, JFrame jf, Validations validation, JFrame parameterWindow){
        
        // Guarda los objetos
        this.validations = validation;
        this.parent = parent;
        
        // Retorna la configuracion del perfil por primera vez
        this.profileConf = validation.getProfileOptions();
        
        this.mandatoryFields = validation.getMandatoryFields();
        
        
        //******************************************************************************************
        // Define todos los callback que tendran los componentes del FrontEnd        
        ActionListener actionCallback = (ActionEvent evt) -> {
            showExtraOptions(new ComponentEvent((Component)evt.getSource(), 1));
        };
        
        ActionListener radioCallback = this::refreshSelectionInRadios;
        
        ActionListener buttonCallback = this::jb_getProfileChecklist;
        //******************************************************************************************
        
        // Define el Frame principal
        this.jf = jf;
        this.jf.setTitle("Profile Options");
        this.jf.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/images/credit_cards.png")));
        this.jp = new JPanel(new GridLayout(0, 1));
        this.jp.setBorder(new EmptyBorder(20,20,20,20));
        
        
        
        // Define un String que usa el ciclo principal para saber que componente se puso ultimo
        String last = "none";        
        
        // Para todos los Pairs<unique_id,label_text>
        for(Pair myPair : this.profileConf) {
            // Define FrontEnd cuando el componente es un combobox
            if(myPair.getKey().toString().contains("combobox")){
                // Agrega el combobox si no existe con la configuracion
                JComboBox jcb = new JComboBox();
                jcb.addItem(myPair.getValue().toString());
                jcb.setName(myPair.getKey().toString());
                // Si el ultimo componente que agrego tiene el mismo Key
                // Agrega el siguiente combobox al grupo y al primer
                if( (last == null ? myPair.getKey().toString() == null : last.equals(myPair.getKey().toString())) || last.contains("title") || last.contains("description") ){
                    if(this.jp.getComponentCount() != 0){
                        Component comboPanel = this.jp.getComponent(this.jp.getComponentCount()-1);
                        if( (comboPanel.getClass()) == JPanel.class){
                            // Si el panel de los combos esta vacio agrega un combo, sino agrega el item dentro del combo
                            JPanel comboJPanel = ((JPanel)comboPanel);
                            if(comboJPanel.getComponentCount() != 0){
                                if(comboJPanel.getComponent(0).getClass() == JComboBox.class)
                                    ((JComboBox)comboJPanel.getComponent(0)).addItem(myPair.getValue().toString());
                            } else {
                                jcb.addActionListener(actionCallback);
                                comboJPanel.add(jcb);
                            }
                        }
                    }
                // Si el ultimo componente no contenia el mismo Key
                // Agrega un nuevo grupo de comboboxes
                } else {
                    JPanel subJPanel = new JPanel();
                    jcb.addActionListener(actionCallback);
                    subJPanel.add(jcb);
                    subJPanel.setBorder(new TitledBorder(""));  // Personaliza el Panel que contiene a los radio de un grupo
                    this.jp.add(subJPanel);
                }
                last = myPair.getKey().toString();
            
            // Define FrontEnd cuando el componente es un radio
            } else if(myPair.getKey().toString().contains("radio")){
                // Configura las propiedades del Radio
                JRadioButton jrb = new JRadioButton();
                jrb.setText(myPair.getValue().toString());
                jrb.setName(myPair.getKey().toString());
                jrb.addActionListener(actionCallback);  // Agrega el listener de la accion primero y despues el listener propietario de los radios
                jrb.addActionListener(radioCallback);
                // Si el ultimo componente que agrego tiene el mismo Key
                // Agrega el siguiente radio al grupo
                if( (last == null ? myPair.getKey().toString() == null : last.equals(myPair.getKey().toString())) || last.contains("title") || last.contains("description")){
                    if(this.jp.getComponentCount() != 0){
                        Component radioPanel = this.jp.getComponent(this.jp.getComponentCount()-1);
                        if( (radioPanel.getClass()) == JPanel.class){
                            ((JPanel)radioPanel).add(jrb);   
                        }
                    }
                // Si el ultimo componente no contenia el mismo Key
                // Agrega un nuevo grupo de radios
                } else {
                    JPanel subJPanel = new JPanel();
                    subJPanel.add(jrb);
                    subJPanel.setBorder(new TitledBorder(""));  // Personaliza el Panel que contiene a los radio de un grupo
                    this.jp.add(subJPanel);
                }
                last = myPair.getKey().toString();
                
            // Define FrontEnd cuando el componente es un checkbox
            } else if(myPair.getKey().toString().contains("check")){
                // Agrega el checkbox con la configuracion deseada
                JCheckBox jcb = new JCheckBox();
                jcb.setText(myPair.getValue().toString());
                jcb.setName(myPair.getKey().toString());
                // Si el ultimo componente que agrego tiene el mismo Key
                // Agrega el siguiente check al grupo
                if( (last == null ? myPair.getKey().toString() == null : last.equals(myPair.getKey().toString())) || last.contains("title") || last.contains("description")){
                    if(this.jp.getComponentCount() != 0){
                        Component checkPanel = this.jp.getComponent(this.jp.getComponentCount()-1);
                        if( (checkPanel.getClass()) == JPanel.class){
                            jcb.addActionListener(actionCallback);
                            ((JPanel)checkPanel).add(jcb);   
                        }
                    }
                // Si el ultimo componente no contenia el mismo Key
                // Agrega un nuevo grupo de checks
                } else {
                    JPanel subJPanel = new JPanel();
                    jcb.addActionListener(actionCallback);
                    subJPanel.add(jcb);
                    subJPanel.setBorder(new TitledBorder(""));  // Personaliza el Panel que contiene a los radio de un grupo
                    this.jp.add(subJPanel);
                }
                last = myPair.getKey().toString();
                
            // Define FrontEnd cuando el componente es un titulo
            } else if(myPair.getKey().toString().contains("title")){
                JPanel subJPanel = new JPanel();
                subJPanel.setBorder(new TitledBorder(myPair.getValue().toString()));  // Personaliza el Panel que contiene a los grupos de elementos que continuan en el ciclo
                this.jp.add(subJPanel);
                last = myPair.getKey().toString();
                
            // Define FrontEnd cuando el componente es una descripcion
            } else if(myPair.getKey().toString().contains("description")){
                if( last.contains("title") || last.contains("description") ){
                    if(this.jp.getComponentCount() != 0){
                        Component comboPanel = this.jp.getComponent(this.jp.getComponentCount()-1);
                        if( (comboPanel.getClass()) == JPanel.class){
                            // Si el panel de los combos esta vacio agrega un combo, sino agrega el item dentro del combo
                            JPanel labelJPanel = ((JPanel)comboPanel);
                            labelJPanel.add(new JLabel(myPair.getValue().toString()));
                        }
                    }
                } else {
                    JPanel subJPanel = new JPanel();
                    subJPanel.add(new JLabel(myPair.getValue().toString()));
                    this.jp.add(subJPanel);
                    last = myPair.getKey().toString();
                }
            }
        }
        
        // Agrega el panel de las opciones extras
        this.extraJPanel = new JPanel();
        this.jp.add(this.extraJPanel);
        
        // Agrega el boton de aceptar
        jb = new JButton("OK");
        jb.addActionListener(buttonCallback);
        this.jp.add(jb);
    }
    
    //************************ FUNCIONES PUBLICAS ****************************//
    
    public JPanel returnProfileJPanel(){
        JPanel optionPanel = new JPanel();
        JPanel subJP = new JPanel();
        
        List<Pair> selectedOptions = this.getTitleOfSelectedOptions();
        for(Pair option : selectedOptions){
            subJP.add(new Label(option.getKey().toString()));
            JLabel label = new JLabel(option.getValue().toString());
            label.setBorder(new CompoundBorder(new LineBorder(Color.gray, 2, true), new EmptyBorder(5, 5, 5, 5)));
            subJP.add(label);
        }        
        
        // Agrega el subpanel
        optionPanel.add(subJP);
        
        return optionPanel;
    }
    
    public List<Pair> getMainSelectedOptions(){ return getSelectedOptions(false); }
    public List<Pair> getAllSelectedOptions(){ return getSelectedOptions(true); }
    
    public void popUp(){
        // Agrega el JPanel al popup (JFrame)
        this.jf.setContentPane(jp);
        this.jf.pack();
        this.jf.setLocationRelativeTo(parent);
        this.jf.setVisible(true);
       
    }
    
    //********************** Metodos privados ****************************//
        
    private void refreshSelectionInRadios(ActionEvent evt) {
        // Define variables que va a usar mas adelante
        String name = ((JRadioButton)evt.getSource()).getName();
        String text = ((JRadioButton)evt.getSource()).getText();
        boolean selected = ((JRadioButton)evt.getSource()).isSelected();
        
        // Selecciona el radio correspondiente
        if(evt.getSource().getClass() == JRadioButton.class){
            JRadioButton rb = ((JRadioButton)evt.getSource());
            rb.setSelected(!rb.isSelected());
        }
        
        // Recorre todos los componentes que estan en el mismo JPanel que de donde vino la accion
        // y los setea a falso
        for(Component comp : ((JPanel)((JRadioButton)evt.getSource()).getParent()).getComponents())
            if((comp.getName() == null ? name == null : comp.getName().equals(name)) && comp.getName().contains("radio")){
                boolean setting = ((JRadioButton)comp).getText() == null ? text != null : ((JRadioButton)comp).getText().equals(text);
                ((JRadioButton)comp).setSelected(setting);
            }
    }
    
    private void showExtraOptions(ComponentEvent evt){       
        // Saca los items seleccionados por el usuario
        List<Pair> selected = getMainSelectedOptions();
        
        // Consulta a la validacion cuales son los parametros adicionales particulares
        List<Pair> extra = validations.getExtraProfileOptions(selected);
        
        //******************************************************************************************
        // Define todos los callback que tendran los componentes del FrontEnd        
        ActionListener radioCallback = this::refreshSelectionInRadios;
        //******************************************************************************************
        
        // Define un String que usa el ciclo principal para saber que componente se puso ultimo
        String last = "none";       
        
        // Borra lo anterior de las opciones extras
        this.extraJPanel.removeAll();
        
        // Muestra en el FrontEnd esos parametros adicionales
        // Para todos los Pairs<unique_id,label_text>
        for(Pair myPair : extra) {
            // Define FrontEnd cuando el componente es un combobox
            if(myPair.getKey().toString().contains("combobox")){
                // Agrega el combobox si no existe con la configuracion
                JComboBox jcb = new JComboBox();
                jcb.addItem(myPair.getValue().toString());
                jcb.setName(myPair.getKey().toString());
                // Si el ultimo componente que agrego tiene el mismo Key
                // Agrega el siguiente combobox al grupo y al primer
                if( (last == null ? myPair.getKey().toString() == null : last.equals(myPair.getKey().toString())) || last.contains("title") || last.contains("description") ){
                    if(this.extraJPanel.getComponentCount() != 0){
                        Component comboPanel = this.extraJPanel.getComponent(this.extraJPanel.getComponentCount()-1);
                        if( (comboPanel.getClass()) == JPanel.class){
                            // Si el panel de los combos esta vacio agrega un combo, sino agrega el item dentro del combo
                            JPanel comboJPanel = ((JPanel)comboPanel);
                            if(comboJPanel.getComponentCount() != 0){
                                if(comboJPanel.getComponent(0).getClass() == JComboBox.class)
                                    ((JComboBox)comboJPanel.getComponent(0)).addItem(myPair.getValue().toString());
                            } else {
                                comboJPanel.add(jcb);
                            }
                        }
                    }
                // Si el ultimo componente no contenia el mismo Key
                // Agrega un nuevo grupo de comboboxes
                } else {
                    JPanel subJPanel = new JPanel();
                    subJPanel.add(jcb);
                    subJPanel.setBorder(new TitledBorder(""));  // Personaliza el Panel que contiene a los radio de un grupo
                    this.extraJPanel.add(subJPanel);
                }
                last = myPair.getKey().toString();
            
            // Define FrontEnd cuando el componente es un radio
            } else if(myPair.getKey().toString().contains("radio")){
                // Configura las propiedades del Radio
                    JRadioButton jrb = new JRadioButton();
                jrb.setText(myPair.getValue().toString());
                jrb.setName(myPair.getKey().toString());
                jrb.addActionListener(radioCallback);
                // Si el ultimo componente que agrego tiene el mismo Key
                // Agrega el siguiente radio al grupo
                if( (last == null ? myPair.getKey().toString() == null : last.equals(myPair.getKey().toString())) || last.contains("title") || last.contains("description")){
                    if(this.extraJPanel.getComponentCount() != 0){
                        Component radioPanel = this.extraJPanel.getComponent(this.extraJPanel.getComponentCount()-1);
                        if( (radioPanel.getClass()) == JPanel.class){
                            ((JPanel)radioPanel).add(jrb);   
                        }
                    }
                // Si el ultimo componente no contenia el mismo Key
                // Agrega un nuevo grupo de radios
                } else {
                    JPanel subJPanel = new JPanel();
                    subJPanel.add(jrb);
                    subJPanel.setBorder(new TitledBorder(""));  // Personaliza el Panel que contiene a los radio de un grupo
                    this.extraJPanel.add(subJPanel);
                }
                last = myPair.getKey().toString();
                
            // Define FrontEnd cuando el componente es un checkbox
            } else if(myPair.getKey().toString().contains("check")){
                // Agrega el checkbox con la configuracion deseada
                JCheckBox jcb = new JCheckBox();
                jcb.setText(myPair.getValue().toString());
                jcb.setName(myPair.getKey().toString());
                // Si el ultimo componente que agrego tiene el mismo Key
                // Agrega el siguiente check al grupo
                if( (last == null ? myPair.getKey().toString() == null : last.equals(myPair.getKey().toString())) || last.contains("title") || last.contains("description")){
                    if(this.extraJPanel.getComponentCount() != 0){
                        Component checkPanel = this.extraJPanel.getComponent(this.extraJPanel.getComponentCount()-1);
                        if( (checkPanel.getClass()) == JPanel.class){
                            //jcb.addFocusListener(focusCallback);
                            jcb.addActionListener(radioCallback);
                            ((JPanel)checkPanel).add(jcb);   
                        }
                    }
                // Si el ultimo componente no contenia el mismo Key
                // Agrega un nuevo grupo de checks
                } else {
                    JPanel subJPanel = new JPanel();
                    subJPanel.add(jcb);
                    subJPanel.setBorder(new TitledBorder(""));  // Personaliza el Panel que contiene a los radio de un grupo
                    this.extraJPanel.add(subJPanel);
                }
                last = myPair.getKey().toString();
                
            // Define FrontEnd cuando el componente es un titulo
            } else if(myPair.getKey().toString().contains("title")){
                JPanel subJPanel = new JPanel();
                subJPanel.setBorder(new TitledBorder(myPair.getValue().toString()));  // Personaliza el Panel que contiene a los grupos de elementos que continuan en el ciclo
                this.extraJPanel.add(subJPanel);
                last = myPair.getKey().toString();
                
            // Define FrontEnd cuando el componente es una descripcion
            } else if(myPair.getKey().toString().contains("description")){
                if( last.contains("title") || last.contains("description") ){
                    if(this.extraJPanel.getComponentCount() != 0){
                        Component comboPanel = this.extraJPanel.getComponent(this.extraJPanel.getComponentCount()-1);
                        if( (comboPanel.getClass()) == JPanel.class){
                            // Si el panel de los combos esta vacio agrega un combo, sino agrega el item dentro del combo
                            JPanel labelJPanel = ((JPanel)comboPanel);
                            labelJPanel.add(new JLabel(myPair.getValue().toString()));
                        }
                    }
                } else {
                    JPanel subJPanel = new JPanel();
                    subJPanel.add(new JLabel(myPair.getValue().toString()));
                    this.extraJPanel.add(subJPanel);
                    last = myPair.getKey().toString();
                }
            }
        }
        
        // Empaqueta todo para que se muestre lo extra
        this.jf.pack();
    }
    
    /*private boolean hasValidOptionsTag(String tag){
        return tag.contains("combobox") || tag.contains("check") 
                || tag.contains("radio") || tag.contains("title") 
                || tag.contains("item") || tag.contains("description");
    }*/
    
    private List<Pair> getSelectedOptions(boolean all){
        List<Pair> selected = new ArrayList<>();
        
        // Este ciclo es por proteccion ya que componentes deberia haber 1 solo
        for(Component comp : this.jf.getComponents()){
            if(comp.getClass() == JRootPane.class){
                JRootPane myRootPane = ((JRootPane)comp);
                
                // Cada panel
                for(Component panelComp : myRootPane.getContentPane().getComponents()){
                    if(panelComp.getClass() == JPanel.class && (all?true:!(((JPanel)panelComp).equals(this.extraJPanel))) ){
                        JPanel myPanel = (JPanel)panelComp;                        
                        
                        // Si es un panel de opciones extras
                        if(myPanel.equals(this.extraJPanel)) {
                            for(Component myExtraPanelComponent : myPanel.getComponents()){
                                if(myExtraPanelComponent.getClass() == JPanel.class){
                                    for(Component itemComp : ((JPanel)myExtraPanelComponent).getComponents()){
                                        if(itemComp.getName() != null && itemComp.getName().contains("check")){
                                            // Check values are all selected options separated by ",", without spaces between them.
                                            // It's an empty string if none was selected.
                                            //String value = ""
                                            if(((JCheckBox)itemComp).isSelected())
                                                selected.add(new Pair(itemComp.getName(),((JCheckBox)itemComp).getText()));
                                        } else if(itemComp.getName() != null && itemComp.getName().contains("radio")){
                                            if(((JRadioButton)itemComp).isSelected())
                                                selected.add(new Pair(itemComp.getName(),((JRadioButton)itemComp).getText()));
                                        } else if(itemComp.getName() != null && itemComp.getName().contains("combobox")){
                                            if(((JComboBox)itemComp).getSelectedIndex() != -1)
                                                selected.add(new Pair(itemComp.getName(),((JComboBox)itemComp).getSelectedItem().toString()));
                                        }
                                    }
                                }
                            }
                        } else {
                            // Si es un panel estandar
                            for(Component itemComp : myPanel.getComponents()){
                                if(itemComp.getName() != null && itemComp.getName().contains("check")){
                                    if(((JCheckBox)itemComp).isSelected())
                                        selected.add(new Pair(itemComp.getName(),((JCheckBox)itemComp).getText()));
                                } else if(itemComp.getName() != null && itemComp.getName().contains("radio")){
                                    if(((JRadioButton)itemComp).isSelected())
                                        selected.add(new Pair(itemComp.getName(),((JRadioButton)itemComp).getText()));
                                } else if(itemComp.getName() != null && itemComp.getName().contains("combobox")){
                                    if(((JComboBox)itemComp).getSelectedIndex() != -1)
                                        selected.add(new Pair(itemComp.getName(),((JComboBox)itemComp).getSelectedItem().toString()));
                                }
                            }
                        }
                    }
                }   
            }
        }
        
        return selected;        
    }
    
    public List<Pair> getTitleOfSelectedOptions(){
        List<Pair> selected = new ArrayList<>();
        
        // Este ciclo es por proteccion ya que componentes deberia haber 1 solo
        for(Component comp : this.jf.getComponents()){
            if(comp.getClass() == JRootPane.class){
                JRootPane myRootPane = ((JRootPane)comp);
                
                // Cada panel en el RootPane
                for(Component panelComp : myRootPane.getContentPane().getComponents()){
                    if(panelComp.getClass() == JPanel.class){
                        JPanel myPanel = (JPanel)panelComp;                        
                        
                        // Si es un panel de opciones extras
                        if(myPanel.equals(this.extraJPanel)) {
                            // Recorre cada panel de opciones extras
                            for(Component myExtraPanelComponent : myPanel.getComponents()){
                                if(myExtraPanelComponent.getClass() == JPanel.class){
                                    
                                    // Extrae el titulo del panel
                                    String title;
                                    title = ((TitledBorder)((JPanel)myExtraPanelComponent).getBorder()).getTitle();
                                    
                                    // Revisa todos los objetos del panel y segun su tipo ve el item seleccionado
                                    for(Component itemComp : ((JPanel)myExtraPanelComponent).getComponents()){
                                        if(itemComp.getName() != null && itemComp.getName().contains("check")){
                                            if(((JCheckBox)itemComp).isSelected())
                                                selected.add(new Pair(title,((JCheckBox)itemComp).getText()));
                                        } else if(itemComp.getName() != null && itemComp.getName().contains("radio")){
                                            if(((JRadioButton)itemComp).isSelected())
                                                selected.add(new Pair(title,((JRadioButton)itemComp).getText()));
                                        } else if(itemComp.getName() != null && itemComp.getName().contains("combobox")){
                                            if(((JComboBox)itemComp).getSelectedIndex() != -1)
                                                selected.add(new Pair(title,((JComboBox)itemComp).getSelectedItem().toString()));
                                        }
                                    }
                                }
                            }   
                        }
                        
                        // Si es un panel estandar
                        else {
                            String title = "";
                            if(((TitledBorder)(myPanel.getBorder())) != null)
                                title = ((TitledBorder)(myPanel.getBorder())).getTitle();
                            
                            // Revisa todos los objetos del panel y segun su tipo ve el item seleccionado
                            for(Component itemComp : myPanel.getComponents()){
                                if(itemComp.getName() != null && itemComp.getName().contains("check")){
                                    if(((JCheckBox)itemComp).isSelected())
                                        selected.add(new Pair(title,((JCheckBox)itemComp).getText()));
                                } else if(itemComp.getName() != null && itemComp.getName().contains("radio")){
                                    if(((JRadioButton)itemComp).isSelected())
                                        selected.add(new Pair(title,((JRadioButton)itemComp).getText()));
                                } else if(itemComp.getName() != null && itemComp.getName().contains("combobox")){
                                    if(((JComboBox)itemComp).getSelectedIndex() != -1)
                                        selected.add(new Pair(title,((JComboBox)itemComp).getSelectedItem().toString()));
                                }
                            }
                        }
                    }
                }   
            }
        }
        
        return selected;
    }
    
    private void jb_getProfileChecklist(java.awt.event.ActionEvent evt) {
        // We check the mandatory fields are completed.
        
        List<Pair> selectedOptions = this.getAllSelectedOptions();
        
        boolean allFound = true;
        String mandatoryStr = "";
        for (Pair mandatoryField : this.mandatoryFields) {
            mandatoryStr = mandatoryField.getValue().toString();
            boolean found = false;
            for (Pair selectedOption : selectedOptions) {
                if(!mandatoryField.getKey().equals(selectedOption.getKey())) continue;
                found = true;
                break;
            }
            if(found) continue;
            allFound = false;
            break;
        }
        
        
        if(allFound) for(WindowListener wl : this.jf.getWindowListeners()){
            wl.windowClosing(new WindowEvent(jf,0));
        }
        else {
            JOptionPane.showMessageDialog(this.jf, "The \"" + mandatoryStr + "\" field is mandatory");
        }
    }
}
