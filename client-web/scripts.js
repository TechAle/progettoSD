var API_URI = "http://localhost:8080";
var zonaFilm= document.querySelector("#locandina");
var zonaPrenotazioni = document.querySelector("#sala table");
var postiScelti = [];

class film {
    constructor(identificativo, numeroPosti){
        this._id = identificativo;
        this._posti = numeroPosti;
        this._occupati = getPrenotazioniFake(identificativo)
    }
    //si occupa di fornire i posti di una sola prenotazione
    getOccupatiById(n){
        let ris = [];
        for (let i of this._occupati){
            if(i[1] == n) ris.push(i[0]);
        }
        return ris;
    }
}

var current;                            //utilizzo per il film

//variabili di prova
var films = [
    {id: 1, nome: "Non aprite quella porta", descrizione: "...che è dell'ufficio di Antoniotti", sala: "Sala 1", orario:"10:30", posti: 250},
    {id: 2, nome: "Mine", descrizione: "Il protagonista dovrà affrontare diverse insidie.. per creare il proprio progetto di SD", sala: "Sala 2", orario:"9:45", posti: 220},
    {id: 3, nome: "Wild University", descrizione: "Documentario sulla routine quotidiana degli studenti di Informatica dell'UniMiB", sala: "a", orario:"a", posti: 125},
    {id: 4, nome: "300", descrizione: "Storia di valorosi eroi che sono siusciti a resistere al progetto di LP", sala: "a", orario:"a", posti: 300},
    {id: 5, nome: "Terminator", descrizione: "Sorrenti è venuto dal futuro e minaccia l'esistenza dell'Università! Riuscirà il Sig. Ferretti a salvarla?", sala: "a", orario:"a", posti: 160},
    {id: 6, nome: "Fuga da RSAD", descrizione:"a", sala: "a", orario:"a", posti: 280},
    {id: 7, nome: "Apocalypto", descrizione: "Una matricola lotta per la propria vita contro un gruppo di docenti di GAL. Poi arrivano i maranza...", sala: "a", orario:"a", posti: 140},
    {id: 8, nome: "The Hunger Games", descrizione: "24 pendolari universitari, di 12 facoltà diverse, rimangono intrappolati in un treno fermo sui binari, mentre si recano alla loro università preferita. Osservati dai dipendenti di Trenord, inizieranno una dura lotta per la sopravvivenza. Solo uno rimarrà vivo... forse", sala: "a", orario:"a", posti: 300},
    {id: 9, nome: "Mission Impossible - Protocollo fantasma", descrizione: "Riuscirà il nostro eroe ad uscire dalla lezione dell'Avitabile senza essere visto?", sala: "a", orario:"a", posti: 320},
    {id: 10, nome: "Un film da non vedere", descrizione: "Sperando di non sembrare troppo cattivo: invece di pensare a delle descrizioni per questo film, non sarebbe stato meglio utilizzare quel poco tempo per approfondire come si implementa in CMOS una porta And a 6 ingressi oppure quanta approssimazione c'è tra un numero in virgola mobile ed il successivo quando stiamo rappresentando valori attorno a 2425.32?", sala: "a", orario:"a", posti: 0}
  ];
var prenotazioniFake = [
    [2, 10],
    [4, 10],
    [1, 3],
    [3, 10],
    [22, 10],
    [11, 4],
    [23, 10],
    [40, 1],
    [7, 4],
    [24, 10]
] //coppie <posto, prenotazione>

//Chiamate GET
async function getProiezioni(){                                 //usata nella init e nell'aggiornamento
    let response = await fetch(`${API_URI}/proiezioni`);          
    if(!response.ok){                                             
        throw new Error (`${response.status} ${response.statusText}`)
    }
    return await response.json();                                 
}
async function getPrenotazioni(id){                             //usata nella classe film
    let response = await fetch(`${API_URI}/proiezioni/${id}`);
    if(!response.ok){
        throw new Error (`${response.status} ${response.statusText}`)
    }
    let a = await response.json();
    return a.sort(function(a, b){
        return a[0] - b[0];
    })
}
async function getPrenotazione(idProiezione, idPrenotazione){          //???                       
    let response = await fetch(`${API_URI}/proiezioni/${idProiezione}/${idPrenotazione}`);          
    if(!response.ok){                                             
        throw new Error (`${response.status} ${response.statusText}`)
    }
    return await response.json();                                 
}

//invio i posti che vorrei prenotare al server, chiamata POST
async function inviaPrenotazione(idProiezione){
    const endpoint = `${API_URI}/films/${idProiezione}`
    const response = await fetch(endpoint, {
        method: "POST",
        headers: {
            "Content-type": "application/json"
        },
        body: JSON.stringify(postiScelti)
    });
    if(!response.ok){
        if(response.status == 408){
            throw new Error("Prenotazione fallita: uno o più posti selezionati sono già occupati");
        }
        else throw new Error(`${response.status} ${response.statusText}`);
    }
    const location = response.headers.get("Location");
    postiScelti = [];
    return location.split("/").pop();
}

