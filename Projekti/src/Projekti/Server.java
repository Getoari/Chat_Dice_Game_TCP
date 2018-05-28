package Projekti;
import java.net.*;
import java.io.*;
import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JLabel;

public class Server extends JFrame {

	private JPanel contentPane;
	static private JTextField txtMsg;
	static private JButton btnRecord;
	static ServerSocket msgServerSocket;
	static ServerSocket voiceServerSocket;
	static Socket msgSocket;
	static Socket voiceSocket;
	static DataInputStream dis;
	static DataOutputStream dos;
	static JTextArea msg_text;
	static TargetDataLine targetDataLine;
	/**
	 * Launch the application.
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws InterruptedException {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Server frame = new Server();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		

		try {
			msgServerSocket = new ServerSocket(8888);
			voiceServerSocket = new ServerSocket(8889);
			voiceSocket = voiceServerSocket.accept();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		
		while(true) {
			
			msgSocket = null;
			
			try
	        {
	            // socket object to receive incoming client requests
				msgSocket = msgServerSocket.accept();
	            
				msg_text.append("A new client is connected: " + msgSocket + "\n");
	             
	            // obtaining input and out streams
	            dis = new DataInputStream(msgSocket.getInputStream());
	            dos = new DataOutputStream(msgSocket.getOutputStream());
	             
	            System.out.println("Assigning new thread for this client");

	            // create a new thread object
	            Thread t = new Thread(new Runnable() {
					
					@Override
					public void run() {
						try {
							String msgin = "";
							while(!msgin.equals("exit")) {
								msgin=dis.readUTF();
								msg_text.append("Klient:\t"+ msgin+"\n");																		
							}
						
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});

	            // Invoking the start() method
	            t.start();
	             
	        }
	        catch (Exception e){
	            e.printStackTrace();
	        }

		}
	}

	/**
	 * Create the frame.
	 */
	public Server() {
		setTitle("Server");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 301);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		msg_text = new JTextArea();
		msg_text.setBounds(10, 11, 403, 197);
		contentPane.add(msg_text);

		btnRecord = new JButton("R");
		btnRecord.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent arg0) {
				// Stop Recording
				System.out.println("Rec Stopped!");
				targetDataLine.stop();
				targetDataLine.close();
				
				//Send recording				
				Thread t1 = new Thread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						try {
							
				            BufferedOutputStream outToClient = null;
				            outToClient = new BufferedOutputStream(voiceSocket.getOutputStream());
				            
				            if (outToClient != null) {
				                File myFile = new File("audio.wav");
				                byte[] mybytearray = new byte[(int) myFile.length()];

				                FileInputStream fis = null;

				                try {
				                    fis = new FileInputStream(myFile);
				                } catch (FileNotFoundException ex) {
				                    // Do exception handling
				                }
				                BufferedInputStream bis = new BufferedInputStream(fis);

				                try {
				                    bis.read(mybytearray, 0, mybytearray.length);
				                    outToClient.write(mybytearray, 0, mybytearray.length);
				                    outToClient.flush();
				                    outToClient.close();
				                    
				                    System.out.println("Voice Sent!");
				                } catch (IOException ex) {
				                    // Do exception handling
				                }
				            }
							
						} catch (Exception e) {
							// TODO: handle exception
							e.printStackTrace();
						}
						
						try {
							voiceSocket = voiceServerSocket.accept();
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
				});
				
				t1.start();
				
			    
			}
			@Override
			public void mousePressed(MouseEvent e) {
				AudioFormat format = new AudioFormat(16000, 8, 2, true, true);
				DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
				
				if(!AudioSystem.isLineSupported(info)) {
					System.out.println("Line not supported");
				}
				
				try {
					targetDataLine = (TargetDataLine) AudioSystem.getLine(info);
					
					//start the rec
					targetDataLine.open();
					
					System.out.println("Starting rec!");
					
					targetDataLine.start();
					
					Thread stopper = new Thread(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							AudioInputStream ais = new AudioInputStream(targetDataLine);
							File wavFile = new File("audio.wav");
							try {
								AudioSystem.write(ais, AudioFileFormat.Type.WAVE, wavFile);
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					});
					
					stopper.start();					
				} catch (LineUnavailableException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		btnRecord.setBounds(279, 219, 45, 32);
		contentPane.add(btnRecord);
		
		txtMsg = new JTextField();
		txtMsg.setBounds(10, 219, 271, 33);
		contentPane.add(txtMsg);
		txtMsg.setColumns(10);
		
		JButton btnSend = new JButton("Send");
		btnSend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) 
			{
				try {
					String msgout="";
					msgout=txtMsg.getText().trim();
					dos.writeUTF(msgout);
					//rreshti me poshte eshte per avancim.
					//msg_text.setText(msg_text.getText().trim()+"\n You :\t"+ msgout);
					txtMsg.setText("");
					
				} catch (Exception e2) {
					// TODO: handle exception
				}

			}
		});
		btnSend.setBounds(334, 219, 89, 32);
		contentPane.add(btnSend);
	}
}