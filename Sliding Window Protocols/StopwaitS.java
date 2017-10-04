import java.net.*;
import java.io.*;

public class StopwaitS{
	public static void main(String[] args)throws UnknownHostException, IOException{
		try{
			String CRLF = "\r\n";
			
			//creat the socket
			ServerSocket Myserver = new ServerSocket(9527);
			System.out.println("Waiting......");
			
			Socket socket = Myserver.accept();
			System.out.println("Connection established!");
			
			//define reader and wirter
			DataOutputStream writer = new DataOutputStream (socket.getOutputStream());
			BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			
			//welcome message;
			//writer.writeBytes("Connection Established."+CRLF);
			
			int noPackets = reader.read();
			
			System.out.println("number of packets: "+noPackets);
			
			int lastAck = 0;
			int received;
			while (lastAck<noPackets){
				received=reader.read();
				if (received!=lastAck+1){
					System.out.println("receive: "+received);
					received = reader.read();
					//loop
				}
				System.out.println("received packet "+(lastAck+1));
				writer.write(lastAck+1);
				lastAck++;
				
			}
			
			socket.close();
			Myserver.close();
			
		}catch(Exception e){System.out.println(e);e.getStackTrace();}
		
	}
}