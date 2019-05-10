/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package block_chain;
import java.util.ArrayList;
import com.google.gson.GsonBuilder;
import java.io.File;
import java.io.IOException;
import java.security.Security;
import java.util.HashMap;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
/**
 *
 * @author lenovo
 */
public class Block_Chain {

    /**
     * @param args the command line arguments
     */
    public static ArrayList<Block> blockchain = new ArrayList<Block>();
    public static int difficulty = 5;
    public static Wallet WalletA;
    public static Wallet WalletB;
    public static HashMap<String,TransactionOutput> UTOs = new HashMap<String,TransactionOutput>(); //list of all unspent transactions. 
    public static float minimumTransaction = 0.1f;
    public static Transaction genesisTransaction;
    
    
    public static void main(String[] args) throws TransformerException, TransformerConfigurationException, ParserConfigurationException, SAXException, IOException, IOException {
        
        /*Block genesisblock = new Block("Hi I am genesis block", "0");
        System.out.println("Hash for block 1: "+ genesisblock.Hash);
        
        Block secondblock = new Block("Hi I am second block", "0");
        System.out.println("Hash for block 2: "+ secondblock.Hash);
        
        Block thirdblock = new Block("Hi I am third block", "0");
        System.out.println("Hash for block 3: "+ genesisblock.Hash);
        
        blockchain.add(new Block("Hi I am genesis block", "0"));
        System.out.println("Trying to mine block 1... ");
        blockchain.get(0).MineBlock(difficulty);
        
        blockchain.add(new Block("Hi I am second block", blockchain.get(blockchain.size()-1).Hash));
        System.out.println("Trying to mine block 2... ");
        blockchain.get(1).MineBlock(difficulty);
        
        blockchain.add(new Block("Hi I am third block", blockchain.get(blockchain.size()-1).Hash));
        System.out.println("Trying to mine block 3... ");
        blockchain.get(2).MineBlock(difficulty);
        
        System.out.println("\nBlockchain is Valid: " + IsChainValid());*/
        
        
        String blockchainJson = new GsonBuilder().setPrettyPrinting().create().toJson(blockchain);
        System.out.println("\nThe block Chain: ");
        System.out.println(blockchainJson);
        
        //Setup Bouncey castle as a Security Provider
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider()); 
		//Create the new wallets
		WalletA = new Wallet();
		WalletB = new Wallet();
                WalletA.name = "Uzair";
                WalletB.name = "Ahmed";
                //XmlWriteWallet(WalletA.name, WalletA.privatekey.toString(), WalletA.publickey.toString(), WalletA.UTOs.toString());
                XmlWriteWallet();//XmlWriteWallet(WalletB.name, WalletB.privatekey.toString(), WalletB.publickey.toString(), WalletB.UTOs.toString());
		Wallet coinbase = new Wallet();
                //XmlWriteWallet(coinbase.name, coinbase.privatekey.toString(), coinbase.publickey.toString(), coinbase.UTOs.toString());
		
		//create genesis transaction, which sends 100 NoobCoin to walletA: 
		genesisTransaction = new Transaction(coinbase.publickey, WalletA.publickey, 100f, null);
                genesisTransaction.generateSignature(coinbase.privatekey);	 //manually sign the genesis transaction	
		genesisTransaction.transactionId = "0"; //manually set the transaction id
		genesisTransaction.outputs.add(new TransactionOutput(genesisTransaction.reciepient, genesisTransaction.value, genesisTransaction.transactionId)); //manually add the Transactions Output
		UTOs.put(genesisTransaction.outputs.get(0).id, genesisTransaction.outputs.get(0)); //its important to store our first transaction in the UTXOs list.
		//XmlWriteTransaction(genesisTransaction, UTOs);
		
		System.out.println("Creating and Mining Genesis block... ");
		Block genesis = new Block("0");
		genesis.addTransaction(genesisTransaction);
		addBlock(genesis);
		
