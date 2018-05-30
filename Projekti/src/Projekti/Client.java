package Projekti;

import java.awt.Color;
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
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import java.net.*;
import java.io.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class Client extends JFrame {

	private JPanel contentPane;
	private JTextField txtMsg;
	
	//variabla globale	
	static Socket msgSocket;
	static Socket voiceReceivingSocket;
	static Socket voiceSendingSocket;
	static DataInputStream dis;
	static DataOutputStream dos;
	static JTextArea msg_text;
	static JButton btnRecord;
	static JButton btnSend;
	private boolean mouseDown;
	static TargetDataLine targetDataLine;
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
			
			// Message Receiver Thread
			Thread t1 = new Thread(new Runnable() {
				
				@Override
				public void run() {
					while (true) {
						String msgin="";
						try {
							dis = new DataInputStream(msgSocket.getInputStream());
							dos = new DataOutputStream(msgSocket.getOutputStream());
					
							msgin=dis.readUTF();
							msg_text.append("Server:\t"+msgin+"\n");
						} catch (IOException e) {
							
						}
					}
				}
			});
			
			t1.start();
			
			// Voice Receiver
			while(true) {
					
				try {
					voiceReceivingSocket = new Socket("localhost",8889);
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				InputStream is = null;
				try {
					is = voiceReceivingSocket.getInputStream();
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
		                fos = new FileOutputStream("audio/client/remote/audio.wav");
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
		            } catch (IOException ex) {
		                // Do exception handling
		            }
		            
		            File yourFile;
			        AudioInputStream stream = null;
			        AudioFormat format;
			        DataLine.Info info;
			        Clip clip = null;
			        
			        yourFile = new File("audio/client/remote/audio.wav");
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
			        msg_text.append("Server: Voice Message! \n");
			        
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
		setResizable(false);
		setTitle("Client");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 468, 327);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		msg_text = new JTextArea();
		msg_text.setEditable(false);
		msg_text.setBounds(10, 11, 414, 206);
		contentPane.add(msg_text);
		
		txtMsg = new JTextField();
		txtMsg.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_ENTER) {
					btnSend.doClick();
				}
			}
		});
		txtMsg.setBounds(10, 228, 257, 54);
		contentPane.add(txtMsg);
		
		btnRecord = new JButton(new ImageIcon(((new ImageIcon(Server.class.getResource("/images/mic.png"))
				.getImage()
	            .getScaledInstance(24, 24, java.awt.Image.SCALE_SMOOTH)))));
		btnRecord.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent arg0) {
				mouseDown = false;
								
				// Stop Recording
				System.out.println("Rec Stopped!");
				targetDataLine.stop();
				targetDataLine.close();
				
				try {
					voiceSendingSocket = new Socket("localhost",8890);
				} catch (IOException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}
				
				//Send recording				
				Thread t1 = new Thread(new Runnable() {
					
					@Override
					public void run() {
						try {
							
				            BufferedOutputStream outToClient = null;
				            outToClient = new BufferedOutputStream(voiceSendingSocket.getOutputStream());
				            
				            if (outToClient != null) {
				                File myFile = new File("audio/client/local/audio.wav");
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
					}
				});
				
				t1.start();	
			}
			
			@Override
			public void mousePressed(MouseEvent e) {
				mouseDown = true;
				
				Thread timer = new Thread(new Runnable() {
					
					@Override
					public void run() {
						int i = 0;
						do {

							btnRecord.setForeground(Color.BLACK);
							
							try {
								Thread.sleep(1000);
								i++;
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							
							if (i < 10)
								btnRecord.setText("00:0"+ i);
							else if (i < 21) {
								btnRecord.setText("00:"+ i);
							} else {
								btnRecord.setForeground(Color.RED);
								targetDataLine.stop();
							}
							
		            		btnRecord.setBounds(265, 228, 97, 53);
		            		
		                } while (mouseDown);
						
						btnRecord.setText("");
						btnRecord.setBounds(265, 228, 50, 53);
					}
				});
				
				timer.start();
				
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
							File wavFile = new File("audio/client/local/audio.wav");
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
		btnRecord.setBounds(265, 228, 50, 53);
		contentPane.add(btnRecord);
		
		btnSend = new JButton("Send");
		btnSend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) 
			{
				String msgout="";
				msgout=txtMsg.getText().trim();
				try 
				{
					dos.writeUTF(msgout);
					msg_text.append("You:\t" + msgout + "\n");
					txtMsg.setText("");
				} 
				catch (Exception e) 
				{
					e.printStackTrace();
				}
			}
		});
		btnSend.setBounds(329, 228, 95, 53);
		contentPane.add(btnSend);
		
	}
}