package project;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;

/**
 * Controls the flow of information in the warehouse by coordinating databases with manipulators.
 *
 */
public class Controller {
  
  /** The warehouse keeps track of inventory and allows access to its stock. */
  private WarehouseManager warehouse;
  
  /** All of the orders that have come in, allows creation of new <code>Order</code>. */
  private OrderManager orders;
  
  /** All <code>Worker</code> currently working. */
  private EmployeeManager employees;
  
  /** Staging areas in the warehouse used for loading and marshaling. */
  private StagingManager staging;
  
  /** All Loaded and in the process of being loaded <code>Truck</code>. */
  private TruckManager trucks;



  /**
   * Creates a new instance of all of the Managers that comprise a warehouse.
   * 
   * @throws IOException file is removed or otherwise ceases to exist during reading.
   * @throws FileNotFoundException if a file cannot be found prints an error message that it could
   *         not be found.
   */

  public Controller(Path fileDirectory) throws FileNotFoundException, IOException {
    warehouse = new WarehouseManager(fileDirectory);
    orders = new OrderManager(fileDirectory);
    employees = new EmployeeManager();
    staging = new StagingManager();
    trucks = new TruckManager();

  }

  /** Provides access to the <code>WarehouseManager</code>. */
  protected WarehouseManager getWarehouse() {
    return warehouse;
  }

  /** Provides access to the <code>OrderManager</code>. */
  protected OrderManager getOrders() {
    return orders;
  }

  /** Provides access to the <code>EmployeeManager</code>. */
  protected EmployeeManager getEmployees() {
    return employees;
  }

  /** Provides access to the <code>StagingManager</code>. */
  protected StagingManager getStaging() {
    return staging;
  }

  /** Provides access to the <code>TruckManager</code>. */
  protected TruckManager getTrucks() {
    return trucks;
  }
}
