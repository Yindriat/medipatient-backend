@echo off
echo ========================================
echo   MediPatient Backend - Arret Dev
echo ========================================
echo.

echo Arret de Docker Compose...
docker-compose down

echo.
echo [OK] Environnement de developpement arrete
echo.
pause