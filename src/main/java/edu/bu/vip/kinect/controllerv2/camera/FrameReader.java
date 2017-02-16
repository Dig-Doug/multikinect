package edu.bu.vip.kinect.controllerv2.camera;

import com.google.common.collect.ImmutableList;
import edu.bu.vip.multikinect.Protos.Frame;
import java.io.IOException;
import java.io.InputStream;

public class FrameReader {
  private Frame next;
  private InputStream input;

  public FrameReader(InputStream in) throws IOException {
    this.input = in;
    readNext();
  }

  public void close() throws IOException {
    if (this.input != null) {
      this.input.close();
    }
  }

  public boolean hasNext() {
    return (this.next != null);
  }

  public Frame next() {
    Frame last = this.next;
    try {
      readNext();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return last;
  }

  private void readNext() throws IOException {
    if (this.input != null) {
      this.next = Frame.parseDelimitedFrom(input);
      if (this.next == null) {
        this.input.close();
        this.input = null;
      }
    }
  }

  public static ImmutableList<Frame> readAllFrames(InputStream in) throws IOException {
    ImmutableList.Builder<Frame> builder = ImmutableList.builder();
    FrameReader reader = new FrameReader(in);
    while (reader.hasNext()) {
      builder.add(reader.next());
    }
    reader.close();

    return builder.build();
  }
}
