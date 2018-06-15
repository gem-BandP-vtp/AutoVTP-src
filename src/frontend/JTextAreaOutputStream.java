/*
 * Este modulo es un output stream que te guarda todo lo que entra en un JTextArea
 * 
 */
package frontend;
 
import java.io.IOException;
import java.io.OutputStream;
 
import javax.swing.JTextArea;

/**
 *
 * @authors npalavec, Ezequiel Martin Zarza
 */
public class JTextAreaOutputStream extends OutputStream {
    final private JTextArea textArea;
     
    public JTextAreaOutputStream(JTextArea textArea) {
        this.textArea = textArea;
    }
     
    @Override
    public void write(int b) throws IOException {
        // redirects data to the text area
        textArea.append(String.valueOf((char)b));
        // scrolls the text area to the end of data
        textArea.setCaretPosition(textArea.getDocument().getLength());
        textArea.repaint();
    }
}
