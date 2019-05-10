/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package block_chain;

/**
 *
 * @author Uzair Ahmed
 */
public class TransactionInput {
    public String transactionOutputId; //Reference to TransactionOutputs -> transactionId
	public TransactionOutput UTO; //Contains the Unspent transaction output
	
	public TransactionInput(String transactionOutputId) {
		this.transactionOutputId = transactionOutputId;
	}
}
