@echo off
echo ========================================
echo   MediPatient Backend - Demarrage Dev
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

REM Demarrage de la base de donnees
echo Demarrage de PostgreSQL...
docker-compose up -d

REM Attendre que PostgreSQL soit pret
echo Attente du demarrage de PostgreSQL...
timeout /t 10 /nobreak >nul

REM Verification du statut
docker-compose ps

echo ========================================
echo Services disponibles :
echo - PostgreSQL  : localhost:5433
echo - Adminer     : http://localhost:8081
echo ========================================
echo.

REM Demarrage de l'application Spring Boot
echo Demarrage de l'application Spring Boot...
echo.
mvnw.cmd spring-boot:run

pause