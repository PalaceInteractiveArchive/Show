package network.palace.show.sequence.handlers;

public enum LaserObject {
    SOURCE, TARGET;

    public static LaserObject fromString(String s) {
        switch (s.toLowerCase()) {
            case "source":
            case "base":
                return TARGET;
            case "target":
                return SOURCE;
        }
        return null;
    }
}
