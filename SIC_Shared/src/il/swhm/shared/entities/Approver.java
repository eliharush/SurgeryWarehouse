package il.swhm.shared.entities;

public class Approver {
	private String id;
	private String password;
	private String signature;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}


	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getSignature() {
		return signature;
	}
	public void setSignature(String signature) {
		this.signature = signature;
	}
	
}