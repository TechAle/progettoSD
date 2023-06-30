const API_URI = "http://localhost:8080";
const zonaFilm= document.querySelector("#locandina");
const zonaPrenotazioni = document.querySelector("#sala table");
let postiScelti = [];
let postiRimossi = [];

class Film {
    constructor(identificativo, numeroPosti, a){
        this._id = identificativo;
        this._posti = numeroPosti;
        this._occupati = a;
    }
    static async builder(identificativo, numeroPosti){
        let a = await getPostiOccupati(identificativo);
        return new Film(identificativo, numeroPosti, a);
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

let current;                            //utilizzo per il film

//variabili di prova
/*const films = [
    {id: 1, nome: "Non aprite quella porta", descrizione: "...che è dell'ufficio di Antoniotti", sala: "Sala 1", orario:"10:30", data:"a", posti: 250},
    {id: 2, nome: "Mine", descrizione: "Il protagonista dovrà affrontare diverse insidie.. per creare il proprio progetto di SD", sala: "Sala 2", orario:"9:45", data:"a", posti: 220},
    {id: 3, nome: "Wild University", descrizione: "Documentario sulla routine quotidiana degli studenti di Informatica dell'UniMiB", sala: "a", orario:"a", data:"a", posti: 125},
    {id: 4, nome: "300", descrizione: "Storia di valorosi eroi che sono siusciti a resistere al progetto di LP", sala:"a", orario:"a", posti: 300},
    {id: 5, nome: "Terminator", descrizione: "Sorrenti è venuto dal futuro e minaccia l'esistenza dell'Università! Riuscirà il Sig. Ferretti a salvarla?", sala:"a", orario:"a", data:"a", posti: 160},
    {id: 6, nome: "Fuga da RSAD", descrizione:"a", sala: "a", orario:"a", data:"a", posti: 280},
    {id: 7, nome: "Apocalypto", descrizione: "Una matricola lotta per la propria vita contro un gruppo di docenti di GAL. Poi arrivano i maranza...", sala: "a", orario:"a", data:"a", posti: 140},
    {id: 8, nome: "The Hunger Games", descrizione: "24 pendolari universitari, di 12 facoltà diverse, rimangono intrappolati in un treno fermo sui binari, mentre si recano alla loro università preferita. Osservati dai dipendenti di Trenord, inizieranno una dura lotta per la sopravvivenza. Solo uno rimarrà vivo... forse", sala: "a", orario:"a", data:"a", posti: 300},
    {id: 9, nome: "Mission Impossible - Protocollo fantasma", descrizione: "Riuscirà il nostro eroe ad uscire dalla lezione dell'Avitabile senza essere visto?", sala: "a", orario:"a", data:"a", posti: 320},
    {id: 10, nome: "Un film da non vedere", descrizione: "Sperando di non sembrare troppo cattivo: invece di pensare a delle descrizioni per questo film, non sarebbe stato meglio utilizzare quel poco tempo per approfondire come si implementa in CMOS una porta And a 6 ingressi oppure quanta approssimazione c'è tra un numero in virgola mobile ed il successivo quando stiamo rappresentando valori attorno a 2425.32?", sala: "a", orario:"a", data:"a", posti: 0}
  ];
const prenotazioniFake = [
    [2, 10],
    [4, 10],
    [1, 3],
    [3, 10],
    [22, 10],
    [11, 4],
    [23, 10],
    [40, 1],
    [7, 4],
    [24, 10],
    [32, 16],
    [26, 2],
    [41, 1],
    [42, 1]
] //coppie <posto, prenotazione>*/
/*const parsing="[{\"idProiezione\": 50,\"nome\":\"film1\",\"descrizione\":\"a\",\"sala\":\"a1\",\"postiTotali\":100,\"postiPrenotati\":[],\"giorno\":\"2002-06-01-10-5\"},{\"idProiezione\": 1,\"nome\":\"film2\",\"descrizione\":\"b\",\"sala\":\"a1\",\"postiTotali\":100,\"postiPrenotati\":[[0,1],[0,4],[1,6]],\"giorno\":\"2003-06-02-15-15\"},{\"idProiezione\": 2,\"nome\":\"film3\",\"descrizione\":\"c\",\"sala\":\"a2\",\"postiTotali\":50,\"postiPrenotati\":[[0,3]],\"giorno\":\"2002-08-05-19-0\"}]";
*/
//prende una lista di proiezioni, metodo GET
async function getProiezioni(){
    let response = await fetch(`${API_URI}/getFilms`);          
    if(!response.ok){
        throw new Error (`${response.status} ${response.statusText}`)
    }
    return await response.json();
    //return JSON.parse(parsing);
}
//prende una lista di prenotazioni per un film, metodo GET
async function getPrenotazioni(id){
    let response = await fetch(`${API_URI}/getFilm/${id}`);
    if(!response.ok){
        if(response.status === 404){
            throw new Error("Errore: film inesistente");
        }
        throw new Error (`${response.status} ${response.statusText}`)
    }
    let a = await response.json();
    return a["postiPrenotati"].sort(function(a, b){
        return a[0] - b[0];
    })
}

//invio i posti che vorrei prenotare al server (array di numeri), chiamata POST
async function inviaPrenotazione(){
    const endpoint = `${API_URI}/aggiungiPosto/${current._id}`
    const response = await fetch(endpoint, {
        method: "POST",
        headers: {
            "Content-type": "application/json",
            "Access-Control-Request-Method": "PUT",
            "Access-Control-Request-Headers": "content-type",
            "Referer": "http://localhost:63342/",
            "Origin": "http://localhost:63342",
            "Host": "localhost:8080",
            "Sec-Fetch-Dest": "empty",
            "Sec-Fetch-Mode": "cors",
            "Sec-Fetch-Site": "same-site"
        },
        body: JSON.stringify(postiScelti)
    });
    if(!response.ok){
        if(response.status === 409){                     //gestione errore 409 - Conflict
            throw new Error("Prenotazione fallita: uno o più posti selezionati sono già occupati");
        }
        else throw new Error(`${response.status} ${response.statusText}`); //errore generico
    }
    const tmp = await response.json();
    const location = tmp["location"];
    postiScelti = [];
    return location/*.split("/").pop();*/
}

//invio i posti da mantenere al server (array di numeri), chiamata PUT
async function aggiungiPosti(idPrn){
    const endpoint = `${API_URI}/aggiungiPosto/${current._id}/${idPrn}`;
    const response = await fetch(endpoint, {
        method: "PUT",
        headers: {
            "Content-type": "application/json"
        },
        body: JSON.stringify(postiScelti)
    });
    if(!response.ok){
        if(response.status === 404){
            throw new Error("Errore in aggiornamento: prenotazione non trovata")
        }
        else if(response.status === 409){
            throw new Error("Aggiornamento fallito in aggiunta: uno o più posti selezionati sono già occupati");
        }
        throw new Error(`${response.status} ${response.statusText}`);
    }
}

//chiamata DELETE
async function rimuoviPosti(idPrn){
    const endpoint = `${API_URI}/eliminaPosto/${current._id}/${idPrn}`
    const response = await fetch(endpoint, {
        method: "PUT",
        headers: {
            "Content-type": "application/json"
        },
        body: JSON.stringify(postiRimossi)
    });
    if(!response.ok){
        if(response.status == 404){
            throw new Error("Errore: prenotazione inesistente")
        }
        throw new Error(`${response.status} ${response.statusText}`);
    }
}

async function getPostiOccupati(id){
    return await getPrenotazioni(id);
}

//logica di creazione locandine in zonaPrenotazioni, modifica dell'albero DOM
function createCard(film){
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
    a.appendChild(document.createTextNode(film["giorno"]));
    body.appendChild(a);
    a = document.createElement("button");
    a.appendChild(document.createTextNode("Prenota Visione"));
    a.addEventListener("click", function(){
        mostraPrenotazioni(film["postiTotali"], film["idProiezione"]);
    });
    body.appendChild(a);
    elem.appendChild(body);
    return elem;
}

//Aggiunta di film in zonaProiezioni, modifica dell'albero DOM
function addFilm(film){
    let presentazione = document.createElement("div");
    presentazione.className="item";
    presentazione.appendChild(createCard(film));
    zonaFilm.appendChild(presentazione);
}

//Aggiunta icone spettatore, indipendentemente dal film corrente; modifica albero DOM
function createSomeImages(){
    document.querySelectorAll("td").forEach(addImage);
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
async function mostraPrenotazioni(posti, id){
    document.getElementById("proiezioni").hidden = true;            //nasconde le proiezioni
    document.getElementById("prenotazioni").hidden = false;         //Mostra i posti, da disegnare
    current = await Film.builder(id, posti);                                  //chiamata implicita al server, vedasi la dichiarazione della classe
    await makeTable();
    createSomeImages();
    document.getElementById("invio").appendChild(document.createTextNode("Invia Prenotazione"));
    document.getElementById("cerca-prenotazione").addEventListener("submit",trovaPrenotazione)
    document.getElementById("invio").addEventListener("click", async function(){
        if(postiScelti.length == 0){
            alert("Nessuna prenotazione inviata: non hai selezionato nessun posto");
        }
        else{
            /*try{*/
            let a = await mandaPrenotazione();
            alert("Il numero di prenotazione è: " + a);
            mostraProiezioni();
            /*} catch(error) {
                alert("Errore all'invio della prenotazione")
            }*/
        }
    })
}

//Creazione posti sala, dipendente dal film corrente; modifica albero DOM
async function makeTable(){
    let nPosti = current._posti;
    let nFile = Math.floor(nPosti / 20);
    let PostiRestanti = nPosti - nFile * 20;
    let o = current._occupati.map((x) => x).sort(function(a, b){
        return a[0] - b[0];
    });
/*    let o = current._occupati.toSorted(function(a, b){
        return a[0] - b[0];
    });*/
    if(o.length === 0) o.push([-2, -2]);         //valori impossibili da ottenere
    for(let i = 0; i<nFile; i++){
        let row = document.createElement("tr");
        for(let j = 0; j < 20; j++){
            let cell = document.createElement("td");
            if(i*20+j === o[0][0]){
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
        if(nFile*20+j === o[0][0]){
            cell.className = "booked";
            o.shift();
            if(o.length === 0) o.push([-2, -2]);
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
    if(posto.className === "selected"){
        posto.className = "seat";
        let i = postiScelti.indexOf(numero);
        if(i === -1){
            console.log("errore");
            return;
        }
        postiScelti[i] = postiScelti[postiScelti.length - 1];
        postiScelti.pop();
    }
    else if(posto.className === "seat"){
        posto.className = "selected";
        postiScelti.push(numero);
    }
    else if(posto.className === "autobook"){
        posto.className = "leave";
        postiRimossi.push(numero);
    }
    else if(posto.className === "leave"){
        posto.className = "autobook";
        let i = postiRimossi.indexOf(numero);
        if(i === -1){
            console.log("errore");
            return;
        }
        postiRimossi[i] = postiRimossi[postiRimossi.length - 1];
        postiRimossi.pop();
    }
}

//ritorno alle proiezioni e cancello tutto ciò che riguarda le prenotazioni
function mostraProiezioni(){
    document.getElementById("proiezioni").hidden = false;
    document.getElementById("prenotazioni").hidden = true;
    zonaPrenotazioni.innerHTML="";
    postiRimossi = [];
    postiScelti = [];
    eliminaHandlers();
}

//fetching dei film
async function inserisciFilm(){
    getProiezioni().then((films) => films.forEach(addFilm), (error) => alert("Caricamento film non riuscito"));
//    getProiezioni().forEach(addFilm);
}

//inizializzazione pagina
async function init(){
    inserisciFilm();
    mostraProiezioni();
}

//azioni per l'invio della prenotazione sul film corrente
async function mandaPrenotazione(){
    let ris = await inviaPrenotazione();
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
        mostraPrenotazione(a);
        eliminaHandlers();
        let e = document.getElementById("invio");
        e.appendChild(document.createTextNode("Modifica Prenotazione"));
        e.addEventListener("click", function(){
            modifica(idPren);
        });
    }
}

//highlighting dei posti di una prenotazione
function mostraPrenotazione(postiPrenotati){
    postiPrenotati.sort(function(a, b){
        return a - b;
    });
    let righe = zonaPrenotazioni.childNodes;
    let n = 0;
    for(let i of righe){
        let celle = i.childNodes;
        for(let j of celle){
            if(n === postiPrenotati[0]){
                j.className = "autobook";
                postiPrenotati.shift(); 
                if(postiPrenotati.length === 0){
                    return;
                }
            }
            n++;
        }
    }
}

//invio delle modifiche al server
function modifica(idPrenotazione){
    if(postiScelti.length != 0){
        aggiungiPosti(idPrenotazione);
    }
    if(postiRimossi.length != 0){
        rimuoviPosti(idPrenotazione);
    }
    alert("Prenotazione modificata con successo");
    postiScelti = [];
    postiRimossi = [];
    mostraProiezioni();
}

//Funzione che elimina il contenuto interno del pulsante di invio e i suoi handlers, in pratica sostituisce l'elemento con un suo clone
function eliminaHandlers(){
    let old = document.getElementById("invio");
    let newNode = old.cloneNode();
    old.parentNode.replaceChild(newNode, old);
}

function aggiornaFilm(){
    zonaFilm.innerHTML="";
    inserisciFilm();
}