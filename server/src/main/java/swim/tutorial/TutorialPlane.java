package swim.tutorial;

import swim.api.SwimRoute;
import swim.api.agent.AgentRoute;
import swim.api.plane.AbstractPlane;
import swim.api.space.Space;
import swim.client.ClientRuntime;
import swim.kernel.Kernel;
import swim.server.ServerLoader;

public class TutorialPlane extends AbstractPlane {

  @SwimRoute("/unit/:id")
  AgentRoute<UnitAgent> unitAgent;

  public static void main(String[] args) throws InterruptedException {
    final Kernel kernel = ServerLoader.loadServer();

    kernel.start();
    System.out.println("Running Tutorial plane...");
    kernel.run();

    // Send data to the above Swim server. Could (and in practice, usually will)
    // be done in external processes instead
    final ClientRuntime client = new ClientRuntime();
    client.start();
    final DataSource ds = new DataSource(client, "warp://localhost:9001");
    ds.sendCommands();
  }

}
