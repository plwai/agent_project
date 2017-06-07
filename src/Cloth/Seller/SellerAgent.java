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
import Cloth.Inventory.DBRequest;
import Cloth.Inventory.ItemProperties;
import Cloth.Inventory.Inventory;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.*;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.Envelope;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import org.apache.commons.codec.binary.Base64;

public class SellerAgent extends Agent 
{	
    static final Base64 base64 = new Base64();
    private Map<String, AID> AIDMap = new HashMap<String, AID>();
    private List<String> serviceList = new ArrayList<String>();
    
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
            sd.addProperties(new Property("service", "check inventory"));
            sd.addProperties(new Property("service", "restock"));
            sd.addProperties(new Property("service", "add new product"));
            sd.addProperties(new Property("service", "view request"));
            dfd.addServices(sd);
  		
            DFService.register(this, dfd);
  	}
  	catch (FIPAException fe) {
            fe.printStackTrace();
  	}
        
        getServiceAgent();
	//First set-up answering behaviour	
	addBehaviour(new CyclicBehaviour(this) 
	{
            public void action() {
                ACLMessage msg = receive();
                SellerStore store = new SellerStore();
                DBRequest dbReq = new DBRequest();
                
		if (msg != null) 
                {   
                    String msgContent = msg.getContent();
                    
                    
                    System.out.println("\n[SellerAgent] Message Received");
                    System.out.println("[SellerAgent] Sender Agent   : " + msg.getSender());
                    System.out.println("[SellerAgent] Message content [Base64 string]: " + msgContent);                    
                    
                    if(msg.getPerformative() == ACLMessage.INFORM){
                        try
                        {
                            dbReq = (DBRequest)deserializeObjectFromString(msgContent);
                        }
                        catch(Exception ex)
                        {
                            System.out.println("\n[SellerAgent] StrToObj conversion error: " + ex.getMessage());
                        }
                        
                        if(dbReq.getRequest().equals("get all product")) {
                            store.setServiceType("check inventory");
                            store.setSummary(dbReq.getInventory());
                            store.setIsSuccess(true);
                            store.setInfo("Successfully Get Inventory");

                            String strObj = ""; 
                            try
                            {
                                strObj = serializeObjectToString(store);
                            }
                            catch (Exception ex)
                            {
                                System.out.println("\n[SellerAgent] ObjToStr conversion error: " + ex.getMessage());
                            }

                            ACLMessage reply = new ACLMessage(ACLMessage.INFORM);

                            reply.addReceiver((AID)msg.getAllReplyTo().next()); //get from envelope                       

                            reply.setContent(strObj);                        
                            send(reply);

                            System.out.println("\n[SellerAgent] Sending Message!");
                            System.out.println("[SellerAgent] Receiver Agent                 : " + msg.getSender());
                            System.out.println("[SellerAgent] Message content [Base64 string]: " + msg.getContent());
                        }
                        else if(dbReq.getRequest().equals("update product quantity")) {
                            store.setServiceType("restock");
                            if(dbReq.isSuccess()){
                                store.setIsSuccess(true);
                                store.setInfo("Successfully Restock");
                            }
                            else{
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
                                System.out.println("\n[SellerAgent] ObjToStr conversion error: " + ex.getMessage());
                            }

                            ACLMessage reply = new ACLMessage(ACLMessage.INFORM);

                            reply.addReceiver((AID)msg.getAllReplyTo().next()); //get from envelope                       

                            reply.setContent(strObj);                        
                            send(reply);

                            System.out.println("\n[SellerAgent] Sending Message!");
                            System.out.println("[SellerAgent] Receiver Agent                 : " + msg.getSender());
                            System.out.println("[SellerAgent] Message content [Base64 string]: " + msg.getContent());
                        }
                        else if(dbReq.getRequest().equals("insert product")) {
                            store.setServiceType("add new product");
                            store.setIsSuccess(true);
                            store.setInfo("Successfully Added New Item");

                            String strObj = ""; 
                            try
                            {
                                strObj = serializeObjectToString(store);
                            }
                            catch (Exception ex)
                            {
                                System.out.println("\n[SellerAgent] ObjToStr conversion error: " + ex.getMessage());
                            }

                            ACLMessage reply = new ACLMessage(ACLMessage.INFORM);

                            reply.addReceiver((AID)msg.getAllReplyTo().next()); //get from envelope                       

                            reply.setContent(strObj);                        
                            send(reply);

                            System.out.println("\n[SellerAgent] Sending Message!");
                            System.out.println("[SellerAgent] Receiver Agent                 : " + msg.getSender());
                            System.out.println("[SellerAgent] Message content [Base64 string]: " + msg.getContent());
                        }
                        else if(dbReq.getRequest().equals("get all request")) {
                            store.setServiceType("view request");
                            store.setRequestList(dbReq.getCusRequestList());
                            store.setIsSuccess(true);
                            store.setInfo("Successfully Load Customer Order Request");

                            String strObj = ""; 
                            try
                            {
                                strObj = serializeObjectToString(store);
                            }
                            catch (Exception ex)
                            {
                                System.out.println("\n[SellerAgent] ObjToStr conversion error: " + ex.getMessage());
                            }

                            ACLMessage reply = new ACLMessage(ACLMessage.INFORM);

                            reply.addReceiver((AID)msg.getAllReplyTo().next()); //get from envelope                       

                            reply.setContent(strObj);                        
                            send(reply);

                            System.out.println("\n[SellerAgent] Sending Message!");
                            System.out.println("[SellerAgent] Receiver Agent                 : " + msg.getSender());
                            System.out.println("[SellerAgent] Message content [Base64 string]: " + msg.getContent());
                        }
                    }
                    else {
                         try
                        {
                            store = (SellerStore)deserializeObjectFromString(msgContent);
                        }
                        catch(Exception ex)
                        {
                            System.out.println("\n[SellerAgent] StrToObj conversion error: " + ex.getMessage());
                        }


                        if (store.getServiceType().equals("check inventory")) {
                            dbReq.setRequest("get all product");

                            String strObj = ""; 
                            try
                            {
                                strObj = serializeObjectToString(dbReq);
                            }
                            catch (Exception ex)
                            {
                                System.out.println("\n[SellerAgent] ObjToStr conversion error: " + ex.getMessage());
                            }

                            ACLMessage requestMsg = new ACLMessage(ACLMessage.REQUEST);

                            requestMsg.addReceiver(AIDMap.get(dbReq.getRequest()));
                            requestMsg.setContent(strObj);
                            requestMsg.addReplyTo(msg.getSender());

                            send(requestMsg);

                            System.out.println("\n[SellerAgent] Sending Message!");
                            System.out.println("[SellerAgent] Receiver Agent                 : " + msg.getSender());
                            System.out.println("[SellerAgent] Message content [Base64 string]: " + msg.getContent());
                        }
                        else if(store.getServiceType().equals("restock")) {
                            int productId = store.getRestockId();
                            int quantity = store.getQuantity();

                            dbReq.setRequest("update product quantity");
                            dbReq.setQuantity(quantity);
                            dbReq.setId(productId);

                            String strObj = ""; 
                            try
                            {
                                strObj = serializeObjectToString(dbReq);
                            }
                            catch (Exception ex)
                            {
                                System.out.println("\n[SellerAgent] ObjToStr conversion error: " + ex.getMessage());
                            }

                            ACLMessage requestMsg = new ACLMessage(ACLMessage.REQUEST);

                            requestMsg.addReceiver(AIDMap.get(dbReq.getRequest()));
                            requestMsg.setContent(strObj);
                            requestMsg.addReplyTo(msg.getSender());

                            send(requestMsg);
                            

                            System.out.println("\n[SellerAgent] Sending Message!");
                            System.out.println("[SellerAgent] Receiver Agent                 : " + msg.getSender());
                            System.out.println("[SellerAgent] Message content [Base64 string]: " + msg.getContent());
                        }
                        else if(store.getServiceType().equals("add new product")) {
                            dbReq.setRequest("insert product");
                            dbReq.setItem(store.getNewItem());
                            
                            String strObj = ""; 
                            try
                            {
                                strObj = serializeObjectToString(dbReq);
                            }
                            catch (Exception ex)
                            {
                                System.out.println("\n[SellerAgent] ObjToStr conversion error: " + ex.getMessage());
                            }

                            ACLMessage requestMsg = new ACLMessage(ACLMessage.REQUEST);

                            requestMsg.addReceiver(AIDMap.get(dbReq.getRequest()));
                            requestMsg.setContent(strObj);
                            requestMsg.addReplyTo(msg.getSender());

                            send(requestMsg);

                            System.out.println("\n[SellerAgent] Sending Message!");
                            System.out.println("[SellerAgent] Receiver Agent                 : " + msg.getSender());
                            System.out.println("[SellerAgent] Message content [Base64 string]: " + msg.getContent());
                        } 
                        else if(store.getServiceType().equals("view request")) {
                            dbReq.setRequest("get all request");

                            String strObj = ""; 
                            try
                            {
                                strObj = serializeObjectToString(dbReq);
                            }
                            catch (Exception ex)
                            {
                                System.out.println("\n[SellerAgent] ObjToStr conversion error: " + ex.getMessage());
                            }

                            ACLMessage requestMsg = new ACLMessage(ACLMessage.REQUEST);

                            requestMsg.addReceiver(AIDMap.get(dbReq.getRequest()));
                            requestMsg.setContent(strObj);
                            requestMsg.addReplyTo(msg.getSender());

                            send(requestMsg);

                            System.out.println("\n[SellerAgent] Sending Message!");
                            System.out.println("[SellerAgent] Receiver Agent                 : " + msg.getSender());
                            System.out.println("[SellerAgent] Message content [Base64 string]: " + msg.getContent());
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
                                System.out.println("\n[SellerAgent] ObjToStr conversion error: " + ex.getMessage());
                            }

                            ACLMessage reply = new ACLMessage(ACLMessage.NOT_UNDERSTOOD);

                            reply.addReceiver(msg.getSender()); //get from envelope                       

                            reply.setContent(strObj);                        
                            send(reply);

                            System.out.println("\n[SellerAgent] Sending Message!");
                            System.out.println("[SellerAgent] Receiver Agent                 : " + msg.getSender());
                            System.out.println("[SellerAgent] Message content [Base64 string]: " + msg.getContent());                                  
                        }                    
                    }
                   
		}
                
                System.out.println("[SellerAgent] CyclicBehaviour Block");
                block();
            }
	});
    }
    
     public void getServiceAgent() {
  	try {
            String service = null;
            String serviceType = "basic-inventory";
            System.out.println("Searching the DF/Yellow-Pages for " + serviceType + " service");
            AIDMap.clear();
            serviceList.clear();
            
            // Build the description used as template for the search
            DFAgentDescription template = new DFAgentDescription();
            
            ServiceDescription templateSd = new ServiceDescription();
            templateSd.setType(serviceType);
            template.addServices(templateSd);
  		
            SearchConstraints sc = new SearchConstraints();
            // We want to receive 10 results at most
            sc.setMaxResults(new Long(10));
  		
            DFAgentDescription[] results = DFService.search(this, template, sc);
            if (results.length > 0) {
  		System.out.println("Agent "+getLocalName()+" found the following " + serviceType + " services:");
                
  		for (int i = 0; i < results.length; ++i) {
                    DFAgentDescription dfd = results[i];
                    AID agentAID = dfd.getName();
                    for(Iterator it = dfd.getAllServices(); it.hasNext();) {
                        ServiceDescription serviceDesc = (ServiceDescription)it.next();
                        
                        for(Iterator it2 = serviceDesc.getAllProperties();it2.hasNext();) {
                            Property p = (Property)(it2.next());
                            service = p.getValue().toString();

                            AIDMap.put(service, dfd.getName());

                            serviceList.add(service);
                        }
                    }
  		}
            }	
            else {
                System.out.println("No Service Found");
            }
  	}
  	catch (FIPAException fe) {
            fe.printStackTrace();
  	}    
        
        try {
            String service = null;
            String serviceType = "basic-request";
            System.out.println("Searching the DF/Yellow-Pages for " + serviceType + " service");
            
            // Build the description used as template for the search
            DFAgentDescription template = new DFAgentDescription();
            
            ServiceDescription templateSd = new ServiceDescription();
            templateSd.setType(serviceType);
            template.addServices(templateSd);
  		
            SearchConstraints sc = new SearchConstraints();
            // We want to receive 10 results at most
            sc.setMaxResults(new Long(10));
  		
            DFAgentDescription[] results = DFService.search(this, template, sc);
            if (results.length > 0) {
  		System.out.println("Agent "+getLocalName()+" found the following " + serviceType + " services:");
                
  		for (int i = 0; i < results.length; ++i) {
                    DFAgentDescription dfd = results[i];
                    AID agentAID = dfd.getName();
                    for(Iterator it = dfd.getAllServices(); it.hasNext();) {
                        ServiceDescription serviceDesc = (ServiceDescription)it.next();
                        
                        for(Iterator it2 = serviceDesc.getAllProperties();it2.hasNext();) {
                            Property p = (Property)(it2.next());
                            service = p.getValue().toString();

                            AIDMap.put(service, dfd.getName());

                            serviceList.add(service);
                        }
                    }
  		}
            }	
            else {
                System.out.println("No Service Found");
            }
  	}
  	catch (FIPAException fe) {
            fe.printStackTrace();
  	}   
    }
}
