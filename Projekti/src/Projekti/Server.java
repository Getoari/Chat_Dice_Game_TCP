package Projekti;

import java.net.*;
import java.io.*;
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
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Font;
import javax.swing.ImageIcon;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.SwingConstants;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;

public class Server extends JFrame implements Runnable {

	/**
	 * Server v1.0
	 */
	private static final long serialVersionUID = -8546228107459408123L;
	private JPanel contentPane;
	static private JTextField txtMsg;
	static private JButton btnRecord;
	static ServerSocket msgServerSocket;
	static ServerSocket voiceServerSS;
	static ServerSocket voiceServerRS;
	static Socket msgSocket;
	static Socket voiceReceivingSocket;
	static Socket voiceSendingSocket;
	static DataInputStream dis;
	static DataOutputStream dos;
	static JTextArea msg_text;
	static JTextArea onlineUsers;
	static TargetDataLine targetDataLine;
	private JButton btnConnect;
	private JButton btnDisconnect;
	private JButton btnSend;
	private JLabel lblOnlineUsers;
	static private JLabel lblServerAddress;
	volatile static boolean serverOnline = false;
	private boolean mouseDown;
	private static JLabel imgServerStatus;
	static int clientCount = 0;
	ClientHandler[] clients = new ClientHandler[50];
	static Server server = null;
	private JScrollPane scrollPane;
	static Thread serverStarter;
	
	/**
	 * Launch the application.
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws Exception {
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
	
		server = new Server(8888);	
			
		while(true) {
			
			//Voice Receiver 
			if(serverOnline) {
				try {
					voiceReceivingSocket = voiceServerRS.accept();
				} catch (IOException e2) {
					e2.printStackTrace();
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
		                fos = new FileOutputStream("audio/server/audio.wav");
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
		            	ex.printStackTrace();
		            }
		            
		            File directory;
		            File audioFile;
			        AudioInputStream stream = null;
			        AudioFormat format;
			        DataLine.Info info;
			        Clip clip = null;
			        
			        directory = new File("audio/server");
			        
			        if (!directory.exists())
			        	directory.mkdir();
			        
			        audioFile = new File("audio/server/audio.wav");
			        
			        
			        try {
						stream = AudioSystem.getAudioInputStream(audioFile);
					} catch (UnsupportedAudioFileException | IOException e) {
						e.printStackTrace();
					}
			        format = stream.getFormat();
			        info = new DataLine.Info(Clip.class, format);
			        try {
						clip = (Clip) AudioSystem.getLine(info);
				        clip.open(stream);
					} catch (LineUnavailableException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
			        clip.start();
				}	
			}
		}
	}

	/**
	 * Create the frame.
	 */
	public Server() {
		setResizable(false);
		setTitle("Server");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 647, 407);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		scrollPane = new JScrollPane();
		scrollPane.setBounds(12, 65, 403, 227);
		contentPane.add(scrollPane);
		
		msg_text = new JTextArea();
		msg_text.setCaretPosition(msg_text.getDocument().getLength());
		scrollPane.setViewportView(msg_text);
		msg_text.setEditable(false);

