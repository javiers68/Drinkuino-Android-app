package dse.coctelera;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import at.abraxas.amarino.Amarino;
import at.abraxas.amarino.AmarinoIntent;

public class DetallesMezcla extends Activity {

	private Mezcla m; //Mezcla actualmente siendo visualizada
	private int posicion_m; //Posici�n en la lista de la mezcla actualmente siendo visualizada
	
	GestorPreferencias gestorPreferencias; //Para obtener y editar datos de las preferencias compartidas (SharedPreferences)
	private ArduinoReceptorDatos arduinoReceptor = new ArduinoReceptorDatos(); //Para obtener datos desde Arduino
	
	private ProgressDialog rueda_espera; //Rueda de carga que indica que la mezcla est� sirvi�ndose, desaparecer� cuando termine de servirse
	private AsyncTask<Void, Void, Boolean> temporizador_rueda; //Tarea para monitorizar el tiempo que se est� esperando respuesta por parte de Arduino para mostrar error en caso de que la expulsi�n de la mezcla se est� demorando demasiado
	private static final long TIEMPO_MAXIMO = 20000; //Tiempo m�ximo en milisegundos que puede pasar sin recibir respuesta de que la mezcla se sirvi� por parte de Arduino
	
	//Componentes donde se muestran los detalles de la mezcla en c�digo Java
	private TextView textView_nombre;
	private TextView textView_capacidad;
	private TextView[] textViews_liquidos;
	
