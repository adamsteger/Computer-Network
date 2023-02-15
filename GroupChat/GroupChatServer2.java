
import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class GroupChatServer2 implements Runnable{
    private BufferedReader fromSockReader;
    private PrintWriter toSockWriter;
    private ArrayList<Thread> userThreads;

    public GroupChatServer2(BufferedReader reader, PrintWriter writer, ArrayList<Thread> users) {
        fromSockReader = reader;
        toSockWriter = writer;
        userThreads = users;
    }

    public PrintWriter getPrintWriter() {
        return toSockWriter;
    }

    public void run()
	{
		// Read from the keyboard and write to socket
		try {
			// Keep doing till user types EOF (Ctrl-D)
			while (true) {
				// Read a line from the user
				String line = fromSockReader.readLine();

				// If we get null, it means EOF, so quit
				if (line == null) {
					System.out.println("*** Client closed connection");
					break;
				}

                // Write to socket
				toSockWriter.println(line);

                for(Thread user: userThreads) {
                    // TODO broadcast message to all threads
                }
			}
		}
		catch (Exception e) {
			System.out.println(e);
			System.exit(1);
		}

		// End the other thread too
		System.exit(0);
	}

    public static void main(String args[]) {
        // Server needs a port to listen on
		if (args.length != 1) {
			System.out.println("usage: java TwoWayAsyncMesgServer <port>");
			System.exit(1);
		}

        // Get the port on which server should listen */
		int serverPort = Integer.parseInt(args[0]);

        ArrayList<Thread> userThreads = new ArrayList<>();

        while (true) {
            Socket clientSock = null;
            try {
                // Create a server socket with the given port
                ServerSocket serverSocket = new ServerSocket(serverPort);
    
                // Wait for a client and accept it
                System.out.println("Waiting for a client ...");
                clientSock = serverSocket.accept();
                System.out.println("Connected to a client at ('" +
                                        ((InetSocketAddress) clientSock.getRemoteSocketAddress()).getAddress().getHostAddress()
                                        + "', '" +
                                        ((InetSocketAddress) clientSock.getRemoteSocketAddress()).getPort()
                                        + "')"
                                        );

                // Prepare to write to socket with auto flush on
			    PrintWriter toSockWriter =
                        new PrintWriter(clientSock.getOutputStream(), true);

                // Prepare to read from keyboard
                BufferedReader fromSockReader = new BufferedReader(
                        new InputStreamReader(clientSock.getInputStream()));

                // Spawn a thread to read from user and write to socket
                Thread child = new Thread(
                    new GroupChatServer2(fromSockReader, toSockWriter, userThreads));
                
                // Add child thread to list of threads and start the thread
                userThreads.add(child);
                child.start();

                
            }
            catch(Exception e) {
                System.out.println(e);
                System.exit(1);
            }
        }
    }
}
