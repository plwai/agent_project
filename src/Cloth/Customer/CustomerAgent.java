package Cloth.Customer;

import Cloth.Seller.Inventory;
import Cloth.Seller.ItemProperties;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.Property;
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
	//First set-up answering behaviour	
	addBehaviour(new CyclicBehaviour(this) 
	{
            public void action() {
                ACLMessage msg = receive();
                CustomerRequest cusReq= new CustomerRequest();
                String action;
                
		if (msg != null) 
                {   
                    String msgContent = msg.getContent();
                    
                    System.out.println("\n[CustomerAgent] Message Received");
                    System.out.println("[CustomerAgent] Sender Agent   : " + msg.getSender());
                    System.out.println("[CustomerAgent] Message content [Base64 string]: " + msgContent);                    
                    
                    try
                    {
                        cusReq = (CustomerRequest)deserializeObjectFromString(msgContent);
                    }
                    catch(Exception ex)
                    {
                        System.out.println("\n[CustomerAgent] StrToObj conversion error: " + ex.getMessage());
                    }
                    
                    if(cusReq.getAction().equals("Confirm")){
                        OrderList orders = cusReq.getOrder();
                        Connection con;
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

                        ACLMessage reply = new ACLMessage(ACLMessage.NOT_UNDERSTOOD);

                        reply.addReceiver(msg.getSender()); //get from envelope                       

                        reply.setContent(strObj);                        
                        send(reply);

                        System.out.println("\n[CustomerAgent] Sending Message!");
                        System.out.println("[CustomerAgent] Receiver Agent                 : " + msg.getSender());
                        System.out.println("[CustomerAgent] Message content [Base64 string]: " + msg.getContent());                                  
                    } 
                    else if (cusReq.getAction().equals("Load Display")) {
                        ClothList clothes = new ClothList();
                        clothes.loadData(cusReq.getClothType());
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

                        ACLMessage reply = new ACLMessage(ACLMessage.NOT_UNDERSTOOD);

                        reply.addReceiver(msg.getSender()); //get from envelope                       

                        reply.setContent(strObj);                        
                        send(reply);

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
                //setBaju();
                System.out.println("[CustomerAgent] CyclicBehaviour Block");
                block();
            }
	});
    }
    
}
