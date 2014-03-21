package dse.coctelera;

import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

public class MisMezclas extends ListActivity {

	private List<Mezcla> lista_mezclas; //La lista de mezclas
	private ListView listView = null;
	private ListAdapter listAdapter = null;	
	
	//C�digos para identificar el tipo de actividad creada desde esta actividad
	private int CODIGO_NUEVA = 0; //C�digo para la actividad de creaci�n de una nueva mezcla
	private int CODIGO_DETALLES = 1; //C�digo para la actividad de mostrar detalles de una mezcla
	private int CODIGO_PREFERENCIAS = 2; //C�digo para la actividad de mostrar preferencias
	
	public final static String EXTRA_MEZCLA_LISTADA = "dese.coctelera.MEZCLA_LISTADA";
	public final static String EXTRA_POSICION_MEZCLA_LISTADA = "dese.coctelera.POSICION_MEZCLA_LISTADA";
	
	//Lee las mezclas guardadas de otras sesiones en el fichero de mezclas y las devuelve como lista
	private List<Mezcla> leerFicheroMezclas() {
		FileInputStream fileStream;
		ObjectInputStream objectStream;
		List<Mezcla> lista_mezclas = new ArrayList<Mezcla>();
		
		try {
			fileStream = openFileInput(getString(R.string.FILENAME_MEZCLAS)); //Se abre el fichero donde se guardan las mezclas que se encuentra en el directorio de fichero de la aplicaci�n
			objectStream = new ObjectInputStream(fileStream); //Para leer objetos del fichero
			
		} catch (IOException e) { //Si hay alg�n problema de entrada/salida (por ejemplo el fichero no existe devuelve una lista vac�a)
			return lista_mezclas;
		}
		
		try {
			while (true) { // Se leen todos los objetos del fichero
				Mezcla mezcla_aux = (Mezcla) objectStream.readObject(); // Se obtiene la mezcla del fichero
				lista_mezclas.add(mezcla_aux); // Se a�ade a la lista
			}
		} catch (EOFException e) { // Cuando salte la excepci�n de fin de fichero
			try {
				objectStream.close(); // Se cierra el fichero
				return lista_mezclas; // Devuelve la lista con las mezclas que ley� del fichero incluidas
			} catch (IOException e1) {
				return lista_mezclas;
			}
		} catch (ClassNotFoundException ex) {
			return lista_mezclas;
		} catch (IOException ex) {
			return lista_mezclas;
		}
	}
	
	//Escribe las mezclas guardadas en la lista en un fichero
	private void escribirFicheroMezclas() {
		FileOutputStream fileStream;
		ObjectOutputStream objectStream;
		try { 
	        fileStream = openFileOutput(getString(R.string.FILENAME_MEZCLAS), Context.MODE_PRIVATE); //Crea el fichero de mezclas en el directorio de fichero de la aplicaci�n 
	        objectStream = new ObjectOutputStream(fileStream); 
	    	for (Mezcla mezcla_aux : lista_mezclas) { //Recorre la lista escribiendo los objetos en el fichero
	    		objectStream.writeObject(mezcla_aux);
	    	}
	        objectStream.close(); //Cierra el fichero
		} catch (IOException ex) { 
	        System.err.println(ex); 
	    } 
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mis_mezclas);
		
		lista_mezclas = leerFicheroMezclas(); //Obtiene la lista del fichero, si no exist�a tal fichero la lista estar� vac�a
		listView = (ListView) findViewById(android.R.id.list); //Se obtiene el visor de listas
		
		listAdapter = new ArrayAdapter<Mezcla>(this, android.R.layout.simple_list_item_1, lista_mezclas); //Se crea un adaptador simple para la lista de mezclas		
		listView.setAdapter(listAdapter); //Se aplica el adaptador al visor
	}
	
	@Override
	protected void onStop() { //Se ejecutar� cada vez que la actividad pase a ser invisible
		super.onStop();
		
		escribirFicheroMezclas(); //Actualiza el fichero de mezclas
	}
	
	//Se ejecutar� al pulsar el boton "+" para a�adir una mezcla
	public void mostrarNuevaMezcla(View view) {
		Intent intent = new Intent(this, NuevaMezcla.class); //Se crea un "intent" con la actividad NuevaMezcla
		startActivityForResult(intent, CODIGO_NUEVA); //Se muestra la actividad para crear una nueva mezcla y se esperan sus datos
	}
	
	//Se ejecutar� al pulsar el bot�n de configuraciones (esquina superior izquierda)
	public void mostrarPreferencias(View view) {
		Intent intent = new Intent(this, Preferencias.class); //Se crea un "intent" con la actividad NuevaMezcla
		startActivityForResult(intent, CODIGO_PREFERENCIAS); //Se muestra la actividad para crear una nueva mezcla y se esperan sus datos
	}	
	
	//Funci�n para leer el resultado de una actividad creada desde �sta 
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent datos) {
		//super.onActivityResult(requestCode, resultCode, datos);
		if (resultCode == RESULT_OK) { // Si la actividad ha producido resultados						
			if (requestCode == CODIGO_NUEVA) { //Si se trataba de la actividad para crear una nueva mezcla
				Mezcla nueva_mezcla;
				nueva_mezcla = (Mezcla) datos.getExtras().get(NuevaMezcla.EXTRA_MEZCLA_NUEVA); // Se obtiene la mezcla creada en la actividad							
				lista_mezclas.add(nueva_mezcla); // Se a�ade a la lista de mezclas
//				((ArrayAdapter<Mezcla>) lv.getAdapter()).notifyDataSetChanged(); //Se notifica que la lista ha cambiado, debe refrescarse la vista
			}
			else if (requestCode == CODIGO_DETALLES) { //Si se trataba de la actividad para mostrar detalles de una mezcla
				int posicion_aBorrar = datos.getExtras().getInt(DetallesMezcla.EXTRA_POSICION_MEZCLA_BORRADA); //Se obtiene la posici�n de la mezcla que se estaba mostrando, la cual se ha decidido borrar
				lista_mezclas.remove(posicion_aBorrar); //Se borra de la lista de mezclas
//				((ArrayAdapter<Mezcla>) lv.getAdapter()).notifyDataSetChanged(); //Se notifica que la lista ha cambiado, debe refrescarse la vista
			}
			((ArrayAdapter<Mezcla>) listView.getAdapter()).notifyDataSetChanged(); //Se notifica que la lista ha cambiado, debe refrescarse la vista
			//escribirFicheroMezclas(); //Actualiza el fichero de mezclas
		}
	}
	
	//Se ejecutar� al pulsar sobre un elemento de la lista
	@Override
	protected void onListItemClick(ListView l, View v, int posicion, long id) {
		 //super.onListItemClick(l, v, position, id);
		Intent intent = new Intent(this, DetallesMezcla.class);
		intent.putExtra(EXTRA_MEZCLA_LISTADA, lista_mezclas.get(posicion)); //Comunica la mezcla que se encuentra en la posici�n seleccionada
		intent.putExtra(EXTRA_POSICION_MEZCLA_LISTADA, posicion); //Tambi�n comunica la posici�n que ocupa en la lista de mezclas
		startActivityForResult(intent, CODIGO_DETALLES); //Se muestra la actividad para mostrar los detalles de la mezcla y se espera alg�n resultado
	}	

}
