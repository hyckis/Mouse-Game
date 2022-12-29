
public class User {
	
	private String userID;
	private int level;
	
	public User() {}
	
	public User(String userID, int level) {
		setUserID(userID);
		setLevel(level);
	}
	
	public void setUserID(String userID) {
		this.userID = userID;
	}
	public String getUserID() {
		return userID;
	}
	
	public void setLevel(int level) {
		this.level = level;
	}
	public int getLevel() {
		return level;
	}

}
