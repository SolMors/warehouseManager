package project;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;

import org.junit.Before;
import org.junit.Test;

public class WarehouseTest {

  Path filePath = Paths.get(System.getProperty("user.dir"));

  private Controller controller;
  private Truck truck;
  private Pallet frontPallet;
  // private Pallet rearPallet;
  private TruckManager truckManager;
  private Order order1;
  private Order order2;
  private Order order3;
  private Order order4;
  private Order order5;
  private PickRequest pickReq;
  private PickRequest pickReq2;

  /**
   * Warehouse objects set up in the before to be used by multiple tests.
   * 
   * @throws FileNotFoundException Throwable exception.
   * @throws IOException Throwable exception.
   */
  @Before
  public void initialObjects() throws FileNotFoundException, IOException {
    controller = new Controller(filePath);
    truck = new Truck(0);
    frontPallet = new Pallet(4);
    // rearPallet = new Pallet(4);
    truckManager = new TruckManager();

    ArrayList<String> sku1 = new ArrayList<String>(Arrays.asList("1", "2"));
    order1 = new Order(sku1, 1);
    ArrayList<String> sku2 = new ArrayList<String>(Arrays.asList("3", "4"));
    order2 = new Order(sku2, 2);
    ArrayList<String> sku3 = new ArrayList<String>(Arrays.asList("5", "6"));
    order3 = new Order(sku3, 3);
    ArrayList<String> sku4 = new ArrayList<String>(Arrays.asList("7", "8"));
    order4 = new Order(sku4, 4);
    LinkedList<Order> orderLinked = new LinkedList<>();
    orderLinked.add(order1);
    orderLinked.add(order2);
    orderLinked.add(order3);
    orderLinked.add(order4);
    pickReq = new PickRequest(orderLinked, 0);

    ArrayList<String> sku5 = new ArrayList<String>(Arrays.asList("9", "10"));
    Order order6 = new Order(sku5, 6);
    ArrayList<String> sku6 = new ArrayList<String>(Arrays.asList("11", "12"));
    Order order7 = new Order(sku6, 7);
    ArrayList<String> sku7 = new ArrayList<String>(Arrays.asList("13", "14"));
    Order order8 = new Order(sku7, 8);
    ArrayList<String> sku8 = new ArrayList<String>(Arrays.asList("15", "16"));
    Order order9 = new Order(sku8, 9);
    LinkedList<Order> orderLinked2 = new LinkedList<>();
    orderLinked2.add(order6);
    orderLinked2.add(order7);
    orderLinked2.add(order8);
    orderLinked2.add(order9);
    pickReq2 = new PickRequest(orderLinked2, 2);
    order5 = new Order(sku1, 5);
  }

  // ---------- Controller ----------
  @Test
  public void testControllerGetters() {
    assertTrue(controller.getEmployees() instanceof EmployeeManager);
    assertTrue(controller.getOrders() instanceof OrderManager);
    assertTrue(controller.getStaging() instanceof StagingManager);
    assertTrue(controller.getTrucks() instanceof TruckManager);
    assertTrue(controller.getWarehouse() instanceof WarehouseManager);
  }

  // ---------- Warehouse ----------
  @Test
  public void testWarehouseManager()
      throws FileNotFoundException, IOException, WarehouseExceptions {
    PickFace pickface = controller.getWarehouse().getWarehouseFloor().get("A001");
    controller.getWarehouse().replenish(pickface); // Replenish up to 30
    for (int num = 1; num <= 31; num++) { // Pick to 0, don't pick below 0
      controller.getWarehouse().pick("A001");
    }
    for (int num = 1; num < 30; num++) {
      controller.getWarehouse().putBack("A001");
    }
    int stockQty = controller.getWarehouse().getStock("A001");
    for (int num = stockQty; num > 2; num--) {
      controller.getWarehouse().pick("A001");
    }

    controller.getWarehouse().replenish(pickface);
    assertEquals(controller.getWarehouse().getStock("A001"), 27);
    assertEquals(controller.getWarehouse().getSku("A000"), "1");
    assertEquals(controller.getWarehouse().getSkuLocation("100"), null);
    assertEquals(controller.getWarehouse().getSkuLocation("1"), "A000");
    assertTrue(controller.getWarehouse().getWarehouseFloor() instanceof HashMap<?, ?>);

    // Replenisher receive replenish request
    Replenisher ruby = new Replenisher("Ruby", controller);
    ruby.work("A001");
    ruby.push();
    ruby.receive();
    ruby.work("A003");
    ruby.work("A001");
  }

