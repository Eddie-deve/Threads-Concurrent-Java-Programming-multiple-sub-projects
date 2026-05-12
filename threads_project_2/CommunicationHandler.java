package systems.secondary;

import systems.Spacecraft;

enum SecondarySystemState {
    HEALTHY,
    UNHEALTHY,
    CRITICAL,
    MALFUNCTIONED;
}

public class CommunicationHandler implements Runnable {
    Spacecraft sc;
    boolean isPropulsionController;
    String name;
    final int COMMUNICATIONHANDLER_POWER = 12;
    final int PROPULSIONCONTROLLER_POWER = 15;
    int powerNeeded = COMMUNICATIONHANDLER_POWER;
    SecondarySystemState state;
    public CommunicationHandler(Spacecraft sc) {
        this.sc = sc;
        this.name = sc.name;
    }
    public CommunicationHandler(Spacecraft sc, boolean isPropulsionController) {
        this(sc);
        this.isPropulsionController = isPropulsionController;
        this.powerNeeded = PROPULSIONCONTROLLER_POWER;
    }
    public void run() {
        if (!sc.getIsAlive()) return;
        boolean gotPower = sc.consumePower(powerNeeded);
        if (gotPower) {
            state = SecondarySystemState.HEALTHY;
            System.out.println("[" + name + "]: " + (isPropulsionController ? "PropulsionController" : "CommunicationHandler") + " is running, consuming " + powerNeeded + " units of power.");
        } else if (state == SecondarySystemState.HEALTHY || state == SecondarySystemState.UNHEALTHY) {
            state = state == SecondarySystemState.UNHEALTHY ? SecondarySystemState.CRITICAL : SecondarySystemState.UNHEALTHY;
            System.out.println("[" + name + "]: Not enough power for " + (isPropulsionController ? "PropulsionController" : "CommunicationHandler") + ". Switching off...");
        } else if (state == SecondarySystemState.CRITICAL) {
            state = SecondarySystemState.MALFUNCTIONED;
            System.out.println("[" + name + "]: the crew has died (A malfunction occurred in the " + (isPropulsionController ? "PropulsionController" : "CommunicationHandler") + ")!");
            sc.setNotAlive();
        }
    }

}
