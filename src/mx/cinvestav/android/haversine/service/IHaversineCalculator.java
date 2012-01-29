package mx.cinvestav.android.haversine.service;

import mx.cinvestav.android.haversine.beans.Point;

public interface IHaversineCalculator {

	static final double DEG_RAD = 0.01745329251994;
	static final double R_EARTH = 6367.45;
	
	double getDistance(Point p1, Point p2);
	double getSDKDistance(Point p1, Point p2);
}
