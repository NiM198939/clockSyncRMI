public class LogicalClock {

	CounterObject counterObject;
	Boolean endFlag;
	Boolean timerFlag;
	

	public LogicalClock() {
		timerFlag = false;
		endFlag = false;
	}

	public Boolean getEndFlag() {
		return this.endFlag;
	}

	public void setEndFlag(boolean flag)
	{
		this.endFlag = flag;
	}
	
	public Boolean getTimerFlag() {
		return timerFlag;
	}

	public void setTimerFlag(Boolean timerFlag) {
		this.timerFlag = timerFlag;
	}

	public CounterObject getCounter() {
		this.timerFlag = false;
		return this.counterObject;
	}

	public void setCounter(CounterObject counterObject) {
		this.timerFlag = true;
		this.counterObject = counterObject;
	}
	
	

}
