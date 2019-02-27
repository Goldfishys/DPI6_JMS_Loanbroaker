package mix.model.loan;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * This class stores all information about a
 * request that a client submits to get a loan.
 *
 */
public class LoanRequest implements Serializable {

    private int ssn; // unique client number.
    private int amount; // the ammount to borrow
    private int time; // the time-span of the loan

    public LoanRequest() {
        super();
        this.ssn = 0;
        this.amount = 0;
        this.time = 0;
    }

    public LoanRequest(int ssn, int amount, int time) {
        super();
        this.ssn = ssn;
        this.amount = amount;
        this.time = time;
    }

    public LoanRequest(String str){
        String[] args = str.split(";");
        this.ssn = Integer.valueOf(args[0]);
        this.amount = Integer.valueOf(args[1]);
        this.time = Integer.valueOf(args[2]);
    }

    public int getSsn() {
        return ssn;
    }

    public void setSsn(int ssn) {
        this.ssn = ssn;
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
        return ssn + ";" + amount + ";" + time;
    }

    @Override
    public String toString(){
        return "ssn=" + ssn + " amount=" + amount + " time=" + time;
    }
}
