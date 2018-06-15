/*
 * Este modulo es el FrontEnd que tiene toda la configuracion grafica del programa
 * y se comunica con otras partes graficas y no graficas para tener toda la logica
 * del programa
 */

package frontend;

import backend.StringOutputStream;
import backend.APDUrepository;
import backend.Validations;
import backend.SmartCardReader;
import backend.CommandProcessor; 
import backend.ConfigurationFile;
import backend.PDFReport;
import backend.ScriptExecution;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.DocumentException;
import de.cardcontact.scdp.scsh3.ReaderDialog;
import java.awt.Component;
import java.util.List;
import javafx.util.Pair;
import java.awt.Toolkit;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.ImageIcon;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileNameExtensionFilter;
import com.itextpdf.text.Image;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.PopupMenu;
import java.time.LocalDateTime;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

/**
 *
 * @authors npalavec, Ezequiel Martin Zarza
 */
public class FrontEnd extends javax.swing.JFrame {
    
    final private FrontEnd frame = this;
    //******************* PESTANA COMMANDS ***********************
    // Objeto que lee y escribe en la tarjeta mediante el lector seleccionado
    final private SmartCardReader cardReader;
    
    // Objeto que contiene todos los apdus incluidos en el archivo correspondiente
    final private APDUrepository apdu; // No se sabe si se va a usar
    //************************************************************
    
    // Objetos que leen de los archivos externos para determinar la logica de las VALIDACIONES
    final private Validations validation;         // Objeto que define todas las validaciones, scripts, etc (Backend)
    private ValidationChecklist checklist;  // Parte grafica del checklist de las validaciones
    private ProfileSelector profileSelector;// Pop-up que permite seleccion de perfil
    
     private CommandProcessor cp;
    
    // Contenedor de los botones.
    private JPanel bottomButtonPanel;
    private JPanel upperButtonPanel;
    // Botones que ejecutan los scripts
    private JButton bExecuteScriptsButton;
    private JButton uExecuteScriptsButton;
    // Botones de reset de tarjeta.
    private JButton bResetCardButton;
    private JButton uResetCardButton;
    
    // Stream de resultados en scripts
    private StringOutputStream results;
    
    // Sentencias para los resultados de los scripts
    private String throw_statement="";
    private String ok_statement="";
    private String na_statement="";
    private String warning_statement="";
    private String error_statement="";
    
    private int timeout = 30;
    
    private Parameters parameters;
    
    // Enum que define la accion en el selector de archivos
    private enum FCaction {SAVE,OPEN};
    
