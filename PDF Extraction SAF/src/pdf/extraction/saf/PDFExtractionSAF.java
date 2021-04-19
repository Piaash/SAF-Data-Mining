/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pdf.extraction.saf;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.font.TextAttribute;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import static java.awt.print.Printable.NO_SUCH_PAGE;
import static java.awt.print.Printable.PAGE_EXISTS;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.text.DecimalFormat;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.UIManager;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

/**
 *
 * @author Shahnewaz Mahmud(Piaash)
 * Bsc. Engg. in CSE,CUET
 * SO-IT,HRD,Janata Bank
 */
public class PDFExtractionSAF implements Printable,ActionListener
{
    static String[] printString; //static variables for using in different methods
    static String[] khulnaString , dhakaCityString , dhakaDivisionString , rajshahiString , ctgString;
    static ArrayList khulnaList = new ArrayList();
    static ArrayList dhakaCityList = new ArrayList();
    static ArrayList dhakaDivisionList = new ArrayList();
    static ArrayList rajshahiList = new ArrayList();
    static ArrayList ctgList = new ArrayList();
    static ArrayList safNotFoundList = new ArrayList();
    static int noOfLines ;
    static int khulnaLines , dhakaCityLines, dhakaDivisionLines, rajshahiLines, ctgLines;
    static String totalPrint;
    static String zoneName;
    static String fullPath;
    static ArrayList serialExtra = new ArrayList();//these variables with extra are used for adding additional advices if needed,this is probably not needed but the option is available
    static ArrayList zoneExtra = new ArrayList();
    static ArrayList branchExtra = new ArrayList();
    static ArrayList adviceExtra = new ArrayList();
    static ArrayList dateExtra = new ArrayList();
    static ArrayList takaExtra = new ArrayList();
    static ArrayList printExtra = new ArrayList();
    static BigDecimal bigTotal = BigDecimal.ZERO;
    static int serial = 0;
    static JSpinner spinnerMonth , spinnerYear;
    static int currentMonth ,currentYear ;
    static String printSelect;
    static BigDecimal extraTotal = BigDecimal.ZERO,khulnaTotal = BigDecimal.ZERO , dhakaCityTotal = BigDecimal.ZERO ,dhakaDivisionTotal = BigDecimal.ZERO ,rajshahiTotal = BigDecimal.ZERO ,ctgTotal = BigDecimal.ZERO ,allTotal = BigDecimal.ZERO;
    boolean doublePrintCheck = false;
    static String result = "";
    //public String getName(String area)
    //{
      //  return area;
    //}
    @Override
    public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException 
    { //method for printing
        if(pageIndex>0)
        {
            return NO_SUCH_PAGE;
        }
        /*if(fullPath==null)
        {
            JOptionPane.showMessageDialog(null, "Please Choose PDF before printing.", "Error", JOptionPane.ERROR_MESSAGE);
            return NO_SUCH_PAGE;
        }*/
        Graphics2D g2d = (Graphics2D) graphics;//graphics for drawing the data on the page
        g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());//setting the pageformat
        graphics.setFont(new Font("monospaced",Font.BOLD,12));//font needs to be monospaced or the format breaks
        if(!printSelect.equals("Print Voucher"))//first two lines not needed in voucher
        {
        graphics.drawString(printSelect, 200, 20);//first two lines in centre
        }
        if(currentMonth == 1)
        {
            currentMonth = 13 ;//To make January of next year greater than December of previous year.
        }
        if(!printSelect.equals("Print Voucher"))//no need to print this in voucher
        {
        graphics.drawString("Month: "+ Month.of(currentMonth - 1) +" Year: "+ currentYear, 200, 35);
        }
        graphics.setFont(new Font("monospaced",Font.BOLD,7));
        if(!printSelect.equals("Final Report") && !printSelect.equals("Print Voucher"))//not needed in final report and voucher
        {
        String firstLine = String.format("%-2s.%-30s - %-25s - %-10s - %-10s - %15s - %-10s\n", "SL","Zone","Br. Name","Adv. No","Date","Amount","Comt.");//format for right/left justification when needed
        graphics.drawString(firstLine, 50, 65);
        }
        int lineSpace = 80;//lineSpace determines the gap between lines
        
