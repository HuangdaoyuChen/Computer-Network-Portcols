import java.net.*;
import java.io.*;

public class ListenerW implements Runnable{
	Socket socket;
	int noPackets;
	public ListenerW(Socket socket, int noPackets){
		this.socket=socket;
		this.noPackets=noPackets;
	}
	
	public void run(){
		try{
			BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			int received;
			while(true){
				received=reader.read();
				GBNW.setAckNum(received);
				System.out.println("received ack no."+received);
				if (received == noPackets) return;
				
			}
			
			
			
		}catch(Exception e){System.out.println(e);e.getStackTrace();}
	}
	
}