
# API : Rapport labo 4 SMTP

## Description du projet

Ce projet contient une application permettant de faire des pranks sous forme d'email forgés.
L'utilisateur peut définir une liste de messages et de personnes victimes desdits pranks, ainsi qu'un nombre de groupes (possibilité de séparer la liste des personnes afin de faire plusieurs pranks en une fois).
L'application se charge ensuite de générer les emails en séparant la liste des personnes en plusieurs groupes, choisissant 
pour chacun des groupes la première personne comme expéditeur et les autres comme destinataires et enfin séléctionnant un message aléatoirement.
Les emails sont ensuite envoyés automatiquement aux différentes victimes.

###### A brief description of your project: if people exploring GitHub find your repo, without a prior knowledge of the API course, they should be able to understand what your repo is all about and whether they should look at it more closely.

## Configuration d'un serveur mock SMTP

###### Instructions for setting up a mock SMTP server (with Docker - which you will learn all about in the next 2 weeks). The user who wants to experiment with your tool but does not really want to send pranks immediately should be able to use a mock SMTP server. For people who are not familiar with this concept, explain it to them in simple terms. Explain which mock server you have used and how you have set it up.

## Instructions pour envoyer des pranks

Afin de générer les parnks il faut modifier plusieurs fichiers :

### Fichier config.properties

Ce fichier permet de configurer l'envoi des emalis ainsi que la syntaxe pour lire les autres fichiers.

Liste des propriétés :
- serverAddress : adresse du serveur SMTP
- serverPort : port utilisé pour la communication
- numberOfGroups : nombre de groupes à générer à partir des adresses fournies
(il doit y avoir au moins 3 adresses par groupe)
- witnessAddresses : adresse email du ou des destinataire(s) en copie cachée
- messageSeparator : symbole(s) séparant les différents messages dans le fichier "message.txt"
- personSeparator : symbole(s) séparant le nom, prénom et adresse de chaque personne dans le fichier "targets.txt"
- witnessSeparator : symbole(s) séparant les différentes adresses de la propriété "witnessAddresses" spécifiée plus haut

Syntaxe :
```
<nomPropriété1>=<paramètre1>
<nomPropriété2>=<paramètre2>
[...]
```

### Fichier messages.txt

La liste des messages qui peuvent être envoyés. Ceux-ci doivent être séparés par la propriété "messageSeparator" définie dans le fichier "config.propreties".

Syntaxe :
```
<message1>
<messageSeparator>
<message2>
<messageSeparator>
<message3>
[...]
```

### Fichier targets.txt

La liste des personnes victimes du prank (expéditeurs et destinataires), comportant leur nom, prénom et adresse mail.
Ces différents attributs doivent se trouver sur la même ligne et être séparés par la propriété "personSeparator" définie dans le fichier "config.properties".

Il faut au moins 3 personnes par groupe (nombre défini dans le fichier "config.properties") et par conséquent au minimum 3 personnes.
A noter qu'une même personne peut apparaître plusieurs fois. 

Syntaxe :
```
<nom1><personSeparator><prenom1><personSeparator><adresseEmail1>
<nom2><personSeparator><prenom2><personSeparator><adresseEmail2>
[...]
```

###### Clear and simple instructions for configuring your tool and running a prank campaign. If you do a good job, an external user should be able to clone your repo, edit a couple of files and send a batch of e-mails in less than 10 minutes.

## Implémentation

###### A description of your implementation: document the key aspects of your code. It is probably a good idea to start with a class diagram. Decide which classes you want to show (focus on the important ones) and describe their responsibilities in text. It is also certainly a good idea to include examples of dialogues between your client and an SMTP server (maybe you also want to include some screenshots here).
