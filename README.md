# Progetto esame "Metodi avanzati di programmazione" (UniBA)
## di Pierdamiano Zagaria
<br>

# Indice
1. Introduzione al progetto
2. Struttura fondamentale del software
3. Requisiti richiesti
4. Come impostare la propria storia
5. Specifica algebrica
6. Diagrammi UML

# 1 Introduzione al progetto
L'idea del progetto é quella di sviluppare un engine/framework con cui é possibile, attraverso l'ausilio di un file json, impostare la propria storia e renderla giocabile.<br>
Il gioco in sé é un'avventura testuale con alcuni elementi grafici a solo scopo visivo e non interattivi, in quanto gli input vengono dati esclusivamente attraverso un classico box di testo.<br>
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

Il gioco quindi richiede dei comandi dal giocatore, dati attraverso frasi immesse nel box di testo.<br>
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

Partendo dal salotto, esaminiamo il divano e troveremo un accendino, prendiamolo e proseguiamo verso ovest per il corridoio, poi andiamo a sud.

Ci troveremo nell'ingresso, qui dovremo aprire la porta (apri porta) ed entrare nel vestibolo (a ovest), dove troveremo un cappotto, esaminiamolo e prendiamo la collana al suo interno, dopo di che torniamo indietro al corridoio e questa volta proseguiamo a nord.

Una volta arrivati alla nuova parte di corridoio, apriamo la porta ed entriamo nella cucina (a est), apriamo la credenza e prendiamo la ciotola al suo interno, poi torniamo indietro e andiamo a ovest, arrivando nel salone. Qui andiamo a sud-ovest nell'andito e tiriamo la leva per far cadere il candelabro e renderlo accessibile, quindi torniamo nel salone, guardiamo il candeliere e prendiamo la chiave. A questo punto possiamo tornare nel corridoio e proseguire a nord.

Saliamo le scale e andiamo verso sud, ora dovremmo trovarci nel corridoio del 1° piano. Per prima cosa entriamo nella camera da letto a est, troveremo un candeliere con una candela ma é irraggiungibile, quindi ci basterá spostare il comó (sposta comó), esaminare il candeliere e quindi prendere la candela. Una volta presa la candela, possiamo entrare nel bagno a nord e possiamo esaminare la vasca, rivelando dell'acqua, che possiamo prendere grazie alla ciotola di prima (prendi acqua).

Ora possiamo uscire dal bagno e dalla camera da letto, e proseguire verso sud, dove troveremo un percorso obbligato verso la porta chiusa a chiave ad ovest. Apriamo quindi la porta con la chiave che abbiamo preso nel salone (apri porta con chiave) ed entriamo nello studio, dove troveremo una libreria che andrá spostata, quindi facciamolo (sposta libreria) e verrá rivelata un'incisione della forma di una stella a cinque punte, che é proprio il pendente della nostra collana. Esaminiamo quindi la collana e prendiamo il pendente, poi inseriamolo nell'incisione ("inserisci pendente nell incisione" senza apostrofi) rivelando un passaggio segreto. Entriamoci e scendiamo le scale.

Arriveremo in una stanza completamente buia, infatti non potremo vedere nulla né interagire con nulla, l'unica cosa che potremo fare sará tornare indietro dalla direzione di provenienza, peró noi abbiamo un accendino e una candela, quindi possiamo illuminare la stanza accendendola (accendi candela). Ora che possiamo vedere, proseguiamo verso nord-est e poi sud-est dove troveremo una porta non chiusa a chiave. Apriamola ed entriamo.
 
Finita la cutscene, parliamo almeno una volta con Valeria, prendiamo l'anello e infine torniamo indietro al bivio, per andare a nord.

Adesso dobbiamo indossare l'anello che Valeria ci ha appena dato per poter oltrepassare il varco a nord, quindi indossiamolo (indossa anello) e proseguiamo, trovando l'antro della strega che sta facendo i suoi incantesimi finali. Per sconfiggerla basterá indossare la collana, cosí ci sará possibile interagire con il braciere, e quindi rovesciarci l'acqua della ciotola per spegnerlo (spegni fuoco con acqua), rendendo vani gli sforzi della strega. 

Una volta completate le cutscenes, il gioco sará completato e si chiuderá.

# 2 Struttura fondamentale del software
Piccola descrizione delle componenti fondamentali del software.

## 2.1 Engine
L'engine é la classe che si occupa di gestire tutti i componenti del software e che permette la comunicazione tra essi. 
Contiene riferimenti dei componenti fondamentali (es. GUI) ed esegue il processing dei comandi dati in input.

