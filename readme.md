# MediPatient Backend
test modif
Backend API REST pour l'application MediPatient, développée avec Spring Boot 3.2 et architecturée selon les principes du Domain-Driven Design (DDD).

## 🏗️ Architecture

Le projet suit une architecture DDD simple organisée par domaines métier 

## 🚀 Technologies utilisées

- **Spring Boot 3.2.0** - Framework principal
- **Java 21** - Version du langage
- **Spring Data JPA** - Persistance des données
- **PostgreSQL** - Base de données
- **Flyway** - Migration de base de données
- **Lombok** - Réduction du boilerplate
- **MapStruct** - Mapping objet-objet
- **Spring Security** - Sécurité
- **OpenAPI/Swagger** - Documentation API
- **JWT** - Authentification par token
- **Maven** - Gestionnaire de dépendances

## 📋 Prérequis pour le développement

### Outils requis à télécharger :

#### 1. **Java Development Kit (JDK) 21**
- 🔗 [Oracle JDK 21](https://www.oracle.com/java/technologies/javase/jdk21-archive-downloads.html)
- 🔗 [OpenJDK 21](https://adoptium.net/temurin/releases/?version=21)

#### 2. **Docker Desktop** (pour la base de données)
- 🔗 [Docker Desktop pour Windows](https://desktop.docker.com/win/main/amd64/Docker%20Desktop%20Installer.exe)
- 🔗 [Docker Desktop pour Mac](https://desktop.docker.com/mac/main/amd64/Docker.dmg)

#### 3. **Git et outils de versioning**
- 🔗 [Git pour Windows](https://git-scm.com/download/win)
- 🔗 [SourceTree](https://www.sourcetreeapp.com/) (Interface graphique Git recommandée)

#### 4. **IDE recommandé**
- 🔗 [IntelliJ IDEA Community](https://www.jetbrains.com/idea/download/) (Gratuit)
- 🔗 [VS Code](https://code.visualstudio.com/download) avec extension Java

#### 5. **Maven** (optionnel, inclus dans la plupart des IDEs)
- 🔗 [Apache Maven](https://maven.apache.org/download.cgi)

## 🚀 Installation et démarrage

### 1. **Cloner le projet avec SourceTree**

#### Option A : Avec SourceTree (Interface graphique)
1. Ouvrir SourceTree
2. Cliquer sur "Clone"
3. Saisir l'URL : `https://github.com/kitalmartial-lang/medipatient-backend.git`
4. Choisir le dossier de destination
5. Cliquer sur "Clone"

#### Option B : Avec Git en ligne de commande
```bash
git clone https://github.com/kitalmartial-lang/medipatient-backend.git
cd medipatient-backend
```

### 2. **Démarrer la base de données avec Docker**

#### Prérequis : S'assurer que Docker Desktop est démarré

```bash
# Démarrer PostgreSQL avec Docker Compose
docker-compose up -d

# Vérifier que les containers sont démarrés
docker-compose ps
```

✅ **Services disponibles après démarrage :**
- **PostgreSQL** : `localhost:5432`
  - Base : `medipatient`
  - Utilisateur : `medipatient`
  - Mot de passe : `medipatient123`
- **Adminer** (Interface web pour la DB) : http://localhost:8081

### 3. **Configuration du projet**

La configuration est déjà prête dans `src/main/resources/application.yml` avec les bonnes valeurs pour Docker :

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/medipatient
    username: medipatient
    password: medipatient123
server:
  port: 7080
```

### 4. **Lancer l'application**

```bash
# Compiler et démarrer
./mvnw clean install
./mvnw spring-boot:run

# Ou avec Maven installé
mvn clean install
mvn spring-boot:run
```

> 💡 **Astuce** : Utilisez les scripts fournis pour Windows :
> - Double-clic sur `start-dev.bat` pour tout démarrer automatiquement
> - Double-clic sur `stop-dev.bat` pour tout arrêter

✅ **L'application sera accessible sur :**
- **API** : http://localhost:7080/api
- **Health Check** : http://localhost:7080/api/health
- **Swagger UI** : http://localhost:7080/api/swagger-ui.html

### 5. **Arrêter l'environnement**

```bash
# Arrêter l'application : Ctrl+C dans le terminal

# Arrêter Docker
docker-compose down

# Arrêter et supprimer les volumes (⚠️ supprime les données)
docker-compose down -v
```

## 📚 Documentation API

La documentation Swagger est disponible sur :
- **Swagger UI** : http://localhost:7080/api/swagger-ui.html
- **OpenAPI JSON** : http://localhost:7080/api/v3/api-docs

## 🔐 Endpoints principaux

### Health Check
- `GET /api/health` - Vérification de l'état de l'application

### Utilisateurs
- `GET /api/users` - Liste des utilisateurs
- `POST /api/users` - Créer un utilisateur
- `GET /api/users/{id}` - Détails d'un utilisateur
- `PUT /api/users/{id}` - Mettre à jour un utilisateur
- `DELETE /api/users/{id}` - Supprimer un utilisateur

## 🐳 Gestion Docker

### Commandes utiles

```bash
# Voir les logs des containers
docker-compose logs postgres
docker-compose logs adminer

# Redémarrer un service
docker-compose restart postgres

# Accéder à la base PostgreSQL directement
docker-compose exec postgres psql -U medipatient -d medipatient

# Voir l'état des volumes
docker volume ls

# Backup de la base de données
docker-compose exec postgres pg_dump -U medipatient medipatient > backup.sql

# Restaurer la base de données
docker-compose exec -T postgres psql -U medipatient medipatient < backup.sql
```

### Interface Adminer

Accédez à http://localhost:8081 pour gérer la base de données via une interface web.

**Paramètres de connexion :**
- **Système** : PostgreSQL
- **Serveur** : postgres
- **Utilisateur** : medipatient
- **Mot de passe** : medipatient123
- **Base de données** : medipatient

## 🗃️ Base de données

### Migrations Flyway
Les migrations sont dans `src/main/resources/db/migration/` :
- `V1__Create_users_table.sql` - Table des utilisateurs
- `V2__Create_specialties_table.sql` - Table des spécialités médicales
- `V3__Create_patients_doctors_tables.sql` - Tables patients et médecins
- `V4__Create_appointments_table.sql` - Table des rendez-vous

### Modèle de données
- **users** : Informations de base des utilisateurs
- **specialties** : Spécialités médicales
- **patients** : Données spécifiques aux patients
- **doctors** : Données spécifiques aux médecins
- **appointments** : Rendez-vous médicaux

## 🛠️ Développement

### Commandes utiles
```bash
# Compiler le projet
mvn clean compile

# Lancer les tests
mvn test

# Créer le package
mvn clean package

# Générer les mappers MapStruct
mvn clean compile
```

### Profils
- **default** : Profil de développement avec PostgreSQL
- **test** : Profil de test avec H2 en mémoire

## 🔧 Configuration

### Profils disponibles
- **default** : Profil de développement avec PostgreSQL (Docker)
- **test** : Profil de test avec H2 en mémoire

### Variables d'environnement (optionnelles)
Les valeurs par défaut fonctionnent avec Docker, mais vous pouvez les surcharger :
- `DB_USERNAME` : Nom d'utilisateur de la base de données (défaut: medipatient)
- `DB_PASSWORD` : Mot de passe de la base de données (défaut: medipatient123)
- `JWT_SECRET` : Clé secrète pour JWT (défaut: mySecretKey)

## 🛠️ Guide de développement rapide

### Structure du projet

Consultez le fichier [STRUCTURE_SIMPLE.md](./STRUCTURE_SIMPLE.md) pour comprendre l'organisation des domaines.

### Commandes Maven utiles

```bash
# Compilation rapide
./mvnw compile

# Tests
./mvnw test

# Lancer avec profil de développement
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev

# Package sans tests
./mvnw package -DskipTests
```

### Troubleshooting

#### Problème : Port 5432 déjà utilisé
```bash
# Voir ce qui utilise le port
netstat -an | findstr 5432  # Windows
lsof -i :5432              # Mac/Linux

# Changer le port dans docker-compose.yml si nécessaire
```

#### Problème : Permission denied avec Docker
```bash
# Windows : Exécuter en tant qu'administrateur
# Mac/Linux : Ajouter votre utilisateur au groupe docker
sudo usermod -aG docker $USER
```

#### Problème : Base de données corrompue
```bash
# Supprimer les volumes et redémarrer
docker-compose down -v
docker-compose up -d
```

## 📝 TODO

- [x] ✅ Structure DDD par domaines
- [x] ✅ Configuration Docker avec PostgreSQL
- [x] ✅ Base de données avec Flyway
- [x] ✅ Configuration Spring Boot 3.2
- [ ] 🔄 Implémenter l'authentification JWT complète
- [ ] 🔄 Développer les domaines métier (auth, patient, appointment)
- [ ] 🔄 Ajouter les tests unitaires et d'intégration
- [ ] 🔄 Ajouter la validation des données
- [ ] 🔄 Implémenter la gestion des erreurs globale
- [ ] 🔄 Ajouter les logs structurés
- [ ] 🔄 Configurer les métriques et monitoring

## 🤝 Contribution

1. Fork le projet
2. Créer une branche feature (`git checkout -b feature/amazing-feature`)
3. Commit les changements (`git commit -m 'Add amazing feature'`)
4. Push sur la branche (`git push origin feature/amazing-feature`)
5. Ouvrir une Pull Request