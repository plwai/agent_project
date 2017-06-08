/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Cloth.Inventory;

import Cloth.Customer.Cloth;
import Cloth.Customer.CustomerAgent;
import Cloth.Customer.Order;
import Cloth.Customer.OrderList;
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

/**
 *
 * @author Wai Pai Lee
 */
public class RequestAgent extends Agent{
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
        String serviceName = "request-agent";
        
  	try {
            DFAgentDescription dfd = new DFAgentDescription();
            dfd.setName(getAID());
            ServiceDescription sd = new ServiceDescription();
            sd.setName(serviceName);
            sd.setType("basic-request");
            sd.addProperties(new Property("service", "get all request"));
            sd.addProperties(new Property("service", "insert request"));
            dfd.addServices(sd);
  		
            DFService.register(this, dfd);
  	}
  	catch (FIPAException fe) {
            fe.printStackTrace();
  	}
        
        try {
            Class.forName("org.apache.derby.jdbc.ClientDriver");
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(RequestAgent.class.getName()).log(Level.SEVERE, null, ex);
        }

	//First set-up answering behaviour	
	addBehaviour(new CyclicBehaviour(this) 
	{
            public void action() {
                ACLMessage msg = receive();
                DBRequest req = new DBRequest();
                
		if (msg != null) 
                {   
                    String msgContent = msg.getContent();
                    
                    System.out.println("\n[RequestAgent] Message Received");
                    System.out.println("[RequestAgent] Sender Agent   : " + msg.getSender());
                    System.out.println("[RequestAgent] Message content [Base64 string]: " + msgContent);                    
                    
                    try
                    {
                        req = (DBRequest)deserializeObjectFromString(msgContent);
                    }
                    catch(Exception ex)
                    {
                        System.out.println("\n[RequestAgent] StrToObj conversion error: " + ex.getMessage());
                    }
                    
                    Inventory inventory = new Inventory();
                    Connection con;
                    try {
                        con = DriverManager.getConnection(  "jdbc:derby://localhost:1527/sample","app","app");
                        Statement stmt=con.createStatement(); 
                        ResultSet rs=stmt.executeQuery("select * from INVENTORY");  

                        while(rs.next()){  
                            inventory.addItemSummary(new ItemProperties(rs.getString(2), rs.getString(3), rs.getString(5), rs.getString(4), rs.getInt(6), rs.getInt(1), rs.getInt(7)));
                        }  

                        con.close();
                    } catch (SQLException ex) {
                        Logger.getLogger(RequestAgent.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    
                    if(msg.getPerformative() == ACLMessage.INFORM){
                        if(req.getRequest().equals("buy product")){
                            req.setRequest("insert request");
                            String strObj = ""; 
                            try
                            {
                                strObj = serializeObjectToString(req);
                            }
                            catch (Exception ex)
                            {
                                System.out.println("\n[RequestAgent] ObjToStr conversion error: " + ex.getMessage());
                            }

                            ACLMessage reply = new ACLMessage(ACLMessage.INFORM);

                            
                            Iterator it = msg.getAllReplyTo(); 
                            reply.addReplyTo((AID)it.next());
                            reply.addReceiver((AID)it.next()); //get from envelope    
                            reply.setContent(strObj);                        
                            send(reply);

                            System.out.println("\n[RequestAgent] Sending Message!");
                            System.out.println("[RequestAgent] Receiver Agent                 : " + msg.getSender());
                            System.out.println("[RequestAgent] Message content [Base64 string]: " + msg.getContent());
                        }
                    }
                    else {
                        if(req.getRequest().equals("get all request")){                 
                        try {  
                            con=DriverManager.getConnection(  "jdbc:derby://localhost:1527/sample","app","app");
                            Statement stmt=con.createStatement(); 
                            ResultSet rs=stmt.executeQuery("select * from RECEIPT");  

                            while(rs.next()){  
                                CustomerRequests cusReq = new CustomerRequests(rs.getInt(1));

                                Connection con2=DriverManager.getConnection(  "jdbc:derby://localhost:1527/sample","app","app");

                                Statement stmt2=con2.createStatement(); 
                                ResultSet rs2=stmt2.executeQuery("select * from ORDERS WHERE RECEIPTID="+Integer.toString(rs.getInt(1)));  


                                while(rs2.next()){  
                                    cusReq.addRequest(new Request(rs2.getInt(2), rs2.getInt(3)));
                                }  

                                con2.close();  
                                
                                req.addCusReq(cusReq);
                            }  

                            con.close();  
                        } catch (SQLException ex) {
                            Logger.getLogger(Inventory.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        
                        String strObj = ""; 
                        try
                        {
                            strObj = serializeObjectToString(req);
                        }
                        catch (Exception ex)
                        {
                            System.out.println("\n[RequestAgent] ObjToStr conversion error: " + ex.getMessage());
                        }

                        ACLMessage reply = new ACLMessage(ACLMessage.INFORM);

                        reply.addReceiver(msg.getSender()); //get from envelope                       
                        reply.addReplyTo((AID)msg.getAllReplyTo().next());
                        reply.setContent(strObj);                        
                        send(reply);

                        System.out.println("\n[RequestAgent] Sending Message!");
                        System.out.println("[RequestAgent] Receiver Agent                 : " + msg.getSender());
                        System.out.println("[RequestAgent] Message content [Base64 string]: " + msg.getContent());
                    }
                    else if(req.getRequest().equals("insert request")){
                        getServiceAgent();
                        OrderList orders = req.getCusReq().getOrder();
                        int receiptId, orderId;
                        
                        try {
                            con = DriverManager.getConnection(  "jdbc:derby://localhost:1527/sample","app","app");
                            
                            Statement stmt=con.createStatement(); 
                            ResultSet rs=stmt.executeQuery("SELECT * FROM RECEIPT ORDER BY ID DESC");  

                            if(rs.next()){
                                receiptId = rs.getInt(1) + 1;
                            }
                            else {
                                receiptId = 1;
                            }
                            
                            stmt=con.createStatement(); 
                            rs=stmt.executeQuery("SELECT * FROM ORDERS ORDER BY ID DESC");  

                            if(rs.next()){
                                orderId = rs.getInt(1) + 1;
                            }
                            else {
                                orderId = 1;
                            }
                            
                            System.out.println(orderId);
                            
                            String query = " insert into RECEIPT (id)"
                                    + " values (?)";
                                    
                            PreparedStatement preparedStmt = con.prepareStatement(query);
                            preparedStmt.setInt (1, receiptId);

                            preparedStmt.execute();
                            
                            for(Order order: orders.getProductList()) {
                                try {  
                                    Cloth cloth = order.getBaju();

                                    query = " insert into ORDERS (id, ProductId, Quantity, ReceiptID)"
                                    + " values (?, ?, ?, ?)";

                                    preparedStmt = con.prepareStatement(query);
                                    preparedStmt.setInt (1, orderId);
                                    preparedStmt.setInt (2, cloth.getId());
                                    preparedStmt.setInt (3, order.getQuantity());
                                    preparedStmt.setInt (4, receiptId);

                                    preparedStmt.execute();

                                    orderId++;
                                } catch (SQLException ex) {
                                    Logger.getLogger(Inventory.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }

                            con.close();
                            
                        } catch (SQLException ex) {
                            Logger.getLogger(CustomerAgent.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        
                        req.setRequest("buy product");
                        
                        String strObj = ""; 
                        try
                        {
                            strObj = serializeObjectToString(req);
                        }
                        catch (Exception ex)
                        {
                            System.out.println("\n[CustomerAgent] ObjToStr conversion error: " + ex.getMessage());
                        }

                        ACLMessage requestMsg = new ACLMessage(ACLMessage.REQUEST);

                        requestMsg.addReceiver(AIDMap.get(req.getRequest()));
                        requestMsg.setContent(strObj);
                        requestMsg.addReplyTo((AID)msg.getAllReplyTo().next());
                        requestMsg.addReplyTo(msg.getSender());

                        send(requestMsg);

                        System.out.println("\n[RequestAgent] Sending Message!");
                        System.out.println("[RequestAgent] Receiver Agent                 : " + msg.getSender());
                        System.out.println("[RequestAgent] Message content [Base64 string]: " + msg.getContent());
                    }
                    else {

                        String strObj = ""; 
                        try
                        {
                            strObj = serializeObjectToString(req);
                        }
                        catch (Exception ex)
                        {
                            System.out.println("\n[RequestAgent] ObjToStr conversion error: " + ex.getMessage());
                        }

                        ACLMessage reply = new ACLMessage(ACLMessage.NOT_UNDERSTOOD);

                        reply.addReceiver(msg.getSender()); //get from envelope                       

                        reply.setContent(strObj);                        
                        send(reply);

                        System.out.println("\n[RequestAgent] Sending Message!");
                        System.out.println("[RequestAgent] Receiver Agent                 : " + msg.getSender());
                        System.out.println("[RequestAgent] Message content [Base64 string]: " + msg.getContent());                                  
                    }    
                    }
                    
                }
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
    }
}
