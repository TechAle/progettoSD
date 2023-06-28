var API_URI = "http://localhost:8080";
var zonaFilm= document.querySelector("#locandina");
var zonaPrenotazioni = document.querySelector("#sala table");
var postiScelti = [];


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
]
//Chiamate GET
async function getProiezioni(){                                 
    let response = await fetch(`${API_URI}/proiezioni`);          
    if(!response.ok){                                             
        throw new Error (`${response.status} ${response.statusText}`)
    }
    return await response.json();                                 
}
async function getPrenotazioni(id){
    let response = await fetch(`${API_URI}/proiezioni/${id}`);
    if(!response.ok){
        throw new Error (`${response.status} ${response.statusText}`)
    }
    return await response.json();
}
async function getPrenotazione(idProiezione, idPrenotazione){                                 
    let response = await fetch(`${API_URI}/proiezioni/${idProiezione}/${idPrenotazione}`);          
    if(!response.ok){                                             
        throw new Error (`${response.status} ${response.statusText}`)
    }
    return await response.json();                                 
}
//invio i posti che vorrei prenotare al server, chiamata POST
/*async function inviaPrenotazione(idProiezione){
    const endpoint = `${API_URI}/films/${idProiezione}`
    const response = await fetch(endpoint, {
        method: "POST",
        headers: {
            "Content-type": "application/json"
        },
        body: JSON.stringify(postiScelti)
    });
    if(!response.ok)
        throw new Error(`${response.status} ${response.statusText}`);
        return;
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
}*/

function createCard(film){                                  //Gestione creazione locandine
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

function addFilm(film){                                     //Aggiunta di film
    let presentazione = document.createElement("div");
    presentazione.className="item";
    presentazione.appendChild(createCard(film));
    zonaFilm.appendChild(presentazione);
}

function createSomeImages(){                                //Aggiunta icone spettatore
    let cells = document.querySelectorAll(".seat");
    cells.forEach(addImage);
}

function addImage(e){                                       //Aggiunta icone spettatore
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

function makeTable(nPosti){                                 //Creazione posti sala
    let nFile = Math.floor(nPosti / 20);
    let PostiRestanti = nPosti - nFile * 20;
    for(let i = 0; i<nFile; i++){
        let row = document.createElement("tr");
        for(let j = 0; j < 20; j++){
            let cell = document.createElement("td");
            cell.className = "seat";
            cell.addEventListener("click", function(){
                modificaPosto(cell, i*20+j);
            })
            row.appendChild(cell);
        }
        zonaPrenotazioni.appendChild(row);
    }
    let last = document.createElement("tr");
    for(let j = 0; j < PostiRestanti; j++){
        let cell = document.createElement("td");
        cell.className = "seat";
        cell.addEventListener("click", function(){
            modificaPosto(cell, nFile*20+j);
        })
        last.appendChild(cell);
    }
    zonaPrenotazioni.appendChild(last);
}

function mostraPrenotazioni(posti, id){  //mostra prenotazioni
    document.getElementById("proiezioni").hidden = true;
    document.getElementById("prenotazioni").hidden = false;
    makeTable(posti);
    createSomeImages();
    document.getElementById("invio").addEventListener("click", function(){
        /*try{
            let a = inviaPrenotazione(id);
            alert(a);
            mostraProiezioni();
        } catch(error) {
            alert("Errore all'invio nella prenotazione");
        }*/
        if(postiScelti.length == 0){
            alert("Nessuna prenotazione inviata");
        }
        else{
            let a = inviaPrenotazione(id);
            alert(a);
        }
        //sostituisco il bottone con un suo clone per eliminare i gestori di eventi
        let old = document.getElementById("invio");
        let newNode = old.cloneNode();
        old.parentNode.replaceChild(newNode, old);
        document.getElementById("invio").appendChild(document.createTextNode("Invia Prenotazione"));
        mostraProiezioni();
    })
}

function mostraProiezioni(){
    document.getElementById("proiezioni").hidden = false;
    document.getElementById("prenotazioni").hidden = true;
    zonaPrenotazioni.innerHTML="";
}

/*async*/function aggiornaFilm(){
    /*getProiezioni().then((films) => films.forEach(addFilm), (error) => alert("Caricamento film non riuscito"));*/
    films.forEach(addFilm);
}

/*async*/function init(){
    document.getElementById("cerca-prenotazione").addEventListener("submit",trovaPrenotazione)
    aggiornaFilm();
    mostraProiezioni();
}

function inviaPrenotazione(idProiezione){
    console.log("Invio in corso");
    postiScelti=[];
    return 10;
}

function trovaPrenotazione(){
    event.preventDefault();
    let iden = document.getElementById("input-id")
    let idPren = iden.value;
    iden.value = "";
    mostraPrenotazione(prenotazioniFake, idPren);
}

function mostraPrenotazione(postiPrenotati, prenotazione){
    postiPrenotati.sort(function(a, b){
        return a[0] - b[0];
    });
    postiScelti = [];
    let righe = zonaPrenotazioni.childNodes;
    let n = 0;
    for(let i of righe){
        let celle = i.childNodes;
        for(let j of celle){
            if(n == postiPrenotati[0][0]){
                if(prenotazione == postiPrenotati[0][1]){
                    j.className = "autobook";
                    postiScelti.push(postiPrenotati[0][0]);
                }
                else{
                    j.className = "booked";
                }
                postiPrenotati.shift();
                if(postiPrenotati.length == 0){
                    document.querySelectorAll(".seat").forEach(block);
                    return;
                }
            }
            else j.className = "booked";
            n++;
        }
    }
}

function block(cella){
    cella.className = "booked";
}