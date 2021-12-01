
#API : Rapport labo 4 SMTP

## Description du projet

Ce projet contient une application qui permet de faire des pranks. Lesdits pranks sont des e-mails forgés définis par un faux expéditeur, des destinataires et une liste de blagues. Il est aussi possible de générer plusieurs pranks en même temps en divisant la liste de victimes en un nombre de groupes à choix. 

######A brief description of your project: if people exploring GitHub find your repo, without a prior knowledge of the API course, they should be able to understand what your repo is all about and whether they should look at it more closely.

## Configuration d'un serveur mock SMTP

######Instructions for setting up a mock SMTP server (with Docker - which you will learn all about in the next 2 weeks). The user who wants to experiment with your tool but does not really want to send pranks immediately should be able to use a mock SMTP server. For people who are not familiar with this concept, explain it to them in simple terms. Explain which mock server you have used and how you have set it up.

## Instructions pour envoyer des pranks

Afin de générer et envoyer des pranks il faut modifier plusieurs fichiers :

### Fichier config.properties



### Fichier messages.txt

La liste des messages qui peuvent être envoyés.

### Fichier targets.txt

La liste des addresses e-mails des victimes (expéditeurs et destinataires). La syntaxe est nom:prénom:addresse

######Clear and simple instructions for configuring your tool and running a prank campaign. If you do a good job, an external user should be able to clone your repo, edit a couple of files and send a batch of e-mails in less than 10 minutes.

## Implémentation

######A description of your implementation: document the key aspects of your code. It is probably a good idea to start with a class diagram. Decide which classes you want to show (focus on the important ones) and describe their responsibilities in text. It is also certainly a good idea to include examples of dialogues between your client and an SMTP server (maybe you also want to include some screenshots here).
