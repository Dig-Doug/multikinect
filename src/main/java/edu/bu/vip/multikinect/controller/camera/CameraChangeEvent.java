package edu.bu.vip.multikinect.controller.camera;

import edu.bu.vip.multikinect.master.camera.Grpc.CameraProps;

public class CameraChangeEvent {
  public enum Type {
    Added,
    Removed
  }
  
  private CameraProps props;
  private Type type;
  
  public CameraChangeEvent(CameraProps props, Type type) {
    this.props = props;
    this.type = type;
  }

  public CameraProps getProps() {
    return props;
  }
  public Type getType() {
    return type;
  }
}
