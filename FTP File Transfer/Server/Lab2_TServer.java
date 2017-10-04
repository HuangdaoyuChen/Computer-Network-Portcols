import java.net.*;
import java.io.*;

public class Lab2_TServer{
	public static void main (String[] args)throws UnknownHostException, IOException{
		try{
			//create the control socket
			ServerSocket Myserver = new ServerSocket(9527);
			System.out.println("Waiting......");
			Socket socket = Myserver.accept();
			System.out.println("Connection established!");
			String CRLF = "\r\n";
			int hostnumber = 1994;
			BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			DataOutputStream writer = new DataOutputStream (socket.getOutputStream());
			
			
			//welcome message;
			writer.writeBytes("Connection Established. Input filename"+CRLF);
			
			//loop
			while (true){
				String filename = reader.readLine();
				System.out.println("checking file " +filename+"....");
				
				if (filename.equals("QUIT")){
					socket.close();
					break;
				}
				//create a file
				File file = new File(filename);
				if (file.exists()){
					//assign a new hostnumber;
					hostnumber++;
					writer.writeBytes("Ready to transfer the file"+CRLF);
					writer.writeBytes(String.valueOf(hostnumber)+CRLF);
					Thread thread = new Thread(new ServerTransfer(hostnumber,file));
					thread.start();
					
				}else {
					writer.writeBytes("The file "+filename+" does not exist"+CRLF);
				}
			}
			
			
			
			
			
		}catch(Exception e){System.out.println(e);e.getStackTrace();}
	}
}


class ServerTransfer implements Runnable {
	int hostnumber;
	File file;
	
	ServerTransfer(int hostnumber, File file){
		this.hostnumber=hostnumber;
		this.file=file;
	}
	
	public void run(){
		try{
			// create a new socket;
			ServerSocket Transferserver = new ServerSocket(hostnumber);
			System.out.println(String.valueOf(this.hostnumber) + " Connecting...");
			Socket socket_t = Transferserver.accept();
			System.out.println("connection established!");
			DataOutputStream dos = new DataOutputStream(socket_t.getOutputStream());

			byte[] buffer = new byte[1024];
			FileInputStream fis = new FileInputStream(this.file);

			// iteratively read the file;
			System.out.println("Filename: " + file.getName() + " Length: " + file.length());
			System.out.println("Transfering...");
			int tLen = 0;

			while (true) {
				int len = fis.read(buffer);
				// if reach the end of the file;
				if (len < 0) {
					socket_t.close();
					Transferserver.close();
					System.out.println("Done sending data!");
					break;
				}
				dos.write(buffer, 0, len);
				tLen += len;
				System.out.println("Transfering: " + len + " Total: " + tLen);
			}

			// close the fis
			socket_t.close();
			fis.close();
		}catch(Exception e){System.out.println(e);e.getStackTrace();}
	}
		
		
}
