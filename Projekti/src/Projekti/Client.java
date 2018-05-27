package Projekti;
import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JButton;
import java.net.*;
import java.io.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
public class Client extends JFrame {

	private JPanel contentPane;
	private JTextField txtMesazhi;
	
	//variabla globale	
	static Socket msgSocket;
	static Socket voiceSocket;
	static DataInputStream din;
	static DataOutputStream dout;
	static JTextArea msg_text;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Client frame = new Client();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
		
		try {
			msgSocket = new Socket("localhost",8888);
			Thread.sleep(3000);
			
			while(true)
			{

				voiceSocket = new Socket("localhost",8889);
									
				String msgin="";
				din = new DataInputStream(msgSocket.getInputStream());
				dout = new DataOutputStream(msgSocket.getOutputStream());
				
				try {
					msgin=din.readUTF();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				msg_text.setText(msg_text.getText().trim()+"\nServer:\t"+msgin);	
				

				if(voiceSocket.isConnected()) {
					Thread t1 = new Thread(new Runnable() {
						
						@Override
						public void run() {
							InputStream is = null;
							try {
								is = voiceSocket.getInputStream();
							} catch (IOException e1) {
								is = null;
							}
					        
					        byte[] aByte = new byte[1];
					        int bytesRead;

					        ByteArrayOutputStream baos = new ByteArrayOutputStream();

					        if (is != null) {

					            FileOutputStream fos = null;
					            BufferedOutputStream bos = null;
					            try {
					                fos = new FileOutputStream("E:/audio.wav");
					                bos = new BufferedOutputStream(fos);
					                bytesRead = is.read(aByte, 0, aByte.length);

					                do {
				                        baos.write(aByte);
				                        bytesRead = is.read(aByte);
					                } while (bytesRead != -1);

					                bos.write(baos.toByteArray());
					                bos.flush();
					                bos.close();
					                is.close();
					                voiceSocket.close();
					            } catch (IOException ex) {
					                // Do exception handling
					            }
					        }
					        
					        File yourFile;
					        AudioInputStream stream = null;
					        AudioFormat format;
					        DataLine.Info info;
					        Clip clip = null;
					        
					        yourFile = new File("E:\\audio.wav");
					        try {
								stream = AudioSystem.getAudioInputStream(yourFile);
							} catch (UnsupportedAudioFileException | IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
					        format = stream.getFormat();
					        info = new DataLine.Info(Clip.class, format);
					        try {
								clip = (Clip) AudioSystem.getLine(info);
						        clip.open(stream);
							} catch (LineUnavailableException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
					        clip.start();
					       
						}
					});
					t1.start();
					
				}
				
			}
			
			
			
			
		} 
		catch (Exception e) 
		{
			System.out.println("Gabim ne klient");
			e.printStackTrace();
		}	
		
	}

	/**
	 * Create the frame.
	 */
	public Client() {
		setTitle("Client");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		msg_text = new JTextArea();
		msg_text.setBounds(10, 11, 414, 191);
		contentPane.add(msg_text);
		
		txtMesazhi = new JTextField();
		txtMesazhi.setBounds(10, 213, 299, 37);
		contentPane.add(txtMesazhi);
		txtMesazhi.setColumns(10);
		
		JButton btnNewButton = new JButton("Send");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) 
			{
				String msgout="";
				msgout=txtMesazhi.getText().trim();
				try 
				{
					dout.writeUTF(msgout);
					txtMesazhi.setText("");
				} 
				catch (Exception e) 
				{
					e.printStackTrace();
				}
			}
		});
		btnNewButton.setBounds(319, 213, 105, 37);
		contentPane.add(btnNewButton);
		
	}
}

