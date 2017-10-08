package project;

import java.util.ArrayList;
import java.util.Collections;

/** <code>WarehousePicking</code> represents the third party picking software. */
public class WarehousePicking {

  /**
   * Returns an optimized picking order as locations, given a list of SKUs.
   * 
   * @param skuNums <code>ArrayList</code> of SKU numbers to be picked.
   * @param warehouse The warehouse we are working in.
   * @return List of <code>String</code> locations for the SKU to be picked, optimized for speed.
   */
  protected static ArrayList<String> optimize(ArrayList<String> skuNums,
      WarehouseManager warehouse) {
    Collections.sort(skuNums);
    ArrayList<String> optimizedOrder = new ArrayList<>();
    optimizedOrder = warehouse.getSkuLocs(skuNums);

    return optimizedOrder;
  }

}
