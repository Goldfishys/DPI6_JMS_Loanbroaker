package mix.model.bank;

import java.io.Serializable;

/**
 *
 * This class stores all information about an request from a bank to offer
 * a loan to a specific client.
 */
public class BankInterestRequest implements Serializable {

    private int amount; // the requested loan amount
    private int time; // the requested loan period

    public BankInterestRequest() {
        super();
        this.amount = 0;
        this.time = 0;
    }

    public BankInterestRequest(int amount, int time) {
        super();
        this.amount = amount;
        this.time = time;
    }

    public BankInterestRequest(String str){
        String[] args = str.split(";");
        amount = Integer.valueOf(args[0]);
        time = Integer.valueOf(args[1]);
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }


    public String Serialize() {
        return amount + ";" + time;
    }

    @Override
    public String toString(){
        return " amount=" + amount + " time=" + time;
    }
}
