package loanbroker;

import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.HashMap;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import mix.model.bank.*;
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
	public BrokerClientGateway brokerClientGateway;
	public BrokerBankGateway bankGateway;

	
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

		brokerClientGateway = new BrokerClientGateway(this);
		bankGateway = new BrokerBankGateway(this);
	}

	public void setNewLoanRequest(LoanRequest LR, String id){
		//add new loan request to gui list of loan requests
		addToList(LR);

		//send BankInterestRequest to bank
		bankGateway.SendBankInterestRequest(LR, id);
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

	public void NewLoanReply(String id,LoanRequest lr, BankInterestReply BIR){
		//add BIR object to list
		add(lr,BIR);

		//Send Loan Reply to the client
		brokerClientGateway.SendLoanReplyToClient(id, BIR);
	}

	public void add(LoanRequest loanRequest, BankInterestReply bankReply){
		JListLine rr = getRequestReply(loanRequest);
		if (rr!= null && bankReply != null){
			rr.setBankReply(bankReply);;
            list.repaint();
		}		
	}
}
