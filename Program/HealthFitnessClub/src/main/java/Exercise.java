public class Exercise {
    private final String name;
    private final String description;
    private final int sets;
    private final int repetitions;

    Exercise(String name, String description, int sets, int repetitions)
    {
        this.name = name;
        this.description = description;
        this.sets = sets;
        this.repetitions = repetitions;
    }

    /**
     * Gives a string representation of the class.
     * @return string representation of the class
     */
    @Override
    public String toString()
    {
        return "\tName: " + name +
                "\n\tDescription: " + description +
                "\n\tSets: " + sets +
                "\n\tRepetitions: " + repetitions + "\n\n";
    }
}
