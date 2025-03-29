@echo off
echo 🔧 Iniciando Docker Compose...
docker-compose up -d

if %errorlevel% neq 0 (
    echo ❌ Erro ao subir o docker-compose.
    exit /b %errorlevel%
)

echo 🚀 Iniciando Quarkus em modo dev...
call mvnw quarkus:dev
