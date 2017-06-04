package Cloth.Customer;


import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.*;
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
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import org.apache.commons.codec.binary.Base64;
import java.util.ArrayList;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author on
 */
public class CustomerSender extends Agent {
    private CustomerGUI customerGui;
    private Cart cart;
    private CustomerRequest customerRequest;
    //private ArrayList<ClothDiscription> showClothInfo;
    private ClothDiscription showCloth;
    static final Base64 base64 = new Base64();
    private AID customerServiceAgentAID = null;
    private ArrayList<Cloth> Baju = new ArrayList();
    
    private void setBaju(){
        Cloth b = new Cloth("Jubah A","Blue","One-Piece","S", 20);
        Baju.add(b);
        b = new Cloth("Jubah A","Blue","One-Piece","XL", 20);
        Baju.add(b);
        Cloth a = new Cloth("Jubah B","Blue","One-Piece","S", 20);
        Baju.add(a);
        Cloth c = new Cloth("T--","Blue","T-Shirt","S", 21);
        Baju.add(c);
    }
    //not using agent
    private CustomerRequest sendtoagent(CustomerRequest request){
        if (request.getAction().equals("Add Product to Carts")) {
            //action
            customerGui.appendLog("add product");
            order order = new order();
            orderList newOrder = request.getNewOrder();
            order=request.getOrder();
            order.addProduct(newOrder);
            float totalPrice = order.getTotalPrice();
            totalPrice=totalPrice+(newOrder.getQuantity()*newOrder.getBaju().getPrice());
            order.setTotalPrice(totalPrice);
            request.setOrder(order);
            request.setSuccess(true);
            
        }
        else if(request.getAction().equals("Remove Product from Carts")){
            //action
            customerGui.appendLog("remove product");
            ArrayList<orderList> order = request.getOrder().getProductList();
            float minusPrice= order.get(request.getRemoveProduct()).getQuantity()*order.get(request.getRemoveProduct()).getBaju().getPrice();
            request.getOrder().setTotalPrice(request.getOrder().getTotalPrice()-minusPrice);
            order.remove(request.getRemoveProduct());
            request.getOrder().setProductCart(order);
        }
        else if(request.getAction().equals("Confirm")){
            //action
            customerGui.appendLog("Confirm product");
            //Object request need to send
            //request send order to seller
            request.getOrder().getProductList().removeAll(request.getOrder().getProductList());
            customerGui.showResult("Order send to seller");
            
        }
        return request;
    }
    //
    private String[] getBajuName(ArrayList<Cloth> baju, String ClothType){
        
        ArrayList<String> bajuname = new ArrayList();
        for(int i=0; i<baju.size(); i++)
        {
            if(baju.get(i).getType().equals(ClothType)){
                if(bajuname.size()==0){
                    bajuname.add(baju.get(i).getName());
                }
                else{
                    boolean s=false;
                    for(int y=0; y<bajuname.size(); y++){
                        if(bajuname.get(y).equals(baju.get(i).getName())){
                            s=true;
                        }
                    }
                    if(!s){
                        bajuname.add(baju.get(i).getName());
                    }
                }
            }
        }
        
        String[] BajuName = bajuname.toArray(new String[0]);
        return BajuName;
    }
    private String[] getBajuNameAll(ArrayList<Cloth> baju){
        
        ArrayList<String> bajuname = new ArrayList();
        for(int i=0; i<baju.size(); i++)
        {
            if(bajuname.size()==0){
                bajuname.add(baju.get(i).getName());
            }
            else{
                boolean s=false;
                for(int y=0; y<bajuname.size(); y++){
                    if(bajuname.get(y).equals(baju.get(i).getName())){
                        s=true;
                    }
                }
                if(!s){
                    bajuname.add(baju.get(i).getName());
                }
            }
        }
        
        String[] BajuName = bajuname.toArray(new String[0]);
        return BajuName;
    }
    
    
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
    
    public void requestCatalog(String clothType){
        //ArrayList <Cloth> Baju = new ArrayList();
        setBaju();
        String[] BajuName = getBajuName(Baju, clothType);
        customerGui.clearLog();
        customerGui.appendLog("Receiving cloth catalog request from CustomerGUI");
        customerGui.appendLog("Cloth Type : " + clothType);  
        customerGui.appendLog("\n");
        customerGui.displayCalatalog(BajuName);
    }
    
    public void ProductToCart(String action, orderList newOrder, int removeProdcut){
        customerGui.clearLog();
        customerGui.appendLog("Request add product to cart data from gui");
        customerGui.appendLog("Action: " + action);
        customerGui.appendLog("RemoveProduct: " + removeProdcut);
        customerGui.appendLog("Cloth Name: " + newOrder.getBaju().getName());
        customerGui.appendLog("Cloth Size: " + newOrder.getBaju().getSize());
        customerGui.appendLog("Cloth Color: " + newOrder.getBaju().getColor());
        customerGui.appendLog("Cloth Price: RM " + newOrder.getBaju().getPrice());
        customerGui.appendLog("Quantity: " + newOrder.getQuantity());
        customerRequest.setRemoveProduct(removeProdcut);
        customerRequest.setAction(action);
        customerRequest.setNewOrder(newOrder);  
        customerGui.appendLog("ok");
        //Send messages to "cap - CustomerAgent"
        
        
        customerGui.appendLog("Preparing ACL msg: INFORM");
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        
        
        customerGui.appendLog("Convert CustomerRequest obj to String Base64");   
        String strObj = ""; 
        try
        {
            strObj = serializeObjectToString(action);
        }
        catch (Exception ex)
        {
            System.out.println("\n[CalcSender] ObjToStr conversion error: " + ex.getMessage());
        }
        
	msg.setContent(strObj);
        
     	msg.addReceiver(new AID("cap", AID.ISLOCALNAME));
        msg.addReceiver(customerServiceAgentAID);
        send(msg);
        
        customerGui.appendLog("Sending Message to cap");
        customerGui.appendLog("Message content [Base64 string]: " + strObj);
        //customerRequest = request;
        
        
        if(strObj!=null){
            customerRequest=sendtoagent(customerRequest);
            customerGui.clearLog();
            customerGui.appendLog("Show back add product to cart data from gui");
            customerGui.appendLog("Product list size : "+ customerRequest.getOrder().getProductList().size());
            for(int i=0; i<customerRequest.getOrder().getProductList().size(); i++){
                customerGui.appendLog(customerRequest.getOrder().getProductList().get(i).getBaju().getName()+"\t"+customerRequest.getOrder().getProductList().get(i).getQuantity());
            }
            cart.setTable(customerRequest.getOrder());
        }
        showCloth.closeGui();
        customerGui.appendLog("\n");
    }
    
