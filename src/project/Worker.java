package project;

/** <code>Worker</code> in the warehouse. */
public abstract class Worker {

  /**
   * Whether <code>Worker</code> can take on a new task. <code>true</code> if this
   * <code>Worker</code> can receive new work, <code>false</code> if <code>Worker</code> is busy.
   */
  private boolean ready;

  /** This <code>Worker</code> name, used to identify this <code>Worker</code>. */
  protected String name;

  /** Active <code>PickRequest</code> for this <code>Worker</code>. */
  protected PickRequest pickReq;

  /** This warehouse's <code>Controller</code>. */
  protected Controller controller;

  /** The worker's progress through their job. */
  protected int progress = 0;

  /**
   * Constructs <code>Worker</code> with a name and a <code>Controller</code> which allows this
   * <code>Worker</code> access to various parts of this warehouse.
   * 
   * @param name This <code>Worker</code> name.
   * @param controller The warehouse <code>Controller</code>.
   */
  public Worker(String name, Controller controller) {
    this.name = name;
    this.controller = controller;
    ready = true;
  }

  /**
   * Return <code>true</code> if this <code>Worker</code> is ready to accept new work.
   * 
   * @return <code>true</code> if ready, <code>false</code> if not ready.
   */
  protected boolean isReady() {
    return ready;
  }

  /** Change this <code>Worker</code> status to <code>false</code>. */
  protected void setBusy() {
    ready = false;
  }

  /** Change this <code>Worker</code> status to <code>true</code>. */
  protected void setReady() {
    ready = true;
  }

  /**
   * Return this <code>Worker</code> name.
   * 
   * @return The <code>Worker</code> name.
   */
  protected String getName() {
    return name;
  }

  /**
   * Receive a <code>PickRequest</code>, <code>Order</code>, or <code>Pallet</code>.
   */
  abstract void receive();

  /**
   * Perform a task given a String representing a SKU or type of <code>Pallet</code>.
   * 
   * @param toWorkOn the task to be performed as a String.
   */
  abstract void work(String toWorkOn);

  /**
   * Move completed tasks on to the next part in the system.
   */
  protected void push() {
    RunWarehouse.logger.warning(name + " has nothing to push.");
  }

  /** Worker has nothing to rescan. Requires sequenced <code>Pallet</code>. */
  protected void rescan() {
    RunWarehouse.logger.info(name + " has nothing to rescan.");
  }
}
