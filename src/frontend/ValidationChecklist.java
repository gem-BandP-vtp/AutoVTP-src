/*
 *  Esta clase es la encargada de estructurar graficamente la lista de validaciones.
 *  Se le pasa como parametro una lista de pares de Strings con el nombre del test
 *  y su descripcion. Devuelve con su funcion correspondiente el JPanel.
 *
 *  This class is responsible for structuring the list of validations graphically.
 *  You are passed as a parameter a list of pairs of Strings with the name of the test
 *  and its description. Returns the JPanel calling its corresponding function.
 */

package frontend;

import java.awt.Color;
import java.awt.GridLayout;
import javax.swing.JPanel;
import java.util.List;
import javafx.util.Pair;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.HashMap;
import javax.swing.ComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.ListCellRenderer;
import javax.swing.event.ListDataListener;
import layout.TableLayout;

/**
 *
 * @authors npalavec, Ezequiel Martin Zarza
 */
public class ValidationChecklist {
    
    // Panel principal
    final private JPanel jp;
    
    // Imagenes
    private ImageIcon[] images;
    private Color bgColor;
    
    // Variables auxiliares para reproducir los scripts individualmente
    private FrontEnd fe;
    private HashMap<String, String> testAndScripts;
    
    public enum TestStatus{NA,OK,WARNING,ERROR};
    
