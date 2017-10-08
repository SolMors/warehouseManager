package project;

import java.util.ArrayList;

/** A <code>Picker</code> worker in the warehouse. */
public class Picker extends Worker {

  /**
   * Pick order of locations received from <code>WarehousePicking</code> via
   * <code>PickRequest</code>.
   */
  protected ArrayList<String> pickOrder;

  /** Active <code>PickRequest</code>. */
  protected PickRequest pickReq;

  /**
   * Constructs new <code>Picker</code> with a name and a <code>Controller</code>, giving
   * <code>Picker</code> access to the rest of the warehouse.
   * 
   * @param name The Picker's name.
   * @param controller The warehouse <code>Controller</code>.
   */
  public Picker(String name, Controller controller) {
    super(name, controller);
  }

  /**
   * Assign a new <code>PickRequest</code> to this <code>Picker</code>. If this <code>Picker</code>
   * is ready for a new <code>PickRequest</code>, it gets one from <code>OrderManager</code>. The
   * <code>pickOrder</code> is assigned here as well.
   */
  protected void receive() {
    if (this.isReady()) {
      pickReq = controller.getOrders().getNewPickReq();
      if (pickReq != null) {
        pickOrder = pickReq.getPickOrder(controller.getWarehouse());
        RunWarehouse.logger.info("Picker " + name + " received new pick request.");
        this.setBusy();
      } else {
        RunWarehouse.logger.info("No pick requests available. " + name + " ready.");
      }
    }
  }

  /**
   * Picks the supplied SKU from inventory, if it is the next one to be picked on this
   * <code>pickOrder</code>.
   * 
   * @param sku The SKU number to be picked from inventory.
   */
  protected void work(String sku) {
    String skuLoc = controller.getWarehouse().getSkuLocation(sku);
    controller.getWarehouse().pick(skuLoc);
    if (pickReq.pickPalFull()) {
      RunWarehouse.logger
          .warning("Pallet is already full. Maximum number of bumpers have been picked.");
    } else {
      if (!skuLoc.equals(pickOrder.get(progress))) {
        controller.getWarehouse().putBack(skuLoc);
        RunWarehouse.logger.warning("Wrong item! Please pick item at " + pickOrder.get(progress)
            + ". Returning " + sku + ".");
      } else {
        pickReq.unsortedPal.add(sku);
        progress++;
        RunWarehouse.logger
            .info("Picker " + this.name + " picks faschia " + sku + " from location " + skuLoc);
      }
    }
  }

  /**
   * After pick, check if the <code>Picker</code> is done with this order. If yes, mark orders as
   * picked and send to <code>marshalQueue</code>.
   */
  @Override
  protected void push() {
    updateStatus("picked");
    RunWarehouse.logger.info("Picker " + this.name + " takes PickRequest "
        + pickReq.getPickRequestId() + " to Marshaling");
    controller.getStaging().marshalAdd(pickReq);
    progress = 0;
    this.setReady();
  }

  /**
   * Before a <code>PickRequest</code> is marshaled, update the status on all orders in that
   * <code>PickRequest</code> and the status of <code>PickRequest</code> itself to picked.
   */
  private void updateStatus(String newStatus) {
    pickReq.updateAllOrders(newStatus);
    pickReq.updateStatus(newStatus);

  }


}
