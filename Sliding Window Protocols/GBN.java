import java.net.*;
import java.io.*;
import java.util.Scanner;

public class GBN {
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
			
			writer.write(noPackets);
			writer.write(probError);
			
			int sent = 1;
			for (int i = 1; i<=noPackets; i++){
				writer.write(sent);
				sent++;
			}
			scr.next();
			socket.close();
			
		}catch(Exception e){System.out.println(e);e.getStackTrace();}
	}
}