		btnRecord = new JButton(new ImageIcon(((new ImageIcon(Server.class.getResource("/images/mic.png"))
				.getImage()
	            .getScaledInstance(24, 24, java.awt.Image.SCALE_SMOOTH)))));
		btnRecord.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
			}
		});
		btnRecord.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent arg0) {
				// Stop Recording
				mouseDown = false;
				System.out.println("Rec Stopped!");
				targetDataLine.stop();
				targetDataLine.close();
				
				try {
					voiceSendingSocket = voiceServerSS.accept();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				//Send recording				
				Thread t1 = new Thread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						try {
							
				            BufferedOutputStream outToClient = null;
				            outToClient = new BufferedOutputStream(voiceSendingSocket.getOutputStream());
				            
				            if (outToClient != null) {
				                File myFile = new File("audio/server/audio.wav");
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
				                    
				                    msg_text.append("Server: Voice Message!" + "\n");
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
							
		            		btnRecord.setBounds(272, 305, 97, 53);
		            		
		                } while (mouseDown);
						
						btnRecord.setText("");
						btnRecord.setBounds(272, 305, 50, 53);
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
							File wavFile = new File("audio/server/audio.wav");
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
		btnRecord.setBounds(272, 305, 50, 53);
		contentPane.add(btnRecord);
		
		txtMsg = new JTextField();
		txtMsg.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_ENTER) {
					btnSend.doClick();
				}
			}
		});
		txtMsg.setBounds(12, 305, 261, 54);
		contentPane.add(txtMsg);
		
		btnSend = new JButton("Send");
		btnSend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) 
			{
				try {
					server.handle(-1, txtMsg.getText().trim());
					txtMsg.setText("");
				} catch (Exception e2) {
					e2.printStackTrace();
				}

			}
		});
		btnSend.setBounds(334, 305, 81, 53);
		contentPane.add(btnSend);
		
		btnConnect = new JButton("Connect");
		btnConnect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				connect();
			}
		});
		btnConnect.setBounds(196, 27, 97, 25);
		contentPane.add(btnConnect);
		
		btnDisconnect = new JButton("Disconnect");
		btnDisconnect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				disconnect();				
			}
		});
		btnDisconnect.setBounds(301, 27, 114, 25);
		contentPane.add(btnDisconnect);
		
		lblOnlineUsers = new JLabel("Online Users");
		lblOnlineUsers.setHorizontalAlignment(SwingConstants.CENTER);
		lblOnlineUsers.setFont(new Font("Tahoma", Font.PLAIN, 17));
		lblOnlineUsers.setBounds(425, 30, 180, 25);
		contentPane.add(lblOnlineUsers);
		
		onlineUsers = new JTextArea();
		onlineUsers.setEditable(false);
		onlineUsers.setBounds(427, 65, 178, 227);
		contentPane.add(onlineUsers);
		
		imgServerStatus = new JLabel();
		imgServerStatus.setIcon(new ImageIcon(Server.class.getResource("/images/offline.png")));
		imgServerStatus.setBounds(12, 29, 25, 25);
		contentPane.add(imgServerStatus);
		
		lblServerAddress = new JLabel("Disconnected");
		lblServerAddress.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblServerAddress.setBounds(47, 30, 115, 22);
		contentPane.add(lblServerAddress);
	}
	
	public Server(int port) {  
		try {  
			System.out.println("Binding to port " + port + ", please wait  ...");
			msgServerSocket = new ServerSocket(port);  
			System.out.println("Server started: " + msgServerSocket);
			
		} catch(IOException ioe) {  
			System.out.println("Can not bind to port " + port + ": " + ioe.getMessage()); 
		}
	}
	
    public void stop() {  
    	try {  
    		if (dis != null)  dis.close();
		    if (dos != null)  dos.close();
		    if (msgSocket != null)  msgSocket.close();
        } catch(IOException ioe) {  
    	   System.out.println("Error closing ...");
        }
    }

	public static void connect(){
		try {
			if(!serverOnline) {				
				serverStarter = new Thread(new Runnable() {
					
					@Override
					public void run() {
						server.run();
					}
				});
				
				serverStarter.start();
				
				lblServerAddress.setText("Connected");
				
				voiceServerSS = new ServerSocket(8889);
				voiceServerRS = new ServerSocket(8890);
				
				imgServerStatus.setIcon(new ImageIcon(Server.class.getResource("/images/online.png")));
				serverOnline = true;
			} else {
				JOptionPane.showMessageDialog(null, "Server is currently active!");
			}
			
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
	}
	public static void disconnect() {
		try {
			server.stop();
			serverStarter.interrupt();
			
			voiceServerSS.close();
			voiceServerRS.close();
			
			server.dissconnectClients();
			
			lblServerAddress.setText("Dissconected");
			
			serverOnline = false;
			
			imgServerStatus.setIcon(new ImageIcon(Server.class.getResource("/images/offline.png")));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
	}
	
	private int findClient(int id) {
		for (int i = 0; i < clientCount; i++)
			if (clients[i].getID() == id)
				return i;
		return -1;
	}
		
	public synchronized void handle(int id, String input) {
		if (input.equals(".exit")) {  
			clients[findClient(id)].send(".exit");
	        remove(id); 
		} else if( id == -1) {
			msg_text.append("Server: " + input + "\n");
            for (int i = 0; i < clientCount; i++) {
	            clients[i].send("Server: " + input + "\n");
            }
		} else {
            msg_text.append(id + ": " + input);
            for (int i = 0; i < clientCount; i++) {
	            clients[i].send(id + ": " + input);
	        }
	    }
	         
	}
	
	public synchronized void updateOnlineUsers() {
       	onlineUsers.setText("");
        if(clientCount != 0) {
			for (int i = 0; i < clientCount; i++) {
	            onlineUsers.append(clients[i].getID()+"\n");
	        }
        }
    }
	
	private void addThread(Socket socket)   {  
		if (clientCount < clients.length) {  
			System.out.println("Client accepted: " + socket);
	        clients[clientCount] = new ClientHandler(this, socket);
	        
	        try {  
	        	clients[clientCount].open(); 
	            clients[clientCount].start();
	            onlineUsers.append(clients[clientCount].getID()+"\n");
	          
	            clientCount++; 
	         } catch(IOException ioe) {  
	        	 System.out.println("Error opening thread: " + ioe); 
	         } 
	   } else {
	         System.out.println("Client refused: maximum " + clients.length + " reached.");
	   }
	}
	
	public synchronized void remove(int ID) {  
		int pos = findClient(ID);
	    if (pos >= 0) {
	    	ClientHandler toTerminate = clients[pos];
	        System.out.println("Removing client thread " + ID + " at " + pos);
	        if (pos < clientCount-1)
	        	for (int i = pos+1; i < clientCount; i++)
	        		clients[i-1] = clients[i];
	        clientCount--;
	        updateOnlineUsers();
	        try {  
	        	toTerminate.close();
	        	toTerminate.interrupt();
	        } catch(IOException ioe) {  
	        	System.out.println("Error closing thread: " + ioe); 
	        }
	    }
	}
	
	public synchronized void dissconnectClients() throws IOException {
		for (int i = 0; i < clientCount; i++) {
    		clients[i].close();
    		clients[i].interrupt();
		}
			
	} 

	@Override
	public void run() {
		while (true) {
			if (serverOnline)
				try { 
					System.out.println("Waiting for a client ..."); 
					addThread(msgServerSocket.accept()); 
				} catch(IOException ioe) {  
		        	System.out.println("Server accept error: " + ioe);
		        	stop(); 
		        }
	    }		
	}
}
