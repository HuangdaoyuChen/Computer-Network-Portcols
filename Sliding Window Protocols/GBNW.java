import java.net.*;
import java.io.*;
import java.util.Scanner;

public class GBNW {
	
	static int lastAck= 0;
	public static void main (String[] args) throws UnknownHostException, IOException{
		try{
			String CRLF = "\r\n";
			//create a client socket
			Socket socket = new Socket("localhost",9876);
			//define reader and wirter
			DataOutputStream writer = new DataOutputStream (socket.getOutputStream());

			
			
			
			
			//define a scanner
			Scanner scr = new Scanner(System.in);
			
			//read noPackets and proError, wSize and Timeout
			System.out.println("input noPackets: ");
			int noPackets = scr.nextInt();
			
			System.out.println("input probError: ");
			int probError = scr.nextInt();
			
			System.out.println("input wSize: ");
			int wSize = scr.nextInt();
			
			System.out.println("input timeOut: ");
			int timeOut = scr.nextInt();
			
			//create timer array;
			long[] timer = new long[wSize];
			
			//create a listener thread
			Thread listener = new Thread(new ListenerW(socket,noPackets));
			listener.start();
			
			//send the number of packets and probError;
			writer.write(noPackets);
			writer.write(probError);
			
			long startTime = System.currentTimeMillis();
			
			int sent=1;
			while (GBNW.lastAck<noPackets){
				int available = (sent-GBNW.lastAck);
				if (available<=wSize&&sent<=noPackets){
					writer.write(sent);
					timer[(sent-1)%wSize] = System.currentTimeMillis();
					System.out.println("sending package no."+sent);
					sent++;
				}
				else{
					if ((System.currentTimeMillis()-timer[GBNW.lastAck%wSize])>timeOut){
						sent=GBNW.lastAck+1;
					}
					else
						Thread.yield();
				}
			}
			
			
			
			
			System.out.println("Total time to send all packets is "+(System.currentTimeMillis()-startTime)/1000+" seconds");
			System.out.println("All packets have been sent successfully");
			System.out.println("QUIT");
			socket.close();
		
		}catch(Exception e){System.out.println(e);e.getStackTrace();}
	}
	
	public static void setAckNum(int ackNum){
		GBNW.lastAck=ackNum;
	}
}