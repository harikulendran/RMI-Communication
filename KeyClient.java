import java.rmi.*;

public interface KeyClient extends Remote {
	public void sendData(String data) throws RemoteException;
}
