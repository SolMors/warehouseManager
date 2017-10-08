package project;

/**
 * A location in the warehouse that contains the item associated with a SKU and the number of those
 * SKU in stock.
 */
public class PickFace {

  /**
   * An alpha-numeric value to identify the contents of this <code>PickFace</code>.
   */
  private String sku;

  /**
   * The quantity of items in this <code>PickFace</code>.
   */
  private int stockQuantity;

  /**
   * Initializes a <code>PickFace</code> with a SKU and the amount of initial stock.
   * 
   * @param sku represents the item in this pick face.
   * @param stock amount of the item in this pick face.
   */
  public PickFace(String sku, int stock) {
    this.sku = sku;
    this.stockQuantity = stock;
  }

  /**
   * Decrease <code>stockQuantity</code> by a given amount.
   * 
   * @param amount to decrease stockQuantity by.
   */
  protected void removeFromStock(int amount) {
    stockQuantity -= amount;
  }

  /**
   * Increase <code>stockQuantity</code> by a given amount.
   * 
   * @param amount to increase stockQuantity by.
   */
  protected void addToStock(int amount) {
    stockQuantity += amount;
  }

  /**
   * Set the amount of <code>stockQuantity</code>, used to change the <code>stockQuantity</code>
   * when current quantity is unknown.
   * 
   * @param amount to set the <code>stockQuantity</code> to.
   */
  protected void setStockQty(int amount) {
    stockQuantity = amount;
  }

  /**
   * The alpha-numeric value of the item in this <code>PickFace</code>.
   * 
   * @return <code>String</code> representing the SKU.
   */
  protected String getSku() {
    return sku;
  }

  /**
   * The amount of stock currently in inventory.
   * 
   * @return <code>int</code> representing the quantity.
   */
  protected int getStockQty() {
    return stockQuantity;
  }
}
