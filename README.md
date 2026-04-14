# 🚀 SpringSpy

Lightweight HTTP traffic inspection library for Spring Boot applications.

SpringSpy captures request/response traffic, stores it (memory + file), and provides a built-in UI for **live monitoring and search**.

---

## ✨ Features

- 🔍 Capture HTTP request & response traffic
- 📊 Live monitoring dashboard
- 🔎 Advanced search screen
- 💾 File-based persistence
- 🔐 Built-in UI authentication (session-based)
- ⚡ Minimal setup (starter-based)
- 🧩 Spring Boot 2 & 3 support
- 🛣️ Context-path aware UI

---

## 📦 Modules

| Module     | Description |
|-----------|------------|
| core      | Event model, storage, search logic |
| starter2  | Spring Boot 2 integration (javax) |
| starter3  | Spring Boot 3 integration (jakarta) |
| ux        | Thymeleaf UI + static assets |

---

## ⚙️ Installation

### Spring Boot 3 (Jakarta)

```xml
<dependency>
    <groupId>io.github.spyfcc</groupId>
    <artifactId>spyfcc-starter3</artifactId>
    <version>1.0.0</version>
</dependency>
```

### Spring Boot 2 (Javax)

```xml
<dependency>
    <groupId>io.github.spyfcc</groupId>
    <artifactId>spyfcc-starter2</artifactId>
    <version>1.0.0</version>
</dependency>
```

---

## 🚀 Quick Start

Add dependency and run your application.

Then open:

http://localhost:8080/spy

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
```

---

## 🖥️ UI Screens

| Endpoint       | Description |
|---------------|------------|
| /spy/login    | Login page |
| /spy/logs     | Live traffic |
| /spy/search   | Search logs |

---

## ⚠️ Notes

- starter2 → uses javax
- starter3 → uses jakarta


---

## 🗺️ Roadmap

- Reduce starter duplication
- Add tests
- Improve logging
- Maven Central release

---

## 📄 License

Apache License 2.0
