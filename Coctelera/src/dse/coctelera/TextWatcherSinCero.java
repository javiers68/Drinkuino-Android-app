
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
