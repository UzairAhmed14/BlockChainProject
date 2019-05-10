/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package block_chain;
import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
/**
 *
 * @author Uzair Ahmed
 */
public class Wallet {
    public String name;
    public PrivateKey privatekey;
    public PublicKey publickey;
    public HashMap<String,TransactionOutput> UTOs = new HashMap<String,TransactionOutput>(); //only UTXOs owned by this wallet.
    
    public Wallet(){
        generatekeypair();
    }
    
    public void generatekeypair(){
        try{
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("ECDSA","BC");
           SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
			ECGenParameterSpec ecSpec = new ECGenParameterSpec("prime192v1");
			// Initialize the key generator and generate a KeyPair
			keyGen.initialize(ecSpec, random);   //256 bytes provides an acceptable security level
	        	KeyPair keyPair = keyGen.generateKeyPair();
	        	// Set the public and private keys from the keyPair
                       privatekey = keyPair.getPrivate();
	        	publickey = keyPair.getPublic();
		}catch(Exception e) {
			throw new RuntimeException(e); 
        }
    }
    //returns balance and stores the UTXO's owned by this wallet in this.UTXOs
	public float getBalance() {
		float total = 0;	
        for (Map.Entry<String, TransactionOutput> item: Block_Chain.UTOs.entrySet()){
        	TransactionOutput UTXO = item.getValue();
            if(UTXO.isMine(publickey)) { //if output belongs to me ( if coins belong to me )
            	UTOs.put(UTXO.id,UTXO); //add it to our list of unspent transactions.
            	total += UTXO.value ; 
            }
        }  
		return total;
	}
	//Generates and returns a new transaction from this wallet.
	public Transaction sendFunds(PublicKey _recipient,float value ) {
		if(getBalance() < value) { //gather balance and check funds.
			System.out.println("#Not Enough funds to send transaction. Transaction Discarded.");
			return null;
		}
    //create array list of inputs
		ArrayList<TransactionInput> inputs = new ArrayList<TransactionInput>();
    
		float total = 0;
		for (Map.Entry<String, TransactionOutput> item: UTOs.entrySet()){
			TransactionOutput UTXO = item.getValue();
			total += UTXO.value;
			inputs.add(new TransactionInput(UTXO.id));
			if(total > value) break;
		}
		
		Transaction newTransaction = new Transaction(publickey, _recipient , value, inputs);
		newTransaction.generateSignature(privatekey);
		
		for(TransactionInput input: inputs){
			UTOs.remove(input.transactionOutputId);
		}
		return newTransaction;
	}
	
}