    public void ViewClothDescription(String BajuName){
        float price=(float) 20.00;
        ArrayList<Cloth> b = new ArrayList();
        ArrayList<String> size = new ArrayList();
        Cloth[] baju = Baju.toArray(new Cloth[0]);
        for(int i=0; i< baju.length; i++){
            if(baju[i].getName().equals(BajuName)){
                b.add(baju[i]);
            }
        }
        for(int i=0; i<b.size();i++){
            if(size.size()<=0){
                size.add(b.get(i).getSize());
            }
            else{
                boolean e=false;
                for(int y=0; y< size.size(); y++)
                {
                    if(size.get(y).equals(b.get(i).getSize())){
                        e=true;
                    }
                }
                if(!e)
                    size.add(b.get(i).getSize());
            }
        }
        
        if(b.size()>0){
            String type=b.get(0).getType();
            String name=b.get(0).getName();
            showCloth = new ClothDiscription(this);
            showCloth.setBajuData(type, name, price , size);
            showCloth.showGui();
        }
        customerGui.clearLog();
    }
    
    public void getCustomerServiceAgent() {
  	try {
            String serviceType = "Customer";
            customerGui.appendLog("Searching the DF/Yellow-Pages for " + serviceType + " service");
            
            // Build the description used as template for the search
            DFAgentDescription template = new DFAgentDescription();
            
            ServiceDescription templateSd = new ServiceDescription();
            templateSd.setType(serviceType);
            templateSd.addProperties(new Property("Action1", "Add Product to Carts"));
            templateSd.addProperties(new Property("Action2", "Remove Product from Carts"));
            templateSd.addProperties(new Property("Action3", "Confirm"));
            template.addServices(templateSd);
  		
            SearchConstraints sc = new SearchConstraints();
            // We want to receive 10 results at most
            sc.setMaxResults(new Long(10));
  		
            DFAgentDescription[] results = DFService.search(this, template, sc);
            customerGui.appendLog("\n"+Integer.toString(results.length));
            if (results.length > 0) {
  		customerGui.appendLog("Agent "+getLocalName()+" found the following " + serviceType + " services:");
  		for (int i = 0; i < results.length; ++i) {
                    DFAgentDescription dfd = results[i];
                    AID agentAID = dfd.getName();
                    customerGui.popup("Agent name: " + agentAID);
                    customerGui.appendLog("Agent name: " + agentAID);
                    customerGui.appendLog("\n"); 
  		}
                
                //just use the first one
                DFAgentDescription dfd = results[0];
                customerServiceAgentAID = dfd.getName();
                
                //enable calcGui.combobox and submit button
                customerRequest = new CustomerRequest();
                customerGui.enabledGUI();
                cart.showGui();
            }	
            else {
                customerGui.appendLog("Agent "+getLocalName()+" did not find any " + serviceType + " service");
                customerGui.popup("No " + serviceType + " agent service found!");
            }
  	}
  	catch (FIPAException fe) {
            fe.printStackTrace();
  	}
        customerGui.appendLog("\n");        
    }
    protected void setup() 
    {        
        customerGui = new CustomerGUI(this);
        cart = new Cart (this);
	customerGui.showGui();
        //cart.showGui();
        //for receiving calculation result	
	addBehaviour(new CyclicBehaviour(this) 
	{            
            public void action() 
            { 
                ACLMessage msg= receive();
                
		if (msg != null) {
                    customerGui.appendLog("\n");
                    customerGui.appendLog("Message received from " + msg.getSender());
                    
                    String msgContent = msg.getContent();
                    customerGui.appendLog("Message content [Base64 string]: " + msgContent);
                    customerGui.appendLog("Msg performative: " + ACLMessage.getPerformative(msg.getPerformative()));                   
                    
                    try
                    {
//                        CustomerRequest request = (CustomerRequest)deserializeObjectFromString(msgContent);
                        String result = (String)deserializeObjectFromString(msgContent);
                        customerGui.appendLog(result);
                        //customerGui.showResult(result);
                        
                        if(result.equals("")){
                            cart.closeGui();
                        }
//                        order order;
//                        order = request.getOrder();
//                        cart.setTable(order);                                                
                    }
                    catch(Exception ex)
                    {
                        customerGui.appendLog("StrToObj conversion error: " + ex.getMessage());
                    }
                }
                
                customerGui.appendLog("[CustomerAgent] CyclicBehaviour Block");
                block();
            }
        });
    }
}
