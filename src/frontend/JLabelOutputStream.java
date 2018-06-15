/*
 * Este modulo es un output stream que te guarda todo lo que entra en un JLabel
 * 
 */
package frontend;
 
import java.io.IOException;
import java.io.OutputStream;
 
import javax.swing.JLabel;

/**
 *
 * @authors npalavec, Ezequiel Martin Zarza
 */
public class JLabelOutputStream extends OutputStream {
    final private JLabel label;
    
    
    public JLabelOutputStream(JLabel label) {
        this.label = label;
    }
    
     
    @Override
    public void write(int b) throws IOException {
        label.setText(label.getText()+String.valueOf((char)b));
    }
}
