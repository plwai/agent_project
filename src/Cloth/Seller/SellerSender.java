/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Cloth.Seller;

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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import org.apache.commons.codec.binary.Base64;

/**
 *
 * @author Wai Pai Lee
 */
public class SellerSender extends Agent{
    private SellerGUI sellerGui;
    static final Base64 base64 = new Base64();
    private Map<String, AID> AIDMap = new HashMap<String, AID>();;
    private List<String> serviceList = new ArrayList<String>();;
    
    //object to string
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
    
    //string to object
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
        sellerGui = new SellerGUI(this);
	sellerGui.showGui();
        
        //for receiving calculation result	
	addBehaviour(new CyclicBehaviour(this) 
	{            
            public void action() 
            { 
                ACLMessage msg= receive();
                
		if (msg != null) {
                    sellerGui.appendLog("\n");
                    sellerGui.appendLog("Message received from " + msg.getSender());
                    
                    String msgContent = msg.getContent();
                    sellerGui.appendLog("Message content [Base64 string]: " + msgContent);
                    sellerGui.appendLog("Msg performative: " + ACLMessage.getPerformative(msg.getPerformative()));                   
                    
                    try
                    {
                        SellerStore store = (SellerStore)deserializeObjectFromString(msgContent);
                        
                        if (store.getIsSuccess()) {                                           
                            sellerGui.appendLog("Store - info   : " + store.getInfo());
                        } else {
                            sellerGui.appendLog("Store - info   : " + store.getInfo());
                            sellerGui.appendLog("Msg performative: " + ACLMessage.getPerformative(msg.getPerformative()));
                        }
                        
                        sellerGui.showResult(store);                                                
                    }
                    catch(Exception ex)
                    {
                        sellerGui.appendLog("StrToObj conversion error: " + ex.getMessage());
                    }
                }
                
                sellerGui.appendLog("[CalcAgentPlus] CyclicBehaviour Block");
                block();
            }
        });
    }
    
    public void getCalcServiceAgent() {
  	try {
            String service = null;
            String serviceType = "basic-seller";
            sellerGui.appendLog("Searching the DF/Yellow-Pages for " + serviceType + " service");
            sellerGui.appendLog("Service properties: check-summary");
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
  		sellerGui.appendLog("Agent "+getLocalName()+" found the following " + serviceType + " services:");
                
  		for (int i = 0; i < results.length; ++i) {
                    DFAgentDescription dfd = results[i];
                    AID agentAID = dfd.getName();
                    for(Iterator it = dfd.getAllServices(); it.hasNext();) {
                        ServiceDescription serviceDesc = (ServiceDescription)it.next();
                        
                        for(Iterator it2 = serviceDesc.getAllProperties();it2.hasNext();) {
                            Property p = (Property)(it2.next());
                            service = p.getValue().toString();
                        }
                    }
                    sellerGui.popup("Agent name: " + agentAID);
                    sellerGui.appendLog("Agent name: " + agentAID);
                    sellerGui.appendLog("Service: " + service + " found"); 
                    sellerGui.appendLog("\n");  
                    
                    AIDMap.put(service, dfd.getName());
                    
                    serviceList.add(service);
  		}
                
                //enable sellerGui.combobox and submit button
                sellerGui.enabledGUI();
            }	
            else {
                sellerGui.appendLog("Agent "+getLocalName()+" did not find any " + serviceType + " service");
                sellerGui.popup("No " + serviceType + " agent service found!");
            }
            
            sellerGui.addService(serviceList);
  	}
  	catch (FIPAException fe) {
            fe.printStackTrace();
  	}
        sellerGui.appendLog("\n");        
    }
    
    public void requestSummary(SellerStore storeObj) {
        sellerGui.clearLog();
        sellerGui.appendLog("Receiving summary request and Store object from CalcGUI");
        sellerGui.appendLog("Store object - Service: " + storeObj.getServiceType());   
        sellerGui.appendLog("\n");
        
        sellerGui.appendLog("Preparing ACL msg: INFORM");   
	ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        
        sellerGui.appendLog("Convert Store obj to String Base64");   
        String strObj = ""; 
        try
        {
            strObj = serializeObjectToString(storeObj);
        }
        catch (Exception ex)
        {
            System.out.println("\n[SellerSender] ObjToStr conversion error: " + ex.getMessage());
        }
        
	msg.setContent(strObj);
        
        msg.addReceiver(AIDMap.get(storeObj.getServiceType()));
        send(msg);
        
        sellerGui.appendLog("Sending Message to cap");
        sellerGui.appendLog("Message content [Base64 string]: " + strObj);   
    }
}
