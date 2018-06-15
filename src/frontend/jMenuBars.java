package frontend;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.PopupMenu;
import java.awt.Toolkit;
import javax.swing.*;
import javax.swing.border.Border;
/**
 *
 * @author Ezequiel Martin Zarza
 */
public class jMenuBars extends javax.swing.JMenuBar {
    public static void main(String[] args) {
    SwingUtilities.invokeLater(new Runnable() {
 
      @Override
      public void run() {
        FrontEnd frame = null;
        JMenu menu1 = null, menu2 = null,menu3 = null;
        JComboBox combobox = null;
        JTextArea textField = null;
        JButton button = null;
        
        new jMenuBars().makeUI(frame, menu1, menu2, menu3, combobox, textField, button);
      }
    });
  }
 
  public void makeUI(FrontEnd frame, JMenu menu1, JMenu menu2, JMenu menu3, JComboBox combobox, JTextArea textField, JButton button) {
    JMenuBar outerBar = new JMenuBar();
    outerBar.setLayout(new GridLayout(0, 1));
    JMenu Espacio = new javax.swing.JMenu();
    Espacio.setEnabled(false);
    
    JScrollPane jsp = new JScrollPane(textField);
    
    Espacio.setText("                                                                                       "
            + "                                                                 "
            + "                                   "
            + "                                   "
            + "                                   ");
    
    
    JMenu Espacio2 = new javax.swing.JMenu();
    Espacio2.setEnabled(false);
    //Font font = new Font("monospaced", Font.BOLD, 14);
    //Espacio2.setFont(font);
    Espacio2.setText("Select Reader:        ");
    for (int i = 0; i < 2; i++) {
      JMenuBar innerBar = new JMenuBar();
      innerBar.setBackground(new java.awt.Color(248, 248, 248));
      if (i == 0){
          
          innerBar.add(menu1);
          innerBar.add(menu2);
          innerBar.add(menu3);
      }
      else if (i==1){
          innerBar.add(Espacio2);
          //combobox.setPreferredSize(new Dimension(20,20));
          innerBar.add(combobox, BorderLayout.NORTH);
          textField.setEditable(false);
          
          
          innerBar.add(Espacio);
          //textField.setPreferredSize(new Dimension(25,25));
          this.setTextAreaBorder(textField);
          innerBar.add(textField);
          
          innerBar.add(button);
          
          JMenu esp = new JMenu();
          esp = Espacio;
          innerBar.add(esp);
          //innerBar.add(jsp);
      }
      //JMenu menu = new JMenu("Menu " + i);
      //menu.add(new JMenuItem("Item " + i));
      //JMenuBar innerBar = new JMenuBar();
      //innerBar.add(menu);
      outerBar.add(innerBar);
    }
 
    //JFrame frame = new JFrame();
    //Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    //frame.setSize(screenSize.width/2, screenSize.height/2);
    frame.setJMenuBar(outerBar);
 
    //frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    //frame.setSize(400, 400);
    //frame.setLocationRelativeTo(null);
    //frame.setVisible(true);
  }
  
  public void setTextAreaBorder(JTextArea textField){
      Border border = BorderFactory.createLineBorder(Color.BLACK);

      textField.setSize(400, 200);
      textField.setBorder(border);
      textField.setEditable(false);
      textField.setFont(new Font("Verdana", Font.BOLD, 12));
      add(textField);
  }

}
