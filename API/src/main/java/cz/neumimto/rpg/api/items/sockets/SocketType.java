package cz.neumimto.rpg.api.items.sockets;


public class SocketType {

    private final String name;
    private final String id;

    public SocketType(String name) {
        this.id = name.toLowerCase();
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SocketType that = (SocketType) o;
        return getId().equals(that.getId());
    }
}
