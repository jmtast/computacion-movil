package dc.uba.taxinow.model;

public class TravelData {

	private String taxiDriverId;
	private String requestId;
	private String passengerId;
	private String passengerPosition;
	private String passengerAddress;
	
	
	
	public TravelData(String taxiDriverId, String requestId,
			String passengerId, String passengerPosition,
			String passengerAddress) {
		super();
		this.taxiDriverId = taxiDriverId;
		this.requestId = requestId;
		this.passengerId = passengerId;
		this.passengerPosition = passengerPosition;
		this.passengerAddress = passengerAddress;
	}
	
	public String getTaxiDriverId() {
		return taxiDriverId;
	}
	public String getRequestId() {
		return requestId;
	}
	public String getPassengerId() {
		return passengerId;
	}
	public String getPassengerPosition() {
		return passengerPosition;
	}
	public String getPassengerAddress() {
		return passengerAddress;
	}

	@Override
	public String toString() {
		return  passengerId + "; " + passengerAddress;
	}
	
	
}
