package loanbroker;

import mix.JMS.MessageReceiver;
import mix.JMS.MessageSender;
import mix.model.bank.BankInterestRequest;
import mix.model.loan.LoanReply;
import mix.model.loan.LoanRequest;

import javax.jms.Message;
import javax.jms.MessageListener;
import java.util.HashMap;

public class BrokerClientGateway {
    private MessageSender sender;
    private MessageReceiver reciever;
    private HashMap<String, BankInterestRequest> BirID = new HashMap<>();
    private HashMap<String, String> BirLrLink = new HashMap<>();
    private HashMap<String, LoanRequest> LrID = new HashMap<>();
    private LoanBrokerFrame LoanBrokerFrame;

    public BrokerClientGateway(LoanBrokerFrame LoanBrokerFrame){
        this.LoanBrokerFrame = LoanBrokerFrame;
        sender = new MessageSender();
        reciever = new MessageReceiver("tcp://localhost:61616","LoanRequestDestination");
        MessageListener ml = message -> OnLoanReplyArrived(message);
        reciever.setListener(ml);
    }

    public void OnLoanReplyArrived(Message msg){

    }

    public void SendLoanReplyToClient(LoanReply lr){
        String msgid = sender.SendObject();
    }
}
