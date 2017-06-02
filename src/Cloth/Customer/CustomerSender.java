package Cloth.Customer;


import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.*;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.Property;
import jade.domain.FIPAAgentManagement.SearchConstraints;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import org.apache.commons.codec.binary.Base64;
import java.util.ArrayList;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author on
 */
public class CustomerSender extends Agent {
    private CustomerGUI customerGui;
    private ClothDiscription showCloth;
    static final Base64 base64 = new Base64();
    private AID customerServiceAgentAID = null;
    private ArrayList<Cloth> Baju = new ArrayList();
    
    private void setBaju(){
        Cloth b = new Cloth("Jubah A","Blue","One-Piece","S");
        Baju.add(b);
        b = new Cloth("Jubah A","Blue","One-Piece","XL");
        Baju.add(b);
        Cloth a = new Cloth("Jubah B","Blue","One-Piece","S");
        Baju.add(a);
        Cloth c = new Cloth("T--","Blue","T-Shirt","S");
        Baju.add(c);
    }
    private String[] getBajuName(ArrayList<Cloth> baju, String ClothType){
        ArrayList<String> bajuname = new ArrayList();
        for(int i=0; i<baju.size();i++)
        {
            if(baju.get(i).getType().equals(ClothType)){
                if(bajuname.size()>0){
                    for(int y=0; y<bajuname.size(); y++){
                        boolean q=false;
                        if(bajuname.get(y).equals(baju.get(i).getName()))
                            q=true;
                        if(q)
                            bajuname.add(baju.get(i).getName());
                    }
                }else
                    bajuname.add(baju.get(i).getName());
            }
        }
        String[] BajuName = bajuname.toArray(new String[0]);
        return BajuName;
    }
    
    //object to string
    public String serializeObjectToString(Object object) throws IOException 
    {
        String s = null;
        
        try 
        {
            ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
            GZIPOutputStream gzipOutputStream = new GZIPOutputStream(arrayOutputStream);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(gzipOutputStream);         
        
            objectOutputStream.writeObject(object);
            objectOutputStream.flush();
            gzipOutputStream.close();
            
            objectOutputStream.flush();
            objectOutputStream.close();
            
            s = new String(base64.encode(arrayOutputStream.toByteArray()));
            arrayOutputStream.flush();
            arrayOutputStream.close();
        }
        catch(Exception ex){}
        
        return s;
    }
    
    //string to object
    public Object deserializeObjectFromString(String objectString) throws IOException, ClassNotFoundException 
    {
        Object obj = null;
        try
        {    
            ByteArrayInputStream arrayInputStream = new ByteArrayInputStream((byte[]) base64.decode(objectString));
            GZIPInputStream gzipInputStream = new GZIPInputStream(arrayInputStream);
            ObjectInputStream objectInputStream = new ObjectInputStream(gzipInputStream);
            obj =  objectInputStream.readObject();
            
            objectInputStream.close();
            gzipInputStream.close();
            arrayInputStream.close();
        }
        catch(Exception ex){}
        return obj;
    }
    
    
    public void requestCatalog(String clothType){
        //ArrayList <Cloth> Baju = new ArrayList();
        setBaju();
        String[] BajuName = getBajuName(Baju, clothType);
        customerGui.clearLog();
        customerGui.appendLog("Receiving cloth catalog request from CustomerGUI");
        customerGui.appendLog("Cloth Type : " + clothType);  
        customerGui.appendLog("\n");
        customerGui.displayCalatalog(BajuName);
    }
    
    public void AddProductToCart(String clothType){
        ArrayList <Cloth> Baju = new ArrayList();
        setBaju();
        String[] BajuName = getBajuName(Baju, clothType);
        customerGui.clearLog();
        customerGui.appendLog("Receiving cloth catalog request from CustomerGUI");
        customerGui.appendLog("Cloth Type : " + clothType);  
        customerGui.appendLog("\n");
        customerGui.displayCalatalog(BajuName);
    }
    
