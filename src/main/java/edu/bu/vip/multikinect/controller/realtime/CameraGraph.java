package edu.bu.vip.multikinect.controller.realtime;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.primitives.Doubles;
import edu.bu.vip.kinect.controller.calibration.Protos.CameraPairCalibration;
import java.util.List;
import org.ejml.data.DenseMatrix64F;
import org.ejml.simple.SimpleMatrix;
import org.jgrapht.DirectedGraph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.FloydWarshallShortestPaths;
import org.jgrapht.graph.ClassBasedEdgeFactory;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedMultigraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Graph of transforms between pairs of cameras. Each node of the grpah is a camera. Each edge
 * represents a transformation between two cameras learned during the calibration phase.
 */
public class CameraGraph {

  private final Logger logger = LoggerFactory.getLogger(getClass());
  private final DirectedGraph<String, CameraPairEdge> graph = new DirectedMultigraph<>(
      new ClassBasedEdgeFactory<String, CameraPairEdge>(CameraPairEdge.class));

  private final FloydWarshallShortestPaths<String, CameraPairEdge> paths;
  private final String centralCamera;
  private final ImmutableSet<String> vertices;
  private final LoadingCache<ImmutableList<String>, DenseMatrix64F> transformCache = CacheBuilder
      .newBuilder()
      .maximumSize(100)
      .build(new CacheLoader<ImmutableList<String>, DenseMatrix64F>() {
        @Override
        public DenseMatrix64F load(ImmutableList<String> vertices) {
          String cameraA = vertices.get(0);
          String cameraB = vertices.get(1);

          // Get the path between the cameras
          GraphPath<String, CameraPairEdge> path = paths.getPath(cameraA, cameraB);

          SimpleMatrix combined = SimpleMatrix.identity(4);
          for (CameraPairEdge edge : path.getEdgeList()) {
            SimpleMatrix edgeTransform = new SimpleMatrix(edge.getTransform());
            combined = edgeTransform.mult(combined);
          }

          return combined.getMatrix();
        }
      });

  public CameraGraph(List<CameraPairCalibration> pairs) {
    ImmutableSet.Builder<String> builder = ImmutableSet.builder();
    for (CameraPairCalibration pair : pairs) {
      builder.add(pair.getCameraA());
      builder.add(pair.getCameraB());

      graph.addVertex(pair.getCameraA());
      graph.addVertex(pair.getCameraB());

      if (!pair.getCameraA().equals(pair.getCameraB())) {
        DenseMatrix64F transform = new DenseMatrix64F(4, 4, true,
            Doubles.toArray(pair.getTransformList()));
        graph.addEdge(pair.getCameraA(), pair.getCameraB(),
            new CameraPairEdge(pair, transform, false));

        DenseMatrix64F inverse = inverseTransformationMatrix(transform);
        graph
            .addEdge(pair.getCameraB(), pair.getCameraA(), new CameraPairEdge(pair, inverse, true));
      }
    }
    vertices = builder.build();

    // Compute all shortest paths in graph
    paths = new FloydWarshallShortestPaths<>(graph);

    centralCamera = computeCentralVertex();
  }

  private String computeCentralVertex() {
    // Find the central vertex by finding the one with lowest eccentricity
    double lowestEcc = Double.POSITIVE_INFINITY;
    String lowestVertex = vertices.asList().get(0);

    for (String startVertex : vertices) {
      double ecc = Double.NEGATIVE_INFINITY;
      for (String endVertex : vertices) {
        // Eccentricity of a vertex is largest distance to any other vertex
        GraphPath<String, CameraPairEdge> path = paths.getPath(startVertex, endVertex);
        if (path.getWeight() > ecc) {
          ecc = path.getWeight();
        }
      }

      // Update min
      if (ecc < lowestEcc) {
        lowestVertex = startVertex;
        lowestEcc = ecc;
      }

    }

    return lowestVertex;
  }

  private DenseMatrix64F inverseTransformationMatrix(DenseMatrix64F orig) {
    SimpleMatrix origMat = new SimpleMatrix(orig);

    SimpleMatrix rotationMat = origMat.extractMatrix(0, 3, 0, 3);
    SimpleMatrix translation = origMat.extractMatrix(0, 3, 3, 4);

    // For rotation matrices, the inverse is the transpose
    SimpleMatrix inverseRot = rotationMat.transpose();
    SimpleMatrix newTranslation = inverseRot.scale(-1).mult(translation);

    SimpleMatrix finalMat = new SimpleMatrix(4, 4);
    finalMat = finalMat.combine(0, 0, inverseRot);
    finalMat = finalMat.combine(0, 3, newTranslation);

    return finalMat.getMatrix();
  }

  /**
   * Equivalent to: {@link #calculateTransform calculateTransform(} {@link #getCentralCamera()
   * getCentralCamera())}.
   */
  public DenseMatrix64F calculateTransform(String cameraA) {
    return calculateTransform(cameraA, getCentralCamera());
  }

  /**
   * Calculates the transformation matrix for transforming cameraA's coordinate system into
   * cameraB's coordinate system.
   *
   * @param cameraA - Starting camera id
   * @param cameraB - Ending camera id
   */
  public DenseMatrix64F calculateTransform(String cameraA, String cameraB) {
    if (!vertices.contains(cameraA)) {
      logger.error("Camera {} not in graph", cameraA);
      throw new RuntimeException(cameraA + " is not in the graph");
    }
    if (!vertices.contains(cameraB)) {
      logger.error("Camera {} not in graph", cameraB);
      throw new RuntimeException(cameraB + " is not in the graph");
    }

    ImmutableList<String> key = ImmutableList.of(cameraA, cameraB);

    // Cache loader doesn't throw exceptions
    return transformCache.getUnchecked(key);
  }

  public String getCentralCamera() {
    return centralCamera;
  }

  public ImmutableSet<String> getVertices() {
    return vertices;
  }


  private static class CameraPairEdge extends DefaultEdge {

    private static final long serialVersionUID = -2078620037807429905L;

    private final CameraPairCalibration pair;
    private final DenseMatrix64F transform;
    private final boolean inverse;


    public CameraPairEdge(CameraPairCalibration pair, DenseMatrix64F transform, boolean inverse) {
      this.pair = pair;
      this.transform = transform;
      this.inverse = inverse;
    }

    public DenseMatrix64F getTransform() {
      return transform;
    }
  }
}
