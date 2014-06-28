package com.hw.example;

import android.location.Location;

public class TravelRequest {
	
	private Location origin;
	private String destination;
	private String passengerId;
	
	public TravelRequest(Location origin, String destination, String passengerId) {
		super();
		this.origin = origin;
		this.destination = destination;
		this.passengerId = passengerId;
	}
	
	public Location getOrigin() {
		return origin;
	}
	public String getDestination() {
		return destination;
	}

	public String getPassengerId() {
		return passengerId;
	}

}
