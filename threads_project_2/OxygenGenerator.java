package systems.primary;

import systems.Spacecraft;

public class OxygenGenerator implements Runnable {
    Spacecraft sc;
    String name;
    final int OXYGENGENERATOR_POWER = 8;
    int powerNeeded = OXYGENGENERATOR_POWER;
    public OxygenGenerator(Spacecraft sc) {
        this.sc = sc;
        this.name = sc.name;
    }
    public void run() {
        try {
            if (!sc.getIsAlive()) return;
            boolean gotPower = sc.consumePower(powerNeeded);
            if (!gotPower) {
                Thread.sleep(2000);
                if (!sc.getIsAlive()) return;
                gotPower = sc.consumePower(powerNeeded);
            }
            if (gotPower) {
                System.out.println("[" + name + "]: OxygenGenerator is running, consuming " + powerNeeded + " units of power.");
            } else {
                System.out.println("[" + name + "]: the crew has died (OxygenGenerator failed due to insufficient power)!");
                sc.setNotAlive();
            }
        } catch (InterruptedException e) {
            if (!sc.getIsAlive()) return;
            System.out.println("[" + name + "]: Re-running oxygen checks...");
        }
    }
}