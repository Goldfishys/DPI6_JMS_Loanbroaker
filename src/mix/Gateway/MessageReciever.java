package mix.Gateway;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import java.util.ArrayList;
import java.util.Arrays;

public class MessageReciever {

    public MessageConsumer CreateReciever(String _connection, String _destination){
        MessageConsumer consumer = null; // for receiving messages

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
        return consumer;
    }
}
