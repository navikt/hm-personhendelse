# hm-personhendelse

App som lytter på Kafka-emner for personhendelser (leesah og skjermede-personer-status)
og publiserer meldinger videre på DigiHoTs rapid. Appen er implementert med Kafka Streams.

```mermaid
flowchart TD
    pdl.leesah-v1 --> ADRESSEBESKYTTELSE_V1 --> hm-personhendelse
    pdl.leesah-v1 --> DOEDSFALL_V1 --> hm-personhendelse
    nom.skjermede-personer-status-v1 --> id1[FNR / Boolean] --> hm-personhendelse
    hm-personhendelse --> teamdigihot.hm-soknadsbehandling-v1
```
