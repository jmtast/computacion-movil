package dc.uba.taxinow.model;

public class Travel {

	private String requestId;
	private String passengerId;
	private String taxiDriverId;
	
	public Travel(String passengerId,
			String taxiDriverId) {
		super();
		this.requestId = null;
		this.passengerId = passengerId;
		this.taxiDriverId = taxiDriverId;
	}
	
	public Travel(String requestId, String passengerId,
			String taxiDriverId) {
		super();
		this.requestId = requestId;
		this.passengerId = passengerId;
		this.taxiDriverId = taxiDriverId;
	}
	
	public String getRequestId() {
		return requestId;
	}
	public String getPassengerId() {
		return passengerId;
	}
	public String getTaxiDriverId() {
		return taxiDriverId;
	}
}
