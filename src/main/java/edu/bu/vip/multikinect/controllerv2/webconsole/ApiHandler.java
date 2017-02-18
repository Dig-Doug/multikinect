package edu.bu.vip.multikinect.controllerv2.webconsole;

import static ratpack.jackson.Jackson.json;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import edu.bu.vip.kinect.controller.calibration.Protos.Calibration;
import edu.bu.vip.multikinect.controllerv2.Controller;
import edu.bu.vip.multikinect.controllerv2.webconsole.api.CalibrationRep;
import edu.bu.vip.multikinect.controllerv2.webconsole.api.RecordingRep;
import edu.bu.vip.multikinect.controllerv2.webconsole.api.SessionRep;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ratpack.func.Action;
import ratpack.handling.Chain;
import ratpack.http.Status;

@Singleton
public class ApiHandler implements Action<Chain> {
  // TODO(doug) - Consistent CRUD naming style
  private static final String BASE_URL = "_";
  private static final String NEW_CALIBRATION_URL = BASE_URL + "/newCalibration";
  private static final String SELECT_CALIBRATION_URL = BASE_URL + "/selectCalibration/:id";
  private static final String DELETE_CALIBRATION_URL = BASE_URL + "/deleteCalibration/:id";
  private static final String NEW_FRAME_URL = BASE_URL + "/newFrame";
  private static final String DELETE_FRAME_URL = BASE_URL + "/deleteFrame/:id";
  private static final String FINISH_CALIBRATION_URL = BASE_URL + "/finishCalibration";
  private static final String FINISH_FRAME_URL = BASE_URL + "/finishFrame";
  private static final String CREATE_SESSION_URL = BASE_URL + "/createSession";
  private static final String SELECT_SESSION_URL = BASE_URL + "/selectSession/:id";
  private static final String DELETE_SESSION_URL = BASE_URL + "/deleteSession/:id";
  private static final String CANCEL_SELECT_SESSION_URL = BASE_URL + "/cancelSelectSession";
  private static final String NEW_RECORDING_URL = BASE_URL + "/newRecording";
  private static final String DELETE_RECORDING_URL = BASE_URL + "/deleteRecording/:id";
  private static final String FINISH_SESSION_URL = BASE_URL + "/finishSession";
  private static final String STOP_RECORDING_URL = BASE_URL + "/stopRecording";

  private final Logger logger = LoggerFactory.getLogger(getClass());
  private Controller controller;

  @Inject
  protected ApiHandler(Controller controller) {
    this.controller = controller;
  }

  @Override
  public void execute(Chain chain) throws Exception {
    chain.post(NEW_CALIBRATION_URL, (context) -> {
      // TODO(doug) - implement
      context.parse(CalibrationRep.class).then(calibration -> {
        controller.newCalibration(calibration.getName());
        context.getResponse().status(Status.OK).send();
      });
    });

    chain.get(SELECT_CALIBRATION_URL, (context) -> {
      // TODO(doug) - implement
      long id = Long.parseLong(context.getPathTokens().get("id"));
      controller.selectCalibration(id);
      context.getResponse().status(Status.OK).send();
    });

    chain.get(DELETE_CALIBRATION_URL, (context) -> {
      // TODO(doug) - implement
      long id = Long.parseLong(context.getPathTokens().get("id"));
      controller.deleteCalibration(id);
      context.getResponse().status(Status.OK).send();
    });

    chain.post(NEW_FRAME_URL, context -> {
      // TODO(doug) - implement

      controller.newCalibrationFrame();
      context.getResponse().status(Status.OK).send();
    });

    chain.get(DELETE_FRAME_URL, context -> {
      // TODO(doug) - implement
      long id = Long.parseLong(context.getPathTokens().get("id"));
      controller.deleteCalibrationRecording(id);

      context.getResponse().status(Status.OK).send();
    });

    chain.post(FINISH_CALIBRATION_URL, context -> {
      // TODO(doug) - implement

      controller.finishNewCalibration();
      context.getResponse().status(Status.OK).send();
    });

    chain.post(FINISH_FRAME_URL, context -> {
      // TODO(doug) - implement
      controller.finishNewCalibrationFrame();
      context.getResponse().status(Status.OK).send();
    });

    chain.post(CREATE_SESSION_URL, context -> {
      // TODO(doug) - implement
      context.parse(SessionRep.class).then(session -> {
        controller.createSession(session.getName());
        context.getResponse().status(Status.OK).send();
      });
    });

    chain.get(SELECT_SESSION_URL, context -> {
      // TODO(doug) - implement
      long id = Long.parseLong(context.getPathTokens().get("id"));
      controller.selectSession(id);
      context.getResponse().status(Status.OK).send();
    });

    chain.get(DELETE_SESSION_URL, context -> {
      // TODO(doug) - implement
      long id = Long.parseLong(context.getPathTokens().get("id"));
      controller.deleteSession(id);
      context.getResponse().status(Status.OK).send();
    });

    chain.post(CANCEL_SELECT_SESSION_URL, context -> {
      // TODO(doug) - implement
      controller.finishSelectSession();
    });

    chain.post(NEW_RECORDING_URL, context -> {
      // TODO(doug) - implement
      context.parse(RecordingRep.class).then(recordingRep -> {
        controller.newRecording(recordingRep.getName());
        context.getResponse().status(Status.OK).send();
      });
    });

    chain.get(DELETE_RECORDING_URL, context -> {
      // TODO(doug) - implement
      long id = Long.parseLong(context.getPathTokens().get("id"));
      controller.deleteRecording(id);
      context.getResponse().status(Status.OK).send();
    });

    chain.post(FINISH_SESSION_URL, context -> {
      // TODO(doug) - implement
      controller.finishSession();
      context.getResponse().status(Status.OK).send();
    });

    chain.post(STOP_RECORDING_URL, context -> {
      // TODO(doug) - implement
      controller.stopRecording();
      context.getResponse().status(Status.OK).send();
    });

    chain.post("_/proto", context -> {
     context.parse(Calibration.class).then(cal -> {
        logger.info(cal.toString());
        context.render(cal);
     });
    });
  }
}
