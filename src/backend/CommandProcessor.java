/*
 *
 * Esta clase es la encargada de manejar las librerias de SCSH3 que es el motor
 * de interpretacion de los comandos escritos en JavaScript.
 * Se personalizo el modulo original para que interprete un archivo externo
 * y texto (String) pasado directamente (sin InputStream)
 *
 *
 * @developmentContinuator 10053892 Ezequiel Martin Zarza
*/
package backend;

import de.cardcontact.scdp.engine.ScriptCompletionListener;
//import de.cardcontact.scdp.engine.CommandProcessor;
import de.cardcontact.scdp.engine.Engine;
import de.cardcontact.scdp.engine.ExecuteRequest;
import de.cardcontact.scdp.engine.FileResourceLocatorFS;
import de.cardcontact.scdp.engine.OCFTracer;
import de.cardcontact.scdp.engine.ScriptExecutor;
import de.cardcontact.scdp.engine.Shell;
import de.cardcontact.scdp.js.GPRuntime;
import de.cardcontact.scdp.js.GPRuntimeHelper;
import de.cardcontact.scdp.js.GPTracer;
import de.cardcontact.smartcardhsmprovider.SmartCardHSMProvider;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.security.Security;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import opencard.core.service.CardServiceException;
import opencard.core.service.SmartCard;
import opencard.core.terminal.CardTerminalException;
import opencard.core.util.OpenCardPropertyLoadingException;
import opencard.core.util.Tracer;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.RhinoException;
import org.mozilla.javascript.Script;

public class CommandProcessor extends Thread implements ScriptCompletionListener {

    
    PrintStream ostream = null;
    PrintStream estream = null;
    //InputStream istream = null;
    private boolean hasInit = false;
    private volatile boolean isExecuting = false;
    private final  boolean automaticExit;
    private boolean streamOutIsWritten = false;
    //private static final long serialVersionUID = -366457048667206297L;
    private final String prompt;
    private static final int RUNNING = 0;
    private static final int QUIT = 1;
    private static final int RESTART = 2;
    public int lifeCycle = RUNNING;
    final private ScriptExecutor se;
    final private Shell shell;
    final private Engine engine;
    
    private int timeout = 0;
    
    // Current Line Number
    private int currentLN = 0;
    
    // I intended to use this for the output, to put the scripts route 
    // if needed, but it also adds it to the "ok" messages. Not going to happen.
    private String name = "script";
    
    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    PipedOutputStream out;
    PipedInputStream in = null;
    
    
    public void resetExecution() {
        
       if(this.se.isAlive()) {
          
           this.se.interrupt();
          
       }
        
    }

    public CommandProcessor(boolean automaticExit, int timeout) throws IOException, RhinoException {
        this.timeout = timeout;
        // Setea el prompt original y el nombre del thread
        this.prompt = "";
        setName("CommandProcessor");
        
        this.automaticExit = automaticExit;
        
        // Inicializa el pipeline que lleva los comandos escritos en out como entrada del Command Processor
        // esto se hizo porque originalmente el CP tenia como entrada la consola y se requeria que tenga como entrada
        // un InputStream generico
        this.out = new PipedOutputStream();
        this.in = new PipedInputStream(this.out);
        
        Security.addProvider(new BouncyCastleProvider());

        // Pone como directorio de trabajo (engine) el lugar de ejecucion del programa
        File userDir = new File(System.getProperty("user.dir"));
        String exeDir = System.getProperty("scsh3.exepath");
        File sysDir;
        if (exeDir != null) {
            sysDir = new File(exeDir);
        } else {
            sysDir = userDir;
        }
        FileResourceLocatorFS frl = new FileResourceLocatorFS(sysDir, userDir);
        this.engine = new Engine();
        this.engine.setFileResourceLocator(frl);
        
        // Ejecuta script de configuracion inicial
        engine.executeSetupScript("config.js");
        
        this.engine.initializeModule();

        // Inicializa el ejecutor de comandos
        this.se = new ScriptExecutor();

        // Inicializa el InputStream con nuestro Pipeline y el Output/Error como la consola
        //this.istream = in;
        //else this.istream = System.in;

        // Inicializa la consola
        this.shell = (Shell) engine.newDynamicScope("Shell");
        
        // Levanta el flag indicando que inicializo
        this.hasInit = true;
        
        // Pone la salida del shell y del command processor por defecto
        this.setOutputStream(System.out);
    }
    
