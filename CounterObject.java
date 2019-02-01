import java.io.Serializable;

public class CounterObject extends Thread implements Serializable{
	
	private static final long serialVersionUID = 119L;
	   
	long counter = 0;
	
	public CounterObject()
	{
		
	}
	public long getCounter() {
		return counter;
	}

	public void setCounter(long counter) {
		this.counter = counter;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(true)
		{
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			counter = counter +1;
			
		}
	}
	
	

	
}
