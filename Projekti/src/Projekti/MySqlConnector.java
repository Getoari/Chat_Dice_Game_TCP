package Projekti;

import java.sql.Connection;
import java.sql.DriverManager;

import javax.swing.JOptionPane;

public class MySqlConnector {
    
 Connection conn = null;


	public static Connection connectFiekDb()
	{
		try
		{
			//perdoret per marrjen e Driverit per lidhje
			Class.forName("com.mysql.jdbc.Driver");
			Connection conn=DriverManager.getConnection("jdbc:mysql://localhost:3306/dbSisteme?&autoReconnect=true&useSSL=false","root","toor");
			return conn;
		}
		catch (Exception se)
		{
			JOptionPane.showMessageDialog(null, "Errori :" + se.getMessage());
		
		return null; }
		
		
		
	}

	}
