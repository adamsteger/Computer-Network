/*
 * Implementation of a two way messaging server in Java
 * By Srihari Nelakuditi and Adam Steger for CSCE 416
 */

// I/O related package
import java.io.*;

// Socket related package
import java.net.*;
import java.util.ArrayList;

/*
 * This class does all of two way messaging server's job
 * It simultaneously watches both keyboard and socket for input
 *
 * It consists of 2 threads: parent thread (code inside main method)
 * and child thread (code inside run method)
 *
 * Parent thread spawns a child thread and then
 * reads from the socket and writes to the screen
 *
 * Child thread reads from the keyboard and writes to socket
 *
 * Since a thread is being created with this class object,
 * this class declaration includes "implements Runnable"
 */
public class GroupChatServer implements Runnable
{
	private Socket socket;

	// For managing the different child threads
	private static ArrayList<PrintWriter> clients = new ArrayList<>();


	// Method to add a new client to the list of active clients
	private static synchronized void addClient(PrintWriter client) {
		clients.add(client);
	}

	// Method to remove a client from the list of active clients
	private static synchronized void removeClient(PrintWriter client) {
		clients.remove(client);
	}

	// Method to broadcast a message to all clients except the sender
	private static void relayMessage(PrintWriter client, String message) {
		for (PrintWriter user : clients) {
			if(user != client) {
				user.println(message);
			}
		}
	}
    
	// Constructor sets the socket for the child thread
	public GroupChatServer(Socket socket)
	{
		this.socket = socket;
	}

	// The child thread starts here
	public void run()
	{
		try {

			// Prepare to write to socket with auto flush on
			PrintWriter toSockWriter =
			new PrintWriter(socket.getOutputStream(), true);

			// Add to the list of active clients
			addClient(toSockWriter);

			// Prepare to read from socket
			BufferedReader fromSockReader = new BufferedReader(
					new InputStreamReader(socket.getInputStream()));

			// Get the name from the client
			String name = fromSockReader.readLine();
			System.out.println("New client connected: " + name + "\n");

			// Keep doing till user is done
			while (true) {
				// Read a line from the socket
				String line = fromSockReader.readLine();

				// If we get null, it means EOF
				if (line.equals("null")) {
					// Tell user client quit
					System.out.println(name + " disconnected from the server");
					break;
				}

				// Write the line to the other users and the console
				String message = name + ": " + line;
				System.out.println(message);
                relayMessage(toSockWriter, message);
            }
			removeClient(toSockWriter);
		}
		catch (Exception e) {
			System.out.println(e);
			System.exit(1);
		}

	}

	/*
	 * The messaging server program starts from here.
	 * It sets up streams for reading & writing from keyboard and socket
	 * Spawns a thread which does the stuff under the run() method
	 * Then, it continues to read from socket and write to display
	 */
	public static void main(String args[])
	{
		// Server needs a port to listen on
		if (args.length != 1) {
			System.out.println("usage: java TwoWayAsyncMesgServer <port>");
			System.exit(1);
		}

		// Get the port on which server should listen */
		int serverPort = Integer.parseInt(args[0]);

		// Be prepared to catch socket related exceptions
		Socket clientSock = null;
		try {
			// Create a server socket with the given port
			ServerSocket serverSocket = new ServerSocket(serverPort);

			while(true) {
				clientSock = serverSocket.accept();
				
				// Spawn a thread to read from user and write to socket
				Thread child = new Thread(
						new GroupChatServer(clientSock));
				child.start();
			}
			
		}
		catch(Exception e) {
			System.out.println(e);
			System.exit(1);
		}

		// End the other thread too
		System.exit(0);
    }
}
