package Cloth.Customer;

import Cloth.Inventory.DBRequest;
import Cloth.Inventory.Inventory;
import Cloth.Inventory.ItemProperties;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
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
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import org.apache.commons.codec.binary.Base64;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author on
 */
public class CustomerAgent extends Agent{
    static final Base64 base64 = new Base64();
    private OrderList order = new OrderList();
    private Cloth baju;
    private Order list;
    private CustomerRequest customerReq=new CustomerRequest();
    private Map<String, AID> AIDMap = new HashMap<String, AID>();
    private List<String> serviceList = new ArrayList<String>();
    
//    private void setBaju(){
//        Cloth b = new Cloth("Jubah A","Blue","One-Piece","s");
//        Baju.add(b);
//    }
    
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
        String serviceName = "Customer-agent";
        
        try {  
            Class.forName("org.apache.derby.jdbc.ClientDriver");
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Inventory.class.getName()).log(Level.SEVERE, null, ex);
        }
        
  	try {
            DFAgentDescription dfd = new DFAgentDescription();
            dfd.setName(getAID());
            ServiceDescription sd = new ServiceDescription();
            sd.setName(serviceName);
            sd.setType("Customer");
            sd.addProperties(new Property("Action2", "Load Display"));
            sd.addProperties(new Property("Action3", "Confirm"));
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
                CustomerRequest cusReq= new CustomerRequest();
                String action;
                DBRequest dbReq = new DBRequest();
                
		if (msg != null) 
                {   
                    String msgContent = msg.getContent();
                    
                    System.out.println("\n[CustomerAgent] Message Received");
                    System.out.println("[CustomerAgent] Sender Agent   : " + msg.getSender());
                    System.out.println("[CustomerAgent] Message content [Base64 string]: " + msgContent);                    
                    
                    if(msg.getPerformative() == ACLMessage.INFORM){
                        try
                        {
                            dbReq = (DBRequest)deserializeObjectFromString(msgContent);
                        }
                        catch(Exception ex)
                        {
                            System.out.println("\n[CustomerAgent] StrToObj conversion error: " + ex.getMessage());
                        }
                        
                        if(dbReq.getRequest().equals("insert request")) {
                            cusReq.setAction("Send Order to seller");
                            String strObj = ""; 
                            try
                            {
                                strObj = serializeObjectToString(cusReq);
                            }
                            catch (Exception ex)
                            {
                                System.out.println("\n[CalcAgentPlus] ObjToStr conversion error: " + ex.getMessage());
                            }

                            ACLMessage reply = new ACLMessage(ACLMessage.INFORM);

                            reply.addReceiver((AID)msg.getAllReplyTo().next()); //get from envelope                       

                            reply.setContent(strObj);                        
                            send(reply);

                            System.out.println("\n[CustomerAgent] Sending Message!");
                            System.out.println("[CustomerAgent] Receiver Agent                 : " + msg.getSender());
                            System.out.println("[CustomerAgent] Message content [Base64 string]: " + msg.getContent());
                        }
                        else if(dbReq.getRequest().equals("get all product")){
                            ClothList clothes = new ClothList();
                            cusReq = dbReq.getCusReq();
                            
                            for(ItemProperties item: dbReq.getInventory().getItemSummary()){
                                clothes.addBaju(new Cloth(item.getItemName(), item.getItemColor(), item.getItemType(), item.getItemSize(), item.getItemPrice(), item.getId()));
                            }
                            cusReq.setClothes(clothes);
                            cusReq.setAction("Load clothes");
                            
                            String strObj = ""; 
                            try
                            {
                                strObj = serializeObjectToString(cusReq);
                            }
                            catch (Exception ex)
                            {
                                System.out.println("\n[CalcAgentPlus] ObjToStr conversion error: " + ex.getMessage());
                            }

                            ACLMessage reply = new ACLMessage(ACLMessage.INFORM);

                            reply.addReceiver((AID)msg.getAllReplyTo().next()); //get from envelope                       

                            reply.setContent(strObj);                        
                            send(reply);

                            System.out.println("\n[CustomerAgent] Sending Message!");
                            System.out.println("[CustomerAgent] Receiver Agent                 : " + msg.getSender());
                            System.out.println("[CustomerAgent] Message content [Base64 string]: " + msg.getContent());
                        }
                    
                    }
                    else {
                        try
                        {
                            cusReq = (CustomerRequest)deserializeObjectFromString(msgContent);
                        }
                        catch(Exception ex)
                        {
                            System.out.println("\n[CustomerAgent] StrToObj conversion error: " + ex.getMessage());
                        }

                        if(cusReq.getAction().equals("Confirm")){
                            dbReq.setRequest("insert request");
                            dbReq.addReq(cusReq);
                            
                            String strObj = ""; 
                            try
                            {
                                strObj = serializeObjectToString(dbReq);
                            }
                            catch (Exception ex)
                            {
                                System.out.println("\n[CustomerAgent] ObjToStr conversion error: " + ex.getMessage());
                            }

                            ACLMessage requestMsg = new ACLMessage(ACLMessage.REQUEST);

                            requestMsg.addReceiver(AIDMap.get(dbReq.getRequest()));
                            requestMsg.setContent(strObj);
                            requestMsg.addReplyTo(msg.getSender());

                            send(requestMsg);

                            System.out.println("\n[CustomerAgent] Sending Message!asdsadas");
                            System.out.println("[CustomerAgent] Receiver Agent                 : " + msg.getSender());
                            System.out.println("[CustomerAgent] Message content [Base64 string]: " + msg.getContent());
                        } 
                        else if (cusReq.getAction().equals("Load Display")) {   
                            dbReq.setRequest("get all product");
                            dbReq.addReq(cusReq);
                            
                            String strObj = ""; 
                            try
                            {
                                strObj = serializeObjectToString(dbReq);
                            }
                            catch (Exception ex)
                            {
                                System.out.println("\n[CalcAgentPlus] ObjToStr conversion error: " + ex.getMessage());
                            }

                            ACLMessage requestMsg = new ACLMessage(ACLMessage.REQUEST);

                            requestMsg.addReceiver(AIDMap.get(dbReq.getRequest()));
                            requestMsg.setContent(strObj);
                            requestMsg.addReplyTo(msg.getSender());

                            send(requestMsg);

                            System.out.println("\n[CustomerAgent] Sending Message!");
                            System.out.println("[CustomerAgent] Receiver Agent                 : " + msg.getSender());
                            System.out.println("[CustomerAgent] Message content [Base64 string]: " + msg.getContent());
                        }
                        else{

                            cusReq.setAction("Error");
                            String strObj = ""; 
                            try
                            {
                                strObj = serializeObjectToString(cusReq);
                            }
                            catch (Exception ex)
                            {
                                System.out.println("\n[CalcAgentPlus] ObjToStr conversion error: " + ex.getMessage());
                            }

                            ACLMessage reply = new ACLMessage(ACLMessage.NOT_UNDERSTOOD);

                            reply.addReceiver(msg.getSender()); //get from envelope                       

                            reply.setContent(strObj);                        
                            send(reply);

                            System.out.println("\n[CustomerAgent] Sending Message!");
                            System.out.println("[CustomerAgent] Receiver Agent                 : " + msg.getSender());
                            System.out.println("[CustomerAgent] Message content [Base64 string]: " + msg.getContent());                                  
                        } 
                    }
                    
		}
                //setBaju();
                System.out.println("[CustomerAgent] CyclicBehaviour Block");
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
