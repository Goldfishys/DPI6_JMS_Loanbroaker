package loanbroker;

import mix.JMS.MessageReceiver;
import mix.JMS.MessageSender;
import mix.model.bank.BankInterestReply;
import mix.model.bank.BankInterestRequest;
import mix.model.loan.LoanReply;
import mix.model.loan.LoanRequest;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import java.util.HashMap;

public class BrokerClientGateway {
    private MessageSender sender;
    private MessageReceiver reciever;
    private LoanBrokerFrame LoanBrokerFrame;

    public BrokerClientGateway(LoanBrokerFrame LoanBrokerFrame) {
        this.LoanBrokerFrame = LoanBrokerFrame;
        sender = new MessageSender();
        reciever = new MessageReceiver("tcp://localhost:61616", "LoanRequestDestination");
        MessageListener ml = message -> OnLoanRequestArrived(message);
        reciever.setListener(ml);
    }

    public void OnLoanRequestArrived(Message msg) {
        System.out.println(msg);
        try {
            //save msg in HashMap
            String id = msg.getJMSCorrelationID();
            LoanRequest LR = (LoanRequest) ((ObjectMessage) msg).getObject();

            LoanBrokerFrame.setNewLoanRequest(LR, id);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    public void SendLoanReplyToClient(String id, BankInterestReply BIR){
        LoanReply loanReply = new LoanReply(BIR.getInterest(), BIR.getQuoteId());
        sender.SendObject("tcp://localhost:61616", "LoanReplyDestination", loanReply, id);
    }
}
