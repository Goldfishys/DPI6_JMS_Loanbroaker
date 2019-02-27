package bank;

import mix.JMS.MessageReceiver;
import mix.JMS.MessageSender;
import mix.model.bank.BankInterestReply;
import mix.model.bank.BankInterestRequest;

import javax.jms.*;
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
        MessageListener ml = message -> onBankInterestRequestArrived((TextMessage) message);
        reciever.setListener(ml);
    }

    public void onBankInterestRequestArrived(TextMessage msg){
        try {
            //add BIR to HashMap
            String id = msg.getJMSCorrelationID();
            BankInterestRequest BIR = new BankInterestRequest(msg.getText());
            BirID.put(BIR,id);

            //add BIR to List
            JMSBankFrame.add(BIR);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    public void SendBankInterestReply(BankInterestRequest bankInterestRequest, BankInterestReply bankInterestReply){
        String id = BirID.get(bankInterestRequest);
        sender.SendMessage("tcp://localhost:61616","BrokerBankInterestReplyDestination",bankInterestReply.Serialize(), id);
    }
}
