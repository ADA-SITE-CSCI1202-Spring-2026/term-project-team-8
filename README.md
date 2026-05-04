[![Review Assignment Due Date](https://classroom.github.com/assets/deadline-readme-button-22041afd0340ce965d47ae6ef1cefeee28c7c493a6346c4f15d667ab976d596c.svg)](https://classroom.github.com/a/TDA47i3L)

# Ares Base

## Prerequisites

- Java JDK 17+ → https://adoptium.net
- Apache Maven 3.8+ → https://maven.apache.org/download.cgi

Verify:

java -version
mvn -version

## Setup

git clone <your-repo-url>
cd term-project-team-8/AresBase

## Run

mvn javafx:run

First run downloads dependencies (~50 MB) – one time only.

## Project Structure

AresBase/
├── pom.xml
├── src/aresbase/
│ ├── Main.java
│ ├── Launcher.java
│ ├── engine/
│ ├── model/
│ ├── processor/
│ └── ui/
└── saves/
