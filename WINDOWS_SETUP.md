# 🪟 Guide d'installation pour Windows

## 📥 Installation étape par étape

### 1. **Java 21** (Obligatoire)

#### Télécharger et installer :
- 🔗 [OpenJDK 21 pour Windows](https://adoptium.net/temurin/releases/?version=21&os=windows&arch=x64&package=jdk)
- Choisir la version `.msi` pour une installation automatique

#### Vérifier l'installation :
```cmd
java -version
javac -version
```

#### Si Java n'est pas reconnu :
1. Ajouter `JAVA_HOME` dans les variables d'environnement :
   - `JAVA_HOME` = `C:\Program Files\Eclipse Adoptium\jdk-21.0.x.x-hotspot`
2. Ajouter `%JAVA_HOME%\bin` au `PATH`

### 2. **Docker Desktop** (Obligatoire)

#### Télécharger et installer :
- 🔗 [Docker Desktop pour Windows](https://desktop.docker.com/win/main/amd64/Docker%20Desktop%20Installer.exe)

#### Configuration requise :
- Windows 10/11 (64-bit)
- WSL 2 (sera installé automatiquement)
- Virtualisation activée dans le BIOS

#### Vérifier l'installation :
```cmd
docker --version
docker-compose --version
```

### 3. **Git et SourceTree**

#### Git :
- 🔗 [Git pour Windows](https://git-scm.com/download/win)
- ✅ Cocher "Git Bash Here" et "Git GUI Here" lors de l'installation

#### SourceTree (Interface graphique) :
- 🔗 [SourceTree](https://www.sourcetreeapp.com/)
- Créer un compte Atlassian (gratuit)

### 4. **IDE recommandé**

#### IntelliJ IDEA Community (Gratuit) :
- 🔗 [IntelliJ IDEA Community](https://www.jetbrains.com/idea/download/?section=windows)
- Inclut Maven et support Java automatiquement

#### Alternative - VS Code :
- 🔗 [VS Code](https://code.visualstudio.com/download)
- Installer l'extension "Extension Pack for Java"

## 🚀 Démarrage rapide

### 1. **Cloner le projet**

#### Avec SourceTree :
1. Ouvrir SourceTree
2. File → Clone / New...
3. URL : `https://github.com/kitalmartial-lang/medipatient-backend.git`
4. Destination : `C:\Dev\medipatient-backend` (ou votre dossier)
5. Clone

#### Avec Git Bash :
```bash
cd /c/Dev
git clone https://github.com/kitalmartial-lang/medipatient-backend.git
cd medipatient-backend
```

### 2. **Lancer Docker Desktop**
- Démarrer Docker Desktop depuis le menu Windows
- Attendre que l'icône Docker soit verte dans la barre de tâches

### 3. **Démarrer la base de données**

```cmd
# Dans le dossier du projet
docker-compose up -d

# Vérifier que ça fonctionne
docker-compose ps
```

### 4. **Lancer l'application**

#### Avec IntelliJ IDEA :
1. Ouvrir le dossier du projet
2. Attendre l'indexation Maven
3. Clic droit sur `MedipatientApplication.java` → Run

#### Avec ligne de commande :
```cmd
# Dans le dossier du projet
mvnw.cmd clean install
mvnw.cmd spring-boot:run
```

## 🔧 Résolution de problèmes Windows

### Problème : "mvn command not found"
```cmd
# Utiliser le wrapper Maven inclus
mvnw.cmd clean install
```

### Problème : "Docker daemon not running"
1. Démarrer Docker Desktop
2. Attendre le démarrage complet (icône verte)
3. Retry la commande

### Problème : Port 5432 occupé
```cmd
# Voir ce qui utilise le port
netstat -an | findstr 5432

# Modifier le port dans docker-compose.yml :
# "5433:5432" au lieu de "5432:5432"
```

### Problème : "Permission denied"
1. Exécuter CMD en tant qu'administrateur
2. Ou démarrer Docker Desktop en tant qu'administrateur

### Problème : WSL 2 non installé
1. Ouvrir PowerShell en tant qu'administrateur
2. Exécuter :
```powershell
wsl --install
```
3. Redémarrer l'ordinateur

### Problème : Performances lentes
1. Allouer plus de RAM à Docker :
   - Docker Desktop → Settings → Resources → Memory
   - Augmenter à 4GB minimum
2. Activer WSL 2 backend dans Docker Desktop

## 📁 Structure recommandée des dossiers

```
C:\Dev\
├── medipatient-backend\     # Ce projet
├── tools\                   # Outils téléchargés
│   ├── maven\              # Maven (si installé séparément)
│   └── postman\            # Postman pour tester l'API
└── workspace\              # Autres projets
```

## 🔗 Liens utiles pour Windows

- 🔗 [Windows Terminal](https://apps.microsoft.com/store/detail/windows-terminal/9N0DX20HK701) (Terminal moderne)
- 🔗 [Postman](https://www.postman.com/downloads/) (Test d'API)
- 🔗 [DBeaver](https://dbeaver.io/download/) (Client base de données)

## ✅ Checklist de vérification

- [ ] Java 21 installé et configuré
- [ ] Docker Desktop démarré et opérationnel
- [ ] Git installé et configuré
- [ ] Projet cloné dans un dossier accessible
- [ ] Docker Compose fonctionne (`docker-compose ps`)
- [ ] Application démarre (`mvnw.cmd spring-boot:run`)
- [ ] Health check accessible (http://localhost:7080/api/health)

Si tous les points sont cochés, votre environnement de développement est prêt ! 🎉