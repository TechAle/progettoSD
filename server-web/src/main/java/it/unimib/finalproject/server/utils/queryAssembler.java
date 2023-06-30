package it.unimib.finalproject.server.utils;

/**
 * 
 * @author Martina Elli
 * @since 25/06/23
 *
 */
public class queryAssembler {

	/**
	 * VIEW di tutti i film disponibili.
	 * @return query [VIEW]
	 */
	public static String generateView(){
		String query = "[VIEW]";
		return query;
	}


	/**
	 * VIEW di un determinato film.
	 * @return query [VIEW:idProiezione]
	 */
	public static String generateView(int idProiezione){
		String query = "[VIEW:";
		query += Integer.toString(idProiezione) + "]";
		return query;
	}

	/**
	 * ADD di posti prenotati ad una proiezione.
	 * @param idProiezione id della proiezione
	 * @param posti stringa corrispondente ad una lista di posti
	 * @return query [ADD:idProiezione[:posto1:posto2:...]
	 */
	public static String generateAdd(int idProiezione, String posti){
		//	Prima parte della query.
		String query = "[ADD:";
		//	Aggiunta id.
		query += Integer.toString(idProiezione) + ":[";

		//	Controllo che posti corrisponda ad una lista vuota:
		//	in tal caso ritorno una stringa vuota per l'errore.
		if(posti.length() == 2)
			return "";

		//	Tolgo parentesi quadre dalla lista dei posti.
		posti = posti.substring(1, posti.length() - 1);
		//	Aggiungo ciascun posto nella stringa.
		for(String s : posti.split(", "))
			query += s + ":";

		//	Chiudo la stringa.
		query += "]]";
		return query;

	}


	/**
	 * ADD di posti associati ad una specifica prenotazione per una proiezione.
	 * @param idProiezione id della proiezione
	 * @param idPrenotazione id della prenotazione
	 * @param posti stringa corrispondente ad una lista di posti
	 * @return query [ADD:idProiezione:idPrenotazione[:posto1:posto2:...]
	 */
	public static String generateAdd(int idProiezione, int idPrenotazione, String posti){
		//	Prima parte della query.
		String query = "[ADD:";
		//	Aggiunta id.
		query += Integer.toString(idProiezione) + ":";
		query += Integer.toString(idPrenotazione) + ":[";

		//	Controllo che posti corrisponda ad una lista vuota:
		//	in tal caso ritorno una stringa vuota per l'errore.
		if(posti.length() == 2)
			return query += "";

		//	Tolgo parentesi quadre dalla lista dei posti.
		posti = posti.substring(1, posti.length() - 1);
		//	Aggiungo ciascun posto nella stringa.
		for(String s : posti.split(", "))
			query += s + ":";

		//	Chiudo la stringa.
		query += "]]";
		return query;

	}


	/**
	 * DELETE senza la lista dei posti.
	 * @param idProiezione id della proiezione
	 * @param idPrenotazione id della prenotazione
	 * @return query [DELETE:idProiezione:idPrenotazione]
	 */
	public static String generateDelete(int idProiezione, int idPrenotazione) {
		//	Prima parte della query.
		String query = "[DELETE:";
		//	Aggiunta id.
		query += Integer.toString(idProiezione) + ":";
		query += Integer.toString(idPrenotazione) +"]";
		
		return query;
	}


	/**
	 * DELETE con la lista dei posti.
	 * @param idProiezione id della proiezione
	 * @param idPrenotazione id della prenotazione
	 * @param posti stringa equivalente alla lista dei posti
	 * @return query [DELETE:idProiezione:idPrenotazione:[:posto1:posto2:...]
	 */
	public static String generateDelete(int idProiezione, int idPrenotazione, String posti){
		//	Prima parte della query.
		String query = "[DELETE:";
		//	Aggiunta id.
		query += Integer.toString(idProiezione) + ":";
		query += Integer.toString(idPrenotazione) +":[";

		//	Controllo che posti corrisponda ad una lista vuota:
		//	in caso affermativo aggiungo una lista vuota alla query.
		if(posti.length() == 2)
			return query += "]]";

		//	Tolgo parentesi quadre dalla lista dei posti.
		posti = posti.substring(1, posti.length() - 1);
		//	Aggiungo ciascun posto nella stringa.
		for(String s : posti.split(", "))
			query += s + ":";

		//	Chiudo la stringa.
		query += "]]";
		return query;
	}


}
