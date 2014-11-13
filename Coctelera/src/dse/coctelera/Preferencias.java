
/*
 * Copyright (C) 2014 Drinkuino
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package dse.coctelera;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import at.abraxas.amarino.AmarinoIntent;

//Implementa los m�todos de OnClickListener para los botones de recarga
public class Preferencias extends Activity implements OnClickListener {

	GestorPreferencias gestorPreferencias; //Para obtener y editar datos de las preferencias compartidas (SharedPreferences)
	private AmarinoReceptorEstadoConexion receptor_conexion = new AmarinoReceptorEstadoConexion(); //Para obtener datos sobre la conexi�n desde Amarino
	
	private TextView[] textViews_niveles; //Array que almacenar� las referecias a los textViews que informan sobre el nivel de cada l�quido
	private Button[] buttons_recargas; //Array que almacenar� las referencias a los buttons de recarga del recipiente
	private EditText editText_direccionBluetooth;
	private ImageView imageView_estadoConexion;
	
	private static final int NIVEL_MAXIMO = 2000; //Capacidad m�xima de un recipiente en mililitros 
	private String direccion_bluetooth;
	private int[] niveles;
	private boolean estado_conexion; //Estado de la conexi�n bluetooth (true = conectado) 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.preferencias);
		
		gestorPreferencias = new GestorPreferencias(this);
		
		//Componentes para mostrar y editar el nivel de cada recipiente
		buttons_recargas = obtenerButtonsRecargas();
		textViews_niveles = obtenerTextViewsNiveles();
		editText_direccionBluetooth = (EditText) findViewById(R.id.editText_direccionBluetooth);
		imageView_estadoConexion = (ImageView) findViewById(R.id.imageView_estadoConexion);
		//Se registran los OnClickListener para los botones de recarga
		registrarClickListeners(buttons_recargas);
	}
	
	//Se ejecutar� cada vez que se pase por esta actividad
	@Override
	protected void onResume() {
		super.onResume();
		
		//Para recibir difusiones de "Intents" tenemos que registrar el receptor
		registerReceiver(receptor_conexion, new IntentFilter(AmarinoIntent.ACTION_CONNECTED));
		
		//Se obtienen los datos de las preferencias
		direccion_bluetooth = gestorPreferencias.obtenerDireccionBluetooth();
		niveles = gestorPreferencias.obtenerNiveles();
		estado_conexion = gestorPreferencias.obtenerEstadoConexion();
		//Se actualizan los componentes que muestran la informaci�n
		editarTextViewsNiveles();
		editText_direccionBluetooth.setText(direccion_bluetooth);
		establecerImagen();
	}
	
	//Se ejecutar� cuando se pare la actividad
	@Override
	protected void onStop() {
		super.onStop();
		
		//Se obtienen los valores de los componentes
		direccion_bluetooth = obtenerDireccionBluetooth();
		
		//Se actualizan las preferencias
		gestorPreferencias.editarDireccionBluetooth(direccion_bluetooth);
		gestorPreferencias.editarNiveles(niveles);
		gestorPreferencias.editarEstadoConexion(estado_conexion);
		
		unregisterReceiver(receptor_conexion); //Una vez detenemos la actividad  desregistramos el receptor
	}

	//Devuelve las referencias a los botones de recarga de recipientes ordenadas en un array
	private Button[] obtenerButtonsRecargas() {
		Button[] buttons_recargas = new Button[4];
		buttons_recargas[0] = (Button) findViewById(R.id.button_reiniciarL1);
		buttons_recargas[1] = (Button) findViewById(R.id.button_reiniciarL2);
		buttons_recargas[2] = (Button) findViewById(R.id.button_reiniciarL3);
		buttons_recargas[3] = (Button) findViewById(R.id.button_reiniciarL4);
		
		return buttons_recargas;
	}

	//Devuelve las referencias a los textViews de niveles de cada recipiente ordenadas en un array
	private TextView[] obtenerTextViewsNiveles() {
		TextView[] textViews_niveles = new TextView[4];
		textViews_niveles[0] = (TextView) findViewById(R.id.textView_nivelL1);
		textViews_niveles[1] = (TextView) findViewById(R.id.textView_nivelL2);
		textViews_niveles[2] = (TextView) findViewById(R.id.textView_nivelL3);
		textViews_niveles[3] = (TextView) findViewById(R.id.textView_nivelL4);
		
		return textViews_niveles;
	}	
	
	//Establece el OnClickListener a los botones de recarga
	private void registrarClickListeners(Button[] botones) {
		for (int i = 0; i < buttons_recargas.length; i++) {
			botones[i].setOnClickListener(this);
		}
	}
	
	//Devuelve la posici�n en el array de vistas donde se encuentra una vista buscada, -1 si no est�
	private int buscarVista(View vista_buscada, View[] vistas) {
		int identificadorVista = vista_buscada.getId();
		int encontrada = -1;
		int i = 0;
		while ((i < vistas.length) && (encontrada == -1)) {
			if (identificadorVista == vistas[i].getId()) {
				encontrada = i;
			}
			i++;
		}
		
		return encontrada;
	}	
	
	//Escribe el nivel del recipiente en la textView indicada mediante su posici�n en el array de textsViews
	private void editarTextViewNivel(int posicion, int nivel) {
		textViews_niveles[posicion].setText("L" + (posicion + 1) + ": " + nivel + " ml de " + NIVEL_MAXIMO + " ml");
	}
	
	//Escribe los niveles de los recipientes en cada textView correspondiente
	private void editarTextViewsNiveles() {
		for (int i = 0; i < textViews_niveles.length; i++) {
			editarTextViewNivel(i, niveles[i]);
		}
	}
	
	//Establece la imagen seg�n el estado de conexi�n (true = conectado)
	private void establecerImagen() {
		if (estado_conexion) {
			imageView_estadoConexion.setImageResource(R.drawable.ic_tick);
		}
		else {
			imageView_estadoConexion.setImageResource(R.drawable.ic_error);
		}
	}
	
	//Obtiene el String de la direcci�n del editText correspondiente
	private String obtenerDireccionBluetooth() {
		String direccion_bluetooth = editText_direccionBluetooth.getText().toString();
		
		return direccion_bluetooth;
	}
	
	//Se ejecuta al pulsar el bot�n de "Atr�s": vuelve a la actividad desde la que se llam� sin producir ning�n resultado
	public void volverAtras(View view) {
		setResult(RESULT_CANCELED); //Indica que no ha producido resultados (no tiene mezcla que comunicar para ser a�adida)
		finish(); //Finaliza la actividad
	}
	
	//Se ejecuta al pulsar el bot�n con el s�mbolo de bluetooth: env�a una solicitud de conexi�n a Amarino
	public void conectarBluetooth(View view) {
		Intent intent = new Intent(AmarinoIntent.ACTION_CONNECT);
		direccion_bluetooth = obtenerDireccionBluetooth();
		intent.putExtra(AmarinoIntent.EXTRA_DEVICE_ADDRESS, direccion_bluetooth);
		sendBroadcast(intent);
	}
	
	@Override
	public void onClick(View view) {
		Button button = (Button) view; //Se obtiene el bot�n que se ha pulsado
		int posicion = buscarVista(button, buttons_recargas); //Se obtiene la posici�n que ocupa el bot�n en el array para identificarlo
		if (posicion > -1) {
			niveles[posicion] = NIVEL_MAXIMO; //Se cambia el nivel en el array de enteros
			editarTextViewNivel(posicion, NIVEL_MAXIMO); //Se escribe el nivel m�ximo en el textView correspondiente
		}
	}
	
	//Clase para recibir datos desde Amarino a trav�s de "Intents" desde la aplicaci�n Amarino para comprobar si se ha procucido la conexi�n
	public class AmarinoReceptorEstadoConexion extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			String direccion_recibida = intent.getStringExtra(AmarinoIntent.EXTRA_DEVICE_ADDRESS); //Se obtiene la direcci�n recibida
			if (direccion_bluetooth.equalsIgnoreCase(direccion_recibida)) { //Si la direcci�n reibida es igual a la que nos quer�amos conectar
				if (AmarinoIntent.ACTION_CONNECTED.equals(action)) {
					estado_conexion = true;
				}
				else if (AmarinoIntent.ACTION_DISCONNECTED.equals(action)) {
					estado_conexion = false;
				}
				else if (AmarinoIntent.ACTION_CONNECTION_FAILED.equals(action)) {
					estado_conexion = false;
				}
				establecerImagen(); //Se establece la imagen representativa del estado de conexi�n en funci�n de este
			}			
		}
	}	
	
}
