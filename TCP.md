# Protocollo TCP
[TODO RISCRIVERE MEGLIO]
## Semantica
[TODO]
## Richieste del client
- Visualizza tutte le sale disponibili: VIEW
- Visualizza posti disponibili in una sala: VIEW:sala
- Visualizza posti disponibili di una prenotazione: VIEW[:prenotazione]
- Aggiungere una nuova prenotazione: ADD:sala[:posto]
  - :sala rappresenta un numero con l'id della sala
  - :posto rappresenta un numero con il posto da rimuovere
- Rimozione di posti: DEL:sala*N[:posto]
  - Se *N è 0 allora si rimuovono tutti i posti
- Nota: Le richieste del client sono una bulk string e non un array
## Risposte del server
- \+[Messaggio di successo]
- \-[Messaggio di errore]
- Nota: Una richiesta o è totalmente corretta oppure non verrà eseguita.
  esempio: Se stessimo cercando di rimuovere il posto 10 e posto 20, ed il posto 20 non è prenotato, allora il 10 non verrà rimosso
