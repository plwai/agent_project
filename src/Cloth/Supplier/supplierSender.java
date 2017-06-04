/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Cloth.Supplier;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.*;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.Property;
import jade.domain.FIPAAgentManagement.SearchConstraints;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import org.apache.commons.codec.binary.Base64;
/**
 *
 * @author Tiki
 */
public class supplierSender extends Agent {
    private supplierGui suppGui;
    static final Base64 base64 = new Base64();
    private AID supplierServiceAgentAID = null;
    
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
            ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(base64.decode(objectString));
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
    
    protected void setup()
    {
        suppGui = new supplierGui(this);
        suppGui.showGui();
        
         	
         addBehaviour(new CyclicBehaviour(this) 
	{            
            public void action() 
            { 
                ACLMessage msg= receive();
                
		if (msg != null) {
                    suppGui.appendLog("\n");
                    suppGui.appendLog("[supplierSender] Message received from " + msg.getSender());
                    
                    String msgContent = msg.getContent();
                    suppGui.appendLog("[supplierSender] Message content [Base64 string]: " + msgContent);
                    suppGui.appendLog("[supplierSender] Msg performative: " + ACLMessage.getPerformative(msg.getPerformative())); 
                    //suppGui.popup("Clothes Products added");
                    
                    try
                    {
                        Supplier supp = (Supplier)deserializeObjectFromString(msgContent);  
                        
                        //suppGui.showResult(supp);                                                
                    }
                    catch(Exception ex)
                    {
                        suppGui.appendLog("[supplierSender] StrToObj conversion error: " + ex.getMessage());
                    }
                }
                
                suppGui.appendLog("[supplierSender] CyclicBehaviour Block");
                block();
            }
        });
    }
    
    public void getSupplierServiceAgent() {
  	try {
            String serviceType = "basic-Supplier";
            suppGui.clearLog();
            suppGui.appendLog("[supplierSender]Searching the DF/Yellow-Pages for " + serviceType + " service");
            suppGui.appendLog("[supplierSender]Service properties:Add Tshirt");
            
            // Build the description used as template for the search
            DFAgentDescription template = new DFAgentDescription();
            
            ServiceDescription templateSd = new ServiceDescription();
            templateSd.setType(serviceType);
            templateSd.addProperties(new Property("Clothing1", "cloth"));
            templateSd.addProperties(new Property("Clothing2", "panth"));
            templateSd.addProperties(new Property("Clothing3", "onepiece"));
            template.addServices(templateSd);
  		
            SearchConstraints sc = new SearchConstraints();
            // We want to receive 10 results at most
            sc.setMaxResults(new Long(10));
  		
            DFAgentDescription[] results = DFService.search(this, template, sc);
            if (results.length > 0) {
  		suppGui.appendLog("Agent "+getLocalName()+" found the following " + serviceType + " services:");
  		for (int i = 0; i < results.length; ++i) {
                    DFAgentDescription dfd = results[i];
                    AID agentAID = dfd.getName();
                    suppGui.popup("Agent name: " + agentAID);
                    suppGui.appendLog("Agent name: " + agentAID);
                    suppGui.appendLog("\n"); 
  		}
                
                //just use the first one
                DFAgentDescription dfd = results[0];
                supplierServiceAgentAID = dfd.getName();
                
                //enable calcGui.combobox and submit button
                //suppGui.enabledGUI();
            }	
            else {
                suppGui.appendLog("[supplierSender]Agent "+getLocalName()+" did not find any " + serviceType + " service");
                suppGui.popup("No " + serviceType + " agent service found!");
                
            }
  	}
  	catch (FIPAException fe) {
            fe.printStackTrace();
            
  	}
        suppGui.appendLog("\n");        
    }
    
    public void addCloth(Supplier supp) {
        suppGui.clearLog();
        suppGui.appendLog("[shoesSupplierSender]Product successfully added");
        suppGui.appendLog("[supplierSender]Receiving updated request object from suppGui");
        suppGui.appendLog("\nCloths information. \n\tColor: " + supp.getClothColor() + " \n\tsize: " + supp.getClothSize() + "\n\tquantity: " + supp.getClothQuantity());
        suppGui.appendLog("\n\n\nPants information.  \n\tsize: " + supp.getPantSize() + "\n\tquantity: " + supp.getPantQuantity());
        suppGui.appendLog("\n\nOnepiece information. \n\tcolor: " + supp.getOnepieceColor() + " \n\tsize: " + supp.getOnepieceSize() + "\n\tquantity: " + supp.getOnepieceQuantity());
        suppGui.appendLog("\n");
        
        //Send messages to "cap - CalcAgentPlus"  
        suppGui.appendLog("[supplierSender]Preparing ACL msg: INFORM");   
	ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        
        suppGui.appendLog("[supplierSender]Convert Supplier supp to String Base64");   
        String strObj = ""; 
        try
        {
            strObj = serializeObjectToString(supp);
        }
        catch (Exception ex)
        {
            System.out.println("\n[supplierSender] ObjToStr conversion error: " + ex.getMessage());
        }
        
	msg.setContent(strObj);
        
     	//msg.addReceiver(new AID("cap", AID.ISLOCALNAME));
        msg.addReceiver(supplierServiceAgentAID);
        send(msg);
        
        suppGui.appendLog("[supplierSender]Sending Message to cap");
        suppGui.appendLog("[supplierSender]Message content [Base64 string]: " + strObj);   
    }
    
}
