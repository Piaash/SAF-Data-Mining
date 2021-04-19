/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pdf.extraction;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.font.TextAttribute;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

/**
 *
 * @author Shahnewaz Mahmud(Piaash)
 * Bsc. Engg. in CSE,CUET
 * SO-IT,HRD,Janata Bank
 */
public class PDFExtraction implements Printable,ActionListener
{
    static String[] printString; //static variables for using in different methods
    static int noOfLines ;
    static String totalPrint;
    static String zoneName;
    static String fullPath;
    static ArrayList serialExtra = new ArrayList();
    static ArrayList branchExtra = new ArrayList();
    static ArrayList adviceExtra = new ArrayList();
    static ArrayList dateExtra = new ArrayList();
    static ArrayList takaExtra = new ArrayList();
    static BigDecimal bigTotal = BigDecimal.ZERO;
    static int serial = 0;
    static String[] printExtra;
    @Override
    public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException { //method for printing pageindex amd format can be changed to print multiple pages
        if(pageIndex>0)
        {
            return NO_SUCH_PAGE;//designed to print in only one page can be upgraded later
        }
        Graphics2D g2d = (Graphics2D) graphics;//graphics for drawing the data on the page
        g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());//setting the page axis
        graphics.setFont(new Font("monospaced",Font.BOLD,13));//font needs to be monospaced or the format breaks
        graphics.drawString("Zone Name: ", 200, 20);//first two lines in centre
        graphics.drawString("Responding Date:", 200, 35);
        graphics.setFont(new Font("monospaced",Font.BOLD,9));//setting font monospaced works other fonts like times new roman may not work
        String firstLine = String.format("%-2s.%-30s -  %-10s  -  %-10s  -  %15s  - %-10s\n", "SL","Br. Name","Adv. No","Date","Amount","Comt./B.F");//format for right/left justification when needed
        graphics.drawString(firstLine, 50, 65);
        int lineSpace = 80;//lineSpace determines the gap between lines
        for(int i = 0 ; i < noOfLines ; i++)//lines derived from pdf printed here
        {
        graphics.drawString(printString[i], 50, lineSpace);
        lineSpace += 15;
        }
        //ArrayList printExtra = new ArrayList();
        printExtra = new String[serialExtra.size()];//extra input from gui printed here
        for(int i = 0 ; i < serialExtra.size(); i++)
        {
            printExtra[i] = String.format("%-2s.%-30s -  %-10s  -  %-10s  -  %15s  - %-10s\n", serialExtra.get(i).toString(), branchExtra.get(i).toString(), adviceExtra.get(i).toString(), dateExtra.get(i).toString(), takaExtra.get(i).toString(),"");
            graphics.drawString(printExtra[i], 50, lineSpace);
            lineSpace += 15;   
        }
        totalPrint = String.format("%65s: %15s\n", "Total",bigTotal.toString());
        HashMap<TextAttribute, Integer> fontAttributes = new HashMap<>();//for underline
        fontAttributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);//for underline
        graphics.setFont(new Font("monospaced",Font.BOLD,9).deriveFont(fontAttributes));//for underline
        String underLine = String.format("%82s", " ");//for underline
        graphics.drawString(underLine, 50, lineSpace - 10);//for underline
        graphics.setFont(new Font("monospaced",Font.BOLD,9));
        graphics.drawString(totalPrint, 50, lineSpace);//prints the total amount
        return PAGE_EXISTS;
    } 
     @Override
    public void actionPerformed(ActionEvent e) {//method for printer job
         PrinterJob job = PrinterJob.getPrinterJob();
         job.setPrintable(this);
         //PageFormat pf = new PageFormat();
         //pf.setOrientation(PageFormat.LANDSCAPE);
         boolean ok = job.printDialog();
         if (ok) {
             try {
                 //job.setPrintable(this, pf);
                  job.print();
                  bigTotal = BigDecimal.ZERO;//setting total amount to zero so it does not get added to next pdf caution:printing the same pdf twice/mpre without pressing choose causes total to be zero
             } catch (PrinterException ex) {
              /* The job did not successfully complete */
              System.out.println("Error");
             }
         }
    }
    public static void main(String[] args) throws IOException {
        // TODO code application logic here
        //main method that creates gui
        createGUI();
        
    }
    public static void createFileChooser(JFrame frame) throws IOException
    {
        JFileChooser fileChooser = new JFileChooser("C:\\Users\\USER\\Desktop");//code for selecting pdf
        fileChooser.showDialog(frame, "Choose");//code for selecting pdf
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);//code for selecting pdf
        File pathFile = fileChooser.getSelectedFile();//saving the selected file's address
        fullPath = pathFile.getAbsolutePath();//saving the selected file's address
        System.out.println(fullPath);//saving the selected file's address
        File file = new File(fullPath);//saving the selected file's address
        PDDocument document = PDDocument.load(file);//loading the selected file's address uses pdfbox framework
        PDFTextStripper pdfStripper = new PDFTextStripper();//used for turing pdf into text
        String text = pdfStripper.getText(document);//used for turing pdf into text
        System.out.println(text);//test for input
        //System.out.println(text.length());
        int count1 = 0;
        int count2 = 0;
        int count3 = 0;
        int count4 = 0;
        //regex for matching
        Pattern pattern1 = Pattern.compile("((?<=Debit: CIBTA)(.*)(?=\\(\\d+\\) Zone:))");//matches nondigit after Debit: CIBTA
        Pattern pattern2 = Pattern.compile("(?<=\\bAdvice No: (AC|CT)\\s)(\\d+)");//matches digit after Advice No: AC or CT
        Pattern pattern3 = Pattern.compile("(?<=\\bOriginating Date:\\s)(\\d+)\\/(\\d+)\\/(\\d+)");//matches digit after Originating Date: 
        Pattern pattern4 = Pattern.compile("(?:\\d+,)*\\d+\\.\\d+(?=\\s+In Word : Taka)");//matches digit with ,. before In Word : Taka 
        //Pattern pattern5 = Pattern.compile("(?<=\\b\\) Zone:\\s)(.*)(?=\\(\\d+\\))");//matches nondigit after ) Zone:
        Matcher match1 = pattern1.matcher(text);//matcher for pattern
        Matcher match2 = pattern2.matcher(text);
        Matcher match3 = pattern3.matcher(text);
        Matcher match4 = pattern4.matcher(text);
        //Matcher match5 = pattern5.matcher(text); used for zone discontinued for conflict with corporate branches
        String test;
        ArrayList keyList = new ArrayList();
        //loop for finding and saving all the mathes
        while(match2.find())
        {
            test = match2.group();
            System.out.println(test);
            keyList.add(test);
            count2++;
        }
        String[] branchArray = new String[count2];
        while(match1.find())
        {
            test = match1.group();
            System.out.println(test);
            branchArray[count1] = test;
            count1++;
        }
        String[] dateArray = new String[count2];
        while(match3.find())
        {
            test = match3.group();
            System.out.println(test);
            dateArray[count3] = test;
            count3++;
        }
        String[] takaArray = new String[count2];
        while(match4.find())
        {
            test = match4.group();
            System.out.println(test);
            takaArray[count4] = test;
            bigTotal = bigTotal.add(BigDecimal.valueOf(Double.parseDouble(test.strip().replaceAll(",", ""))));//bigdecimal used because double causes precision problem
            count4++;
        }
        /*while(match5.find())//for zone conflict with corporate branch discontinued
        {
            test = match5.group();
            System.out.println(test);
            zoneName = test;
            break;
        }*/
        
        System.out.format("%-2s.%-30s -  %-10s  -  %-10s  -  %15s  - %-10s\n", "SL","Br. Name","Adv. No","Date","Amount","Comment");//format for heading
        String nothing = "";
        noOfLines = keyList.size();
        printString = new String[keyList.size()];
        for(int i = 0 ; i < keyList.size() ; i++)
        {
            System.out.format("%-2s.%-30s -  %-10s  -  %-10s  -  %15s  - %-10s\n",i+1,branchArray[i],keyList.get(i),dateArray[i],takaArray[i],nothing);
            printString[i] = String.format("%-2s.%-30s -  %-10s  -  %-10s  -  %15s  - %-10s\n",i+1,branchArray[i],keyList.get(i),dateArray[i],takaArray[i],nothing);//format for output
            serial++;//keeps track of serial
            
        }
        System.out.format("%65s: %15s\n", "Total",bigTotal.toString());
    } 
    public static void createGUI()
    {
        UIManager.put("swing.boldMetal", Boolean.FALSE);//Main window code
        JFrame frame = new JFrame("PDF Extraction");
        frame.addWindowListener(new WindowAdapter() 
        {@Override
           public void windowClosing(WindowEvent e) 
           {System.exit(0);}
        }
        );
        frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(),BoxLayout.Y_AXIS));
        JButton chooseButton = new JButton("Choose");
        frame.add(chooseButton);
        JLabel pdfPath = new JLabel();//shows the directory location of the pdf
        pdfPath.setForeground(Color.red);
        frame.add(pdfPath);
        chooseButton.addActionListener((ActionEvent e) -> {//lamda expression for override
            try {
                createFileChooser(frame);
                pdfPath.setText(fullPath);
                frame.pack();
                //printExtra = new String[serialExtra.size()];
            } catch (IOException ex) {
                Logger.getLogger(PDFExtraction.class.getName()).log(Level.SEVERE, null, ex);
            }
        });        
        JButton printButton = new JButton("Print");//all GUI elements are created here
        printButton.addActionListener(new PDFExtraction());
        frame.add(printButton);
        JLabel branchLabel = new JLabel("Branch:");
        branchLabel.setForeground(Color.BLUE);
        frame.add(branchLabel);
        JTextField branchField = new JTextField();
        frame.add(branchField);
        JLabel adviceLabel = new JLabel("Advice:");
        adviceLabel.setForeground(Color.BLUE);
        frame.add(adviceLabel);
        JTextField adviceField = new JTextField();
        frame.add(adviceField);
        JLabel dateLabel = new JLabel("Date:");
        dateLabel.setForeground(Color.BLUE);
        frame.add(dateLabel);
        JTextField dateField = new JTextField();
        frame.add(dateField);
        JLabel takaLabel = new JLabel("Amount:");
        takaLabel.setForeground(Color.BLUE);
        frame.add(takaLabel);
        JTextField takaField = new JTextField();
        frame.add(takaField);     
        JButton submitButton = new JButton("Submit");
        frame.add(submitButton);
        JLabel lastRecord = new JLabel("Last Record:");//for dispalying the last entry
        lastRecord.setFont(new Font("Verdana",Font.PLAIN,12));
        lastRecord.setForeground(Color.red);
        frame.add(lastRecord);
        submitButton.addActionListener((ActionEvent e) -> {//taking extra data input outside of pdf
            serialExtra.add(++serial);
            branchExtra.add(" "+branchField.getText());
            adviceExtra.add(adviceField.getText());
            dateExtra.add(dateField.getText());
            takaExtra.add(takaField.getText());
            branchField.setText("");
            adviceField.setText("");
            dateField.setText("");
            takaField.setText("");
            //{
                System.out.println(serialExtra.get(serialExtra.size() - 1));//testing purpose
                System.out.println(branchExtra.get(branchExtra.size() - 1));//
                System.out.println(adviceExtra.get(adviceExtra.size() - 1));//
                System.out.println(dateExtra.get(dateExtra.size() - 1));//
                System.out.println(takaExtra.get(takaExtra.size() - 1));//
                bigTotal = bigTotal.add(BigDecimal.valueOf(Double.parseDouble(takaExtra.get(takaExtra.size() - 1).toString().strip().replaceAll(",", ""))));
                System.out.println("Total: "+ bigTotal);

            lastRecord.setText("Last Record:"+ serialExtra.get(serialExtra.size() - 1) +" "+ branchExtra.get(branchExtra.size() - 1) +" "+ adviceExtra.get(adviceExtra.size() - 1) +" "+ dateExtra.get(dateExtra.size() - 1) +" "+ takaExtra.get(takaExtra.size() - 1));
            frame.pack();
            /*System.out.println(serialExtra);
            System.out.println(branchExtra);
            System.out.println(adviceExtra); //for testing
            System.out.println(dateExtra);
            System.out.println(takaExtra);*/
        });
        JLabel devCredits = new JLabel("Developed By: SHAHNEWAZ MAHMUD,SO-IT,HRD,JANATA BANK");//Developer credits
        frame.add(devCredits);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
