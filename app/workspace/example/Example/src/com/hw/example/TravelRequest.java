package com.hw.example;

import android.location.Location;

public class TravelRequest {
	
	private Location origin;
	private String destination;
	
	public TravelRequest(Location origin, String destination) {
		super();
		this.origin = origin;
		this.destination = destination;
	}
	
	public Location getOrigin() {
		return origin;
	}
	public String getDestination() {
		return destination;
	}
}
