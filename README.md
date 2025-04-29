# Belsign – 1. års eksamensprojekt (Datamatiker)

**Belsign** er et eksamensprojekt udviklet på datamatikeruddannelsen ved Erhvervsakademi SydVest i Esbjerg i samarbejde med **Belman A/S** – en dansk virksomhed specialiseret i ekspansionsfuger og fleksible rørløsninger til bl.a. energi-, offshore- og kemikalieindustrien.

## 📌 Formål

Formålet med projektet er at digitalisere og forbedre Belmans foto-dokumentationsproces i produktionen. I dag håndteres billeder manuelt og lagres i store mapper – en løsning, der er både uoverskuelig og ineffektiv. Belsign tilbyder en moderne, tabletvenlig applikation, hvor produktionsmedarbejdere nemt kan:

- Tage billeder og tilknytte dem til en specifik ordre
- Få billeder godkendt af kvalitetskontrollen
- Generere og sende rapporter direkte til kunden
- Arbejde med forskellige brugerroller og rettigheder

## 🧱 Arkitektur

Systemet er opbygget efter en **trelagsarkitektur**:
- **GUI** (Præsentationslag): Brugervenligt og tabletvenligt interface
- **BLL** (Forretningslogik): Håndtering af databehandling og regler
- **DAL** (Dataadgangslag): Kommunikation med en **Microsoft SQL Server** database

Projektet er udviklet med **Scrum** som agil udviklingsmetode, med Belman som produkt ejer (PO).

## 🚀 Teknologier

Projektet er bygget med følgende teknologier:

- **JavaFX** – GUI-udvikling
- **Gluon Mobile** – Platform til mobilvenlige Java-applikationer
- **AlatnaFX** – Design og UI-komponenter
- **GraalVM** – Cross-compilation og native builds

## 📷 Funktionalitet

- 📸 Billeddokumentation
- 📂 Ordretilknytning
- 👤 Brugerroller og adgangsstyring
- 📤 Automatisk rapportgenerering og e-mail afsendelse
- ✅ Godkendelsesflow for kvalitetssikring

## 📁 Projektstatus

Projektet er under aktiv udvikling som en del af 1. års eksamensopgaven og afspejler en prototype rettet mod implementering i en reel virksomhedskontekst.

## 📬 Kontakt

Projektet vedligeholdes som en del af mit studie. Kontakt via [GitHub-profilen her]([https://github.com/](https://github.com/)).

---

> *Dette projekt er skabt udelukkende til undervisningsbrug og er ikke en officiel softwareudgivelse fra Belman A/S.*
