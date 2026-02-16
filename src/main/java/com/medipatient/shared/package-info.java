/**
 * Shared components package for MediPatient application.
 * 
 * Contains cross-cutting concerns and shared components used across
 * multiple domains and modules:
 * 
 * Domain layer:
 * - model: Base entities and common domain objects
 * - repository: Base repository interfaces
 * - service: Shared domain services
 * - valueobject: Common value objects (Money, Address, Email, etc.)
 * 
 * Application layer:
 * - dto: Base DTOs and common transfer objects
 * - mapper: Base mapper interfaces
 * - service: Shared application services
 * - exception: Custom exception hierarchy
 * 
 * Infrastructure layer:
 * - config: Global configurations (database, security, etc.)
 * - security: Security configurations and utilities
 * - persistence: Database configurations and utilities
 * - web: Global web configurations and filters
 * - integration: External service integrations
 * 
 * This package ensures DRY principles and provides common functionality
 * while maintaining proper separation of concerns.
 */
package com.medipatient.shared;