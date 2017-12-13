import java.math.*;
import java.rmi.*;
import java.rmi.registry.*;
import java.rmi.server.*;
import java.io.IOException;
import java.util.UUID;

public class MyClient extends DiffieHellmanKeyGenerator implements KeyClient {
	private KeyServer server;
	private UUID myID;
	String username;

	public MyClient(String host, String username) {
		myID = UUID.randomUUID();
		this.username = username;
		connect(host);
		exchange();
	}

	private void connect(String host) {
		try {
			Registry registry = LocateRegistry.getRegistry(host);
			server = (KeyServer) registry.lookup("DHG");
			KeyClient thisClient = (KeyClient) UnicastRemoteObject.exportObject(this, 0);
			server.connect(myID, thisClient);
		} catch (IOException | NotBoundException e) {
			e.printStackTrace();
		}
	}

	private void exchange() {
		try {
			prime = server.getPrime();
			primitiveRoot = server.getPrimitive();
			mod = calculateKey(prime, primitiveRoot,privateValue);
			key = calculateKey(prime, server.getMod(myID),privateValue);
			server.setKey(myID, mod);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	public void requestCipher() {
		try {
			server.requestCipher(myID, username);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	public void sendData(String data) {
		Decrypter d = new Decrypter(data);
		System.out.println(d.decrypt(key.intValue()));
	}
	public void close() {
		//make sure the server has enough time to disconnect
		//before closing the client
		new Thread(() -> {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.exit(1);
		}).start();
	}

	public static void main(String[] args) {
		MyClient client = new MyClient(args[0],args[1]);
		client.requestCipher();
	}
}
