import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.PriorityQueue;
import java.util.Collections;
import java.util.Random;
import java.nio.Buffer;
import java.nio.ByteBuffer;

public class MainClient{
	
	static int lastAck= 0;
	
	public static void main(String[] args) {
		
		try{
			String CRLF = "\r\n";
			Scanner scr = new Scanner(System.in);
			int cwnd = 1;
			int ssthresh = 1024;
			
			
			//connect to server
			//create a client socket
			System.out.println("connected to localhost 9876");
			Socket socket = new Socket("localhost",9876);
			
			
			//define reader and writer
			BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			DataOutputStream writer = new DataOutputStream (socket.getOutputStream());
			
			
			//get the number of nodes
			System.out.println("waiting waiting to receive the number of nodes...");
			String line = reader.readLine();
			int noNodes = Integer.parseInt(line);
			System.out.println("number of nodes: "+noNodes);
			
			//read the adjancet nodes information
			line = reader.readLine();
			// Create an adjacency matrix after reading from server
			double[][] matrix = new double[noNodes][noNodes];
			StringTokenizer st = new StringTokenizer(line);
			for (int i=0; i < noNodes; i++){
				for (int j=0; j<noNodes; j++){
						matrix[i][j]=Double.parseDouble(st.nextToken());

				}
			}
			System.out.println();
			System.out.println("Adjacency Matrix: ");
			for (int i = 0; i < noNodes; i++) {
				for (int j=0;j<noNodes;j++)
					System.out.print(matrix[i][j]+" ");
				
				System.out.println();
			}
			System.out.println();
			
			
			//calculate the paths that start from node 0
			//The nodes are stored in a list, nodeList
			List<Node> nodeList = new ArrayList<Node>();
			for(int i = 0; i < noNodes; i++){
				nodeList.add(new Node(i));
			}
			// Create edges from adjacency matrix
			adjacenyToEdges(matrix, nodeList);
			computePaths(nodeList.get(0));
			System.out.println("Shortest path start from Node "+nodeList.get(0).name);
			for (int j=0;j<noNodes;j++){
				List<Integer> path=getShortestPathTo(nodeList.get(j));
				System.out.println("Total time to reach node "+nodeList.get(j).name+": "+nodeList.get(j).minDistance+" ms, Path: "+path);
			}
			System.out.println();
			
			
			//send the shortest path to the server
			List<Integer> path = getShortestPathTo(nodeList.get(noNodes-1));
			writer.writeBytes(path.toString()+CRLF);
			//set the timeoutInterval
			double timeoutInterval = nodeList.get(noNodes-1).minDistance*2+200;
			System.out.println("TimeoutInterval is set to be "+timeoutInterval+"ms");
			System.out.println();
			
			
			//get the file
			System.out.print("Enter the name of the file: ");
			String fileName = scr.next();
			File file = new File(fileName);
			//record the last byte array lens
			int len=0;
			
			//transfer the file
			if (file.exists()){
				//send the file name
				System.out.println("sending file name: "+fileName);
				writer.writeBytes(fileName+CRLF);
				FileInputStream fis= new FileInputStream(file);
				System.out.println();
				
				
				//send the numberofPacket
				long fileLen = file.length();
				int noPackets = (int)Math.ceil((double)fileLen/(double)1000.0);
				writer.writeBytes(Long.toString(noPackets)+CRLF);
				
				//get all the bytearray ready
				byte[][] buffer = new byte[noPackets][1004];
				for (int i = 0; i<noPackets; i++){
					buffer[i] = ByteBuffer.allocate(1004).putInt(i+1).array();
					len=fis.read(buffer[i], 4, 1000);
				}
				System.out.println(buffer[noPackets-1].length);
				
				//create a listener thread
				Thread listener = new Thread(new Listener(socket,noPackets));
				listener.start();
				
				
				//send the packets
				//initialize the timer
				long[] timer = new long[noPackets];
				for(int i=0;i<noPackets;i++){
					timer[i]=-1;
				}
				
				long startTime = System.currentTimeMillis();
				
				int sent=1;
				int head=1;
				boolean flag=true;
				//===================writer.write(buffer[0]);
				while (lastAck<noPackets){
					
					//get the lastAck, in case the other thread change it
					int lAck=lastAck;
					
					//if window is not full
					if (flag&&sent-lAck<=cwnd){
						System.out.println("lastAck="+lAck);
						System.out.println("cwnd: "+cwnd);
						//send cwnd packets
						head=sent;
						for(;sent<head+cwnd&&sent<=noPackets;sent++){
							writer.write(buffer[sent-1]);
							timer[sent-1]=System.currentTimeMillis();
							System.out.println("sending package no."+sent);
						}
						flag=false;
					}
					//all sent packet recived
					else if (!flag&&lAck>=sent-1){
						System.out.println("lastAck="+lAck);
						//can sent the packets
						flag=true;
						sent=lAck+1;
						System.out.println("set sent to "+sent);
						if (cwnd<ssthresh)
							cwnd*=2;
						else 
							cwnd++;
					}
					//timeout
					else if (timer[lAck]>0&&System.currentTimeMillis()-timer[lAck]>timeoutInterval){
						System.out.println("lastAck="+lAck);
						System.out.println("time out on packet no."+(lAck+1)+" sent="+sent);
						flag=true;
						ssthresh=cwnd/2;
						cwnd=1;
						sent=lAck+1;
					}
					Thread.yield();
				}
				System.out.println("finish,QUIT");
				
				
				//close the fileinputstream
				fis.close();
				
				
			}
			else{
				System.out.println("The file "+fileName+" does not exist!");
			}
			
			
			
			
			
			
			//close the socket
			socket.close();
			
		}catch(Exception e){System.out.println(e);e.getStackTrace();}
		
		
	}
	
