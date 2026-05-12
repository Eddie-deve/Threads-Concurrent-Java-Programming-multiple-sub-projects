# Concurrent Programming Exam 2024/2025

## Story

The year is 2552, and humanity has advanced far beyond its past technological achievements.

Currently, all eyes on Earth are gazing at the sky as the Endymion Mission has begun.  
The mission's objective is to study black holes up close and understand their mechanisms.  
To achieve this, scientists have designed a special spacecraft capable of flying near black holes.  
A total of 10 such spacecraft have left Earth to complete the mission.

However, the mission took an unexpected turn when the 10 ships deviated from their designated path and got too close to the black holes they were studying.  
As a result, the ships' systems malfunctioned, and they have dangerously low energy to operate properly.  
The only solution is to create a new control software that enables the ships' systems to distribute the remaining energy among their systems.

Your task is to write a program that will help the ships' systems become temporarily operational and analyze the final state of the crew on each ship.

## Tasks

### Energy Regulator - 4 points

The module serving as the ships' energy supplier will be represented by a class named `systems.PowerSupplier`.

- It has an initial energy amount (integer), which is injected by its constructor (the value should be the same for all ships).
- It has a method `consumePower`, which takes an integer as parameter and reduces the system's energy by this amount if sufficient energy is available.
- It also has a method `addPower` that adds amount of energy passed as an integer parameter to the system.

### Spacecraft Data - 3 points

The ships' data will be represented by a class named `systems.SpacecraftData`.

- This module stores whether the ship's crew is alive or not.

### Primary Systems - 8 points

The primary systems of the ships are those that frequently require energy, running regularly every 1 second.  
Only a single one of this system exists: the `systems.primary.OxygenGenerator` class.

The primary system attempts to request energy twice from the `PowerSupplier` for its operation.  
If it fails to acquire energy on the first attempt, it sleeps for 2 seconds before trying again.

If it succeeds on the first attempt, the following message is printed to the console:  
`"[" + name + "]: OxygenGenerator is running, consuming " + powerNeeded + " units of power."`  
Where `name` is the name of the spacecraft, and `powerNeeded` is the amount of energy the system consumes.

If it fails on the first attempt but succeeds on the one after sleeping, the same message is printed.

If it fails on both attempts, the following is printed to the console:  
`"[" + name + "]: the crew has died (OxygenGenerator failed due to insufficient power)!"`  
Additionally, it is recorded that the crew did not survive the journey.

If any exception is thrown in the system, catch it and print the following message to the console:  
`"[" + name + "]: Re-running oxygen checks..."`  
Then system must continue its operation.

### Secondary Systems - 10 points

The secondary systems of the ships require energy less frequently, they only run regularly every 2 seconds.
These are the classes `systems.secondary.CommunicationHandler` and `systems.secondary.PropulsionController`.

The secondary systems also attempt to request energy twice from the `PowerSupplier` for their operation.

If the system succeeds to get energy from the `PowerSupplier`, then its state is `HEALTHY`, and the following message is printed to the console:  
`"[" + name + "]: CommunicationHandler/PropulsionController is running, consuming " + powerNeeded + " units of power."`  
Where `name` is the name of the spacecraft, and `powerNeeded` is the amount of energy the system consumes.

If the system fails to acquire energy, its state is changed to `UNHEALTHY`, and the following message is printed to the console:  
`"[" + name + "]: Not enough power for CommunicationHandler/PropulsionController. Switching off..."`

If the system's state is `UNHEALTHY`, it tries to acquire energy again.  
If it fails again, the system's state is changed to `CRITICAL`.  
If it succeeds, the system's state is restored to `HEALTHY`.

If the system's state is `CRITICAL`, the following message is printed to the console:  
`"[" + name + "]: the crew has died (A malfunction occurred in the CommunicationHandler)!"`  
Additionally, the system's state is changed to `MALFUNCTIONED`.

If the system is in the `MALFUNCTIONED` state, the crew did not survive (to be stored in `SpacecraftData`).

It is important to note that state changes only take effect during the next run.  
Additionally, the system operates only if the ship's crew is alive.

State transitions are described as follows:  
`HEALTHY` → energy acquired successfully → no change  
`HEALTHY` → failed to acquire energy → `UNHEALTHY` → energy acquired successfuly → `HEALTHY`  
`HEALTHY` → failed to acquire energy → `UNHEALTHY` → failed to acquire energy → `CRITICAL` → `MALFUNCTIONED`

The enumeration type containing these states should be `systems.secondary.SecondarySystemState`.

### Spacecraft - 7 points

The spacecraft will be represented by a class named `Spacecraft`.  
Each spacecraft contains the 3 systems that will be executed using a scheduled `ExecutorService`.  
Their scheduling intervals are specified in the corresponding systems' sections above.  
After 10 seconds, the ship's systems are stopped, and the ship's state is returned to the fleet, where analyzers process their data further.

### Analyzers - 8 points

The analyzers are responsible for counting the surviving and lost ships at the end of the mission.  
This will be represented by a class named `Analyzer`.  
Two analyzers start their tasks simultaneously, and at the end, they summarize the results.  
One of the analyzers counts the survivors, while the other one counts the lost ships.  
They do this by iterating over the blocking queue stored in the `SpaceFleet` that contains the `Future` values of the ships' data (`BlockingQueue<Future<SpacecraftData>>`).
Remove those elements from the blocking queue that retun null using a removal operation that returns null if there are no more elements in the queue.

### Space Fleet - 10 points

The fleet of spacecraft will be represented by a class named `systems.SpaceFleet`.

- Create all the spacecraft participating in the mission (10 in total) and add them to an `ExecutorService`.  
  The spacecraft names should follow the pattern: `Endymion-` + ID (0-9), e.g., `Endymion-7`.  
  The systems should operate for 10 seconds in each ship, so also here, after 10 seconds, shut down the `ExecutorService`.
- The ships' data is stored in the fleet's blocking queue, which is passed to the analyzers at the end.
- The analyzers are also created using an `ExecutorService`, which shuts down after 2 seconds.
- The analyzers return their results, which the fleet prints to the console.

## Sample Output

See the file `sample_output.txt` for a sample output.
