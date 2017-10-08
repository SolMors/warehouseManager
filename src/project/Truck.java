package project;

/**
 * The truck class, representing trucks being loaded with <code>Pallet</code> containing items from
 * the <code>Order</code>.
 */
public class Truck {

  /** Unique ID for this <code>Truck</code>. */
  private int truckId;

  /**
   * The bed of <code>Truck</code> where <code>Pallet</code> are loaded. Each index contains one
   * <code>Pallet</code>.
   */
  private Pallet[] truckBed;

  /** The number of <code>Pallet</code> that can be loaded onto the <code>truckBed</code>. */
  private int bedSize = 40;

  /**
   * The next position in the <code>truckBed</code> to be loaded. Represents the fullness of the
   * <code>Truck</code>.
   */
  private int loadPos;

  /**
   * Constructs a <code>Truck</code>. Will be filled with <code>Pallet</code>.
   */
  public Truck(int truckCount) {
    truckId = truckCount;
    truckBed = new Pallet[bedSize];
    loadPos = 0;

  }

  /**
   * Load the <code>Pallet</code> onto the <code>Truck</code> in the next open position on
   * <code>truckBed</code>. Increments <code>loadPos</code> by one.
   * 
   * @param <code>Pallet</code> being loaded on the truck.
   */
  protected void load(Pallet pallet) {
    truckBed[loadPos] = pallet;
    loadPos++;
  }

  /**
   * Return this truck's identification number.
   * 
   * @return <code>int</code> representing this <code>Truck</code> ID.
   */
  protected int getTruckId() {
    return truckId;
  }

  /**
   * Return the next spot on the truck to be loaded.
   * 
   * @return <code>int</code> representing the next spot on <code>truckBed</code> to be loaded.
   */
  protected int getLoadPos() {
    return loadPos;
  }
}
