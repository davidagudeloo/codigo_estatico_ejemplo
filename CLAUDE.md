# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project overview

Motor de facturación y descuentos para eventos (conciertos, conferencias, cine). Proyecto académico en Java + Gradle (Kotlin DSL), con una aplicación de consola interactiva (`Main`) y una suite de pruebas (unitarias AAA + rendimiento) con JUnit 5. Módulo único: `event-billing-engine/`.

## Commands

Ejecutar siempre desde `event-billing-engine/` (o anteponer `event-billing-engine\` en Windows).

```powershell
# compilar
.\gradlew.bat build

# correr la app de consola interactiva (requiere --console=plain para que la lectura por teclado funcione)
.\gradlew.bat run -q --console=plain

# correr toda la suite de tests
.\gradlew.bat test

# correr una sola clase de test
.\gradlew.bat test --tests "com.events.billing.DiscountEngineTest"

# correr un solo método de test
.\gradlew.bat test --tests "com.events.billing.DiscountEngineTest.nombreDelMetodo"
```

El wrapper (`gradlew`/`gradlew.bat`) está incluido en el repo. Se requiere JDK 17 (el toolchain en `build.gradle.kts` fija exactamente esa versión) y Gradle 9.1.0.

## Architecture

- `src/main/java/com/events/billing/`
  - `Main.java`: punto de entrada, app de consola (menú interactivo, lee stdin, imprime la factura con desglose de descuentos).
  - `DiscountEngine.java`: orquesta las reglas de descuento sobre una `Purchase`; puede aplicar una regla, ambas combinadas o ninguna, y garantiza que el total nunca sea negativo.
  - `discount/`: `DiscountRule` (interfaz) con dos implementaciones — `CouponDiscountRule` (descuento de monto fijo por código, valida existencia/vigencia y lanza `InvalidCouponException`) y `CustomerCategoryDiscountRule` (porcentaje según `CustomerCategory`: STUDENT 15%, SENIOR 20%, REGULAR 0%).
  - `exception/InvalidCouponException.java`.
  - `model/`: `Event`, `Ticket`, `Customer`, `CustomerCategory`, `Purchase` — el dominio puro, sin lógica de descuento.
- `src/test/java/com/events/billing/`: tests unitarios con patrón AAA por regla (`discount/CouponDiscountRuleTest`, `discount/CustomerCategoryDiscountRuleTest`, `DiscountEngineTest`) y `performance/DiscountEnginePerformanceTest` (mide rendimiento, imprime resultados por consola — `testLogging.showStandardStreams` está activado en `build.gradle.kts` para verlos).

Regla de diseño clave: agregar una nueva forma de descuento significa implementar `DiscountRule`, no modificar `DiscountEngine`.

## Token efficiency (para el propio Claude Code)

- Nunca leer, listar ni grepear `event-billing-engine/.gradle/`, `event-billing-engine/build/` ni `event-billing-engine/bin/`: son caché y artefactos compilados, no aportan nada y `build/reports/tests/...` tiene rutas muy largas (pueden incluso exceder el límite de longitud de archivo en Windows).
- Para iterar sobre un test puntual, usar `--tests "Clase.metodo"` en vez de correr toda la suite; evita volcar el output completo de `DiscountEnginePerformanceTest` (que imprime por consola) cuando no es relevante.
- Si una build o test falla, filtrar el output por `FAILED`/`ERROR`/`Exception` en vez de pegar el log completo.
- No releer un archivo ya mostrado en la conversación salvo que se haya editado desde entonces.
