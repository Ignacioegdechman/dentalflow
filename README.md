# Dentalflow

Backend Spring Boot para un consultorio odontológico.

## Funcionalidades
- Autenticación JWT con roles
- Pacientes, odontólogos, tratamientos, turnos y pagos
- Historia clínica
- Dashboard y reportes
- Exportación de reportes en PDF y Excel
- Filtros avanzados
- Recordatorios de turnos por email y preparación de WhatsApp
- Recordatorios automáticos programables

## Endpoints destacados
- `GET /reportes/resumen`
- `GET /reportes/resumen/pdf`
- `GET /reportes/resumen/excel`
- `GET /reportes/rango?desde=YYYY-MM-DD&hasta=YYYY-MM-DD`
- `GET /reportes/rango/pdf?desde=YYYY-MM-DD&hasta=YYYY-MM-DD`
- `GET /reportes/rango/excel?desde=YYYY-MM-DD&hasta=YYYY-MM-DD`
- `GET /recordatorios/proximos?horas=24`
- `POST /recordatorios/turnos/{id}/email`
- `POST /recordatorios/turnos/{id}/whatsapp`
- `GET /turnos/buscar`
- `PATCH /turnos/{id}/confirmar`
- `PATCH /turnos/{id}/cancelar`
- `PATCH /turnos/{id}/reprogramar`
- `GET /pagos/buscar`
- `GET /actuator/health`
- `GET /actuator/info`
- `GET /integraciones/sync/turnos`
- `GET /integraciones/sync/pacientes`
- `GET /integraciones/sync/tratamientos`
- `GET /webhooks/whatsapp?hub.verify_token=...&hub.challenge=...`
- `POST /webhooks/whatsapp`
- `GET /integraciones/whatsapp/auditoria`
- `POST /integraciones/whatsapp/enviar-prueba`

## Variables de entorno
- `JWT_SECRET`
- `JWT_EXPIRATION_MS`
- `DB_URL`
- `DB_USERNAME`
- `DB_PASSWORD`
- `MAIL_HOST`
- `MAIL_PORT`
- `MAIL_USERNAME`
- `MAIL_PASSWORD`
- `APP_RECORDATORIOS_ENABLED`
- `APP_RECORDATORIOS_HORAS`
- `APP_RECORDATORIOS_CRON`
- `APP_WHATSAPP_ENABLED`
- `APP_WHATSAPP_USE_META_API`
- `APP_WHATSAPP_WEBHOOK_TOKEN`
- `APP_WHATSAPP_WEBHOOK_SECRET`
- `APP_WHATSAPP_API_URL`
- `APP_WHATSAPP_ACCESS_TOKEN`
- `APP_WHATSAPP_PHONE_NUMBER_ID`

## Ejecutar tests
```powershell
.\mvnw test
```

## Integracion con PostgreSQL y pgAdmin 4
1. Abri pgAdmin 4 y conectate a tu servidor local PostgreSQL.
2. Crea una base de datos nueva llamada `dentalflow`.
3. Si queres usar un usuario dedicado, crea rol y permisos:

```sql
CREATE ROLE dentalflow_user WITH LOGIN PASSWORD 'TuPasswordFuerte';
GRANT ALL PRIVILEGES ON DATABASE dentalflow TO dentalflow_user;
```

4. Configura variables de entorno antes de levantar la app (PowerShell):

```powershell
$env:DB_URL="jdbc:postgresql://localhost:5432/dentalflow"
$env:DB_USERNAME="postgres"
$env:DB_PASSWORD="postgres"
# Opcional para produccion
$env:JPA_DDL_AUTO="update"
```

5. Levanta la API:

```powershell
.\mvnw spring-boot:run
```

6. Verifica en pgAdmin:
- Schema `public` con tablas creadas por JPA.
- Tabla `whatsapp_message_audit` para trazabilidad del bot.
- Tabla `recordatorio_envios` para control de idempotencia de recordatorios.

7. Para ambiente profesional, recomendacion:
- mover credenciales a variables de entorno reales (no hardcodear),
- usar `JPA_DDL_AUTO=validate` + migraciones versionadas (Flyway/Liquibase),
- hacer backups automaticos desde PostgreSQL.


