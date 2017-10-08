package project;

/** A Loader worker in the warehouse. */
public class Loader extends Worker {

  /** The pick ID for the next set of orders to be loaded onto the truck. */
  private int nextPickId;

  /**
   * Constructs new <code>Loader</code> worker with a name and <code>Controller</code> which allows
   * this <code>Loader</code> access to various parts of this warehouse.
   * 
   * @param name The loader's name.
   * @param controller The warehouse's <code>Controller</code>.
   */
  public Loader(String name, Controller controller) {
    super(name, controller);
  }

  /**
   * Get the next <code>PickRequest</code> to be loaded on the truck from the <code>loadZone</code>,
   * if it is there.
   */
  @Override
  protected void receive() {
    TruckManager truckMan = controller.getTrucks();
    StagingManager stagMan = controller.getStaging();
    nextPickId = truckMan.getNextPickId();
    pickReq = stagMan.loadRemove(nextPickId);
    if (pickReq == null) {
      // There are no pick requests waiting in the load zone
      if (stagMan.getLoadZoneSize() == 0) {
        RunWarehouse.logger.info("No pick requests in the loading zone.");
        // The desired pick request (by ID) is not waiting in the load
        // zone
      } else {
        RunWarehouse.logger.info(name
            + " can not load this PickRequest, waiting for correct PickRequest to arrive first.");
      }
      // The correct one is there. Set the worker to busy.
    } else {
      RunWarehouse.logger.info("Loader " + name + " received sequenced pallets to load.");
      this.setBusy();
    }
  }

  /**
   * Check the sequence of the SKU numbers on the <code>Pallet</code> against the
   * <code>PickRequest</code> order.
   * 
   * @param sku The SKU number to be checked for order.
   */
  protected void work(String sku) {
    if (pickReq != null) {
      int frontOrRear = progress % 2; // 0 is front pallets, 1 is rear pallet.
      int loadpos = progress / 2;
      String correctSku = pickReq.getSeqOrds().get(loadpos).getContents().get(frontOrRear);
      if (sku.equals(correctSku)) { // sku matches the correct sequential order.
        check(frontOrRear, sku, loadpos);
      } else { // not sequencing the correct sku
        RunWarehouse.logger
            .warning(sku + " is not the next SKU to check. Please check " + correctSku + " next.");
      }
    } else {
      RunWarehouse.logger.info("Loader has no active PickRequest to check.");
    }
  }

  /**
   * <code>Loader</code> checks the position of a SKU number on its sequenced <code>Pallet</code>.
   * 
   * @param frontOrRear <code>int</code> indicating whether the SKU is on the <code>frontPal</code>
   *        or <code>rearPal</code>.
   * @param sku Items SKU number.
   * @param loadpos Item's load position on the <code>Pallet</code>.
   */
  private void check(int frontOrRear, String sku, int loadpos) {
    if (frontOrRear == 0) {
      if (pickReq.getFrontPallet().getItemAtPosition(loadpos).equals(sku)) {
        progress++;
        RunWarehouse.logger.info(sku + " is sequenced correctly.");
      } else {
        RunWarehouse.logger.warning(sku + " is sequenced incorrectly.");
      }
    } else {
      if (pickReq.getRearPallet().getItemAtPosition(loadpos).equals(sku)) {
        progress++;
        RunWarehouse.logger.info(sku + " is sequenced correctly.");
      } else {
        RunWarehouse.logger.info(sku + " is sequenced incorrectly.");
      }
    }
  }


  /** Load the <code>Pallet</code> onto <code>Truck</code>. */
  @Override
  protected void push() {
    if (pickReq != null) {
      TruckManager truckMan = controller.getTrucks();
      truckMan.load(pickReq);
      RunWarehouse.logger
          .info("Loader " + this.name + " loads PickRequest " + pickReq.getPickRequestId());
      pickReq.updateAllOrders("loaded");
      pickReq.updateStatus("loaded");
      this.setReady();
      pickReq = null;
      progress = 0;
    } else {
      RunWarehouse.logger.info("Loader has no active PickRequest to load.");
    }
  }

  /**
   * Causes <code>Loader</code> to start checking the sequenced pallets from the beginning again.
   */
  protected void rescan() {
    progress = 0;
    RunWarehouse.logger.info("Loader " + name + " rescans. Begin checking from beginning.");
  }
}
