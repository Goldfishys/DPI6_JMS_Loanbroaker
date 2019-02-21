package loanbroker;

import mix.JMS.MessageReceiver;
import mix.JMS.MessageSender;
import mix.model.bank.BankInterestReply;
import mix.model.bank.BankInterestRequest;
import mix.model.loan.LoanRequest;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import java.util.HashMap;

public class BrokerBankGateway {
    private MessageSender sender;
    private MessageReceiver reciever;
    private LoanBrokerFrame LoanBrokerFrame;
    public HashMap<String, LoanRequest> LrID = new HashMap<>();

    public BrokerBankGateway(LoanBrokerFrame LoanBrokerFrame) {
        this.LoanBrokerFrame = LoanBrokerFrame;
        sender = new MessageSender();
        reciever = new MessageReceiver("tcp://localhost:61616", "BrokerBankInterestReplyDestination");
        MessageListener ml = message -> OnBankInterestReplyArrived(message);
        reciever.setListener(ml);
    }

    public void SendBankInterestRequest(LoanRequest lr, String id) {
        LrID.put(id,lr);

        //create BIR
        BankInterestRequest BIR = new BankInterestRequest(lr.getAmount(), lr.getTime());

        //send msg with BIR
        sender.SendObject("tcp://localhost:61616","ABNBankRequestDestination",BIR, id);
    }

    public void OnBankInterestReplyArrived(Message msg){
        System.out.println(msg);
        try {
            //get id and BIR object
            String id = msg.getJMSCorrelationID();
            BankInterestReply BIR = (BankInterestReply) ((ObjectMessage) msg).getObject();

            //get LR id
            LoanRequest loanRequest = LrID.get(id);
            LoanBrokerFrame.NewLoanReply(id, loanRequest, BIR);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
