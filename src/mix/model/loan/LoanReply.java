package mix.model.loan;

import java.io.Serializable;

/**
 *
 * This class stores all information about a bank offer
 * as a response to a client loan request.
 */
public class LoanReply implements Serializable {

        private double interest; // the interest that the bank offers
        private String bankID; // the unique quote identification

    public LoanReply() {
        super();
        this.interest = 0;
        this.bankID = "";
    }
    public LoanReply(double interest, String quoteID) {
        super();
        this.interest = interest;
        this.bankID = quoteID;
    }

    public LoanReply(String str){
        String[] args = str.split(";");
        interest = Double.valueOf(args[0]);
        bankID = args[1];
    }

    public double getInterest() {
        return interest;
    }

    public void setInterest(double interest) {
        this.interest = interest;
    }

    public String getQuoteID() {
        return bankID;
    }

    public void setQuoteID(String quoteID) {
        this.bankID = quoteID;
    }
    

    public String Serialize(){
        return interest + ";" + bankID;
    }

    @Override
    public String toString(){
        return " interest=" + interest + " bankID=" + bankID;
    }
}