		//testing
		Block block1 = new Block(genesis.Hash);
		System.out.println("\nWalletA's balance is: " + WalletA.getBalance());
		System.out.println("\nWalletA is Attempting to send funds (40) to WalletB...");
		block1.addTransaction(WalletA.sendFunds(WalletB.publickey, 40f));
                //XmlWriteBlock(block1);
		addBlock(block1);
                //XmlWriteMineBlock(block1);
		System.out.println("\nWalletA's balance is: " + WalletA.getBalance());
		System.out.println("WalletB's balance is: " + WalletB.getBalance());
		
		Block block2 = new Block(block1.Hash);
		System.out.println("\nWalletA Attempting to send more funds (1000) than it has...");
		block2.addTransaction(WalletA.sendFunds(WalletB.publickey, 1000f));
                //XmlWriteBlock(block2);
		addBlock(block2);
                //XmlWriteMineBlock(block1);
		System.out.println("\nWalletA's balance is: " + WalletA.getBalance());
		System.out.println("WalletB's balance is: " + WalletB.getBalance());
		
		Block block3 = new Block(block2.Hash);
		System.out.println("\nWalletB is Attempting to send funds (20) to WalletA...");
		block3.addTransaction(WalletB.sendFunds( WalletA.publickey, 20));
                XmlWriteBlock(block3);
		System.out.println("\nWalletA's balance is: " + WalletA.getBalance());
		System.out.println("WalletB's balance is: " + WalletB.getBalance());
		
