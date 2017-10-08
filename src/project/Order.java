package project;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * An <code>Order</code> for the Warehouse. Each <code>Order</code> has a unique ID, a status
 * relating to its stage in the work flow, and all ordered items in its contents.
 */
public class Order {

  /** A list of SKU numbers corresponding to the ordered items. */
  private ArrayList<String> contents;

  /**
   * Status of this <code>Order</code>. Possible states: created / purgatory / picked / sequenced /
   * loaded
   */
  private String status = "created";

  /** Unique ID for the order. */
  private int orderId;

  /**
   * Creates new <code>Order</code> in the warehouse. Has a unique order ID number and a a list of
   * SKU contents.
   * 
   * @param skus SKU numbers for the bumpers making up this order ID.
   * @param newOrderId the ID of this new order, unique number.
   */
  public Order(ArrayList<String> skus, int newOrderId) {
    orderId = newOrderId;
    contents = skus;
  }

  /**
   * Return the status of this <code>Order</code>. Possible states are: created / purgatory / picked
   * / sequenced / loaded
   * 
   * @return A <code>String</code> representation of the status.
   */
  protected String getStatus() {
    return status;
  }

  /**
   * Updated the status of this <code>Order</code>. Possible newStatus are: purgatory / picked /
   * sequenced / loaded. If the newStatus is not from the list of valid States raise an error.
   */
  protected void updateStatus(String newStatus) {
    List<String> validStates = Arrays.asList("purgatory", "picked", "sequenced", "loaded");
    if (validStates.contains(newStatus)) {
      status = newStatus;
    } else {
      System.err
          .print("Could not update status to: " + newStatus + ". Please enter a valid status.\n");
    }
  }

  /**
   * Return the contents of this <code>Order</code>.
   * 
   * @return <code>ArrayList</code> containing the SKU numbers in this order.
   */
  protected ArrayList<String> getContents() {
    return contents;
  }

  /**
   * Return the unique order ID for this <code>Order</code>.
   * 
   * @return Integer of the order ID.
   */
  protected int getOrderId() {
    return orderId;
  }

  @Override
  public String toString() {
    return "Order # " + orderId + " Status: " + status + " Contains: " + contents.get(0).toString()
        + " and " + contents.get(1).toString();
  }
}
