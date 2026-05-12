package systems.secondary;

import systems.Spacecraft;

public class PropulsionController extends CommunicationHandler {
    public PropulsionController(Spacecraft sc) {
        super(sc, true);
    }
}