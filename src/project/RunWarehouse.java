package project;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

/** Main class that facilitates the running of the Warehouse for simulation purposes. */
public class RunWarehouse {

  /** Creates a logger to keep track of events that occur in the warehouse. */
  protected static final Logger logger = Logger.getLogger(RunWarehouse.class.getName());

  /** Prints logged information to the console. */
  private static final Handler consoleHandler = new ConsoleHandler();

  /** Retrieves the path to the current directory. */
  static Path currentRelativePath = Paths.get(System.getProperty("user.dir"));

  /**
   * Runs an instance of the Warehouse given the directory where all files are to be stored and
   * retrieved, and that days list of events.
   * 
   * @throws IOException when input and output files cannot be write or read.
   */

  public RunWarehouse() throws IOException {
    logger.setLevel(Level.ALL);
    consoleHandler.setLevel(Level.ALL);
    logger.addHandler(consoleHandler);
  }

  /**
   * Executes the program given the simulation and creates a report when finished.
   * 
   * @param args Input the simulation to run.
   * @throws IOException when input and output files cannot be write or read.
   */
  public static void main(String[] args) throws IOException {
    RunWarehouseHelper helper = new RunWarehouseHelper();
    helper.createLogFile(currentRelativePath);
    String simulation = args[0];
    logger.info("simluation is about to take place");
    helper.run(currentRelativePath, simulation);
    System.out.print("Would you like to run another simulation? Y/N: ");
    BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
    String runAnother = input.readLine();
    if (runAnother.equals("Y")) {
      System.out.print("Enter the simulation you would like to run: ");
      String nextSimulation = input.readLine();
      helper.run(currentRelativePath, nextSimulation);
    } else {
      logger.info("Simulation terminated.");
      System.exit(1);
    }
  }
}
