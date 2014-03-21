package dse.coctelera;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

//Implementa los m�todos de OSBChangeListener para las barras de desplazamiento y OFChangeListener para los editText
public class NuevaMezcla extends Activity implements OnSeekBarChangeListener, OnFocusChangeListener {
	
	//Campos de texto en c�digo Java
	private EditText editText_nombre;
	private EditText editText_capacidadRecipiente;
	//Componentes para editar el nivel de cada l�quido, deben ser coherentes entre s�, de manera que el cambio en uno debe propagarse a los otros
	private SeekBar[] seekBars_porcentajes;
	private EditText[] editTexts_porcentajes;
	private EditText[] editTexts_cantidades;
	private TextView textView_porcentajeTotal;
	private TextView textView_cantidadTotal;
	//TextWatcher para los editTexts de cantidades, se guarda para poder ser borrado en caso de cambiar la capacidad del recipiente
	private TextWatcherValorMaximo textWatcher_cantidades; 

	
	public final static String EXTRA_MEZCLA_NUEVA = "dese.coctelera.MEZCLA_NUEVA";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.nueva_mezcla);
		
		//Se obtienen las referencias a los componentes en c�digo Java una vez creados
		editText_nombre = (EditText) findViewById(R.id.editText_nombre);
		editText_capacidadRecipiente = (EditText) findViewById(R.id.editText_capacidadRecipiente);
		seekBars_porcentajes = obtenerSeekBarsPorcentajes();
		editTexts_porcentajes = obtenerEditTextsPorcentajes();
		editTexts_cantidades = obtenerEditTextsCantidades();
		textView_porcentajeTotal = (TextView) findViewById(R.id.textView_porcentajeTotal);
		textView_cantidadTotal = (TextView) findViewById(R.id.textView_cantidadTotal);
		
		registrarTextWatcherSinCerosEditTexts();
		registrarTextWatchersValorMaximoEditTextsPorcentajes();
		textWatcher_cantidades = new TextWatcherValorMaximo(obtenerValor(editText_capacidadRecipiente));
		registrarTextWatchersValorMaximoEditTextsCantidades();
		registrarListenersEditTexts();
		registrarListenersSeekBars();
		
		actualizarTextViewsTotales(); //Se actualizan los textView que informan del total de porcentaje y cantidad
	}
	
	//Devuelve las referencias a las seekBars de porcentajes ordenadas en un array
	private SeekBar[] obtenerSeekBarsPorcentajes() {
		SeekBar[] seekBars_porcentajes = new SeekBar[4];
		seekBars_porcentajes[0] = (SeekBar) findViewById(R.id.seekBar_porcentajeL1);
		seekBars_porcentajes[1] = (SeekBar) findViewById(R.id.seekBar_porcentajeL2);
		seekBars_porcentajes[2] = (SeekBar) findViewById(R.id.seekBar_porcentajeL3);
		seekBars_porcentajes[3] = (SeekBar) findViewById(R.id.seekBar_porcentajeL4);
		
		return seekBars_porcentajes;
	}	
	
	//Devuelve las referencias a los editTexts de porcentajes ordenadas en un array
	private EditText[] obtenerEditTextsPorcentajes() {
		EditText[] editTexts_porcentajes = new EditText[4];
		editTexts_porcentajes[0] = (EditText) findViewById(R.id.editText_porcentajeL1);
		editTexts_porcentajes[1] = (EditText) findViewById(R.id.editText_porcentajeL2);
		editTexts_porcentajes[2] = (EditText) findViewById(R.id.editText_porcentajeL3);
		editTexts_porcentajes[3] = (EditText) findViewById(R.id.editText_porcentajeL4);
		
		return editTexts_porcentajes;
	}
	
	//Devuelve las referencias a los editTexts de cantidades ordenadas en un array
	private EditText[] obtenerEditTextsCantidades() {
		EditText[] editTexts_cantidades = new EditText[4];
		editTexts_cantidades[0] = (EditText) findViewById(R.id.editText_cantidadL1);
		editTexts_cantidades[1] = (EditText) findViewById(R.id.editText_cantidadL2);
		editTexts_cantidades[2] = (EditText) findViewById(R.id.editText_cantidadL3);
		editTexts_cantidades[3] = (EditText) findViewById(R.id.editText_cantidadL4);
		
		return editTexts_cantidades;
	}
	
	//Se registran los listeners de p�rdida de foco para los editTexts
	private void registrarListenersEditTexts() {
		editText_capacidadRecipiente.setOnFocusChangeListener(this);
		for (int i = 0; i < editTexts_porcentajes.length; i++) {
			editTexts_porcentajes[i].setOnFocusChangeListener(this);
			editTexts_cantidades[i].setOnFocusChangeListener(this);
		}
	}
	
	//Se registran los listeners para los editText
	private void registrarListenersSeekBars() {
		for (int i = 0; i < seekBars_porcentajes.length; i++) {
			seekBars_porcentajes[i].setOnSeekBarChangeListener(this);
		}		
	}	
	
	//Registra los textWatchers de escritura para los editText de porcentajes
	private void registrarTextWatchersValorMaximoEditTextsPorcentajes() {
		TextWatcherValorMaximo textWatcher_porcentajes = new TextWatcherValorMaximo(100); //Se crea un textWatcher que no permite escribir valores superiores a 100 (para porcentajes)
		for (int i = 0; i < editTexts_porcentajes.length; i++) { //Se a�aden ambos textWatchers a cada editText
			editTexts_porcentajes[i].addTextChangedListener(textWatcher_porcentajes);
		}		
	}
	
	//Registra un nuevo textWatcher borrando el anterior para los editText de cantidades
	private void registrarTextWatchersValorMaximoEditTextsCantidades() {
		TextWatcherValorMaximo textWatcher_cantidadesAnterior = new TextWatcherValorMaximo(textWatcher_cantidades); //Se crea una copia del textWatcher actual
		textWatcher_cantidades = new TextWatcherValorMaximo(obtenerValor(editText_capacidadRecipiente)); //Se crea un textWatcher que no permite escribir valores superiores a la actual capacidad escrita
		for (int i = 0; i < editTexts_cantidades.length; i++) {
			editTexts_cantidades[i].removeTextChangedListener(textWatcher_cantidadesAnterior); //Borra el textWatcher anterior
			editTexts_cantidades[i].addTextChangedListener(textWatcher_cantidades); //Se a�ade el nuevo textWatcher
		}
	}
	
	//Registra un textWatcher que impide escribir ceros al inicio en todos los editTexts que implican n�meros
	private void registrarTextWatcherSinCerosEditTexts() {
		TextWatcherSinCero textWatcher_sinCeros = new TextWatcherSinCero(); //Se crea un textWatcher que no permite escribir ceros como n�mero inicial
		editText_capacidadRecipiente.addTextChangedListener(textWatcher_sinCeros); //Se a�ade el textWatcher al editText para escribir la capacidad del recipiente
		for (int i = 0; i < editTexts_porcentajes.length; i++) { //Se a�aden ambos textWatchers a cada editText
			editTexts_porcentajes[i].addTextChangedListener(textWatcher_sinCeros);
			editTexts_cantidades[i].addTextChangedListener(textWatcher_sinCeros);
		}	
	}
	
	//Actualiza el textView con la suma de los valores y coloreando el texto en funci�n de si supera o no el valor m�ximo
	private void actualizarTextView(TextView textView, int[] valores, int valorMaximo) {
		int sumatorio = 0;
		for(int i = 0; i < valores.length; i++) {
			sumatorio += valores[i];
		}
		if (sumatorio <= valorMaximo) { //Si no supera el valor m�ximo lo colorea de verde
			textView.setTextColor(Color.GREEN);
		}
		else { //Si los supera de rojo
			textView.setTextColor(Color.RED);
		}
		textView.setText(String.valueOf(sumatorio)); //Escribe el texto
	}
	
	//Se actualizan los textView que informan del total de porcentaje y cantidad
	private void actualizarTextViewsTotales() {
		actualizarTextView(textView_porcentajeTotal, obtenerPorcentajes(), 100);
		actualizarTextView(textView_cantidadTotal, obtenerCantidades(), obtenerValor(editText_capacidadRecipiente));
	}
	
	//Se ejecuta al pulsar el bot�n de "Atr�s": vuelve a la actividad desde la que se llam� sin producir ning�n resultado
	public void volverAtras(View view) {
		setResult(RESULT_CANCELED); //Indica que no ha producido resultados (no tiene mezcla que comunicar para ser a�adida)
		finish(); //Finaliza la actividad
	}
	
	//Devuelve true si el nombre es distinto de la cadena vac�a y de null
	private boolean esNombreValido(String nombre) {
		return ((!nombre.equalsIgnoreCase("")) && (nombre != null));
	}
	
	//Devuelve el array de porcentajes establecidos en las seekBar
	private int[] obtenerPorcentajes() {
		int[] porcentajes = new int[seekBars_porcentajes.length];
		for (int i = 0; i < seekBars_porcentajes.length; i++) {
			porcentajes[i] = seekBars_porcentajes[i].getProgress();
		}
		
		return porcentajes;
	}
	
	//Devuelve el array de cantidades establecidas en los editText
	private int[] obtenerCantidades() {
		int[] cantidades = new int[editTexts_cantidades.length];
		for (int i = 0; i < editTexts_cantidades.length; i++) {
			cantidades[i] = obtenerValor(editTexts_cantidades[i]);
		}
		
		return cantidades;
	}	
	
	//Devuelve el valor escrito en un editText, 0 por defecto si no hay nada escrito
	private int obtenerValor(EditText editText) {
		int valor;
		try {
			valor = Integer.parseInt(editText.getText().toString());
		} catch (NumberFormatException e) { //En caso de que el campo est� vac�o el valor se tomar� como 0
			valor = 0;
		}
		
		return valor;
	}
	
	//Se ejecuta al pulsar el bot�n de "Aceptar": intenta crear la nueva mezcla
	public void crearMezcla(View view) {
		Mezcla mezcla;		
		String nombre = editText_nombre.getText().toString();
		int capacidadRecipiente = obtenerValor(editText_capacidadRecipiente);
		int[] porcentajes = obtenerPorcentajes();
		int[] cantidades = obtenerCantidades();
		
		if (esNombreValido(nombre)) { //El nombre debe ser v�lido, de lo contrario se muestra un aviso
			//Si la mezla cabe en el recipiente indicado se contruye en efecto la mezcla
			if (Mezcla.cabeEnRecipienteCantidades(cantidades, capacidadRecipiente)) {
				Intent datos = new Intent(); //Creamos un nuevo Intent
				mezcla = new Mezcla(nombre, porcentajes, cantidades, capacidadRecipiente);
				datos.putExtra(EXTRA_MEZCLA_NUEVA, mezcla); //A�ade el valor "mezcla" a la clave EXTRA_MEZCLA
				setResult(RESULT_OK, datos); //Actualiza el resultado que esta actividad devolver� a la actividad principal (comunica la mezcla)
				finish(); //Finaliza esta actividad volviendo a la principal
			} else { //Si los datos introducidos son inv�lidos
				mostrarDialogo("La mezcla no puede crearse", "La cantidad de los l�quidos excede la capacidad del recipiente");
			}
		}
		else {
			mostrarDialogo("La mezcla no puede crearse", "El campo de nombre est� vac�o");
		}
	}
	
	//Muestra un di�logo simple (con bot�n aceptar) con el t�tulo y mensaje indicados
	private void mostrarDialogo(String titulo, String mensaje) {
		//Se construye y muestra un di�logo informando del problema
		AlertDialog.Builder constructor_dialogo = new AlertDialog.Builder(this);
		constructor_dialogo.setTitle(titulo);
		constructor_dialogo.setMessage(mensaje);
		constructor_dialogo.setIcon(R.drawable.ic_error);
		constructor_dialogo.setPositiveButton("Aceptar", null);
		constructor_dialogo.create();
		constructor_dialogo.show();
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
	
	//Actualiza los editText que corresponden al porcentaje y la cantidad de un l�quido si la barra para controlarlo ha cambiado
	private void actualizarDesdeSeekBar(SeekBar seekBar) {
		int porcentaje = seekBar.getProgress(); //Se obtiene el porcentaje de la barra de desplazamiento
		int capacidad = obtenerValor(editText_capacidadRecipiente); //Obtiene la capacidad escrita en el campo
		int cantidad = Mezcla.calcularCantidad(capacidad, porcentaje);
		int posicion_vista = buscarVista(seekBar, seekBars_porcentajes); //En funci�n de la seekBar sobre la que se est� realizando el cambio se actualizan los editText correspondientes
		if (posicion_vista > -1) {
			editTexts_porcentajes[posicion_vista].setText(String.valueOf(porcentaje));
			editTexts_cantidades[posicion_vista].setText(String.valueOf(cantidad));
		}
	}
	
	//Actualiza uno de los editText de cantidad y la seekBar que marca el porcentaje
	private void actualizarDesdeEditTextPorcentaje(EditText editText) {		
		int porcentaje = obtenerValor(editText); //Obtiene el porcentaje escrito
		int capacidad = obtenerValor(editText_capacidadRecipiente); //Obtiene la capacidad escrita en el campo
		int cantidad = Mezcla.calcularCantidad(capacidad, porcentaje);
		int posicion_vista = buscarVista(editText, editTexts_porcentajes); //En funci�n del editText sobre la que se est� realizando el cambio se actualizan los otros dos elementos relativos
		if (posicion_vista > -1) {
			seekBars_porcentajes[posicion_vista].setProgress(porcentaje);
			editTexts_cantidades[posicion_vista].setText(String.valueOf(cantidad));
		}
	}
	
	//Actualiza uno de los editText de porcentaje y la seekBar que marca el porcentaje
	private void actualizarDesdeEditTextCantidad(EditText editText) {		
		int cantidad = obtenerValor(editText); //Obtiene la cantidad escrita
		int capacidad = obtenerValor(editText_capacidadRecipiente); //Obtiene la capacidad escrita en el campo
		int porcentaje = Mezcla.calcularPorcentaje(capacidad, cantidad); //Se calcula el porcentaje
		int posicion_vista = buscarVista(editText, editTexts_cantidades); //En funci�n del editText sobre la que se est� realizando el cambio se actualizan los otros dos elementos relativos
		if (posicion_vista > -1) {
			seekBars_porcentajes[posicion_vista].setProgress(porcentaje);
			editTexts_porcentajes[posicion_vista].setText(String.valueOf(porcentaje));
		}
	}	

/*M�todos sobreescritos de las interfaces que implementa*/
	
	/*Interfaz OnSeekBarChangeListener*/
	//Cuando el progreso de la barra cambia se actualizan las etiquetas que informan de los porcentajes de cada l�quido
	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		if (fromUser) { //Solo si el cambio est� realizado por el usuario
			actualizarDesdeSeekBar(seekBar);
			actualizarTextViewsTotales();
		}
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		actualizarDesdeSeekBar(seekBar);
		actualizarTextViewsTotales();
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		actualizarDesdeSeekBar(seekBar);
		actualizarTextViewsTotales();
	}

	/*Interfaz OnFocusChangeListener*/
	@Override
	public void onFocusChange(View view, boolean hasFocus) {
		EditText editText = (EditText) view; //Se obtiene el editText sobre el que se cambia el foco
		int valor = obtenerValor(editText); //Se obtiene el valor escrito
		if (!hasFocus) { //Cuando se pierde el foco
			if (valor == 0) { //Si el valor obtenido es 0 volvemos a escribir un 0 (es posible que se borrara todo el texto)
				editText.setText(String.valueOf(valor));
			}
			
			if(buscarVista(editText, editTexts_porcentajes) > -1) { //Si se trata de un editText de porcentajes
				actualizarDesdeEditTextPorcentaje(editText);
			}
			else { 
				if (buscarVista(editText, editTexts_cantidades) > -1) { //Si se trata de un editText de cantidades
					actualizarDesdeEditTextCantidad(editText);
				}
				else { //Si se trata del editText de cantidad total de la mezcla
					registrarTextWatchersValorMaximoEditTextsCantidades(); //Se registran los textWatchers para impedir que se puedan escribir valores mayores a la capacidad del recipiente
					//Actualiza todos los elementos
					for (int i = 0; i < editTexts_porcentajes.length; i++) {
						actualizarDesdeEditTextPorcentaje(editTexts_porcentajes[i]); //Se actualizan los componentes de manera adecuada a la nueva cantidad total de la mezcla
					}
				}
			}
			actualizarTextViewsTotales(); //En cualquier caso se actualizan los textView que informan del total de porcentaje y cantidad
		}	
	}	
}
