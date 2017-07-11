package model;

public class User {
	
	private Long Id;
	
	private String userName;
	
	private String password;
	

	public User() {
		super();
		// TODO Auto-generated constructor stub
	}

	public User(Long id, String userName, String password) {
		super();
		Id = id;
		this.userName = userName;
		this.password = password;
	}

	public Long getId() {
		return Id;
	}

	public void setId(Long id) {
		Id = id;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	

}
