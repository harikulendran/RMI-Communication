import java.rmi.*;
import java.math.*;

public interface KeyGenerator extends Remote {
	public BigInteger getPrime() throws RemoteException;
	public BigInteger getPrimitive() throws RemoteException;
	public BigInteger getMod() throws RemoteException;
	public void setKey(BigInteger primitive) throws RemoteException;
	public String sendToMainServer(String username, int key) throws RemoteException;
}
