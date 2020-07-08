package swim.tutorial;

import swim.api.ref.SwimRef;
import swim.structure.Record;

/**
 * Simple wrapper around some {@code SwimRef}, e.g. a {@code SwimClient} handle,
 * that pushes data to the Swim server running at {@code hostUri}.
 */
class DataSource {

  private final SwimRef ref;
  private final String hostUri;

  DataSource(SwimRef ref, String hostUri) {
    this.ref = ref;
    this.hostUri = hostUri;
  }

  void sendCommands() throws InterruptedException {
    int indicator = 0;
    while (true) {
      int foo = (int) (Math.random() * 10 - 5) + 30;
      int bar = (int) (Math.random() * 20 - 10) + 60;
      int baz = (int) (Math.random() * 30 - 15) + 90;
      if ((indicator / 25) % 2 == 0) {
        foo *= 2;
        bar *= 2;
        baz *= 2;
      }
      // msg's Recon serialization will take the following form:
      //   "{foo:$foo,bar:$bar,baz:$baz}"
      final Record msg = Record.create(3)
          .slot("foo", foo)
          .slot("bar", bar)
          .slot("baz", baz);

      // Push msg to the
      //   *CommandLane* addressable by "publish" OF the
      //   *Web Agent* addressable by "/unit/master" RUNNING ON the
      //   *(Swim) server* addressable by hostUri
      this.ref.command(this.hostUri, "/unit/master", "publish", msg);
      
      // To instantiate more agents, follow the format of the URI pattern specified in TutorialPlane
      //   see line 12: @SwimRoute("/unit/:id")
      
      // TODO: Create two new web agents using the above format
      
   // ***** EXAMPLE SOLUTION ******
      // this.ref.command(this.hostUri, "/unit/secondAgent", "publish", msg);
      // this.ref.command(this.hostUri, "/unit/thirdAgent", "publish", msg);
      
      indicator = (indicator + 1) % 1000;

      // Throttle events to four every three seconds
      Thread.sleep(750);
    }
  }

}