  // ---------- PickFace ----------
  @Test
  public void testPickFace() {
    PickFace bin = new PickFace("sku", 10);
    assertTrue(bin.getSku().equals("sku"));
    assertEquals(bin.getStockQty(), 10);
    bin.removeFromStock(5);
    assertEquals(bin.getStockQty(), 5);
    bin.addToStock(3);
    assertEquals(bin.getStockQty(), 8);
    bin.setStockQty(100);
    assertEquals(bin.getStockQty(), 100);
  }

  // ---------- EmployeeManager ----------
  @Test
  public void testAddEmployee() throws FileNotFoundException, IOException {
    assertEquals("There is an employee when there shouldn't be one",
        controller.getEmployees().getEmployee("Alice"), null);
    controller.getEmployees().addEmployee("Alice", "Picker", controller);
    controller.getEmployees().addEmployee("Sue", "Sequencer", controller);
    controller.getEmployees().addEmployee("Bill", "Loader", controller);
    controller.getEmployees().addEmployee("Ruby", "Replenisher", controller);
    controller.getEmployees().addEmployee("Steve", "Driver", controller);
    controller.getEmployees().getEmployee("Dave");
    assertEquals("Employee improperly added to warehouse",
        controller.getEmployees().getEmployeeCount(), 4);
    assertEquals("Employee missing", (controller.getEmployees().getEmployee("Alice")).getName(),
        "Alice");
  }

  // ---------- Trucks ----------
  @Test
  public void testNewTruck() {
    truck.load(frontPallet);
    assertEquals("Truck did not load properly", truck.getLoadPos(), 1);
    assertEquals("Truck ID error", truck.getTruckId(), 0);
  }

  // ---------- Truck Manager ----------
  @Test
  public void testTruckManager() {
    truckManager.newTruck();
    assertEquals(truckManager.getActiveTruckId(), 1);
    assertEquals(truckManager.getNextPickId(), 0);
    truckManager.load(pickReq);
    assertEquals(truckManager.getNextPickId(), 1);
  }

  // ---------- Pallet ----------
  @Test
  public void testNewPallet() {
    frontPallet.add("A1");
    assertEquals(frontPallet.getFillProg(), 1);
    frontPallet.add("A2");
    frontPallet.add("A3");
    frontPallet.add("A4");
    frontPallet.remove("A5");
    assertEquals(frontPallet.contains("A5"), false);
  }

  // ---------- OrderManager ----------
  @Test
  public void testNewOrder() throws FileNotFoundException, IOException {
    OrderManager manager = new OrderManager(filePath);
    manager.newOrder("White", "S");
    manager.newOrder("White", "SE");
    manager.newOrder("White", "SES");
    manager.newOrder("White", "SEL");
    ArrayList<Order> archive = manager.getOrderArchive();
    assertEquals("archive is empty", archive.isEmpty(), false);
    assertEquals("archive is not of size 4", archive.size(), 4);
    ArrayList<String> firstOrder = archive.get(0).getContents();
    ArrayList<String> lastOrder = archive.get(3).getContents();
    assertTrue("firstOrder != first order entered",
        firstOrder.equals(new ArrayList<>(Arrays.asList("1", "2"))));
    assertFalse(firstOrder.equals(new ArrayList<>(Arrays.asList(1, 3))));
    assertTrue("lastOrder != last order entered",
        lastOrder.equals(new ArrayList<>(Arrays.asList("7", "8"))));
  }

  // ---------- Order ----------
  @Test
  public void testUniqueOrderId() {
    order1.updateStatus("ooooo");
  }

  @Test
  public void testGetContents() {
    assertEquals(order5.getStatus(), "created");
    assertEquals("1", order5.getContents().get(0));
    assertEquals("2", order5.getContents().get(1));
  }

  @Test
  public void testGetAndUpdateStatus() {
    assertEquals(order1.getStatus(), "created");
    order1.updateStatus("loaded");
    assertEquals(order1.getStatus(), "loaded");
    ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    System.setErr(new PrintStream(errContent));
    String newstat = "bogus";
    order1.updateStatus(newstat);
    assertEquals("Could not update status to: " + newstat + ". Please enter a valid status.\n",
        errContent.toString());
  }

  @Test
  public void testToString() {
    int orderId = order1.getOrderId();
    assertEquals(order1.toString(), "Order # " + orderId + " Status: created Contains: 1 and 2");
  }

  // ---------- PickRequest ----------
  @Test
  public void testPick() throws FileNotFoundException, IOException {

    OrderManager manager = new OrderManager(filePath);
    manager.newOrder("White", "SEL");
    manager.newOrder("White", "SE");
    manager.newOrder("White", "SES");
    manager.newOrder("White", "S");

    PickRequest pickreq = manager.getNewPickReq();
    assertEquals(pickreq.getPickRequestId(), 0);
    WarehouseManager warehouse = new WarehouseManager(filePath);
    pickreq.getPickOrder(warehouse);
    pickreq.getPickOrder(warehouse); // Test Pick Order is not null
  }