		IsChainValid();
                
                
		/*//Test public and private keys
		System.out.println("Private and public keys:");
		System.out.println(Hashing.getStringFromKey(WalletA.privatekey));
		System.out.println(Hashing.getStringFromKey(WalletA.publickey));
		//Create a test transaction from WalletA to walletB 
		Transaction transaction = new Transaction(WalletA.publickey, WalletB.publickey, 5, null);
		transaction.generateSignature(WalletA.privatekey);
		//Verify the signature works and verify it from the public key
		System.out.println("Is signature verified");
		System.out.println(transaction.verifiySignature());*/
    }
    public static Boolean IsChainValid()
    {
        Block currentblock;
        Block previousblock;
        String hashTarget = new String(new char[difficulty]).replace('\0', '0');
        HashMap<String,TransactionOutput> tempUTXOs = new HashMap<>(); //a temporary working list of unspent transactions at a given block state.
        tempUTXOs.put(genesisTransaction.outputs.get(0).id, genesisTransaction.outputs.get(0));
		
        //Loop through the blocks to check hashes
        for(int i = 1; i<blockchain.size(); i++)
        {
            currentblock = blockchain.get(i);
            previousblock = blockchain.get(i-1);
            //compare registered hashes and calculate hashes
            if(!currentblock.Hash.equals(currentblock.CalculateHash()))
            {
                System.out.println("Current hashes not equal");
                return false;
            }
            //compare previous hashes and registered hashes
            if(!previousblock.Hash.equals(currentblock.PreHash))
            {
                System.out.println("Previous hashes not equal");
                return false;
            }
            //Check if hash is solved
            if(!currentblock.Hash.substring(0, difficulty).equals(hashTarget))
            {
                System.out.println("The block hasn't been mined.");
                return false;
            }
     
    
    //loop thru blockchains transactions:
            TransactionOutput tempOutput;
            for(int t=0; t <currentblock.transactions.size(); t++) {
            Transaction currentTransaction = currentblock.transactions.get(t);
				
            if(!currentTransaction.verifiySignature()) {
            System.out.println("#Signature on Transaction(" + t + ") is Invalid");
            return false; 
            }
            if(currentTransaction.getInputsValue() != currentTransaction.getOutputsValue()) {
            System.out.println("#Inputs are note equal to outputs on Transaction(" + t + ")");
            return false; 
            }
				
            for(TransactionInput input: currentTransaction.inputs) {	
            tempOutput = tempUTXOs.get(input.transactionOutputId);
					
            if(tempOutput == null) {
            System.out.println("#Referenced input on Transaction(" + t + ") is Missing");
            return false;
            }
					
            if(input.UTO.value != tempOutput.value) {
            System.out.println("#Referenced input Transaction(" + t + ") value is Invalid");
            return false;
            }
					
            tempUTXOs.remove(input.transactionOutputId);
            }
				
            for(TransactionOutput output: currentTransaction.outputs) {
            tempUTXOs.put(output.id, output);
            }
				
            if( currentTransaction.outputs.get(0).reciepient != currentTransaction.reciepient) {
            System.out.println("#Transaction(" + t + ") output reciepient is not who it should be");
            return false;
            }
            if( currentTransaction.outputs.get(1).reciepient != currentTransaction.sender) {
            System.out.println("#Transaction(" + t + ") output 'change' is not sender.");
            return false;
            }
				
	}
			
    }
            System.out.println("Blockchain is valid");
            return true;
	}
	
	public static void addBlock(Block newBlock) {
		newBlock.MineBlock(difficulty);
		blockchain.add(newBlock);
	}
        
        public static void XmlWriteWallet() throws TransformerConfigurationException, TransformerException, ParserConfigurationException{
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document d = db.newDocument();
        
        Element element = d.createElement("User");
        d.appendChild(element);
        
//        Attr attr = d.createAttribute("Id");
//        attr.setValue("1");
//        element.setAttributeNode(attr);
        Element name = d.createElement("Name");
        name.appendChild(d.createTextNode(WalletB.privatekey.toString()));
        element.appendChild(name);
        
        Element prikey = d.createElement("PrivateKey");
        prikey.appendChild(d.createTextNode(WalletB.publickey.toString()));
        element.appendChild(prikey);
        
        Element pubkey = d.createElement("PublicKey");
        pubkey.appendChild(d.createTextNode(WalletB.name));
        element.appendChild(pubkey);
        
        Element uto = d.createElement("UTOs");
        uto.appendChild(d.createTextNode(WalletB.UTOs.toString()));
        element.appendChild(uto);
        
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer t = tf.newTransformer();
        DOMSource source = new DOMSource(d);
        
        StreamResult sr = new StreamResult(new File("./Wallet.xml"));
        t.transform(source, sr);
    }
        
        public static void XmlWriteTransaction(Transaction transaction, HashMap<String,TransactionOutput> Utos) throws TransformerConfigurationException, TransformerException, ParserConfigurationException{
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document d = db.newDocument();
        
        Element element = d.createElement("Transaction");
        d.appendChild(element);
        
//        Attr attr = d.createAttribute("Id");
//        attr.setValue("1");
//        element.setAttributeNode(attr);
        Element Tranid = d.createElement("TranId");
        Tranid.appendChild(d.createTextNode(transaction.transactionId));
        element.appendChild(Tranid);
        
        Element Frompk = d.createElement("FromPK");
        Frompk.appendChild(d.createTextNode(transaction.sender.toString()));
        element.appendChild(Frompk);
        
        Element Topk = d.createElement("ToPK");
        Topk.appendChild(d.createTextNode(transaction.reciepient.toString()));
        element.appendChild(Topk);
        
        Element Value = d.createElement("Value");
        Value.appendChild(d.createTextNode(Float.toString(transaction.value)));
        element.appendChild(Value);
        
        Element Inputs = d.createElement("Inputs");
        Inputs.appendChild(d.createTextNode(transaction.inputs.toString()));
        element.appendChild(Inputs);
        
        String s = new String(transaction.signature);
        Element Sig = d.createElement("Signature");
        Sig.appendChild(d.createTextNode(s));
        element.appendChild(Sig);
        
        Element Outputs = d.createElement("Outputs");
        Outputs.appendChild(d.createTextNode(transaction.outputs.toString()));
        element.appendChild(Outputs);
        
        Element Utoxs = d.createElement("Utos");
        Utoxs.appendChild(d.createTextNode(Utos.toString()));
        element.appendChild(Utoxs);
        
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer t = tf.newTransformer();
        DOMSource source = new DOMSource(d);
        
        StreamResult sr = new StreamResult(new File("./Tansactions.xml"));
        t.transform(source, sr);
        
    }
        
        public static void XmlWriteBlock(Block block) throws TransformerConfigurationException, TransformerException, ParserConfigurationException{
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document d = db.newDocument();
        
        Element element = d.createElement("Block");
        d.appendChild(element);
        
//        Attr attr = d.createAttribute("Id");
//        attr.setValue("1");
//        element.setAttributeNode(attr);
        Element hash = d.createElement("Hash");
        hash.appendChild(d.createTextNode(block.Hash));
        element.appendChild(hash);
        
        Element Prehash = d.createElement("PreHash");
        Prehash.appendChild(d.createTextNode(block.PreHash));
        element.appendChild(Prehash);
        
        Element Trans = d.createElement("Transactions");
        Trans.appendChild(d.createTextNode(block.transactions.toString()));
        element.appendChild(Trans);
        
        
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer t = tf.newTransformer();
        DOMSource source = new DOMSource(d);
        
        StreamResult sr = new StreamResult(new File("./Blocks.xml"));
        t.transform(source, sr);
        
    }
         public static void XmlWriteBlockEnd(Document doc) throws TransformerConfigurationException, TransformerException, ParserConfigurationException{
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document d = db.newDocument();
        
        DOMSource ds = new DOMSource(doc);
        StreamResult res = new StreamResult(new File("./Blocks.xml"));
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer t = tf.newTransformer();
        t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
        t.setOutputProperty(OutputKeys.INDENT, "yes");
        t.transform(ds, res);
    }
        
        
        public static void XmlWriteMineBlock(Block block) throws TransformerConfigurationException, TransformerException, ParserConfigurationException, SAXException, IOException, IOException{
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document d = db.newDocument();
        
        File xmlfile = new File("./Blocks.xml");   
        DocumentBuilderFactory dbfr = DocumentBuilderFactory.newInstance();
        DocumentBuilder dbr = dbfr.newDocumentBuilder();
        Document dr = dbr.parse(xmlfile);
        
        Node nd = Readtodelete(block, dr);
        dr.removeChild(nd);
        XmlWriteBlockEnd(dr);
        
        Element element = d.createElement("MinedBlock");
        d.appendChild(element);
        
//        Attr attr = d.createAttribute("Id");
//        attr.setValue("1");
//        element.setAttributeNode(attr);
        Element hash = d.createElement("Hash");
        hash.appendChild(d.createTextNode(block.Hash));
        element.appendChild(hash);
        
        Element Prehash = d.createElement("PreHash");
        Prehash.appendChild(d.createTextNode(block.PreHash));
        element.appendChild(Prehash);
        
        Element Trans = d.createElement("Transactions");
        Trans.appendChild(d.createTextNode(block.transactions.toString()));
        element.appendChild(Trans);
        
        
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer t = tf.newTransformer();
        DOMSource source = new DOMSource(d);
        
        StreamResult sr = new StreamResult(new File("./MinedBlocks.xml"));
        t.transform(source, sr);
        
    }
    
        public static Node Readtodelete(Block block, Document doc){
        //delete from block and inset in mined blocks
        NodeList lst = doc.getElementsByTagName("Block");
        for(int i=0; i<lst.getLength();i++)
        {
        String content = lst.item(i).getTextContent();
        if(content.equalsIgnoreCase(block.Hash)&&content.equalsIgnoreCase(block.PreHash)&&content.equalsIgnoreCase(block.transactions.toString()));
        {
        Node p = lst.item(i).getParentNode();
        return p;
        }
        }
        return null;
        }
}
