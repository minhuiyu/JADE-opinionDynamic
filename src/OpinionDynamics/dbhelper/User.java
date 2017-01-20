package OpinionDynamics.dbhelper;

import FIPA.DateTime;

public class User {
	private String user_realID;
	private String register_time;
	
	
	public String getUser_realID() {
		return user_realID;
	}
	public void setUser_realID(String user_realID) {
		this.user_realID = user_realID;
	}
	
	public String getRegister_time() {
		return register_time;
	}
	public void setRegister_time(String register_time) {
		this.register_time = register_time;
	}
}
