import java.net.*;
import java.io.*;
import java.util.Scanner;

public class StopwaitC{
	public static void main(String[] args)throws UnknownHostException, IOException{
		try{
			String CRLF = "\r\n";
			//create a client socket
			Socket socket = new Socket("localhost",9876);
			//define reader and wirter
			BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			DataOutputStream writer = new DataOutputStream (socket.getOutputStream());
			//DataInputStream reader = new DataInputStream (socket.getInputStream());
			//read the welcome message
			//System.out.println(reader.readLine());
			
			//define a scanner
			Scanner scr = new Scanner(System.in);
			
			//get the number of packets
			int noPackets=scr.nextInt();
			//send the number of packets
			writer.write(noPackets);
			writer.write(0);
			int received;
			//define sent
			int sent = 1;
			
			while (sent<=noPackets){
				writer.write(sent);
				System.out.println("sent packet "+sent);
				received=reader.read();
				while (received!=sent){
					System.out.println("receive: "+received);
					received=reader.read();
					//loop
				}
				System.out.println("received ACK "+sent);
				sent++;
			}
			
			
			
			socket.close();
		}catch(Exception e){System.out.println(e);e.getStackTrace();}
		
		
	}
}