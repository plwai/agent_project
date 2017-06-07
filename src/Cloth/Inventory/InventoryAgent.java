/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Cloth.Inventory;

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
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import org.apache.commons.codec.binary.Base64;

public class InventoryAgent extends Agent 
{	
    static final Base64 base64 = new Base64();
    private Map<String, AID> AIDMap = new HashMap<String, AID>();
    
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
        String serviceName = "inventory-agent";
        
  	try {
            DFAgentDescription dfd = new DFAgentDescription();
            dfd.setName(getAID());
            ServiceDescription sd = new ServiceDescription();
            sd.setName(serviceName);
            sd.setType("basic-inventory");
            sd.addProperties(new Property("service", "get all product"));
            sd.addProperties(new Property("service", "update product quantity"));
            sd.addProperties(new Property("service", "insert product"));
            dfd.addServices(sd);
  		
            DFService.register(this, dfd);
  	}
  	catch (FIPAException fe) {
            fe.printStackTrace();
  	}
        
        try {
            Class.forName("org.apache.derby.jdbc.ClientDriver");
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(InventoryAgent.class.getName()).log(Level.SEVERE, null, ex);
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
                    
                    System.out.println("\n[InventoryAgent] Message Received");
                    System.out.println("[InventoryAgent] Sender Agent   : " + msg.getSender());
                    System.out.println("[InventoryAgent] Message content [Base64 string]: " + msgContent);                    
                    
                    try
                    {
                        req = (DBRequest)deserializeObjectFromString(msgContent);
                    }
                    catch(Exception ex)
                    {
                        System.out.println("\n[InventoryAgent] StrToObj conversion error: " + ex.getMessage());
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
                        Logger.getLogger(InventoryAgent.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    
                    if(req.getRequest().equals("get all product")) {
                        req.setInventory(inventory);
                        
                        String strObj = ""; 
                        try
                        {
                            strObj = serializeObjectToString(req);
                        }
                        catch (Exception ex)
                        {
                            System.out.println("\n[SellerAgentInventory] ObjToStr conversion error: " + ex.getMessage());
                        }

                        ACLMessage reply = new ACLMessage(ACLMessage.INFORM);

                        reply.addReceiver(msg.getSender()); //get from envelope                       

                        reply.setContent(strObj);
                        reply.addReplyTo((AID)msg.getAllReplyTo().next());
                        send(reply);

                        System.out.println("\n[InventoryAgent] Sending Message!");
                        System.out.println("[InventoryAgent] Receiver Agent                 : " + msg.getSender());
                        System.out.println("[InventoryAgent] Message content [Base64 string]: " + msg.getContent());
                    }
                    else if(req.getRequest().equals("update product quantity")) {
                        int id = req.getId();
                        int quantity = req.getQuantity();
                        
                        if(id <= inventory.getItemSummary().size()){
                            inventory.getItemSummary().get(id - 1).setItemQuantity(inventory.getItemSummary().get(id - 1).getItemQuantity() + quantity);
                        
                            try {  
                                con = DriverManager.getConnection(  "jdbc:derby://localhost:1527/sample","app","app");
                                String query = " update INVENTORY set QUANTITY = ? where id = ?";

                                PreparedStatement preparedStmt = con.prepareStatement(query);

                                preparedStmt.setInt (1, inventory.getItemSummary().get(id - 1).getItemQuantity());
                                preparedStmt.setInt (2, inventory.getItemSummary().get(id - 1).getId());

                                preparedStmt.executeUpdate();

                                con.close();  
                            } catch (SQLException ex) {
                                Logger.getLogger(InventoryAgent.class.getName()).log(Level.SEVERE, null, ex);
                            }

                            req.setInventory(inventory);
                            req.setSuccess(true);

                            String strObj = ""; 
                            try
                            {
                                strObj = serializeObjectToString(req);
                            }
                            catch (Exception ex)
                            {
                                System.out.println("\n[SellerAgentInventory] ObjToStr conversion error: " + ex.getMessage());
                            }

                            ACLMessage reply = new ACLMessage(ACLMessage.INFORM);

                            reply.addReceiver(msg.getSender()); //get from envelope                       

                            reply.setContent(strObj);
                            reply.addReplyTo((AID)msg.getAllReplyTo().next());
                            send(reply);
                        }
                        else {
                            req.setSuccess(false);
                            String strObj = ""; 
                            try
                            {
                                strObj = serializeObjectToString(req);
                            }
                            catch (Exception ex)
                            {
                                System.out.println("\n[SellerAgentInventory] ObjToStr conversion error: " + ex.getMessage());
                            }

                            ACLMessage reply = new ACLMessage(ACLMessage.INFORM);

                            reply.addReceiver(msg.getSender()); //get from envelope                       

                            reply.setContent(strObj);
                            reply.addReplyTo((AID)msg.getAllReplyTo().next());
                            send(reply);
                        }

                        System.out.println("\n[InventoryAgent] Sending Message!");
                        System.out.println("[InventoryAgent] Receiver Agent                 : " + msg.getSender());
                        System.out.println("[InventoryAgent] Message content [Base64 string]: " + msg.getContent());
                    }
                    else if(req.getRequest().equals("insert product")) {
                        ItemProperties item = req.getItem();
                        item.setId(inventory.getItemSummary().size() + 1);
                        inventory.getItemSummary().add(item);
        
                        try {  
                            con=DriverManager.getConnection(  "jdbc:derby://localhost:1527/sample","app","app");

                            String query = " insert into INVENTORY (ID, NAME, TYPE, COLOR, SIZE, QUANTITY, PRICE)"
                            + " values (?, ?, ?, ?, ?, ?, ?)";

                            PreparedStatement preparedStmt = con.prepareStatement(query);
                            preparedStmt.setInt (1, item.getId());
                            preparedStmt.setString (2, item.getItemName());
                            preparedStmt.setString (3, item.getItemType());
                            preparedStmt.setString (4, item.getItemColor());
                            preparedStmt.setString (5, item.getItemSize());
                            preparedStmt.setInt    (6, item.getItemQuantity());
                            preparedStmt.setFloat    (7, item.getItemPrice());

                            preparedStmt.execute();

                            con.close();  
                        } catch (SQLException ex) {
                            Logger.getLogger(Inventory.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        
                        req.setInventory(inventory);
                        
                        String strObj = ""; 
                        try
                        {
                            strObj = serializeObjectToString(req);
                        }
                        catch (Exception ex)
                        {
                            System.out.println("\n[SellerAgentInventory] ObjToStr conversion error: " + ex.getMessage());
                        }

                        ACLMessage reply = new ACLMessage(ACLMessage.INFORM);

                        reply.addReceiver(msg.getSender()); //get from envelope                       
                        reply.addReplyTo((AID)msg.getAllReplyTo().next());
                        reply.setContent(strObj);                        
                        send(reply);

                        System.out.println("\n[InventoryAgent] Sending Message!");
                        System.out.println("[InventoryAgent] Receiver Agent                 : " + msg.getSender());
                        System.out.println("[InventoryAgent] Message content [Base64 string]: " + msg.getContent());
                    }
                    else {

                        String strObj = ""; 
                        try
                        {
                            strObj = serializeObjectToString(req);
                        }
                        catch (Exception ex)
                        {
                            System.out.println("\n[InventoryAgent] ObjToStr conversion error: " + ex.getMessage());
                        }

                        ACLMessage reply = new ACLMessage(ACLMessage.NOT_UNDERSTOOD);

                        reply.addReceiver(msg.getSender()); //get from envelope                       

                        reply.setContent(strObj);                        
                        send(reply);

                        System.out.println("\n[InventoryAgent] Sending Message!");
                        System.out.println("[InventoryAgent] Receiver Agent                 : " + msg.getSender());
                        System.out.println("[InventoryAgent] Message content [Base64 string]: " + msg.getContent());                                  
                    }                    
		}
                
                System.out.println("[InventoryAgent] CyclicBehaviour Block");
                block();
            }
	});
    }
}