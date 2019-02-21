open module swim.tutorial {
  requires transitive swim.loader;
  requires swim.client;

  exports swim.tutorial;

  // Allow ServerLoader to load a Swim server with a TutorialPlane
  provides swim.api.plane.Plane with swim.tutorial.TutorialPlane;
}
