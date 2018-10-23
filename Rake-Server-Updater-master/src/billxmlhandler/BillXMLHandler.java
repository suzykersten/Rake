

package billxmlhandler;

import com.google.gson.Gson;
import java.io.BufferedWriter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import javax.xml.namespace.NamespaceContext;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.zip.ZipException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.xml.sax.InputSource;
import javax.xml.xpath.*;
import org.apache.commons.io.FileUtils;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
/**
 *
 * @author Suzanne
 */
public class BillXMLHandler {
    private static boolean doDownload = false;
    private static ArrayList<SetOfBills> setOfBills;
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {       
        
        
        setupSetOfBills();
        
        
        //download most recent version of bill files
        if (doDownload){
            for (int i = 0; i < setOfBills.size(); i++){
                log("Downloading bills for bill type " + setOfBills.get(i).type + "...");
                downloadFiles(setOfBills.get(i).linkToZip, setOfBills.get(i).getInputFilename());
                
            }
        }
        
         
        
        //Pull out all the data from the just-downloaded zip files
        XPath xpath = setupXPath();
        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        
        for (int i = 0; i < setOfBills.size(); i++){
            log ("Pulling out data from " + setOfBills.get(i).getInputFilename() + "...");
            getBillDataFromZip(setOfBills.get(i), xpath, db);
            setOfBills.get(i).sort();
        }

        
        for (int i = 0; i < setOfBills.size(); i++){
            log("Writing JSON file for bill type " + setOfBills.get(i).type + "...");
            writeToFile(setOfBills.get(i).getOutputFilename(), setOfBills.get(i).getJSON());
        }
        
        
        System.out.println("Done.");       
    }
    
