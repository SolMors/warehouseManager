package project;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * Contains the <code>warehouseFloor</code>, and manages inventory.
 */
public class WarehouseManager {

  /** Stores the SKUs and their stock quantities in a location in the warehouse. */
  private HashMap<String, PickFace> warehouseFloor = new HashMap<>();

  /** Keeps track of all <code>PickFace</code> that need to be replenished. */
  private LinkedList<PickFace> toReplenish = new LinkedList<>();

  /** Path of the file directory where all pertinent .csv files are kept. */
  private Path filePath;

  /**
   * Creates a warehouse given a file that specifies location of SKUs. Sets all quantities to 30,
   * unless otherwise specified in the stockLevels.csv file.
   * 
   * @param directory is a file path to the directory in which traversal_table.csv and
   *        stockLevels.csv are stored.
   * @throws IOException file is removed or otherwise ceases to exist during reading.
   * @throws FileNotFoundException if a file cannot be found prints an error message that it could
   *         not be found.
   */
  public WarehouseManager(Path directory) throws FileNotFoundException, IOException {
    filePath = directory;
    createWarehouse();
    updateStockLevels();
  }

  /**
   * Creates a brand new warehouse according to traversal_table.csv. Each pick face in the warehouse
   * is initially set to 30.
   * 
   * @throws IOException file is removed or otherwise ceases to exist during reading.
   * @throws FileNotFoundException if a file cannot be found prints an error message that it could
   *         not be found.
   */
  private void createWarehouse() throws FileNotFoundException, IOException {
    ArrayList<String[]> fileContents =
        ReadAndWrite.readFile(filePath.resolve("traversal_table.csv"), ",", false);
    for (String[] line : fileContents) {
      String location = line[0] + line[1] + line[2] + line[3];
      PickFace pickFace = new PickFace(line[4], 30);
      warehouseFloor.put(location, pickFace);
    }
  }

  /**
   * Updates the warehouse with the initial stock levels found in stockLevels.csv, any locations not
   * included in stockLevels.csv start the day with a quantity of 30.
   * 
   * @throws IOException file is removed or otherwise ceases to exist during reading.
   * @throws FileNotFoundException if a file cannot be found prints an error message that it could
   *         not be found.
   */
  private void updateStockLevels() throws FileNotFoundException, IOException {
    ArrayList<String[]> fileContents =
        ReadAndWrite.readFile(filePath.resolve("initial.csv"), ",", true);
    for (String[] line : fileContents) {
      String location = line[0] + line[1] + line[2] + line[3];
      PickFace pickFace = warehouseFloor.get(location); // Find the pick face.
      pickFace.setStockQty(Integer.valueOf(line[4])); // Set the quantity of the pick face
    }
  }


  /**
   * An item is picked from this inventory location, decrementing this <code>PickFace</code> by 1.
   * Called by the <code>Picker</code>. Checks inventory level to see if a refill is required, if so
   * generates a <code>replenishRequest</code>.
   * 
   * @param location The inventory location of the pick.
   */
  protected void pick(String location) {
    PickFace pickFace = warehouseFloor.get(location);
    if (pickFace.getStockQty() > 0) {
      pickFace.removeFromStock(1);
    } else {
      RunWarehouse.logger.warning("Can not pick from this location, inventory is 0.");
    }
    if (pickFace.getStockQty() <= 5) {
      replenishRequest(location, pickFace);
    }
  }

  /**
   * Return an item back to its location, increment <code>PickFace</code> at this location by one.
   * 
   * @param location a <code>String</code> identification of a <code>PickFace</code> in the
   *        warehouse.
   */
  protected void putBack(String location) {
    PickFace pickFace = warehouseFloor.get(location);
    pickFace.addToStock(1);
  }

  /**
   * Replenishes a <code>PickFace</code> at a location in the warehouse with 25 more of that
   * particular SKU.
   * 
   * @param location a <code>String</code> identification of a <code>PickFace</code> in the
   *        warehouse.
   */
  protected void replenish(PickFace pickFace) {
    if (pickFace.getStockQty() <= 5) {
      pickFace.addToStock(25);
    } else {
      pickFace.setStockQty(30);
    }
    RunWarehouse.logger.info("SKU # " + pickFace.getSku() + " replenished. Current stock at "
        + pickFace.getStockQty() + ".");
  }

  /**
   * Triggered when <code>PickFace</code> has a quantity less than or equal to 5. Creates a request
   * to replenish the location of this <code>PickFace</code>.
   * 
   * @param location a <code>String</code> identification of a <code>PickFace</code> in the
   *        warehouse.
   * @param pickFace The SKU and number of that SKU in the <code>PickFace</code>.
   */
  private void replenishRequest(String location, PickFace pickFace) {
    if (!toReplenish.contains(pickFace)) {
      toReplenish.add(pickFace);
    }
    RunWarehouse.logger.info("SKU # " + pickFace.getSku() + " at " + location
        + " needs to be replenished. " + pickFace.getStockQty() + " items remaining.");
  }

  /**
   * Takes an array of SKUs and returns their locations in the warehouse.
   * 
   * @param Skus <code>ArrayList</code> of <code>String</code> representing SKUs.
   * @return List of String SKU locations.
   */
  protected ArrayList<String> getSkuLocs(ArrayList<String> skus) {
    ArrayList<String> skuLocs = new ArrayList<String>();
    for (String sku : skus) {
      skuLocs.add(getSkuLocation(sku));
    }
    return skuLocs;
  }

  /**
   * Return the stock level in a <code>PickFace</code> at target location.
   * 
   * @param location a <code>String</code> identification of a <code>PickFace</code> in the
   *        warehouse.
   * @return <code>int</code> representing the stock amount in this <code>PickFace</code>.
   */
  protected int getStock(String location) {
    return warehouseFloor.get(location).getStockQty();
  }

  /**
   * Return the SKU number associated with this <code>PickFace</code> at target location.
   * 
   * @param location a <code>String</code> identification of a <code>PickFace</code> in the
   *        warehouse.
   * @return String representation of the SKU in this <code>PickFace</code>.
   */
  protected String getSku(String location) {
    return warehouseFloor.get(location).getSku();
  }

  /**
   * Given a SKU retrieve a location.
   * 
   * @param sku to find the location of.
   * @return <code>String</code> representation of the location.
   */
  protected String getSkuLocation(String sku) {
    for (String key : warehouseFloor.keySet()) {
      if (warehouseFloor.get(key).getSku().equals(sku)) {
        return key;
      }
    }
    return null;
  }

  /**
   * Return the first <code>PickFace</code> that needs to be replenished.
   * 
   * @return next <code>PickFace</code> that needs to be replenished
   */
  protected PickFace getNextReplenish() {
    return toReplenish.removeFirst();
  }

  /**
   * Get the warehouse floor.
   * 
   * @return the <code>warehouseFloor</code>.
   */
  protected HashMap<String, PickFace> getWarehouseFloor() {
    return warehouseFloor;
  }

}


