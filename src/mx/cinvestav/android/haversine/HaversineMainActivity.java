package mx.cinvestav.android.haversine;

import mx.cinvestav.android.haversine.beans.Point;
import mx.cinvestav.android.haversine.service.HaversineCalculatorService;
import mx.cinvestav.android.haversine.service.HaversineCalculatorService.LocalBinder;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class HaversineMainActivity extends Activity {
	private HaversineCalculatorService haversineService;
	private boolean isBound = false;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        // Eventos de butones
        Button button = (Button) findViewById(R.id.btn_calc);
        button.setOnClickListener(calcListener);
    }
    
    @Override
    protected void onStart() {
    	super.onStart();
    	
    	// Hacer el bind al servicio
    	doBindService();
    }
    
    @Override
    protected void onRestart() {
    	super.onRestart();
    	doBindService();
    }
    
    private OnClickListener calcListener = new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			double distance;
			Point p1 = new Point();
			Point p2 = new Point();
			
			// Verifica que los campos tengan datos
			try {
				EditText txt = (EditText) findViewById(R.id.txt_lat1);
				p1.setLatitude(Double.valueOf(txt.getText().toString()));
				
				txt = (EditText) findViewById(R.id.txt_lon1);
				p1.setLongitude(Double.valueOf(txt.getText().toString()));
				
				txt = (EditText) findViewById(R.id.txt_lat2);
				p2.setLatitude(Double.valueOf(txt.getText().toString()));
				
				txt = (EditText) findViewById(R.id.txt_lon2);
				p2.setLongitude(Double.valueOf(txt.getText().toString()));
			} catch (Exception e) {
				// TODO: Personalizar error cuando no se pongan los campos
				// con datos validos
				return;
			}
			
			Resources resources = getResources();
			
			// distancia usando haversine
			distance = haversineService.getDistance(p1, p2);
			TextView res = (TextView) findViewById(R.id.txt_result);
			res.setText(String.format("%.4f", distance).
					concat(" " + resources.getString(R.string.sym_km)));
			
			// distancia del SDK
			distance = haversineService.getSDKDistance(p1, p2);
			res = (TextView) findViewById(R.id.txt_sdkres);
			res.setText(String.format("%.4f", distance).
					concat(" " + resources.getString(R.string.sym_km)));
			
			// Cuadro con los resultados
			LinearLayout layout = (LinearLayout) findViewById(R.id.lay_result);
			layout.setVisibility(View.VISIBLE);
		}
	};
	
	// Usado para recibir el bind del servicio y poderse conectar
	private ServiceConnection connection = new ServiceConnection() {
		
		@Override
		public void onServiceDisconnected(ComponentName name) {
			haversineService = null;
			isBound = false;
		}
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			LocalBinder binder = (LocalBinder) service;
			haversineService = binder.getService();
			isBound = true;
		}
	};
	
	void doBindService() {
		if (!isBound) {
			Intent intent = new Intent(this, HaversineCalculatorService.class);
	    	bindService(intent, connection, Context.BIND_AUTO_CREATE);
	    	isBound = true;
		}
	}
	
	void doUnbindService() {
		if (isBound) {
			unbindService(connection);
			isBound = false;
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		doUnbindService();
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		doUnbindService();
	}
}