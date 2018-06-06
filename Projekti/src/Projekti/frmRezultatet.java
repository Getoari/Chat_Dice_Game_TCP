package Projekti;

import java.awt.EventQueue;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.swing.JFrame;
import javax.swing.JTable;

import net.proteanit.sql.DbUtils;

import javax.swing.JScrollPane;
import javax.swing.JLabel;
import java.awt.Font;

public class frmRezultatet extends JFrame {

	private JFrame frame;
	public JTable tblRezultatet;
	Connection conn=null;
	PreparedStatement pst=null;
	ResultSet rs=null;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					frmRezultatet window = new frmRezultatet();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public frmRezultatet() {
		conn=MySqlConnector.connectFiekDb("localhost");
		
		getContentPane().setLayout(null);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(31, 76, 706, 317);
		getContentPane().add(scrollPane);
		
		tblRezultatet = new JTable();
		scrollPane.setViewportView(tblRezultatet);
		
		JLabel lblRezultatet = new JLabel("Rezultatet");
		lblRezultatet.setFont(new Font("Tahoma", Font.PLAIN, 17));
		lblRezultatet.setBounds(335, 32, 134, 31);
		getContentPane().add(lblRezultatet);
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		try {
			String sql="select id as 'ID',dice1 as 'Dice1',dice2 as 'Dice2', diceSum as 'Shuma', vlera_pritur as 'Vlera_pritur',won as 'Fituar',price as' Shperblimi' from dice";
			pst=conn.prepareStatement(sql);
			rs=pst.executeQuery();
			tblRezultatet.setModel(DbUtils.resultSetToTableModel(rs));
			pst.close();
			}
			catch (Exception e5){
				e5.printStackTrace();
			}
		
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}
