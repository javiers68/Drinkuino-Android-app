
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
