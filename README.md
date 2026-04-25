# 🚀 SpringSpy

Lightweight HTTP traffic inspection library for Spring Boot applications.

SpringSpy captures request/response traffic, stores it (memory + file + DB), and provides a built-in UI for **live monitoring and search**.

---

## 🌐 Landing Page

🌐 [Visit SpringSpy Landing Page https://www.spyfcc.com](https://www.spyfcc.com) 

Explore features, screenshots, and usage examples on the official landing page.

[developer@spyfcc.com](mailto:developer@spyfcc.com)

---

## 📦 Maven Central

SpringSpy is available on Maven Central:

👉 [Visit Maven Central](https://central.sonatype.com/artifact/io.github.spyfcc/springspy)

---

## ✨ Features

- 🔍 Capture HTTP request & response traffic
- 📊 Live monitoring dashboard (auto-refresh)
- 🔎 Advanced search (file-based)
- ⚡ In-memory store with configurable limit
- 🧹 Clear memory cache (UI action)
- 💾 Pluggable persistence (File, JDBC, NoSQL)
- 🍃 MongoDB support (NoSQL module)
- 🔐 Built-in UI authentication (session-based)
- ⚙️ Minimal setup (starter-based integration)
- 🧩 Spring Boot 2 (`javax`) & 3 (`jakarta`) support
- 🛣️ Context-path aware UI

---

## 📦 Modules

- **spyfcc-core**: Event model, storage contracts, in-memory storage, and search logic  
- **spyfcc-starter2**: Spring Boot 2 integration based on `javax.*`  
- **spyfcc-starter3**: Spring Boot 3 integration based on `jakarta.*`  
- **spyfcc-ux**: Thymeleaf UI templates and static assets  
- **spyfcc-nosql**: NoSQL storage integration. MongoDB is supported for now

---

## ⚙️ Installation : File Store

### Spring Boot 3 (Jakarta)

```xml
<dependency>
    <groupId>io.github.spyfcc</groupId>
    <artifactId>spyfcc-starter3</artifactId>
    <version>1.1.0-RC1</version>
</dependency>
```

### Spring Boot 2 (Javax)

```xml
<dependency>
    <groupId>io.github.spyfcc</groupId>
    <artifactId>spyfcc-starter2</artifactId>
    <version>1.1.0-RC1</version>
</dependency>
```

---

## 🧩 Optional Installation : MongoDB Storage

- SpyFCC does not include MongoDB by default.
- If you want to use MongoDB as storage, you need to add additional dependencies.

```xml
<dependencies>
<!-- For Spring Boot 2 -->
  <dependency>
      <groupId>io.github.spyfcc</groupId>
      <artifactId>spyfcc-starter2</artifactId>
      <version>1.1.0-RC1</version>
  </dependency>
<!-- For Spring Boot 3 -->
  <dependency>
    <groupId>io.github.spyfcc</groupId>
    <artifactId>spyfcc-starter3</artifactId>
    <version>1.1.0-RC1</version>
  </dependency>  
<!-- Optional MongoDB storage module -->
  <dependency>
	  <groupId>io.github.spyfcc</groupId>
	  <artifactId>spyfcc-nosql</artifactId>
	  <version>1.1.0-RC1</version>
  </dependency> 
<!-- Required only when using MongoDB storage -->  
  <dependency>
	  <groupId>org.springframework.boot</groupId>
	  <artifactId>spring-boot-starter-data-mongodb</artifactId>
  </dependency>
</dependencies>
```

### 🔧 Configuration
```properties
traffic.spy.storage.type=nosql
traffic.spy.nosql.provider=mongo
spring.data.mongodb.uri=mongodb://dburl:dbport/mongodbname
```
### Notes:
- A valid DataSource bean must be present in the application context.
- If no DataSource is found, SpyFCC will throw an error at startup.

### ✅ Result

When MongoDB storage is correctly configured, SpyFCC will automatically persist captured HTTP traffic into the `spy_traffic_log` collection.

---
## 🧩 Optional Installation : JDBC Storage
SpyFCC supports JDBC-based storage for relational databases.

```xml
<!-- For Spring Boot 2 -->
  <dependency>
      <groupId>io.github.spyfcc</groupId>
      <artifactId>spyfcc-starter2</artifactId>
      <version>1.1.0-RC1</version>
  </dependency>
<!-- For Spring Boot 3 -->
  <dependency>
    <groupId>io.github.spyfcc</groupId>
    <artifactId>spyfcc-starter3</artifactId>
    <version>1.1.0-RC1</version>
  </dependency>  
```
Make sure your project includes a JDBC driver (PostgreSQL, MySQL, etc.)

### 🔧 Configuration
```properties
traffic.spy.storage.type=jdbc
```

### Notes:
- A valid DataSource bean must be present in the application context.
- If no DataSource is found, SpyFCC will throw an error at startup.

### ✅ Result

When JDBC storage is correctly configured, SpyFCC will automatically persist captured HTTP traffic into the `spy_traffic_log` table.

---

## 📦 Other Build Tools (Gradle, etc.)

🌐 [View on Maven Central](https://central.sonatype.com/artifact/io.github.spyfcc/springspy) 

---

## 🚀 Quick Start

Add dependency and run your application.

Then open:

http://localhost:8080/spy

Login Page

<p align="center">
  <img src="docs/images/login.png" width="800"/>
</p>

Live Monitoring

<p align="center">
  <img src="docs/images/main.png" width="800"/>
</p>

Request Details

<p align="center">
  <img src="docs/images/detail.png" width="800"/>
</p>

Search Screen

<p align="center">
  <img src="docs/images/search.png" width="800"/>
</p>


---

## 🔧 Configuration

```properties
traffic.spy.enabled=true
traffic.spy.ui-path=/spy
traffic.spy.file-path=./logs/spy
traffic.spy.memory-size=1000
traffic.spy.max-body-size=2048
traffic.spy.mask-sensitive=true
traffic.spy.security.username=spy
traffic.spy.security.password=spy123
traffic.spy.workingthread=2
traffic.spy.storage.type=file
```

---

## ⚠️ Notes

- starter2 → uses javax
- starter3 → uses jakarta


---

## 🗺️ Roadmap

### 📌 Core Improvements
- Improve logging performance and structure
- Add advanced filtering & masking options

### 🔍 Observability
- Advanced search capabilities
- Real-time monitoring enhancements
- Metrics & dashboard improvements

### 🤖 AI Features
- AI-powered log analysis
- Automatic anomaly detection
- Smart error grouping & summarization
- AI-based request insights (slow requests, failure patterns)


### 🌐 UI & UX
- UI improvements with better visualization
- Dark mode support
- Export logs (JSON / CSV)

---

## 📄 License

Apache License 2.0
