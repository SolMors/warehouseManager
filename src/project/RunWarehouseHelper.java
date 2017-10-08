package project;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.SimpleFormatter;

/**
 * Performs all of the functions to run the warehouse, called from <code>RunWarehouse</code>.
 */
public class RunWarehouseHelper {

  /** Stores the list of actions to perform in the Warehouse. */
  private ArrayList<String[]> events;

  /** Key words in the simulation file to invoke a <code>Worker.work</code> method. */
  private String[] workKeyWords = new String[] {"pick", "sequence", "check", "replenish", "scan"};

  /** Key words in the simulation file to invoke a <code>Worker.push</code> method. */
  private String[] pushKeyWords = new String[] {"marshal", "move", "load"};

  /** Controls information flow throughout the system, for this particular warehouse. */
  private Controller sysController;

  /** Instantiate the helper. */
  public RunWarehouseHelper() {}

  /**
   * Read and run the simulation file. Create a various reports when the simulation has fully
   * executed.
   * 
   * @param fileDirectory a <code>Path</code> storing the current working directory.
   * @param simulation the name of the simulation to be run.
   * @throws IOException file is removed or otherwise ceases to exist during reading.
   */
  protected void run(Path fileDirectory, String simulation) throws IOException {
    sysController = new Controller(fileDirectory);
    events = ReadAndWrite.readFile(fileDirectory.resolve(simulation), " ", false);
    String simulationDescription = "";
    for (String item : events.get(0)) {
      simulationDescription = simulationDescription + " " + item;
    }
    RunWarehouse.logger.info(simulationDescription);
    events.remove(0); // remove the summary before executing
    execute();
    report(fileDirectory);
  }


  /**
   * Given a list of events, call on the appropriate elements in the Warehouse to perform the
   * actions of the events.
   */
  private void execute() {
    while (!events.isEmpty()) {
      String[] line = events.get(0);
      events.remove(0);
      if (line[0].equals("Order")) {
        RunWarehouse.logger.info("Incoming Fax: " + stringBuilder(line));
        order(line);
      } else {
        RunWarehouse.logger.info("Instruction: " + stringBuilder(line));
        workerDoesWork(line);
      }
    }
  }

  /**
   * Creates a new <code>Worker</code> of specified type, or get <code>Worker</code> currently
   * working in the warehouse to do one of three tasks: receive, work, or push.
   * 
   * @param line contains the information to create new <code>Worker</code> or have them perform
   *        tasks.
   */
  private void workerDoesWork(String[] line) {
    if (line[2].equals("ready")) {
      sysController.getEmployees().addEmployee(line[1], line[0], sysController);
    } else {
      Worker employee = sysController.getEmployees().getEmployee(line[1]);
      if (line[2].equals("get")) {
        employee.receive();
      } else if (line[2].equals("rescan")) {
        employee.rescan();
      } else if (Arrays.asList(workKeyWords).contains(line[2])) {
        employee.work(line[3]);
      } else if (Arrays.asList(pushKeyWords).contains(line[2])) {
        employee.push();
      }
    }
  }

  /**
   * Create and store a new <code>Order</code>.
   * 
   * @param toDo contains pertinent command information.
   */
  private void order(String[] toDo) {
    sysController.getOrders().newOrder(toDo[2], toDo[1]);
  }

  /**
   * Create various reports and store them in the given directory.
   * 
   * @param fileDirectory <code>Path</code> of the directory to write the reports in.
   * @throws IOException when input and output files cannot be write or read.
   */
  private void report(Path fileDirectory) throws IOException {
    createStockReport(fileDirectory);
    createOrderReport(fileDirectory);
  }

  /**
   * Creates and stores a report of all inventory item whos' quantity is not 30.
   * 
   * @throws IOException when input and output files cannot be write or read.
   */
  private void createStockReport(Path fileDirectory) throws IOException {
    HashMap<String, PickFace> floor = sysController.getWarehouse().getWarehouseFloor();
    ArrayList<String[]> linesToWrite = new ArrayList<String[]>();
    for (String location : floor.keySet()) {
      Integer skuQuantity = sysController.getWarehouse().getStock(location);
      if (skuQuantity != 30) {
        String[] splitLocation = location.split("");
        linesToWrite.add(new String[] {splitLocation[0], splitLocation[1], splitLocation[2],
            splitLocation[3], skuQuantity.toString()});
      }
    }
    ReadAndWrite.writeFile(fileDirectory, "final.csv", linesToWrite);
    RunWarehouse.logger.info("Generated report: final.csv");
  }

  /**
   * Creates and stores a report of all <code>Order</code> that were successfully loaded onto a
   * truck. <code>Order</code> which were sequenced but not loaded are not included.
   * 
   * @param fileDirectory Path of the directory to write the reports in.
   * @throws IOException when input and output files cannot be write or read.
   */
  private void createOrderReport(Path fileDirectory) throws IOException {
    ArrayList<String[]> ordersToWrite = new ArrayList<String[]>();
    ArrayList<Order> loadedOrders = getLoadedOrders();
    for (Order order : loadedOrders) {
      String[] line = {order.toString()};
      ordersToWrite.add(line);
    }
    ReadAndWrite.writeFile(fileDirectory, "orders.csv", ordersToWrite);
    RunWarehouse.logger.info("Generated report: orders.csv");
  }

  /**
   * Retrieve all <code>Order</code> that have been successfully loaded.
   * 
   * @return a list of <code>Order</code> that have a status of loaded.
   */
  private ArrayList<Order> getLoadedOrders() {
    ArrayList<Order> loaded = new ArrayList<>();
    ArrayList<Order> allOrders = sysController.getOrders().getOrderArchive();
    for (Order order : allOrders) {
      if (order.getStatus() == "loaded") {
        loaded.add(order);
      }
    }
    return loaded;
  }

  /**
   * Creates a single String element out of a String Array.
   * 
   * @param line the <code>String[]</code> to combine.
   * @return a single String composed of the String Array.
   */
  private String stringBuilder(String[] line) {
    StringBuilder builder = new StringBuilder();
    for (String stringItem : line) {
      builder.append(stringItem + " ");
    }
    return builder.toString();
  }

  /**
   * Creates a file to log events that occur in the warehouse.
   * 
   * @param filePath <code>Path</code> of the directory to write the log in.
   * @throws IOException when input and output files cannot be write or read.
   */
  protected void createLogFile(Path filePath) throws IOException {
    FileHandler handler = null;
    try {
      String pathPattern = filePath.toString() + File.separatorChar + "log.txt";
      handler = new FileHandler(pathPattern, false);
      RunWarehouse.logger.addHandler(handler);
      handler.setLevel(Level.ALL);
      handler.setFormatter(new SimpleFormatter());
    } catch (SecurityException error) {
      RunWarehouse.logger.warning(error.toString());;
    }
  }
}
