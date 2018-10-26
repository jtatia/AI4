
import javafx.animation.*;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import javafx.util.Duration;

public class PendulumSimulation extends Application {

    public static Main obj;
    static double T;

    @Override
    public void start(final Stage stage) {
        final Group group = new Group();
        final Scene scene = new Scene(group, 900, 600, Color.WHITE);
        stage.setScene(scene);
        stage.setTitle("Pendulum Animation");
        stage.show();
        //Pendulum Line
        final Line pendulumHand = new Line(0, 175, 0, 0);
        pendulumHand.setTranslateX(450);
        pendulumHand.setTranslateY(350);

        //Pendulum Ball
        final Circle circle = new Circle(0, 0, 5);
        circle.setTranslateX(450);
        circle.setTranslateY(350);
        circle.setFill(Color.BLACK);

        final Rectangle rectangle = new Rectangle(350,525,200,30);
        circle.setFill(Color.BLACK);

        final Label label = new Label("Angular Displacement :");
        label.setLayoutY(5);

        final TextField theta = new TextField();
        theta.setPromptText("Enter Theta Value");
        theta.setTranslateX(165);

        final Label label1 = new Label("Angular Velocity :");
        label1.setTranslateX(400);
        label1.setLayoutY(5);

        final TextField angularVelocity = new TextField();
        angularVelocity.setPromptText("Enter angularVelocity");
        angularVelocity.setTranslateX(520);

        final Button submitInitialConfig = new Button("Submit");
        submitInitialConfig.setTranslateX(700);

        final Button pause = new Button("Pause");
        pause.setTranslateX(520);
        pause.setTranslateY(300);

        final Button play = new Button("Play");
        play.setTranslateX(520);
        play.setTranslateY(350);

        final TextArea textArea = new TextArea();
        textArea.setTranslateX(50);
        textArea.setPromptText("FuzzyController Output will be displaced");
        textArea.setTranslateY(50);
        textArea.setPrefWidth(700);
        textArea.setPrefHeight(200);

        group.getChildren().add(circle);
        group.getChildren().add(pendulumHand);
        group.getChildren().add(rectangle);
        group.getChildren().addAll(theta,angularVelocity,label,label1,submitInitialConfig,textArea,pause,play);
        final Timeline[] fiveSecondsWonder = new Timeline[1];
        submitInitialConfig.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                int displacement = Integer.parseInt(theta.getText());
                final Rotate secondRotate = new Rotate(-1*displacement*20,0,175);


                //moves pendulum hand
                fiveSecondsWonder[0] = new Timeline(new KeyFrame(Duration.seconds(T), new EventHandler<ActionEvent>() {
                    double velocity = Double.parseDouble(angularVelocity.getText());
                    double displacement = Double.parseDouble(theta.getText());
                    @Override
                    public void handle(ActionEvent event) {
                        double angularAcceleration = obj.fuzzy(displacement,velocity);
                        String x = String.format( "angularVelocity.input = "+velocity+" and angle.input = "+displacement+" -> current.output = "+angularAcceleration);
                        textArea.appendText(x+"\n");
                        displacement = displacement+velocity*T+0.5*angularAcceleration*T*T;
                        velocity = velocity+angularAcceleration*T;
                        secondRotate.setAngle(displacement*20);
                    }
                }));
                fiveSecondsWonder[0].setCycleCount(Timeline.INDEFINITE);
                pendulumHand.getTransforms().add(secondRotate);
                circle.getTransforms().add(secondRotate);
                fiveSecondsWonder[0].play();
            }
        });
        pause.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                fiveSecondsWonder[0].pause();
            }
        });
        play.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                fiveSecondsWonder[0].play();
            }
        });
    }

    public static void main(final String[] arguments) {
        System.out.println("::::::::::::Please Input All Profiles::::::::::::");
        obj = new Main();
        obj.initiate();
        T = 0.2;
        Application.launch(arguments);
    }
}