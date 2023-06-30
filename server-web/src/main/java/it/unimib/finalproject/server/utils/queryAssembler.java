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
		return "[VIEW]";
	}


	/**
	 * VIEW di un determinato film.
	 * @return query [VIEW:idProiezione]
	 */
	public static String generateView(int idProiezione){
		String query = "[VIEW:";
		query += idProiezione + "]";
		return query;
	}

	/**
	 * ADD di posti prenotati ad una proiezione.
	 * @param idProiezione id della proiezione
	 * @param posti stringa corrispondente ad una lista di posti
	 * @return query [ADD:idProiezione[:posto1:posto2:...]
	 */
	public static String generateAdd(int idProiezione, String posti){

		//	Controllo che posti corrisponda ad una lista vuota:
		//	in tal caso ritorno una stringa vuota per l'errore.
		if(posti.length() == 2)
			return "";

		StringBuilder query = new StringBuilder("[ADD:");
		//	Aggiunta id.
		query.append(idProiezione).append("[");

		//	Tolgo parentesi quadre dalla lista dei posti.
		posti = posti.substring(1, posti.length() - 1);
		//	Aggiungo ciascun posto nella stringa.
		String[] splittedPosti = posti.split(",");
		for(int i = 0; i < splittedPosti.length; i++) {
			query.append(":").append(splittedPosti[i]);
		}

		//	Chiudo la stringa.
		query.append("]]");
		return query.toString();

	}


	/**
	 * ADD di posti associati ad una specifica prenotazione per una proiezione.
	 * @param idProiezione id della proiezione
	 * @param idPrenotazione id della prenotazione
	 * @param posti stringa corrispondente ad una lista di posti
	 * @return query [ADD:idProiezione:idPrenotazione[:posto1:posto2:...]
	 */
	public static String generateAdd(int idProiezione, int idPrenotazione, String posti){

		//	Controllo che posti corrisponda ad una lista vuota:
		//	in tal caso ritorno una stringa vuota per l'errore.
		if(posti.length() == 2)
			return "";

		StringBuilder query = new StringBuilder("[ADD:");
		//	Aggiunta id.
		query.append(idProiezione).append(":");
		query.append(idPrenotazione).append("[");

		//	Tolgo parentesi quadre dalla lista dei posti.
		posti = posti.substring(1, posti.length() - 1);
		//	Aggiungo ciascun posto nella stringa.
		String[] splittedPosti = posti.split(", ");
		for(int i = 0; i < splittedPosti.length; i++) {
			query.append(":").append(splittedPosti[i]);
		}

		//	Chiudo la stringa.
		query.append("]]");
		return query.toString();

	}


	/**
	 * DELETE senza la lista dei posti.
	 * @param idProiezione id della proiezione
	 * @param idPrenotazione id della prenotazione
	 * @return query [DELETE:idProiezione:idPrenotazione]
	 */
	public static String generateDelete(int idProiezione, int idPrenotazione) {
		//	Prima parte della query.
		String query = "[DEL:";
		//	Aggiunta id.
		query += idProiezione + ":";
		query += idPrenotazione +"]";
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
		StringBuilder query = new StringBuilder("[DEL:");
		//	Aggiunta id.
		query.append(idProiezione).append(":");
		query.append(idPrenotazione).append("[");

		//	Controllo che posti corrisponda ad una lista vuota:
		//	in caso affermativo aggiungo una lista vuota alla query.
		if(posti.length() == 2)
			return query.append("]]").toString();

		//	Tolgo parentesi quadre dalla lista dei posti.
		posti = posti.substring(1, posti.length() - 1);
		//	Aggiungo ciascun posto nella stringa.
		String[] splittedPosti = posti.split(", ");
		for(int i = 0; i < splittedPosti.length; i++) {
			query.append(":").append(splittedPosti[i]);
		}

		//	Chiudo la stringa.
		query.append("]]");
		return query.toString();
	}


}