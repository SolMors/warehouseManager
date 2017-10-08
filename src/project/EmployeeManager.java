package project;

import java.util.ArrayList;

/**
 * Keeps track of all <code>Worker</code> currently working in the warehouse.
 *
 */
public class EmployeeManager {

  /**
   * Contains a list of all <code>Worker</code> who are currently working in the warehouse.
   */
  private ArrayList<Worker> warehouseEmployees = new ArrayList<>();

  /**
   * Creates a database containing all currently working <code>Worker</code>.
   */
  public EmployeeManager() {}

  /**
   * Creates and adds a specific type of Worker to a list of warehouse employees.
   * 
   * @param name The name of the Worker.
   * @param type The specific type the employee is supposed to be. Valid types are
   *        <code>Picker</code>, <code>Sequencer</code>, <code>Loader</code>, and
   *        <code>Replenisher</code>.
   * @param manager <code>OrderManager</code> which stores all the orders on which
   *        <code>Worker</code> work.
   */
  protected void addEmployee(String name, String type, Controller controller) {
    if (type.equals("Picker")) {
      warehouseEmployees.add(new Picker(name, controller));
      RunWarehouse.logger.info(type + " " + name + " starts work.");
    } else if (type.equals("Sequencer")) {
      warehouseEmployees.add(new Sequencer(name, controller));
      RunWarehouse.logger.info(type + " " + name + " starts work.");
    } else if (type.equals("Loader")) {
      warehouseEmployees.add(new Loader(name, controller));
      RunWarehouse.logger.info(type + " " + name + " starts work.");
    } else if (type.equals("Replenisher")) {
      warehouseEmployees.add(new Replenisher(name, controller));
      RunWarehouse.logger.info(type + " " + name + " starts work.");
    }
  }

  /**
   * Retrieves a <code>Worker</code> from the list of employees. If <code>Worker</code> is not in
   * the list of employees returns <code>null</code>.
   * 
   * @param name name of <code>Worker</code> to retrieve.
   * @return a <code>Worker</code>, or null if the employee has not checked in yet.
   */
  protected Worker getEmployee(String name) {
    for (Worker employee : warehouseEmployees) {
      if (employee.getName().equals(name)) {
        return employee;
      }
    }
    return null;
  }

  /**
   * Returns the number of employees working in the warehouse.
   * 
   * @return <code>int</code> representing the number of currently active <code>Worker</code>.
   */
  protected int getEmployeeCount() {
    return warehouseEmployees.size();
  }
}
