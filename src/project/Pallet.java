package project;

/**
 * <code>Pallet</code> class, representing a pallet in the warehouse. Can either be a larger one
 * used by <code>Picker</code> or a smaller one to be loaded on the truck.
 */

public class Pallet {

  /**
   * Stores the SKUs loaded on this <code>Pallet</code>. Unfilled space represented by a
   * <code>null</code> entry.
   */
  private String[] surface;

  /** Tracks the number of SKUs loaded onto <code>Pallet</code>. */
  private int fillProg = 0;

  /**
   * Constructs new <code>Pallet</code> object that can hold <code>size</code> number of SKUs.
   * 
   * @param size the number of bumpers this pallet can hold.
   */
  public Pallet(int size) {
    this.surface = new String[size];
  }

  /**
   * Add a SKU to <code>Pallet</code>.
   * 
   * @param sku The SKU to be added to <code>Pallet</code>.
   */
  protected void add(String sku) {
    surface[fillProg] = sku;
    fillProg++;
  }

  /**
   * Returns the <code>Pallet</code> filling progress.
   * 
   * @return <code>int</code> representing the fullness of this <code>Pallet</code>.
   */
  protected int getFillProg() {
    return fillProg;
  }

  /**
   * Returns <code>true</code> if the <code>Pallet</code> is full.
   * 
   * @return <code>true</code> if the <code>Pallet</code> is full.
   */
  protected boolean isFull() {
    return (fillProg >= surface.length);
  }

  /**
   * Returns the SKU loaded on <code>Pallet</code> at the given position.
   * 
   * @param position The position on <code>Pallet</code> to inspect.
   * @return SKU number at given position.
   */
  protected String getItemAtPosition(int position) {
    return surface[position];
  }

  /**
   * Removed the given SKU from the <code>Pallet</code>.
   * 
   * @param sku <code>String</code> representing SKU number.
   */
  protected void remove(String sku) {
    for (int index = 0; index < surface.length; index++) {
      if (surface[index] != null) {
        if (surface[index].equals(sku)) {
          surface[index] = null;
          break;
        }
      }
    }
  }

  /**
   * Checks if <code>Pallet</code> contains the given SKU.
   * 
   * @param sku SKU number to look for on <code>Pallet</code>.
   * @return <code>true</code> if the <code>Pallet</code> contains the given SKU.
   */
  protected boolean contains(String sku) {
    for (int index = 0; index < surface.length; index++) {
      if (surface[index] != null) {
        if (surface[index].equals(sku)) {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * Clear <code>surface</code>. All the SKUs are put in the garbage, represented by setting the
   * entries in the surface array to <code>null</code>.
   */
  protected void clear() {
    for (int i = 0; i < surface.length; i++) {
      surface[i] = null;
    }
  }
}
