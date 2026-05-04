[![Review Assignment Due Date](https://classroom.github.com/assets/deadline-readme-button-22041afd0340ce965d47ae6ef1cefeee28c7c493a6346c4f15d667ab976d596c.svg)](https://classroom.github.com/a/TDA47i3L)

# 🚀 Ares Base - Colony Survival Management Dashboard

## 📖 Overview

Ares Base is a real-time, graphical colony survival simulation dashboard built in Java. As the newly appointed Base Commander of Ares Station on Mars, your mission is to manage incoming colony crises, maintain critical resources, and keep the station operational. The simulation runs on an internal clock that continuously generates new tasks, and you must interact with the interface to process tasks, manage resources, and prevent system failure.

This project was developed as the term project for Programming Principles II, demonstrating mastery of Object-Oriented Programming, Java Collections Framework, File I/O, and GUI development.

## 🎮 Features

- **Real-time Task Generation** – New colony crises automatically appear every few seconds
- **Task Queue Management** – Visual list of pending tasks with "Execute Next Task" functionality
- **Resource Management** – Track Oxygen, Spare Parts, Rations, and Base Credits using HashMap-based inventory
- **Supply Chain System** – Dropdown menu to synthesize/restock critical resources
- **Live System Log** – Scrolling text area displaying real-time events and errors
- **Polymorphic Task Processing** – Different base modules (Engineering Bay, Medical Ward) handle different task types
- **Save/Load State** – Persist your game state including resources, credits, and pending tasks to a file

## 🛠️ Technologies Used

- Java (OOP Principles)
- JavaFX (GUI)
- Java Collections Framework (Queue, HashMap)
- File I/O for state persistence
- Lambda Expressions for event handling
- Maven (build & dependency management)

## 📋 Prerequisites

You only need two things installed — **no manual JavaFX download required**. Maven handles everything automatically.

| Requirement  | Version       | Download                              |
| ------------ | ------------- | ------------------------------------- |
| Java JDK     | 17 or higher  | https://adoptium.net                  |
| Apache Maven | 3.8 or higher | https://maven.apache.org/download.cgi |

To verify you have both installed, run:

```bash
java -version
mvn -version
```

## 📁 Project Structure

```
term-project-team-8/
└── AresBase/
    ├── pom.xml                         ← Maven config (start here)
    ├── src/
    │   └── aresbase/
    │       ├── Main.java
    │       ├── Launcher.java
    │       ├── engine/
    │       │   ├── SimulationEngine.java
    │       │   ├── TaskFilter.java
    │       │   ├── TaskGenerator.java
    │       │   └── SaveLoadManager.java
    │       ├── model/
    │       │   ├── ColonyTask.java
    │       │   ├── LifeSupportTask.java
    │       │   ├── EngineeringTask.java
    │       │   ├── ResearchTask.java
    │       │   ├── Resource.java
    │       │   └── ResourceManager.java
    │       ├── processor/
    │       │   ├── IProcessor.java
    │       │   ├── EngineeringBay.java
    │       │   ├── MedicalWard.java
    │       │   └── HydroponicsBay.java
    │       └── ui/
    │           └── DashboardController.java
    └── saves/                          ← created automatically on first save
```

## 🚀 Running the Project

### Step 1 — Clone the repository

```bash
git clone <your-repo-url>
cd term-project-team-8/AresBase
```

### Step 2 — Choose how to run

---

#### ▶ Option A: Run directly with Maven (recommended for development)

No build step needed. Maven downloads JavaFX automatically on first run.

```bash
mvn javafx:run
```

That's it. Works on Windows, macOS, and Linux.

---

#### 📦 Option B: Build a portable jar and run it anywhere

This creates a single `.jar` file that includes JavaFX inside it — no Maven needed on the target machine, just Java.

**Build:**

```bash
mvn package
```

This creates `target/ares-base-1.0.jar`.

**Run:**

```bash
java -jar target/ares-base-1.0.jar
```

You can copy this `.jar` to any machine that has Java 17+ and run it the same way — no JavaFX install, no Maven needed.

---

### First-time run note

The first time you run `mvn javafx:run` or `mvn package`, Maven will download JavaFX and other dependencies (~50 MB). This only happens once — they are cached locally after that.

---

## 💾 Save / Load

- Click **SAVE STATE** to write current resources, credits, and task queue to `saves/colony_save.csv`
- Click **LOAD STATE** to restore a previously saved session
- The `saves/` folder is created automatically the first time you save

## 🎯 How to Play

1. The simulation starts automatically — new crises appear in the queue every 2–5 seconds
2. Click **▶ EXECUTE NEXT TASK** to process the next crisis in queue
   - If you have enough resources: task resolves, you earn credits
   - If you don't: task stays at the front of the queue, an error is logged
3. When resources run low, select one from the **CARGO REPLICATOR** dropdown and click **⟳ SYNTHESIZE** (costs 50 credits)
4. Watch the **SYSTEM LOG** for live updates and the **CRITICAL** counter in the stat bar for urgent threats
5. Save your session anytime with **💾 SAVE STATE** and resume later with **📂 LOAD STATE**
