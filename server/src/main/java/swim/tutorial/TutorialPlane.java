package swim.tutorial;

import java.io.IOException;
import swim.api.SwimRoute;
import swim.api.agent.AgentType;
import swim.api.plane.AbstractPlane;
import swim.api.server.ServerContext;
import swim.client.ClientRuntime;
import swim.loader.ServerLoader;

public class TutorialPlane extends AbstractPlane {

  @SwimRoute("/unit/:id")
  final AgentType<UnitAgent> unitAgent = agentClass(UnitAgent.class);

  public static void main(String[] args) throws IOException, InterruptedException {

    final ServerContext server = ServerLoader.load(TutorialPlane.class.getModule()).serverContext();
    server.start();
    System.out.println("Running TutorialPlane...");
    // Runs asynchronously (relative to the main thread) until termination
    server.run();

    // Send data to the above Swim server. Could (and in practice, usually will)
    // be done in external processes instead
    final ClientRuntime client = new ClientRuntime();
    client.start();
    final DataSource ds = new DataSource(client, "warp://localhost:9001");
    ds.sendCommands();
  }
}
