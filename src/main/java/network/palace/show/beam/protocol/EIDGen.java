package network.palace.show.beam.protocol;

class EIDGen {
    private static int lastIssuedEID = 1_500_000_000;

    static int generateEID() {
        return lastIssuedEID++;
    }
}
