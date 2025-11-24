#  Service Bancaire Distribué avec CORBA

[![Java](https://img.shields.io/badge/Java-1.8-orange.svg)](https://www.oracle.com/java/)
[![CORBA](https://img.shields.io/badge/Middleware-CORBA-blue.svg)](https://www.omg.org/corba/)
[![JNDI](https://img.shields.io/badge/Registry-JNDI-green.svg)](https://docs.oracle.com/javase/8/docs/technotes/guides/jndi/)
[![License](https://img.shields.io/badge/License-Academic-yellow.svg)]()

> **Application distribuée client-serveur** implémentant un système de gestion de comptes bancaires via CORBA (Common Object Request Broker Architecture) avec service de nommage JNDI.

---

##  Table des matières

- [ Vue d'ensemble](#-vue-densemble)
- [ Fonctionnalités](#-fonctionnalités)
- [ Architecture](#️-architecture)
- [ Technologies](#-technologies)
- [ Installation et Exécution](#-installation-et-exécution)
- [ Particularités d'implémentation](#-particularités-dimplémentation)
- [ Tests et Résultats](#-tests-et-résultats)
- [ Concepts CORBA illustrés](#-concepts-corba-illustrés)
- [ Structure du projet](#-structure-du-projet)
- [ Prérequis et Limitations](#️-prérequis-et-limitations)
- [ Contribution](#-contribution)

---

##  Vue d'ensemble

Cette application démontre l'utilisation du middleware CORBA pour créer un service bancaire distribué permettant à plusieurs clients de gérer des comptes à distance. Le système utilise **JNDI (Java Naming and Directory Interface)** comme service d'annuaire, offrant une alternative élégante au Naming Service CORBA traditionnel.

### Cas d'usage

-  **Gestion de comptes** : Création, consultation et modification
-  **Opérations bancaires** : Versements et retraits avec validation
-  **Conversion de devises** : Euro (€) vers Dinar Tunisien (DT)
-  **Consultation globale** : Vue d'ensemble de tous les comptes

---

##  Fonctionnalités

### Opérations disponibles

| Opération | Description | Paramètres |
|-----------|-------------|------------|
| `creerCompte` | Crée un nouveau compte bancaire | `Compte(code, solde)` |
| `verser` | Dépose de l'argent sur un compte | `montant, code` |
| `retirer` | Retire de l'argent d'un compte | `montant, code` |
| `getCompte` | Consulte un compte spécifique | `code` |
| `getComptes` | Liste tous les comptes | - |
| `conversion` | Convertit € → DT (taux: 3.3) | `montant` |

### Validations métier

-  Unicité des codes de compte
-  Vérification du solde avant retrait
-  Messages d'erreur explicites
-  Traçabilité des opérations (logs serveur)

---

##  Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                    Architecture CORBA                        │
└─────────────────────────────────────────────────────────────┘

┌──────────────┐          ┌──────────────┐          ┌──────────────┐
│              │          │              │          │              │
│   CLIENT     │◄────────►│  JNDI + ORB  │◄────────►│   SERVEUR    │
│              │   IIOP   │   (Port 900) │   Local  │              │
│ BanqueClient │          │  tnameserv   │          │ BanqueServer │
│              │          │              │          │              │
└──────────────┘          └──────────────┘          └──────────────┘
       │                         │                         │
       │                         │                         │
   [Stub]                  [Annuaire]                 [Servant]
       │                    "BK" → ref                     │
       │                         │                    [BanqueImpl]
       │                         │                         │
       └─────────────────────────┴─────────────────────────┘
                     Transparence de localisation
```

### Flux d'une requête

1. **Client** : Appel de méthode sur le stub `stub.verser(500, 1001)`
2. **Stub** : Marshalling des paramètres (sérialisation)
3. **ORB Client** : Envoi via IIOP (Internet Inter-ORB Protocol)
4. **ORB Serveur** : Réception et unmarshalling
5. **POA** : Routage vers le servant `BanqueImpl`
6. **Servant** : Exécution métier `compte.solde += 500`
7. **Retour** : Marshalling du résultat et envoi au client
8. **Stub** : Unmarshalling et retour à l'appelant

---

## 🔧 Technologies

- **Java 1.8** (JDK 8) - Obligatoire pour CORBA
- **CORBA** - Middleware de communication
- **JNDI** - Service d'annuaire Java
- **IDL** - Langage de définition d'interface
- **IIOP** - Protocole de communication
- **ORB** - Object Request Broker
- **POA** - Portable Object Adapter

---

##  Installation et Exécution

### Prérequis

```bash
# Vérifier la version de Java
java -version
# Doit afficher : java version "1.8.0_xxx"

# Vérifier la disponibilité de idlj
idlj
# Doit afficher l'aide de l'outil

# Vérifier la disponibilité de tnameserv
tnameserv -help
```

### Étape 1 : Cloner le projet

```bash
git clone https://github.com/Hsan-Kh/TP5-DAR2025
cd KhecharemHsan_LSI3_DevAppReparties_TP5
```

### Étape 2 : Compiler l'IDL

**Pour le serveur :**
**(A faire si vous n'avez pas le package corbaBanque)**
```bash
cd TP5_CorbaBanque_Server/src
idlj -fall -v Banque.idl
```

**Pour le client :**
**(A faire si vous n'avez pas le package corbaBanque)**
```bash
cd TP5_CorbaBanque_Client/src
idlj -fall -v Banque.idl 
```



### Étape 3 : Lancer l'application

** Ordre d'exécution OBLIGATOIRE :**

#### 1️ Démarrer le service de nommage
```bash
tnameserv 
```
>  Laissez cette fenêtre ouverte pendant toute la session

#### 2️ Démarrer le serveur
```bash
cd TP5_CorbaBanque_Server
java -cp bin corbaServer.BanqueServer
```
>  Attendez le message : "Serveur Banque prêt et en attente de requêtes..."

#### 3️ Lancer le client
```bash
cd TP5_CorbaBanque_Client
java -cp bin corbaClient.BanqueClient
```
>  Les tests s'exécutent automatiquement !

---

##  Particularités d'implémentation

###  Innovation 1 : JNDI au lieu du Naming Service CORBA

**Approche traditionnelle (CosNaming) :**
```java
//  Approche standard CORBA
org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
NamingContextExt nameContext = NamingContextExtHelper.narrow(objRef);
NameComponent[] path = nameContext.to_name("BanqueService");
nameContext.rebind(path, banqueRef);
```

**Notre approche (JNDI) :**
```java
//  Notre approche simplifiée avec JNDI
Context ctx = new InitialContext();
ctx.rebind("BK", rootPOA.servant_to_reference(banqueImpl));
```

**Avantages :**
-  **Code plus concis** : 2 lignes au lieu de 4+
-  **API Java standard** : Pas de classes CORBA spécifiques
-  **Plus flexible** : JNDI peut utiliser différents backends
-  **Meilleure intégration** : Compatible avec les applications Java EE

###  Innovation 2 : Gestion avancée des erreurs

**Code service avec messages détaillés :**
```java
if (c.solde >= mt) {
    c.solde -= mt;
    System.out.println(" Retrait de " + mt + " du compte " + code);
    System.out.println("   Nouveau solde: " + c.solde);
} else {
    System.out.println(" ERREUR : Solde insuffisant pour le compte " + code);
}
```

**Bénéfices :**
-  **Débogage facilité** : Messages clairs côté serveur
-  **Audit trail** : Traçabilité de toutes les opérations
-  **Validation robuste** : Vérifications avant modification

###  Innovation 3 : Architecture modulaire

```
TP5_CorbaBanque_Server/
├── src/
│   ├── Banque.idl              # Interface IDL
│   ├── jndi.properties         # Configuration JNDI
│   ├── corbaBanque/            # Code généré (stubs/skeletons)
│   ├── service/                # Couche métier
│   │   └── BanqueImpl.java     #    Logique bancaire isolée
│   └── corbaServer/            #    Couche distribution
│       └── BanqueServer.java   #    Infrastructure CORBA
```

**Séparation des responsabilités :**
-  **service/** : Logique métier pure (réutilisable)
-  **corbaServer/** : Infrastructure CORBA (remplaçable)
-  **Couplage faible** : Possibilité de changer le middleware

---

##  Tests et Résultats

### Suite de tests automatiques

Le client exécute 7 tests couvrant tous les cas d'usage :

```
 Test 1 : Création de 3 comptes (codes: 1001, 1002, 1003)
 Test 2 : Consultation d'un compte spécifique
 Test 3 : Versement de 500€ (solde: 1000€ → 1500€)
 Test 4 : Retrait de 300€ (solde: 2500€ → 2200€)
 Test 5 : Tentative de retrait avec solde insuffisant (gestion d'erreur)
 Test 6 : Conversion de devises (100€ → 330 DT)
 Test 7 : Consultation de tous les comptes (liste complète)
```

### Exemple de sortie console

**Console Serveur :**
```
Initialisation de l'ORB...
Récupération du POA...
Création du servant...
Serveur Banque prêt et en attente de requêtes...
Compte créé: Code=1001, Solde=1000.0
Compte créé: Code=1002, Solde=2500.0
Compte créé: Code=1003, Solde=500.0
Consultation compte 1001: Solde=1000.0
Versement de 500.0 sur compte 1001
Nouveau solde: 1500.0
```

**Console Client :**
```
=== Test 1: Création de comptes ===
3 comptes créés avec succès!

=== Test 2: Consultation d'un compte ===
Compte 1001 - Solde: 1000.0€

=== Tests terminés avec succès! ===
```

---

##  Concepts CORBA illustrés

### 1. IDL (Interface Definition Language)

```idl
module corbaBanque {
    struct Compte {
        long code;
        float solde;
    };
    
    interface IBanqueRemote {
        void creerCompte(in Compte cpte);
        void verser(in float mt, in long code);
        // ...
    };
};
```

**Principe** : Définition neutre de l'interface, indépendante du langage.

### 2. Marshalling / Unmarshalling

**Marshalling** : Sérialisation des paramètres Java → Flux binaire IIOP
**Unmarshalling** : Désérialisation du flux IIOP → Objets Java

```java
// Côté client (marshalling automatique)
stub.verser(500.0f, 1001);  // float et int → bytes

// Côté serveur (unmarshalling automatique)
public void verser(float mt, int code) {  // bytes → float et int
    // ...
}
```

### 3. Transparence de localisation

```java
// Le client appelle comme si c'était local
Compte c = stub.getCompte(1001);

// Mais en réalité :
// 1. Appel sur stub local
// 2. Marshalling
// 3. Envoi réseau via IIOP
// 4. Exécution distante sur le serveur
// 5. Retour du résultat
// 6. Unmarshalling
// 7. c reçoit l'objet Compte
```

### 4. POA (Portable Object Adapter)

```java
POA rootPOA = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
rootPOA.the_POAManager().activate();

// Le POA gère :
// - Création des servants
// - Mapping object reference ↔ servant
// - Cycle de vie des objets
// - Routage des requêtes
```

### 5. Service de nommage (JNDI)

```java
// Enregistrement (serveur)
Context ctx = new InitialContext();
ctx.rebind("BK", reference);  // Publication

// Recherche (client)
Object ref = ctx.lookup("BK");  // Découverte
IBanqueRemote stub = IBanqueRemoteHelper.narrow((org.omg.CORBA.Object)ref);
```

---

##  Structure du projet

```
TP5-CORBA-Banque/
│
├── TP5_CorbaBanque_Server/
│   ├── src/
│   │   ├── Banque.idl                      # Interface IDL
│   │   ├── jndi.properties                 # Config JNDI (port 900)
│   │   │
│   │   ├── corbaBanque/                    # Généré par idlj
│   │   │   ├── Compte.java
│   │   │   ├── CompteHelper.java
│   │   │   ├── CompteHolder.java
│   │   │   ├── IBanqueRemote.java
│   │   │   ├── IBanqueRemoteHelper.java
│   │   │   ├── IBanqueRemoteHolder.java
│   │   │   ├── IBanqueRemoteOperations.java
│   │   │   ├── IBanqueRemotePOA.java       # Skeleton serveur
│   │   │   └── _IBanqueRemoteStub.java
│   │   │
│   │   ├── service/                        # Logique métier
│   │   │   └── BanqueImpl.java             # Implémentation servant
│   │   │
│   │   └── corbaServer/                    # Infrastructure
│   │       └── BanqueServer.java           # Point d'entrée serveur
│   │
│   └── TP5_CorbaBanque_Server.iml
│
├── TP5_CorbaBanque_Client/
│   ├── src/
│   │   ├── Banque.idl                      # Copie du serveur
│   │   ├── jndi.properties                 # Copie du serveur
│   │   │
│   │   ├── corbaBanque/                    # Généré par idlj
│   │   │   └── (mêmes fichiers)
│   │   │
│   │   └── corbaClient/                    # Application cliente
│   │       └── BanqueClient.java           # Point d'entrée client
│   │
│   └── TP5_CorbaBanque_Client.iml
│
├── README.md                               # Ce fichier
└── .gitignore
```

---

##  Prérequis et Limitations

### Prérequis strictes

-  **JDK 1.8 obligatoire** : CORBA retiré après Java 8
-  **Port 900 disponible** : Nécessaire pour tnameserv
-  **Ordre d'exécution** : tnameserv → Serveur → Client

### Limitations connues

-  **Pas de persistance** : Données en mémoire (perdues au redémarrage)
-  **Mono-serveur** : Un seul serveur à la fois
-  **Pas d'authentification** : Tous les clients ont accès complet
-  **Pas de transactions** : Pas de rollback en cas d'erreur
-  **CORBA legacy** : Technologie dépassée (remplacée par REST, gRPC)

### Améliorations possibles

```
 Évolutions futures :
├──  Base de données (PostgreSQL, MySQL)
├──  Authentification (utilisateurs, rôles)
├──  Transactions ACID
├──  Interface graphique (JavaFX, Swing)
├──  API REST alternative
├──  Containerisation (Docker)
└──  Déploiement cloud
```

---

##  Contribution

### Auteur

**Hsan Khecharem**
-  LSI3 - Développement d'applications réparties
-  Email: khecharemhsan@gmail.com
-  GitHub: https://github.com/Hsan-Kh

### Ressources

-  [Documentation CORBA](https://www.omg.org/spec/CORBA/)
-  [Java IDL Documentation](https://docs.oracle.com/javase/8/docs/technotes/guides/idl/)
-  [JNDI Tutorial](https://docs.oracle.com/javase/tutorial/jndi/)

### Licence

Projet académique - Tous droits réservés © 2025

---

##  Conclusion

Ce projet illustre les fondamentaux des **systèmes distribués** en utilisant CORBA comme middleware de communication. Bien que CORBA soit une technologie des années 1990 aujourd'hui remplacée par des solutions plus modernes (REST, gRPC, microservices), les concepts appris restent **universels** :

-  **Middleware** : Abstraction de la communication
-  **Marshalling** : Sérialisation des données
-  **Service Registry** : Découverte de services
-  **Transparence** : Appels distants comme appels locaux
-  **Architecture** : Séparation client/serveur

Ces principes se retrouvent dans **toutes les architectures distribuées modernes**, faisant de CORBA un excellent outil pédagogique pour comprendre les fondations des systèmes répartis contemporains.

---

<div align="center">

** Si ce projet vous a été utile, n'hésitez pas à mettre une étoile ! **

</div>
