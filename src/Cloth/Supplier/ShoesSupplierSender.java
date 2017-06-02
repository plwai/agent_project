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
public class ShoesSupplierSender extends Agent {
    private shoesSupplierGUI shoeGui;
    static final Base64 base64 = new Base64();
    private AID calcServiceAgentAID = null;
    
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
        shoeGui = new shoesSupplierGUI(this);
        shoeGui.showGui();
        
         //for receiving calculation result	
         addBehaviour(new CyclicBehaviour(this) 
	{            
            public void action() 
            { 
                ACLMessage msg= receive();
                
		if (msg != null) {
                    shoeGui.appendLog("\n");
                    shoeGui.appendLog("Message received from " + msg.getSender());
                    
                    String msgContent = msg.getContent();
                    shoeGui.appendLog("Message content [Base64 string]: " + msgContent);
                    shoeGui.appendLog("Msg performative: " + ACLMessage.getPerformative(msg.getPerformative()));                   
                    
                    try
                    {
                        shoesSupplier shoesSupp = (shoesSupplier)deserializeObjectFromString(msgContent);
                        
                        if (shoesSupp.isSuccess()) {                                                    
                            shoeGui.appendLog("Shoes - info   : " + shoesSupp.getInfo());
                        } else {
                            shoeGui.appendLog(" Shoes - info   : " + shoesSupp.getInfo());
                            shoeGui.appendLog("Msg performative: " + ACLMessage.getPerformative(msg.getPerformative()));
                        }
                        
                        //suppGui.showResult(supp);                                                
                    }
                    catch(Exception ex)
                    {
                        shoeGui.appendLog("StrToObj conversion error: " + ex.getMessage());
                    }
                }
                
                shoeGui.appendLog("[ShoesSuppliesAgent] CyclicBehaviour Block");
                block();
            }
        });
    }
    
    public void getCalcServiceAgent() {
  	try {
            String serviceType = "basic-Supplier";
            shoeGui.appendLog("[supplierSender]Searching the DF/Yellow-Pages for " + serviceType + " service");
            shoeGui.appendLog("[supplierSender]Service properties:Add Tshirt");
            
            // Build the description used as template for the search
            DFAgentDescription template = new DFAgentDescription();
            
            ServiceDescription templateSd = new ServiceDescription();
            templateSd.setType(serviceType);
            templateSd.addProperties(new Property("CLOTHS", "TSHIRT"));
            template.addServices(templateSd);
  		
            SearchConstraints sc = new SearchConstraints();
            // We want to receive 10 results at most
            sc.setMaxResults(new Long(10));
  		
            DFAgentDescription[] results = DFService.search(this, template, sc);
            if (results.length > 0) {
  		shoeGui.appendLog("[supplierSender]Agent "+getLocalName()+" found the following " + serviceType + " services:");
  		for (int i = 0; i < results.length; ++i) {
                    DFAgentDescription dfd = results[i];
                    AID agentAID = dfd.getName();
                    //calcGui.popup("Agent name: " + agentAID);
                    shoeGui.appendLog("[supplierSender]Agent name: " + agentAID);
                    shoeGui.appendLog("\n"); 
  		}
                
                //just use the first one
                DFAgentDescription dfd = results[0];
                calcServiceAgentAID = dfd.getName();
                
                //enable calcGui.combobox and submit button
                //calcGui.enabledGUI();
            }	
            else {
                shoeGui.appendLog("[supplierSender]Agent "+getLocalName()+" did not find any " + serviceType + " service");
               // suppGui.popup("No " + serviceType + " agent service found!");
            }
  	}
  	catch (FIPAException fe) {
            fe.printStackTrace();
  	}
        shoeGui.appendLog("\n");        
    }
    
    public void addShoes(Supplier supp) {
        shoeGui.clearLog();
        shoeGui.appendLog("[supplierSender]Receiving updated request object from suppGui");
        shoeGui.appendLog("[supplierSender]Cloths information. color: " + supp.getClothColor() + " size: " + supp.getClothSize() + "quantity: " + supp.getClothQuantity());
        shoeGui.appendLog("[supplierSender]Pants information.  size: " + supp.getPantSize() + "quantity: " + supp.getPantQuantity());
        shoeGui.appendLog("[supplierSender]Onepiece information. color: " + supp.getOnepieceColor() + " size: " + supp.getOnepieceSize() + "quantity: " + supp.getOnepieceQuantity());
        shoeGui.appendLog("\n");
        
        //Send messages to "cap - CalcAgentPlus"  
        shoeGui.appendLog("[supplierSender]Preparing ACL msg: INFORM");   
	ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        
        shoeGui.appendLog("[supplierSender]Convert Supplier supp to String Base64");   
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
        msg.addReceiver(calcServiceAgentAID);
        send(msg);
        
        shoeGui.appendLog("[supplierSender]Sending Message to cap");
        shoeGui.appendLog("[supplierSender]Message content [Base64 string]: " + strObj);   
    }
    
}
