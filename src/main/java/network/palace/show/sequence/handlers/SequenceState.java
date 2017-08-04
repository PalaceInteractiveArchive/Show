package network.palace.show.sequence.handlers;

public enum SequenceState {
    RELATIVE, ACTUAL;

    public static SequenceState fromString(String s) {
        switch (s.toLowerCase()) {
            case "relative":
                return RELATIVE;
            case "actual":
                return ACTUAL;
        }
        return null;
    }
}
