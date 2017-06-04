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
public class ShoesSupplierAgent extends Agent{
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
         String serviceName = "Shoes Supplier Agent";
        
  	try {
            DFAgentDescription dfd = new DFAgentDescription();
            dfd.setName(getAID());
            ServiceDescription sd = new ServiceDescription();
            sd.setName(serviceName);
            sd.setType("Shoes basic-Supplier");
            sd.addProperties(new Property("Shoes1", "slipper"));
            sd.addProperties(new Property("Shoes2", "ballerina"));
            sd.addProperties(new Property("Shoes3", "boots"));
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
                //create numbers object
                shoesSupplier shoesSupp = new shoesSupplier();
                
		if (msg != null) 
                {   
                    String msgContent = msg.getContent();
                    
                    System.out.println("\n[ShoesSupplierAgent] Message Received");
                    System.out.println("[ShoesSupplierAgent] Sender Agent   : " + msg.getSender());
                    System.out.println("[ShoesSupplierAgent] Message content [Base64 string]: " + msgContent);                    
                    
                    try
                    {
                        shoesSupp = (shoesSupplier)deserializeObjectFromString(msgContent);
                    }
                    catch(Exception ex)
                    {
                        System.out.println("\n[ShoesSupplierAgent] StrToObj conversion error: " + ex.getMessage());
                    }
                    
                    
//                    to determine if the quantity box is not zero

                        if(shoesSupp.getSlipperQty() != 0) {
                              
                            System.out.println("\nSlipper Information\n---------------------");
                            System.out.println("Size: " + shoesSupp.getSlipperSize() + "\nColor: " +shoesSupp.getSlipperColor() + "\nQuantity: " +shoesSupp.getSlipperQty());
                            String strObj = ""; 
                            try
                            {
                                strObj = serializeObjectToString(shoesSupp);
                            }
                            catch (Exception ex)
                            {
                                System.out.println("\n[SupplierAgent] ObjToStr conversion error: " + ex.getMessage());
                            }

                            ACLMessage reply = new ACLMessage(ACLMessage.INFORM);

                            reply.addReceiver(msg.getSender()); //get from envelope                       

                            reply.setContent(strObj);                        
                            send(reply);

                            System.out.println("\n[SupplierAgent] Sending Message from Slipper!");
                            System.out.println("[SupplierAgent] Receiver Agent                 : " + msg.getSender());
                            System.out.println("[SupplierAgent] Message content [Base64 string]: " + msg.getContent());
                            
                        }//end if
                        if(shoesSupp.getBallerinaQty() != 0){
                              
                            System.out.println("\nBallerina Information\n---------------------");
                            System.out.println("Size: " + shoesSupp.getBallerinaSize() + "\nQuantity: " +shoesSupp.getBallerinaQty());
                            String strObj = ""; 
                            try
                            {
                                strObj = serializeObjectToString(shoesSupp);
                            }
                            catch (Exception ex)
                            {
                                System.out.println("\n[SupplierAgent] ObjToStr conversion error: " + ex.getMessage());
                            }

                            ACLMessage reply = new ACLMessage(ACLMessage.INFORM);

                            reply.addReceiver(msg.getSender()); //get from envelope                       

                            reply.setContent(strObj);                        
                            send(reply);

                            System.out.println("\n[SupplierAgent] Sending Message from Ballerina!");
                            System.out.println("[SupplierAgent] Receiver Agent                 : " + msg.getSender());
                            System.out.println("[SupplierAgent] Message content [Base64 string]: " + msg.getContent());
                        
                        }
                        if(shoesSupp.getBootQty() != 0){
                            System.out.println("\nBoot Information\n---------------------");
                            System.out.println("Size: " + shoesSupp.getBootSize() + "\nColor: " +shoesSupp.getBootColor() + "\nQuantity: " +shoesSupp.getBootQty());
                            String strObj = ""; 
                            try
                            {
                                strObj = serializeObjectToString(shoesSupp);
                            }
                            catch (Exception ex)
                            {
                                System.out.println("\n[SupplierAgent] ObjToStr conversion error: " + ex.getMessage());
                            }

                            ACLMessage reply = new ACLMessage(ACLMessage.INFORM);

                            reply.addReceiver(msg.getSender()); //get from envelope                       

                            reply.setContent(strObj);                        
                            send(reply);

                            System.out.println("\n[SupplierAgent] Sending Message from Boot!");
                            System.out.println("[SupplierAgent] Receiver Agent                 : " + msg.getSender());
                            System.out.println("[SupplierAgent] Message content [Base64 string]: " + msg.getContent());
                        
                        }
                        else{
                            
                            shoesSupp.setSuccess(false);
                            shoesSupp.setInfo("Product add ERROR");
                        
                            System.out.println("Product add ERROR");
                        

                            String strObj = ""; 
                            try
                            {
                                strObj = serializeObjectToString(shoesSupp);
                            }
                            catch (Exception ex)
                            {
                                System.out.println("\n[SupplierAgent] ObjToStr conversion error: " + ex.getMessage());
                            }

                            ACLMessage reply = new ACLMessage(ACLMessage.NOT_UNDERSTOOD);

                            reply.addReceiver(msg.getSender()); //get from envelope                       

                            reply.setContent(strObj);                        
                            send(reply);

                            System.out.println("\n[SupplierAgent] Sending Message!");
                            System.out.println("[SupplierAgent] Receiver Agent                 : " + msg.getSender());
                            System.out.println("[SupplierAgent] Message content [Base64 string]: " + msg.getContent());   
                            System.out.println("\n[SupplierAgent] Clothes product fail to be updated!");                                              
                        
                        
                        }//end else
                    
                    
		}
                
                System.out.println("[ShoesSupplierAgent] CyclicBehaviour Block");
                block();
            }
	});
     }
    
}