### 2.1.1 GameManager
Il GameManager é la classe che memorizza lo stato del gioco. Segue il pattern Singleton.<br>
Contiene la lista delle stanze, dei comandi, degli oggetti nell'inventario e riferimenti alla stanza attuale e precedente.
Contiene metodi per la gestione dello stato (es. `moveTo(room)` che aggiorna la stanza attuale e precedente) e per eventuali aggiunte (es. `addRoom(room)`).

### 2.1.2 GUIManager
Il GUIManager é la classe "middleman" che permette la comunicazione tra manager dei componenti e i chiamanti, per un'interazione piú controllata.
Va registrato il riferimento al MainFrame all'inizializzazione del software.

### 2.1.2.1 CompassManager
Il CompassManager é la classe che gestisce il pannello della bussola della GUI.
Contiene metodi per l'aggiornamento della bussola.
Vanno registrati i riferimenti ai JLabel all'inizializzazione del software.

### 2.1.2.2 OutputManager
L'OutputManager é la classe che gestisce il pannello testuale (output) e il box di testo (input) della GUI.
Contiene metodi per la scrittura e per la gestione dell'input.
Vanno registrati i riferimenti al JTextPane e al JTextField all'inizializzazione del software.

### 2.1.3 SoundManager
Il SoundManager é la classe che si occupa di gestire tutta la parte sonora del software.
Permette la riproduzione di file audio su due canali distinti, uno esclusivo per la musica e uno per gli effetti sonori delle interazioni. 
Allo stato attuale i file musicali, nonostante dovrebbero essere personalizzabili, sono hard-coded e pertanto non personalizzabili.<br>
Gli effetti sonori delle interazioni invece sono caricati dalle risorse interne al JAR.

### 2.1.4 DBManager
Il DBManager é la classe che permette il salvataggio/caricamento dello stato di gioco.
Contiene metodi utili alla gestione del database H2 e all'esecuzione dei processi di salvataggio o caricamento.

## 2.2 Package Component
Le classi del package `Component` rappresentano i principali elementi costituenti il dominio di gioco.<br>
Si distinguono in `Room` e `Entity`. Di seguito alcuni esempi piú importanti.

### 2.2.1 GameComponent
Un `GameComponent` é un qualsiasi componente del dominio di gioco che ha un ID, un nome e una descrizione.
É una classe astratta in quanto non é una specializzazione, ma semplicemente una base di partenza.
Tutte le classi del package ereditano da questa classe.

### 2.2.2 AbstractRoom
Una stanza ha un'unica aggiunta al terzetto di variabili iniziale, ovvero il percorso dell'immagine da visualizzare.
Di conseguenza `AbstractRoom` é la base di partenza per le stanze.

