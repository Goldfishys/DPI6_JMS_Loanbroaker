package loanbroker;

import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.HashMap;

import javax.jms.*;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import mix.Gateway.MessageReciever;
import mix.Gateway.MessageSender;
import mix.model.bank.*;
import mix.model.loan.LoanReply;
import mix.model.loan.LoanRequest;


public class LoanBrokerFrame extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private DefaultListModel<JListLine> listModel = new DefaultListModel<>();
	private JList<JListLine> list;
	private HashMap<String, BankInterestRequest> BirID = new HashMap<>();
	private HashMap<String, String> BirLrLink = new HashMap<>();
	private HashMap<String, LoanRequest> LrID = new HashMap<>();
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					LoanBrokerFrame frame = new LoanBrokerFrame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}


	/**
	 * Create the frame.
	 */
	public LoanBrokerFrame() {
		setTitle("Loan Broker");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[]{46, 31, 86, 30, 89, 0};
		gbl_contentPane.rowHeights = new int[]{233, 23, 0};
		gbl_contentPane.columnWeights = new double[]{1.0, 0.0, 1.0, 0.0, 0.0, Double.MIN_VALUE};
		gbl_contentPane.rowWeights = new double[]{1.0, 0.0, Double.MIN_VALUE};
		contentPane.setLayout(gbl_contentPane);

		JScrollPane scrollPane = new JScrollPane();
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.gridwidth = 7;
		gbc_scrollPane.insets = new Insets(0, 0, 5, 5);
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 0;
		contentPane.add(scrollPane, gbc_scrollPane);

		//create listener
		list = new JList<>(listModel);
		scrollPane.setViewportView(list);


		setBankListener();
		setClientListener();
	}

	private void setClientListener() {
		MessageConsumer consumer = new MessageReciever().CreateReciever("tcp://localhost:61616", "LoanRequestDestination");

		//set consumer to listen
		try {
			consumer.setMessageListener(new MessageListener() {

				@Override
				public void onMessage(Message msg) {
					System.out.println(msg);
					try {
						//save msg in HashMap
						String id = msg.getJMSCorrelationID();
						LoanRequest LR = (LoanRequest) ((ObjectMessage) msg).getObject();
						LrID.put(id, LR);

						//add to list
						addToList(LR);

						//send BIR to bank
						SendBankInterestRequest(LR, id);
					} catch (JMSException e) {
						e.printStackTrace();
					}
				}
			});

		} catch (JMSException e) {
			e.printStackTrace();
		}
	}

	private void setBankListener() {
		//create listener
		MessageConsumer consumer = new MessageReciever().CreateReciever("tcp://localhost:61616", "BIReplyDestination");

		//set consumer to listen
		try {
			consumer.setMessageListener(new MessageListener() {

				@Override
				public void onMessage(Message msg) {
					System.out.println(msg);
					try {
						//get id and BIR object
						String id = msg.getJMSCorrelationID();
						BankInterestReply BIR = (BankInterestReply) ((ObjectMessage) msg).getObject();

						//get LR id
						String LoanRqID = BirLrLink.get(id);
						System.out.println("LoanRqID " + LoanRqID);

						//add BIR object to list
						add(LrID.get(LoanRqID),BIR);

						//Send Loan Reply to the client
						SendLoanReply(BIR, LoanRqID);
					} catch (JMSException e) {
						e.printStackTrace();
					}
				}
			});

		} catch (JMSException e) {
			e.printStackTrace();
		}
	}

	private JListLine getRequestReply(LoanRequest request){
	     
	     for (int i = 0; i < listModel.getSize(); i++){
	    	 JListLine rr =listModel.get(i);
	    	 if (rr.getLoanRequest() == request){
	    		 return rr;
	    	 }
	     }
	     
	     return null;
	   }
	
	public void addToList(LoanRequest loanRequest){
		listModel.addElement(new JListLine(loanRequest));		
	}

	public void addToList(LoanRequest loanRequest,BankInterestRequest bankRequest){
		JListLine rr = getRequestReply(loanRequest);
		if (rr!= null && bankRequest != null){
			rr.setBankRequest(bankRequest);
            list.repaint();
		}		
	}

	public void SendLoanReply(BankInterestReply BIR, String LRid){
		LoanReply LR = new LoanReply(BIR.getInterest(), BIR.getQuoteId());
		new MessageSender().SendObject("tcp://localhost:61616", "LoanReplyDestination", LR, LRid);
	}

	public void SendBankInterestRequest(LoanRequest LR, String LRid){
		//create BIR
		BankInterestRequest BIR = new BankInterestRequest(LR.getAmount(), LR.getTime());

		//send msg with BIR
		String id = new MessageSender().SendObject("tcp://localhost:61616","BIRequestDestination",BIR, "");

		//save BIR to HashMap and update list
		BirID.put(id, BIR);
		BirLrLink.put(id, LRid);
		addToList(LR,BIR);
	}

	public void add(LoanRequest loanRequest, BankInterestReply bankReply){
		JListLine rr = getRequestReply(loanRequest);
		if (rr!= null && bankReply != null){
			rr.setBankReply(bankReply);;
            list.repaint();
		}		
	}
}