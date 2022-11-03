package utility;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Utils {

    public static Set<String> loadFileListInSet(File file) throws IOException {
        Set<String> set = new HashSet<>();
        BufferedReader reader = new BufferedReader(new FileReader(file));
        while (reader.ready()) {
            set.add(reader.readLine().trim().toLowerCase());
        }
        reader.close();
        return set;
    }

    public static List<String> parseString(String string, Set<String> stopwords) {
        List<String> tokens = new ArrayList<>();
        String[] split = string.toLowerCase().split("\\s+");

        if (split.length > 1) {
            for (String t : split) {
                if (!stopwords.contains(t)) {
                    tokens.add(t);
                }
            }
        } else if (split.length == 1) {
            tokens.add(split[0]);
        }
        return tokens;
    }

    public static Field getField(Class<?> clazz, String fieldName) {
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

        throw new Error("Field '" + fieldName
                + "' not found on class " + clazz);
    }

    public static List<Field> getInheritedPrivateFields(Class<?> type) {
        List<Field> result = new ArrayList<Field>();

        Class<?> i = type;
        while (i != null && i != Object.class) {
            Collections.addAll(result, i.getDeclaredFields());
            i = i.getSuperclass();
        }
        for (Field field : result) {
            field.setAccessible(true);
        }
        return result;
    }

}
