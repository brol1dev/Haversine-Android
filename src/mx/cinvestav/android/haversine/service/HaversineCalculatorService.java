package mx.cinvestav.android.haversine.service;

import mx.cinvestav.android.haversine.beans.Point;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Binder;
import android.os.IBinder;

public class HaversineCalculatorService extends Service 
	implements IHaversineCalculator{

	private final IBinder binder = new LocalBinder();
	
	@Override
	public IBinder onBind(Intent arg0) {
		return binder;
	}

	public class LocalBinder extends Binder {
		public HaversineCalculatorService getService() {
			return HaversineCalculatorService.this;
		}
	}
	
	/**
	 * Obtiene la distancia, en kilometros, entre dos puntos en la Tierra, 
	 * usando la formula de haversine.
	 */
	@Override
	public double getDistance(Point p1, Point p2) {
		double haversine, distance;
		double dLat, dLon;
		dLat = (p2.getLatitude() - p1.getLatitude()) * DEG_RAD;
		dLon = (p2.getLongitude() - p1.getLongitude()) * DEG_RAD;
		
		haversine = Math.sin(dLat * 0.5) * Math.sin(dLat * 0.5) + 
				Math.sin(dLon * 0.5) * Math.sin(dLon * 0.5) * 
				Math.cos(p1.getLatitude() * DEG_RAD) * 
				Math.cos(p2.getLatitude() * DEG_RAD);
		
		distance = Math.asin(Math.sqrt(haversine)) * R_EARTH * 2.0;
		return distance;
	}
	
	/**
	 * Obtiene la distancia, en kilometros, entre dos puntos en la Tierra,
	 * usando la clase Location del SDK
	 */
	@Override
	public double getSDKDistance(Point p1, Point p2) {
		Location l1 = new Location("point 1");
		l1.setLatitude(p1.getLatitude());
		l1.setLongitude(p1.getLongitude());
		
		Location l2 = new Location("point 2");
		l2.setLatitude(p2.getLatitude());
		l2.setLongitude(p2.getLongitude());
		
		return l1.distanceTo(l2) * 0.001;
	}
}
