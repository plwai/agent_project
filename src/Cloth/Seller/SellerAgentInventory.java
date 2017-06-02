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
import jade.domain.FIPAAgentManagement.SearchConstraints;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import org.apache.commons.codec.binary.Base64;

public class SellerAgentInventory extends Agent 
{	
    static final Base64 base64 = new Base64();
    private Map<String, AID> AIDMap = new HashMap<String, AID>();;
    static Inventory storeInventory = new Inventory();
    
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
        
        // Initialize fake database
        ItemProperties item = new ItemProperties();

        item.setId(1);
        item.setItemColor("Blue");
        item.setItemName("The T-shirt");
        item.setItemQuantity("50");
        item.setItemSize("L");
        item.setItemType("shirt");
        
        storeInventory.addItem(item);
        
        ItemProperties item2 = new ItemProperties();
        
        item2.setId(2);
        item2.setItemColor("Red");
        item2.setItemName("The T-shirt");
        item2.setItemQuantity("5");
        item2.setItemSize("L");
        item2.setItemType("shirt");
        
        storeInventory.addItem(item2);
        
        ItemProperties item3 = new ItemProperties();
        
        item3.setId(3);
        item3.setItemColor("Black");
        item3.setItemName("The Pants");
        item3.setItemQuantity("10");
        item3.setItemSize("L");
        item3.setItemType("pant");

        storeInventory.addItem(item3);
        
  	try {
            DFAgentDescription dfd = new DFAgentDescription();
            dfd.setName(getAID());
            ServiceDescription sd = new ServiceDescription();
            sd.setName(serviceName);
            sd.setType("basic-seller");
            sd.addProperties(new Property("service", "check inventory"));
            sd.addProperties(new Property("service", "restock"));
            sd.addProperties(new Property("service", "add new product"));
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
                    
                    System.out.println("\n[SellerAgentInventory] Message Received");
                    System.out.println("[SellerAgentInventory] Sender Agent   : " + msg.getSender());
                    System.out.println("[SellerAgentInventory] Message content [Base64 string]: " + msgContent);                    
                    
                    try
                    {
                        store = (SellerStore)deserializeObjectFromString(msgContent);
                    }
                    catch(Exception ex)
                    {
                        System.out.println("\n[SellerAgentInventory] StrToObj conversion error: " + ex.getMessage());
                    }
                    
                    if (store.getServiceType().equals("check inventory")) {
                        store.setSummary(storeInventory);
                        store.setIsSuccess(true);
                        store.setInfo("Successfully Get Inventory");

                        String strObj = ""; 
                        try
                        {
                            strObj = serializeObjectToString(store);
                        }
                        catch (Exception ex)
                        {
                            System.out.println("\n[SellerAgentInventory] ObjToStr conversion error: " + ex.getMessage());
                        }

                        ACLMessage reply = new ACLMessage(ACLMessage.INFORM);

                        reply.addReceiver(msg.getSender()); //get from envelope                       

                        reply.setContent(strObj);                        
                        send(reply);

                        System.out.println("\n[SellerAgentInventory] Sending Message!");
                        System.out.println("[SellerAgentInventory] Receiver Agent                 : " + msg.getSender());
                        System.out.println("[SellerAgentInventory] Message content [Base64 string]: " + msg.getContent());
                    }
                    else if(store.getServiceType().equals("restock")) {
                        int productId = store.getRestockId();
                        int quantity = store.getQuantity();
                        List<ItemProperties> itemSummary = storeInventory.getItemSummary();
                        
                        if(productId <= itemSummary.size()) {
                            itemSummary.get(productId).setItemQuantity(Integer.toString(quantity));
                            store.setIsSuccess(true);
                            store.setInfo("Successfully Restock");
                        }
                        else {
                            store.setIsSuccess(false);
                            store.setInfo("Fail to Restock");
                        }
                        
                        String strObj = ""; 
                        try
                        {
                            strObj = serializeObjectToString(store);
                        }
                        catch (Exception ex)
                        {
                            System.out.println("\n[SellerAgentInventory] ObjToStr conversion error: " + ex.getMessage());
                        }
                        
                        ACLMessage reply = new ACLMessage(ACLMessage.INFORM);

                        reply.addReceiver(msg.getSender()); //get from envelope                       

                        reply.setContent(strObj);                        
                        send(reply);

                        System.out.println("\n[SellerAgentInventory] Sending Message!");
                        System.out.println("[SellerAgentInventory] Receiver Agent                 : " + msg.getSender());
                        System.out.println("[SellerAgentInventory] Message content [Base64 string]: " + msg.getContent());
                    }
                    else if(store.getServiceType().equals("add new product")) {
                        ItemProperties newItem = store.getNewItem();
                        newItem.setId(storeInventory.getItemSummary().size() + 1);

                        storeInventory.addItem(newItem);
                        store.setIsSuccess(true);
                        store.setInfo("Successfully Added New Item");
                        
                        String strObj = ""; 
                        try
                        {
                            strObj = serializeObjectToString(store);
                        }
                        catch (Exception ex)
                        {
                            System.out.println("\n[SellerAgentInventory] ObjToStr conversion error: " + ex.getMessage());
                        }
                        
                        ACLMessage reply = new ACLMessage(ACLMessage.INFORM);

                        reply.addReceiver(msg.getSender()); //get from envelope                       

                        reply.setContent(strObj);                        
                        send(reply);

                        System.out.println("\n[SellerAgentInventory] Sending Message!");
                        System.out.println("[SellerAgentInventory] Receiver Agent                 : " + msg.getSender());
                        System.out.println("[SellerAgentInventory] Message content [Base64 string]: " + msg.getContent());
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
                            System.out.println("\n[SellerAgentInventory] ObjToStr conversion error: " + ex.getMessage());
                        }

                        ACLMessage reply = new ACLMessage(ACLMessage.NOT_UNDERSTOOD);

                        reply.addReceiver(msg.getSender()); //get from envelope                       

                        reply.setContent(strObj);                        
                        send(reply);

                        System.out.println("\n[SellerAgentInventory] Sending Message!");
                        System.out.println("[SellerAgentInventory] Receiver Agent                 : " + msg.getSender());
                        System.out.println("[SellerAgentInventory] Message content [Base64 string]: " + msg.getContent());                                  
                    }                    
		}
                
                System.out.println("[SellerAgentInventory] CyclicBehaviour Block");
                block();
            }
	});
    }
}