    public boolean hasInit(){ return this.hasInit; }

    @Override
    public void scriptCompleted(Object result) {
        
        
        if ((result instanceof RhinoException)) {
            Engine.printRhinoExceptionMessage((RhinoException) result, this.estream);
        }
        else if (result != Context.getUndefinedValue()) {
            this.shell.put("lastresult", this.shell, result);
            this.ostream.println(Context.toString(result));
        }
        this.ostream.print(this.prompt);
        this.ostream.flush();
        
        // Pone el flag diciendo que termino la ejecucion
        // Only if there's nothing in the QQ.
        if(this.se.getRequestQueue().isEmpty()) this.isExecuting = false;
    }

    @Override
    @SuppressWarnings("SleepWhileInLoop")
    public void run()  {
        if(!this.se.isAlive()) this.se.start();
        this.isExecuting = true;
        do {
            // Checkea si se reseteo.
            if(this.lifeCycle == RESTART) this.lifeCycle = RUNNING;
            GPRuntime gpr = GPRuntimeHelper.getGPRuntime(this.shell);
            OCFTracer ocfTracer = new OCFTracer(gpr.getTracer());
            
            try {   
                Tracer.addTracerListener(ocfTracer);
                SmartCard.start();
                
            }
            catch(OpenCardPropertyLoadingException | CardServiceException | CardTerminalException | ClassNotFoundException e) {
                e.printStackTrace(System.out);
            }
            try {
                // Aca hace el proceso de los comandos
                process();
            }
            catch(IOException | InterruptedException e) {
                e.printStackTrace(System.out);
            }
            finally {
                SmartCardHSMProvider.removeProviders();
                try {
                    SmartCard.shutdown();
                    System.setProperty("OpenCard.loaderClassName", "");
                }
                catch(CardTerminalException e) {
                    System.setProperty("OpenCard.loaderClassName", "");
                    e.printStackTrace(System.out);
                }
                Tracer.removeTracerListener(ocfTracer);
            }
        } while ( this.lifeCycle == RESTART );
        
        // Espera a que ScriptExecutor termine de ejecutar
        // ya que este llama al listener scriptCompleted en este objeto
        while(this.isExecuting) { 
            try {
                System.out.println("CP Waiting");
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                Logger.getLogger(CommandProcessor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        se.shutdown();
    }

    public void executeScript(String scriptLocation) {  
        
        // Borra todos los errores anteriores
        //this.estream.flush();
        this.currentLN = 0;
        this.name = scriptLocation;
        if(scriptLocation != null){
            File f = new File(scriptLocation);
            if(f.exists() && f.canRead()){
                String text="";
                try (BufferedReader br = new BufferedReader(new FileReader(f))) {
                    String line;
                    
                    while((line=br.readLine()) != null)
                        text+=line+'\n';
                    br.close();
                    
                    // Pone todos los comandos del script en el Stream para que sean ejecutados
                    this.out.write((text+'\n').getBytes(StandardCharsets.UTF_8));
                    this.engine.getTracer().trace("Ejecutando script", GPTracer.LogLevel.DEBUG, scriptLocation);
                    if(this.automaticExit)
                        this.streamOutIsWritten = true;
                } catch (IOException ex) {
                    Logger.getLogger(CommandProcessor.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                this.abortExecution("File not found or can't read (" + scriptLocation + ")");
            }
        }
    }
    
    public void executeText(String text){
        this.currentLN = 0;
        this.name = "script";
        System.out.println("antes del try");
       
        
        try {
            
            this.out.write(text.getBytes(StandardCharsets.UTF_8));
            System.out.println(text);
            if(this.automaticExit){
                this.streamOutIsWritten = true;
            }
            this.out.flush();
            
            
        } catch(IOException ex) {
            Logger.getLogger(CommandProcessor.class.getName()).log(Level.SEVERE, null, ex);
            
        }
    }
    
    public void closePipedOutputStream(){
        try{
        this.in.close();
        }catch (IOException ex){Logger.getLogger(CommandProcessor.class.getName()).log(Level.SEVERE, null, ex);}
    }
    
    
    public void abortExecution(String reason){
        this.se.abort();
        this.ostream.print("[Abort]\n"/* + this.prompt*/);
        this.ostream.flush();
        if(reason != null) this.scriptCompleted("ERROR: " + reason);
        else this.scriptCompleted("ERROR: Aborted");
        this.lifeCycle = QUIT;        
    }
    
    public void abortExecution(){
        this.abortExecution(null);
    }
    
    public boolean isExecuting(){ return this.isExecuting; }
    
    final public void setOutputStream(PrintStream out) {
        this.ostream = out;
        this.estream = out;
        if(this.shell != null) 
            this.shell.setOutputStream(out);
            
    }
    
    public void setErrorOutputStream(PrintStream eout) {
        this.estream = eout;
    }
    
    public void setTraceOutputStream(PrintStream out){
        if(this.engine != null) 
            this.engine.setTracePrintStream(out);
    }
    
    //**************** FUNCIONES PRIVADAS ***********************//
    
    private int process() throws IOException,InterruptedException {
        Context cx = Context.enter();
        
        //To the future person in charge of fixing this:
        //This code has MANY obsolete parts which are leftovers of an iterative 
        // console this had.
        //I haven't had time to track and delete them, so I left them here, 
        // unless they were generating bugs.
        //Sorry I did so without a repository, but they gave me this in the
        // same condition.
        //Good luck.
        //PD: Mozilla Rhino's documentation is hard to find, but it's there
        // somewhere. It uses Javascript 1.7 (October 2006), so forget about 
        // JSON, Promises, or anything fancy.

        BufferedReader buffIn = new BufferedReader(new InputStreamReader(this.in));

        // Este es el banner que dice la informacion del shell
        //this.ostream.print(VersionInfo.getBanner());
        //this.ostream.print(this.prompt);
        this.ostream.flush();
        
        try {
            do {
                String command = "";
                try {
                    String line;
                    int nLines = 0;
                    do {
                        line = null;
                        try {
                            if(buffIn.ready() || !this.automaticExit) {
                                line = buffIn.readLine();
                                nLines++;
                            } else if(this.streamOutIsWritten)
                                this.lifeCycle = QUIT;
                        }
                        catch(IOException e) {
                            this.lifeCycle = QUIT;
                        }
                        if (line == null) {
                            if(!this.automaticExit || this.streamOutIsWritten)
                                this.lifeCycle = QUIT;
                        } else {
                            command = command + line + "\n";
                        }
                    } while (( this.lifeCycle != QUIT ) && (!cx.stringIsCompilableUnit(command)) );
                    this.ostream.flush();

                    if ((command.equalsIgnoreCase("quit\n")) || (command.equalsIgnoreCase("q\n"))) {
                        this.lifeCycle = QUIT;
                    }
                    else if (command.equalsIgnoreCase("restart\n")) {
                        this.lifeCycle = RESTART;
                    }
                    else if (command.equals(".\n")) {
                        this.se.abort();

                        this.ostream.print("[Abort]" /*+ this.prompt*/);
                        this.ostream.flush();
                    }
                    else if (this.lifeCycle == RUNNING) {
                        if(line != null){
                            Script s = cx.compileString(command, "script", currentLN + 1, null);
                            //Awful version below. The script route is added to each "ok" message.
                            //Script s = cx.compileString(command, this.name, currentLN + 1, null);
                            currentLN += nLines;
                            //this.se.addRequest();
                            this.isExecuting = true;
                            this.se.getRequestQueue().add(new ExecuteRequest(this.shell, s, null, this));
                        } 
                   }
                }
                catch(RhinoException e) {
                    System.out.println(e.toString());
                    Engine.printRhinoExceptionMessage(e, this.estream);
                    //this.ostream.print(this.prompt);
                    this.ostream.flush();
                    this.isExecuting = false;
                }
                
            } while ( this.lifeCycle == RUNNING );
        }
        finally {
            
            synchronized(this.se){
                int counter = 0;
                while(counter < timeout && this.isExecuting) {
                    this.se.wait(1000);
                    counter++;
                }
                if(counter == timeout) { this.isExecuting = false; this.se.abort(); }
            }
            Context.exit();

            
            this.ostream.flush();
            
            buffIn.close();
            
            // Si no sale automaticamente debe reiniciar
            if(!this.automaticExit){
                this.lifeCycle = RESTART;
                this.in.close();
                this.out.close();
            }
            
            this.isExecuting = false;
        }
        
        // Si no sale automaticamente debe reiniciar
        if(!this.automaticExit)
                this.lifeCycle = RESTART;
            
        return this.lifeCycle;
    }
    
    
    public void restart()  {
        if(this.se.isAlive()) this.se.interrupt();
        this.isExecuting = false;
        
        do {
            // We check if it's restart due to race condition if file don't exists.
            if(this.lifeCycle == RUNNING){ this.lifeCycle = QUIT;System.out.println("forrrro");}
            GPRuntime gpr = GPRuntimeHelper.getGPRuntime(this.shell);
            OCFTracer ocfTracer = new OCFTracer(gpr.getTracer());
            
            
            try {
                // Aca hace el proceso de los comandos
                //process();
                this.in.close();
                this.out.close();
                //this.shell.setOutputStream(this.out);
                //this.shell = new Shell();
                
                
                
            }
            //catch(IOException | InterruptedException e) {
            catch(IOException  e) {
                e.printStackTrace(System.out);
            }
            //finally {
                SmartCardHSMProvider.removeProviders();
                try {
                    
                    SmartCard.shutdown();
                    System.setProperty("OpenCard.loaderClassName", "");
                }
                catch(CardTerminalException e) {
                    System.setProperty("OpenCard.loaderClassName", "");
                    e.printStackTrace(System.out);
                }
                Tracer.removeTracerListener(ocfTracer);
            //}
        } while ( this.lifeCycle == RESTART );
        
        // Espera a que ScriptExecutor termine de ejecutar
        // ya que este llama al listener scriptCompleted en este objeto
        while(!this.isExecuting) { 
            try {
                System.out.println("CP Waiting");
                Thread.sleep(10);
            } catch (InterruptedException ex) {
                Logger.getLogger(CommandProcessor.class.getName()).log(Level.SEVERE, null, ex);
            }
            this.isExecuting=true;
        }
        this.se.shutdown();
    }

    //public void unableResetProfile( javax.swing.JMenuItem jmi_resetProfile) {
    public void disableResetProfile( javax.swing.JMenuItem jmi_resetProfile,JButton bExecuteScriptsButton,
            JButton bResetCardButton,JButton uExecuteScriptsButton,JButton uResetCardButton){
        System.out.println("hola1");
        this.isExecuting=true;
        if(this.isExecuting()){
                System.out.println("hola2");
                //jmi_resetProfile.setEnabled(false);
                //jmi_resetProfile.setToolTipText("Wait until the end of execution to reset.");
                
                jmi_resetProfile.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    if (bExecuteScriptsButton.isEnabled() == false ||
                        bResetCardButton.isEnabled() == false ||
                        uExecuteScriptsButton.isEnabled() == false ||
                        uResetCardButton.isEnabled() == false 
                        ){
                    jmi_resetProfileActionPerformed2(evt,jmi_resetProfile);System.out.println("hola3");}
                }
                });
                
                
                
                
            }
    }

    public void enableResetProfile(JMenuItem jmi_resetProfile) {
        jmi_resetProfile.setEnabled(true);
        System.out.println("holaEnable");
        this.isExecuting = false;
        
    }
    
    private void jmi_resetProfileActionPerformed2(java.awt.event.ActionEvent evt, javax.swing.JMenuItem jmi_resetProfile){
        //JFrame frame = new JFrame("Warning");
        JOptionPane.showMessageDialog(null,"Wait until the end of execution to reset","Warning",JOptionPane.WARNING_MESSAGE);
        //jmi_resetProfile.setEnabled(false);
        jmi_resetProfile.setToolTipText("Wait until the end of execution to reset.");
    }

    
}