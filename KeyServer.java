import java.rmi.*;
import java.math.*;
import java.util.UUID;

public interface KeyServer extends Remote {
	public void connect(UUID clientID, KeyClient client) throws RemoteException;
	public BigInteger getPrime() throws RemoteException;
	public BigInteger getPrimitive() throws RemoteException;
	public BigInteger getMod() throws RemoteException;
	public void setKey(UUID client, BigInteger primitive) throws RemoteException;
	public void requestCipher(UUID client, String username) throws RemoteException;
}
