package Projekti;

import java.awt.Color;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.mysql.jdbc.StringUtils;


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
import java.sql.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.Random;
import java.io.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JScrollPane;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import java.awt.Font;

public class Client extends JFrame {

	/**
	 * Client v1.1
	 */
	private static final long serialVersionUID = 1L;
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
	Connection conn=null;
	PreparedStatement pst=null;
	ResultSet rs=null;
	private JButton btn_Roll_Dice1;
	static int dice1;
	static int dice2;
	static String vlera;
	static int vlera_pritur;
	private JTextField txt_vlera_pritur;
	private JScrollPane scrollPane;
	final static String serverAddress = "localhost";
	
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
			msgSocket = new Socket(serverAddress,8888);
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
							msg_text.append(msgin);
							msg_text.setCaretPosition(msg_text.getDocument().getLength());    
						} catch (IOException e) {
							e.printStackTrace();
							System.exit(0);
						}
					}
				}
			});
			
			t1.start();
			
			// Voice Receiver
			while(true) {
				try {
					voiceReceivingSocket = new Socket(serverAddress,8889);
					
					Thread voiceHandler = new Thread(new Runnable() {
						
						@Override
						public void run() {
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
									fos = new FileOutputStream("audio/client/audio.wav");
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
								
								directory = new File("audio/client");
								
								if (!directory.exists())
									directory.mkdirs();
								
								audioFile = new File("audio/client/audio.wav");
								
								
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
								msg_text.append("Server: Voice Message.\n");
								
								
							}
						}
					});
					
					voiceHandler.start();
				} catch (IOException e2) {
					e2.printStackTrace();
				}
					
				try {
					voiceReceivingSocket = new Socket(serverAddress,8889);
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
		                fos = new FileOutputStream("audio/client/audio.wav");
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
		            
		            File audioFile;
		            File directory;
			        AudioInputStream stream = null;
			        AudioFormat format;
			        DataLine.Info info;
			        Clip clip = null;
			        
			        audioFile = new File("audio/client/audio.wav");
			        
			        directory = new File("audio/client");
			        			        
			        if (!directory.exists())
			        	directory.mkdirs();
			        
			        audioFile = new File("audio/client/audio.wav");
			        
			        try {
						stream = AudioSystem.getAudioInputStream(audioFile);
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
		conn=MySqlConnector.connectFiekDb(serverAddress);
		setResizable(false);
		setTitle("Client");
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e3) {
			// TODO Auto-generated catch block
			e3.printStackTrace();
		}
		setTitle("Client: " + msgSocket.getLocalPort());
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 858, 333);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 11, 414, 206);
		contentPane.add(scrollPane);
		
		msg_text = new JTextArea();
		msg_text.setCaretPosition(msg_text.getDocument().getLength());
		msg_text.setEditable(false);
		msg_text.setBounds(10, 11, 414, 206);
		scrollPane.setViewportView(msg_text);
		
		txtMsg = new JTextField();
		txtMsg.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_ENTER) {
					btnSend.doClick();
				}
			}
		});
		txtMsg.setBounds(10, 228, 256, 54);
		contentPane.add(txtMsg);
		
		btnRecord = new JButton(new ImageIcon(((new ImageIcon(Server.class.getResource("/images/mic.png"))
				.getImage()
	            .getScaledInstance(24, 24, java.awt.Image.SCALE_SMOOTH)))));
		btnRecord.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		btnRecord.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent arg0) {
				mouseDown = false;
								
				// Stop Recording
				System.out.println("Rec Stopped!");
				targetDataLine.stop();
				targetDataLine.close();
				
				try {
					voiceSendingSocket = new Socket(serverAddress,8890);
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
				                File myFile = new File("audio/client/audio.wav");
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
				                    
				                    dos.writeUTF("Voice Message!\n");
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
								SwingUtilities.invokeLater(new Runnable() {
									
									@Override
									public void run() {
										btnRecord.setForeground(Color.RED);
									}
								});
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
							File wavFile = new File("audio/client/audio.wav");
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

				try {

					dos.writeUTF(txtMsg.getText().trim() + "\n");
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
		
		
		
		JPanel panel = new JPanel();
		panel.setBounds(449, 109, 168, 173);
		contentPane.add(panel);
		panel.setLayout(null);
		JLabel lblRoll1 = new JLabel("");
		lblRoll1.setIcon(new ImageIcon(Client.class.getResource("/images/dice-rolling-1.png")));
		lblRoll1.setBounds(0, 13, 168, 137);
		panel.add(lblRoll1);
		
		JLabel lblRez1 = new JLabel("");
		lblRez1.setHorizontalAlignment(SwingConstants.LEFT);
		lblRez1.setForeground(Color.RED);
		lblRez1.setBounds(0, 141, 168, 33);
		panel.add(lblRez1);
		
		JPanel panel_1 = new JPanel();
		panel_1.setLayout(null);
		panel_1.setBounds(654, 108, 168, 173);
		contentPane.add(panel_1);
		
		JLabel lblRoll2 = new JLabel("");
		lblRoll2.setIcon(new ImageIcon(Client.class.getResource("/images/dice-rolling-1.png")));
		lblRoll2.setBounds(0, 13, 168, 137);
		panel_1.add(lblRoll2);
		
		JLabel lblRez2 = new JLabel("");
		lblRez2.setHorizontalAlignment(SwingConstants.LEFT);
		lblRez2.setForeground(Color.RED);
		lblRez2.setBounds(0, 141, 168, 32);
		panel_1.add(lblRez2);
		
		
		btn_Roll_Dice1 = new JButton("Roll The Dice");
		btn_Roll_Dice1.setBounds(438, 71, 384, 25);
		contentPane.add(btn_Roll_Dice1);
		
		txt_vlera_pritur = 
				new JTextField();
		
		
		txt_vlera_pritur.setBounds(545, 11, 275, 33);
		contentPane.add(txt_vlera_pritur);
		txt_vlera_pritur.setColumns(10);
		
		JLabel lbl_vlera_pritur = new JLabel("Vlera e pritur :");
		lbl_vlera_pritur.setFont(new Font("Tahoma", Font.PLAIN, 16));
		lbl_vlera_pritur.setBounds(436, 17, 111, 16);
		contentPane.add(lbl_vlera_pritur);
		
		JLabel lblValidate = new JLabel("");
		lblValidate.setHorizontalAlignment(SwingConstants.CENTER);
		lblValidate.setForeground(Color.RED);
		lblValidate.setFont(new Font("Tahoma", Font.PLAIN, 16));
		lblValidate.setBounds(436, 46, 386, 25);
		contentPane.add(lblValidate);
		
		
		btn_Roll_Dice1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(!txt_vlera_pritur.getText().toString().equals("")) { 
					if(StringUtils.isStrictlyNumeric(txt_vlera_pritur.getText().toString())) {
						if(Integer.valueOf(txt_vlera_pritur.getText()) >= 1 && Integer.valueOf(txt_vlera_pritur.getText()) <= 12 ) {
							
							vlera=txt_vlera_pritur.getText();
							vlera_pritur=Integer.parseInt(vlera);
							
							btn_Roll_Dice1.setEnabled(false);
							
							Thread shuffle = new Thread(new Runnable() {
								
								@Override
								public void run() {
									for(int i = 1; i < 12; i++) {
										SwingUtilities.invokeLater(new Runnable() {
											@Override
											public void run() {
												dice1 = roll(lblRoll1);
												dice2 = roll(lblRoll2);
											}
										});
										try {
											Thread.sleep(300);
										} catch (InterruptedException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
									}
									
									try {
										Thread.sleep(700);
									} catch (InterruptedException e1) {
										// TODO Auto-generated catch block
										e1.printStackTrace();
									}
									
									if(vlera_pritur==(dice1+dice2)){
										
										try 
										{
											String sql="insert into dice (dice1,dice2,diceSum,vlera_pritur,won,price) values (?,?,?,?,?,?)";
											pst=conn.prepareStatement(sql);
											
											pst.setInt (1, dice1);
											pst.setInt (2, dice2) ;
											pst.setInt(3, dice1+dice2);
											pst.setInt(4, vlera_pritur);
											pst.setBoolean(5, true);
											pst.setString(6, "1000");
											pst.execute();
											pst.close();
										} 
										catch (SQLException e5) 
										{
											JOptionPane.showMessageDialog(null, "Error:"+e5.getMessage());
										} 
										JOptionPane.showMessageDialog(null, "Urime keni fituar 1000euro\n"+"Shuma = "+(dice1+dice2)+"\nVlera e pritur = "+vlera_pritur);
										lblRoll1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/dice-rolling-1.png")));
										lblRoll2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/dice-rolling-1.png")));
										txt_vlera_pritur.setText("");
									} else {
										try {
											String sql="insert into dice (dice1,dice2,diceSum,vlera_pritur,won,price) values (?,?,?,?,?,?)";
											pst=conn.prepareStatement(sql);
											
											pst.setInt (1, dice1);
											pst.setInt (2, dice2) ;
											pst.setInt(3, dice1+dice2);
											pst.setInt(4, vlera_pritur);
											pst.setBoolean(5, false);
											pst.setString(6, "1000");
											pst.execute();
											pst.close();
											
										} 
										catch (SQLException e5) 
										{
											JOptionPane.showMessageDialog(null, "Error:"+e5.getMessage());
										} 
										JOptionPane.showMessageDialog(null,"Na vjen keq, vlera e pritur nuk perputhet me shumen e rene.\nShuma = "+(dice1+dice2)+ 
												" \n Vlera e pritur = "+vlera_pritur + " \n"+
												"Provoni perseri.");

										lblRoll1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/dice-rolling-1.png")));
										lblRoll2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/dice-rolling-1.png")));
										txt_vlera_pritur.setText("");										
									}
									btn_Roll_Dice1.setEnabled(true);
								}
							});
							
							shuffle.start();
							
						} else {
							lblValidate.setText("Vlera e pritur duhet te jete ne mes numrave 1 dhe 12");
						}
							

					
					}
					
				else {
				lblValidate.setText("Vlera e pritur nuk mund te jete tekst!!!");
				txt_vlera_pritur.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseEntered(MouseEvent e) {
						lblValidate.setText("");
					}
				});
					
		
				}
					
				}
				else {
					lblValidate.setText("Vlera e pritur nuk mund te jete e zbrazet!!!!");
					txt_vlera_pritur.addMouseListener(new MouseAdapter() {
						@Override
						public void mouseEntered(MouseEvent e) {
							lblValidate.setText("");
						}
					});
					
				}
			}
	
	
			int roll(JLabel lbl) {
				Random rd=new Random();
				int random = 0;
				random=rd.nextInt(6)+1;
				switch(random){
				case 1:
					lbl.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/dice-rolling-1.png")));
					break;
				case 2:
					lbl.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/dice-rolling-2.png")));
					break;
				case 3:
					lbl.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/dice-rolling-3.png")));
					break;
				case 4:
					lbl.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/dice-rolling-4.png")));
					break;		
				case 5:
					lbl.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/dice-rolling-5.png")));
					break;
				case 6:
					lbl.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/dice-rolling-6.png")));
					break;
				}
				return random;
			}
			
		});
	}
}