package mix.JMS;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import java.util.ArrayList;
import java.util.Arrays;

public class MessageReceiver {
    private MessageConsumer consumer;

    public MessageReceiver(String _connection, String _destination){
        try {
            ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(_connection);
            factory.setTrustedPackages(new ArrayList(Arrays.asList("mix.model.bank,mix.model.loan".split(","))));
            Connection connection = factory.createConnection();

            //create session
            Session session = connection.createSession(false,Session.AUTO_ACKNOWLEDGE);

            //create destination
            Destination receiveDestination = session.createQueue(_destination);
            consumer = session.createConsumer(receiveDestination);

            connection.start(); // this is needed to start receiving messages
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    public void setListener(MessageListener ml){
        try {
            consumer.setMessageListener(ml);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
