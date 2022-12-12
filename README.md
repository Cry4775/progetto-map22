# Progetto esame "Metodi avanzati di programmazione" (UniBA)
## Team: Pierdamiano Zagaria

### Indice dei riferimenti ai requisiti richiesti con corrispondenti sezioni del documento

- Database:
- Utilizzo dei file:
- Programmazione concorrente: 
- Swing:
- RESTful: 

## 1 Introduzione al progetto
L'idea del progetto é quella di sviluppare un engine/framework con cui é possibile, attraverso l'ausilio di un file json, impostare la propria storia e renderla giocabile.
Il gioco in sé é un'avventura testuale con alcuni elementi grafici a solo scopo visivo e non interattivi, in quanto gli input vengono dati attraverso un classico box di testo.
É inclusa nel pacchetto una storia giá impostata e pronta per essere giocata.

## 1.1 The Haunted House
"The Haunted House" é una storia soft-horror che racconta la disavventura di un uomo che si ritrova rinchiuso all'interno di una vecchia villa, circondata dai suoi misteri, alla ricerca dell'amica, proprietaria della casa.

## 1.2 Interfaccia grafica e comandi
Per giocare é sufficiente una tastiera in quanto non é richiesto l'utilizzo del puntatore, se non in caso di presenza di un salvataggio, dal momento che viene richiesta l'interazione con una finestra di dialogo.

<p align="center">
<img src="docs/img/gui.png" width=500>
</p>

L'interfaccia é divisa in piú parti:
- la barra superiore ospita a sinistra il nome della stanza, a destra il contatore delle azioni;
- la bussola, *non* interattiva, indica per ogni direzione attraverso un colore se quella direzione é percorribile o meno:
    - rosso: direzione bloccata da un muro;
    - giallo: direzione bloccata da una porta chiusa;
    - verde: direzione percorribile;
    - blu: direzione da cui si proviene.
- l'immagine della stanza;
- il pannello di output con scrittura sequenziale (per saltare l'animazione basta premere INVIO);
- in basso, il box di testo per l'input.

Il gioco divide le stanze in due tipi principali: 
- *giocabili*;
- *non giocabili* (cutscenes).

Per quanto riguarda le stanze giocabili, sono normali stanze che richiedono azioni del giocatore, mentre le cutscenes sono stanze di passaggio, che tipicamente descrivono la situazione e alla fine richiedono l'input del giocatore (**INVIO**) per continuare, come suggerisce il box di input stesso.

Il gioco quindi richiede dei comandi dal giocatore, dati attraverso frasi immesse nel box di testo.
Le frasi comprensibili per l'engine sono del tipo *\<azione> \<oggetto> \<oggetto>* dove:
- ***azione***: le azioni in corsivo non richiedono la specifica di oggetti, le azioni in grassetto richiedono la specifica di fino a 2 oggetti, mentre le restanti richiedono solo la specifica di 1 oggetto. 
<br>**N.B.** Ogni azione ha un suo set di sinonimi, vengono riportati solo i nomi principali.
    - *Nord, Nord-Ovest, Nord-Est, Sud, Sud-Est, Sud-Ovest, Est, Ovest, sali, scendi*
    - *Inventario*
    - *Salva*
    - Esamina
    - Prendi
    - Premi
    - Tira
    - Sposta
    - Indossa
    - Spegni
    - Parla
    - Togli
    - Leggi
    - **Accendi**
    - **Apri**
    - **Inserisci**
    - **Versa**
- ***oggetto***: qualsiasi entitá che sia nella stanza o nell'inventario.

<p align="center">
<img src="docs/img/pickup-ex.png" width=500>
</p>

É possibile salvare la partita in qualsiasi momento con il comando "salva", per poi caricare il salvataggio ad un nuovo avvio del gioco.<br>
**N.B.** Allo stato attuale é presente un solo slot di salvataggio, quindi verrá sempre sovrascritto.

## 1.3 Soluzione del gioco
**N.B.** Per prendere gli oggetti é necessario prima esaminare il contenitore.

Partendo dal salotto, esaminate il divano e troverete un accendino, prendetelo e proseguite ad ovest per il corridoio, poi andate a sud.

Vi troverete nell'ingresso, qui dovete aprire la porta (apri porta) ed entrare nel vestibolo (a ovest), dove troverete un cappotto, esaminatelo e prendete la collana al suo interno, dopo di che tornate indietro al corridoio e questa volta proseguite a nord.

Una volta arrivati alla nuova parte di corridoio, aprite prima la porta ed entrate nella cucina (a est), aprite la credenza e prendete la ciotola al suo interno, poi tornate indietro e andate a ovest, arrivando nel salone. Qui potete andare a sud-ovest nell'andito e tirate la leva per far cadere il candelabro e renderlo accessibile, quindi tornate nel salone, guardate il candeliere e prendete la chiave. A questo punto potete tornare nel corridoio e proseguite  a nord, salite le scale e andate verso sud.

Ora dovreste essere nel corridoio del 1° piano. Per prima cosa entrate nella camera da letto a est, troverete un candeliere con una candela ma é irraggiungibile, quindi ci basterá spostare il comó (sposta comó), esaminare il candeliere e quindi prendere la candela. Una volta presa la candela, possiamo entrare nel bagno a nord e possiamo esaminare la vasca, rivelando dell'acqua, che possiamo prendere grazie alla ciotola di prima (prendi acqua).

Ora possiamo uscire dal bagno e dalla camera da letto, e proseguire verso sud, dove troveremo un percorso obbligato verso la porta chiusa a chiave ad ovest. Apriamo quindi la porta con la chiave che abbiamo preso nel salone (apri porta con chiave) ed entriamo nello studio, dove troveremo una libreria che andrá spostata, quindi facciamolo (sposta libreria) e verrá rivelata un'incisione della forma di una stella a cinque punte, che é proprio il pendente della nostra collana. Esaminiamo quindi la collana e prendiamo il pendente, poi inseriamolo nell'incisione ("inserisci pendente nell incisione" senza apostrofi) rivelando un passaggio segreto. Entriamoci e scendiamo le scale.

Arriveremo in una stanza completamente buia, infatti non potremo vedere nulla né interagire con nulla, l'unica cosa che potremo fare sará tornare indietro dalla direzione di provenienza, peró noi abbiamo un accendino e una candela, quindi possiamo illuminare la stanza accendendola (accendi candela). Ora che possiamo vedere, proseguiamo verso nord-est e poi sud-est dove troveremo una porta non chiusa a chiave. Apriamola ed entriamo.
 
Finita la cutscene, parliamo almeno una volta con Valeria, prendiamo l'anello e infine torniamo indietro al bivio, per andare a nord.

Adesso dobbiamo indossare l'anello che Valeria ci ha appena dato per poter oltrepassare il varco a nord, quindi indossiamolo (indossa anello) e proseguiamo, trovando l'antro della strega che sta facendo i suoi incantesimi finali. Per sconfiggerla basterá indossare la collana, cosí ci sará possibile interagire con il braciere, e quindi rovesciarci l'acqua della ciotola per spegnerlo (spegni fuoco con acqua), rendendo vani gli sforzi della strega. 

Una volta completate le cutscenes, il gioco sará completato e si chiuderá.