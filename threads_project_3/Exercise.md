Petting Zoo
============

In this task, your job is to program a small petting zoo simulation. Your task is to ensure that various parts of the system are properly designed to allow concurrent access.

## Basic Task (5 points) PettingZoo (15 points)

Create a `Simulation` class, which will serve as the entry point for your program. During its operation, the program should create a `PettingZoo` object, start it on a separate thread, and then start 1000 `Guest` objects on individual threads. However, ensure that only 300 threads run concurrently (i.e., the 301st thread can only start once at least one of the first 300 threads has finished). The `main` program should terminate only after all `Guest` threads and the thread running the `PettingZoo` have completed. At the end, it should print `Simulation has finished`.

For this task, the `Guest` and `PettingZoo` classes initially do very little. Each `Guest` instance will call the `PettingZoo.enter(Guest)` method upon starting, wait for a random duration between 100-120 ms, and then call the `PettingZoo.exit(Guest)` method.

The `PettingZoo` waits for 500 ms after starting and then checks every 10 ms to see if it can stop. The stopping condition is that there are no visitors in the petting zoo.

## Animals (15 points)

When the `PettingZoo` starts, it creates 3 goats (`Goat`), 5 bunnies (`Bunny`), and 10 guinea pigs (`GuineaPig`). Each animal is started on its own thread. The `PettingZoo` will not stop until all the threads managing the animals have stopped. Once the `PettingZoo.close()` method is called, it waits up to 10 seconds for all animal threads to terminate gracefully before forcing them to stop.

Each animal (inherited from the `Animal` superclass) starts in the stable (`PettingZoo.stable`) and randomly decides (using the `Animal.shouldMove()` method) whether to move to the runway (`PettingZoo.runway`) or back to the stable. It uses the `PettingZoo.move(Animal)` method to transition between these areas. After each move, the animal sleeps for a random duration between 70-210 ms, managed by the `remainInPlace()` method.

The `Simulation` class should call the `PettingZoo.close()` method 5 seconds after starting the guests.

If the petting zoo is closed (`PettingZoo.isOpen()` returns `false`), the animal threads can stop.

## Guests (15 points) Food Producer (5 points)

Add a `foodSupply` variable to the `PettingZoo`, which can hold up to 10 units of food as integers. Guests will use this supply to buy food for the animals. Ensure that guests are served in arrival order from this supply.

Modify the behavior of the `Guest` as follows:
- After entering, a `Guest` calls the `PettingZoo.buyPetFood(int)` method, passing their patience level as a parameter. This method returns the amount of food they successfully purchase.
    - If the return value is not positive, the guest prints `"I have nothing to do here"` and leaves the zoo, using the `PettingZoo.exit(Guest)` method.
    - Otherwise, the guest records the purchased food in their `food` variable.
- Guests stay in the park as long as it is open (`isOpen`) or they still have food (`food > 0`).
- Between each action, a guest waits 50 ms using the `standStill()` method.
- During each action, the guest randomly selects an animal from the runway (`pettingZoo.getRandomAnimal()`). If they can access it (`Animal.tryToAttach(Guest)`), they start petting it and record it in their `currentAnimal` variable.
    - If access fails, they restart the process.
    - Once petting an animal, they perform the following every 100 ms:
        - If the animal is still present (`currentAnimal != null`) and they have food (`food > 0`), they decide whether to continue petting using the `shouldStay()` method.
            - If continuing, they call `printPet()`.
            - If not, they leave using the `Animal.detach(Guest)` method.
        - **Ensure that animals cannot move during the guest's decision to pet them, but allow animals to move between these decisions.**
- Implement the `feed()` method, which reduces the food count by 1 and returns `false` if the guest has no food left, or `true` otherwise.
- Implement the `leave(Animal)` method, which sets `currentAnimal` to `null` if the parameter matches the currently petted animal.

Modify the animal logic:
- Implement `tryToAttach(Guest)` and `detach(Guest)` methods so that animals track which guests are petting them.
- Animals should beg for food in each cycle using the `getFood()` method:
    - Randomly select a guest (`getRandomGuest()`) and call their `feed()` method.
    - If feeding fails (`feed()` returns `false`), the animal considers moving (`shouldMove()`).
    - Before moving, the animal detaches from all guests and clears its list of attached guests.

Add a `FoodProducer` object to the `PettingZoo` on a separate thread. While the park is open (`isOpen()`), this thread generates a new food package (`createFood()`) every 10 ms and adds it to the supply using the `PettingZoo.addFood(int)` method.

Implement the `PettingZoo.addFood(int)` method to wait up to 100 ms for space in the `foodSupply` variable.

## Doctor (5 points)

The `PettingZoo` creates a `Doctor` object and starts it on a separate thread. The `PettingZoo` will wait for the doctor thread to finish before stopping. The doctor maintains a list of sick animals and checks if any are present during each cycle. If there are sick animals, the doctor treats them; otherwise, they check if the park is open and sleep for 1 second if there are no sick animals.

- If a sick animal is added using the `addSickAnimal(Animal)` method, the doctor should wake up immediately (if sleeping).
- While treating an animal, the doctor:
    - Removes the first animal from the list.
    - Calls the `cure()` method.
    - Calls the animal's `cured()` method.
- The doctor cannot go back to sleep as long as there are sick animals to treat.

Modify the `Animal` class logic so that after each successful feeding, it calls the `gotSick()` method. If this method returns `true`:
- The animal detaches from all guests petting it.
- Calls `PettingZoo.markSick(Animal)`.
- Does nothing further until its `cured()` method is called.
- Immediately resumes normal activity upon being cured.

Implement the `PettingZoo.markSick(Animal)` method to call the `Doctor.addSickAnimal(Animal)` method with the sick animal.
