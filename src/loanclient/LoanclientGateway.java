package loanclient;


import mix.JMS.MessageReceiver;
import mix.JMS.MessageSender;
import mix.model.loan.LoanReply;
import mix.model.loan.LoanRequest;

import javax.jms.*;
import java.util.HashMap;

public class LoanclientGateway {
    private MessageSender sender;
    private MessageReceiver reciever;
    private HashMap<String, LoanRequest> LrID = new HashMap<>();
    private LoanClientFrame loanClientFrame;

    public LoanclientGateway(LoanClientFrame loanClientFrame){
        this.loanClientFrame = loanClientFrame;
        sender = new MessageSender();
        reciever = new MessageReceiver("tcp://localhost:61616","LoanReplyDestination");
        MessageListener ml = message -> OnLoanReplyArrived((TextMessage) message);
        reciever.setListener(ml);
    }

    public void SendLoanRequestToBroker(LoanRequest request) {
        String msgid = sender.SendMessage("tcp://localhost:61616", "LoanRequestDestination", request.Serialize(), "");
        LrID.put(msgid, request);
    }

    public void OnLoanReplyArrived(TextMessage msg){
        System.out.println(msg);
        try {
            System.out.println("GOT A MESSAGE");
            String id = msg.getJMSCorrelationID();
            LoanRequest loanrequest = LrID.get(id);
            LoanReply loanreply = new LoanReply(msg.getText());

            loanClientFrame.UpdateRequestReply(loanrequest, loanreply);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