    @SuppressWarnings("null")
    public ValidationChecklist(List<Pair> testAndDescription, List<Pair> testAndScript, FrontEnd fe){
        this.fe = fe;
        this.testAndScripts = new HashMap<>();
        
        // Define las imagenes
        images = new ImageIcon[]{
            new ImageIcon(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/images/void.png")).getScaledInstance(30,30,0)),
            new ImageIcon(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/images/tick.png")).getScaledInstance(30,30,0)),
            new ImageIcon(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/images/exclamation.png")).getScaledInstance(30,30,0)),
            new ImageIcon(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/images/error.png")).getScaledInstance(30,30,0)),
            new ImageIcon(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/images/na.png")).getScaledInstance(30,30,0))
        };
        
        bgColor = new Color(190,190,190,Color.TRANSLUCENT);
        
        // Define el renderizador a utilizar en JComboBox
        ListCellRenderer renderer = (ListCellRenderer) (JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) -> {
           
            JLabel label = new JLabel();
            if(index!=-1) label.setOpaque(true);
            //label.setText(value.toString());
            
            // Pone la imagen correspondiente
            if(index != -1 && index < images.length)
                label.setIcon(images[index]);
            else if(list.getSelectedIndex() >= 0 && list.getSelectedIndex() < images.length )
                label.setIcon(images[list.getSelectedIndex()]);
            
            // Colorea el fondo si esta seleccionada
            if (isSelected)
                label.setBackground(bgColor);
            
            return label;
        };
        
        // Va a ser una "tabla" con cinco columnas
        
        // Originalmente se usó un "GridLayout". El GridLayout obliga a las 
        // columnas de ser del mismo ancho, así que lo cambiaré.
        this.jp = new JPanel(new GridBagLayout());

        this.jp.setBorder(new EmptyBorder(5, 5, 5, 5));
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.fill = GridBagConstraints.BOTH;
        // Agrega el header
        String[] headerNames = new String[]{"Test Name","Result","Comments","Description","Execute"};
        for(String headerName : headerNames){
            JLabel headerItem = new JLabel(headerName);
            headerItem.setBackground(Color.gray);
            headerItem.setOpaque(true);
            headerItem.setForeground(Color.white);
            this.jp.add(headerItem,c);
            c.gridx++;
        }
        testAndDescription.forEach((data) -> {
            c.gridy ++;
            c.gridx = 0;
            boolean singleScriptEnable = false;
            
            // Crea el combobox con las opciones
            
            /* ERROR AQUÍ!
            Al abrir la lista, pasar el mouse por encima de una opción, y 
            luego perder el foco sin hacer click (Por acción externa o saliendo
            de la lista y haciendo click en otro lugar), se actualiza el display
            del elemento pero no se actualiza el getSelectedIndex().
            
            Esto provoca errores en el PDF generado, o cualquier otro lugar
            donde se llame "getSelectedIndex".
            
            Esto también ocurre cuando un ejecutor de Scripts termina de
            ejecutar y actualiza el resultado de algún script.
            
            Por ahora, desactivaré el cambiar resultados de pruebas sin scripts
            cuando hay scripts ejecutandose, pero es algo para arreglar.
            
            Lo siento, no encontré suficiente información del problema.
            
            (Se detectó unas horas antes de irme de Gemalto).
            */
            JComboBox jcb = new JComboBox();
            
            jcb.setEditable(false);
            
            // Agrega tres elementos para que se muestren las imagenes
            jcb.addItem("");
            jcb.addItem("ok");
            jcb.addItem("wa");
            jcb.addItem("er");
            jcb.addItem("na");
            
            // Define el renderer en el JComboBox
            jcb.setRenderer(renderer);
            
            // Si lo encuentra dentro de los test que tienen script no deja modificar el combobox
            Pair<String,String> myTestAndScript = null;
            for(Pair test : testAndScript){
                if(!test.getValue().toString().equals("")){
                    if( test.getKey().toString().equals(data.getKey().toString()) ){
                        // Setea los enable de los componentes
                        jcb.setEnabled(false);
                        singleScriptEnable = true; // Este enable se hace mas abajo cuando el objeto esta creado
                        
                        // Agrega el test a la lista de Tests
                        myTestAndScript = test;
                        this.testAndScripts.put(test.getKey().toString(), test.getValue().toString());
                    }
                }
            }
            
            
            // Crea boton de ejecucion de script
            JButton singleScriptButton = new JButton("",new ImageIcon(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/images/scriptPlayer.png")).getScaledInstance(35,35,0)));
            if(singleScriptEnable){
                singleScriptButton.addActionListener(this::callExecution);
                singleScriptButton.setName(myTestAndScript.getKey());
            }
            singleScriptButton.setEnabled(singleScriptEnable);

            // Agrega los cuatro campos de cada fila
            this.jp.add(new JLabel(data.getKey().toString().replace("\n","<br>")),c);
            c.gridx++;
            this.jp.add(jcb,c);
            c.gridx++;
            this.jp.add(new JTextArea(""),c);
            c.gridx++;
            this.jp.add(new JLabel(data.getValue().toString()),c);
            c.gridx++;
            this.jp.add(singleScriptButton,c);
        });
        
        for(Component comp : this.jp.getComponents()){
            if(comp.getClass() == JLabel.class)
                ((JLabel)comp).setBorder(new CompoundBorder(new LineBorder(Color.gray), new EmptyBorder(5,10,5,10)));
            else if(comp.getClass() == JComboBox.class)
                ((JComboBox)comp).setBorder(new CompoundBorder(new LineBorder(Color.gray), new EmptyBorder(5,10,5,10)));
            else if(comp.getClass() == JTextField.class)
                ((JTextField)comp).setBorder(new CompoundBorder(new LineBorder(Color.gray), new EmptyBorder(5,10,5,10)));
            else if(comp.getClass() == JTextArea.class)
                ((JTextArea)comp).setBorder(new CompoundBorder(new LineBorder(Color.gray), new EmptyBorder(5,10,5,10)));
        }
    }
    
    public JPanel returnChecklistPanel(){ return this.jp; }
    
    public void setResultInTest(String testName, TestStatus status, String comment){
        int i = 0;
        boolean testNameFound = false;
        for(Component myComp : this.jp.getComponents()){
            if(testNameFound && myComp.getClass() == JComboBox.class){
                JComboBox myComboBox = (JComboBox)myComp;
                
                switch(status){
                    case NA:
                        myComboBox.setSelectedIndex(4);
                        break;
                    case OK:
                        myComboBox.setSelectedIndex(1);
                        break;
                    case WARNING:
                        myComboBox.setSelectedIndex(2);
                        break;
                    case ERROR:
                    default:
                        myComboBox.setSelectedIndex(3);
                        break;
                }
                
                myComboBox.requestFocus();
                myComboBox.requestFocusInWindow();
            }
            
            // Pone el comentario en el JTextArea de la validacion
            if(testNameFound && myComp.getClass() == JTextArea.class){
                ((JTextArea)myComp).setText(comment);
                testNameFound = false;
            }
            
            if(myComp.getClass() == JLabel.class){
                if(i%5 == 0 && i>=5){
                    if(((JLabel)myComp).getText().trim().equals(testName.trim()))
                        testNameFound = true;  
                }
            }
            i++;
        }
    }
    
    public void enablePanel(){ this.jp.setEnabled(true); this.jp.enableInputMethods(true); }
    public void disablePanel(){ this.jp.setEnabled(false); this.jp.enableInputMethods(false); }
    
    /********************** FUNCIONES PRIVADAS *****************************/
    
    private void callExecution(ActionEvent e){
        String name = null;
        if(e.getSource().getClass() == JButton.class)
            name = ((JButton)e.getSource()).getName();
        this.fe.executeSingleScript(new Pair(name,this.testAndScripts.get(name)));
    }
}
