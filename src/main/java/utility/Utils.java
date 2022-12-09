package utility;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import gui.MainFrame;

/** Utility class that holds possibly useful and methods. */
public class Utils {

    /**
     * Loads a list of words in a file as a set of strings.
     * 
     * @param file the file to read.
     * @return the words in a set (no duplicates).
     * @throws IOException if file reading problems occur.
     */
    public static Set<String> loadFileListInSet(File file) throws IOException {
        Set<String> set = new HashSet<>();
        BufferedReader reader = new BufferedReader(new FileReader(file));
        while (reader.ready()) {
            set.add(reader.readLine().trim().toLowerCase());
        }
        reader.close();
        return set;
    }

    /**
     * Gets the requested field of a class by the field name.
     * 
     * @param clazz the class where the field is.
     * @param fieldName the name of the field.
     * @return the {@link java.lang.reflect.Field Field} object if exists.
     * @throws NoSuchFieldException if the field doesn't exist within the provided class.
     */
    public static Field getField(Class<?> clazz, String fieldName) throws NoSuchFieldException {
        Class<?> tmpClass = clazz;
        do {
            try {
                Field f = tmpClass.getDeclaredField(fieldName);
                f.setAccessible(true);
                return f;
            } catch (NoSuchFieldException e) {
                tmpClass = tmpClass.getSuperclass();
            }
        } while (tmpClass != null);

        throw new NoSuchFieldException("Field '" + fieldName
                + "' not found on class " + clazz);
    }

    /**
     * Gets all the fields (inherited and private too) of a requested class.
     * 
     * @param clazz the class to get the fields from.
     * @return a list of {@link java.lang.reflect.Field Field} objects.
     */
    public static List<Field> getFields(Class<?> clazz) {
        List<Field> result = new ArrayList<Field>();

        Class<?> i = clazz;
        while (i != null && i != Object.class) {
            Collections.addAll(result, i.getDeclaredFields());
            i = i.getSuperclass();
        }

        for (Field field : result) {
            field.setAccessible(true);
        }

        return result;
    }

    /**
     * Gets the requested field in a JSON by the field name.
     * 
     * @param json the JSON string.
     * @param fieldName the field name.
     * @return the requested {@link com.google.gson.JsonElement JsonElement} if exists,
     *         {@code null} otherwise.
     */
    public static JsonElement getJsonField(String json, String fieldName) {
        JsonElement element = JsonParser.parseString(json);

        if (element.isJsonObject()) {
            return element.getAsJsonObject().get(fieldName);
        } else if (element.isJsonArray()) {
            if (!element.getAsJsonArray().isEmpty())
                return element.getAsJsonArray().get(0).getAsJsonObject().get(fieldName);
        }

        return null;
    }

    /**
     * Checks for requested class objects in the given list and lists
     * them casted in a list as result.
     * 
     * @param <T> the class you want to check for.
     * @param clazz the class object you want to check for.
     * @param list the general list to check from.
     * @return a list of the requested class objects retrieved from the source list.
     */
    public static <T> List<T> listCheckedObjects(Class<T> clazz,
            List<?> list) {
        List<T> result = new ArrayList<>();

        if (list != null) {
            for (Object obj : list) {
                if (clazz.isInstance(obj)) {
                    result.add(clazz.cast(obj));
                }
            }
        }

        return result;
    }

    /**
     * @param path the path of the resource.
     * @return the requested {@link javax.swing.ImageIcon ImageIcon}.
     * @throws IOException if file reading problems occur.
     */
    public static ImageIcon getResourceAsImageIcon(String path) throws IOException {
        InputStream inputStream = MainFrame.class.getResourceAsStream(path);

        return new ImageIcon(ImageIO.read(inputStream));
    }

}
