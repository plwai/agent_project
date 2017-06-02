/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Cloth.Seller;

/**
 *
 * @author Wai Pai Lee
 */
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.*;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.Property;
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

public class SellerAgentSummary extends Agent 
{	
    static final Base64 base64 = new Base64();
    
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
        String serviceName = "seller-agent";
        
  	try {
            DFAgentDescription dfd = new DFAgentDescription();
            dfd.setName(getAID());
            ServiceDescription sd = new ServiceDescription();
            sd.setName(serviceName);
            sd.setType("basic-seller");
            sd.addProperties(new Property("service", "check summary"));
            dfd.addServices(sd);
  		
            DFService.register(this, dfd);
  	}
  	catch (FIPAException fe) {
            fe.printStackTrace();
  	}
	//First set-up answering behaviour	
	addBehaviour(new CyclicBehaviour(this) 
	{
            public void action() {
                ACLMessage msg = receive();
                SellerStore store = new SellerStore();
                
		if (msg != null) 
                {   
                    String msgContent = msg.getContent();
                    
                    System.out.println("\n[SellerAgentSummary] Message Received");
                    System.out.println("[SellerAgentSummary] Sender Agent   : " + msg.getSender());
                    System.out.println("[SellerAgentSummary] Message content [Base64 string]: " + msgContent);                    
                    
                    try
                    {
                        store = (SellerStore)deserializeObjectFromString(msgContent);
                    }
                    catch(Exception ex)
                    {
                        System.out.println("\n[SellerAgentSummary] StrToObj conversion error: " + ex.getMessage());
                    }
                    
                    if (store.getServiceType().equals("check summary")) {
                        ItemProperties item = new ItemProperties();
                        Summary summary = new Summary();
                        
                        item.setItemColor("test");
                        item.setItemName("test");
                        item.setItemQuantity("test");
                        item.setItemSells("test");
                        item.setItemSize("test");
                        item.setItemType("test");
                        
                        summary.addItem(item);
                        store.setSummary(summary);
                        store.setIsSuccess(true);
                        store.setInfo("Successfully Summarize");

                        String strObj = ""; 
                        try
                        {
                            strObj = serializeObjectToString(store);
                        }
                        catch (Exception ex)
                        {
                            System.out.println("\n[SellerAgentSummary] ObjToStr conversion error: " + ex.getMessage());
                        }

                        ACLMessage reply = new ACLMessage(ACLMessage.INFORM);

                        reply.addReceiver(msg.getSender()); //get from envelope                       

                        reply.setContent(strObj);                        
                        send(reply);

                        System.out.println("\n[SellerAgentSummary] Sending Message!");
                        System.out.println("[SellerAgentSummary] Receiver Agent                 : " + msg.getSender());
                        System.out.println("[SellerAgentSummary] Message content [Base64 string]: " + msg.getContent());
                    } else {
                        store.setIsSuccess(false);
                        store.setInfo("Service " + store.getServiceType() + " is not available");

                        String strObj = ""; 
                        try
                        {
                            strObj = serializeObjectToString(store);
                        }
                        catch (Exception ex)
                        {
                            System.out.println("\n[SellerAgentSummary] ObjToStr conversion error: " + ex.getMessage());
                        }

                        ACLMessage reply = new ACLMessage(ACLMessage.NOT_UNDERSTOOD);

                        reply.addReceiver(msg.getSender()); //get from envelope                       

                        reply.setContent(strObj);                        
                        send(reply);

                        System.out.println("\n[SellerAgentSummary] Sending Message!");
                        System.out.println("[SellerAgentSummary] Receiver Agent                 : " + msg.getSender());
                        System.out.println("[SellerAgentSummary] Message content [Base64 string]: " + msg.getContent());                                  
                    }                    
		}
                
                System.out.println("[SellerAgentSummary] CyclicBehaviour Block");
                block();
            }
	});
    }
}
