package co.edu.unal.poo.actividad5;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase encargada de todo el manejo del archivo "friendsContact.txt"
 * usando RandomAccessFile. Implementa las 4 operaciones CRUD:
 * Create, Read, Update, Delete.
 *
 * Está basada en el ejemplo de GeeksforGeeks (File Handling in Java
 * with CRUD operations) pero corrigiendo los errores del artículo:
 *  - Comparación de Strings con equals() en vez de ==
 *  - Se agrega manejo correcto de excepciones
 *  - Se corrige la variable "inputName" que no existía en el original
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

    /**
     * CREATE: agrega un nuevo contacto al final del archivo,
     * si el nombre no existe todavía.
     */
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

    /**
     * READ: devuelve todos los contactos guardados en el archivo.
     */
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

    /**
     * UPDATE: busca el contacto por nombre y le actualiza el número.
     * Se reescribe el archivo completo (patrón "archivo temporal").
     */
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

    /**
     * DELETE: elimina el contacto cuyo nombre coincide.
     * También se reescribe el archivo completo.
     */
    public boolean delete(String name) throws IOException {
        List<Friend> friends = readAll();
        boolean removed = friends.removeIf(f -> f.getName().equalsIgnoreCase(name));

        if (removed) {
            rewriteFile(friends);
        }
        return removed;
    }

    // ---------- métodos de apoyo ----------

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
