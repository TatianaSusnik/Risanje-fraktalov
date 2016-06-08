# Risanje-fraktalov
Projekt pri predmetu Programiranje 2

Program, ki rise Juliajeve mnozice in Mandelbrotovo mnozico.

Uporabnik lahko izbere parametre s katerimi je fraktal določen.

Možne razširitve:
* bolj napreden in hitrejši postopek risanja fraktala, 
* ob kliku na Mandelbrotovo mnozico se odpre novo okno s pripadajoco Juliajevo mnozico,
* uporabnik lahko interaktivno povečuje podrobnosti v sliki, 
* uporabnik lahko izbere barvno paleto, s katero je narisan fraktal, 
* izvoz slike v datoteko.

Vsebina repozitorija:
* `Aplikacija`: program, ki zažene okno
* `Okno`: glavno okno
* `Platno`: računanje slike in izris fraktala
* `DodatnoOkno`: manjše okno, ki vsebuje le izbrano Juliajevo množico
* `MiniPlatno`: računanje slike in izris fraktala na DodatnoOkno
* `Complex`: definicija kompleksnih števil

Navodila za uporabo:

Aplikacijo zaženemo z ukazom `java APlikacija`. 

Ob zagonu se izriše Juliajeva množica, ki pripada konstanti c = 0.
Uporabnik lahko izbere med Juliajevo in Mandelbrotovo množico, spremeni maksimalno število iteracij, izbere eno od sedmih barvnih možnosti in v primeru, da je izbrana Juliajeva množica, nastavi konstanto c.
S klikom na gumb `Narisi` ali s pritiskom tipke ENTER se izriše fraktal z izbranimi parameti.


Z levim klikom na sliko, se fraktal poveča, z desnim klikom pa pomanjša. Če so bili parametri pred klikom spremenjeni,
se spremembe upoštevajo pri približevanju/oddaljevanju.
Če je izrisana Mandelbrotova množica, lahko uporabnik izbira kaj se bo zgodilo ob kliku. Poleg povečevanja/pomanjševanja lahko izbere, da se ob kliku odpre novo manjše okno na katerem je narisana Juliajeva množica, ki za konstanto c vzame točko, kjer je bil klik.
Novo okno vsebuje gumb `Povecaj`. S pritiskom na ta gumb se fraktal iz novega okna izriše na glavno okno, novo okno se zapre in nastavi se nova konstanta.

Uporabnik lahko kadarkoli shrani sliko na glavnem oknu s klikom na gumb `Shrani`.
