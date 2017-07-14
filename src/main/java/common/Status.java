package common;

public enum Status {
	
	CREATED("CREATED"),
	INPROCESS("INPROCESS"),
	COMPLETED("COMPLETED");
	
	private String current;
	
	private Status(String current) {
		this.current = current;
	}
	
	public String getCurrent(){
		return this.current;
	}
	
	
}
