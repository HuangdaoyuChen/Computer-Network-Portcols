import java.net.*;
import java.io.*;

public class Lab2_TClient {
	public static void main (String[] args)throws UnknownHostException, IOException{
		try{
			//create a control socket
			Socket socket_c = new Socket("localhost",9527);
			BufferedReader reader = new BufferedReader(new InputStreamReader(socket_c.getInputStream()));
			DataOutputStream writer = new DataOutputStream (socket_c.getOutputStream());
			BufferedReader scnr = new BufferedReader(new InputStreamReader(System.in));
			String CRLF = "\r\n";
			
			//read the welcome message.
			System.out.println(reader.readLine());
			
			//loop
			while(true){
				//send request
				String filename=scnr.readLine();
				//String filename = "cytus.txt";//trying multithread
				writer.writeBytes(filename+CRLF);
				if (filename.equals("QUIT")){
					break;
				}
				//read the response message
				String responseM = reader.readLine();
				System.out.println(responseM);	
				
				//transfer the data
				if (responseM.equals("Ready to transfer the file")){
					int hostnumber = Integer.parseInt(reader.readLine());
					System.out.println("hostnumber: "+hostnumber);
					Thread thread = new Thread(new ClientTransfer(hostnumber,filename));
					thread.start();
				}
				
			}
			
			
			
			socket_c.close();
		}catch(Exception e){System.out.println(e);e.getStackTrace();}
	}
}

class ClientTransfer implements Runnable {
	int hostnumber;
	String filename;
	
	ClientTransfer(int hostnumber, String filename){
		this.hostnumber=hostnumber;
		this.filename = filename;
	}
	
	public void run() {
		try {
			Socket socket_t = new Socket("localhost", this.hostnumber);
			DataInputStream socket_in = new DataInputStream(socket_t.getInputStream());

			// create a buffer
			byte[] buffer = new byte[1024];
			File file = new File(filename);
			file.createNewFile();
			FileOutputStream fos = new FileOutputStream(file);

			// iterately read the buffer and write to the file'
			System.out.println("Transfering...");
			int tLen = 0;
			long time1 = System.currentTimeMillis();
			while (true) {
				int len = socket_in.read(buffer);
				if (len < 0) {
					socket_t.close();
					System.out.println("Done receiving data!");
					break;
				}
				tLen += len;
				fos.write(buffer, 0, len);
				System.out.println("Transfering: " + len + " Total: " + tLen);
			}
			long time2 = System.currentTimeMillis();
			System.out.println("transfer time: " + (time2 - time1));
			System.out.println("Filename: " + file.getName() + " Length: " + file.length());
			// close the fos
			fos.close();
			socket_t.close();
		} catch (Exception e) {
			System.out.println(e);
			e.getStackTrace();
		}
	}
}