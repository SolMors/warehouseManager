package project;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * The <code>PickRequest</code> for the warehouse. Used to track and store information about groups
 * of 4 <code>Order</code> throughout the warehouse.
 */
class PickRequest {

  /** Unique ID for this <code>PickRequest</code> object. */
  private int pickRequestId;

  /** Stores <code>Order</code> in the order in which they were received. */
  protected LinkedList<Order> sequentialOrders = new LinkedList<>();

  /** The status of this <code>PickRequest</code>. */
  protected String status;

  /** <code>Pallet</code> used by <code>Picker</code>, contains unsorted SKUs. */
  protected Pallet unsortedPal = new Pallet(8);

  /** <code>Pallet</code> used by <code>Sequencer</code>, contains sorted front SKUs. */
  protected Pallet frontPal = new Pallet(4);

  /** <code>Pallet</code> used by <code>Sequencer</code>, contains sorted rear SKUs. */
  protected Pallet rearPal = new Pallet(4);

  /** The order in which SKUs should be picked, received from third party. */
  private ArrayList<String> pickOrder = null;

  /**
   * Creates new <code>PickRequest</code>. Transfers the <code>orderPuragtory</code>
   * <code>Order</code> into a list to keep track of the correct order in which they were received.
   * 
   * @param orders a list of <code>Order</code> which come as a set of four.
   * @param pickReqId the unique ID recieved from the <code>OrderManager</code>.
   */
  public PickRequest(LinkedList<Order> orders, int pickReqId) {
    for (Order ord : orders) {
      this.sequentialOrders.add(ord);
    }
    this.pickRequestId = pickReqId;
    status = "created";
    RunWarehouse.logger.info("Generated PickRequest " + pickRequestId);
  }

  /**
   * Get the optimized picking order. Called by <code>Picker</code>.
   * 
   * @param warehouse The warehouse worker is working in.
   * @return pickOrder, a list of SKU locations.
   */
  protected ArrayList<String> getPickOrder(WarehouseManager warehouse) {
    // If the pickorder has not been generated, call optimize to do that.
    if (pickOrder == null) {
      ArrayList<String> toBeProcessed = new ArrayList<String>();
      for (Order ord : this.sequentialOrders) {
        toBeProcessed.add(ord.getContents().get(0));
        toBeProcessed.add(ord.getContents().get(1));
      }
      pickOrder = WarehousePicking.optimize(toBeProcessed, warehouse);
    }
    return pickOrder;
  }

  /**
   * Return the ID for this <code>PickRequest</code>.
   * 
   * @return <code>int</code> representing the ID of the <code>PickRequest</code>.
   */
  protected int getPickRequestId() {
    return pickRequestId;
  }

  /**
   * Updated the status of this <code>PickRequest</code>. Possible <code>newStatus</code> are:
   * picked / sequenced / loaded If the <code>newStatus</code> is not from the list of valid States
   * raise an error.
   * 
   * @param newStatus the new status for the pick request.
   */
  protected void updateStatus(String newStatus) {
    List<String> validStates = Arrays.asList("picked", "sequenced", "loaded");
    if (validStates.contains(newStatus)) {
      status = newStatus;
    } else {
      RunWarehouse.logger.warning(newStatus + "is not a valid status for this PickRequest.");
    }
  }


  /**
   * Update the order status for all <code>Order</code> in the <code>sequentialOrders</code> list.
   * 
   * @param newStatus the new status for order
   */
  protected void updateAllOrders(String newStatus) {
    for (Order order : sequentialOrders) {
      order.updateStatus(newStatus);
    }
  }

  /**
   * Return the sorted front <code>Pallet</code>.
   * 
   * @return the <code>frontPal</code>.
   */
  protected Pallet getFrontPallet() {
    return frontPal;
  }

  /**
   * Return the sorted rear <code>Pallet</code>.
   * 
   * @return the <code>rearPal</code>.
   */
  protected Pallet getRearPallet() {
    return rearPal;
  }

  /**
   * Return the pick request status.
   * 
   * @return a <code>String</code> representation of the <code>PickRequest</code> status.
   */
  protected String getStatus() {
    return status;
  }

  /**
   * Return <code>true</code> if the picking pallet is full.
   * 
   * @return <code>true</code> if this <code>Pallet</code> is full.
   */
  protected boolean pickPalFull() {
    return (unsortedPal.isFull());
  }

  /**
   * Return the list of <code>sequentialOrders</code> contained in this <code>PickRequest</code>.
   * 
   * @return The list of <code>sequentialOrders</code>.
   */
  protected LinkedList<Order> getSeqOrds() {
    return sequentialOrders;
  }

}
