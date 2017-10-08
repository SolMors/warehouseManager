package project;

/** A <code>Sequencer</code> worker in the Warehouse. */
public class Sequencer extends Worker {

  /**
   * Constructs new <code>Sequencer</code> worker with a name and <code>Controller</code>, giving
   * this <code>Sequencer</code> access to the rest of the Warehouse.
   * 
   * @param name This <code>Sequencer</code> name.
   * @param controller The warehouse <code>Controller</code>.
   */
  public Sequencer(String name, Controller controller) {
    super(name, controller);
  }

  /**
   * Assign a new <code>PickRequest</code> to this <code>Sequencer</code>. If this
   * <code>Sequencer</code> is ready, it gets new <code>unsortedPal</code> from the Staging area.
   */
  protected void receive() {
    if (this.isReady()) {
      pickReq = controller.getStaging().marshalRemove();
      progress = 0;
      if (pickReq != null) {
        RunWarehouse.logger.info("Sequencer " + name + " received new pallet to sequence.");
        this.setBusy();
      } else {
        RunWarehouse.logger.info("No pallets available for marshaling. " + name + " ready.");
      }
    } else {
      RunWarehouse.logger.info("Sequencer " + name + " is already busy sequencing a pallet.");
    }
  }

  /**
   * Take the SKU from the <code>unsortedPal</code> and put it in position on the correct sequenced
   * pallet, <code>frontPal</code> or <code>rearPal</code>.
   * 
   * @param sku The SKU to be sequenced.
   */
  protected void work(String sku) {
    int frontOrRear = progress % 2; // 0 is front pallets, 1 is rear pallet.
    String correctSku = pickReq.sequentialOrders.get(progress / 2).getContents().get(frontOrRear);
    // After a rescan event - check if the sku is already on the pallet
    if (rescanCheck(frontOrRear, sku)) {
      RunWarehouse.logger.info("Sequencer " + this.name + " sequenced " + sku + ".");
    } else if (sku.equals(correctSku)) {
      if (seqCheck(sku)) {
        load(frontOrRear, sku);
        pickReq.unsortedPal.remove(sku);
        progress++;
        RunWarehouse.logger.info("Sequencer " + this.name + " sequenced " + sku + ".");
      } else { // not sequencing the correct sku
        throwOut();
        RunWarehouse.logger
            .warning(sku + " is not on the unsorted pallet. Sending PickRequest to be re-picked.");
      }
    } else {
      RunWarehouse.logger.warning(
          sku + " is not the correct SKU to sequence. Please sequence " + correctSku + " next.");
    }
  }


  /**
   * Once all items in the orders have been sequenced, move the <code>frontPal</code> and
   * <code>rearPal</code> to the <code>loadZone</code>. Sets this <code>Sequencer</code> to ready.
   */
  @Override
  protected void push() {
    pickReq.updateStatus("sequenced");
    controller.getStaging().loadAdd(pickReq);
    RunWarehouse.logger.info("Sequencer " + this.name + " moves PickRequest "
        + pickReq.getPickRequestId() + " to load zone.");
    progress = 0;
    this.setReady();
  }

  /**
   * Helper method to check if a SKU is on the <code>PickRequest</code> <code>unsortedPal</code>,
   * meaning it was successfully picked.
   * 
   * @param sku SKU number to check if on <code>Pallet</code>.
   * @return <code>true</code> if the SKU is on the <code>Pallet</code>.
   */
  private boolean seqCheck(String sku) {
    return pickReq.unsortedPal.contains(sku);
  }

  /**
   * Place the front and rear bumpers on their <code>Pallet</code> in the correct order.
   * 
   * @param frontOrRear 0 represents the front pallet. 1 represents the rear pallet.
   * @param sku The SKU to be loaded onto the <code>Pallet</code>.
   */
  private void load(int frontOrRear, String sku) {
    if (frontOrRear == 0) {
      pickReq.frontPal.add(sku);
      RunWarehouse.logger.info(sku + " loaded on to Front Pallet");
    } else {
      pickReq.rearPal.add(sku);
      RunWarehouse.logger.info(sku + " loaded on to Rear Pallet");
    }
  }

  /**
   * Return <code>true</code> if the SKU is already on the <code>frontPal</code> or
   * <code>rearPal</code> in the correct place. This represents a recheck of the SKUs on the
   * <code>Pallet</code>.
   * 
   * @param frontOrRear 0 represents <code>frontPal</code>. 1 represents <code>rearPal</code>.
   * @param sku The SKU number being checked.
   * @return <code>true</code> if the SKU is on the <code>Pallet</code>.
   */
  private boolean rescanCheck(int frontOrRear, String sku) {
    String loaded = null;
    if (frontOrRear == 0) {
      loaded = pickReq.frontPal.getItemAtPosition(progress / 2);
    } else {
      loaded = pickReq.rearPal.getItemAtPosition(progress / 2);
    }
    if (loaded != null) {
      if (loaded.equals(sku)) {
        progress++;
        return true;
      }
    }
    return false;
  }

  /**
   * Reset <code>progress</code> of this <code>Sequencer</code> and check the sequenced pallets from
   * beginning again.
   */
  protected void rescan() {
    progress = 0;
    RunWarehouse.logger.info("Sequencer " + name + " rescans. Begin checking from beginning.");
  }

  /**
   * Throw out all SKUs on all <code>Pallet</code> for this <code>PickRequest</code>, and send it
   * back to <code>activePickRequest</code> queue.
   */
  private void throwOut() {
    // Clean the pallets
    pickReq.frontPal.clear();
    pickReq.rearPal.clear();
    pickReq.unsortedPal.clear();
    controller.getOrders().returnPickReq(pickReq);
    pickReq = null;
    progress = 0;
    this.setReady();
  }
}