        if(null != printSelect)
        {
        /*for(int i = 0 ; i < noOfLines ; i++)//lines derived from pdf printed here
        {
        graphics.drawString(printString[i], 50, lineSpace);
        lineSpace += 15;
        }*/
        switch (printSelect) {
            case "Khulna/Barishal/Sylhet" -> {
                int i = 0;
                for( ; i < khulnaLines ; i++)//prints all the advices in the zone
                {
                    graphics.drawString(Integer.toString(i+1)+"."+khulnaList.get(i).toString(), 50, lineSpace);
                    lineSpace += 15;
                }
                for(int j = 0 ; j < printExtra.size(); j++)//prints extra advices submitted
        {
            graphics.drawString(++i +"."+printExtra.get(j).toString(), 50, lineSpace);
            lineSpace += 15;
        }  
                String khulnaTotalPrint = String.format("%88s: %15s\n", "Total",khulnaTotal.toString());
                graphics.drawString(khulnaTotalPrint, 50, lineSpace);
              if(doublePrintCheck == true)//this ensures the same info in extra is not printed repeatedly
            {
            printExtra.clear();//clears the arraylist
            doublePrintCheck = false;
            }
            else
            {
                doublePrintCheck = true;
            }
                
            } 
            case "Dhaka City" -> {//same as before
                int i = 0;
                for(; i < dhakaCityLines ; i++)
                {
                    graphics.drawString(Integer.toString(i+1)+"."+dhakaCityList.get(i).toString(), 50, lineSpace);
                    lineSpace += 15;
                }
                for(int j = 0 ; j < printExtra.size(); j++)
        {
            graphics.drawString(++i +"."+printExtra.get(j).toString(), 50, lineSpace);
            lineSpace += 15;   
        }
                String dhakaCityTotalPrint = String.format("%88s: %15s\n", "Total",dhakaCityTotal.toString());
                graphics.drawString(dhakaCityTotalPrint, 50, lineSpace);
                if(doublePrintCheck == true)
            {
            printExtra.clear();
            doublePrintCheck = false;
            }
            else
            {
                doublePrintCheck = true;
            }
            }
            case "Dhaka Division" -> {//same as before
                int i = 0;
                for( ; i < dhakaDivisionLines ; i++)
                {
                    graphics.drawString(Integer.toString(i+1)+"."+dhakaDivisionList.get(i).toString(), 50, lineSpace);
                    lineSpace += 15;
                }
                for(int j = 0 ; j < printExtra.size(); j++)
        {
            graphics.drawString(++i +"."+printExtra.get(j).toString(), 50, lineSpace);
            lineSpace += 15;   
        }
                String dhakaDivisionTotalPrint = String.format("%88s: %15s\n", "Total",dhakaDivisionTotal.toString());
                graphics.drawString(dhakaDivisionTotalPrint, 50, lineSpace);
                if(doublePrintCheck == true)
            {
            printExtra.clear();
            doublePrintCheck = false;
            }
            else
            {
                doublePrintCheck = true;
            }
            }
            case "Rajshahi" -> {//same as before
                int i = 0 ;
                for(; i < rajshahiLines ; i++)
                {
                    graphics.drawString(Integer.toString(i+1)+"."+rajshahiList.get(i).toString(), 50, lineSpace);
                    lineSpace += 15;
                }       
                for(int j = 0 ; j < printExtra.size(); j++)
        {
            graphics.drawString(++i +"."+printExtra.get(j).toString(), 50, lineSpace);
            lineSpace += 15;   
        }
                String rajshahiTotalPrint = String.format("%88s: %15s\n", "Total",rajshahiTotal.toString());
                graphics.drawString(rajshahiTotalPrint, 50, lineSpace);
                if(doublePrintCheck == true)
            {
            printExtra.clear();
            doublePrintCheck = false;
            }
            else
            {
                doublePrintCheck = true;
            }
            }
            case "CTG/Noakhali" -> {//same as before
                int i = 0 ;
                for(; i < ctgLines ; i++)
                {
                    graphics.drawString(Integer.toString(i+1)+"."+ctgList.get(i).toString(), 50, lineSpace);
                    lineSpace += 15;
                }       
                for(int j = 0 ; j < printExtra.size(); j++)
        {
            graphics.drawString(++i +"."+printExtra.get(j).toString(), 50, lineSpace);
            lineSpace += 15;   
        }
                String ctgTotalPrint = String.format("%88s: %15s\n", "Total",ctgTotal.toString());
                graphics.drawString(ctgTotalPrint, 50, lineSpace);
                if(doublePrintCheck == true)
            {
            printExtra.clear();
            doublePrintCheck = false;
            }
            else
            {
                doublePrintCheck = true;
            }
            }
            case "Final Report" -> {//prints a summary
                graphics.setFont(new Font("monospaced",Font.BOLD,10));
                graphics.drawString("Khulna/Barishal/Sylhet:  "+khulnaTotal +" TK", 50, lineSpace);
                lineSpace += 15;
                graphics.drawString("Dhaka City            :  "+dhakaCityTotal +" TK", 50, lineSpace);
                lineSpace += 15;
                graphics.drawString("Dhaka Division        :  "+dhakaDivisionTotal +" TK", 50, lineSpace);
                lineSpace += 15;
                graphics.drawString("Rajshahi              :  "+rajshahiTotal +" TK", 50, lineSpace);
                lineSpace += 15;
                graphics.drawString("Ctg/Noakhali          :  "+ctgTotal +" TK", 50, lineSpace);
                lineSpace += 15;
                HashMap<TextAttribute, Integer> fontAttributes = new HashMap<>();//for underline
                fontAttributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);//for underline
                graphics.setFont(new Font("monospaced",Font.BOLD,10).deriveFont(fontAttributes));//for underline
                String underLine = String.format("%82s", " ");//for underline
                graphics.drawString(underLine, 50, lineSpace - 10);//for underline
                graphics.setFont(new Font("monospaced",Font.BOLD,10));
                lineSpace += 5;
                graphics.drawString("Total                 :  "+allTotal +" TK", 50, lineSpace);
                lineSpace += 30;
                graphics.drawString("Warning(SAF not found):", 50, lineSpace);
                lineSpace += 15;
                underLine = String.format("%25s", " ");
                graphics.setFont(new Font("monospaced",Font.BOLD,10).deriveFont(fontAttributes));
                graphics.drawString(underLine, 50, lineSpace - 10);//for underline
                graphics.setFont(new Font("monospaced",Font.BOLD,8));
                lineSpace += 5;
                for(int i = 0 ; i < safNotFoundList.size() ; i++)
                {
                graphics.drawString(safNotFoundList.get(i).toString(), 50, lineSpace);//prints the advices that does not contain the word SAF/Super Annuation Fund in the description of the advice
                lineSpace += 15;
                }
            }
            case "Print Voucher" ->{
                //Toolkit tool = Toolkit.getDefaultToolkit();
                //Image img = tool.getImage("G:\\Items\\Janata bank logo small.png");
                //URL imageLocation = getClass().getClassLoader().getResource("img\\jbl.png");//loads image saved in img folder in src of project
                URL imageLocation = getClass().getResource("img\\jbl.png");//loads image saved in img folder in src of project
                //Image img = tool.getImage("Janata bank logo small.png");
                Image img = new ImageIcon(imageLocation).getImage();//get image of Janata Bank Logo
                graphics.drawImage(img, 30, 22,25,25, null);//drwas the logo
                graphics.setFont(new Font("monospaced",Font.BOLD,10));
                graphics.drawString("Janata Bank LTD", 60, 30);
                graphics.drawString("HRD EFM Cell", 60, 45);
                //graphics.drawString("GPF/CPF/SAF Fund", 60, 60);
                graphics.drawString("Date:", 400, 30);
                graphics.drawString("Debit:Banker's A/C SAF", 30, 75);
                graphics.drawRect(30, 90, 540, 150);//print a rectangle for description
                graphics.drawString("Description", 35, 100);
                graphics.drawString("Amount", 450, 100);
                graphics.drawLine(30, 110 , 580, 110);
                graphics.drawString("To the amount of SAF Bank Contribution of various branches for the ", 35, 120);
                graphics.drawString("month of "+Month.of(currentMonth - 1)+"-"+currentYear+" deposited to SND Account no-010236000937", 35, 135);
                graphics.drawString("at Local Office.", 35, 150);
                graphics.drawLine(440, 90, 440, 240);
                graphics.drawString(allTotal +"Tk", 450, 120);//prints the total amount
                graphics.drawString("Total:", 390, 230);
                graphics.drawString(allTotal +"Tk", 450, 230);
                graphics.setFont(new Font("monospaced",Font.BOLD,8));
                String[] splitAllTotal = allTotal.toString().split("\\.");//splits the total amount to taka and paisa
                result = "";
                String inWord = "In Word:Taka "+inWords(Integer.parseInt(splitAllTotal[0]));//converts the taka to in word
                StringBuilder strBuilder = new StringBuilder(inWord);//convert to stringbuilder cause string is immutable
                if(Integer.parseInt(splitAllTotal[1]) != 0)
                {
                    result = "";
                    String appendix = " & Paisa "+ inWords(Integer.parseInt(splitAllTotal[1]));//converts the paisa to inword
                    strBuilder.append(appendix);
                }
                strBuilder.append(" Only");
                graphics.drawString(strBuilder.toString(), 30, 260);
                graphics.drawLine(65, 330 , 115, 330);
                graphics.drawString("Officer", 70, 340);
                graphics.drawLine(390, 330 , 440, 330);
                graphics.drawString("Officer", 400, 340);
                graphics.drawLine(10, 400 , 590, 400);//separator for debit and credit voucher needs to be in the middle of the page
                graphics.drawImage(img, 30, 422,25,25, null);//same thing printed for credit just head different
                graphics.setFont(new Font("monospaced",Font.BOLD,10));
                graphics.drawString("Janata Bank LTD", 60, 430);
                graphics.drawString("HRD EFM Cell", 60, 445);
                //graphics.drawString("GPF/CPF/SAF Fund", 60, 460);
                graphics.drawString("Date:", 400, 430);
                graphics.drawString("Credit:JBE SAF", 30, 475);
                graphics.drawRect(30, 490, 540, 150);
                graphics.drawString("Description", 35, 100);
                graphics.drawString("Amount", 450, 100);
                graphics.drawLine(30, 510 , 580, 510);
                graphics.drawString("By the amount of SAF Bank Contribution of various branches for the ", 35, 520);
                graphics.drawString("month of "+Month.of(currentMonth - 1)+"-"+currentYear+" deposited to Banker's Account.", 35, 535);
                //graphics.drawString("at Local Office.", 35, 550);
                graphics.drawLine(440, 490, 440, 640);
                graphics.drawString(allTotal +"Tk", 450, 520);
                graphics.drawString("Total:", 390, 630);
                graphics.drawString(allTotal +"Tk", 450, 630);
                graphics.setFont(new Font("monospaced",Font.BOLD,8));
                graphics.drawString(strBuilder.toString(), 35, 660);
                //graphics.drawString(inWords(), 35, 680);
                graphics.drawLine(65, 730 , 115, 730);
                graphics.drawString("Officer", 70, 740);
                graphics.drawLine(390, 730 , 440, 730);
                graphics.drawString("Officer", 400, 740);
            }
            default -> {
            }
        }
        }
        /*totalPrint = String.format("%65s: %15s\n", "Total",bigTotal.toString());
        HashMap<TextAttribute, Integer> fontAttributes = new HashMap<>();//for underline
        fontAttributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
        graphics.setFont(new Font("monospaced",Font.BOLD,9).deriveFont(fontAttributes));
        String underLine = String.format("%82s", " ");
        graphics.drawString(underLine, 50, lineSpace - 10);
        graphics.setFont(new Font("monospaced",Font.BOLD,9));
        graphics.drawString(totalPrint, 50, lineSpace);*/
        return PAGE_EXISTS;
    } 
     @Override
    public void actionPerformed(ActionEvent e) {//prints when a button get pressed
         PrinterJob job = PrinterJob.getPrinterJob();
         job.setPrintable(this);
         printSelect = ((JButton)e.getSource()).getActionCommand();
         boolean ok = job.printDialog();
         if(fullPath == null)
         {
             ok = false;
         }
         if (ok) {
             try {
                  job.print();
             } catch (PrinterException ex) {
              /* The job did not successfully complete */
              System.out.println("Error");
             }
         }
         else
         {
             JOptionPane.showMessageDialog(null, "Please click Choose PDF and select PDF downloaded from JB Remittance.","Error",JOptionPane.ERROR_MESSAGE);
         }
    }
    public static String formatConversion(double convert)//converts the nimber to following for ease of processing
    {
        DecimalFormat formatter = new DecimalFormat("0.00");
        return formatter.format(convert);
    }
    public static String inWords(int number)//method for converting the number to word format
    {
        String[] units = {"One","Two","Three","Four","Five","Six","Seven","Eight","Nine","Ten"};
        String[] elevenToNineteen = {"Eleven","Twelve","Thirteen","Fourteen","Fifteen","Sixteen","Seventeen","Eighteen","Nineteen"};
        String[] tens = {"Twenty","Thirty","Fourty","Fifty","Sixty","Seventy","Eighty","Ninety"};
        if(number < 11)
        {
            switch (number) {
                case 1 -> result += units[number - 1];
                case 2 -> result += units[number - 1];
                case 3 -> result += units[number - 1];
                case 4 -> result += units[number - 1];
                case 5 -> result += units[number - 1];
                case 6 -> result += units[number - 1];
                case 7 -> result += units[number - 1];
                case 8 -> result += units[number - 1];
                case 9 -> result += units[number - 1];
                case 10 -> result += units[number - 1];
                default -> {
                }
            }
            
        }
        else if(number < 20)
        {
            switch (number) {
                case 11 -> result += elevenToNineteen[0];
                case 12 -> result += elevenToNineteen[1];
                case 13 -> result += elevenToNineteen[2];
                case 14 -> result += elevenToNineteen[3];
                case 15 -> result += elevenToNineteen[4];
                case 16 -> result += elevenToNineteen[5];
                case 17 -> result += elevenToNineteen[6];
                case 18 -> result += elevenToNineteen[7];
                case 19 -> result += elevenToNineteen[8];
                default -> {
                }
            }
        }
        else if (number > 9999999 && number < 1000000000)
        {
            inWords(number / 10000000);
            result += " Crore ";
            inWords(number % 10000000);
        }
        else if (number > 99999 && number < 10000000)
        {
            inWords(number / 100000);
            result += " Lac ";
            inWords(number % 100000);
        }
        else if(number > 999 && number < 100000)
        {
            inWords(number / 1000);
            result += " Thousand ";
            //if(number % 1000 != 0)
            {
                //number %= 1000;
                inWords(number % 1000);
            }
        }
        else if(number > 99 && number < 1000)
        {
            inWords(number / 100);
            result += " Hundred ";
            //if(number % 100 != 0)
            {
                //number %= 100;
                inWords(number % 100);
                //inWords(number - (number % 10));
                //inWords(number % 10);
            }
        }
        else if(number % 10 == 0)
        {
            switch (number) {
                case 20 -> result += tens[0];
                case 30 -> result += tens[1];
                case 40 -> result += tens[2];
                case 50 -> result += tens[3];
                case 60 -> result += tens[4];
                case 70 -> result += tens[5];
                case 80 -> result += tens[6];
                case 90 -> result += tens[7];
                default -> {
                }
            }
        }
        else if(number % 10 != 0)
        {
            inWords(number - (number % 10));
            result += " ";
            inWords(number % 10);
        }
        return result;
    }
    public static void main(String[] args) throws IOException {
        // TODO code application logic here
       
        createGUI();
        
    }
    public static void createFileChooser(JFrame frame) throws IOException
    {
        JFileChooser fileChooser = new JFileChooser("G:\\");//code for selecting pdf
        fileChooser.showDialog(frame, "Choose");
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        File pathFile = fileChooser.getSelectedFile();
        if(pathFile != null)
        {
        fullPath = pathFile.getAbsolutePath();
        System.out.println(fullPath);
        currentMonth = Integer.parseInt(spinnerMonth.getValue().toString());
        if(currentMonth == 12)
        {
            currentMonth = 1;
        }
        else
        {
        currentMonth++;
        }
        currentYear = Integer.parseInt(spinnerYear.getValue().toString());
        File file = new File(fullPath);
        PDDocument document = PDDocument.load(file);
        PDFTextStripper pdfStripper = new PDFTextStripper();
        String text = pdfStripper.getText(document);
        //System.out.println(text);//test for input
        //System.out.println(text.length());
        int count1 = 0, count2 = 0, count3 = 0, count4 = 0, count5 = 0, count6 = 0;
        //regex for matching
        Pattern pattern1 = Pattern.compile("((?<=Debit: CIBTA )(.*)(?=\\(\\d+\\) Zone:))");//matches nondigit after Debit: CIBTA
        Pattern pattern2 = Pattern.compile("(?<=\\bAdvice No: (AC|CT)\\s)(\\d+)");//matches digit after Advice No: AC or CT
        Pattern pattern3 = Pattern.compile("(?<=\\bOriginating Date:\\s)(\\d+)\\/(\\d+)\\/(\\d+)");//matches digit after Originating Date: 
        Pattern pattern4 = Pattern.compile("(?:\\d+,)*\\d+\\.\\d+(?=\\s+In Word : Taka)");//matches digit with ,. before In Word : Taka 
        Pattern pattern5 = Pattern.compile("(?<=\\b\\) Zone:\\s)(.*)(?=\\(\\d+\\))");//matches nondigit after ) Zone:
        Pattern pattern6 = Pattern.compile("(?<=Description Amount\\s\\n)(?:.*\\s\\n)+?(?=In Word : Taka)");
        Matcher match1 = pattern1.matcher(text);
        Matcher match2 = pattern2.matcher(text);
        Matcher match3 = pattern3.matcher(text);
        Matcher match4 = pattern4.matcher(text);
        Matcher match5 = pattern5.matcher(text);
        Matcher match6 = pattern6.matcher(text);
        String test;
        ArrayList keyList = new ArrayList();
        //loop for finding and savibg all the mathes
        while(match2.find())
        {
            test = match2.group();
            //System.out.println(test);
            keyList.add(test);
            count2++;
        }
        //System.out.println(count6);
        String[] branchArray = new String[count2];
        while(match1.find())
        {
            test = match1.group();
            //System.out.println(test);
            branchArray[count1] = test;
            count1++;
        }
        String[] dateArray = new String[count2];
        while(match3.find())
        {
            test = match3.group();
            //System.out.println(test);
            dateArray[count3] = test;
            count3++;
        }
        String[] takaArray = new String[count2];
        while(match4.find())
        {
            test = match4.group();
            //System.out.println(test);
            takaArray[count4] = test;
            bigTotal = bigTotal.add(BigDecimal.valueOf(Double.parseDouble(test.strip().replaceAll(",", ""))));//bigdecimal used because double causes precision problem
            count4++;
        }
        String[] zoneArray = new String[count2];
        while(match5.find())
        {
            test = match5.group();
            //System.out.println(test);
            zoneName = test;
            zoneArray[count5] = test;
            count5++;
        }
        String[] descriptionArray = new String[count2];
        while(match6.find())
        {
            test = match6.group();
            System.out.println(count6 +" "+ branchArray[count6] +"\n "+ test);
            descriptionArray[count6] = test;
            count6++;
        }
        System.out.println(count2 +" desc amount: "+ count6);
        System.out.format("%-2s.%-30s - %-25s - %-10s - %-10s - %15s - %-10s\n", "SL","Zone","Br. Name","Adv. No","Date","Amount","Comment");
        String nothing = "";
        noOfLines = keyList.size();
        printString = new String[keyList.size()];
        khulnaString = new String[999];
        dhakaCityString = new String[999];
        dhakaDivisionString = new String[999];
        rajshahiString = new String[999];
        ctgString = new String[999];
        int khulna = 0, dhakaCity = 0, dhakaDivision = 0, rajshahi = 0, ctg = 0;//map for string and matching all possible advices may need updating later
        String[] khulnaArray = {"KHULNA CORPORATE","BARISHAL CORPORATE","KUSHTIA CORPORATE","CHUADANGA","BHOLA","JHENAIDAH","BAGERHAT CORPORATE","MAGURA","M K ROAD CORP.(JASHORE)","SATKHIRA","PATUAKHALI","SYLHET CORPORATE","SUNAMGONJ","MOULVI BAZAR","HABIGONJ","FOREIGN EXCHANGE CORP.(SYLHET)","KHAN E SABUR RD. CORP."}; 
        String[] dhakaCityArray = {"DHAKA NORTH","DHAKA EAST","DHAKA NORTH","DHAKA SOUTH","MAGHBAZAR CORP.","IMAMGONJ CORPORATE","RAJARBAG CORPORATE","RAMNA CORPORATE","DILKUSHA CORPORATE","RAJUK BHABAN CORP.","MOHAMMADPUR CORPORATE","SANTINAGOR CORP.","DU CAMPUS CORP.","TOPKHANA CORPORATE","KAWRAN BAZAR CORPORATE","MOHAKHALI CORPORATE","MOTIJHEEL CORPORATE","WAPDA CORP.","SHER-E-BANGLA NAGAR CORP.","FOREIGN EXCH.CORP.(DHAKA)","KAMAL ATATURK CORP.","NABAB ABDUL GANI RD. CORP.","UTTARA MODEL TOWN CORP.","DHANMONDI CORP.","JANATA BHABAN CORP.","GULSHAN CIRCLE-2","LOCAL OFFICE, DHAKA"};
        String[] dhakaDivisionArray = {"NITAIGONJ CORPORATE","MYMENSINGH","MYMENSINGH CORPORATE","FARIDPUR CORPORATE","KISHOREGONJ","TANGAIL","JAMALPUR","MADARIPUR","MUNSHIGONJ","NARSHINGDI CORP.","NETRAKONA","B.B.ROAD CORP.(N.GONJ)"};    
        String[] rajshahiArray = {"RAJSHAHI","RAJSHAHI CORPORATE","BOGURA","BOGURA CORPORATE","NAOGAON","GAIBANDHA","NATORE","THAKURGAON","CHAPAI NAWABGONJ","DINAJPUR CORPORATE","KURIGRAM","PABNA","SERAJGONJ CORPORATE","RANGPUR CORPORATE"};
        String[] ctgArray = {"SADHARAN BIMA BHABAN CORP","FOREIGN EXCH. CORP.(CTG)","CHATTOGRAM-C","NEW MARKET CORP.","DEWANHAT CORP.","SHEIKH MUJIB ROAD CORP.","COXBAZAR CORP.","A.K. FAZLUL HAQ RD.CORP.","CUMILLA CORPORATE","CUMILLA-SOUTH","CUMILLA-NORTH","B.BARIA CORP.","MAIZDEE COURT CORPORATE","CHANDPUR","FENI CORP.","LALDIGHI EAST CORP."};
        HashMap<String,Boolean> khulnaMap = new HashMap();//puts all map value to false by default
        for (String khulnaArray1 : khulnaArray) {
            khulnaMap.put(khulnaArray1, Boolean.FALSE);
        }
            HashMap<String,Boolean> dhakaCityMap = new HashMap();
        for (String dhakaCityArray1 : dhakaCityArray) {
            dhakaCityMap.put(dhakaCityArray1, Boolean.FALSE);
        }
            HashMap<String,Boolean> dhakaDivisionMap = new HashMap();
        for (String dhakaDivisionArray1 : dhakaDivisionArray) {
            dhakaDivisionMap.put(dhakaDivisionArray1, Boolean.FALSE);
        }
            HashMap<String,Boolean> rajshahiMap = new HashMap();
        for (String rajshahiArray1 : rajshahiArray) {
            rajshahiMap.put(rajshahiArray1, Boolean.FALSE);
        }
            HashMap<String,Boolean> ctgMap = new HashMap();
        for (String ctgArray1 : ctgArray) {
            ctgMap.put(ctgArray1, Boolean.FALSE);
        }         
        for(int i = 0 ; i < keyList.size() ; i++)
        {   //checks if the term SAF/Super annuation fund is found in the description
            if(!descriptionArray[i].toLowerCase().contains("saf") && !descriptionArray[i].toLowerCase().contains("superannuation fund") && !descriptionArray[i].toLowerCase().contains("super annuation fund"))
            {
                safNotFoundList.add("Branch: " + branchArray[i] +" Advice: " + keyList.get(i) +" Taka "+ takaArray[i]);
                System.out.println(branchArray[i]);
            }
            //System.out.format("%-2s.%-30s - %-30s  -  %-10s  -  %-10s  -  %15s  - %-10s\n",i+1,zoneArray[i],branchArray[i],keyList.get(i),dateArray[i],takaArray[i],nothing);
            //printString[i] = String.format("%-2s.%-30s - %-30s  -  %-10s  -  %-10s  -  %15s  - %-10s\n",i+1,zoneArray[i],branchArray[i],keyList.get(i),dateArray[i],takaArray[i],nothing);
            String[] date = dateArray[i].split("/");
            int day = Integer.parseInt(date[0]);
            int month = Integer.parseInt(date[1]);
            int year = Integer.parseInt(date[2]);
            if(month > currentMonth && month != 12 && currentMonth != 1)//currentmonth cant be smaller than date in advice
            {
                continue;
            }
            else if(month == currentMonth && day >=25)//saf advices are usually sent before 26 date of the month
            {
                continue;
            }
            else if(year > currentYear)//currentyear cant be greater than year
            {
                continue;
            }
            serial++;
            if(khulnaMap.containsKey(zoneArray[i]))//calculates and puts all the value of khulna/barishal/sylhet in arraylist and map
            {
                khulna++;
                    //System.out.format("%-2s.%-30s - %-25s - %-10s - %-10s - %15s - %-10s\n",khulna,zoneArray[i],branchArray[i],keyList.get(i),dateArray[i],takaArray[i],nothing);
                    khulna--;
                    String format = String.format("%-30s - %-25s - %-10s - %-10s - %15s - %-10s\n",zoneArray[i],branchArray[i],keyList.get(i),dateArray[i],takaArray[i],nothing);
                    ++khulna;
                    khulnaString[khulna] = format;
                    khulnaList.add(format);
                    khulnaTotal = khulnaTotal.add(BigDecimal.valueOf(Double.parseDouble(takaArray[i].strip().replaceAll(",", ""))));
                    khulnaMap.put(zoneArray[i], Boolean.TRUE);
            }
            else if(dhakaCityMap.containsKey(zoneArray[i]))//same as before
            {
                dhakaCity++;
                    //System.out.format("%-2s.%-30s - %-25s - %-10s - %-10s - %15s - %-10s\n",dhakaAO,zoneArray[i],branchArray[i],keyList.get(i),dateArray[i],takaArray[i],nothing);
                    dhakaCity--;  
                    String format = String.format("%-30s - %-25s - %-10s - %-10s - %15s - %-10s\n",zoneArray[i],branchArray[i],keyList.get(i),dateArray[i],takaArray[i],nothing);
                    ++dhakaCity;
                    dhakaCityString[dhakaCity] = format;
                    dhakaCityList.add(format);
                    dhakaCityTotal = dhakaCityTotal.add(BigDecimal.valueOf(Double.parseDouble(takaArray[i].strip().replaceAll(",", ""))));
                    dhakaCityMap.put(zoneArray[i], Boolean.TRUE);
            }
            else if(dhakaDivisionMap.containsKey(zoneArray[i]))//same as before
            {
                dhakaDivision++;
                    //System.out.format("%-2s.%-30s - %-25s - %-10s - %-10s - %15s - %-10s\n",dhakaAO,zoneArray[i],branchArray[i],keyList.get(i),dateArray[i],takaArray[i],nothing);
                    dhakaDivision--;  
                    String format = String.format("%-30s - %-25s - %-10s - %-10s - %15s - %-10s\n",zoneArray[i],branchArray[i],keyList.get(i),dateArray[i],takaArray[i],nothing);
                    ++dhakaDivision;
                    dhakaDivisionString[dhakaDivision] = format;
                    dhakaDivisionList.add(format);
                    dhakaDivisionTotal = dhakaDivisionTotal.add(BigDecimal.valueOf(Double.parseDouble(takaArray[i].strip().replaceAll(",", ""))));
                    dhakaDivisionMap.put(zoneArray[i], Boolean.TRUE);
            }
            else if(rajshahiMap.containsKey(zoneArray[i]))//same as before
            {
                rajshahi++;
                    //System.out.format("%-2s.%-30s - %-25s - %-10s - %-10s - %15s - %-10s\n",dhakaAO,zoneArray[i],branchArray[i],keyList.get(i),dateArray[i],takaArray[i],nothing);
                    rajshahi--;  
                    String format = String.format("%-30s - %-25s - %-10s - %-10s - %15s - %-10s\n",zoneArray[i],branchArray[i],keyList.get(i),dateArray[i],takaArray[i],nothing);
                    ++rajshahi;
                    rajshahiString[rajshahi] = format;
                    rajshahiList.add(format);
                    rajshahiTotal = rajshahiTotal.add(BigDecimal.valueOf(Double.parseDouble(takaArray[i].strip().replaceAll(",", ""))));
                    rajshahiMap.put(zoneArray[i], Boolean.TRUE);
            }
            else if(ctgMap.containsKey(zoneArray[i]))//same as before
            {
                ctg++;
                    //System.out.format("%-2s.%-30s - %-25s - %-10s - %-10s - %15s - %-10s\n",dhakaAO,zoneArray[i],branchArray[i],keyList.get(i),dateArray[i],takaArray[i],nothing);
                    ctg--;  
                    String format = String.format("%-30s - %-25s - %-10s - %-10s - %15s - %-10s\n",zoneArray[i],branchArray[i],keyList.get(i),dateArray[i],takaArray[i],nothing);
                    ++ctg;
                    ctgString[ctg] = format;
                    ctgList.add(format);
                    ctgTotal = ctgTotal.add(BigDecimal.valueOf(Double.parseDouble(takaArray[i].strip().replaceAll(",", ""))));
                    ctgMap.put(zoneArray[i], Boolean.TRUE);
            }
        }
        
        for(Map.Entry<String,Boolean> m: khulnaMap.entrySet())//adds the advices that are not yet received as empty 
        {
            if(m.getValue().equals(Boolean.FALSE))
            {
                System.out.println(m.getKey());
                khulnaList.add(m.getKey());
                khulna++;
            }
        }
        for(Map.Entry<String,Boolean> m: dhakaCityMap.entrySet())//same as before
        {
            if(m.getValue().equals(Boolean.FALSE))
            {
                System.out.println(m.getKey());
                dhakaCityList.add(m.getKey());
                dhakaCity++;
            }
        }
        for(Map.Entry<String,Boolean> m: dhakaDivisionMap.entrySet())//same as before
        {
            if(m.getValue().equals(Boolean.FALSE))
            {
                System.out.println(m.getKey());
                dhakaDivisionList.add(m.getKey());
                dhakaDivision++;
            }
        }
        for(Map.Entry<String,Boolean> m: rajshahiMap.entrySet())//same as before
        {
            if(m.getValue().equals(Boolean.FALSE))
            {
                System.out.println(m.getKey());
                rajshahiList.add(m.getKey());
                rajshahi++;
            }
        }
        for(Map.Entry<String,Boolean> m: ctgMap.entrySet())//same as before
        {
            if(m.getValue().equals(Boolean.FALSE))
            {
                System.out.println(m.getKey());
                ctgList.add(m.getKey());
                ctg++;
            }
        }
        
        Collections.sort(khulnaList);//sort the lists alphabetically
        Collections.sort(dhakaCityList);
        Collections.sort(dhakaDivisionList);
        Collections.sort(rajshahiList);
        Collections.sort(ctgList);
        khulnaLines = khulna;//signifies total no of advices in this zone
        System.out.format("%88s: %15s\n", "Khulna Total",khulnaTotal.toString());
        dhakaCityLines = dhakaCity;///same as before
        System.out.format("%88s: %15s\n", "Dhaka City Total",dhakaCityTotal.toString());
        dhakaDivisionLines = dhakaDivision;//same as before
        System.out.format("%88s: %15s\n", "Dhaka Division Total",dhakaDivisionTotal.toString());
        rajshahiLines = rajshahi;//same as before
        System.out.format("%88s: %15s\n", "Rajshahi Total",rajshahiTotal.toString());
        ctgLines = ctg;//same as before
        System.out.format("%88s: %15s\n", "CTG Total",ctgTotal.toString());
        allTotal = khulnaTotal.add(dhakaCityTotal).add(dhakaDivisionTotal).add(rajshahiTotal).add(ctgTotal);//calculate total amount
        System.out.format("%88s: %15s\n", "All Total",allTotal.toString());
    }
    } 
    public static void createGUI()//creates the user interface
    {
        UIManager.put("swing.boldMetal", Boolean.FALSE);
        JFrame frame = new JFrame("PDF Extraction SAF");//base jframe
        frame.addWindowListener(new WindowAdapter()//application closing event 
        {
           @Override
           public void windowClosing(WindowEvent e) 
           {System.exit(0);}
        }
        );
        frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(),BoxLayout.Y_AXIS));//uses boxlayout
        JLabel monthLabel = new JLabel("Choose Month:");//notes the texts
        monthLabel.setForeground(Color.BLUE);
        frame.add(monthLabel);
        SpinnerModel monthSpinnerModel = new SpinnerNumberModel(1,1,12,1);//spinner for selecting month
        spinnerMonth = new JSpinner(monthSpinnerModel);
        frame.add(spinnerMonth);
        JLabel yearLabel = new JLabel("Choose Year:");
        yearLabel.setForeground(Color.BLUE);
        frame.add(yearLabel);
        SpinnerNumberModel yearSpinnerModel = new SpinnerNumberModel(2021,2000,3000,1);//spinner for year
        spinnerYear = new JSpinner(yearSpinnerModel);
        spinnerYear.setEditor(new JSpinner.NumberEditor(spinnerYear, "#"));
        frame.add(spinnerYear);
        JButton chooseButton = new JButton("Choose PDF");
        frame.add(chooseButton);
        JLabel pdfPath = new JLabel();//shows the directory location of the pdf
        pdfPath.setForeground(Color.red);
        frame.add(pdfPath);
        chooseButton.addActionListener((ActionEvent e) -> {//lamda expression for override
            try {
                createFileChooser(frame);
                pdfPath.setText(fullPath);
                frame.pack();
            } catch (IOException ex) {
                Logger.getLogger(PDFExtractionSAF.class.getName()).log(Level.SEVERE, null, ex);
            }
        }); 
        JButton khulnaPrintButton = new JButton("Khulna/Barishal/Sylhet");//all GUI elements are created here
        khulnaPrintButton.addActionListener(new PDFExtractionSAF());
        frame.add(khulnaPrintButton);
        JButton dhakaCityPrintButton = new JButton("Dhaka City");//all GUI elements are created here
        dhakaCityPrintButton.addActionListener(new PDFExtractionSAF());
        frame.add(dhakaCityPrintButton);
        JButton dhakaDivisionPrintButton = new JButton("Dhaka Division");//all GUI elements are created here
        dhakaDivisionPrintButton.addActionListener(new PDFExtractionSAF());
        frame.add(dhakaDivisionPrintButton);
        JButton rajshahiPrintButton = new JButton("Rajshahi");//all GUI elements are created here
        rajshahiPrintButton.addActionListener(new PDFExtractionSAF());
        frame.add(rajshahiPrintButton);
        JButton ctgPrintButton = new JButton("CTG/Noakhali");//all GUI elements are created here
        ctgPrintButton.addActionListener(new PDFExtractionSAF());
        frame.add(ctgPrintButton);
        JButton finalPrintButton = new JButton("Final Report");//all GUI elements are created here
        finalPrintButton.addActionListener(new PDFExtractionSAF());
        frame.add(finalPrintButton);
        JButton voucherPrintButton = new JButton("Print Voucher");
        frame.add(voucherPrintButton);
        voucherPrintButton.addActionListener(new PDFExtractionSAF());
        JLabel zoneLabel = new JLabel("Zone:");
        zoneLabel.setForeground(Color.BLUE);
        frame.add(zoneLabel);
        JTextField zoneField = new JTextField();
        frame.add(zoneField);
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
            zoneExtra.add(zoneField.getText());
            branchExtra.add(branchField.getText());
            adviceExtra.add(adviceField.getText());
            dateExtra.add(dateField.getText());
            takaExtra.add(takaField.getText());
            //{
                //System.out.println(serialExtra.get(serialExtra.size() - 1));//testing purpose
                System.out.println(zoneExtra.get(zoneExtra.size() - 1));//
                System.out.println(branchExtra.get(branchExtra.size() - 1));//
                System.out.println(adviceExtra.get(adviceExtra.size() - 1));//
                System.out.println(dateExtra.get(dateExtra.size() - 1));//
                System.out.println(takaExtra.get(takaExtra.size() - 1));//
                String extra = String.format("%-30s - %-25s - %-10s - %-10s - %15s - %-10s\n",zoneExtra.get(zoneExtra.size() - 1), branchExtra.get(branchExtra.size() - 1),adviceExtra.get(adviceExtra.size() - 1),dateExtra.get(dateExtra.size() - 1),takaExtra.get(takaExtra.size() - 1),"");
                printExtra.add(extra);
                if(!takaExtra.contains("")){
                extraTotal = extraTotal.add(BigDecimal.valueOf(Double.parseDouble(takaExtra.get(takaExtra.size() - 1).toString().strip().replaceAll(",", ""))));
                }
                khulnaTotal = khulnaTotal.add(extraTotal);
                System.out.println("Total: "+ khulnaTotal);

            lastRecord.setText("Last Record:" +" "+ zoneExtra.get(zoneExtra.size() - 1) +" "+ branchExtra.get(branchExtra.size() - 1) +" "+ adviceExtra.get(adviceExtra.size() - 1) +" "+ dateExtra.get(dateExtra.size() - 1) +" "+ takaExtra.get(takaExtra.size() - 1));
            frame.pack();
            zoneField.setText("");
            branchField.setText("");
            adviceField.setText("");
            dateField.setText("");
            takaField.setText("");
            /*System.out.println(serialExtra);
            System.out.println(branchExtra);
            System.out.println(adviceExtra); //for testing
            System.out.println(dateExtra);
            System.out.println(takaExtra);*/
        });
        JButton helpButton = new JButton("Help");//Shows help for user
        helpButton.setForeground(Color.red);
        frame.add(helpButton);
        helpButton.addActionListener((ActionEvent e) -> 
        {
            String step1 = "1.Download your SAF voucher in PDF from JB remmittance.To do that open JB Remittance->Reports->Advice List(For Extract)->Responding->UserID->Date Select->Show->Print->Download\n";
            String step2 = "2.Save and rememeber your download location.On default it is saved in the Downloads folder.\n";
            String step3 = "3.Choose the Month Number.For example: Choose 2 for February.\n";
            String step4 = "4.Click Choose PDF.Find the downloaded SAF voucher PDF and click choose.Usually it is in the Downloads folder.\n";
            String step5 = "5.Choose Year.Click the area u want to print like Dhaka City etc.The report should be printed on A4 size papers.Wait for some time for the report to print\n ";
            String step6 = "6.Click Final Report for a summary.Labels like zone/branch etc. are placeholders for possible future updates.If any problem occurs try restarting the software.\n";
            String step7 = "7.Click Print Voucher to print two vouchers in the same page for debit and credit";
            JOptionPane.showMessageDialog(frame,step1 + step2 + step3 + step4 + step5 + step6 +step7);
        });
        JLabel devCredits = new JLabel("Developed By: SHAHNEWAZ MAHMUD,SO-IT,HRD,JANATA BANK");
        frame.add(devCredits);
        frame.pack();
        frame.setLocationRelativeTo(null);//to center the ui in desktop automatically
        frame.setVisible(true);
    }
}
