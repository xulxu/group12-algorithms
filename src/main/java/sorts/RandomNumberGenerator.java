package sorts;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

class RandomNumberGenerator {
    private static final Random random = new Random();

    public static <T extends Number> List<T> randomList(Class<T> type, int size) {
        List<T> list = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            list.add(randomNumber(type));
        }
        return list;
    }

    public static <T extends Number> T[] randomArray(Class<T> type, int size) {
        T[] array = (T[]) java.lang.reflect.Array.newInstance(type, size);
        for (int i = 0; i < size; i++) {
            array[i] = randomNumber(type);
        }
        return array;
    }

    private static <T extends Number> T randomNumber(Class<T> type) {
        if (type == Integer.class) {
            return type.cast(random.nextInt());
        } else if (type == Double.class) {
            return type.cast(random.nextDouble());
        } else if (type == Float.class) {
            return type.cast(random.nextFloat());
        } else if (type == Long.class) {
            return type.cast(random.nextLong());
        } else if (type == Short.class) {
            return type.cast((short) random.nextInt(Short.MAX_VALUE + 1));
        } else {
            throw new IllegalArgumentException();
        }
    }
}
