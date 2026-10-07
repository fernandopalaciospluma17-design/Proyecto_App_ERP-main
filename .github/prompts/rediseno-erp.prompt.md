---
description: Rediseño visual del frontend de Proyecto_App_ERP y preparación para producción (Render + Cloudflare + Expo/EAS)
agent: agent
---

# ROL
Eres un ingeniero senior full-stack con experiencia en Expo / React Native / React Native Web, monorepos pnpm + Turbo y despliegues en Render y Cloudflare. Trabajas directamente en mi workspace de VS Code con acceso a terminal y archivos.

# OBJETIVO
Preparar Proyecto_App_ERP para producción:
1. Rediseñar ÚNICAMENTE el frontend visual (`apps/web-mobile`), tomando como referencia la identidad visual del repo Diplomado-ERP.
2. Preparar la configuración estrictamente necesaria de build/despliegue (Render para backend, Cloudflare para web, Expo/EAS para Android).

La funcionalidad existente, el backend, la API, la base de datos, la autenticación, los contratos y la lógica de negocio DEBEN conservarse intactos.

# REPOSITORIOS
- **Base de producción (este workspace):** `fernandopalaciospluma17-design/Proyecto_App_ERP`
- **Referencia visual (solo lectura):** `fernandopalaciospluma17-design/Diplomado-ERP`
  - Si no está disponible localmente, pídeme la ruta o autorización para clonarlo en un directorio temporal FUERA de este repo.
  - Úsalo solo para extraer lenguaje visual (logos, assets, tokens, composición). NO copies su backend, lógica ni arquitectura.

# REGLAS DE TRABAJO (OBLIGATORIAS)
1. **Primero inspecciona, después edita.** No asumas rutas, scripts, variables ni nombres de archivo: verifícalos en `package.json`, `tsconfig`, `app.json`, `eas.json` y `apps/backend/src/config/env.ts`.
2. Crea una rama `feat/rediseno-visual` y haz commits pequeños por fase con mensajes claros. Nunca trabajes sobre `main`.
3. **Mínimo cambio:** reutiliza componentes existentes; no reescribas módulos que ya funcionan ni hagas reescrituras masivas.
4. Prioridad: funcionalidad existente → compatibilidad → identidad visual → responsive → animaciones → despliegue → pruebas.
5. Si aparece un error funcional, determina primero si ya existía (compáralo con la rama base). No lo corrijas tocando el backend sin evidencia. Documenta la causa y separa cambios visuales de correcciones funcionales.
6. Tras cada fase importante ejecuta los comandos de typecheck/lint/test/build que EXISTAN realmente en los scripts. Distingue errores heredados de errores introducidos por ti.
7. No instales dependencias nuevas salvo que haga falta una real, compatible con Expo/React Native Web y Android. Justifícala antes de instalarla.
8. Nunca expongas secretos en el bundle del frontend ni los escribas en archivos versionados. No imprimas valores de `.env`.
9. Antes de una modificación amplia (más de ~5 archivos), muéstrame la lista de archivos afectados.
10. No ejecutes comandos destructivos (`rm -rf` fuera de carpetas de build, `git push --force`, `git reset --hard`) sin pedirme confirmación.
11. No hagas deploys ni uses credenciales de Render, Cloudflare o EAS. Prepara la configuración y dame los pasos manuales.

# PUEDES MODIFICAR
- `apps/web-mobile/src` (componentes, pantallas, estilos, tema)
- Assets e imágenes de identidad visual
- Tokens/theme visual del frontend
- `apps/web-mobile/app.json` y configuración visual de Expo (nombre, slug, icono, splash)
- `apps/web-mobile/eas.json` (solo la URL de API de producción)
- `package.json` del frontend, solo si hace falta una dependencia visual real
- Configuración de build/deploy estrictamente necesaria

# NO DEBES MODIFICAR POR MOTIVOS VISUALES
- `apps/backend` (rutas, controladores, modelos, JWT, auth, servicios de PDF/Excel/Resend/almacenamiento)
- `packages/contracts` (salvo incompatibilidad real de build, documentada)
- Base de datos y lógica de negocio
- Endpoints, nombres de campos, payloads y estructuras de respuesta
- Lógica CRUD de ventas, inventario, contactos, compras, finanzas, proyectos, equipo, actividad y reportes
- Package name / bundle identifier de Android/iOS (sin razón explícita de producto)

