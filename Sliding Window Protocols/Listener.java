import java.net.*;
import java.io.*;

public class Listener implements Runnable{
	Socket socket;
	int noPackets;
	public Listener(Socket socket, int noPackets){
		this.socket=socket;
		this.noPackets=noPackets;
	}
	
	public void run(){
		try{
			BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			int received;
			while(true){
				received=reader.read();
				GBNT.setAckNum(received);
				System.out.println("receive ack: "+received);
				if (received == noPackets) return;
			}
			
			
			
		}catch(Exception e){System.out.println(e);e.getStackTrace();}
	}
	
}