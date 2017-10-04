import java.net.*;
import java.io.*;

public class Lab2_Client{
	public static void main (String[] args)throws UnknownHostException, IOException{
		try{

			//the loop
			while (true){
				//create a client socket
				Socket socket = new Socket("localhost",9527);
				
				//define reader and wirter
				BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				DataOutputStream writer = new DataOutputStream (socket.getOutputStream());
				
				String CRLF = "\r\n";
				//define a system input reader.
				BufferedReader scnr = new BufferedReader(new InputStreamReader(System.in));
				
				//read the welcome message.
				System.out.println(reader.readLine());
				
				String filename=scnr.readLine();
				writer.writeBytes(filename+CRLF);
				if (filename.equals("QUIT")){
					socket.close();
					break;
				}
				//read the response message
				String responseM = reader.readLine();
				System.out.println(responseM);	
				
				//transfer the data
				if (responseM.equals("Ready to transfer the file")){			
					
					DataInputStream socket_in = new DataInputStream(socket.getInputStream());
					
					// create a buffer
					byte[] buffer = new byte[1024];
					File file = new File(filename);
					file.createNewFile();
					FileOutputStream fos= new FileOutputStream(file);
					
					//iterately read the buffer and write to the file'
					System.out.println("Transfering...");
					int tLen=0;
					long time1 = System.currentTimeMillis();
					while (true){
						int len =socket_in.read(buffer);
						if (len<0){
							socket.close();
							System.out.println("Done receiving data!");
							break;
						}
						tLen+=len;
						fos.write(buffer, 0, len);
						System.out.println("Transfering: "+len+" Total: "+tLen);
					}
					long time2 = System.currentTimeMillis();
					System.out.println("transfer time: "+(time2-time1));
					System.out.println("Filename: "+filename+" Length: "+file.length());
					//close the fos
					fos.close();
					
				}
				socket.close();
			}
			
			//close the socket
		}
		catch(Exception e){System.out.println(e);e.getStackTrace();}
		
	}
}