	public static void setAckNum(int ackNum){
		MainClient.lastAck=ackNum;
	}
	
	
	//find path functions
	public static void adjacenyToEdges(double[][] matrix, List<Node> v)
	{
		for(int i = 0; i < matrix.length; i++)
		{
			v.get(i).neighbors = new Edge[matrix.length];
			for(int j = 0; j < matrix.length; j++)
			{
				v.get(i).neighbors[j] =  new Edge(v.get(j), matrix[i][j]);	
			}
		}
	}
	public static void computePaths(Node source)
	{
		// Complete the body of this function
		//set the minDistance to be 0
		source.minDistance=0;
		
		//define a priprityQueue
		PriorityQueue<Node> NodeQueue = new PriorityQueue<Node>();
		
		//add the source node to the queue
		NodeQueue.add(source);
		
		//while the queue has more elements
		while(!NodeQueue.isEmpty()){
			
			//poll the top of the queue
			Node sourceNode = NodeQueue.poll();
			
			//for each edge in the source node
			for (int i=0; i<sourceNode.neighbors.length; i++){
				Node targetNode = sourceNode.neighbors[i].target;
				double distanceThroughSource = sourceNode.minDistance+sourceNode.neighbors[i].weight;
				
				if (distanceThroughSource<targetNode.minDistance){
					//need to remove targetnode from the queue, becuase we need to insert it again.
					NodeQueue.remove(targetNode);
					targetNode.minDistance=distanceThroughSource;
					targetNode.previous=sourceNode;
					NodeQueue.add(targetNode);
				}
			}
		}
	}

	public static List<Integer> getShortestPathTo(Node target)
	{
		// Complete the body of this function
		List<Integer> path = new ArrayList<Integer>();
		path.add(target.name);
		while (target.previous!=null){
			target = target.previous;
			path.add(target.name);
		}
		
		//reverse the path
		Collections.reverse(path);
		return path;
	}
	
}

//The network is represented by a graph, that contains nodes and edges
class Node implements Comparable<Node>
{
	public final int name;
	public Edge[] neighbors;
	public double minDistance = Double.POSITIVE_INFINITY;
	public Node previous;     // to keep the path
	public Node(int argName) 
	{ 
		name = argName; 
	}

	public int compareTo(Node other)
	{
		return Double.compare(minDistance, other.minDistance);
	}
}

class Edge
{
	public final Node target;
	public final double weight;
	public Edge(Node argTarget, double argWeight)
	{ 
		target = argTarget;
		weight = argWeight; 
	}
}