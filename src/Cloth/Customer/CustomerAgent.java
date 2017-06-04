package Cloth.Customer;

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
import java.util.ArrayList;
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
    private order order = new order();
    private Cloth baju;
    private orderList list;
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
            DFAgentDescription dfd = new DFAgentDescription();
            dfd.setName(getAID());
            ServiceDescription sd = new ServiceDescription();
            sd.setName(serviceName);
            sd.setType("Customer");
            sd.addProperties(new Property("Action1", "Add Product to Carts"));
            sd.addProperties(new Property("Action2", "Remove Product from Carts"));
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
                String action= new String();
                //CustomerRequest request= new CustomerRequest();
                
		if (msg != null) 
                {   
                    String msgContent = msg.getContent();
                    
                    System.out.println("\n[CustomerAgentPlus] Message Received");
                    System.out.println("[CustomerAgentPlus] Sender Agent   : " + msg.getSender());
                    System.out.println("[CustomerAgentPlus] Message content [Base64 string]: " + msgContent);                    
                    
                    try
                    {
                        //request = (CustomerRequest)deserializeObjectFromString(msgContent);
                        action = (String)deserializeObjectFromString(msgContent);
                    }
                    catch(Exception ex)
                    {
                        System.out.println("\n[CustomerAgentPlus] StrToObj conversion error: " + ex.getMessage());
                    }
                    
                    if (action.equals("Add Product to Carts")) {
                        //action
//                        order order = new order();
//                        orderList newOrder = request.getNewOrder();
//                        order=request.getOrder();
//                        order.addProduct(newOrder);
//                        float totalPrice = order.getTotalPrice();
//                        totalPrice=totalPrice+(newOrder.getQuantity()*newOrder.getBaju().getPrice());
//                        order.setTotalPrice(totalPrice);
//                        request.setOrder(order);
//                        request.setSuccess(true);
                        action="Product has been add to cart";
                        String strObj = ""; 
                        try
                        {
                            strObj = serializeObjectToString(action);
                        }
                        catch (Exception ex)
                        {
                            System.out.println("\n[CalcAgentPlus] ObjToStr conversion error: " + ex.getMessage());
                        }

                        ACLMessage reply = new ACLMessage(ACLMessage.INFORM);

                        reply.addReceiver(msg.getSender()); //get from envelope                       

                        reply.setContent(strObj);                        
                        send(reply);

                        System.out.println("\n[CustomerAgentPlus] Sending Message!");
                        System.out.println("[CustomerAgentPlus] Receiver Agent                 : " + msg.getSender());
                        System.out.println("[CustomerAgentPlus] Message content [Base64 string]: " + msg.getContent());
                    }
                    else if(action.equals("Remove Product from Carts")){
                        
                        action="Product has been remove from cart";
                        String strObj = ""; 
                        try
                        {
                            strObj = serializeObjectToString(action);
                            //strObj = serializeObjectToString(request);
                        }
                        catch (Exception ex)
                        {
                            System.out.println("\n[CalcAgentPlus] ObjToStr conversion error: " + ex.getMessage());
                        }

                        ACLMessage reply = new ACLMessage(ACLMessage.NOT_UNDERSTOOD);

                        reply.addReceiver(msg.getSender()); //get from envelope                       

                        reply.setContent(strObj);                        
                        send(reply);

                        System.out.println("\n[CustomerAgentPlus] Sending Message!");
                        System.out.println("[CustomerAgentPlus] Receiver Agent                 : " + msg.getSender());
                        System.out.println("[CustomerAgentPlus] Message content [Base64 string]: " + msg.getContent());                                  
                    } 
                    else if(action.equals("Confirm")){
                        
                        action="Send Order to seller";
                        String strObj = ""; 
                        try
                        {
                            strObj = serializeObjectToString(action);
                            //strObj = serializeObjectToString(request);
                        }
                        catch (Exception ex)
                        {
                            System.out.println("\n[CalcAgentPlus] ObjToStr conversion error: " + ex.getMessage());
                        }

                        ACLMessage reply = new ACLMessage(ACLMessage.NOT_UNDERSTOOD);

                        reply.addReceiver(msg.getSender()); //get from envelope                       

                        reply.setContent(strObj);                        
                        send(reply);

                        System.out.println("\n[CustomerAgentPlus] Sending Message!");
                        System.out.println("[CustomerAgentPlus] Receiver Agent                 : " + msg.getSender());
                        System.out.println("[CustomerAgentPlus] Message content [Base64 string]: " + msg.getContent());                                  
                    } 
                    else{
                        
                        action="Error";
                        String strObj = ""; 
                        try
                        {
                            strObj = serializeObjectToString(action);
                            //strObj = serializeObjectToString(request);
                        }
                        catch (Exception ex)
                        {
                            System.out.println("\n[CalcAgentPlus] ObjToStr conversion error: " + ex.getMessage());
                        }

                        ACLMessage reply = new ACLMessage(ACLMessage.NOT_UNDERSTOOD);

                        reply.addReceiver(msg.getSender()); //get from envelope                       

                        reply.setContent(strObj);                        
                        send(reply);

                        System.out.println("\n[CustomerAgentPlus] Sending Message!");
                        System.out.println("[CustomerAgentPlus] Receiver Agent                 : " + msg.getSender());
                        System.out.println("[CustomerAgentPlus] Message content [Base64 string]: " + msg.getContent());                                  
                    } 
		}
                //setBaju();
                System.out.println("[CustomerAgentPlus] CyclicBehaviour Block");
                block();
            }
	});
    }
    
}