//chiamata PUT
async function modificaPrenotazione(idProiezione, idPrenotazione){
    const endpoint = `${API_URI}/films/${idProiezione}/${idPrenotazione}`
    const response = await fetch(endpoint, {
        method: "PUT",
        headers: {
            "Content-type": "application/json"
        },
        body: JSON.stringify(postiScelti)
    });
    if(!response.ok){
        throw new Error(`${response.status} ${response.statusText}`);
        return;
    }
}

//chiamata DELETE
async function eliminaPrenotazione(idProiezione, idPrenotazione){
    const endpoint = `${API_URI}/films/${idProiezione}/${idPrenotazione}`
    const response = await fetch(endpoint, {
        method: "DELETE"
    });
    if(!response.ok){
        throw new Error(`${response.status} ${response.statusText}`);
        return;
    }
}

//chiamate finte
function getProiezioniFake(){
    return films;
}
function getPrenotazioniFake(a){
    return prenotazioniFake;
}
function inviaPrenotazioneFake(a){
    return 15;
}
function modificaPrenotazioneFake(a, b){
    return;
}
function eliminaPrenotazioneFake(a, b){
    return;
}

function createCard(film){                                  //Gestione creazione locandine, in Proiezioni
    let elem = document.createElement("div");
    elem.className="card";
    let head = document.createElement("div");
    head.className="filmTitle";
    head.appendChild(document.createTextNode(film["nome"]));
    elem.appendChild(head);
    let body = document.createElement("div");
    body.className="illusion";
    let a = document.createElement("p");
    a.appendChild(document.createTextNode(film["descrizione"]));
    body.appendChild(a);
    a = document.createElement("p");
    a.appendChild(document.createTextNode(film["sala"]));
    body.appendChild(a);
    a = document.createElement("p");
    a.appendChild(document.createTextNode(film["orario"]));
    body.appendChild(a);
    a = document.createElement("button");
    a.appendChild(document.createTextNode("Prenota Visione"));
    a.addEventListener("click", function(){
        mostraPrenotazioni(film["posti"], film["id"]);
    });
    body.appendChild(a);
    elem.appendChild(body);
    return elem;
}

//Aggiunta di film, in Proiezioni
function addFilm(film){
    let presentazione = document.createElement("div");
    presentazione.className="item";
    presentazione.appendChild(createCard(film));
    zonaFilm.appendChild(presentazione);
}

//Aggiunta icone spettatore, indipendentemente dal film corrente
function createSomeImages(){
    let cells = document.querySelectorAll("td");
    cells.forEach(addImage);
}
function addImage(e){
    let x = document.createElementNS("http://www.w3.org/2000/svg","svg");
    x.setAttribute("width", 40);
    x.setAttribute("height", 40);
    let circ = document.createElementNS("http://www.w3.org/2000/svg","circle");
    circ.setAttribute("cx", 20);
    circ.setAttribute("cy", 14.4);
    circ.setAttribute("r", 10);
    x.appendChild(circ);
    let thumb = document.createElementNS("http://www.w3.org/2000/svg","path");
    thumb.setAttribute("d", "M 0,40 Q 0,28 12,28 L 28,28 Q 40,28 40,40 Z");
    x.appendChild(thumb);
    e.appendChild(x);
}

//compito: mostrare le prenotazioni correnti e settare il film corrente
function mostraPrenotazioni(posti, id){
    document.getElementById("proiezioni").hidden = true;            //nasconde le proiezioni
    document.getElementById("prenotazioni").hidden = false;         //Mostra i posti, da disegnare
    current = new film(id, posti);                                  //chiamata implicita al server, vedasi la dichiarazione della classe
    makeTable();
    createSomeImages();
    document.getElementById("invio").appendChild(document.createTextNode("Invia Prenotazione"));
    document.getElementById("cerca-prenotazione").addEventListener("submit",trovaPrenotazione)
    document.getElementById("invio").addEventListener("click", function(){
        if(postiScelti.length == 0){
            alert("Nessuna prenotazione inviata: non hai selezionato nessun posto");
        }
        else{
            /*try{*/
            let a = inviaPrenotazioneFake(id);
            alert("Il numero di prenotazione è: " + a);
            mostraProiezioni();
            /*} catch(error) {
                alert("Errore all'invio della prenotazione")
            }*/
        }
    })
}