# IDENTIDAD VISUAL
Debe comunicar claridad, confianza operativa y escala conectada. La interfaz debe sentirse ordenada antes de explicar su funcionamiento.

## Paleta (centralizar como tokens, sin valores dispersos)
| Uso | Nombre | HEX |
|---|---|---|
| Marca / acción / foco | Firma | `#E08934` |
| Texto principal / fondos oscuros | Tinta | `#18201F` |
| Texto secundario / elementos oscuros | Pizarra | `#35413F` |
| Elementos secundarios | Musgo | `#547064` |
| Fondo suave | Niebla | `#F4F5F1` |
| Superficie | Papel | `#FFFFFF` |
| Bordes / líneas | Line | `#E2E7E1` |
| Éxito | Success | `#2E7D5B` |
| Error / peligro | Danger | `#B44736` |

El naranja (Firma) se reserva para acciones, foco y momentos decisivos.

## Tipografía
- **Manrope:** marca, títulos y números destacados
- **Inter:** lectura general, formularios, tablas
- **IBM Plex Mono:** datos, códigos, identificadores

Usa el mecanismo de carga de fuentes compatible con Expo (p. ej. `@expo-google-fonts/*`), verificando que funcione en web y Android.

## Iconografía y estados
- Iconos de trazo uniforme, esquinas suaves, lectura inmediata; sin uso decorativo innecesario.
- Los estados NUNCA dependen solo del color: icono + texto.
  - ✓ Completado · ◷ En curso · ! Requiere atención · × No se pudo procesar
- Logo claro sobre fondo oscuro; logo oscuro sobre fondo claro.

# FASES
Ejecútalas en orden y **detente en cada punto de control (⛔)**.

## FASE 0 — Auditoría (SIN EDITAR NADA)
Entrégame un diagnóstico breve con:
- Estructura relevante del monorepo y de `apps/web-mobile`
- Componentes reutilizables existentes
- Theme/estilos actuales y cómo se aplican
- Dependencias y scripts reales (frontend y backend)
- Archivos que modificarás
- Archivos que explícitamente NO modificarás
- Riesgos (incluye errores preexistentes al correr typecheck/build ahora)
- Comandos de validación que usarás
- Variables de entorno reales leídas de `apps/backend/src/config/env.ts`

⛔ **Espera mi aprobación antes de continuar.**

## FASE 1 — Design System
Tokens de color, tipografía, espaciado, radios, sombras y estados. Componentes base centralizados: Button (primario naranja, secundario neutro), Input (normal/foco/error/deshabilitado, foco visible naranja), Card, Table, Modal y Badge de estado (icono + texto).

## FASE 2 — Branding
Integra logos claro/oscuro desde Diplomado-ERP (o los assets que yo indique). Actualiza `app.json` (nombre visual, icono, splash) sin cambiar identificadores de paquete. Elimina el branding "Orbit ERP" del frontend donde corresponda.

## FASE 3 — AppShell
Rediseña Sidebar (icono + etiqueta, estado activo claro), Topbar (contexto de sección, usuario, acciones) y contenedores. Navegación consistente en todos los módulos; adaptación móvil sin romper el flujo.

## FASE 4 — Login
Composición dividida: panel oscuro (Tinta/Pizarra) con logo claro y mensaje breve + panel claro (Papel/Niebla) con formulario. CTA en Firma `#E08934`. NO cambies la lógica de autenticación existente.

## FASE 5 — Módulos
Aplica el sistema a: Dashboard (KPIs, números en Manrope, estados texto+icono), Ventas, Inventario, Contactos, Compras, Finanzas, Proyectos, Equipo, Actividad, Reportes, Perfil y demás pantallas existentes. Tablas con encabezados diferenciados; en pantallas pequeñas, tarjetas o scroll controlado. No toques payloads ni nombres de campos de la API.

## FASE 6 — Microinteracciones
- Entrada de pantalla: opacidad + translateY ≈ 8 px, 180–220 ms
- Modal: escala 0.98 → 1 + opacidad
- Botones: feedback breve hover/pressed/focus
- Skeletons de carga donde aporten contexto
- Respetar reducción de movimiento (`prefers-reduced-motion` / `AccessibilityInfo`)
- Nada que retrase acciones administrativas

