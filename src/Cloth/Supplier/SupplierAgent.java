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
public class SupplierAgent extends Agent{
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
         String serviceName = "Cloth Supplier Agent";
        
  	try {
            DFAgentDescription dfd = new DFAgentDescription();
            dfd.setName(getAID());
            ServiceDescription sd = new ServiceDescription();
            sd.setName(serviceName);
            sd.setType("basic-Supplier");
            sd.addProperties(new Property("Clothing1", "cloth"));
            sd.addProperties(new Property("Clothing2", "panth"));
            sd.addProperties(new Property("Clothing3", "onepiece"));
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
                Supplier supp = new Supplier();
                
		if (msg != null) 
                {   
                    String msgContent = msg.getContent();
                    
                    System.out.println("\n[supplierAgent] Message Received");
                    System.out.println("[supplierAgent] Sender Agent   : " + msg.getSender());
                    System.out.println("[supplierAgent] Message content [Base64 string]: " + msgContent);                    
                    
                    try
                    {
                        supp = (Supplier)deserializeObjectFromString(msgContent);
                    }
                    catch(Exception ex)
                    {
                        System.out.println("\n[supplierAgent] StrToObj conversion error: " + ex.getMessage());
                    }
                    
                    
//                    to determine if the quantity box is not zero

                        if(supp.getClothQuantity() != 0) {
                              
                            //int ShirtInfo = supp.getClothQuantity();
                            //System.out.println("[SupplierAgent] Added Shirt quantity is " + ShirtInfo);
                            System.out.println("\nShirt Information");
                            System.out.println("\nSize: " + supp.getClothSize() + "\nColor: " +supp.getClothColor() + "\nQuantity: " +supp.getClothQuantity());
                            String strObj = ""; 
                            try
                            {
                                strObj = serializeObjectToString(supp);
                            }
                            catch (Exception ex)
                            {
                                System.out.println("\n[SupplierAgent] ObjToStr conversion error: " + ex.getMessage());
                            }

                            ACLMessage reply = new ACLMessage(ACLMessage.INFORM);

                            reply.addReceiver(msg.getSender()); //get from envelope                       

                            reply.setContent(strObj);                        
                            send(reply);

                            System.out.println("\n[SupplierAgent] Sending Message from Shirt!");
                            System.out.println("[SupplierAgent] Receiver Agent                 : " + msg.getSender());
                            System.out.println("[SupplierAgent] Message content [Base64 string]: " + msg.getContent());
                            
                        }//end if
                        if(supp.getPantQuantity() != 0){
                              
                            //int pantInfo = supp.getPantQuantity();
                            //System.out.println("[SupplierAgent] Added Pants quantity is " + pantInfo);
                            System.out.println("\nPants Information");
                            System.out.println("\nSize: " + supp.getPantSize() + "\nQuantity: " +supp.getClothQuantity());
                            String strObj = ""; 
                            try
                            {
                                strObj = serializeObjectToString(supp);
                            }
                            catch (Exception ex)
                            {
                                System.out.println("\n[SupplierAgent] ObjToStr conversion error: " + ex.getMessage());
                            }

                            ACLMessage reply = new ACLMessage(ACLMessage.INFORM);

                            reply.addReceiver(msg.getSender()); //get from envelope                       

                            reply.setContent(strObj);                        
                            send(reply);

                            System.out.println("\n[SupplierAgent] Sending Message from Pants!");
                            System.out.println("[SupplierAgent] Receiver Agent                 : " + msg.getSender());
                            System.out.println("[SupplierAgent] Message content [Base64 string]: " + msg.getContent());
                        
                        }
                        if(supp.getOnepieceQuantity() != 0){
                            System.out.println("\nOnepiece Information");
                            System.out.println("\nSize: " + supp.getOnepieceSize() + "\nColor: " +supp.getOnepieceColor() + "\nQuantity: " +supp.getOnepieceQuantity());
                            String strObj = ""; 
                            try
                            {
                                strObj = serializeObjectToString(supp);
                            }
                            catch (Exception ex)
                            {
                                System.out.println("\n[SupplierAgent] ObjToStr conversion error: " + ex.getMessage());
                            }

                            ACLMessage reply = new ACLMessage(ACLMessage.INFORM);

                            reply.addReceiver(msg.getSender()); //get from envelope                       

                            reply.setContent(strObj);                        
                            send(reply);

                            System.out.println("\n[SupplierAgent] Sending Message from Onepiece!");
                            System.out.println("[SupplierAgent] Receiver Agent                 : " + msg.getSender());
                            System.out.println("[SupplierAgent] Message content [Base64 string]: " + msg.getContent());
                        
                        }
                        else{
                            
                            supp.setSuccess(false);
                            supp.setInfo("Product add ERROR");
                        
                            System.out.println("Product add ERROR");
                        

                            String strObj = ""; 
                            try
                            {
                                strObj = serializeObjectToString(supp);
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
                
                System.out.println("[supplierAgent] CyclicBehaviour Block End");
                block();
            }
	});
     }
    
}
