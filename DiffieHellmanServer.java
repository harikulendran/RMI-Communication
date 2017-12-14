import java.math.*;
import java.rmi.*;
import java.rmi.server.*;
import java.rmi.registry.*;
import java.util.*;

public abstract class DiffieHellmanServer extends DiffieHellmanKeyGenerator implements KeyServer {
	Map<UUID,ClientInfo> clients = new HashMap<>(); 

	public DiffieHellmanServer() {
		super();
		//Policy file used is the one provided by Professor Norman in his test zip
		//as it is the correct policy for the job I didn't see a reason to change it
		System.setProperty("java.security.policy", "mypolicy");
		if (System.getSecurityManager() == null)
			System.setSecurityManager(new SecurityManager());
	}

	public synchronized void connect(UUID clientID, KeyClient client) throws RemoteException {
		BigInteger uniquePV = new BigInteger(5, rand);
		boolean unique = true;
		do {
			unique = true;
			for (Map.Entry<UUID,ClientInfo> e : clients.entrySet())
				if (e.getValue().privateValue.equals(uniquePV)) {
					unique = false;
					uniquePV = new BigInteger(5, rand);
					break;
				}
		} while (!unique);

		ClientInfo uniqueClient = new ClientInfo(uniquePV, client);
		clients.put(clientID, uniqueClient);
		System.out.println("connected: " + clientID);
	}

	public BigInteger getPrime() throws RemoteException {
		return prime;
	}
	public BigInteger getPrimitive() throws RemoteException {
		return primitiveRoot;
	}
	public synchronized BigInteger getMod(UUID client) throws RemoteException {
		return calculateKey(prime, primitiveRoot, clients.get(client).privateValue);
	}
	public synchronized void setKey(UUID client, BigInteger primitive) {
		clients.get(client).key = calculateKey(prime, primitive, clients.get(client).privateValue);
		System.out.println(client + " : " + clients.get(client).key);
	}

	public synchronized void requestCipher(UUID client, String username) {
		try {
			Registry timReg = LocateRegistry.getRegistry("svm-tjn1f15-comp2207.ecs.soton.ac.uk", 12345);
			CiphertextInterface ctstub = (CiphertextInterface) timReg.lookup("CiphertextProvider");
			clients.get(client).client.sendData(ctstub.get(username, clients.get(client).key.intValue()));
			close(client);
		} catch (RemoteException | NotBoundException e) {
			e.printStackTrace();
		}
	}

	public synchronized void close(UUID client) throws RemoteException {
		KeyClient temp = clients.get(client).client;
		clients.remove(client);
		temp.close();
	}

	private class ClientInfo {
		public KeyClient client;
		public BigInteger privateValue;
		public BigInteger key;
		public ClientInfo(BigInteger privateValue, KeyClient client) {
			this.privateValue = privateValue;
			this.client = client;
		}
	}
}
