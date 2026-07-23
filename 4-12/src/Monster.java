public class Monster {

    private String name;
    private int level;

    public Monster(String name, int level) {
        this.name = name;
        this.level = level;
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        Monster other = (Monster) obj;

        return name.equals(other.name) && level == other.level;
    }

    @Override
    public String toString() {
        return name + "Lv." + level;
    }
}