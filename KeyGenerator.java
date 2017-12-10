import java.rmi.*;
import java.math.*;
import java.util.UUID;

public interface KeyGenerator extends Remote {
	public UUID connect() throws RemoteException;
	public BigInteger getPrime() throws RemoteException;
	public BigInteger getPrimitive() throws RemoteException;
	public BigInteger getMod() throws RemoteException;
	public void setKey(UUID client, BigInteger primitive) throws RemoteException;
	public String sendToMainServer(UUID client, String username) throws RemoteException;
}
