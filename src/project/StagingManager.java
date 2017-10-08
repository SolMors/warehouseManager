package project;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * <code>StagingManager</code> tracks the areas of the warehouse where <code>PickRequest</code> are
 * passing between workers types. Includes <code>marshalQueue</code>, where <code>Pallet</code> are
 * sent after picking and before sequencing, and the <code>loadingZone</code> for after sequencing
 * but before loading.
 */
public class StagingManager {

  /**
   * Represents the marshaling area. When <code>Picker</code> drops off an <code>unsortedPal</code>,
   * <code>Pallet</code> and the corresponding <code>PickRequest</code> are added to the
   * <code>marshalQueue</code>.
   */
  protected LinkedList<PickRequest> marshalQueue = new LinkedList<>();

  /**
   * Represents the loading area. When <code>Sequencer</code> drops off a sorted pick order,
   * <code>Pallet</code> and corresponding <code>PickRequest</code> are added to the
   * <code>loadQueue</code>.
   */
  private ArrayList<PickRequest> loadZone = new ArrayList<>();

  /**
   * Constructs a <code>StagingManager</code> that belongs to <code>Controller</code>.
   * 
   */
  public StagingManager() {}


  /**
   * Add <code>PickRequest</code> to the marshaling queue. Called when <code>Picker</code> is
   * finished picking the active order.
   * 
   * @param newPickReq The <code>PickRequest</code> used by <code>Picker</code> for this
   *        <code>Pallet</code>.
   */
  protected void marshalAdd(PickRequest newPickReq) {
    marshalQueue.add(newPickReq);
  }

  /**
   * Remove and return the first item in the <code>marshalQueue</code>.
   * 
   * @return The first <code>PickRequest</code> in the <code>marshalQueue</code>.
   */
  protected PickRequest marshalRemove() { // add an exception check
    if (marshalQueue.size() > 0) {
      return marshalQueue.removeFirst();
    } else {
      return null;
    }
  }

  /**
   * Add <code>PickRequest</code> to <code>loadingZone</code>. Called when the
   * <code>Sequencer</code> is finished checking and ordering the pick request's SKUs.
   * 
   * @param newPickReq The <code>PickRequest</code> used by the <code>Sequencer</code> for this
   *        <code>Pallet</code>.
   */
  protected void loadAdd(PickRequest pickReq) {
    loadZone.add(pickReq);
  }

  /**
   * Remove and return the next <code>PickRequest</code> to be loaded onto <code>Truck</code>.
   * 
   * @return <code>PickRequest</code> to be loaded onto <code>Truck</code>.
   */

  protected PickRequest loadRemove(int nextPickId) {
    for (int i = 0; i < loadZone.size(); i++) {
      if (loadZone.get(i).getPickRequestId() == nextPickId) {
        PickRequest nextLoad = loadZone.get(i);
        loadZone.remove(nextLoad);
        return nextLoad;
      }
    }
    return null;
  }

  /**
   * Return the number of <code>PickRequest</code> in <code>loadingZone</code>.
   * 
   * @return <code>int</code> representing the size of <code>loadingZone</code>.
   */
  protected int getLoadZoneSize() {
    return loadZone.size();
  }
}
