package project;

/**
 * Exception class for the warehouse simulation. Error handling behaviour will be implemented when
 * required in phase 2.
 */
public class WarehouseExceptions extends Exception {
  /**
   * The serialized version id.
   */
  private static final long serialVersionUID = 1L;

  /**
   * Constructs a warehouse exception.
   * 
   * @param message Exception message to be displayed when exception caught.
   */
  public WarehouseExceptions(String message) {
    super(message);
  }
}
