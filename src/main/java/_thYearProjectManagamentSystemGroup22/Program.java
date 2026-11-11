package _thYearProjectManagamentSystemGroup22;

/** Programs a project can be restricted to. Extend this list as needed. */
public enum Program {
    SOFTWARE_ENGINEERING("Software Engineering"),
    COMPUTER_SYSTEMS_ENGINEERING("Computer Systems Engineering"),
    COMMUNICATIONS_ENGINEERING("Communications Engineering"),
    ELECTRICAL_ENGINEERING("Electrical Engineering"),
    BIOMEDICAL_ELECTRICAL_ENGINEERING("Biomedical and Electrical Engineering");

    private final String displayName;

    Program(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}