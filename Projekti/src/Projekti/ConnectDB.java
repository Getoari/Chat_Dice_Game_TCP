package Projekti;

import java.sql.Connection;
import java.sql.DriverManager;

import javax.swing.JOptionPane;

public class ConnectDB {
	Connection conn = null;
	
	public static Connection connectDiceDb() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/Dice","root","toor");
			return conn;
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Error: "+e.getMessage());
			return null;
		}
	}
	
	
}

