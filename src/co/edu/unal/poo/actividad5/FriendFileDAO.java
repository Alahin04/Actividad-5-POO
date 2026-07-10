package co.edu.unal.poo.actividad5;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

/**
 */
public class FriendFileDAO {

    private final File file;

    public FriendFileDAO(String fileName) {
        this.file = new File(fileName);
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean create(Friend friend) throws IOException {
        if (existsByName(friend.getName())) {
            return false; // ya existe, no se agrega
        }
        try (RandomAccessFile raf = new RandomAccessFile(file, "rw")) {
            raf.seek(raf.length()); // ir al final del archivo
            raf.writeBytes(friend.toFileLine());
            raf.writeBytes(System.lineSeparator());
        }
        return true;
    }


    public List<Friend> readAll() throws IOException {
        List<Friend> list = new ArrayList<>();
        try (RandomAccessFile raf = new RandomAccessFile(file, "r")) {
            String line;
            while ((line = raf.readLine()) != null) {
                if (!line.isBlank()) {
                    list.add(Friend.fromFileLine(line));
                }
            }
        }
        return list;
    }


    public boolean update(String name, long newNumber) throws IOException {
        List<Friend> friends = readAll();
        boolean found = false;

        for (Friend f : friends) {
            if (f.getName().equalsIgnoreCase(name)) {
                f.setNumber(newNumber);
                found = true;
                break;
            }
        }

        if (found) {
            rewriteFile(friends);
        }
        return found;
    }


    public boolean delete(String name) throws IOException {
        List<Friend> friends = readAll();
        boolean removed = friends.removeIf(f -> f.getName().equalsIgnoreCase(name));

        if (removed) {
            rewriteFile(friends);
        }
        return removed;
    }



    private boolean existsByName(String name) throws IOException {
        for (Friend f : readAll()) {
            if (f.getName().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    private void rewriteFile(List<Friend> friends) throws IOException {
        try (RandomAccessFile raf = new RandomAccessFile(file, "rw")) {
            raf.setLength(0); // borra el contenido actual
            raf.seek(0);
            for (Friend f : friends) {
                raf.writeBytes(f.toFileLine());
                raf.writeBytes(System.lineSeparator());
            }
        }
    }
}
