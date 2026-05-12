# Concurrent Programming exam

## Technical details

- Create folders `task1`, `task2`, and `task3`.
- When solving task `N`, copy your previous solution (or in the case of task 1, the original `Task.java`) into the folder `taskN`. Have no further subfolders.
	- For Task 3, also copy `ConsoleLogger.java`.
- When you submit, pack the three folders into one `zip` file and submit that.
	- If you're not done with some exercise parts yet, do not add their empty folders to the zip.
	- Do not add unnecessary stuff (e.g. the Javadoc folder) into the zip.

# VM Scheduler

In this task you will simulate a scheduler of a virtual machine. The user (which is represented by a thread in the program) - may start processes (class `Process`). The scheduler is also a thread that is designed to handle the start of the processes using a pipeline.

The starting point of the simulation is the `VM.java` file. In this source file, comments indicate the tasks.

### Task 1: Basic process creation and process start (22 points)

First of all, complete the `Process` class.

- Create a static data member `nextIndex` that can be used to provide a unique identifier for each process.
This should be of an integer type that is suitable for providing atomic operations.
- Also create an `int` type variable `index` that gets its unique value with the help of the class-level variable on each instantiation in the constructor.
- Further, create an instance-level `boolean` data member `completed` that can store values, which can perform atomic operations and which will indicate the end of the process.
	- Write a getter method for the logical variable.
- Create the method `dispatch` that generates a random value between 100 and 400 milliseconds that simulates the work for that amount of time. At this point, the process can then be considered completed and the program should indicate this by writing to the console like this: `Process #14 ran successfully`

In the `VM` class, create a thread in the variable `user` that will launch processes, and a Scheduler thread that will handle the launching of processes.

- The `User` thread creates a new process every `50` milliseconds. Then it enters it into a `BlockingQueue` data structure.
- The `Scheduler` starts the next process in the queue as soon as it is available.

### Task 2: Process Handling with Graceful Shutdown (14 points)

The next step is to implement a graceful shutdown of the running threads.
In this task, the scheduler accesses the data channel in a way that does not block indefinitely:
after at most `100` milliseconds, it should have a chance to detect that it has to finish up.

The main program starts the threads, lets them run for `5` seconds, then calls `gracefulShutdown`.

- This method lets the two threads finish working.
	- Each of the two threads should have a corresponding `AtomicBoolean` variable in the `VM` class (`isUserOn`, `isSchedulerOn`) that indicates whether the respective thread should keep working or not.
- The `gracefulShutdown` method prints `Shutdown is in progress.` when it is called and `All proccesses shut down gracefully.` once it is done.

### Task 3: Logging (14 points)

In the last part of the task, implement the logging of the simulation.

First, create a new class called `ConsoleLogger`.

- Its only data member should be a queue of not too large capacity which stores log messages to be processed.
- It also has a `log(String)` method which can be used to add a text to the queue.
- When it is run, let it do the following.
	1. In an infinite loop, take an element from the queue, then write its contents to the standard output.
	1. Then wait for 50 milliseconds.
	1. If it gets interrupted in the meantime, indicate the termination on the standard output (`Logger is shutting down.`), then finish working.

Let the simulation work now as follows.

1. Using an `ExecutorService`, start up the logger and the two previously described activities.
	-	Replace the console output with logging in the entire program.
1. The threads may work for the usual amount of `5` seconds.
1. Initiate shutdown:
	-	The user is informed of this via `isUserOn`.
	-   Let us wait for the two activities to finish up.
	-	Turn off the logger so that it gets interrupted.
		-	Hint: find the appropriate method in the Java API documentation.
1. Finally, stop the `ExecutorService` with a maximum grace period of `10` seconds, then print: `The system is down.`.
