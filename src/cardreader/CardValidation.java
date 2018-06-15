/*
 * Esta es la clase principal que llama al FrontEnd para que se reproduzca en el
 * main thread 
 */
package cardreader;

import frontend.FrontEnd;
import java.io.IOException;
import javax.swing.JOptionPane;

/**
 *
 * @authors npalavec, Ezequiel Martin Zarza
 */
public class CardValidation {
    public static FrontEnd fe = null;
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        fe = new FrontEnd();
        fe.setVisible(true);
    }   
   
}