//Creazione posti sala, dipendente dal film corrente
function makeTable(){
    let nPosti = current._posti;
    let o = current._occupati.toSorted(function(a, b){
        return a[0] - b[0];
    });
    if(o.length == 0) o.push([-2, -2]);         //valori impossibili da ottenere
    let nFile = Math.floor(nPosti / 20);
    let PostiRestanti = nPosti - nFile * 20;
    for(let i = 0; i<nFile; i++){
        let row = document.createElement("tr");
        for(let j = 0; j < 20; j++){
            let cell = document.createElement("td");
            if(i*20+j == o[0][0]){
                cell.className = "booked";
                o.shift();
                if(o.length == 0) o.push([-2, -2]);
            }
            else cell.className = "seat";
            cell.addEventListener("click", function(){
                modificaPosto(cell, i*20+j);
            });
            row.appendChild(cell);
        }
        zonaPrenotazioni.appendChild(row);
    }
    let last = document.createElement("tr");
    for(let j = 0; j < PostiRestanti; j++){
        let cell = document.createElement("td");
        if(nFile*20+j == o[0][0]){
            cell.className = "booked";
            o.shift();
            if(o.length == 0) o.push([-2, -2]);
        }
        else cell.className = "seat";
        cell.addEventListener("click", function(){
            modificaPosto(cell, nFile*20+j);
        });
        last.appendChild(cell);
    }
    zonaPrenotazioni.appendChild(last);
}

//Cambia la classe di un posto. Indipendente dal film corrente
function modificaPosto(posto, numero){
    if(posto.className == "selected"){
        posto.className = "seat";
        let i = postiScelti.indexOf(numero);
        if(i == -1){
            console.log("errore");
            return;
        }
        postiScelti[i] = postiScelti[postiScelti.length - 1];
        postiScelti.pop();
    }
    else if(posto.className == "seat"){
        posto.className = "selected";
        postiScelti.push(numero);
    }
    else if(posto.className == "autobook"){
        posto.className = "leave";
        let i = postiScelti.indexOf(numero);
        if(i == -1){
            console.log("errore");
            return;
        }
        postiScelti[i] = postiScelti[postiScelti.length - 1];
        postiScelti.pop();
    }
    else if(posto.className == "leave"){
        posto.className = "autobook";
        postiScelti.push(numero);
    }
}

//ritorno alle proiezioni e cancello tutto ciò che riguarda le prenotazioni
function mostraProiezioni(){
    document.getElementById("proiezioni").hidden = false;
    document.getElementById("prenotazioni").hidden = true;
    zonaPrenotazioni.innerHTML="";
    eliminaHandlers();
}

//fetching dei film
/*async*/function inserisciFilm(){
    /*getProiezioni().then((films) => films.forEach(addFilm), (error) => alert("Caricamento film non riuscito"));*/
    getProiezioniFake().forEach(addFilm);
}

//inizializzazione pagina
/*async*/function init(){
    inserisciFilm();
    mostraProiezioni();
}

//azioni per l'invio della prenotazione
function mandaPrenotazione(){
    console.log("Invio in corso");
    let ris = inviaPrenotazioneFake();
    postiScelti=[];
    return ris;
}

//Seleziono i posti relativi ad una sola prenotazione
function trovaPrenotazione(){
    event.preventDefault();
    let iden = document.getElementById("input-id")
    let idPren = iden.value;
    iden.value = "";
    let a = current.getOccupatiById(idPren);
    if(a.length == 0){
        alert("Nessuna prenotazione trovata per questa proiezione")
    }
    else{
        document.querySelectorAll("td").forEach(block);
        mostraPrenotazione(a);
        eliminaHandlers();
        let e = document.getElementById("invio");
        e.appendChild(document.createTextNode("Modifica Prenotazione"));
        e.addEventListener("click", function(){
            modifica(idPren);
        });
    }
}

function mostraPrenotazione(postiPrenotati){
    postiPrenotati.sort(function(a, b){
        return a - b;
    });
    postiScelti = postiPrenotati.map(x => x);
    let righe = zonaPrenotazioni.childNodes;
    let n = 0;
    for(let i of righe){
        let celle = i.childNodes;
        for(let j of celle){
            if(n == postiPrenotati[0]){
                j.className = "autobook";
                postiPrenotati.shift(); 
                if(postiPrenotati.length == 0){
                    return;
                }
            }
            n++;
        }
    }
}

function block(cella){
    cella.className = "booked";
}

function modifica(idPrenotazione){
    if(postiScelti.length == 0){
        eliminaPrenotazioneFake(current._id, idPrenotazione);
        alert("Prenotazione eliminata con successo");
    }
    else{
        modificaPrenotazioneFake(current._id, idPrenotazione);
        postiScelti = [];
        alert("Prenotazione modificata con successo");
    }
    mostraProiezioni();
}

function eliminaHandlers(){                         //relativo solo al pulsante di invio
    let old = document.getElementById("invio");
    let newNode = old.cloneNode();
    old.parentNode.replaceChild(newNode, old);
}

function aggiornaFilm(){
    zonaFilm.innerHTML="";
    inserisciFilm();
}