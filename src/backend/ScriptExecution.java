/*
 * Esta es una clase que realiza la ejecucion de los script tomando un CommandProcessor.
 * Se le pasa las ubicaciones de los scripts y una interface de Callback para que
 * este modulo le avise al modulo que lo llama cuando termina, etc.
 */
package backend;

import frontend.ValidationChecklist;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.util.Pair;

/**
 *
 * @authors npalavec, Ezequiel Martin Zarza
 */
public class ScriptExecution implements Runnable {
        
    List<Pair> validations; 
    Callback callback;
    public ScriptExecution(List<Pair> validations, Callback mycallback) {
        this.validations = validations; this.callback = mycallback;
    }

    //@SuppressWarnings({"empty-statement", "StringEquality"})
    @Override
    @SuppressWarnings("SleepWhileInLoop")
    public void run() {
        validations.forEach((myValidation) -> {
            //System.out.println("Validation");
            // Asigna la salida el text area correspondiente
            // Reproduce el script
            String location = myValidation.getValue().toString();
            if (!"".equals(location) && location != null) {
                CommandProcessor cp = this.callback.initialize();
                cp.executeScript(location);
                String originalResult = "";
                synchronized(cp) {
                    while(cp.isAlive()) {
                        try {
                            cp.wait(1000);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(ScriptExecution.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        originalResult += this.callback.getResult();
                    }
                }
                // Pone el tick o no en el test correspondiente y su comentario
                if (!originalResult.equals("")) {
                    String result = originalResult;
                    String myThrow = this.callback.getThrowString()+" ";
                    String myNA = this.callback.getNAString();
                    String myOK = this.callback.getOkString();
                    String myWarning = this.callback.getWarningString();
                    String myError = this.callback.getErrorString();
                    
                    // Error mark takes precedence over warning, and warning 
                    // over ok, and ok over Non Applicable, that's why we check 
                    // and set in this order.
                    if(result.contains(myThrow+myNA)){
                        result = result.replace(myThrow+myNA, myNA);
                        this.callback.setResult(myValidation.getKey().toString(),ValidationChecklist.TestStatus.NA,result);
                    }
                    if(result.contains(myThrow+myOK)){
                        result = result.replace(myThrow+myOK, myOK);
                        this.callback.setResult(myValidation.getKey().toString(),ValidationChecklist.TestStatus.OK,result);
                    }
                    if(result.contains(myThrow+myWarning)){
                        result = result.replace(myThrow+myWarning, myWarning);
                        this.callback.setResult(myValidation.getKey().toString(),ValidationChecklist.TestStatus.WARNING,result);
                    }
                    if (result.contains(myThrow+myError)) {
                        result = result.replace(myThrow+myError, myError);
                        this.callback.setResult(myValidation.getKey().toString(),ValidationChecklist.TestStatus.ERROR,result);
                    }    
                }
            }
        });

        // Una vez que termina llama al callback
        this.callback.enableExecution();
    }
    
    // Define la interface que estructura la funcion de callback cuando se terminan
    // de ejecutar los scripts
    public interface Callback{
        public void enableExecution();
        public String getResult();
        public String getThrowString();
        public String getOkString();
        public String getNAString();
        public String getWarningString();
        public String getErrorString();
        public void setResult(String testName, ValidationChecklist.TestStatus result, String comment);
        public CommandProcessor initialize();
    }
}