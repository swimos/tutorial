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
	
	  // *********************** EXAMPLE SOLUTIONS FOR STATS LANE ***********************
	  
	  // instance variables to track metrics going into stats
	  private long countSum = 0;
	  private int countTotal = 0;
	  private int index = 0;
	  private long[] recentData = new long[5];
	  
	  
	  // intermediary lanes that represent individual metrics
	  @SwimLane("avg")
	  private final ValueLane<Long> avg = this.<Long>valueLane()
	  .didSet((n, o) -> {
	    logMessage("avg: mean updated to " + n + " from " + o);
	  });
	  
	  @SwimLane("localAvg")
	  private final ValueLane<Long> localAvg = this.<Long>valueLane()
	  .didSet((n, o) -> {
	    logMessage("localAvg: local average (last 5 entries) updated to " + n + " from " + o);
	  });
	  
	  @SwimLane("localVar")
	  private final ValueLane<Long> localVar = this.<Long>valueLane()
	  .didSet((n, o) -> {
	    logMessage("localVar: local variance (last 5 entries) updated to " + n + " from " + o);
	  });
	  
	  @SwimLane("localStdDev")
	  private final ValueLane<Long> localStdDev = this.<Long>valueLane()
	  .didSet((n, o) -> {
	    logMessage("localStdDev: local std deviation (last 5 entries) updated to " + n + " from " + o);
	  });
	  
	  
	  // combination all calculations into one swim lane of type Value
	  @SwimLane("stats")
	  private final ValueLane<Value> stats = this.<Value>valueLane()
	  .didSet((n, o) -> {
		  logMessage("stats: set to " + Recon.toString(n) + " from " + Recon.toString(o));
	  });
	
	  // *********************** EXAMPLE SOLUTION FOR HISTOGRAM ***********************
	  @SwimLane("histogram")
	  private final MapLane<Long, Value> histogram = this.<Long, Value>mapLane()
	      .didUpdate((k, n, o) -> {
	        logMessage("histogram: replaced " + k + "'s value to " + Recon.toString(n) + " from " + Recon.toString(o));
	        
	        // calculating overall mean to send to average lane
	        countSum += n.getItem(0).longValue();
	        countTotal ++;
	        final long setAvg = countSum / countTotal;
	        avg.set(setAvg);
	        
	        // appending new data to the recentData array
	        if (index >= recentData.length-1) {
	        	index = 0;
	        }
	        recentData[index] = n.getItem(0).longValue();
	        index ++;
	        
	        // calculating local mean to send to local average lane
	        long localSum = 0;
	        for (long d : recentData) localSum += d;
	        final long setLocalAvg = localSum / (long) recentData.length;
	        localAvg.set(setLocalAvg);
	        
	        // calculating local variance to send to local var lane
	        long squaredDifSum = 0; // (sum of local mean - each value)^2
	        for (long d : recentData) squaredDifSum += (d - setLocalAvg)*(d - setLocalAvg);
	        final long setLocalVar = squaredDifSum/recentData.length;
	        localVar.set(setLocalVar);
	        
	        // calculating local standard deviation to send to local standard deviation lane
	        final long setLocalStdDev = (long)Math.sqrt(setLocalVar);
	        localStdDev.set(setLocalStdDev);
	        
	        // Consolidating all data to the valuelane stats of type value
	        Value all_stats = Record.create(4).slot("avg", setAvg).slot("localAvg", setLocalAvg).slot("localVar", setLocalVar).slot("localStdDev", setLocalStdDev);
	        stats.set(all_stats); 
	        
	        dropOldData();
	      })
	      .didRemove((k,o) -> {
	    	  // remove logic typically follows this format:
	    	  // stats.put(stats.get()-o)
	    	  
	    	  logMessage("histogram: removed <" + k + "," + Recon.toString(o) + ">");
	    	  
	    	  // remove logic for avg lane
	    	  countSum -= o.getItem(0).longValue();
	    	  countTotal --;
	    	  final long setUpdatedAvg = countSum / countTotal;
	    	  avg.set(setUpdatedAvg);
	    	  
	    	  // stats based only on the most recent inputs (i.e. localAvg, et al) will constantly update already
	    	  final long setLocalAvg = localAvg.get();
	    	  final long setLocalVar = localVar.get();
	    	  final long setLocalStdDev = localStdDev.get();
	    	  	  
	    	  // remove logic for stats
	    	  Value updated_stats = Record.create(4).slot("avg", setUpdatedAvg).slot("localAvg", setLocalAvg).slot("localVar", setLocalVar).slot("localStdDev", setLocalStdDev);
		      stats.set(updated_stats); 
	    	  
	      });
	  
	  	// ****************************************************************************
		  
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
