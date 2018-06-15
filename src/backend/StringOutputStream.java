/*
 * Este modulo te genera un output stream y te lo guarda en un String que luego uno
 * puede devolver
 */
package backend;
 
import java.io.IOException;
import java.io.OutputStream;

/**
 *
 * @authors npalavec, Ezequiel Martin Zarza
 */
public class StringOutputStream extends OutputStream {
    private String str;
     
    public StringOutputStream() {
        this.str = "";
    }
     
    @Override
    public void write(int b) throws IOException {
        this.str += String.valueOf((char)b);
    }
    
    public String getOutput(){ String result = this.str; this.str = ""; return result; }
}
