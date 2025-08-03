package Exception;

public class ManagerSaveException extends RuntimeException {

    public ManagerSaveException(String message) {
        super(message);
    }

    @Override
    public String getMessage() {
        return "Error while reading the file: " + super.getMessage();
    }
}