    public void ViewClothDescription(String BajuName){
        float price=(float) 20.00;
        ArrayList<Cloth> b = new ArrayList();
        ArrayList<String> size = new ArrayList();
        for(int i=0; i< Baju.size(); i++)
        {
            if(Baju.get(i).getName().equals(BajuName)){
                b.add(Baju.get(i));
                if(size.size()>0){
                    boolean e=false;
                    for(int y=0; y< size.size(); y++)
                    {
                        if(size.get(y).equals(b.get(i).getSize())){
                            e=true;
                        }
                    }
                    if(!e)
                        size.add(b.get(i).getSize());
                }else{
                    size.add(b.get(i).getSize());
                }
            }
        }
        String type=b.get(0).getType();
        String name=b.get(0).getName();
        
        //showCloth.setVisible(true);
        showCloth = new ClothDiscription(this);
        showCloth.setBajuData(type, name, price , size);
	showCloth.showGui();
        //ShowCloth.
//      showCloth = new ClothDiscription(this);
	//showCloth.showGui();
        customerGui.clearLog();
    }
    
    public void getCustomerServiceAgent() {
  	try {
            String serviceType = "Customer";
            customerGui.appendLog("Searching the DF/Yellow-Pages for " + serviceType + " service");
            
            // Build the description used as template for the search
            DFAgentDescription template = new DFAgentDescription();
            
            ServiceDescription templateSd = new ServiceDescription();
            templateSd.setType(serviceType);
            templateSd.addProperties(new Property("type1", "Add Product to Cart"));
            templateSd.addProperties(new Property("type2", "Remove Product from Cart"));
            templateSd.addProperties(new Property("type3", "Disply Prodcut"));
            template.addServices(templateSd);
  		
            SearchConstraints sc = new SearchConstraints();
            // We want to receive 10 results at most
            sc.setMaxResults(new Long(10));
  		
            DFAgentDescription[] results = DFService.search(this, template, sc);
            customerGui.appendLog("\n"+Integer.toString(results.length));
            if (results.length > 0) {
  		customerGui.appendLog("Agent "+getLocalName()+" found the following " + serviceType + " services:");
  		for (int i = 0; i < results.length; ++i) {
                    DFAgentDescription dfd = results[i];
                    AID agentAID = dfd.getName();
                    customerGui.popup("Agent name: " + agentAID);
                    customerGui.appendLog("Agent name: " + agentAID);
                    customerGui.appendLog("\n"); 
  		}
                
                //just use the first one
                DFAgentDescription dfd = results[0];
                customerServiceAgentAID = dfd.getName();
                
                //enable calcGui.combobox and submit button
                customerGui.enabledGUI();
            }	
            else {
                customerGui.appendLog("Agent "+getLocalName()+" did not find any " + serviceType + " service");
                customerGui.popup("No " + serviceType + " agent service found!");
            }
  	}
  	catch (FIPAException fe) {
            fe.printStackTrace();
  	}
        customerGui.appendLog("\n");        
    }
    protected void setup() 
    {        
        customerGui = new CustomerGUI(this);
	customerGui.showGui();
        //for receiving calculation result	
	addBehaviour(new CyclicBehaviour(this) 
	{            
            public void action() 
            { 
                ACLMessage msg= receive();
                
		if (msg != null) {
                    customerGui.appendLog("\n");
                    customerGui.appendLog("Message received from " + msg.getSender());
                    
                    String msgContent = msg.getContent();
                    customerGui.appendLog("Message content [Base64 string]: " + msgContent);
                    customerGui.appendLog("Msg performative: " + ACLMessage.getPerformative(msg.getPerformative()));                   
                    
                    try
                    {
                        Cloth Baju = (Cloth)deserializeObjectFromString(msgContent);
                        
                        
                        customerGui.showResult(Baju);                                                
                    }
                    catch(Exception ex)
                    {
                        customerGui.appendLog("StrToObj conversion error: " + ex.getMessage());
                    }
                }
                
                customerGui.appendLog("[CustomerAgent] CyclicBehaviour Block");
                block();
            }
        });
    }
}
