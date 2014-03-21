package dse.coctelera;

import android.text.Editable;
import android.text.TextWatcher;

//TextWatcher (vigilante de texto) para evitar que los campos de texto en donde se escribir�n valores tengan ceros como primer d�gito
public class TextWatcherSinCero implements TextWatcher {


	public TextWatcherSinCero() {}
	
	//Se ejecutar� despu�s de haberse realizado una escritura
	@Override
	public void afterTextChanged(Editable editable) {
		if ((editable.length() > 1) && (editable.charAt(0) == '0')) { //Si el primer car�cter escrito es un 0 y se escribieron varios n�meros
			editable.replace(0, 1, ""); //Borra el 0 inicial
		}
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {}
}
