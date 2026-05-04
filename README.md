[![Review Assignment Due Date](https://classroom.github.com/assets/deadline-readme-button-22041afd0340ce965d47ae6ef1cefeee28c7c493a6346c4f15d667ab976d596c.svg)](https://classroom.github.com/a/TDA47i3L)

# 🪐 Ares Base

---

## Prerequisites

Ensure the following are installed before getting started:

| Tool | Version | Download |
|------|---------|----------|
| Java JDK | 17+ | https://adoptium.net |
| Apache Maven | 3.8+ | https://maven.apache.org/download.cgi |

**Verify your installations:**

```bash
java -version
mvn -version
```

---

## Setup

```bash
git clone <your-repo-url>
cd term-project-team-8/AresBase
```

> ⚠️ **Important:** Make sure you are inside the `AresBase` folder when running the project.

---

## Run

```bash
mvn javafx:run
```

> 📦 The first run will download dependencies (~50 MB). This happens once only.

---

## Project Structure

```
AresBase/
├── pom.xml
├── src/aresbase/
│   ├── Main.java
│   ├── Launcher.java
│   ├── engine/
│   ├── model/
│   ├── processor/
│   └── ui/
└── saves/
```