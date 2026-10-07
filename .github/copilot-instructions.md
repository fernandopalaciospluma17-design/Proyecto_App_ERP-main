# Instrucciones del repositorio — Proyecto_App_ERP

Monorepo pnpm + Turbo: `apps/backend` (Express + TypeScript + MongoDB), `packages/contracts` (contratos compartidos), `apps/web-mobile` (Expo + React Native + React Native Web).

## Reglas permanentes
- Inspecciona antes de editar. No inventes rutas, scripts ni variables de entorno: verifícalos en `package.json`, `tsconfig`, `app.json`, `eas.json` y `apps/backend/src/config/env.ts`.
- Cambios mínimos e incrementales. No reescribas módulos que ya funcionan.
- No modifiques backend, base de datos, JWT/autenticación, contratos, endpoints, payloads ni lógica CRUD por motivos visuales.
- Si aparece un error funcional, determina si ya existía antes de tocar nada y documéntalo por separado.
- No expongas secretos en el frontend ni en archivos versionados.
- No cambies package name / bundle identifier.
- No ejecutes comandos destructivos ni despliegues sin mi confirmación.
- Tras cambios importantes ejecuta los scripts reales de typecheck/lint/test/build y distingue errores heredados de introducidos.
- Mantén compatibilidad con Android y accesibilidad básica (foco visible, contraste, objetivos táctiles, estados con icono + texto).

## Identidad visual (frontend)
- Colores como tokens centralizados: Firma `#E08934` (solo acción/foco), Tinta `#18201F`, Pizarra `#35413F`, Musgo `#547064`, Niebla `#F4F5F1`, Papel `#FFFFFF`, Line `#E2E7E1`, Success `#2E7D5B`, Danger `#B44736`.
- Fuentes: Manrope (marca, títulos, números), Inter (UI y lectura), IBM Plex Mono (datos y códigos).
- Logo claro sobre fondo oscuro; logo oscuro sobre fondo claro.
- Animaciones sutiles (180–220 ms) y respetando reducción de movimiento.
