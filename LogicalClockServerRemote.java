

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.HashMap;

public interface LogicalClockServerRemote extends LogicalClockRemote{

	boolean registerClient(String processObject, String ipaddress, int rmiPort) throws RemoteException, MalformedURLException, NotBoundException;
	
	void closeClients() throws RemoteException, MalformedURLException, NotBoundException;
	
}
