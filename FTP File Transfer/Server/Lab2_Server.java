import java.net.*;
import java.io.*;

public class Lab2_Server{
	public static void main (String[] args)throws UnknownHostException, IOException{
		try{
			//creat the socket
			ServerSocket Myserver = new ServerSocket(9527);
			System.out.println("Waiting......");
			
			//the loop
			while(true){
				
				Socket socket = Myserver.accept();
				System.out.println("Connection established!");
				
				//define reader and wirter
				BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				DataOutputStream writer = new DataOutputStream (socket.getOutputStream());
				DataOutputStream socket_out = new DataOutputStream (socket.getOutputStream());
				
				
				String CRLF = "\r\n";
				
				//welcome message;
				writer.writeBytes("Connection Established. Input filename"+CRLF);
				
				
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
							socket.close();
							System.out.println("Done sending data!");
							break;
						}
						socket_out.write(buffer, 0, len);
						tLen+=len;
						System.out.println("Transfering: "+len+" Total: "+tLen);
					}
					
					//close the fis
					fis.close();
					
				}
				else {
					writer.writeBytes("The file "+filename+" does not exist"+CRLF);
				}
				socket.close();
			}
			
			
			//close the socket
			System.out.println("Task finished!");
			Myserver.close();
		}
		catch(Exception e){e.getStackTrace();}
		
	}
}