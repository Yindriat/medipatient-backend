@echo off
echo ========================================
echo   MediPatient Backend - Docker Run
echo ========================================
echo.

REM Verification de Docker
docker --version >nul 2>&1
if %errorlevel% neq 0 (
    echo [ERREUR] Docker n'est pas installe ou demarre
    echo Veuillez demarrer Docker Desktop et reessayer
    pause
    exit /b 1
)

REM Verification que Docker Desktop est lance
docker info >nul 2>&1
if %errorlevel% neq 0 (
    echo [ERREUR] Docker Desktop n'est pas demarre
    echo Veuillez demarrer Docker Desktop et reessayer
    pause
    exit /b 1
)

echo [OK] Docker est operationnel
echo.

REM Demarrage de la base de donnees si necessaire
echo Verification des containers...
docker ps | findstr medipatient-postgres >nul
if %errorlevel% neq 0 (
    echo Demarrage de la base de donnees...
    docker-compose up -d
    echo Attente du demarrage de PostgreSQL...
    timeout /t 10 /nobreak >nul
) else (
    echo La base de donnees est deja lancee.
)

REM Recuperation du reseau Docker
echo Detection du reseau Docker...
for /f "tokens=*" %%i in ('docker inspect -f "{{range $k, $v := .NetworkSettings.Networks}}{{$k}}{{end}}" medipatient-postgres') do set NETWORK_NAME=%%i
echo Reseau detecte : %NETWORK_NAME%

echo.
echo ========================================
echo Demarrage de l'application avec Maven via Docker...
echo ========================================
echo.

REM Creation du dossier .m2-docker si inexistant pour le cache
if not exist ".m2-docker" mkdir ".m2-docker"

docker run -it --rm ^
  -v "%cd%":/usr/src/app ^
  -w /usr/src/app ^
  -v "%cd%/.m2-docker":/root/.m2 ^
  --network %NETWORK_NAME% ^
  -p 7080:7080 ^
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://medipatient-postgres:5432/medipatient ^
  maven:3.9-eclipse-temurin-21 ^
  mvn spring-boot:run

pause
