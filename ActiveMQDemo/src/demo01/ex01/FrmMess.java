package demo01.ex01;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Date;
import java.util.Properties;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import org.apache.log4j.BasicConfigurator;

import data.Person;
import helper.XMLConvert;

public class FrmMess extends JFrame {

	private JPanel contentPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				
				try {
					FrmMess frame = new FrmMess();
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
	public FrmMess() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 566, 486);
		setLocationRelativeTo(null);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(null);

		JPanel noiDung = new JPanel();
		noiDung.setBorder(new TitledBorder(
				new EtchedBorder(EtchedBorder.LOWERED, new Color(255, 255, 255), new Color(160, 160, 160)),
				"N\u1ED9i dung xem", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		noiDung.setBounds(10, 11, 530, 347);
		contentPane.add(noiDung);
		noiDung.setLayout(null);

		JTextArea chatBox = new JTextArea();
		chatBox.setEditable(false);
		chatBox.setBounds(10, 15, 510, 327);
		noiDung.add(chatBox);

		JButton btnSend = new JButton("Send");
		btnSend.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
			}
		});
		btnSend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					//FrmMess.this.dispose();
					// config environment for JMS
					BasicConfigurator.configure();
					// config environment for JNDI
					Properties settings = new Properties();
					settings.setProperty(Context.INITIAL_CONTEXT_FACTORY,
							"org.apache.activemq.jndi.ActiveMQInitialContextFactory");
					settings.setProperty(Context.PROVIDER_URL, "tcp://localhost:61616");
					// create context
					Context ctx = new InitialContext(settings);
					// lookup JMS connection factory
					ConnectionFactory factory = (ConnectionFactory) ctx.lookup("ConnectionFactory");
					// lookup destination. (If not exist-->ActiveMQ create once)
					Destination destination = (Destination) ctx.lookup("dynamicQueues/thanthidet");
					// get connection using credential
					Connection con = factory.createConnection("admin", "admin");
					// connect to MOM
					con.start();
					// create session
					Session session = con.createSession(/* transaction */false, /* ACK */Session.AUTO_ACKNOWLEDGE);
					// create producer
					MessageProducer producer = session.createProducer(destination);
					// create text message
					Message msg = session.createTextMessage("hello mesage from ActiveMQ");
					producer.send(msg);
					Person p = new Person(1001, "Thân Thị Đẹt", new Date());
					String xml = new XMLConvert<Person>(p).object2XML(p);
					msg = session.createTextMessage(xml);
					producer.send(msg);
					
					// shutdown connection
					session.close();
					con.close();
					System.out.println("Finished...");				
				} catch (Exception e2) {
					e2.printStackTrace();
				}
				
			}
		});
		btnSend.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnSend.setBounds(465, 369, 75, 67);
		contentPane.add(btnSend);

		JTextArea txtEnterTxt = new JTextArea();
		txtEnterTxt.setBounds(77, 369, 373, 67);
		contentPane.add(txtEnterTxt);

		JLabel lblEnterText = new JLabel("Enter text");
		lblEnterText.setHorizontalAlignment(SwingConstants.CENTER);
		lblEnterText.setBounds(10, 396, 57, 14);
		contentPane.add(lblEnterText);
	}
}