## FASE 7 — Responsive y accesibilidad
Valida desktop, tablet, web móvil y Android. Foco visible, contraste suficiente, objetivos táctiles adecuados, estados con texto/icono.

## FASE 8 — Build local
Ejecuta los scripts reales de lint/typecheck/tests/build. Para Expo Web, ejecuta el export real y COMPRUEBA en disco dónde se genera la carpeta de salida (candidata: `apps/web-mobile/dist`; no la asumas). Separa errores heredados de nuevos.

Comandos candidatos (verifica antes en los scripts):
```bash
corepack enable
pnpm install --frozen-lockfile
pnpm --filter @erp/contracts build
pnpm --filter @erp/backend build
pnpm --filter @erp/web-mobile build
```

⛔ **Muéstrame el resumen de build y los cambios antes de pasar a despliegue.**

## FASE 9 — Render (backend) — solo preparar
- Root del servicio = raíz del monorepo (NO aislar `apps/backend`; rompería `packages/contracts`)
- Build command y Start command reales, confirmados tras compilar. Con `rootDir='.'` y `outDir='dist'` verifica la ruta real de salida (candidato: `node apps/backend/dist/src/server.js`)
- Variables de entorno verificadas contra `env.ts` (candidatas: `MONGODB_URI`, `JWT_SECRET`, `PORT`, `NODE_ENV=production`, `CORS_ORIGINS`, `RESEND_API_KEY`, `EMAIL_FROM`, `EMAIL_REPLY_TO`, `PUBLIC_API_URL`, `EMAIL_VERIFICATION_TTL_MINUTES`). Solo las que existan realmente.
- Pasos para verificar `GET /health` (`success=true`) y endpoints protegidos
- Los secretos viven solo en Render.

## FASE 10 — Cloudflare (frontend web) — solo preparar
- Build command y output directory REALES (comprobados en Fase 8)
- `EXPO_PUBLIC_API_URL=https://<backend-render>.onrender.com/api` (placeholder hasta que yo dé la URL real)
- `CORS_ORIGINS` del backend = exactamente el origen de Cloudflare, sin comodines
- Reemplaza la referencia a `orbit-erp-api-p9vp.onrender.com` (en `eas.json` y cualquier otro sitio) por la URL nueva cuando yo te la proporcione.

## FASE 11 — Android / Expo
Mantén la app basada en Expo/React Native. Actualiza la URL de API en la config usada por EAS, revisa `app.json` (nombre, slug, icono, splash). No cambies package/bundle identifiers.

## FASE 12 — QA
Genera un checklist y ejecuta las pruebas automáticas disponibles:
- Autenticación: login, registro, confirmación de correo, persistencia de sesión, logout
- Dashboard con datos reales
- Ventas (crear/editar/consultar/eliminar), Inventario (crear/editar/movimientos), Contactos, Compras, Finanzas, Proyectos, Equipo (según rol), Actividad, Reportes
- PDF (generar/descargar) y Excel (exportar)
- Tienda pública (acceso sin autenticación), Imágenes
- Responsive (desktop, tablet, móvil)
- Android (login + API + CRUD principal)

Marca qué pudiste verificar tú y qué debo probar yo manualmente.

# CRITERIOS DE ACEPTACIÓN
- Identidad visual única y coherente en todo el frontend
- Funcionalidad intacta contra el mismo backend y contratos; sin endpoints rotos por cambios de UI
- Autenticación funcional en web y Android
- Responsive; estados no dependientes solo del color; animaciones que no interfieren
- Sin secretos en el bundle del frontend
- Builds de Expo Web y backend reproducibles
- Cloudflare consume solo el backend de producción nuevo; CORS solo con orígenes definidos

# ENTREGA FINAL
1. Resumen de lo realizado por fase
2. Archivos modificados/creados/eliminados
3. Errores heredados vs. introducidos
4. Cambios fuera de alcance (si los hubo) y su justificación
5. Comandos exactos de build/deploy verificados
6. Checklist de QA con estado
7. Pasos pendientes que requieren mis credenciales o acciones manuales

# INICIO
Comienza con la **FASE 0 (auditoría)**. No edites ningún archivo hasta que apruebe el diagnóstico.
