package Projekti;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler extends Thread {
	private Server server = null;
	private Socket socket = null;
    private int id = -1;
    private DataInputStream dis = null;
    private DataOutputStream dos = null;

    public ClientHandler(Server server, Socket socket) {
    	this.server = server;
        this.socket = socket;
        this.id = socket.getPort();
    }
    
    public int getID() {  
    	return id;
    }
    
    public void open() throws IOException { 

    	dis = new DataInputStream(socket.getInputStream());
		dos = new DataOutputStream(socket.getOutputStream());
    }
    
    public void send(String msg) {   
    	try {
    		dos.writeUTF(msg);
    		dos.flush();
        } catch(IOException ioe) {  
        	System.out.println(id + " ERROR sending: " + ioe.getMessage());
        	server.remove(id);
        	interrupt();
        }
    }

    public void run() {
    	System.out.println("Server Thread " + id + " running.");
        while (true) {  
        	try {
        		if(!socket.isClosed())
        			server.handle(id, dis.readUTF());
            } catch(IOException ioe) {  
            	System.out.println(id + " ERROR reading: " + ioe.getMessage());
            	server.remove(id);
           }
        }
    }
    
    public void close() throws IOException {  
    	if (socket != null)    
    		socket.close();
        if (dos != null)  
        	dos.close();
        if (dis != null)
        	dis.close();
    }
}