  // ---------- Picker ----------
  @Test
  public void testPicker() {
    Picker pick = new Picker("Alice", controller);
    pick.receive(); // null pick request
    OrderManager omanager = controller.getOrders();
    omanager.newOrder("White", "SEL");
    omanager.newOrder("White", "SE");
    omanager.newOrder("White", "SES");
    omanager.newOrder("White", "S");
    // busy, can not receive new pick request
    pick.setBusy();
    pick.receive();
    assertEquals(pick.pickReq, null);
    // Receive new pick request
    pick.setReady();
    pick.receive();
    assertEquals(pick.pickReq.getPickRequestId(), 0);
    // Updated status and test rescan not overridden
    pick.pickReq.updateStatus("ooooo"); // Try status change
    pick.rescan();
    // Pick + push
    pick.work("1");
    pick.work("2");
    pick.work("7");
    pick.work("3");
    pick.work("4");
    pick.work("5");
    pick.work("6");
    pick.work("7");
    pick.work("8");
    pick.work("9");
    pick.push();
  }

  // ---------- Sequencer ----------
  @Test
  public void testSequence() {
    Pallet fromPicker = new Pallet(8);
    fromPicker.add("1");
    fromPicker.add("2");
    fromPicker.add("3");
    fromPicker.add("4");
    fromPicker.add("5");
    fromPicker.add("6");
    fromPicker.add("7");
    fromPicker.add("8");
    pickReq.unsortedPal = fromPicker;
    StagingManager staging = controller.getStaging();
    staging.marshalAdd(pickReq);
    Sequencer sally = new Sequencer("Sally", controller);
    sally.setBusy(); // Test .setBusy()
    sally.receive(); // Don't receive sequence when busy
    assertEquals(sally.isReady(), false);
    sally.setReady(); // Test .setReady()
    assertEquals(sally.isReady(), true);
    sally.receive();
    assertTrue(pickReq.equals(sally.pickReq));
    Sequencer steve = new Sequencer("Steve", controller);
    steve.receive(); // Receive null pick request
    assertEquals(steve.pickReq, null);
    sally.work("1");
    sally.work("9"); // Test sequence bumper not on pallet
    sally.rescan();
    assertEquals(sally.progress, 0);
    sally.work("3");
    sally.work("1");
    sally.work("2");
    sally.work("3");
    sally.work("4");
    sally.work("5");
    sally.work("6");
    sally.work("7");
    sally.work("8");
    assertTrue(pickReq.frontPal.getItemAtPosition(0).equals("1"));
    assertTrue(pickReq.rearPal.getItemAtPosition(0).equals("2"));
    assertTrue(pickReq.frontPal.getItemAtPosition(1).equals("3"));
    assertTrue(pickReq.rearPal.getItemAtPosition(1).equals("4"));
    assertTrue(pickReq.frontPal.getItemAtPosition(2).equals("5"));
    assertTrue(pickReq.rearPal.getItemAtPosition(2).equals("6"));
    assertTrue(pickReq.frontPal.getItemAtPosition(3).equals("7"));
    assertTrue(pickReq.rearPal.getItemAtPosition(3).equals("8"));
    sally.push();
  }

  @Test
  public void testBadPickSeq() { // Picker picked wrong bumper, caught by sequencer.
    pickReq.unsortedPal.add("2");
    Sequencer sally = new Sequencer("Sally", controller);
    sally.pickReq = pickReq;
    sally.work("1");
  }

  // ---------- Loader ----------
  @Test
  public void testGetPalletsToLoad() {
    Loader kyle = new Loader("Kyle", controller);
    // No active pick request for Kyle
    kyle.work("front");
    kyle.push();
    kyle.receive();
    // Populate pallets
    controller.getStaging().loadAdd(pickReq);
    pickReq.frontPal.add("1");
    pickReq.frontPal.add("3");
    pickReq.frontPal.add("5");
    pickReq.frontPal.add("7");
    pickReq.rearPal.add("2");
    pickReq.rearPal.add("4");
    pickReq.rearPal.add("6");
    pickReq.rearPal.add("8");
    //Receive and work
    kyle.receive();
    assertEquals(kyle.isReady(), false);
    kyle.work("front");
    kyle.work("1");
    kyle.work("2");
    kyle.work("3");
    kyle.work("4");
    kyle.work("5");
    kyle.work("6");
    kyle.work("7");
    kyle.work("8");
    kyle.push();
    assertEquals(controller.getTrucks().getNextPickId(), 1);
    assertEquals(pickReq.getStatus(), "loaded");
    assertEquals(kyle.isReady(), true);
  }
  
