import java.net.*;
import java.io.*;

public class Lab2_Server2{
	public static void main (String[] args)throws UnknownHostException, IOException{
		try{
			//creat the socket
			ServerSocket Myserver = new ServerSocket(9527);
			System.out.println("Waiting......");
			Socket socket = Myserver.accept();
			System.out.println("Connection established!");
			String CRLF = "\r\n";
			String hostnumber = "4848";
			
			//define reader and wirter
			BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			DataOutputStream writer = new DataOutputStream (socket.getOutputStream());
			
			//welcome message;
			writer.writeBytes("Connection Established. Input filename"+CRLF);
			
			//the loop
			while(true){
				
				String filename = reader.readLine();
				System.out.println("checking file " +filename+"....");
				
				if (filename.equals("QUIT")){
					socket.close();
					break;
				}
				//create a file
				File file = new File(filename);
				if (file.exists()){
					
					writer.writeBytes("Ready to transfer the file"+CRLF);
					writer.writeBytes(hostnumber+CRLF);
					
					
					//create a new socket;
					ServerSocket Transferserver = new ServerSocket(Integer.parseInt(hostnumber));
					System.out.println("Connecting...");
					Socket socket_t = Transferserver.accept();
					System.out.println("connection established!");
					DataOutputStream dos = new DataOutputStream (socket_t.getOutputStream());
					
					byte[] buffer = new byte[1024];
					FileInputStream fis= new FileInputStream(file);
					
					//iteratively read the file;
					System.out.println("Filename: "+filename+" Length: "+file.length());
					System.out.println("Transfering...");
					int tLen=0;
					
					while (true){
						int len= fis.read(buffer);
						//if reach the end of the file;
						if (len<0) {
							socket_t.close();
							Transferserver.close();
							System.out.println("Done sending data!");
							break;
						}
						dos.write(buffer, 0, len);
						tLen+=len;
						System.out.println("Transfering: "+len+" Total: "+tLen);
					}
					
					
					//close the fis
					socket_t.close();
					fis.close();
					
					
				}
				else {
					writer.writeBytes("The file "+filename+" does not exist"+CRLF);
				}
				
			}
			
			
			//close the socket
			System.out.println("Task finished!");
			Myserver.close();
		}
		catch(Exception e){System.out.println(e);e.getStackTrace();}
		
	}
}