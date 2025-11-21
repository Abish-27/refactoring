package theater;

/**
 * Represents a play with a name and a type (e.g., tragedy or comedy).
 * Instances of this class store basic metadata about a play.
 */
public class Play {

    /** The name of the play. */
    private final String name;

    /** The type/genre of the play (e.g., "tragedy", "comedy"). */
    private final String type;

    /**
     * Creates a new Play with the given name and type.
     *
     * @param name the name of the play
     * @param type the type or genre of the play
     */
    public Play(String name, String type) {
        this.name = name;
        this.type = type;
    }

    /**
     * Returns the name of the play.
     *
     * @return the play's name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the type of the play.
     *
     * @return the play's type
     */
    public String getType() {
        return type;
    }
}
