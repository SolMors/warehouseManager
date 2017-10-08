package project;

import java.util.LinkedList;

/** <code>TruckManager</code> tracks all <code>Truck</code> loaded at the warehouse. */

public class TruckManager {
  /**
   * ID that increments with each new <code>Truck</code>. Used to generate the <code>truckId</code>.
   */
  private int nextTruckId = 0;

  /**
   * List of all <code>Truck</code> that have been loaded at the warehouse.
   */
  private LinkedList<Truck> truckList = new LinkedList<>();

  /** Constructs a <code>TruckManager</code> for the warehouse. */
  public TruckManager() {
    Truck firstTruck = new Truck(nextTruckId);
    nextTruckId++;
    truckList.add(firstTruck);
  }

  /** A new <code>Truck</code> arrives at the warehouse to be loaded. */
  protected void newTruck() {
    Truck newTruck = new Truck(nextTruckId);
    truckList.add(newTruck);
    nextTruckId++;
  }

  /**
   * Load one <code>PickRequest</code> worth of SKUs onto the active <code>Truck</code>.
   * <code>rearPal</code> is loaded first.
   * 
   * @param <code>PickRequest</code> containing the sequenced <code>frontPal</code> and
   *        <code>rearPal</code>.
   */
  protected void load(PickRequest pickReq) {
    truckList.getLast().load(pickReq.getRearPallet());
    truckList.getLast().load(pickReq.getFrontPallet());
  }

  /**
   * Return <code>truckId</code> for the <code>Truck</code> currently being loaded.
   * 
   * @return <code>int</code> of <code>Truck</code> that is being loaded.
   */
  protected int getActiveTruckId() {
    return truckList.getLast().getTruckId();
  }

  /**
   * Returns the next <code>PickRequestId</code> that should be loaded on the truck.
   * 
   * @return <code>int</code> representing the unique ID of <code>PickRequest</code>.
   */
  protected int getNextPickId() {
    return (truckList.getLast().getLoadPos() / 2);
  }
}
