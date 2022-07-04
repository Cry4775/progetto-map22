package di.uniba.map.b.adventure;

public class Triple<U, V, W> {

    /**
     * The first element of this <code>Triple</code>
     */
    private U first;

    /**
     * The second element of this <code>Triple</code>
     */
    private V second;

    private W third;

    /**
     * Constructs a new <code>Triple</code> with the given values.
     * 
     * @param first  the first element
     * @param second the second element
     */
    public Triple(U first, V second, W third) {

        this.first = first;
        this.second = second;
        this.third = third;
    }

    public U getFirst() {
        return first;
    }

    public V getSecond() {
        return second;
    }

    public W getThird() {
        return third;
    }
}