    private static void getBillDataFromZip(SetOfBills bills, XPath xpath, DocumentBuilder db){
        //String titleExp = "/bill/legis-body/section/subsection/text/quote/short-title";
        String titleExp = "/bill/metadata/dublinCore/title";
        String resolutionTitleExp = "/resolution/metadata/dublinCore/title";
        
        String stageExp = "/bill/@bill-stage";
        String actionDateExp = "/bill/form/action/action-date";
        String chamberExp = "/bill/form/current-chamber";
        
        try {
            //zip file setup
            ZipFile zipfile = new ZipFile(bills.getInputFilename());
            Enumeration<? extends ZipEntry> entries = zipfile.entries();
            
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
       
                //log("Reading " + entry.getName() + "...");
                Bill bill = new Bill();

                //parse name from the zip file's name
                bill.setLinkToFull(parseBillLinkName(entry.getName()));

                //get the input file
                InputSource inputSource = new InputSource(zipfile.getInputStream(entry));
                Document document = db.parse(inputSource);

                //get all the data from the xml file
                String title;
                
               title = (xpath.evaluate(titleExp, document));
                if (title.equals("")){
                    title = (xpath.evaluate(resolutionTitleExp, document));
                }
                
                //get the second half things after the :
                title = title.substring(title.indexOf(':') + 1);
                
                bill.setTitle(title);
                //log(xpath.evaluate(titleExp, document));
                
                bill.setStage(xpath.evaluate(stageExp, document));
                bill.setActionDate(xpath.evaluate(actionDateExp, document));
                bill.setChamber(xpath.evaluate(chamberExp, document));
                
                bills.add(bill);
            }
            
            zipfile.close();
        } catch (IOException iOException) {
            log("Couldn't open file " + bills.getInputFilename());
            iOException.printStackTrace();
        } catch (SAXException sAXException) {
            sAXException.printStackTrace();
        } catch (XPathExpressionException xPathExpressionException) {
            
            xPathExpressionException.printStackTrace();
        }
    }
    
    private static void setupSetOfBills(){
        setOfBills = new ArrayList<>();
        setOfBills.add(new SetOfBills("hconres", 
            "https://www.govinfo.gov/bulkdata/BILLS/115/2/hconres/BILLS-115-2-hconres.zip"));
        setOfBills.add(new SetOfBills("hjres", 
            "https://www.govinfo.gov/bulkdata/BILLS/115/2/hjres/BILLS-115-2-hjres.zip"));
        setOfBills.add(new SetOfBills("hr", 
            "https://www.govinfo.gov/bulkdata/BILLS/115/2/hr/BILLS-115-2-hr.zip"));
        setOfBills.add(new SetOfBills("hres", 
            "https://www.govinfo.gov/bulkdata/BILLS/115/2/hres/BILLS-115-2-hres.zip"));
        setOfBills.add(new SetOfBills("s", 
            "https://www.govinfo.gov/bulkdata/BILLS/115/2/s/BILLS-115-2-s.zip"));
        setOfBills.add(new SetOfBills("sconres",
            "https://www.govinfo.gov/bulkdata/BILLS/115/2/sconres/BILLS-115-2-sconres.zip"));
        setOfBills.add(new SetOfBills("sjres", 
            "https://www.govinfo.gov/bulkdata/BILLS/115/2/sjres/BILLS-115-2-sjres.zip"));
        setOfBills.add(new SetOfBills("sres", 
            "https://www.govinfo.gov/bulkdata/BILLS/115/2/sres/BILLS-115-2-sres.zip"));
    }
    
    //Attempts to download a file from the url given and store it in a file
    // named after the OutputFilename given.
    private static void downloadFiles(String url, String outputFilename){
        try {
            File zipFile = new File(outputFilename);
            URI uri = new URI(url);
            FileUtils.copyURLToFile(uri.toURL(), zipFile, 15000, 45000);
        } catch (URISyntaxException e) {
            log("Bad URL given to downloadFiles: " + url);
            e.printStackTrace();
            
        } catch (IOException e) {
            log("Couldn't open file from URL: " + url);
            e.printStackTrace();
        }
    }
    
    //setups XPath with the correct namespace
    private static XPath setupXPath(){
        NamespaceContext ctx = new NamespaceContext() {
             public String getNamespaceURI(String prefix) {
                 return prefix.equals("dc") ? "http://purl.org/dc/elements/1.1/" : null; 
             }
             public Iterator getPrefixes(String val) {
                 return null;
             }
             public String getPrefix(String uri) {
                 return null;
             }
         };
        
        XPath xpath = XPathFactory.newInstance().newXPath();
        xpath.setNamespaceContext(ctx);
        return xpath;
    }
    
    //parse link name from bill filename
    private static String parseBillLinkName(String filename){
        //String url = "https://www.govinfo.gov/bulkdata/BILLS/";
        
        String startURL = "https://www.govinfo.gov/link/bills/";
        String endURL = "&link-type=xml";
        
        String regex = "BILLS-(\\d*)(\\D*)(\\d*)(\\D*)\\.xml";
        
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(filename);
        matcher.find();
        /*
        System.out.println(matcher.group(1));
        System.out.println(matcher.group(2));
        System.out.println(matcher.group(3));
        System.out.println(matcher.group(4));
        */
        String middleURL = null;
        try {
            middleURL = matcher.group(1) + "/" //Congress #
                    + matcher.group(2) + "/" //Bill type
                    + matcher.group(3) //Bill number
                    + "?billversion=" + matcher.group(4);//Bill version
        } catch (Exception e) {
            log("FAILED to find something in file " + filename);
            e.printStackTrace();
        }
                       
        
        return startURL + middleURL + endURL;
    }
  
    //Write to a file. Used to quickly write out JSON 
    private static void writeToFile(String filename, String json){
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename))){
            bw.write(json);
        } catch (IOException e){
            log("Unable to save json to file: " + filename);
            e.printStackTrace();
        }
    }
    
    //quickly print to console
    private static void log(String s){
        System.out.println(s);
    }
        
}

class SetOfBills{
    public String type;
    public String linkToZip;
    private ArrayList<Bill> billSet;

    public SetOfBills(String type, String linkToZip) {
        this.type = type;
        this.linkToZip = linkToZip;
        billSet = new ArrayList<> ();
    }
    
    public void sort(){
        Collections.sort(billSet);
        Collections.reverse(billSet);
    }
    
    public void add(Bill bill){
        billSet.add(bill);
    }
    
    public String getJSON(){
        return new Gson().toJson(billSet);
    }
    
    public ArrayList<Bill> getArrayList(){
        return billSet;
    }
    
    public String getInputFilename(){
        return "input/" + type + ".zip";
    }
    
    public String getOutputFilename(){
        return "output/" + type + ".json";
    }
    
}

