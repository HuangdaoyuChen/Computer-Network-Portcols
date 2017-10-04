import java.net.*;
import java.io.*;
import java.util.Scanner;

public class GBNT {
	
	static int lastAck= 0;
	public static void main (String[] args) throws UnknownHostException, IOException{
		try{
			String CRLF = "\r\n";
			//create a client socket
			Socket socket = new Socket("localhost",9876);
			//define reader and wirter
			BufferedReader welreader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			DataOutputStream writer = new DataOutputStream (socket.getOutputStream());
			DataInputStream reader = new DataInputStream (socket.getInputStream());
					
			
			//define a scanner
			Scanner scr = new Scanner(System.in);
			
			//read noPackets and proError;
			System.out.println("input noPackets: ");
			int noPackets = scr.nextInt();
			
			System.out.println("input probError: ");
			int probError = scr.nextInt();
			
			//create a thread
			Thread listener = new Thread(new Listener(socket,noPackets));
			listener.start();
			
			//send the number of packets
			writer.write(noPackets);
			writer.write(probError);
			
			
			int sent = 1;
			for (int i = 1; i<=noPackets; i++){
				writer.write(sent);
				System.out.println("sent " + sent);
				sent++;
			}
			
						
			listener.join();
			socket.close();
		
		}catch(Exception e){System.out.println(e);e.getStackTrace();}
	}
	
	public static void setAckNum(int ackNum){
		GBNT.lastAck=ackNum;
	}
}
