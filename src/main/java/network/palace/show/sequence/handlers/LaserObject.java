package network.palace.show.sequence.handlers;

public enum LaserObject {
    SOURCE, TARGET;

    public static LaserObject fromString(String s) {
        switch (s.toLowerCase()) {
            case "source":
                return SOURCE;
            case "target":
                return TARGET;
        }
        return null;
    }
}
