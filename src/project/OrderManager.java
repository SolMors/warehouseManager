package project;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedList;

/**
 * OrderManager keeps track of all orders and picking requests.
 */
public class OrderManager {

  /**
   * Stores up to four <code>Order</code>. Once a fourth <code>Order</code> is received pushes the
   * list to <code>PickRequest</code> and moves all orders into <code>orderArchive</code>.
   */
  private LinkedList<Order> orderPurgatory = new LinkedList<>();

  /** Holds all orders that have already been pushed to <code>PickRequest</code>. */
  private ArrayList<Order> orderArchive = new ArrayList<>();

  /**
   * Contains all active <code>PickRequests</code>. <code>Picker</code> selects the first
   * <code>PickRequest</code> in the list to work on.
   */
  private LinkedList<PickRequest> activePickRequests = new LinkedList<>();

  /**
   * Represents the marshaling area. When <code>Picker</code> drops off an <code>unsortedPal</code>,
   * the pallet item and the corresponding <code>PickRequest</code> item are added to the
   * <code>marshalQueue</code>.
   */
  protected LinkedList<PickRequest> marshalQueue = new LinkedList<>();

  /**
   * The translation hash table, used to convert orders from (colour/model) to SKU.
   */
  private Hashtable<String, ArrayList<String>> translationTable;

  /**
   * Integer that increments with each new <code>PickRequest</code> object. Used to generate the
   * <code>pickRequestId</code>.
   */
  private int pickReqId = 0;

  /**
   * Integer that increments with each new <code>Order</code> object. Used to generate the
   * <code>orderId</code>.
   */
  private int orderId = 0;

  /**
   * Constructs <code>OrderManager</code> for the warehouse. <code>OrderManger</code> stores all
   * <code>Order</code> that have been received in the system and prepares them for
   * <code>Picker</code> using a <code>PickRequest</code>.
   * 
   * @param directory <code>Path</code> location of the translation table csv file.
   * @throws IOException file is removed or otherwise ceases to exist during reading.
   * @throws FileNotFoundException if a file cannot be found prints an error message that it could
   *         not be found.
   */
  public OrderManager(Path directory) throws FileNotFoundException, IOException {
    this.translationTable = translationTable(directory.resolve("translation.csv"));
  }

  /**
   * Return the <code>orderArchive</code>.
   * 
   * @return <code>ArrayList</code> all of the <code>Order</code> that have come through the system.
   */
  protected ArrayList<Order> getOrderArchive() {
    return orderArchive;
  }

  /**
   * Take a model, and color. Create new <code>Order</code> and put that order into
   * <code>orderPurgatory</code>.
   * 
   * @param model identification of the fascia.
   * @param color of the fascia.
   */
  protected void newOrder(String color, String model) {
    ArrayList<String> skus = translate(color, model);
    Order newOrder = new Order(skus, orderId);
    orderId++;
    RunWarehouse.logger.info("Order #" + String.valueOf(newOrder.getOrderId()) + " created");
    moveToPurgatory(newOrder);
  }

  /**
   * Move new <code>Order</code> to <code>orderPurgatory</code>. When <code>orderPurgatory</code> is
   * full, push all <code>Order</code> to <code>PickRequest</code>, remove each <code>Order</code>
   * from <code>orderPurgatory</code> and move it to <code>orderArchive</code> in first in first out
   * order.
   * 
   * @param newOrder the <code>Order</code> to add to <code>orderPurgatory</code>.
   */
  private void moveToPurgatory(Order newOrder) {
    if (orderPurgatory.size() < 3) {
      orderPurgatory.add(newOrder);
    } else {
      orderPurgatory.add(newOrder);
      pushOrders(orderPurgatory);
      while (!orderPurgatory.isEmpty()) {
        orderArchive.add(orderPurgatory.remove());
      }
    }
  }

  /**
   * Create a new <code>pickingRequest</code> given <code>orderPurgatory</code>, and move that
   * <code>pickingRequest</code> into <code>activePickRequests</code>.
   * 
   * @param orderPurgatory the list of <code>Order</code> to be made into a
   *        <code>pickingRequest</code>.
   */
  private void pushOrders(LinkedList<Order> orderPurgatory) {
    PickRequest pickingRequest = new PickRequest(orderPurgatory, pickReqId);
    activePickRequests.add(pickingRequest);
    pickReqId++;
  }

  /**
   * Remove and return the first item in the <code>activePickRequests</code> linked list.
   * 
   * @return The first pick request in the active pick request list.
   */
  protected PickRequest getNewPickReq() { // add an exception check
    if (!activePickRequests.isEmpty()) {
      return activePickRequests.removeFirst();
    } else {
      return null;
    }
  }

  /**
   * If <code>PickRequest</code> is improperly picked it is returned to the
   * <code>activePickRequest</code> queue. It is added to the front so it gets picked next.
   * 
   * @param pickReq The <code>PickRequest</code> to be added back into the queue.
   */
  protected void returnPickReq(PickRequest pickReq) {
    activePickRequests.addFirst(pickReq);
  }

  /**
   * A helper function to translate color, model combinations into a SKU number.
   * 
   * @param model model of the fascia.
   * @param color of the fascia.
   */
  private ArrayList<String> translate(String color, String model) {
    return this.translationTable.get(color + model);
  }

  /**
   * Take .csv file where the first two columns are the description, and last two columns are the
   * SKUs of the items. Create a Hashtable with description as key and the SKUs the values.
   * 
   * @return a table for easy lookup of items by description.
   * @throws IOException file is removed or otherwise ceases to exist during reading.
   * @throws FileNotFoundException if a file cannot be found prints an error message that it could
   *         not be found.
   */
  private Hashtable<String, ArrayList<String>> translationTable(Path translationlocation)
      throws FileNotFoundException, IOException {
    ArrayList<String[]> fileContents = ReadAndWrite.readFile(translationlocation, ",", true);
    Hashtable<String, ArrayList<String>> translationTable = new Hashtable<>();
    for (String[] line : fileContents) {
      ArrayList<String> skus = new ArrayList<>();
      skus.add(line[2]);
      skus.add(line[3]);
      translationTable.put(line[0] + line[1], skus);
    }
    return translationTable;
  }

}
