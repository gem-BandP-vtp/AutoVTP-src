/*
 * Esta clase es una interface para el facil uso del lector de tarjetas provisto por
 * Java (javax.smartcardio).
 * Es parte de la pestaña que actualmente esta en (deprecated)
 */

package backend;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.smartcardio.*;
import java.util.ArrayList;

/**
 *
 * @authors npalavec, Ezequiel Martin Zarza
 */


public class SmartCardReader {   
    
    // Variables correspondientes a la conexion
    TerminalFactory factory;
    List<CardTerminal> terminalsList = null;
    Card card;
    
    // Variables correspondientes a la ultima comunicacion
    byte[] statusWord;
    byte[] cardResponse;

    public SmartCardReader(){

            // show the list of available terminals
            this.factory = TerminalFactory.getDefault();
            
            // creates array of statusWord
            statusWord = new byte[2];
    }
    
    public List<String> getAvailableTerminals(){
        try {
            this.factory = TerminalFactory.getDefault();
            this.terminalsList = this.factory.terminals().list();
            
            List<String> terminalsNames = new ArrayList<>();
        
            if( terminalsList != null )
                terminalsList.forEach((terminal) -> { terminalsNames.add(terminal.getName()); });

            return terminalsNames;
        } catch (CardException ex) {
            Logger.getLogger(SmartCardReader.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return new ArrayList<>();
    }
    
    public void connectToTerminal(int number){
        try {
            CardTerminal terminal = terminalsList.get(number);
            this.card = terminal.connect("*");
        } catch (CardException ex) {
            Logger.getLogger(SmartCardReader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void disconnectToTerminal(){
        try {
            this.card.disconnect(false);
        } catch (CardException ex) {
            Logger.getLogger(SmartCardReader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public String getATR(){
        return "   " + byteArrayToHexString( this.card.getATR().getBytes(), " ");
    }
    
    public void sendAPDU(String apduHeader, String apduData) {
        CardChannel channel = this.card.getBasicChannel();
        
        // Convierte los paquetes de String a Bytes
        List<Byte> command = apduToBytesList(apduHeader);
        List<Byte> data = apduToBytesList(apduData);
        data.add(0,(byte)data.size());
        
        // Arma el paquete de Bytes
        command.addAll(data);
        byte[] commandBytes = new byte[command.size()];
        for(int i=0; i < command.size(); i++)
            commandBytes[i] = command.get(i);
            
        // Manda el comando
        ResponseAPDU respApdu = null;
        try {
            respApdu = channel.transmit(new CommandAPDU(commandBytes));
        } catch (CardException ex) {
            Logger.getLogger(SmartCardReader.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        // Guarda todo lo relacionado a la respuesta del comando mandado
        if( respApdu != null ){
            
            // Guarda el status word
            this.statusWord[0] = (byte)respApdu.getSW1();
            this.statusWord[1] = (byte)respApdu.getSW2();
            
            // Guarda la respuesta del comando si fue correcto
            if(this.statusWord[0] == 0x90 && this.statusWord[1] == 0x00)
                this.cardResponse = respApdu.getData();
            else
                this.cardResponse = null;
        }
    }
    
    public byte[] getResponse(){ return cardResponse; }
    
    public byte[] getSW(){ return statusWord; }
    
    public String getResponseString(){
        return "   " + byteArrayToHexString( this.cardResponse, " ");
    }
    
    
    //************************ Funciones privadas  ************************//
    private List<Byte> apduToBytesList(String apdu){
        // Remueve los espacios
        String apdusAux = apdu.trim();
        apdusAux = apdusAux.replace(" ", "");
        // Separa el String en bytes de a 2 numeros hex
        String[] apduStrBytes = apdusAux.split("(?<=\\G.{2})");
        List<Byte> apduBytes = new ArrayList<>();
        for(String apduStrByte : apduStrBytes)
            if(apduStrByte.matches("\\p{XDigit}{2}"))
                apduBytes.add(hexStringToByte(apduStrByte));
        return apduBytes;
    }
    
    private byte hexStringToByte(String s) {
        int len = s.length();
        return (byte) ((Character.digit(s.charAt(0), 16) << 4) + Character.digit(s.charAt(1), 16));
    }
    
    private String byteArrayToHexString(byte[] myBytes, String separator) {
        String retStr = "";
        for(byte myByte : myBytes)
           retStr += String.format("%02X", myByte) + separator;
        return retStr;
    }
}
    
