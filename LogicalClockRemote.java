import java.rmi.Remote;
import java.rmi.RemoteException;

public interface LogicalClockRemote extends Remote{

	void setLogicalCounter(CounterObject counterObject) throws RemoteException;
	
	void setLogicaEndFlag(boolean flag) throws RemoteException;
	
	boolean getLogicaTimerFlag() throws RemoteException;
	
	CounterObject getLogicaCounter() throws RemoteException;
}
