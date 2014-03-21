package dse.coctelera;

import android.text.Editable;
import android.text.TextWatcher;

//TextWatcher (vigilante de texto) para evitar que los campos de texto en donde se escribirán valores tengan ceros como primer dígito
public class TextWatcherSinCero implements TextWatcher {


	public TextWatcherSinCero() {}
	
	//Se ejecutará después de haberse realizado una escritura
	@Override
	public void afterTextChanged(Editable editable) {
		if ((editable.length() > 1) && (editable.charAt(0) == '0')) { //Si el primer carácter escrito es un 0 y se escribieron varios números
			editable.replace(0, 1, ""); //Borra el 0 inicial
		}
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {}
}
