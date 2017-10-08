package project;

/**
 * A <code>Replenisher</code> worker in the warehouse. When the <code>Replenisher</code> receives a
 * <code>replenishRequest</code>, he moves 25 SKUs of the appropriate type from the reserve room to
 * its <code>PickFace</code>.
 */
public class Replenisher extends Worker {

  /** The <code>PickFace</code> that needs to be replenished. */
  private PickFace replenishThis;

  /**
   * Constructs new <code>Replenisher</code> worker in the Warehouse with <code>name</code> and
   * <code>Controller</code> allowing him to interact with the rest of the Warehouse.
   * 
   * @param name This <code>Replenisher</code> name.
   * @param controller The <code>Controller</code> for this particular Warehouse.
   */
  public Replenisher(String name, Controller controller) {
    super(name, controller);
  }

  /** Gets a <code>PickFace</code> to replenish from the warehouse. */
  protected void receive() {
    replenishThis = controller.getWarehouse().getNextReplenish();
  }

  /**
   * Replenishes stock at input location in the warehouse. Check to make sure the location is the
   * one received by the <code>replenishRequest</code> Triggered when the location has 5 or less SKU
   * remaining.
   * 
   * @param location The <code>PickFace</code> location coordinate to be replenished.
   */
  protected void work(String location) {
    if (!(replenishThis == null)) {
      if (controller.getWarehouse().getWarehouseFloor().get(location).equals(replenishThis)) {
        controller.getWarehouse().replenish(replenishThis);
        replenishThis = null;
      } else {
        RunWarehouse.logger.warning("Output: Replenisher " + name
            + " does not have a replenish request corresponding to this location.");
      }
    } else {
      RunWarehouse.logger
          .warning("Output: Replenisher " + name + " does not have a replenish request");
    }
  }
}
