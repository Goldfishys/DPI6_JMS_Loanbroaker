package bank;

import mix.JMS.MessageReceiver;
import mix.JMS.MessageSender;
import mix.model.bank.BankInterestReply;
import mix.model.bank.BankInterestRequest;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import java.util.HashMap;

public class BankGateway {
    private MessageSender sender;
    private MessageReceiver reciever;
    private HashMap<BankInterestRequest, String> BirID = new HashMap<>();
    private JMSBankFrame JMSBankFrame;

    public BankGateway(JMSBankFrame JMSBankFrame){
        this.JMSBankFrame = JMSBankFrame;
        sender = new MessageSender();
        reciever = new MessageReceiver("tcp://localhost:61616", "ABNBankRequestDestination");
        MessageListener ml = message -> onBankInterestRequestArrived(message);
        reciever.setListener(ml);
    }

    public void onBankInterestRequestArrived(Message msg){
        try {
            //add BIR to HashMap
            String id = msg.getJMSCorrelationID();
            BankInterestRequest BIR = (BankInterestRequest) ((ObjectMessage) msg).getObject();
            BirID.put(BIR,id);

            //add BIR to List
            JMSBankFrame.add(BIR);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    public void SendBankInterestReply(BankInterestRequest bankInterestRequest, BankInterestReply bankInterestReply){
        String id = BirID.get(bankInterestRequest);
        sender.SendObject("tcp://localhost:61616","BrokerBankInterestReplyDestination",bankInterestReply, id);
    }
}
