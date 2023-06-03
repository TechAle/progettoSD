# Protocollo TCP
## Sintassi
Questo protocollo prende tanta ispirazione da [redis](https://redis.io/docs/about/)<br>
- \+ rappresenta una risposta avvenuta con successo
- \- rappresenta un errore
- : precede sempre un intero
- $ precede sempre una stringa
- [ precede sempre un insieme di valori correlati tra di loro, e questa correlazione termina col carattere ] 
## Richieste del client
Tutte le richieste del client devono essere una lista di commandi.
I possibili commandi che il server accetta sono:
- VIEW<br>Ritorna le seguenti informazioni di tutte le proiezioni: 
  - Id della proiezione (intero)
  - Nome della proiezione (stringa)
  - Numero di posti totali (intero)
  - Lista di posti occupati (Lista)<br>
    che è formato:
    - L'id della prenotazione (intero)
    - Il numero del posto (intero)
- VIEW:idProiezione<br>
  dato un numero che rappresenta l'id della proiezione, ritorna tutte le informazione di quella.<br>
  Sinteticamente parlando è la VIEW che ritorna 1 sola proiezione
- ADD:idProiezione[:posto:posto:...]<br>
  Dato l'id di una proiezione ed una lista di posti, i posti verranno prenotati e viene ritornato un messaggio che contiene l'id della prenotazione.
- ADD:idProiezione:idPrenotazione[:posto:posto:...]
  Dato l'id di una proiezione e l'id della prenotazione ed una lista di posti, verranno prenotati i posti della proiezione con l'id della prenotazione usato come parametro.
- DELETE:idProiezione:idPrenotazione
  Dato l'id di una proiezione e l'id di una prenotazione, verranno eliminati tutti i posti prenotati in quella particolare proiezione.
- DELETE:idProiezione:idPrenotazione[:posto:posto:...]
  Dato l'id di una proiezione, l'id di una prenotazione ed una lista di posti, verranno eliminati tutti i posti dentro la lista che sono nella proiezione.
## Risposte del server
Tutte le risposte del server iniziano con:
- \+ se l'operazione è avvenuta con successo
- \- se ci sono stati dei problemi con l'esecuzione dell'operazione

In generale le risposte del server sono seguite da un messaggio, es: +$messaggio<br>
L'unica eccezione è la risposta della seguente operazione: ADD:idProiezione[:posto:posto:...] che ha come risposta +:idPrenotazione$messaggio