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
	  // instance variables to track metrics going into stats
	  private long count_sum = 0;
	  private int count_total = 0;
	  
	  @SwimLane("stats")
	  private final ValueLane<Long> stats = this.<Long>valueLane()
	  .didSet((n, o) -> {
	    logMessage("stats: mean updated to " + n + " from " + o);
	  });
	
	
	  @SwimLane("histogram")
	  private final MapLane<Long, Value> histogram = this.<Long, Value>mapLane()
	      .didUpdate((k, n, o) -> {
	        logMessage("histogram: replaced " + k + "'s value to " + Recon.toString(n) + " from " + Recon.toString(o));
	        
	        // calculating mean to send to stats
	        count_sum += n.getItem(0).longValue();
	        // logMessage(count_sum);
	        
	        count_total ++;
	        // logMessage(count_total);
	        
	        final long avg = count_sum / count_total;
	        // logMessage(avg);
	        
	        stats.set(avg);
	        
	        dropOldData();
	      })
	      .didRemove((k,o) -> {
	        // update stats with remove logic
	    	  logMessage("histogram: removed <" + k + "," + Recon.toString(o) + ">");
	    	  count_sum = 0;
	    	  count_total = 0;
	      });
	  
	
	
	// tutorial outline
	
//  // TODO: complete the stats Value Lane
//  // @SwimLane("stats")
//	
//  // HINT: Use the valueLane() method to instantiate the lane
//  // HINT: Use the .didSet() lifecycle callback to log a message showing updates to stats
//	
//   @SwimLane("histogram")
//   private final MapLane<Long, Value> histogram = this.<Long, Value>mapLane()
//       .didUpdate((k, n, o) -> {
//         logMessage("histogram: replaced " + k + "'s value to " + Recon.toString(n) + " from " + Recon.toString(o));
//         // TODO: update stats with update logic
//         
//		 dropOldData();
//
//       })
//       .didRemove((k,o) -> {
//        // TODO: update stats with remove logic
//
//       });
		  
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
