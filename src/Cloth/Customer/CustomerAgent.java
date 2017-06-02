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
    //Dummy Data for cloth
    private ArrayList <Cloth> Baju = new ArrayList();
    
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
            ByteArrayInputStream arrayInputStream = new ByteArrayInputStream((byte[]) base64.decode(objectString));
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
            
            sd.addProperties(new Property("type1", "Add Product to Cart"));
            sd.addProperties(new Property("type2", "Remove Product from Cart"));
            sd.addProperties(new Property("type3", "Disply Prodcut"));
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
                String clothType = new String();
                
		if (msg != null) 
                {   
                    String msgContent = msg.getContent();
                    
                    System.out.println("\n[CalcAgentPlus] Message Received");
                    System.out.println("[CalcAgentPlus] Sender Agent   : " + msg.getSender());
                    System.out.println("[CalcAgentPlus] Message content [Base64 string]: " + msgContent);                    
                    
                    try
                    {
                        clothType = (String)deserializeObjectFromString(msgContent);
                    }
                    catch(Exception ex)
                    {
                        System.out.println("\n[CalcAgentPlus] StrToObj conversion error: " + ex.getMessage());
                    }
                    
                    if (clothType.equals("One-Piece")) {
                        String Type = clothType;
                        System.out.println(Type);
                        String strObj = ""; 
                        try
                        {
                            strObj = serializeObjectToString(clothType);
                        }
                        catch (Exception ex)
                        {
                            System.out.println("\n[CalcAgentPlus] ObjToStr conversion error: " + ex.getMessage());
                        }

                        ACLMessage reply = new ACLMessage(ACLMessage.INFORM);

                        reply.addReceiver(msg.getSender()); //get from envelope                       

                        reply.setContent(strObj);                        
                        send(reply);

                        System.out.println("\n[CalcAgentPlus] Sending Message!");
                        System.out.println("[CalcAgentPlus] Receiver Agent                 : " + msg.getSender());
                        System.out.println("[CalcAgentPlus] Message content [Base64 string]: " + msg.getContent());
                    }
                    else {
                        

                        String strObj = ""; 
                        try
                        {
                            strObj = serializeObjectToString(clothType);
                        }
                        catch (Exception ex)
                        {
                            System.out.println("\n[CalcAgentPlus] ObjToStr conversion error: " + ex.getMessage());
                        }

                        ACLMessage reply = new ACLMessage(ACLMessage.NOT_UNDERSTOOD);

                        reply.addReceiver(msg.getSender()); //get from envelope                       

                        reply.setContent(strObj);                        
                        send(reply);

                        System.out.println("\n[CalcAgentPlus] Sending Message!");
                        System.out.println("[CalcAgentPlus] Receiver Agent                 : " + msg.getSender());
                        System.out.println("[CalcAgentPlus] Message content [Base64 string]: " + msg.getContent());                                  
                    }                    
		}
                //setBaju();
                System.out.println("[CalcAgentPlus] CyclicBehaviour Block");
                block();
            }
	});
    }
    
}