	public final static String EXTRA_POSICION_MEZCLA_BORRADA = "dese.coctelera.POSICION_MEZCLA_BORRADA";
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.detalles_mezcla);
		
		gestorPreferencias = new GestorPreferencias(this);
		
		Intent intent = getIntent(); //Obtiene los datos comunicados a esta actividad
		//Se obtiene la mezcla comunicada
		m = (Mezcla) intent.getExtras().get(MisMezclas.EXTRA_MEZCLA_LISTADA);
		posicion_m = intent.getExtras().getInt(MisMezclas.EXTRA_POSICION_MEZCLA_LISTADA); //Obtiene la posici�n de la mezcla que se est� detallando
		//Se obtienen los componentes
		textView_nombre = (TextView) findViewById(R.id.textView_nombreMezcla);
		textView_capacidad = (TextView) findViewById(R.id.textView_capacidadRecipienteMezcla);
		textViews_liquidos = obtenerTextViewsLiquidos();
		//Se actualizan las etiquetas con los detalles de la mezcla
		textView_nombre.setText(m.getNombre());
		textView_capacidad.setText(String.valueOf(m.getCapacidad()));
		actualizarTextViewsLiquidos();
		//Se crea y personaliza la rueda de espera
		crearRuedaEspera();
	}
	
	//Devuelve las referencias a los textViews que informan de la cantidad y porcentaje de cada l�quido ordenadas en un array
	private TextView[] obtenerTextViewsLiquidos() {
		TextView[] textViews_liquidos = new TextView[4];
		textViews_liquidos[0] = (TextView) findViewById(R.id.textView_liquido1);
		textViews_liquidos[1] = (TextView) findViewById(R.id.textView_liquido2);
		textViews_liquidos[2] = (TextView) findViewById(R.id.textView_liquido3);
		textViews_liquidos[3] = (TextView) findViewById(R.id.textView_liquido4);
		
		return textViews_liquidos;
	}
	
	//Escribe el procentaje y cantidad de cada l�quido en su respectiva textView
	private void actualizarTextViewsLiquidos() {
		int[] cantidades = m.getCantidades();
		int[] porcentajes = m.getPorcentajes();
		for(int i = 0; i < textViews_liquidos.length; i++) {
			textViews_liquidos[i].setText("Líquido " + (i + 1) + ": " + porcentajes[i] + "% (" + cantidades[i] + " ml)");
		}
	}
	
	//Crea y personaliza la rueda de espera
	private void crearRuedaEspera() {
		rueda_espera = new ProgressDialog(this);
		rueda_espera.setMessage("La mezcla está sirviéndose");
		rueda_espera.setIndeterminate(true);
		rueda_espera.setCancelable(true);
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		//Para recibir difusiones de "Intents" tenemos que registrar el receptor
		registerReceiver(arduinoReceptor, new IntentFilter(AmarinoIntent.ACTION_RECEIVED));
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		unregisterReceiver(arduinoReceptor); //Una vez detenemos la actividad  desregistramos el receptor
	}

	//Se ejecuta al pulsar el bot�n de "Atr�s": vuelve a la actividad desde la que se llam� sin producir ning�n resultado
	public void volverAtras(View view) {
		setResult(RESULT_CANCELED); //Indica que no ha producido resultados (no tiene mezcla que comunicar para ser borrada)
		finish(); //Finaliza la actividad
	}
	
	public void borrarMezcla(View view) {
		Intent datos = new Intent(); //Crea un nuevo Intent para comunicar la posici�n en la lista de la mezcla a borrar
		datos.putExtra(EXTRA_POSICION_MEZCLA_BORRADA, posicion_m); //Comunica la posici�n de �sta mezcla que ser� borrada
		setResult(RESULT_OK, datos); //Comunica que se ha producido un resultado
		finish(); //Finaliza la actividad
	}
	
	//Muestra un di�logo simple (con bot�n aceptar) con el t�tulo y mensaje indicados, positivo indica si el mensaje muestra error o no
	private void mostrarDialogo(String titulo, String mensaje, boolean positivo) {
		//Se construye y muestra un di�logo informando del problema
		AlertDialog.Builder constructor_dialogo = new AlertDialog.Builder(this);
		constructor_dialogo.setTitle(titulo);
		constructor_dialogo.setMessage(mensaje);
		if (positivo == true) { //Si el se trata de dar un mensaje satisfactorio aparecer� el icono de tick
			constructor_dialogo.setIcon(R.drawable.ic_tick);
		}
		else {
			constructor_dialogo.setIcon(R.drawable.ic_error);
		}
		constructor_dialogo.setPositiveButton("Aceptar", null);
		constructor_dialogo.create();
		constructor_dialogo.show();
	}
	
	//Crea el temporizador y programa la tarea a realizar (mostrar error) si pasa el m�ximo de tiempo razonable
	private void iniciarTemporizador() {
		temporizador_rueda = new AsyncTask<Void, Void, Boolean>() { //Se crea el temporizador para que la aplicaci�n pueda responder si pasa demasiado tiempo sin recibirse respuesta por parte de Arduino de que la mezcla se sirvi� (algun problema ha ocurrido)
			@Override
			protected Boolean doInBackground(Void... params) { //Tarea que se realizar� en segundo plano, devuelve true si se ejecuta completa (durmi� el tiempo m�ximo)
				try {
					Thread.sleep(TIEMPO_MAXIMO); //Quda dormido durante el tiempo m�ximo razonable por el que esperar
				} catch (InterruptedException e) {
					return false;
				} //Duerme durante el tiempo m�ximo razonable por el que esperar una respuesta
				
				return true;
			}
			
	        @Override
	        protected void onPostExecute(Boolean result) { //Tarea que s ejecutar� despu�s de ejecutar la tarea en segundo plano
	            if (result) {
					rueda_espera.dismiss(); //Oculta la rueda de espera despu�s del tiempo m�ximo especificado
					//Se muestra un di�logo informando del problema
					mostrarDialogo("Problema sirviendo la mezcla", "No se recibió respuesta por parte de Arduino en un largo periodo de tiempo, revise la infraestructura", false);
					gestorPreferencias.editarEstadoConexion(false); //Se considera que el bluetooth ha sido desconectado
	            }
	        }		
		};
		temporizador_rueda.execute(); //Se comienza a ejecutar el temporizador
	}
	
	//Oculta la rueda de espera y detiene el temporizador
	private void cancelarTemporizador() {
		temporizador_rueda.cancel(true); //Se cancela el temporizador
		rueda_espera.dismiss(); //Oculta la rueda de espera		
	}
	
	//Se ejecuta al pulsar el bot�n "Servir", sirve la mezcla que se est� visualizando actualmente
	public void servirMezcla(View view) {
		
		boolean estadoConexion = gestorPreferencias.obtenerEstadoConexion(); //Obtiene el estado de conexi�n guardado en las preferencias
		int[] niveles_recipientes = gestorPreferencias.obtenerNiveles(); //Obtiene los niveles de los recipientes
		String direccion_bluetooth = gestorPreferencias.obtenerDireccionBluetooth(); //Obtiene la direcci�n del bluetooth
		
		if (estadoConexion) { //Solo si el bluetooth est� conectado manda los datos		
			int[] cantidades = m.getCantidades(); //Obtiene las cantidades de l�quido que conforman la mezcla
			boolean puede_servirse = Mezcla.puedeServirse(cantidades, niveles_recipientes); //Verifica que puede servirse
			if (puede_servirse) {
				Amarino.sendDataToArduino(this, direccion_bluetooth, 's', cantidades); //Env�a el array de cantidades a Arduino con el flag 's' para que Arduino sepa responder al evento
				iniciarTemporizador();
				rueda_espera.show(); //Se muestra la rueda de espera que desaparecer� cuando Arduino avise de que ha terminado la mezcla o cuando se supere el tiempo m�ximo razonable que se considera que se debe esperar
			}
			else {
				mostrarDialogo("No se puede servir la mezcla", "No hay suficiente cantidad en los despósitos como para servir la mezcla entera, compruebe los niveles en la pantalla de ajustes", false);
			}
		}
		else { //Si el bluetooth no est� conectado se muestra un mensaje de error
			mostrarDialogo("No se puede servir la mezcla", "El dispositivo bluetooth parece no estar conectado compruebe en la pantalla de ajustes", false);
		}
	}
	
	
	//Clase para recibir datos desde Arduino a trav�s de "Intents" desde la aplicaci�n Amarino, definida dentro de la clase DetallesMezcla para poder modificar elementos de la UI
	public class ArduinoReceptorDatos extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			int[] niveles = gestorPreferencias.obtenerNiveles(); //Obtiene los niveles de cada recipiente
			int[] cantidades = m.getCantidades();
			
			String datos_string = null;
			int dataType = intent.getIntExtra(AmarinoIntent.EXTRA_DATA_TYPE, -1); //Se obtiene el tipo de datos que "transporta" el Intent
			//Los datos desde Amarino vienen siempre como String, hay que convertir al dato que realmente mand� Arduino
			if (dataType == AmarinoIntent.STRING_EXTRA) {
				datos_string = intent.getStringExtra(AmarinoIntent.EXTRA_DATA); //Se obtienen los datos desde Amarino	
				if (datos_string != null) { //Se comprueba que realmente se han recibido datos
					cancelarTemporizador(); //Se cancela el temporizador, ya se recibi� respuesta
					if (datos_string.equalsIgnoreCase("k")) { //Si se mand� una 'k'  quiere decir que la mezcla termin� de servirse (ok)
						mostrarDialogo("Mezcla servida", "La mezcla se sirvió satisfactoriamente", true); //Se muestra un di�logo avisando de que se termin� de servir la mezcla
						//Actualiza la monitorizaci�n de niveles de cada recipiente
						niveles = Mezcla.actualizarNiveles(cantidades, niveles);
						gestorPreferencias.editarNiveles(niveles);
					}
					else if (datos_string.equalsIgnoreCase("n")) { //Si se mand� una 'n' quiere decir que no hay ning�n vaso a la distancia adecuada
						mostrarDialogo("No se puede servir la mezcla", "No hay ningún recipiente colocado a la distancia adecuada", false); //Se muestra un di�logo avisando de que debe colocarse un vaso
					}

				}
			}			
		}
	}
}