    /**
     * Creates new form frontEnd
     * @throws java.io.IOException
     */
    public FrontEnd() throws IOException {
        
        // Inicializa el interprete de las salidas de los scripts
        HashMap<String,String> conf;
        try {
            //System.out.println(System.getProperty("user.dir")+"\\interpreter.conf");
            conf = new ConfigurationFile().read(System.getProperty("user.dir")+"\\interpreter.conf");
            this.throw_statement = conf.get("throw_statement");
            this.ok_statement = conf.get("ok_statement");
            this.na_statement = conf.get("na_statement");
            this.warning_statement = conf.get("warning_statement");
            this.error_statement = conf.get("error_statement");  
        } catch (IOException ex) {
            Logger.getLogger(FrontEnd.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        // Trata de poner las partes graficas con estilo de Windows
        try { 
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
            //UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            Logger.getLogger(FrontEnd.class.getName()).log(Level.SEVERE, null, ex);
        }
        // Inicializa lector de tarjetas
        this.cardReader = new SmartCardReader();
        
        // Inicializa componentes visuales de NetBeans
        initComponents();
        
        // Inicializa el procesador de comandos
        this.cp = new CommandProcessor(false, this.timeout);
        cp.start();
        
        // Lee archivos externos
        this.apdu = new APDUrepository(System.getProperty("user.dir")+"\\customAPDUs.xml"); // Lee los comandos 
        this.validation = new Validations(this,System.getProperty("user.dir")+"\\profileValidation.xml"); // Lee los datos de las validaciones
        
        // Agrega los APDUs al ComboBox
        this.apdu.getAll().forEach((apduTag) -> { this.jcb_apdu.addItem(apduTag); });
        
        // Agrega la norma del comando inicial
        jl_apduNorm.setText(this.apdu.getNorm(0));
        
        // Pone la fecha predeterminada
        jdc_fecha.setDate(new Date());
        
        // Agranda la ventana al maximo
        this.setLocationRelativeTo(null);
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jToolBar1 = new javax.swing.JToolBar();
        jMenuItem2 = new javax.swing.JMenuItem();
        jPanel3 = new javax.swing.JPanel();
        jSeparator1 = new javax.swing.JSeparator();
        jPanel9 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jt_project = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jt_executed = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jdc_fecha = new com.toedter.calendar.JDateChooser();
        jPanel10 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jp_checklistTab = new javax.swing.JPanel();
        jsp_profileOptions = new javax.swing.JScrollPane();
        jp_profileOptions = new javax.swing.JPanel();
        jp_checklist = new javax.swing.JPanel();
        jb_setProfile = new javax.swing.JButton();
        
        jb_setParameters = new javax.swing.JButton();
        
        jl_cardATR = new javax.swing.JLabel();
        jp_scriptsTab = new javax.swing.JPanel();
        jSplitPane1 = new javax.swing.JSplitPane();
        jPanel1 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jb_loadScript = new javax.swing.JButton();
        jb_runScript = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jta_script = new javax.swing.JTextArea();
        jPanel6 = new javax.swing.JPanel();
        jb_clearScript = new javax.swing.JButton();
        jb_clearVars = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jb_clearConsole = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        jta_scriptPrint = new javax.swing.JTextArea();
        jp_tracerTab = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        jb_clearConsole1 = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        jta_tracer = new javax.swing.JTextArea();
        jp_commandsTab = new javax.swing.JPanel();
        jp_cardReader = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jcb_readers = new javax.swing.JComboBox<>();
        jb_refresh = new javax.swing.JButton();
        jb_atr = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jl_atr = new javax.swing.JLabel();
        jp_manualCmd = new javax.swing.JPanel();
        jb_sendAPDU = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jcb_apdu = new javax.swing.JComboBox<>();
        jcb_apduOption = new javax.swing.JComboBox<>();
        jl_apduNorm = new javax.swing.JLabel();
        jl_response = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        
        //jMenuBar2 = new javax.swing.JMenuBar();
        jMenuBar2 = new jMenuBars();
        
        jMenu1 = new javax.swing.JMenu();
        jmi_exportPDF = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        jmi_quit = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        jmi_resetProfile = new javax.swing.JMenuItem();
        jMenu3 = new javax.swing.JMenu();
        jmi_run = new javax.swing.JMenuItem();
        jMenuItem1 = new javax.swing.JMenuItem();
        jmi_selectReader = new javax.swing.JMenuItem();
        
        
        jta_textFieldATR = new javax.swing.JTextArea();
        jta_textFieldATR.setPreferredSize(new Dimension(60, 1));
        
        
        
        jcb_selectReaderCB = new javax.swing.JComboBox();
        this.setCardReaderOptions();
        
        JButton jb_getATR = new javax.swing.JButton();
        
        jMenuItem3 = new javax.swing.JMenuItem();

        jToolBar1.setRollover(true);

        jMenuItem2.setText("jMenuItem2");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("AUTO VTP");
        setBackground(new java.awt.Color(248, 248, 248));
        setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/images/automatic-flash.png"))
        );
        getContentPane().setLayout(new javax.swing.BoxLayout(getContentPane(), javax.swing.BoxLayout.Y_AXIS));

        jPanel3.setBackground(new java.awt.Color(248, 248, 248));
        jPanel3.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jPanel3.setMaximumSize(new java.awt.Dimension(2147483647, 500));

        jPanel9.setBackground(new java.awt.Color(248, 248, 248));
        jPanel9.setLocation(15, 15);

        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel4.setText("Project");

        jt_project.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jt_projectActionPerformed(evt);
            }
        });

        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel5.setText("Executed by");

        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel6.setText("Date");

        jdc_fecha.setPreferredSize(new java.awt.Dimension(6, 20));

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addComponent(jt_executed, javax.swing.GroupLayout.PREFERRED_SIZE, 168, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jdc_fecha, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jt_project, javax.swing.GroupLayout.PREFERRED_SIZE, 401, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel9Layout.createSequentialGroup()
                .addContainerGap(20, Short.MAX_VALUE)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jt_project, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jt_executed, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jdc_fecha, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jPanel10.setBackground(new java.awt.Color(248, 248, 248));

        ImageIcon imageIcon = new ImageIcon(new ImageIcon(getClass().getResource("/frontend/logo-gemalto-340x120.gif")).getImage().getScaledInstance(170, 60,0));
        jLabel7.setIcon(imageIcon); 

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addGap(46, 46, 46)
                .addComponent(jLabel7)
                .addContainerGap(20, Short.MAX_VALUE))
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 87, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 1097, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(340, 340, 340)))
                .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(18, Short.MAX_VALUE))
        );

        getContentPane().add(jPanel3);

        jTabbedPane1.setBackground(new java.awt.Color(248, 248, 248));
        jTabbedPane1.setMinimumSize(new java.awt.Dimension(925, 335));

        jp_checklistTab.setLayout(new java.awt.BorderLayout());

        jp_profileOptions.setBackground(new java.awt.Color(248, 248, 248));

        jp_checklist.setLayout(new javax.swing.BoxLayout(jp_checklist, javax.swing.BoxLayout.PAGE_AXIS));
        jp_profileOptions.add(jp_checklist);

        jb_setProfile.setText("Set Profile");
        jb_setProfile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jb_setProfileActionPerformed(evt);
                
            }
        });
        jp_profileOptions.add(jb_setProfile);
        
        ///////
        jb_setParameters.setText("Set Parameters");
        jb_setParameters.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt2) {
                jb_setParametersActionPerformed(evt2);
            }
        });
        jp_profileOptions.add(jb_setParameters);

        
        
        

        jsp_profileOptions.setViewportView(jp_profileOptions);

        jp_checklistTab.add(jsp_profileOptions, java.awt.BorderLayout.CENTER);

        jl_cardATR.setText(" ");
        jl_cardATR.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jl_cardATRMouseReleased(evt);
            }
        });
        jp_checklistTab.add(jl_cardATR, java.awt.BorderLayout.PAGE_END);

        jTabbedPane1.addTab("VTP", new ImageIcon(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/images/payment-terminal.png")).getScaledInstance(30,30,0)), jp_checklistTab);

        jp_scriptsTab.setLayout(new java.awt.BorderLayout());

        jSplitPane1.setDividerLocation(300);
        jSplitPane1.setDividerSize(10);
        jSplitPane1.setOneTouchExpandable(true);

        jPanel1.setLayout(new javax.swing.BoxLayout(jPanel1, javax.swing.BoxLayout.Y_AXIS));

        jPanel4.setMaximumSize(new java.awt.Dimension(32767, 100));
        jPanel4.setMinimumSize(new java.awt.Dimension(300, 30));
        jPanel4.setPreferredSize(new java.awt.Dimension(300, 30));
        jPanel4.setLayout(new javax.swing.BoxLayout(jPanel4, javax.swing.BoxLayout.LINE_AXIS));

        jb_loadScript.setText("Load Script");
        jb_loadScript.setMargin(new java.awt.Insets(10, 10, 10, 10));
        jb_loadScript.setMaximumSize(new java.awt.Dimension(300, 30));
        jb_loadScript.setPreferredSize(new java.awt.Dimension(150, 30));
        jb_loadScript.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jb_loadScriptActionPerformed(evt);
            }
        });
        jPanel4.add(jb_loadScript);

        jb_runScript.setText("Run Script");
        jb_runScript.setMargin(new java.awt.Insets(10, 10, 10, 10));
        jb_runScript.setMaximumSize(new java.awt.Dimension(300, 30));
        jb_runScript.setPreferredSize(new java.awt.Dimension(150, 30));
        jb_runScript.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jb_runScriptActionPerformed(evt);
            }
        });
        jPanel4.add(jb_runScript);

        jPanel1.add(jPanel4);

        jScrollPane1.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        jta_script.setBackground(new java.awt.Color(248, 248, 248));
        jta_script.setColumns(1);
        jta_script.setFont(new java.awt.Font("Monospaced", 0, 12)); // NOI18N
        jta_script.setRows(5);
        jta_script.setDragEnabled(true);
        jScrollPane1.setViewportView(jta_script);

        jPanel1.add(jScrollPane1);

        jPanel6.setLayout(new javax.swing.BoxLayout(jPanel6, javax.swing.BoxLayout.X_AXIS));

        jb_clearScript.setText("Clear Script");
        jb_clearScript.setMargin(new java.awt.Insets(10, 10, 10, 10));
        jb_clearScript.setMaximumSize(new java.awt.Dimension(300, 30));
        jb_clearScript.setMinimumSize(new java.awt.Dimension(77, 30));
        jb_clearScript.setPreferredSize(new java.awt.Dimension(150, 30));
        jb_clearScript.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jb_clearScriptActionPerformed(evt);
            }
        });
        jPanel6.add(jb_clearScript);

        jb_clearVars.setText("Clear Variables (Not Yet)");
        jb_clearVars.setMargin(new java.awt.Insets(10, 10, 10, 10));
        jb_clearVars.setMaximumSize(new java.awt.Dimension(300, 30));
        jb_clearVars.setMinimumSize(new java.awt.Dimension(77, 30));
        jb_clearVars.setPreferredSize(new java.awt.Dimension(150, 30));
        jb_clearVars.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jb_clearVarsActionPerformed(evt);
            }
        });
        /*
        jPanel6.add(jb_clearVars);
        */

        jPanel1.add(jPanel6);

        jSplitPane1.setLeftComponent(jPanel1);

        jPanel2.setAlignmentX(0.0F);
        jPanel2.setLayout(new javax.swing.BoxLayout(jPanel2, javax.swing.BoxLayout.Y_AXIS));

        jPanel5.setMaximumSize(new java.awt.Dimension(321321, 50));
        jPanel5.setMinimumSize(new java.awt.Dimension(73, 30));
        jPanel5.setPreferredSize(new java.awt.Dimension(150, 30));
        jPanel5.setRequestFocusEnabled(false);
        jPanel5.setLayout(new javax.swing.BoxLayout(jPanel5, javax.swing.BoxLayout.LINE_AXIS));

        jb_clearConsole.setLabel("Clear Console");
        jb_clearConsole.setMargin(new java.awt.Insets(10, 10, 10, 10));
        jb_clearConsole.setMaximumSize(new java.awt.Dimension(9999, 30));
        jb_clearConsole.setMinimumSize(new java.awt.Dimension(73, 39));
        jb_clearConsole.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jb_clearConsoleActionPerformed(evt);
            }
        });
        jPanel5.add(jb_clearConsole);

        jPanel2.add(jPanel5);

        jScrollPane2.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        jta_scriptPrint.setEditable(false);
        jta_scriptPrint.setBackground(new java.awt.Color(248, 248, 248));
        jta_scriptPrint.setColumns(1);
        
        Font font = new Font("Consolas", Font.PLAIN, 16);
              
        jta_scriptPrint.setFont(font); // NOI18N
        jta_scriptPrint.setRows(5);
        jScrollPane2.setViewportView(jta_scriptPrint);

        jPanel2.add(jScrollPane2);

        jSplitPane1.setRightComponent(jPanel2);

        jp_scriptsTab.add(jSplitPane1, java.awt.BorderLayout.CENTER);

        jTabbedPane1.addTab("Scripts", new ImageIcon(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/images/script!.png")).getScaledInstance(30,30,0)), jp_scriptsTab);

        jp_tracerTab.setLayout(new java.awt.BorderLayout());

        jPanel7.setAlignmentX(0.0F);
        jPanel7.setLayout(new javax.swing.BoxLayout(jPanel7, javax.swing.BoxLayout.Y_AXIS));

        jPanel8.setLayout(new javax.swing.BoxLayout(jPanel8, javax.swing.BoxLayout.LINE_AXIS));

        jb_clearConsole1.setText("Clear Tracer");
        jb_clearConsole1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jb_clearConsole1.setMargin(new java.awt.Insets(10, 10, 10, 10));
        jb_clearConsole1.setMaximumSize(new java.awt.Dimension(214524545, 30));
        jb_clearConsole1.setMinimumSize(new java.awt.Dimension(73, 30));
        jb_clearConsole1.setName(""); // NOI18N
        jb_clearConsole1.setPreferredSize(new java.awt.Dimension(500, 30));
        jb_clearConsole1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jb_clearConsole1ActionPerformed(evt);
            }
        });
        jPanel8.add(jb_clearConsole1);

        jPanel7.add(jPanel8);

        jta_tracer.setEditable(false);
        jta_tracer.setBackground(new java.awt.Color(248, 248, 248));
        jta_tracer.setColumns(20);
        jta_tracer.setRows(5);
        
        
        jta_tracer.setFont(font);
        jScrollPane3.setViewportView(jta_tracer);

        jPanel7.add(jScrollPane3);

        jp_tracerTab.add(jPanel7, java.awt.BorderLayout.CENTER);

        jTabbedPane1.addTab("Tracer", new ImageIcon(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/images/tracer.png")).getScaledInstance(30,30,0)), jp_tracerTab);

        jp_commandsTab.setBackground(new java.awt.Color(248, 248, 248));
        jp_commandsTab.setEnabled(false);
        jp_commandsTab.setFocusable(false);
        jp_commandsTab.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        jp_cardReader.setBorder(javax.swing.BorderFactory.createTitledBorder("Reader"));
        java.awt.GridBagLayout jp_cardReaderLayout = new java.awt.GridBagLayout();
        jp_cardReaderLayout.columnWidths = new int[] {100, 100, 100, 100};
        jp_cardReader.setLayout(jp_cardReaderLayout);

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel1.setText("Readers");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 3);
        jp_cardReader.add(jLabel1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jp_cardReader.add(jcb_readers, gridBagConstraints);

        jb_refresh.setText("Refresh");
        jb_refresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jb_refreshActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jp_cardReader.add(jb_refresh, gridBagConstraints);

        jb_atr.setText("send ATR");
        jb_atr.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jb_atrActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 8;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jp_cardReader.add(jb_atr, gridBagConstraints);

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel2.setText("ATR");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jp_cardReader.add(jLabel2, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jp_cardReader.add(jl_atr, gridBagConstraints);

        jp_commandsTab.add(jp_cardReader);

        jp_manualCmd.setBorder(javax.swing.BorderFactory.createTitledBorder("Manual APDUs"));
        java.awt.GridBagLayout jp_manualCmdLayout = new java.awt.GridBagLayout();
        jp_manualCmdLayout.columnWidths = new int[] {70, 70, 70, 70, 70, 70, 70, 70};
        jp_manualCmd.setLayout(jp_manualCmdLayout);

        jb_sendAPDU.setText("send APDU");
        jb_sendAPDU.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jb_sendAPDUActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jp_manualCmd.add(jb_sendAPDU, gridBagConstraints);

        jLabel3.setText("APDU");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 7);
        jp_manualCmd.add(jLabel3, gridBagConstraints);

        jcb_apdu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jcb_apduActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jp_manualCmd.add(jcb_apdu, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jp_manualCmd.add(jcb_apduOption, gridBagConstraints);

        jl_apduNorm.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jl_apduNorm.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 3));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jp_manualCmd.add(jl_apduNorm, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jp_manualCmd.add(jl_response, gridBagConstraints);

        jp_commandsTab.add(jp_manualCmd);

        /* Commented so it's invisible. Too much work to just delete it.
        Parts may be used in the future.
        If you want to discomment, go to the navigator, select this element (commandsTab),
        and use "Customize Code". Delete this comment, leaving the uneditable
        line there.

        jTabbedPane1.addTab("Commands [Deprecated]", jp_commandsTab);
        */

        getContentPane().add(jTabbedPane1);
        jTabbedPane1.getAccessibleContext().setAccessibleName("Commands");

        jMenu1.setText("File");

        jmi_exportPDF.setText("Export PDF");
        jmi_exportPDF.setToolTipText("");
        jmi_exportPDF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmi_exportPDFActionPerformed(evt);
            }
        });
        jMenu1.add(jmi_exportPDF);
        jMenu1.add(jSeparator2);

        jmi_quit.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Q, java.awt.event.InputEvent.CTRL_MASK));
        jmi_quit.setLabel("Quit");
        jmi_quit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmi_quitActionPerformed(evt);
            }
        });
        jMenu1.add(jmi_quit);

        jMenuBar1.add(jMenu1);

        jMenu2.setText("Edit");
        
        jmi_resetProfile.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_R, java.awt.event.InputEvent.CTRL_MASK));
        jmi_resetProfile.setLabel("Reset Profile Selection");
        
        
        
        
        jmi_resetProfile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
            
            if (bExecuteScriptsButton.isEnabled() == true &&
        bResetCardButton.isEnabled() == true &&
        uExecuteScriptsButton.isEnabled() == true &&
        uResetCardButton.isEnabled() == true ){//if (!cp.isExecuting()){   
            jmi_resetProfileActionPerformed(evt);
            } //else {jmi_resetProfileActionPerformed(evt); }
            }
            
        });
        jMenu2.add(jmi_resetProfile);

        jMenuBar1.add(jMenu2);

        jMenu3.setText("Actions");

        jmi_run.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F5, 0));
        jmi_run.setText("Run");
        jmi_run.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmi_runActionPerformed(evt);
            }
        });
        //jMenu3.add(jmi_run);

        jMenuItem1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_K, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem1.setText("Clear Console");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuItem1);

        jmi_selectReader.setText("Select Reader");
        jmi_selectReader.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmi_selectReaderActionPerformed(evt);
            }
        });
        //jMenu3.add(jmi_selectReader);
        
        
        
        jcb_selectReaderCB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Object selected = jcb_selectReaderCB.getSelectedItem();
                String readerElejido = selected.toString();
                System.out.println(readerElejido.getClass());
                jcb_selectReaderComboBoxActionPerformed(evt, readerElejido);
                
            }
        });
        
        //jMenu3.add(jcb_selectReaderCB);
        
        jMenuItem3.setText("Set Timeout");
        jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem3ActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuItem3);

        jMenuBar1.add(jMenu3);

        setJMenuBar(jMenuBar1);
        
        //jb_getATR.setText("Get ATR");
        try{
            jb_getATR.setIcon(new ImageIcon(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/images/sim-card.png")) ));//.getScaledInstance(20,20,0)));
            jb_getATR.setToolTipText("Get ATR");
        }catch (Exception ex){}
        jb_getATR.addActionListener(new java.awt.event.ActionListener(){
            public void actionPerformed(java.awt.event.ActionEvent evt){
                resetCardAndATR();
            }
        });
        
        //jMenuBar2.add(jMenu3);
        //getContentPane().add(jTabbedPane2);
        //setJMenuBar(jMenuBar2);
        jMenuBar2.makeUI(this,jMenu1,jMenu2,jMenu3, jcb_selectReaderCB, jta_textFieldATR, jb_getATR);

        pack();
        
        
    
        
    }// </editor-fold>//GEN-END:initComponents

    private void jb_atrActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jb_atrActionPerformed
        // TODO add your handling code here:
        cardReader.connectToTerminal(jcb_readers.getSelectedIndex());
        jl_atr.setText(cardReader.getATR());
    }//GEN-LAST:event_jb_atrActionPerformed

    private void jb_refreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jb_refreshActionPerformed
        // TODO add your handling code here:
        List<String> namesList = this.cardReader.getAvailableTerminals();
        jcb_readers.removeAllItems();
        namesList.forEach((name) -> {
            jcb_readers.addItem(name);
            System.out.println(jcb_readers);
        });
    }//GEN-LAST:event_jb_refreshActionPerformed
    
    
    
    
    public void setCardReaderOptions(){
        List<String> namesList = this.cardReader.getAvailableTerminals();
        jcb_readers.removeAllItems();
        //jcb_selectReaderCB.removeAllItems();
        namesList.forEach((name) -> {
            jcb_readers.addItem(name);
            jcb_selectReaderCB.addItem(name);
            //System.out.println(jcb_readers);
        });
    }
    
    
    
    
    

    private void jb_sendAPDUActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jb_sendAPDUActionPerformed
        // TODO add your handling code here:
        int apduSelected, optionSelected;
        if( (apduSelected = jcb_apdu.getSelectedIndex()) != -1 && (optionSelected = jcb_apduOption.getSelectedIndex()) != -1 )
            cardReader.sendAPDU(apdu.get(apduSelected),apdu.getOption(apduSelected, optionSelected));
        
        // Pone la respuesta en el front
        jl_response.setText(cardReader.getResponseString());
    }//GEN-LAST:event_jb_sendAPDUActionPerformed

    private void jcb_apduActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jcb_apduActionPerformed
        // TODO add your handling code here:
        int selected;
        if((selected = jcb_apdu.getSelectedIndex()) != -1){
            jcb_apduOption.removeAllItems();
            this.apdu.getAllOptions(selected).forEach((apduOption) -> {
                jcb_apduOption.addItem(apduOption);
            });
            jl_apduNorm.setText(this.apdu.getNorm(selected));
        }
    }//GEN-LAST:event_jcb_apduActionPerformed

    private void jb_setProfileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jb_setProfileActionPerformed
        // TODO add your handling code here:
        
        // Define el pop-up que permitira al usuario elegir el perfil
        this.setEnabled(false);
        JFrame jf = new JFrame();
        JFrame jf2 = new JFrame();
        this.profileSelector = new ProfileSelector(this,jf,this.validation,jf2);
        
        // Define las funciones a ser llamadas una vez que se cierra la ventana
        // de eleccion del perfil
        jf.addWindowListener(new WindowListener() {
            @Override
            public void windowClosing(WindowEvent e) {
                //If id != 0, then it's not the button's event.
                if(e.getID() == 0) {
                    profileWindowClosing();
                }
                //e.getWindow().setVisible(false);
                frame.setEnabled(true);
                e.getWindow().dispose();
            }
            @Override public void windowOpened(WindowEvent e) {}
            @Override public void windowClosed(WindowEvent e) {}
            @Override public void windowIconified(WindowEvent e) {}
            @Override public void windowDeiconified(WindowEvent e) {}
            @Override public void windowActivated(WindowEvent e) {}
            @Override public void windowDeactivated(WindowEvent e) {}
        });
        
        this.profileSelector.popUp();
        
    }//GEN-LAST:event_jb_setProfileActionPerformed

    
    private void jb_setParametersActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jb_setParametersActionPerformed
        // TODO add your handling code here:
        
        // Define el pop-up que permitira al usuario elegir el perfil
        /*
        this.setEnabled(false);
        JFrame jf = new JFrame();
        JFrame jf2 = new JFrame();
        //this.ParameterSetter = new ParameterSetter(this,jf,this.validation,jf2);
        
        // Define las funciones a ser llamadas una vez que se cierra la ventana
        // de eleccion del perfil
        jf.addWindowListener(new WindowListener() {
            @Override
            public void windowClosing(WindowEvent e) {
                //If id != 0, then it's not the button's event.
                if(e.getID() == 0) {
                    profileWindowClosing();
                }
                //e.getWindow().setVisible(false);
                frame.setEnabled(true);
                e.getWindow().dispose();
            }
            @Override public void windowOpened(WindowEvent e) {}
            @Override public void windowClosed(WindowEvent e) {}
            @Override public void windowIconified(WindowEvent e) {}
            @Override public void windowDeiconified(WindowEvent e) {}
            @Override public void windowActivated(WindowEvent e) {}
            @Override public void windowDeactivated(WindowEvent e) {}
        });
        
        //this.ParameterSetter.popUp();
        */
        this.parameters = new Parameters();
        //this.fileChooser.createUI();
        try{
            this.parameters.openFile();
        }catch (IOException e){};
        try{
            this.parameters.closeFile(this.parameters.openFile());
        }catch (IOException e){};
        
        
    }//GEN-LAST:event_jb_setParametersActionPerformed

    
    
    
    private void jb_loadScriptActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jb_loadScriptActionPerformed
        // TODO add your handling code here:
        String path = selectDir(FCaction.OPEN, "Scripts JS", "js");
        
        if(path != null){
            File f = new File(path);
            if(f.exists() && f.canRead()){
                try {
                    BufferedReader br = new BufferedReader(new FileReader(f));
                    jta_script.setText("");
                    String line;
                    while((line=br.readLine()) != null)
                        jta_script.append(line+'\n');
                } catch (IOException ex) {
                    Logger.getLogger(FrontEnd.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }//GEN-LAST:event_jb_loadScriptActionPerformed

    private void jb_clearScriptActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jb_clearScriptActionPerformed
        // TODO add your handling code here:
        jta_script.setText("");
    }//GEN-LAST:event_jb_clearScriptActionPerformed

    private void jb_runScriptActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jb_runScriptActionPerformed
        // TODO add your handling code here:
        
        // Asigna la salida el text area correspondiente
        this.cp.setOutputStream(new PrintStream(new JTextAreaOutputStream(this.jta_scriptPrint)));
        this.cp.setTraceOutputStream(new PrintStream(new JTextAreaOutputStream(this.jta_tracer)));
        
        // Reproduce el script
        String text = this.jta_script.getText();
        this.cp.executeText( text != null ? text+'\n' : ""); 
    }//GEN-LAST:event_jb_runScriptActionPerformed

    private void jmi_quitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmi_quitActionPerformed
        // TODO add your handling code here:
        System.exit(0); 
    }//GEN-LAST:event_jmi_quitActionPerformed

    private void jmi_resetProfileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmi_resetProfileActionPerformed
        // TODO add your handling code here:
        this.jb_setProfile.setVisible(true);
        this.jb_setParameters.setVisible(true);
        this.jp_checklist.removeAll();
        //aca va la funcion que me resetea el engine
        
        /*try{
        this.cp = new CommandProcessor(false, this.timeout);
        }catch (IOException e){};
        */
        jta_scriptPrint.setText("");
        this.jta_tracer.setText("");
        
        //Para resetear la muestra en pantalla del art
        
        this.jl_cardATR.setText("");
        this.jta_textFieldATR.setText("");
        //PrintStream ps = new PrintStream(new JLabelOutputStream(this.jl_cardATR));
        //this.cp.setOutputStream(ps);
        //this.cp.setErrorOutputStream(ps);
        //this.cp.setTraceOutputStream(new PrintStream(new JTextAreaOutputStream(this.jta_tracer)));
        //ps.flush();
        
        this.cp.executeText("");
        //this.cp.closePipedOutputStream();
        //this.cp.executeText("var card = new Card(_scsh3.reader);\nvar atr = card.reset(Card.RESET_COLD);\nprint(\"RAW: \"+atr.toByteString().toString()+\" \\n   \");\nprint(atr.toString().split('\\n').join(' \\n'));\n");
        
        
        
    }//GEN-LAST:event_jmi_resetProfileActionPerformed

    private void jmi_runActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmi_runActionPerformed
        // TODO add your handling code here:
        jb_runScriptActionPerformed(evt);
    }//GEN-LAST:event_jmi_runActionPerformed

    private void jmi_selectReaderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmi_selectReaderActionPerformed
        // TODO add your handling code here:
        System.out.println("selecciono Reader");
        ReaderDialog readerDlg = new ReaderDialog(this, true);
        readerDlg.setVisible(true);
        String selection = readerDlg.getSelection();
        readerDlg.dispose();
        String command = "_scsh3.setProperty(\"reader\",\"" + selection + "\");";
        
        // Ejecuta el comando
        this.cp.executeText(command);
    }//GEN-LAST:event_jmi_selectReaderActionPerformed
    
    
    private void jcb_selectReaderComboBoxActionPerformed(java.awt.event.ActionEvent evt, String selected) {//GEN-FIRST:event_jmi_selectReaderActionPerformed
        
        String command = "_scsh3.setProperty(\"reader\",\"" + selected + "\");";
        
        // Ejecuta el comando
        this.cp.executeText(command);
    }//GEN-LAST:event_jmi_selectReaderActionPerformed
    

    private void jb_clearConsoleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jb_clearConsoleActionPerformed
        // TODO add your handling code here:
        jta_scriptPrint.setText("");
    }//GEN-LAST:event_jb_clearConsoleActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        // TODO add your handling code here:
        jb_clearConsoleActionPerformed(evt);
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void jl_cardATRMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jl_cardATRMouseReleased
        // TODO add your handling code here:
        
        // Crea un frame con el ATR dentro y lo muestra en pantalla
        
        this.setEnabled(false);
        JFrame ATRframe = new JFrame("ATR");
        ATRframe.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/images/icon.png")));
        JTextArea jta = new JTextArea(this.jl_cardATR.getText());
        jta.setEditable(false);
        ATRframe.add(jta);
        ATRframe.pack();
        ATRframe.setLocationRelativeTo(this);
        ATRframe.setVisible(true);
        ATRframe.setAlwaysOnTop(true);
        ATRframe.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {}

            @Override
            public void windowClosing(WindowEvent e) {
                frame.setEnabled(true);
            }

            @Override
            public void windowClosed(WindowEvent e) {}

            @Override
            public void windowIconified(WindowEvent e) {}

            @Override
            public void windowDeiconified(WindowEvent e) {}

            @Override
            public void windowActivated(WindowEvent e) {}

            @Override
            public void windowDeactivated(WindowEvent e) {}
        });
        
        
        
    }//GEN-LAST:event_jl_cardATRMouseReleased

    private void jmi_exportPDFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmi_exportPDFActionPerformed
        // Esta linea crea la sugerencia de nombre. Los replace en la fecha son para evitar ":" y "." en el nombre del archivo. (: no está permitido, "." puede interpretarse como extención).
        String fileNameSuggestion = jt_project.getText() + " - " + LocalDateTime.now().toString().replace(":","-").replace(".","-")+".pdf";

        
        String path = selectDir(FCaction.SAVE,"PDF Document","pdf",fileNameSuggestion);
        try {
            createPDF(path);
        } catch (ResultEmpty e) {
            if(JOptionPane.showConfirmDialog(this, "Hay pruebas sin resultado. El informe estará incompleto. ¿Aún así desea continuar?", "Confirmación", JOptionPane.YES_NO_OPTION) == 0) createPDF(path, true);
            else return;
        }
    }//GEN-LAST:event_jmi_exportPDFActionPerformed

    private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed
        // TODO add your handling code here:
        String s = JOptionPane.showInputDialog("Set Timeout, in seconds for each script",Integer.toString(this.timeout));
        if("".equals(s)) return;
        try {
            this.timeout = Integer.parseInt(s);
            this.cp.setTimeout(this.timeout);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Not a number");
        }
    }//GEN-LAST:event_jMenuItem3ActionPerformed

    private void jb_clearVarsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jb_clearVarsActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jb_clearVarsActionPerformed

    private void jb_clearConsole1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jb_clearConsole1ActionPerformed
        this.jta_tracer.setText("");
    }//GEN-LAST:event_jb_clearConsole1ActionPerformed

    private void jt_projectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jt_projectActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jt_projectActionPerformed
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(FrontEnd.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            try {
                FrontEnd fe = new FrontEnd();
                fe.setVisible(true);
                fe.cp.start();
            } catch (IOException ex) {
                Logger.getLogger(FrontEnd.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenuBar jMenuBar1;
    
    //private javax.swing.JMenuBar jMenuBar2;
    private jMenuBars jMenuBar2;
    
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JButton jb_atr;
    private javax.swing.JButton jb_clearConsole;
    private javax.swing.JButton jb_clearConsole1;
    private javax.swing.JButton jb_clearScript;
    private javax.swing.JButton jb_clearVars;
    private javax.swing.JButton jb_loadScript;
    private javax.swing.JButton jb_refresh;
    private javax.swing.JButton jb_runScript;
    private javax.swing.JButton jb_sendAPDU;
    private javax.swing.JButton jb_setProfile;
    
    private javax.swing.JButton jb_setParameters;
    
    private javax.swing.JComboBox<String> jcb_apdu;
    private javax.swing.JComboBox<String> jcb_apduOption;
    private javax.swing.JComboBox<String> jcb_readers;
    private com.toedter.calendar.JDateChooser jdc_fecha;
    private javax.swing.JLabel jl_apduNorm;
    private javax.swing.JLabel jl_atr;
    private javax.swing.JLabel jl_cardATR;
    private javax.swing.JLabel jl_response;
    private javax.swing.JMenuItem jmi_exportPDF;
    private javax.swing.JMenuItem jmi_quit;
    private javax.swing.JMenuItem jmi_resetProfile;
    private javax.swing.JMenuItem jmi_run;
    private javax.swing.JMenuItem jmi_selectReader;
    

    private JComboBox jcb_selectReaderCB;
    private JTextArea jta_textFieldATR;
    
    
    private javax.swing.JPanel jp_cardReader;
    private javax.swing.JPanel jp_checklist;
    private javax.swing.JPanel jp_checklistTab;
    private javax.swing.JPanel jp_commandsTab;
    private javax.swing.JPanel jp_manualCmd;
    private javax.swing.JPanel jp_profileOptions;
    private javax.swing.JPanel jp_scriptsTab;
    private javax.swing.JPanel jp_tracerTab;
    private javax.swing.JScrollPane jsp_profileOptions;
    private javax.swing.JTextField jt_executed;
    private javax.swing.JTextField jt_project;
    private javax.swing.JTextArea jta_script;
    private javax.swing.JTextArea jta_scriptPrint;
    private javax.swing.JTextArea jta_tracer;
    // End of variables declaration//GEN-END:variables
       
    //**************************************************************************
    private void executeValidationScripts(){        
        // Devuelve la lista de las validaciones
        List<Pair> validations = this.validation.getCheckListScripts(this.profileSelector.getAllSelectedOptions());
        
        // Ejecuta el thread para los scripts
        runValidationThread(validations);
        //cp.enableResetProfile(jmi_resetProfile);
    }
    
    public void executeSingleScript(Pair validation){    
        List<Pair> validations = new ArrayList<>();
        validations.add(validation);
        
        // Ejecuta el thread para los scripts
        runValidationThread(validations);
        
    }
    
    // Esta funcion define un thread que es el que va a ejecutar el script
    // Se le pasa el nombre de la validacion y su correspondiente script (location)
    // Ademas se le pasa un objeto que tiene todas las funciones de callback
    // para que no se acceda a las variables de FrontEnd directamente
    private void runValidationThread(List<Pair> validationList){
        
        // Desactiva las actividades en el boton de ejecucion de scripts
        this.bExecuteScriptsButton.setEnabled(false);
        this.bResetCardButton.setEnabled(false);
        this.uExecuteScriptsButton.setEnabled(false);
        this.uResetCardButton.setEnabled(false);
        this.checklist.disablePanel();
        
        // Crea un thread para ejecutar los scripts de validaciones sin bloquear el FrontEnd
        // Dentro del thread crea la funcion de callback con la interface y la definicion de la funcion
        Thread t;
        t = new Thread(new ScriptExecution(validationList, new ScriptExecution.Callback() {
            @Override
            public void enableExecution(){ enableExecuteScriptsButton(); }
            @Override
            public String getResult() { return getResultInTest(); }
            @Override
            public String getThrowString() { return getThrowStatement(); }
            @Override
            public String getNAString() { return getNAStatement(); }
            @Override
            public String getOkString() { return getOkStatement(); }
            @Override
            public String getWarningString() { return getWarningStatement(); }
            @Override
            public String getErrorString() { return getErrorStatement(); }
            @Override
            public void setResult(String testName, ValidationChecklist.TestStatus result, String comment){ setResultInTest(testName,result,comment); }
            
            @Override
            public CommandProcessor initialize(){
                try {
                    CommandProcessor cp = initializeCommandProcessor();
                    initializeAutomaticScriptStreams(cp);
                    cp.start();
                    return cp;
                } catch (IOException ex) {
                    Logger.getLogger(FrontEnd.class.getName()).log(Level.SEVERE, null, ex);
                }
                return null;
            }   
        }));
        t.start();
        
    }

    //**************************************************************************
    // Define la funcion de callback de la ejecucion de los scripts
    // Esto es porque se le restringe a ScriptExecution acceder a las
    // variables que tiene FrontEnd
    private void enableExecuteScriptsButton(){ 
        this.bExecuteScriptsButton.setEnabled(true);
        this.bResetCardButton.setEnabled(true);
        this.uExecuteScriptsButton.setEnabled(true);
        this.uResetCardButton.setEnabled(true);
        this.checklist.enablePanel();
        
        //
        jmi_resetProfile.setEnabled(true);
        cp.enableResetProfile(jmi_resetProfile);
    }
    private String getResultInTest(){ return this.results.getOutput(); }
    private String getThrowStatement(){ return this.throw_statement; }
    private String getOkStatement(){ return this.ok_statement; }
    private String getNAStatement(){ return this.na_statement; }
    private String getWarningStatement(){ return this.warning_statement; }
    private String getErrorStatement(){ return this.error_statement; }
    private void setResultInTest(String testName, ValidationChecklist.TestStatus result, String comment){
        this.checklist.setResultInTest(testName, result, comment);
    }
    private CommandProcessor initializeCommandProcessor() throws IOException{
        // Inicializa el Command Processor para correr scripts de validaciones
        return new CommandProcessor(true, this.timeout);
    }
    private void initializeAutomaticScriptStreams(CommandProcessor cp){
        cp.setOutputStream(new PrintStream(new JTextAreaOutputStream(this.jta_scriptPrint)));
        cp.setErrorOutputStream(new PrintStream(this.results = new StringOutputStream()));
        cp.setTraceOutputStream(new PrintStream(new JTextAreaOutputStream(this.jta_tracer)));
    }
    //**************************************************************************
    
    // Esta funcion se hizo ya que windowClosing no tiene acceso a this (FrontEnd)
    private void profileWindowClosing(){
        this.jb_setParameters.setVisible(false);//una vez seteado el profile se va el boton set parameters
        // Agrega el titulo con las preferencias del perfil
        this.jp_checklist.add(this.profileSelector.returnProfileJPanel());
        
        // Agrega el panel de control superior
        this.upperButtonPanel = new JPanel();
                
        // Agrega el boton de ejecucion        
        this.uExecuteScriptsButton = new JButton("Execute All Scripts");
        this.uExecuteScriptsButton.addActionListener((java.awt.event.ActionEvent evt) -> {
            cp.disableResetProfile( jmi_resetProfile, bExecuteScriptsButton,
                            bResetCardButton, uExecuteScriptsButton, uResetCardButton);
            executeValidationScripts();
            //cp.enableResetProfile( jmi_resetProfile);
        });
        this.upperButtonPanel.add(uExecuteScriptsButton);
        
        // Agrega el boton de reset
        this.uResetCardButton = new JButton("Reset Card");
        this.uResetCardButton.addActionListener((java.awt.event.ActionEvent evt) -> {
            resetCardAndATR();
        });
        this.upperButtonPanel.add(this.uResetCardButton);
        
        this.jp_checklist.add(this.upperButtonPanel);
        
        // Agrega la lista de validaciones
        List<Pair> validations = this.validation.getCheckListDescription(this.profileSelector.getAllSelectedOptions());
        List<Pair> scripts = this.validation.getCheckListScripts(this.profileSelector.getAllSelectedOptions());
        this.checklist = new ValidationChecklist(validations,scripts,this);
        this.jp_checklist.add(this.checklist.returnChecklistPanel());
        
        // Agrega el panel de control inferior
        this.bottomButtonPanel = new JPanel();
                
        // Agrega el boton de ejecucion        
        this.bExecuteScriptsButton = new JButton("Execute All Scripts");
        this.bExecuteScriptsButton.addActionListener((java.awt.event.ActionEvent evt) -> {
            executeValidationScripts();
        });
        this.bottomButtonPanel.add(bExecuteScriptsButton);
        
        // Agrega el boton de reset
        this.bResetCardButton = new JButton("Reset Card");
        this.bResetCardButton.addActionListener((java.awt.event.ActionEvent evt) -> {
            resetCardAndATR();
        });
        this.bottomButtonPanel.add(this.bResetCardButton);
        
        this.jp_checklist.add(this.bottomButtonPanel);
        
        // Hace invisible el boton de eleccion de perfil
        this.jb_setProfile.setVisible(false);
        this.jp_checklist.repaint();
    }
    
    // Funcion que resetea la tarjeta y recibe el ATR poniendolo en un cuadro
    // en la parte inferior del Front
    private void resetCardAndATR(){
        
        // Borra el string en donde va el ATR e inicializa el Stream
        
        this.jl_cardATR.setText("");
        this.jta_textFieldATR.setText("");
        PrintStream ps = new PrintStream(new JLabelOutputStream(this.jl_cardATR));
        PrintStream ps2 = new PrintStream(new JTextAreaOutputStream(this.jta_textFieldATR));
        this.cp.setOutputStream(ps);
        this.cp.setErrorOutputStream(ps);
        
        this.cp.setOutputStream(ps2);
        this.cp.setErrorOutputStream(ps2);
        
        this.cp.setTraceOutputStream(new PrintStream(new JTextAreaOutputStream(this.jta_tracer)));

        // Ejecuta el ATR
        System.out.println("antes del executeText");
        this.cp.executeText("var card = new Card(_scsh3.reader);\nvar atr = card.reset(Card.RESET_COLD);\nprint(\"\"+atr.toByteString().toString()+\" \\n   \");\nprint(atr.toString().split('\\n').join(' \\n'));\n");
        
        ps.flush();

    }
    
    private String selectDir(FCaction action,String namesExt,String ext) {
        return selectDir(action,namesExt,ext,null);
    }
    
    // Funcion que abre un Frame para que el usuario elija un archivo
    private String selectDir(FCaction action,String namesExt,String ext, String defaultFilename) {
        JFileChooser fileChooser = new JFileChooser(System.getProperty("user.dir"));
        String pathAndName = null;
        if(defaultFilename != null) fileChooser.setSelectedFile(new File(defaultFilename));
        if( action == FCaction.SAVE )
        {
            fileChooser.setFileFilter(new FileNameExtensionFilter(namesExt,ext));
            int returnValue = fileChooser.showSaveDialog(null);

            if (returnValue == JFileChooser.APPROVE_OPTION) {
                // Usuario selecciona archivo
                File selectedFile = fileChooser.getSelectedFile();
                try {
                    pathAndName = selectedFile.getAbsolutePath();
                    if (!pathAndName.endsWith("."+ext)) {
                        pathAndName += "."+ext;
                    }
                } catch (SecurityException e) {
                    System.err.println("Security Exception: " + e.getMessage());
                }
            }
        } else if( action == FCaction.OPEN ) {
            fileChooser.setFileFilter(new FileNameExtensionFilter(namesExt,ext));
            int returnValue = fileChooser.showOpenDialog(null);

            if (returnValue == JFileChooser.APPROVE_OPTION) {
                // Usuario selecciona archivo
                File selectedFile = fileChooser.getSelectedFile();
                try {
                    pathAndName = selectedFile.getAbsolutePath();
                    if (!pathAndName.endsWith("."+ext)) {
                        pathAndName += "."+ext;
                    }
                } catch (SecurityException e) {
                    System.err.println("Security Exception: " + e.getMessage());
                }
            }
        }

        return pathAndName;
    }
    
    private class ResultEmpty extends Exception {};
    
    private void createPDF(String pathAndName) throws ResultEmpty {
        if(!createPDF(pathAndName, false)) throw new ResultEmpty();
    }
    
    // Funcion que saca los datos del FrontEnd para crear el PDF de reporte
    private boolean createPDF(String pathAndName, boolean ignoreEmpty) {
        JPanel resultsPanel = null;
        if(this.checklist != null) resultsPanel = this.checklist.returnChecklistPanel();
        
        if(resultsPanel != null){
                        
            List<String> profile = new ArrayList<>();
            List<String> tests = new ArrayList<>();
            List<Image> resultsImages = new ArrayList<>();
            
            try {
                resultsImages.add(Image.getInstance(this.getClass().getResource("/images/void.png")));
                resultsImages.add(Image.getInstance(this.getClass().getResource("/images/tick.png")));
                resultsImages.add(Image.getInstance(this.getClass().getResource("/images/exclamation.png")));
                //Image warning = Image.getInstance(this.getClass().getResource("/images/warning.png"));
                //warning.scaleToFit(12,12);                
                //resultsImages.add(warning);
                resultsImages.add(Image.getInstance(this.getClass().getResource("/images/error.png")));
                resultsImages.add(Image.getInstance(this.getClass().getResource("/images/na.png")));            
            } catch (BadElementException | IOException ex) {
                Logger.getLogger(FrontEnd.class.getName()).log(Level.SEVERE, null, ex);
            }
            List<Integer> resultsIndexes = new ArrayList<>();
            List<String> comments = new ArrayList<>();
            
            int i = 0;
            for(Component comp : resultsPanel.getComponents()){
                if(comp.getClass() == JLabel.class){
                    if(i%5 == 0 && i>=5){ tests.add(((JLabel)comp).getText()); }
                }

                if(comp.getClass() == JComboBox.class){
                    if((i-1)%5 == 0 && i>=5){
                            int index = ((JComboBox)comp).getSelectedIndex();
                            if(!ignoreEmpty && index == 0) return false;
                            resultsIndexes.add(index);
                            //globalResult = globalResult | (1 << ((JComboBox)comp).getSelectedIndex());
                            /*switch(index){
                                case 0:
                                default:
                                    if(ignoreEmpty) resultsImages.add(Image.getInstance(this.getClass().getResource("/images/void.png")));
                                    else return false;
                                    break;
                                case 1:
                                    resultsImages.add(Image.getInstance(this.getClass().getResource("/images/tick.png")));
                                    break;
                                case 2:
                                    resultsImages.add(Image.getInstance(this.getClass().getResource("/images/exclamation.png")));
                                    break;
                                case 3:
                                    resultsImages.add(Image.getInstance(this.getClass().getResource("/images/error.png")));
                                    break;
                                case 4:
                                    resultsImages.add(Image.getInstance(this.getClass().getResource("/images/na.png")));
                                    break;
                            }*/

                    }
                }

                if(comp.getClass() == JTextArea.class){
                    if((i-2)%5 == 0 && i>=5){ comments.add(((JTextArea)comp).getText()); }
                }

                // Aumenta el indice
                i++;
            }
            
            Date myDate = this.jdc_fecha.getDate();
            String dateFormat = new SimpleDateFormat("EEEE dd MMMM, yyyy",Locale.ENGLISH).format(myDate);
            
            // Verifica si existe un perfil dentro del panel
            /*List<Pair> profileSelection = new ArrayList<>();
            if(this.profileSelector != null) profileSelection = this.profileSelector.getAllSelectedOptions();*/
            
            // Saca todas las opciones elegidas para ponerlo en el reporte
            List<Pair> titles = this.profileSelector.getTitleOfSelectedOptions();
            

            titles.forEach((selection) -> {
                profile.add(selection.getKey().toString() + ": " + selection.getValue().toString());
            });
                

            try {
                PDFReport pdfReport = new PDFReport(pathAndName, this.jt_project.getText(), this.jt_executed.getText(), dateFormat, profile, tests, resultsImages, comments, resultsIndexes);
            } catch (FileNotFoundException ex) {
                JOptionPane.showMessageDialog(this, "El archivo que intenta guardar está siendo usado por otro programa o algún error está ocurriendo", "Error en la escritura del archivo", JOptionPane.WARNING_MESSAGE);
                Logger.getLogger(FrontEnd.class.getName()).log(Level.SEVERE, null, ex);
            } catch (DocumentException ex) {
                Logger.getLogger(FrontEnd.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return true;
    }    
    
    
}