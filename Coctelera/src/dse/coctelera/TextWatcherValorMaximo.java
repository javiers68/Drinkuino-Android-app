package dse.coctelera;

import android.text.Editable;
import android.text.TextWatcher;

//TextWatcher (vigilante de texto) para evitar que los campos de texto en donde se escribir�n valores superen un valor maximo (por ejemplo 100 para porcentajes)
public class TextWatcherValorMaximo implements TextWatcher {

	private int valorMaximo;

	public TextWatcherValorMaximo(int valorMaximo) {
		this.valorMaximo = valorMaximo;
	}
	
	public TextWatcherValorMaximo(TextWatcherValorMaximo textWatcher) {
		this.valorMaximo = textWatcher.valorMaximo;
	}
	
	//Se ejecutar� despu�s de haberse realizado una escritura
	@Override
	public void afterTextChanged(Editable editable) {
		try {
			int numero = Integer.parseInt(editable.toString()); //Se obtiene el n�mero escrito
//			if (editable.charAt(0) == '0' && (editable.length() > 1)) { //Si el primer car�cter escrito es un 0 y se escribieron varios n�meros
//				editable.replace(0, 1, ""); //Borra el 0 inicial
//			}
			if (numero > valorMaximo) { //Si el n�mero introducido supera 100
				editable.replace(0, editable.length(), String.valueOf(valorMaximo)); //Reemplaza el texto con el valor m�ximo
			}
		} catch (NumberFormatException e) {}  //En caso de borrar todo el texto se obvia la excepci�n
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {}
	
	//Sobreescritura de m�todos equals y hashCode para poder borrar los filtros de la lista de filtros de un editText
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + valorMaximo;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TextWatcherValorMaximo other = (TextWatcherValorMaximo) obj;
		if (valorMaximo != other.valorMaximo)
			return false;
		return true;
	}
}
