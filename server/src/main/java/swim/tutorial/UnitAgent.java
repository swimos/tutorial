package swim.tutorial;

import swim.api.SwimLane;
import swim.api.agent.AbstractAgent;
import swim.api.lane.CommandLane;
import swim.api.lane.ListLane;
import swim.api.lane.MapLane;
import swim.api.lane.ValueLane;
import swim.recon.Recon;
import swim.structure.Record;
import swim.structure.Value;
import java.util.Iterator;

public class UnitAgent extends AbstractAgent {

  @SwimLane("histogram")
  private final MapLane<Long, Value> histogram = this.<Long, Value>mapLane()
      .didUpdate((k, n, o) -> {
        logMessage("histogram: replaced " + k + "'s value to " + Recon.toString(n) + " from " + Recon.toString(o));
        dropOldData();
      });
  @SwimLane("history")
  private final ListLane<Value> history = this.<Value>listLane()
      .didUpdate((idx, newValue, oldValue) -> {
        logMessage("history: appended {" + idx + ", " + Recon.toString(newValue) + "}");
        final long bucket = newValue.getItem(0).longValue() / 5000 * 5000;
        final Value entry = histogram.get(bucket);
        histogram.put(bucket, Record.create(1).slot("count", entry.get("count").intValue(0) + (int) (Math.random() * 20)));
        final int willDrop = Math.max(0, this.history.size() - 200);
        this.history.drop(willDrop);
      });
  @SwimLane("latest")
  private final ValueLane<Value> latest = this.<Value>valueLane()
      .didSet((newValue, oldValue) -> {
        logMessage("latest: set to " + Recon.toString(newValue) + " from " + Recon.toString(oldValue));
        this.history.add(
            Record.create(2)
                .item(System.currentTimeMillis())
                .item(newValue)
        );
      });
  @SwimLane("publish")
  public final CommandLane<Value> publish = this.<Value>commandLane()
      .onCommand(v -> {
        logMessage("publish: commanded with " + Recon.toString(v));
        latest.set(v);
      });

  private void dropOldData() {
    final long now = System.currentTimeMillis();
    final Iterator<Long> iterator = histogram.keyIterator();
    while (iterator.hasNext()) {
      long key = iterator.next();
      if ((now - key) > 2 * 60 * 1000L) {
        // remove items that are older than 2 minutes
        histogram.remove(key);
      } else {
        // map is sorted by the sort order of the keys, so break out of the loop on the first
        // key that is newer than 2 minutes
        break;
      }
    }
  }

  private void logMessage(Object o) {
    System.out.println("[" + nodeUri() + "] " + o);
  }

  @Override
  public void didStart() {
    logMessage("Hello, world!");
  }

}
