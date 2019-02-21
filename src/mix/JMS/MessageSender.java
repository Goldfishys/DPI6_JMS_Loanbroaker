package mix.JMS;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class MessageSender {

    public String SendObject(String _connection, String _destination, Object transmitObject, String msgid){
        if(msgid == ""){
            msgid = createRandomString();
        }

        try {
            //"tcp://localhost:61616"
            ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(_connection);
            factory.setTrustedPackages(new ArrayList(Arrays.asList("mix.model.bank,mix.model.loan".split(","))));
            Connection connection = factory.createConnection();
            connection.start();

            //create session
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            //create destination
            //Destination
            Destination sendDestination = session.createQueue(_destination);

            //create a msg producer
            MessageProducer producer = session.createProducer(sendDestination);
            producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

            // create a message
            Message msg = session.createObjectMessage((Serializable) transmitObject);
            msg.setJMSCorrelationID(msgid);
            // send the message
            producer.send(msg);
            System.out.println("Message is on its way!");

            // Clean up
            session.close();
            connection.close();
        } catch (JMSException e) {
            e.printStackTrace();
        }
        return msgid;
    }

    private String createRandomString() {
        Random random = new Random(System.currentTimeMillis());
        long randomLong = random.nextLong();
        return Long.toHexString(randomLong);
    }
}