### 2.2.3 PlayableRoom
Una `PlayableRoom` contiene variabili di riferimento per le stanze ad ogni direzione (sia gli ID, per il caricamento da json, che le stanze, per l'esecuzione del gioco vera e propria), una lista per gli oggetti contenuti nella stanza, informazioni per lo stato di illuminazione e infine un riferimento ad un `RoomEvent`.
É la stanza piú comunemente utilizzata, **non** prevede alcun tipo di mutabilitá, difatti possiamo definirla "statica".

### 2.2.4 MutableRoom
Una `MutableRoom` é una specializzazione di `PlayableRoom` e come dice il nome, al contrario della classe da cui eredita, possiamo definirla "dinamica".
Attraverso la definizione di una `newRoom` possiamo permettere l'aggiornamento dei campi definiti dall'utente nel JSON, in seguito al trigger di un evento (che puó essere di un qualsiasi oggetto, anche di un'altra stanza).
Le `newRoom` possono anche essere concatenate fino ad avere infinite possibilitá di mutabilitá.
Tutti i campi di una `PlayableRoom` possono essere aggiornati, anche la lista degli oggetti, infatti se aggiungiamo un oggetto nella `newRoom` con un ID non presente nella stanza originale, allora l'oggetto verrá *aggiunto* alla stanza originale (in seugito all'evento), se invece ne aggiungiamo uno con ID identico a un oggetto giá presente, allora verrá *sostituito*.

### 2.2.5 CutsceneRoom 
Una `CutsceneRoom` é una stanza non giocabile che prevede alla fine dell'output di premere INVIO per continuare.
Se viene contrassegnata nel JSON come `finalRoom` allora il gioco terminerá non appena viene premuto INVIO.

### 2.2.6 AbstractEntity
Un'entitá é qualsiasi cosa sia presente nella lista di entitá di una stanza.
Si puó specializzare in molti tipi di entitá diversi che implementano particolari interfacce ad indicare il tipo stesso.
Contiene riferimenti al `parent` (GameComponent) che puó essere sia una stanza che un `AbstractContainer` e al `closestRoomParent` (PlayableRoom) che serve ad identificare la posizione corretta dell'entitá all'interno della stanza (es. Se é in una MutableRoom, al "secondo livello" di newRoom, allora il closestRoomParent sará quella newRoom con ID "0b" dove "0" é la stanza madre e "0a" é il primo figlio).
Contiene una lista di eventi e di `IWearable` per verificare i requisiti di interazione.

Specializzazioni:
- AbstractContainer;
- BasicObject;
- BasicItem;
- FireObject;
- MovableObject (IMovable);
- PullableObject (IPullable);
- PushableObject (IPushable);
- Door (IOpenable);
- UnopenableDoor;
- InvisibleWall;
- Human (ITalkable).

Verranno descritte solo le classi ritenute piú importanti.


### 2.2.7 AbstractContainer
Un container é appunto un contenitore di oggetti. Il funzionamento prevede che (nel gioco) prima di poter accedere agli oggetti al suo interno, é necessario esaminarlo.

Specializzazioni:
- BasicContainer;
- ChestlikeContainer (IOpenable);
- SocketlikeContainer;
- WearableContainer (IWearable).

### 2.2.8 BasicObject
Un `BasicObject` é una qualsiasi entitá la cui unica azione possibile é "Esamina".

### 2.2.9 BasicItem
Un `BasicItem` invece é un qualsiasi oggetto che puó essere preso nell'inventario tramite il comando "Prendi".

Specializzazioni:
- FillableItem (IFillable);
- FluidItem (IFluid);
- LightSourceItem (ILightSource);
- ReadableItem (IReadable);
- WearableItem (IWearable).

# 3 Requisiti richiesti
I **thread** (Sezione 3.1.2) sono stati utilizzati per:
- Inizializzazione e caricamento del gioco (`RoomsLoader`);
- Pannello animazione noise effect sulla GUI (`NoiseFXPanel`);
- Aggiornamento della bussola nella GUI (`CompassManager.updateCompassLabel()` tramite SwingWorker);
- Engine (`Engine.commandPerformed()`);
- Attesa per il tasto INVIO durante le cutscene (`GUIManager.waitUntilEnterIsPressed()`);
- Monitor progresso del caricamento all'avvio del gioco (`GUIManager.createWeatherProgressMonitor()` tramite SwingWorker).

I **Database** (Sezione 3.2) sono stati utilizzati per:
- Salvataggio e caricamento dello stato di gioco (`DBManager`).

Le **API REST** (Sezione 3.3) sono state utilizzate per:
- Controllo in tempo reale del meteo per decidere i suoni ambientali (`WeatherFetcher`).

La **reflection** (Sezione 3.4) é stata utilizzata per:
- Mutabilitá delle stanze (`MutableRoom.updateFields()`);
- Distinguere i tipi di classe durante il caricamento da JSON (`RoomsLoader`);
- Processing dei riferimenti delle stanze (`Rooms.loadDirections()`);
- Metodi utils (`Utils.getField()`).

L'**utilizzo dei file** (es. `MainFrame`) e delle **lambda expressions** (es. `Rooms.loadDirections()`) é invece distribuito in modo omogeneo all'interno dell'intero codice.
Lo stesso vale per l'utilizzo della **Swing**, framework con cui l'intero programma é stato costruito.

Esaminiamo in dettaglio qualche esempio.

## 3.1 Inizializzazione stato di gioco
Prendiamo in esame il caso in cui esista un salvataggio.

All'avvio del gioco verrá chiesto tramite un dialog se si desidera caricare il salvataggio o meno. In entrambi i casi il processo di caricamento verrá affidato a un thread `RoomsLoader` che in base alla nostra scelta caricherá i dati dal JSON o dal DB.
Prima di tutto vengono caricati i comandi nel `GameManager` tramite un thread `CommandsLoader` e nel frattempo verrá caricato tutto lo stato di gioco (stanze con relativi oggetti e inventario).

## 3.1.1 RoomsLoader (Utilizzo dei file)
In base al valore fornito dal dialog che corrisponde alla modalitá desiderata di caricamento (JSON/DB) verranno eseguite procedure diverse.
Se viene scelto il caricamento da JSON, allora viene impostato l'oggetto Gson che permette la lettura del JSON, e viene quindi letto il file `rooms.json`. 
Se l'esecuzione non viene interrotta da alcun errore, gli oggetti presenti in tutte le stanze verranno mappati e ogni singolo oggetto verrá processato al fine di collegare i riferimenti (che da JSON saranno definiti tramite ID).
Se invece viene scelto il caricamento da DB, allora vengono direttamente caricate le stanze e l'inventario grazie al `DBManager` e gli oggetti processati come sopra.

## 3.1.2 RoomsLoader (Programmazione concorrente)
Indipendentemente dalla modalitá scelta, poi, ci sará il processo di collegamento tra le stanze, che é diviso in piú thread (10 per le `PlayableRoom` + 1 per le `CutsceneRoom`), uno per ogni direzione. Ogni thread quindi tramite le referenze dei metodi forniti (**reflection**) ciclerá per tutte le stanze di sua competenza e cercherá l'ID salvato, per poi impostare la referenza alla stanza giusta (es. viene cercato il `northId` nella lista delle stanze e poi viene impostato il `north`).<br>
Una volta completati tutti i thread, il processo sará completato e il gioco interamente caricato e pronto per essere giocato.

## 3.2 Database - Salvataggi
Il database `SAVEDATA` viene utilizzato per memorizzare in maniera persistente tutte le informazioni al momento del salvataggio, necessarie successivamente a ricostruire l'intero stato del gioco, anche se dovesse essere eliminato il file `rooms.json` (eccetto risorse come immagini o audio).

All'avvio del gioco, se il database non esiste, allora viene creato.
Se, invece, esiste, allora viene visualizzato un dialog per scegliere se caricare o meno il salvataggio, in caso positivo, allora il `DBManager` chiama per ogni componente il metodo `loadFromDB()` e carica il tutto in delle liste che poi verranno processate dal `RoomsLoader` come giá visto.<br>
In caso di salvataggio durante il gioco, viene prima eliminato il contenuto delle tabelle e poi vengono salvati i componenti tramite il metodo `saveOnDB`.

## 3.2.1 Tabelle
Nel database é presente una tabella per ogni classe componente lo stato di gioco.
Molte stanze e molti oggetti sono effettivamente "statici" e quindi potrebbero essere caricate direttamente dal JSON, tuttavia ció andrebbe contro l'idea del progetto stesso, visto che dovrebbe essere possibile cambiare storia e lista delle stanze a piacimento, di conseguenza il salvataggio verrebbe corrotto e non sarebbe piú possibile continuare la storia giá iniziata.<br>
L'idea é quindi quella di salvare nelle tabelle ogni variabile definita anche nel JSON cosí da poter essere caricata in modo indipendente.

Le tabelle sono molte e seguono per la maggior parte la struttura delle classi del package Component, di conseguenza vedremo in dettaglio solo alcuni esempi.

Nella tabella `PLAYABLEROOM` vengono salvati:
- ID;
- Nome;
- Descrizione;
- Percorso dell'immagine;
- Se é mutabile;
- L'ID della stanza in cui mutare;
- ID di ogni direzione;
- Se é buia di natura.

Nella tabella `FILLABLEITEM` vengono salvati:
- ID;
- Nome;
- Descrizione;
- ID della stanza in cui si trova (se é in una stanza);
- ID del contenitore in cui si trova (se é in un contenitore);
- Se si trova nell'inventario;
- Se é pieno;
- ID dell'oggetto di cui si riempie.

Nella tabella `OBJECTEVENT` vengono salvati:
- ID dell'oggetto a cui appartiene;
- ID della stanza in cui l'oggetto si trova (per distinguere gli stadi delle stanze mutabili);
- Tipo evento;
- Testo;
- Se dovrebbe mutare la stanza madre al trigger;
- ID della stanza bersaglio da mutare al trigger (se c'é);
- ID della stanza in cui teletrasportare il giocatore al trigger (se c'é);
- Se dovrebbe distruggere l'oggetto padre al trigger.

Esistono poi tabelle ausiliari come `ALIAS` che memorizza per ogni ID (entitá) il suo alias, `ACTIONSCOUNTER` che memorizza il valore attuale del numero di azioni, `CURRENTROOM` che memorizza l'ID della stanza attuale e di quella precedente, `HUMANPHRASES` che memorizza per ogni ID (entitá) una frase (in ordine) e `REQUIREDWEAREDITEM` che memorizza per ogni ID (entitá) di che oggetto indossato ha bisogno per interagirci.

## 3.3 API REST - AccuWeather
Il gioco prevede un controllo in tempo reale in base alla posizione reale (tramite IP) del meteo, per decidere se riprodurre il sottofondo da pioggia o normale.
In caso di connessione assente, verrá usato il valore di default, ovvero sottofondo normale.<br>
Il check viene effettuato ogni 30 minuti a partire dall'avvio del gioco e prevede i seguenti passaggi:
1. Ricava l'IP tramite un sito internet che fornisce il servizio. (eseguito solo all'avvio del gioco)
2. Tramite l'API per le `locationKey` via IP verrá fatta una GET request passandogli l'IP appena ricavato. In caso di successo, salverá la chiave ricavata, altrimenti userá la chiave di default (BARI). (eseguito solo all'avvio del gioco)
3. Tramite l'API per le condizioni atmosferiche verrá fatta una GET request passandogli la locationKey ricavata e verrá salvato in memoria il JSON di risposta.
4. Viene fatto il parsing del JSON e viene ricercato il campo di interesse `HasPrecipitation` che verrá restituito al chiamante della funzione.

## 3.4 Reflection - mutabilitá delle stanze
Come giá visto, le `MutableRoom` prevedono una `newRoom` da cui andare a prendere eventuali campi da aggiornare, ció é reso possibile grazie alla reflection.
Ogni volta che viene richiesto ad una stanza di mutare, la procedura é la seguente:
- Per ogni campo non `null` e non `id` della classe definita da newRoom (Playable o Mutable)
- se il campo non é una lista, allora possiamo aggiornare il campo della stanza madre con quello nuovo, altrimenti
- se il campo é una lista, allora é la lista degli oggetti nuovi e bisogna verificare se va semplicemente aggiunto (se l'ID dell'oggetto non é giá presente) o sovrascritto a un oggetto giá presente nella stanza madre. In ogni caso, viene aggiornato il campo dell'oggetto `closestRoomParent` per far corrispondere la nuova stanza (es. da "15a" diventa "15").
- finito il ciclo dei campi, si controlla se c'é un altro grado di `newRoom`, altrimenti si setta a `null`. 

## 3.5 Swing - MainFrame
Il `MainFrame` é una classe che estende il `JFrame` e rappresenta l'intera GUI di gioco.
La sua dimensione é fissa (881 x 852) per evitare problemi di proporzionamento con le immagini user-defined (vengono ridimensionate a 581 x 300).

Contiene diversi componenti organizzati su un `BorderLayout`:
- `pnlHeader`, la top-bar che contiene
    - `lblRoomName`, il label contenente il nome della stanza;
    - `lblActions`, il label contenente il contatore delle azioni.
- `pnlInOut`, il pannello che contiene
    - `txtPane`, pannello di testo (output)
    - `txtInput`, box di testo (input)
- `pnlCompass`, pannello che contiene
    - `pnlCompassNorth`, pannello che contiene
        - `lblCompassNorthImage`, label contenente l'immagine;
        - `lblCompassNorthText`, label ruotato contenente "N".
    - `pnlCompassSouth`, pannello che contiene
        - `lblCompassSouthImage`, label contenente l'immagine;
        - `lblCompassSouthText`, label ruotato contenente "S".
    - ...
- `lypRoomImage`, pannello a livelli che contiene, in ordine
    - `noisePanel`, pannello per l'animazione simil-vecchia TV (top)
    - `lblRoomImage`, label per l'immagine della stanza (bottom)
    
# 4 Come impostare la propria storia
Il file `rooms.json` nella cartella `resources` é ció che rende personalizzabile la storia.

Il formato é piuttosto semplice:
<pre>[
    {
        "type": "TipoStanza",
        "id": "1",
        "name": "nome"
    },
    {
        "type": "TipoStanza",
        "id": "2",
        ...
        "objects": 
            [
                {
                    "type": "TipoOggetto",
                    "id": "2",
                    ...
                }
            ]
    }
]</pre>

Per ogni oggetto o stanza é obbligatorio specificare il tipo (la classe) e l'id.
La prima stanza impostata nella lista, sará quella di partenza del gioco.
Se una variabile é una lista (a livello di classe) allora vengono usate le parentesi quadre e ogni elemento sará racchiuso da parentesi graffe.
Se una variabile non viene impostata, verrá considerata automaticamente `null`.

Variabili comuni a tutte le stanze ed entitá:
- `type`: nome della classe (case-sensitive), stringa
- `id`: identificatore, stringa
- `name`: nome, stringa
- `description`: descrizione stampata al momento dell'arrivo (se é una stanza), o all'esaminazione (se é un oggetto).

Verranno illustrate di seguito tutte le possibili classi con descrizione esaustiva delle variabili e del loro uso.

## 4.1 Stanze
Variabili comuni a tutte le classi:
- `imgPath`: percorso dell'immagine di riferimento, stringa

### 4.1.1 PlayableRoom
Stanza giocabile, eventualmente con degli oggetti e collegata ad altre stanze (10 direzioni).
- `northId`: identificatore della stanza situata a nord, stringa
- `southId`: identificatore della stanza situata a sud, stringa
- `eastId`: identificatore della stanza situata a est, stringa
- `westId`: identificatore della stanza situata a ovest, stringa
- `northEastId`: identificatore della stanza situata a nord-est, stringa
- `northWestId`: identificatore della stanza situata a nord-ovest, stringa
- `southEastId`: identificatore della stanza situata a sud-est, stringa
- `southWestId`: identificatore della stanza situata a sud-ovest, stringa
- `upId`: identificatore della stanza situata al piano superiore, stringa
- `downId`: identificatore della stanza situata al piano inferiore, stringa
- `objects`: lista degli oggetti contenuti nella stanza, lista di entitá
- `event`: evento da eseguire all'arrivo nella stanza, RoomEvent
- `darkByDefault`: se la stanza dovrebbe essere buia senza una fonte di luce attiva, booleano

### 4.1.2 MutableRoom
Stanza giocabile, eredita dalla precedente tutte le variabili giá viste, in piú prevede la possibilitá di mutare in seguito ad un evento di un qualsiasi oggetto.
- `newRoom`: la stanza in cui mutare, PlayableRoom/MutableRoom

Nella `newRoom` al contrario delle altre occasioni, é necessario soltanto specificare le variabili di interesse, l'engine semplicemente aggiornerá la stanza principale con i nuovi valori specificati.

In caso di aggiornamento della lista di oggetti, se si vuole sovrascrivere un oggetto giá presente, basterá usare lo stesso id, se invece si vuole semplicemente aggiungere un oggetto alla stanza basterá usare un id diverso.

### 4.1.3 CutsceneRoom
Stanza non giocabile, prevede un input del giocatore (INVIO) al termine dell'output della descrizione.
- `nextRoomId`: l'identificatore della stanza successiva, stringa

## 4.2 Entitá
Variabili comuni a tutte le classi:
- `alias`: lista di nomi alternativi, lista di stringhe
- `events`: lista di eventi, lista di ObjectEvent
- `requiredWearedItemsIdToInteract`: lista di indossabili richiesti per interagire (l'azione Esamina é l'unica possibile anche con i requisiti non soddisfatti), lista di IWearable
- `failedInteractionMessage`: messaggio da stampare quando si interagisce ma non si hanno addosso gli indossabili richiesti, stringa

### 4.2.1 BasicObject
Oggetto la cui unica azione possibile é "Esamina".

Non si puó prendere nell'inventario (tutte le entitá con "Object" nel nome indicano entitá che non si possono raccogliere).

### 4.2.2 FireObject
Oggetto che rappresenta delle fiamme e su cui é possibile eseguire l'azione "Versa" (es. "butta acqua sul fuoco").
- `lit`: se di default il fuoco é acceso o meno, booleano

### 4.2.3 MovableObject (*IMovable*)
Oggetto che puó essere spostato ("Sposta).

### 4.2.4 PullableObject (*IPullable*)
Oggetto che puó essere tirato ("Tira").

### 4.2.5 PushableObject (*IPushable*)
Oggetto che puó essere premuto ("Premi").

### 4.2.6 BasicItem
Oggetto che puó essere preso nell'inventario ("Prendi").

Tutte le entitá con "Item" nel nome indicano entitá che si possono raccogliere.

### 4.2.7 FillableItem (*IFillable*)
Oggetto che puó essere riempito ("Prendi liquido");
- `eligibleItemId`: identificatore del liquido di cui l'oggetto si puó riempire, FluidItem
- `filled`: se di default é giá pieno o meno, booleano

### 4.2.8 FluidItem (*IFluid*)
Oggetto che rappresenta il liquido, puó essere preso nell'inventario attraverso un FillableItem dedicato ("Prendi").

### 4.2.9 LightSourceItem (*ISwitch*)
 Oggetto che rappresenta una fonte di luce, puó essere acceso ("Accendi") e spento ("Spegni"), illumina le stanze buie.
 - `requiredItemId`: l'eventuale oggetto nell'inventario che serve per accenderla, qualsiasi Item, stringa
 
 ### 4.2.10 ReadableItem (*IReadable*)
 Oggetto leggibile ("Leggi").
 - `readText`: testo da stampare, stringa
 
 ### 4.2.11 WearableItem (*IWearable*)
 Oggetto indossabile ("Indossa").
 
 ### 4.2.12 Door (*IOpenable*)
 Oggetto rappresentante una porta che puó essere aperta ("Apri" e "Apri con chiave").
 - `open`: se di default é aperta, booleano
 - `locked`: se di default é chiusa a chiave, booleano
 - `unlockedWithItemId`: in caso sia chiusa a chiave, con che item ID si dovrebbe sbloccare, stringa
 - `blockedRoomId`: l'identificatore della stanza di cui blocca il passaggio, stringa
 
 ### 4.2.13 UnopenableDoor (*IOpenable*)
 Oggetto rappresentante una porta, ma che non puó essere aperta in alcun modo e stampa un messaggio personalizzato in caso l'utente provi ad aprirla.
 - `openEventText`: messaggio da stampare in caso di prova di apertura, stringa
 
 ### 4.2.14 InvisibleWall
 Questa entitá é utilizzabile quando si vuole rendere una zona inaccessibile, con un messaggio personalizzato quando si prova ad oltrepassarla, e che eventualmente sia anche sbloccabile tramite indossabili (da usare in congiunzione con requiredWearedItemsIdToInteract).
 - `locked`: se dovrebbe bloccare tentativi di movimento verso la direzione bloccata, booleano
 - `trespassingWhenLockedText`: messaggio da stampare quando il tentativo di movimento fallisce, stringa
 - `blockedRoomId`: identificatore della stanza bloccata dal muro invisibile
 - `northBlocked`: se la direzione nord é bloccata dal muro invisibile, booleano
 - `southBlocked`: se la direzione sud é bloccata dal muro invisibile, booleano
 - `eastBlocked`: se la direzione est é bloccata dal muro invisibile, booleano
 - `westBlocked`: se la direzione ovest é bloccata dal muro invisibile, booleano
 - `northEastBlocked`: se la direzione nord-est é bloccata dal muro invisibile, booleano
 - `northWestBlocked`: se la direzione nord-ovest é bloccata dal muro invisibile, booleano
 - `southEastBlocked`: se la direzione sud-est é bloccata dal muro invisibile, booleano
 - `southWestBlocked`: se la direzione sud-ovest é bloccata dal muro invisibile, booleano
 
 `blockedRoomId` va usato se esiste una stanza bloccata, mentre i flag sottostanti vanno usati se sono stanze false.
 
 ### 4.2.15 Human (*ITalkable*)
 Questa entitá rappresenta un umano a cui si puó parlare ("Parla").
 - `phrases`: coda di frasi che la persona dirá, coda di stringhe
 
 Una volta finite le frasi, il messaggio sará sempre "..."
 
 ### 4.2.16 BasicContainer
 Oggetto la cui unica azione possibile é "Esamina" e che puó contenere a sua volta degli oggetti.
 
 Tutte le entitá con "Container" nel nome indicano entitá che hanno un inventario al loro interno ed ereditano le seguenti variabili:
 - `list`: oggetti al suo interno, lista di entitá
 - `forFluids`: se é un contenitore riservato ai liquidi, booleano
 
**N.B.** Gli oggetti possono anche essere inseriti dal giocatore al suo interno (es. da inventario a contenitore).
 
 ### 4.2.17 ChestlikeContainer (*IOpenable*)
 Contenitore che puó essere aperto come una cassa ("Apri").
 - `open`: se di default é aperta, booleano
 - `locked`: se di default é chiusa a chiave, booleano
 - `unlockedWithItemId`: in caso sia chiusa a chiave, con che item ID si dovrebbe sbloccare, stringa
 
 ### 4.2.18 SocketlikeContainer
 Contenitore per un singolo oggetto definito a priori (come incastonatura).
 - `eligibleItemId`: identificatore dell'oggetto che puó essere inserito, stringa
 - `itemInside`: se l'oggetto é inserito di default, booleano.
 
 ### 4.2.19 WearableContainer (*IWearable*)
 Contenitore che si puó raccogliere e indossare.
 - `maxSlots`: numero massimo di oggetti che puó contenere, intero
 
 ## 4.3 Eventi
 Variabili comuni a tutte le classi:
 - `eventType`: quando é triggerabile, tipo di evento (EventType)
 - `text`: messaggio da stampare quando viene eseguito, stringa
 
 ### 4.3.1 RoomEvent
 Evento della stanza, viene eseguito quando il giocatore giunge per la prima volta nella stanza.
 
 Allo stato attuale `eventType` é inutile per questo evento.
 
 ### 4.3.2 ObjectEvent
 Evento dell'oggetto, viene eseguito nel momento specificato in `eventType`.
 - `updatingParentRoom`: se deve mutare la stanza in cui l'oggetto é contenuto (assicurarsi che la stanza sia Mutable), booleano
 - `updateTargetRoomId`: identificatore della stanza da mutare (assicurarsi che la stanza sia Mutable), se deve mutarla, stringa
 - `teleportsPlayerToRoomId`: identificatore della stanza in cui teletrasportare il giocatore, se deve, stringa
 - `destroyOnTrigger`: se deve distruggere l'oggetto dell'evento dopo che é stato eseguito, booleano
 
 ### 4.3.3 EventType
 - `LOOK_AT`: comando "Esamina"
 - `PICK_UP`: comando "Prendi"
 - `OPEN_CONTAINER`: comando "Apri" su una cassa (non chiusa a chiave)
 - `OPEN_UNLOCKED`: comando "Apri" su una porta (non chiusa a chiave)
 - `OPEN_LOCKED`: comando "Apri" su un qualsiasi IOpenable chiuso a chiave
 - `PUSH`: comando "Premi"
 - `PULL`: comando "Tira"
 - `MOVE`: comando "Sposta"
 - `INSERT`: comando "Inserisci" su un contenitore
 - `EXTINGUISH`: comando "Versa" (es. "Butta acqua su fuoco")
 - `TALK_WITH`: comando "Parla"
 - `WEAR`: comando "Indossa"
 - `UNWEAR`: comando "Togli"
 - `READ`: comando "Leggi"
 - `TURN_ON`: comando "Accendi"
 - `TURN_OFF`: comando "Spegni"
 
 ## 5 Specifica algebrica
 Si riporta la specifica algebrica della struttura dati `Multimap` del package `com.google.common.collect`, utilizzata nella classe Entities per ricavare tutti i possibili oggetti contenuti in una stanza, anche quelli con lo stesso ID.
 
 Le operazioni sono state adattate per semplificare la scrittura della specifica.
 
 ### 5.1 Specifica sintattica
 sorts: Multimap, AbstractEntity, String, boolean, Collection
 
 Nota: é definita l'operazione getId(AbstractEntity) -> String
 
 operations:
 
 [COSTRUTTORI]
 - create() -> Multimap
 - put(Multimap, AbstractEntity) -> Multimap
 
 [OSSERVATORI]
 - get(Multimap, String) -> Collection
 - remove(Multimap, String, AbstractEntity) -> Multimap
 - removeAll(Multimap, String) -> Multimap
 - containsKey(Multimap, String) -> boolean
 - isEmpty(Multimap) -> boolean
 
 ### 5.2 Specifica semantica
 declare m: Multimap, e: AbstractEntity, c: Collection
 
 Nota: la collection restituita dall'operazione get contiene tutte le coppie chiave-valore trovate
 
 - get(put(m, e), getId(e)) = c
 - remove(put(m,e), getId(e), e) = m
 - removeAll(put(m,e), getId(e)) = m
 - containsKey(put(m,e), getId(e)) = true
 - containsKey(create(), getId(e)) = false
 - isEmpty(put(m,e)) = false
 - isEmpty(create()) = true 
 
 ### 5.3 Specifica di restrizione
 - get(create(), getId(e)) = ERRORE
 - remove(create(), getId(e), e) = ERRORE
 - removeAll(create(), getId(e)) = ERRORE
 
 ## 6. Diagrammi UML
Si riportano il diagramma del package `component` e i relativi diagrammi delle classi.

### 6.1 Diagramma del package
<p align="center">
<img src="docs/img/diagrams/PackageDiagramComponent.svg">
</p>

### 6.2 Diagrammi delle classi "entity"
<p align="center">
<img src="docs/img/diagrams/object.svg">
<img src="docs/img/diagrams/pickupable.svg">
<img src="docs/img/diagrams/humanoid.svg">
<img src="docs/img/diagrams/doorlike.svg">
<img src="docs/img/diagrams/container.svg">
</p>

### 6.3 Diagrammi delle classi "room"
<p align="center">
<img src="docs/img/diagrams/room.svg">
</p>

### 6.4 Diagrammi delle classi "event"
<p align="center">
<img src="docs/img/diagrams/event.svg">
</p>