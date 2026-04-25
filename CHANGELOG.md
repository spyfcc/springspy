# Changelog

All notable changes to this project will be documented in this file.

## [1.0.0] - 2026-04-12

### Added
- Core traffic event model
- Spring Boot 2 starter
- Spring Boot 3 starter
- Thymeleaf UI module
- Live logs screen
- Search screen
- Basic session-based UI authentication
- File-based search support

### Improved
- Context-path aware UI routing
- Static resource path handling
- Search page rendering and pagination
- Search details panel support

### Notes
- This is the first public beta release.
- APIs and internal structure may change before 1.0.0.

## [1.1.0-RC1] - 2026-04-25

### Added
- Pluggable storage architecture (file, jdbc, nosql)
- JDBC-based storage implementation (cross-RDBMS compatible)
- NoSQL storage module (`spyfcc-nosql`)
- MongoDB storage implementation
- Configurable storage type via `traffic.spy.storage.type`
- Configurable NoSQL provider via `traffic.spy.nosql.provider`
- UI Cache clean Button
- UI Version

### Changed
- Unified storage operations under `SpyStore` abstraction (save + search)
- Refactored FileStore to implement `SpyStore`
- Removed `SpySearchService` in favor of storage-based search
- Simplified search logic to be storage-driven

### Removed
- Legacy search configuration (`SpySearchConfig`)
- File-based search service abstraction