  @Test
  public void testFrontLoadSeq() {
    Loader kyle = new Loader("Kyle", controller);
    kyle.pickReq = pickReq;
    Pallet fp1 = new Pallet(4); // Creat unordered front pallet
    fp1.add("2");
    kyle.pickReq.frontPal = fp1;
    kyle.work("1");
  }

  @Test
  public void testRearLoadSeq() {
    Loader kyle = new Loader("Kyle", controller);
    kyle.pickReq = pickReq;
    Pallet fp1 = new Pallet(4); // Creat unordered front pallet
    fp1.add("1");
    kyle.pickReq.frontPal = fp1;
    kyle.work("1");
    Pallet rp1 = new Pallet(4); // Creat unordered front pallet
    rp1.add("3");
    kyle.pickReq.rearPal = rp1;
    kyle.work("2");
  }

  @Test
  public void testSeqRescan() {
    Loader kyle = new Loader("Kyle", controller);
    kyle.pickReq = pickReq;
    Pallet fp1 = new Pallet(4); // Creat unordered front pallet
    fp1.add("1");
    kyle.pickReq.frontPal = fp1;
    kyle.work("1");
    assertEquals(kyle.progress, 1);
    kyle.rescan();
    assertEquals(kyle.progress, 0);
    kyle.work("1");

  }

  @Test
  public void testLoaderReceive() {
    StagingManager smanager = controller.getStaging();

    // Nothing in load zone to receive
    Loader rick = new Loader("Rick", controller);
    rick.receive();
    assertEquals(rick.isReady(), true);
    // Test correct pick request isn't in load zone
    smanager.loadAdd(pickReq2);
    rick.receive();
    assertEquals(rick.isReady(), true);

    // Test gets the correct next pick request for the truck
    smanager.loadAdd(pickReq);
    rick.receive();
    assertEquals(rick.isReady(), false);
  }

  @Test
  public void testBadSequence() {
    StagingManager smanager = controller.getStaging();
    smanager.loadAdd(pickReq);
    Loader rick = new Loader("Rick", controller);
    rick.receive();
    pickReq.frontPal.add("10");
    rick.work("front");
    pickReq.rearPal.add("9");
    rick.work("rear");

  }

  // ---------- Sequencer ----------
  @Test
  public void testSequencer() {
    Sequencer seq = new Sequencer("seq", controller);
    seq.setBusy();
    seq.receive();
    assertEquals(seq.isReady(), false);
    seq.setReady();
    assertEquals(seq.isReady(), true);
    seq.receive();
  }

  // ---------- Replenisher ----------
  @Test
  public void testReplenish() throws FileNotFoundException, IOException {
    EmployeeManager emanager = controller.getEmployees();
    emanager.addEmployee("Alice", "Picker", controller);
    String loc = "A000";
    assertEquals(controller.getWarehouse().getStock(loc), 26);
    emanager.addEmployee("Ruby", "Replenisher", controller);
  }

  // ---------- WarehouseExceptio ----------
  @Test
  public void testWarehouseException() {
    WarehouseExceptions testexcep = new WarehouseExceptions("Test message");
    testexcep.printStackTrace();
  }

  // ---------- ReadAndWrite ----------
  @Test
  public void testReadAndWrite() throws FileNotFoundException, IOException {
    String[] lineOne = new String[] {"A", "0", "B", "1"};
    String[] lineTwo = new String[] {"Z", "9", "X", "8"};
    ArrayList<String[]> toWrite = new ArrayList<>(Arrays.asList(lineOne, lineTwo));
    ReadAndWrite.writeFile(filePath, "TestReadAndWrite.csv", toWrite);
    ArrayList<String[]> readFile =
        ReadAndWrite.readFile(filePath.resolve("TestReadAndWrite.csv"), ",", false);
    String[] readLineOne = readFile.get(0);
    assertEquals(lineOne[0], readLineOne[0]);
    assertEquals(lineOne[1], readLineOne[1]);
    assertEquals(lineOne[2], readLineOne[2]);
    assertEquals(lineOne[3], readLineOne[3]);
    String[] readLineTwo = readFile.get(1);
    assertEquals(lineTwo[0], readLineTwo[0]);
    assertEquals(lineTwo[1], readLineTwo[1]);
    assertEquals(lineTwo[2], readLineTwo[2]);
    assertEquals(lineTwo[3], readLineTwo[3]);
  }
  
  // ---------- ReadAndWrite ----------
  @Test
  public void testRunWarehouseHelper() throws IOException {
    RunWarehouseHelper helper = new RunWarehouseHelper();
    helper.run(filePath, "sim_no_error.txt");
  }
}
