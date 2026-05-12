# Concurrent Java Programming Projects

## Overview

This repository contains a collection of Java-based concurrent and multithreaded programming projects developed through academic coursework and practical software engineering exercises.

The projects focus on designing and implementing thread-safe systems, concurrent simulations, synchronization mechanisms, and parallel execution workflows using core Java concurrency utilities and object-oriented design principles.

Each subproject explores different real-world concurrency challenges such as resource coordination, scheduling, simulation modeling, producer-consumer architectures, and subsystem synchronization.

The repository demonstrates practical understanding of:
- Multithreaded system design
- Concurrent resource management
- Synchronization strategies
- Parallel task execution
- Thread-safe application architecture
- Java concurrency utilities and patterns

---

# Repository Structure

```text
concurrent-java-projects/
│
├── concurrent-farm-simulation/
├── concurrent-order-simulation/
├── thread-scheduler-simulation/
├── spacecraft-threading-simulation/
└── simulation-exercises/
```

---

# Included Projects

## Concurrent Farm Simulation

A multithreaded simulation modeling a farm environment where multiple entities operate concurrently within a shared grid-based system.

The project includes concurrent interactions between:
- Sheep
- Dogs
- Gates
- Shared grid cells
- Environmental controllers

### Concepts Demonstrated

- Shared resource synchronization
- Concurrent entity movement
- Thread-safe grid management
- Inter-thread communication
- State synchronization
- Simulation lifecycle management

### Educational Focus

- Concurrent simulation architecture
- Resource coordination in shared environments
- Object-oriented concurrent design
- Synchronization of independent agents

---

## Concurrent Order Processing Simulation

A restaurant and order-processing simulation implementing concurrent workflows for food preparation, inventory handling, and customer order coordination.

The system demonstrates producer-consumer patterns and multithreaded task execution across shared processing pipelines.

### Concepts Demonstrated

- Producer-consumer architecture
- Blocking queues
- Inventory synchronization
- Task scheduling
- Parallel order preparation
- Shared state coordination

### Educational Focus

- Thread-safe business logic
- Workflow synchronization
- Queue-based concurrent processing
- Real-world concurrent service systems

---

## Thread Scheduler & Virtual Machine Simulation

A scheduling and virtual machine simulation focused on concurrent process execution, logging systems, and thread coordination.

The project models how schedulers manage execution flows and system resources in concurrent environments.

### Concepts Demonstrated

- Thread scheduling
- Execution lifecycle management
- Logging synchronization
- Concurrent process coordination
- Task execution management
- Virtualized execution workflows

### Educational Focus

- Operating-system-inspired scheduling
- Concurrent execution management
- Scheduler design concepts
- Process lifecycle modeling

---

## Spacecraft Threading Simulation

A spacecraft subsystem simulation where multiple independent systems operate concurrently while coordinating shared operational states.

Subsystems may include:
- Propulsion
- Oxygen generation
- Navigation
- Communication systems
- Monitoring services

### Concepts Demonstrated

- Independent subsystem threading
- Concurrent monitoring
- Synchronization between critical systems
- Shared-state coordination
- Fault-tolerant concurrent behavior

### Educational Focus

- Real-time concurrent systems
- Safety-oriented synchronization
- Multisystem coordination
- Simulation of distributed subsystems

---

## Concurrency Simulation Exercises

A set of smaller concurrency-focused exercises exploring synchronization techniques, atomic operations, and multithreaded execution flows.

### Concepts Demonstrated

- Atomic variables
- Thread lifecycle handling
- Synchronization primitives
- Shared-memory coordination
- Parallel execution techniques
- State consistency management

### Educational Focus

- Core Java concurrency fundamentals
- Synchronization strategies
- Safe multithreaded programming
- Parallel computation concepts

---

# Technologies & Concurrency Utilities

The repository includes implementations and concepts using:

| Technology / Concept | Purpose |
|---|---|
| Java | Core programming language |
| Java Threads | Multithreaded execution |
| ExecutorService | Thread pool management |
| BlockingQueue | Producer-consumer workflows |
| Atomic Variables | Lock-free thread-safe operations |
| Reentrant Locks | Explicit synchronization |
| Semaphores | Resource access coordination |
| Synchronization Primitives | Shared-state protection |
| Object-Oriented Programming | Modular system architecture |


To run code use: "javac *.java" in the correct subfolder task on the VSCode IDE, and then "java Name_of_main_file"
---

# Concurrency Concepts Covered

The projects collectively demonstrate practical implementation of:

- Multithreading
- Thread synchronization
- Shared resource protection
- Deadlock prevention strategies
- Producer-consumer workflows
- Parallel task execution
- Thread-safe architecture
- Concurrent simulation systems
- Scheduling and execution coordination
- Inter-thread communication
- Locking mechanisms
- Atomic operations
- Concurrent state management

---

# Educational Objectives

These projects demonstrate practical experience with:

- Concurrent software engineering
- Thread-safe application development
- Java concurrency utilities
- Simulation-based system modeling
- Object-oriented concurrent design
- Parallel execution workflows
- Shared-memory coordination
- Resource synchronization strategies
- Real-world concurrency patterns


---

# License

This repository was developed for educational and portfolio purposes.
