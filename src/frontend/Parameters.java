/*
 * Este modulo es para buscar el archivo Parameters.js y editarlo desde el programa
 * 
 */
package frontend;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
/**
 *
 * @author Ezequiel Martin Zarza
 */

public class Parameters {
    
    
    public static void ParametersMain(String[] args) throws Exception{
        
            
        
    };
    
    //void openFile() throws IOException{
    Process openFile() throws IOException{
        Runtime rt = Runtime.getRuntime();
        
        
        String file = System.getProperty("user.dir")+"\\Payment Profile Scripts\\05 - Support\\PARAMETERS.js";
                                
        
        Process process = rt.exec("Notepad "+file);
        //process.exitValue();
        return process;
    
    }
    
    
    public void closeFile(Process process) throws IOException {
    //in.close() ;
    Runtime rt = Runtime.getRuntime();
        
    process.destroy();
    System.out.println("file closed") ;
        } // end closeFile
     // end class
    
    
       // EventQueue.invokeLater(r);
   // }
       /*
    void createUI() {
        JFrame frame = new JFrame();
        frame.setLayout(new BorderLayout());
        JPanel panel = new JPanel();
        
        
        
        
       
        
        
        
        JButton saveBtn = new JButton("Save");
        JButton openBtn = new JButton("Open");

        saveBtn.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                JFileChooser saveFile = new JFileChooser();
                saveFile.showSaveDialog(null);
            }
        });
        
        
        
        JPanel jPanel4 = new JPanel();
        jPanel4.setMaximumSize(new java.awt.Dimension(32767, 100));
        jPanel4.setMinimumSize(new java.awt.Dimension(300, 30));
        jPanel4.setPreferredSize(new java.awt.Dimension(300, 30));
        jPanel4.setLayout(new javax.swing.BoxLayout(jPanel4, javax.swing.BoxLayout.LINE_AXIS));
        
        
        openBtn.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                JFileChooser openFile = new JFileChooser();
                String pathAndName = null;
                
                
                
                
                String namesExt = "Scripts JS";
                String ext = "js";
                JTextArea jta_script = new javax.swing.JTextArea();
                
                openFile.setFileFilter(new FileNameExtensionFilter(namesExt,ext));
                int option = openFile.showOpenDialog(null);
                if (option == JFileChooser.APPROVE_OPTION){
                    //openFile.getSelectedFile();
                    File selectedFile = openFile.getSelectedFile();
                try {
                    pathAndName = selectedFile.getAbsolutePath();
                    if (!pathAndName.endsWith("."+ext)) {
                        pathAndName += "."+ext;
                    }
                } catch (SecurityException e) {
                    System.err.println("Security Exception: " + e.getMessage());
                    }
                }
                
                // Aca meto la función
               this.openParameterFile(pathAndName);
               /*
               if(pathAndName != null){
                File f = new File(pathAndName);
                if(f.exists() && f.canRead()){
                    try {
                        BufferedReader reader = new BufferedReader(new FileReader(f));
                        jta_script.setText("");
                        String line;
                        while((line=reader.readLine()) != null){
                        jta_script.append(line+'\n');
                        
                        }
                        } catch (IOException ex) {
                            Logger.getLogger(FrontEnd.class.getName()).log(Level.SEVERE, null, ex);
                            }
                    }
                }*/
            /*}

            private List<String> openParameterFile(String pathAndName) {
                List<String> records = new ArrayList<String>();
                if(pathAndName != null){
                //File f = new File(pathAndName);
                
                
                //if(f.exists() && f.canRead()){
                    try {
                        //BufferedReader reader = new BufferedReader(new FileReader(f));
                        BufferedReader reader = new BufferedReader (new FileReader("C:\\Users\\10053892\\Desktop\\Card Reader - v3\\"
                                + "Version 1.0\\CardReader\\Payment Profile's Scripts\\05 - Support\\PARAMETERS.js"));
                        String line;
                       
                        
                        while((line=reader.readLine()) != null){
                        records.add(line);
                        line = reader.readLine();
                        }
                        //reader.close();
                        //return records;
                        } catch (IOException ex) {
                            Logger.getLogger(FrontEnd.class.getName()).log(Level.SEVERE, null, ex);
                            return null;
                            }
                    }
                
                
            return records;

                
            }

        });
        
        
        jPanel4.add(openBtn);
        
         frame.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/images/credit_cards.png")));
        //frame.add( new JPanel(new GridLayout(0, 1)));
        //frame.add(new JPanel(new GridLayout(0, 1)), panel);
        
        frame.setLocationRelativeTo(null);
        
        frame.add(new JLabel("Parameters Chooser"), BorderLayout.NORTH);
        frame.add(saveBtn, BorderLayout.CENTER);
        frame.add(openBtn, BorderLayout.SOUTH);
        frame.setTitle("Parameters Chooser");
        //frame.pack();
        frame.setSize(400,400);
        //panel.setBorder(new EmptyBorder(150,150,150,150));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);// Arreglar esto para que no se cierre todo
        frame.setVisible(true);
        
        
        
        
    }
    
    
    
    */
    
}