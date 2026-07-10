package co.edu.unal.poo.actividad5;

public class Friend {

    private String name;
    private long number;

    public Friend(String name, long number) {
        this.name = name;
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getNumber() {
        return number;
    }

    public void setNumber(long number) {
        this.number = number;
    }


    public String toFileLine() {
        return name + "!" + number;
    }


    public static Friend fromFileLine(String line) {
        String[] parts = line.split("!");
        String n = parts[0];
        long num = Long.parseLong(parts[1].trim());
        return new Friend(n, num);
    }

    @Override
    public String toString() {
        return name + " - " + number;
    }
}
