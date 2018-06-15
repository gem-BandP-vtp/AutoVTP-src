/*
 * Este modulo crea el documento PDF para exportar con la libreria iText 5.5.4
 * 
 */
package backend;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import javax.swing.JFrame;
import java.io.FileOutputStream;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.Image;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.Phrase;
import java.util.List;

//import javafx.geometry.Insets;
import com.itextpdf.text.pdf.PdfPRow;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPatternPainter;
import com.itextpdf.text.pdf.PatternColor;
import com.itextpdf.text.Rectangle;

/**
 *
 * @authors npalavec, Ezequiel Martin Zarza
 */
public class PDFReport extends JFrame {

    private Document doc;
   

    public PDFReport (String pathAndName, String proyecto, String autor, String fecha, List<String> profile, List<String> tests,List<Image> resultImages, List<String> comments, List<Integer> resultIndexes) throws FileNotFoundException, DocumentException {        
               
        
        doc = new Document();
       
        Font writingFont = new Font(FontFamily.HELVETICA, 8, Font.NORMAL, BaseColor.BLACK);
        Font writingFontTittle = new Font(FontFamily.HELVETICA, 8, Font.BOLD, BaseColor.WHITE);
        
        FileOutputStream fileStream = new FileOutputStream(pathAndName);
        PdfWriter writer = PdfWriter.getInstance(doc, fileStream);
        
        doc.setMargins(30, 30, 30, 30);
        doc.open();
        
        /*PdfContentByte canvas = writer.getDirectContent();
        PdfPatternPainter img_pattern = canvas.createPattern(
                100, 100);
        System.out.println(canvas);*/
        
        //PRUEBA CON FONDO
        /*
        try{
        PdfContentByte canvas = writer.getDirectContent();
        
            Image image = Image.getInstance(this.getClass().getResource("/images/pattern.png"));
            PdfPatternPainter img_pattern = canvas.createPattern(
                image.getScaledWidth(), image.getScaledHeight());
            image.setAbsolutePosition(0, 0);
            
            //image.scalePercent(100 * doc.getPageSize().getWidth() / 4 / image.getWidth());
            
            img_pattern.addImage(image);
            BaseColor patron_color = new PatternColor(img_pattern);
            PdfPTable table = new PdfPTable(2);
        table.addCell("Prueba celda con patrón:");
        PdfPCell cell = new PdfPCell();  
        cell.setFixedHeight(200);
        cell.setBackgroundColor(patron_color);
        table.addCell(cell);
        doc.add(table);
        } catch (IOException |  DocumentException e){
            Logger.getLogger(PDFReport.class.getName()).log(Level.SEVERE, null, e);
        }
        
        */
            // FIN PRUEBA
        

        // Agrega Margenes de pagina
        
        // Agrega imagen
        try {
            Image im = Image.getInstance(this.getClass().getResource("/images/gemalto.jpg"));
            im.scalePercent(100 * doc.getPageSize().getWidth() / 4 / im.getWidth());
            im.setAbsolutePosition(doc.left(45), doc.getPageSize().getHeight() - 10.0f - im.getScaledHeight());
            doc.add(im);    
        } catch (BadElementException | IOException ex) {
            Logger.getLogger(PDFReport.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        
        // Agrega Header
        doc.add(writeParagraph(/*autor+" \n"+proyecto+"\n"+*/fecha+"\n",writingFont,Element.ALIGN_RIGHT));
        doc.add(Chunk.NEWLINE);
        doc.add(Chunk.NEWLINE);
        
        // Agrega recuadro
        writingFontTittle.setSize(25.0f);
        Chunk textAsChunk = new Chunk("Validation Test Plan", writingFontTittle);
        //textAsChunk.setBackground(BaseColor.LIGHT_GRAY, 100.0f, 10.0f,100.0f ,10.0f);
        textAsChunk.setBackground(new BaseColor(247, 134, 36), 100.0f, 10.0f, 100.0f, 10.0f);
        doc.add(writeParagraph(textAsChunk,Element.ALIGN_CENTER));
        
        writingFont.setSize(12.0f);
        doc.add(writeParagraph("\nProject: " + proyecto, writingFont,Element.ALIGN_LEFT));
        doc.add(writeParagraph("TC: " + autor, writingFont,Element.ALIGN_LEFT));
        
        // Agrega persona
        /*writingFont.setSize(12.0f);
        /*doc.add(writeParagraph("\nDear sir,", writingFont,Element.ALIGN_LEFT));
        
        // Agrega descripcion
        Paragraph desc = writeParagraph("This document was attached to the specified cards and were sent to the customer's address. This document should be completed after checking if the dispatch is correct. Please return this document duly filled in, signed and stamped to your GEMALTO Technical Consultant.\n", writingFont,Element.ALIGN_JUSTIFIED);
        desc.setFirstLineIndent(50.0f);
        doc.add(desc);*/
        
        // Agrega titulo perfil
        writingFont.setSize(25.0f);
        doc.add(writeParagraph("Profile configuration\n", writingFont,Element.ALIGN_LEFT));
        
        // Agrega perfil
        writingFont.setSize(12.0f);
        String myProfile = "\n";
        
        myProfile = profile.stream().map((str) -> str+"\n").reduce(myProfile, String::concat);
        doc.add(writeParagraph(myProfile, writingFont,Element.ALIGN_LEFT));
        
        
        
        // Agrega Tabla 1
        writingFont.setSize(25.0f);
        doc.add(writeParagraph("Results\n",writingFont,Element.ALIGN_CENTER));
        
        // Agrega Resultados Globales:
        writingFont.setSize(10.0f);
        doc.add(globalResultTable(resultImages,resultIndexes));
        doc.add(writeTable(tests,resultImages,resultIndexes, comments, writer));
        
        
        // Agrega descripcion final
        /*writingFont.setSize(12.0f);
        doc.add(writeParagraph("\nThe undersigned confirms that the attached cards are in compliance with our requirements. We give our assent to GEMALTO to use the mentioned references as a free of responsabilities of the dispatch.\n\n Name  ______________________________\n\n Date   ______________________________", writingFont,Element.ALIGN_JUSTIFIED));
        
        // Agrega area de firma
        writingFont.setSize(12.0f);
        doc.add(writeParagraph("______________________________\nSignature           ", writingFont,Element.ALIGN_RIGHT));
        */
        
        //Agrega pie de Página
        
        // Cierra el documento
        doc.close();
    }

    Document returnIt() {
        return doc;
    }
    
    private Paragraph writeParagraph(String paragraph,Font fnt,int align)
    {
        Paragraph para = new Paragraph(paragraph, fnt);
        para.setAlignment(align);
        return para;
    }
    
    private Paragraph writeParagraph(Chunk paragraph,int align)
    {
        Paragraph para = new Paragraph(paragraph);
        para.setAlignment(align);
        return para;
    }
    
    private PdfPTable writeTable(List<String> tests, List<Image> resultImages, List<Integer> resultIndexes, List<String> comments, PdfWriter writer )
    {
        Font writingFont = new Font(FontFamily.HELVETICA, 11, Font.NORMAL, BaseColor.BLACK);
        // Ancho de las columnas
        
        float[] widths = { 0.45f, 0.1f, 0.45f };
   
        PdfPTable table = new PdfPTable(widths);
        
        table.setSpacingBefore(15.0f);
        table.setWidthPercentage(100.0f);
        
        
        for(int i=0; i < tests.size(); i++ )
        {
            Font writingFontCelda1 = new Font(FontFamily.HELVETICA, 13, Font.NORMAL, BaseColor.BLACK);
            PdfPCell celda1 = new PdfPCell(new Phrase(tests.get(i),writingFontCelda1));
            //celda1.setHorizontalAlignment(Element.ALIGN_CENTER);
            //celda1.setPadding(25.0f);
            celda1.setHorizontalAlignment(Element.ALIGN_CENTER);
            celda1.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celda1.setPadding(5.0f);
            
          
            float borderWidth = 1.0f;
            float borderWidthBottom = 0.0f;
            
            table.addCell(celda1);
            
            
            Image im = resultImages.get(resultIndexes.get(i));
            
            //Tamaño del icono.
            im.scaleToFit(18, 18);
            
            PdfPCell celda2 = (i < resultIndexes.size()) ? new PdfPCell(im, false) : new PdfPCell(new Phrase(""));
            celda2.setHorizontalAlignment(Element.ALIGN_CENTER);
            
            celda2.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celda2.setPadding(5.0f);
            
            table.addCell(celda2);
            
            PdfPCell celda3 = (i < comments.size()) ? new PdfPCell(new Phrase(comments.get(i))) : new PdfPCell(new Phrase(""));
            celda3.setHorizontalAlignment(Element.ALIGN_LEFT);
            celda3.setPadding(5.0f);
 
            table.addCell(celda3);
        }
        
        table = this.setColorAndBorders(table,tests.size());
        return table;
    }
    
    private PdfPTable globalResultTable(List<Image> images, List<Integer> resultIndexes)
    {
        float[] widths = { 0.5f, 0.1f, 0.4f, 0.1f };
        PdfPTable table = new PdfPTable(widths);
        table.setSpacingBefore(15.0f);
        table.setWidthPercentage(100.0f);
        PdfPCell celda1 = new PdfPCell(new Phrase("Global Result"));
        celda1.setHorizontalAlignment(Element.ALIGN_LEFT);
        celda1.setPadding(5.0f);
        PdfPCell celda2;
        int globalResult = 0;
        int naCount = 0;
        for(int i = 0; i < resultIndexes.size(); i++) {
            if(resultIndexes.get(i) != 4) globalResult = globalResult | (1 << resultIndexes.get(i));
            else naCount++;
        }
        Image im;
        if(globalResult == 2) { // If all are ok.
            im = images.get(1);
        } else if((globalResult & 8) == 8) { // If some have errors.
            im = images.get(3);
        } else if((globalResult & 4) == 4) { // If some have warnings
            im = images.get(2);
        } else { // Anything else (Void, new states).
            im = images.get(0);
        }
        im.scaleToFit(25, 25);
        
        celda2 = new PdfPCell(im);
        celda2.setHorizontalAlignment(Element.ALIGN_CENTER);
        celda2.setVerticalAlignment(Element.ALIGN_CENTER);
        celda2.setPadding(5.0f);
        /*table.addCell(celda1);
        table.addCell(celda2);*/
        
        
        PdfPCell celda3 = new PdfPCell(new Phrase("Not Applicable Tests"));
        celda3.setHorizontalAlignment(Element.ALIGN_LEFT);
        celda3.setPadding(5.0f);
        PdfPCell celda4 = new PdfPCell(new Phrase(Integer.toString(naCount)));
        celda4.setHorizontalAlignment(Element.ALIGN_CENTER);
        celda4.setVerticalAlignment(Element.ALIGN_CENTER);
        celda4.setPadding(5.0f);
        
        
        
        table.addCell(celda1);
        table.addCell(celda2);
        table.addCell(celda3);
        table.addCell(celda4);
        
        for(PdfPRow r: table.getRows()) {
            for(PdfPCell c: r.getCells()) {
                c.setBackgroundColor(new BaseColor(251, 195, 150));
            }
        }
        // TODO: Continue HERE.
        /*for(int i=0; i < tests.size(); i++ )
        {
            PdfPCell celda1 = new PdfPCell(new Phrase(tests.get(i)));
            celda1.setHorizontalAlignment(Element.ALIGN_LEFT);
            celda1.setPadding(5.0f);
            table.addCell(celda1);
            
            
            Image im = results.get(i);
            im.scaleToFit(32, 32);
            PdfPCell celda2 = (i < results.size()) ? new PdfPCell(im, false) : new PdfPCell(new Phrase(""));
            celda2.setHorizontalAlignment(Element.ALIGN_CENTER);
            celda2.setVerticalAlignment(Element.ALIGN_CENTER);
            celda2.setPadding(5.0f);
            table.addCell(celda2);
            
            PdfPCell celda3 = (i < comments.size()) ? new PdfPCell(new Phrase(comments.get(i))) : new PdfPCell(new Phrase(""));
            celda3.setHorizontalAlignment(Element.ALIGN_LEFT);
            celda3.setPadding(5.0f);
            table.addCell(celda3);
        }*/
       
        return table;
    }
    
    private PdfPTable setColorAndBorders(PdfPTable table, int cantTest){
        boolean alternate = true;
        int contadorRow = 0;
        for(PdfPRow row: table.getRows()) {
            int contadorCell = 0;
            if (contadorRow == 0 ){
                for(PdfPCell cell: row.getCells()) {
                
                    cell.setBackgroundColor(alternate ? new BaseColor(251, 195, 150) : BaseColor.WHITE);
                
                    cell.setBorder(Rectangle.NO_BORDER);
                    if(contadorCell == 0  ){
                        cell.setBorderWidthTop(1.0f);
                        cell.setBorderWidthLeft(1.0f);
                    }
                    else if (contadorCell == 2){
                        cell.setBorderWidthTop(1.0f);
                        //cell.setBorderWidthLeft(1.0f);
                        cell.setBorderWidthRight(1.0f);
                    }
                    else{
                        cell.setBorderWidthTop(1.0f);
                    }
                    contadorCell++;
                }
            alternate = !alternate;
            }
            else if (contadorRow == cantTest -1){
                for(PdfPCell cell: row.getCells()) {
                
                    cell.setBackgroundColor(alternate ? new BaseColor(251, 195, 150) : BaseColor.WHITE);
                
                    cell.setBorder(Rectangle.NO_BORDER);
                    if(contadorCell == 0  ){
                        cell.setBorderWidthBottom(1.0f);
                        cell.setBorderWidthLeft(1.0f);
                    }
                    else if (contadorCell == 2){
                        cell.setBorderWidthBottom(1.0f);
                        //cell.setBorderWidthLeft(1.0f);
                        cell.setBorderWidthRight(1.0f);
                    }
                    else{
                        cell.setBorderWidthBottom(1.0f);
                    }
                    contadorCell++;
                }
            alternate = !alternate;
            }
            else{
                for(PdfPCell cell: row.getCells()) {
                
                    cell.setBackgroundColor(alternate ? new BaseColor(251, 195, 150) : BaseColor.WHITE);
                
                    cell.setBorder(Rectangle.NO_BORDER);
                    if(contadorCell == 0  ){
                        
                        cell.setBorderWidthLeft(1.0f);
                    }
                    else if (contadorCell == 2){
                       
                        //cell.setBorderWidthLeft(1.0f);
                        cell.setBorderWidthRight(1.0f);
                    }
                    contadorCell++;
                }
            alternate = !alternate;
            }
            contadorRow++;
            
        }
        return table;
    }
    
    /*private void tableLayout(PdfPTable table, float[][] widths, float[] heights,int headerRows, int rowStart, PdfContentByte[] canvases) {
        int columns;
        Rectangle rect;
        int footer = widths.length - table.getFooterRows();
        int header = table.getHeaderRows() - table.getFooterRows() + 1;
        for (int row = header; row < footer; row += 2) {
            columns = widths[row].length - 1;
            rect = new Rectangle(widths[row][0], heights[row],
                        widths[row][columns], heights[row + 1]);
            rect.setBackgroundColor(BaseColor.ORANGE);
            rect.setBorder(Rectangle.NO_BORDER);
            canvases[PdfPTable.BASECANVAS].rectangle(rect);
        }
    }*/
    
    
    
}
