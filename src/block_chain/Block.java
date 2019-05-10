/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package block_chain;

import com.sun.xml.internal.ws.util.StringUtils;
import java.util.ArrayList;
import java.util.Date;

/**
 *
 * @author lenovo
 */
public class Block {
    
    public String Hash;
    public String PreHash;
    private String merkleRoot;
    private long TimeStamp;
    private int nonce;
    public ArrayList<Transaction> transactions = new ArrayList<Transaction>(); //our data will be a simple message.
	
    public Block(String PreHash){
        this.PreHash=PreHash;
        this.TimeStamp= new Date().getTime();
        this.Hash = CalculateHash();
    }
    public String CalculateHash()
    {
        String Calculatehash = Hashing.ApplySha256(PreHash+Long.toString(TimeStamp)+Integer.toString(nonce)+merkleRoot);
        return Calculatehash;
    }
    public void MineBlock(int difficulty)
    {
        merkleRoot = Hashing.getMerkleRoot(transactions);
		String target = Hashing.getDificultyString(difficulty); //Create a string with difficulty * "0" 
		while(!Hash.substring( 0, difficulty).equals(target)) {
			nonce ++;
			Hash = CalculateHash();
		}
		System.out.println("Block Mined!!! : " + Hash);
    }
    //Add transactions to this block
	public boolean addTransaction(Transaction transaction) {
		//process transaction and check if valid, unless block is genesis block then ignore.
		if(transaction == null) return false;		
		if((PreHash != "0")) {
			if((transaction.processTransaction() != true)) {
				System.out.println("Transaction failed to process. Discarded.");
				return false;
			}
		}
		transactions.add(transaction);
		System.out.println("Transaction Successfully added to Block");
		return true;
	}
}
