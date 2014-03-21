package dse.coctelera;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

//Debe implementar la interfaz serializable para ser pasado de una actividad a otra mediante objetos Intent
public class Mezcla implements Serializable {

	/**
	 * Generado al implementar la interfaz Serializable
	 */
	private static final long serialVersionUID = 4368087493393562851L;

	private String nombre; //Nombre de la mezcla
	private int[] porcentajes; //Array con los porcentajes de cada l�quido respecto a la "capacidad del recipiente" (cantidad de mezcla)
	private int[] cantidades; //Array con la cantidad de cada l�quido
	private int capacidad; //Tama�o del contenedor donde se servir� la mezcla
	
	public Mezcla(String nombre, int[] porcentajes, int cantidad_total) {
		this.nombre = nombre;
		this.porcentajes = porcentajes;
		this.cantidades = calcularCantidades(cantidad_total, porcentajes); //Cantidades calculadas en base a los porcentajes
		this.capacidad = cantidad_total;
	}
	
	public Mezcla(String nombre, int[] porcentajes, int[] cantidades, int cantidad_total) {
		this.nombre = nombre;
		this.porcentajes = porcentajes;
		this.cantidades = cantidades;
		this.capacidad = cantidad_total;
	}
	
	public Mezcla(String nombre, int cantidad0, int cantidad1, int cantidad2, int cantidad3, int cantidad_total) {
		this.nombre = nombre;
		this.cantidades = new int[] {cantidad0, cantidad1, cantidad2, cantidad3};
		this.capacidad = cantidad_total;
	}

	//Devuelve true si la suma de cantidades es igual o menor a la capacidad del vaso (cabe en el recipiente)
	public static boolean cabeEnRecipienteCantidades(int[] cantidades, int cantidad_total) {
		
		return ((cantidades[0] + cantidades[1] + cantidades[2] + cantidades[3]) <= cantidad_total);
	}
	
	//Devuelve true si la suma de porcentajes es igual o menor a 100% (cabe en el recipiente)
	public static boolean cabeEnRecipientePorcentajes(int[] porcentajes, int cantidad_total) {		
		return ((porcentajes[0] + porcentajes[1] + porcentajes[2] + porcentajes[3]) <= 100);
	}
	
	//Devuelve true si la mezcla puede servirse, es decir, hay suficiente nivel de cada l�quido como para expulsar cada cantidad
	public static boolean puedeServirse(int[] cantidades, int[] niveles) {
		boolean puede = true;
		int i = 0;
		while((i < cantidades.length) && (puede)) {
			puede = (puede && (niveles[i] >= cantidades[i])); //Puede servirse si antes se pod�a y el nivel en el recipiente siguiente es mayor o igual a la cantidad a servir
			i++;
		}
		
		return puede;
	}
	
	//Devuelve un array de enteros con los nuevos niveles despu�s de haber restado las cantidades
	public static int[] actualizarNiveles(int[] cantidades, int[] niveles) {
		int[] nuevosNiveles = new int[cantidades.length];
		for(int i = 0; i < cantidades.length; i++) {
			nuevosNiveles[i] = niveles[i] - cantidades[i];
		}
		
		return nuevosNiveles;
	}
	
	//Devuelve true si la suma de las cantidades de cada l�quido cabe en el envase en el que se supone que se servir� el l�quido
	public boolean esCantidadCorrecta() {
		int suma = 0;
		for(int i = 0; i < cantidades.length; i++) {
			suma += cantidades[i];
		}
		
		return suma <= capacidad;
	}
	
	//Devuelve una �nica cantidad calcul�ndola en base al procentaje de la capacidad
	public static int calcularCantidad(int capacidad, int porcentaje) {
		double cantidad_d; //Cantidad almacenada en float para no perder precisi�n
		cantidad_d = (double) porcentaje * (double) capacidad / 100.0; //Se calcula el porcentaje
		return (int) Math.round(cantidad_d); //Se devuelve la cantidad haciendo casting a int
	}
	
	//Devuelve un �nico porcentaje calcul�ndolo en base a la fracci�n de la capacidad que representa la cantidad
	public static int calcularPorcentaje(int capacidad, int cantidad) {
		double porcentaje_d; //Porcentaje almacenado en float para no perder precisi�n
		porcentaje_d = (double) cantidad / (double) capacidad * 100.0; //Se calcula el porcentaje
		return (int) Math.round(porcentaje_d); //Se devuelve el porcentaje haciendo casting a int
	}
	
	//Devuelve un array de cantidades calcul�ndolas a partir del porcentaje de cada cantidad respecto de la capacidad
	public static int[] calcularCantidades(int capacidad, int[] porcentajes) {
		double cantidad_d; //Cantidad almacenada en coma flotante para no perder precisi�n
		int[] cantidades_aux = new int[porcentajes.length];
		for(int i = 0; i < porcentajes.length; i++) {
			cantidad_d = (double) porcentajes[i] * (double) capacidad / 100.0; //Se calcula el procentaje
			cantidades_aux[i] = (int) Math.round(cantidad_d); //Se guarda la cantidad haciendo casting a entero		
		}
		
		return cantidades_aux;
	}
	
	//Devuelve una lista con las representaciones String de una lista de mezclas
	public static List<String> devolverListaMezclas(List<Mezcla> lista_mezclas) {
		List<String> lista_cadenas = new ArrayList<String>();
		for (Mezcla m : lista_mezclas) {
			lista_cadenas.add(m.toString());
		}
		
		return lista_cadenas;
	}

	@Override
	public String toString() {
		return nombre;
	}
	
//Getters y setters
	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public int[] getCantidades() {
		return cantidades;
	}

	public void setCantidades(int[] cantidades) {
		this.cantidades = cantidades;
	}

	public int getCapacidad() {
		return capacidad;
	}

	public void setCapacidad(int cantidad_total) {
		this.capacidad = cantidad_total;
	}
	
	public int[] getPorcentajes() {
		return porcentajes;
	}

	public void setPorcentajes(int[] porcentajes) {
		this.porcentajes = porcentajes;
	}
}
