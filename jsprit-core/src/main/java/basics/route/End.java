/*******************************************************************************
 * Copyright (c) 2011 Stefan Schroeder.
 * eMail: stefan.schroeder@kit.edu
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     Stefan Schroeder - initial API and implementation
 ******************************************************************************/
package basics.route;

import util.Coordinate;

public final class End implements TourActivity {

	public static int creation = 0;
	
	public static End newInstance(String locationId, double earliestArrival, double latestArrival) {
		creation++;
		return new End(locationId,earliestArrival,latestArrival);
	}
	
	public static End copyOf(End end){
		return new End(end);
	}

	private String locationId;
	
	private Coordinate coordinate;
	
	public Coordinate getCoordinate() {
		return coordinate;
	}

	public void setCoordinate(Coordinate coordinate) {
		this.coordinate = coordinate;
	}

	private double endTime = -1;
	

	private double theoretical_earliestOperationStartTime;
	
	private double theoretical_latestOperationStartTime;

	private double arrTime;

	public void setTheoreticalEarliestOperationStartTime(double theoreticalEarliestOperationStartTime) {
		theoretical_earliestOperationStartTime = theoreticalEarliestOperationStartTime;
	}

	public void setTheoreticalLatestOperationStartTime(double theoreticalLatestOperationStartTime) {
		theoretical_latestOperationStartTime = theoreticalLatestOperationStartTime;
	}

	public End(String locationId, double theoreticalStart, double theoreticalEnd) {
		super();
		this.locationId = locationId;
		theoretical_earliestOperationStartTime = theoreticalStart;
		theoretical_latestOperationStartTime = theoreticalEnd;
		endTime = theoreticalEnd;
	}

	public End(End end) {
		this.locationId = end.getLocationId();
		theoretical_earliestOperationStartTime = end.getTheoreticalEarliestOperationStartTime();
		theoretical_latestOperationStartTime = end.getTheoreticalLatestOperationStartTime();
		arrTime = end.getArrTime();
		endTime = end.getEndTime();
	}

	public double getTheoreticalEarliestOperationStartTime() {
		return theoretical_earliestOperationStartTime;
	}

	public double getTheoreticalLatestOperationStartTime() {
		return theoretical_latestOperationStartTime;
	}

	public double getEndTime() {
		return endTime;
	}

	public void setEndTime(double endTime) {
		this.endTime = endTime;
	}

	public void setLocationId(String locationId) {
		this.locationId = locationId;
	}

	@Override
	public String getLocationId() {
		return locationId;
	}

	@Override
	public double getOperationTime() {
		return 0.0;
	}


	@Override
	public String toString() {
		return "[type="+getName()+"][locationId=" + getLocationId() 
		+ "][twStart=" + round(theoretical_earliestOperationStartTime)
		+ "][twEnd=" + round(theoretical_latestOperationStartTime) + "]";
	}
	
	private String round(double time) {
		if (time == Double.MAX_VALUE) {
			return "oo";
		}
		return "" + Math.round(time);
	}

	@Override
	public String getName() {
		return "end";
	}

	@Override
	public double getArrTime() {
		return this.arrTime;
	}

	@Override
	public void setArrTime(double arrTime) {
		this.arrTime = arrTime;
		
	}

	@Override
	public int getCapacityDemand() {
		return 0;
	}

	@Override
	public TourActivity duplicate() {
		return new End(this);
	}

}
