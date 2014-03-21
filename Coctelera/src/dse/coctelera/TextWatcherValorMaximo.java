package dse.coctelera;

import android.text.Editable;
import android.text.TextWatcher;

//TextWatcher (vigilante de texto) para evitar que los campos de texto en donde se escribirán valores superen un valor maximo (por ejemplo 100 para porcentajes)
public class TextWatcherValorMaximo implements TextWatcher {

	private int valorMaximo;

	public TextWatcherValorMaximo(int valorMaximo) {
		this.valorMaximo = valorMaximo;
	}
	
	public TextWatcherValorMaximo(TextWatcherValorMaximo textWatcher) {
		this.valorMaximo = textWatcher.valorMaximo;
	}
	
	//Se ejecutará después de haberse realizado una escritura
	@Override
	public void afterTextChanged(Editable editable) {
		try {
			int numero = Integer.parseInt(editable.toString()); //Se obtiene el número escrito
//			if (editable.charAt(0) == '0' && (editable.length() > 1)) { //Si el primer carácter escrito es un 0 y se escribieron varios números
//				editable.replace(0, 1, ""); //Borra el 0 inicial
//			}
			if (numero > valorMaximo) { //Si el número introducido supera 100
				editable.replace(0, editable.length(), String.valueOf(valorMaximo)); //Reemplaza el texto con el valor máximo
			}
		} catch (NumberFormatException e) {}  //En caso de borrar todo el texto se obvia la excepción
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {}
	
	//Sobreescritura de métodos equals y hashCode para poder borrar los filtros de la lista de filtros de un editText
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
