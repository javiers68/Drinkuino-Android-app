package dse.coctelera;

import android.content.Context;
import android.content.SharedPreferences;

//Clase para obtener datos de las preferencias y editar las mismas
public class GestorPreferencias {
	
	private Context context; //Contexto desde el que se obtienen las preferencias
	private SharedPreferences sharedPreferences; //Preferencias
	private SharedPreferences.Editor editor; //Editor de las preferencias
	
	public GestorPreferencias(Context context) {
		this.context = context;
		sharedPreferences = context.getSharedPreferences(context.getString(R.string.FILENAME_PREFERENCIAS), Context.MODE_PRIVATE); //Se obtiene (o se crea si no exist�a) el fichero de preferencias
		editor = sharedPreferences.edit(); //Obtiene el editor
	}
	
	//Devuelve el array de niveles almacenado en las preferencias
	public int[] obtenerNiveles() {
		int nivel_maximo = Integer.parseInt(context.getString(R.string.NIVEL_MAXIMO)); 
		int nivel1 = sharedPreferences.getInt(context.getString(R.string.key_nivel1), nivel_maximo);
		int nivel2 = sharedPreferences.getInt(context.getString(R.string.key_nivel2), nivel_maximo);
		int nivel3 = sharedPreferences.getInt(context.getString(R.string.key_nivel3), nivel_maximo);
		int nivel4 = sharedPreferences.getInt(context.getString(R.string.key_nivel4), nivel_maximo);
		
		return new int[] {nivel1, nivel2, nivel3, nivel4};
	}
	
	//Devuelve la direcci�n MAC del bluetooth almacenada en las preferencias, si no se encuentra devuelve la direcci�n por defecto
	public String obtenerDireccionBluetooth() {
		String direccion_bluetooth = sharedPreferences.getString(context.getString(R.string.key_direccionBluetooth), context.getString(R.string.DIRECCION_BLUETOOTH_DEFECTO));
	
		return direccion_bluetooth;
	}
	
	//Devuelve el estado de la conexi�n bluetooth (true si est� conectado, false si no lo est�)
	public boolean obtenerEstadoConexion() {
		boolean estado = sharedPreferences.getBoolean(context.getString(R.string.key_estadoConexion), false);
		
		return estado;
	}
	
	//Almacena los nuevos niveles en las preferencias
	public void editarNiveles(int[] niveles) { 
		editor.putInt(context.getString(R.string.key_nivel1), niveles[0]);
		editor.putInt(context.getString(R.string.key_nivel2), niveles[1]);
		editor.putInt(context.getString(R.string.key_nivel3), niveles[2]);
		editor.putInt(context.getString(R.string.key_nivel4), niveles[3]);
		editor.commit();
	}
	
	//Almacena la nueva direcci�n en las preferencias
	public void editarDireccionBluetooth(String direccion) { 
		editor.putString(context.getString(R.string.key_direccionBluetooth), direccion);
		editor.commit();
	}
	
	//Almacena el nuevo estado de conexi�n con el bluetooth
	public void editarEstadoConexion(boolean estado) { 
		editor.putBoolean(context.getString(R.string.key_estadoConexion), estado);
		editor.commit();
	}
}
