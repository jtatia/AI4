
import javafx.animation.*;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.io.PrintWriter;
import java.text.DecimalFormat;

public class PendulumSimulation extends Application {

    public static Main obj;
    static double T;
    static File file;
    static File file1;
    static PrintWriter printWriter;
    static PrintWriter printWriter1;

    @Override
    public void start(final Stage stage) {
        final Group group = new Group();
        final Scene scene = new Scene(group, 900, 600, Color.WHITE);
        stage.setScene(scene);
        stage.setTitle("Pendulum Animation");
        stage.show();
        //Pendulum Line
        final Line pendulumHand = new Line(0, 175, 0, 0);
        pendulumHand.setTranslateX(650);
        pendulumHand.setTranslateY(80);

        //Pendulum Ball
        final Circle circle = new Circle(0, 0, 5);
        circle.setTranslateX(650);
        circle.setTranslateY(80);
        circle.setFill(Color.BLACK);

        final Rectangle rectangle = new Rectangle(550,255,200,30);
        circle.setFill(Color.BROWN);

        final Label label = new Label("Angular Displacement :");
        label.setLayoutY(15);

        final TextField theta = new TextField();
        theta.setPromptText("Enter Theta Value");
        theta.setTranslateX(165);
        theta.setTranslateY(10);

        final Label label1 = new Label("Angular Velocity :");
        label1.setLayoutY(55);

        final TextField angularVelocity = new TextField();
        angularVelocity.setPromptText("Enter angularVelocity");
        angularVelocity.setTranslateX(165);
        angularVelocity.setTranslateY(50);

        final Label klabel = new Label("K :");
        klabel.setLayoutY(95);

        final TextField k = new TextField();
        k.setPromptText("Enter K");
        k.setTranslateX(165);
        k.setTranslateY(90);

        final Label mlabel = new Label("Mass :");
        mlabel.setLayoutY(135);

        final TextField mass = new TextField();
        mass.setPromptText("Enter Mass");
        mass.setTranslateX(165);
        mass.setTranslateY(130);

        final Label rlabel = new Label("Radius :");
        rlabel.setLayoutY(175);

        final TextField radius = new TextField();
        radius.setPromptText("Enter Radius");
        radius.setTranslateX(165);
        radius.setTranslateY(170);


        final Label glabel = new Label("Gravity :");
        glabel.setLayoutY(215);

        final CheckBox gravity = new CheckBox();
        gravity.setTranslateX(165);
        gravity.setTranslateY(215);


        final Button submitInitialConfig = new Button("   Submit   ");
        submitInitialConfig.setTranslateX(165);
        submitInitialConfig.setTranslateY(255);

        final Button pause = new Button("Pause");
        pause.setTranslateX(820);
        pause.setTranslateY(150);

        final Button play = new Button("Play");
        play.setTranslateX(820);
        play.setTranslateY(190);

        final Button stop = new Button("Stop");
        stop.setTranslateX(820);
        stop.setTranslateY(230);

        final TextArea textArea = new TextArea();
        textArea.setTranslateX(50);
        textArea.setPromptText("FuzzyController Output will be displaced");
        textArea.setTranslateY(350);
        textArea.setPrefWidth(790);
        textArea.setPrefHeight(200);

        group.getChildren().add(circle);
        group.getChildren().add(pendulumHand);
        group.getChildren().add(rectangle);
        group.getChildren().addAll(theta,angularVelocity,k,mass,radius,gravity,label,label1,klabel,mlabel,rlabel,glabel,submitInitialConfig,textArea,pause,play,stop);
        final Timeline[] fiveSecondsWonder = new Timeline[1];
        submitInitialConfig.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                int displacement = Integer.parseInt(theta.getText());
                final Rotate secondRotate = new Rotate(Math.toDegrees(displacement),0,175);


                //moves pendulum hand
                fiveSecondsWonder[0] = new Timeline(new KeyFrame(Duration.seconds(T), new EventHandler<ActionEvent>() {
                    double velocity = Double.parseDouble(angularVelocity.getText());
                    double displacement = Double.parseDouble(theta.getText());
                    int proportionalityConstant = Integer.parseInt(k.getText());
                    double m = Double.parseDouble(mass.getText());
                    double r = Double.parseDouble(radius.getText());
                    boolean enableGravity = gravity.isSelected();
                    @Override
                    public void handle(ActionEvent event) {
                        double angularAcceleration = obj.fuzzy(displacement,velocity, proportionalityConstant, m, r, enableGravity);
                        DecimalFormat format = new DecimalFormat("##.000000");
                        printWriter.println(angularAcceleration + ", " + displacement);
                        printWriter1.println(angularAcceleration + ", " + velocity);
                        String x = String.format( "angularVelocity = "+velocity+" and angle = "+displacement+" -> current.output = "+angularAcceleration);
                        textArea.appendText(x+"\n");
                        displacement = displacement+velocity*T+0.5*angularAcceleration*T*T;
                        velocity = velocity+angularAcceleration*T;
                        secondRotate.setAngle(Math.toDegrees(displacement));
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
        stop.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                fiveSecondsWonder[0].stop();
                printWriter.close();
                printWriter1.close();
            }
        });
    }

    public static void main(final String[] arguments)throws Exception {
        System.out.println("::::::::::::Please Input All Profiles::::::::::::");
        obj = new Main();
        obj.initiate();
        file = new File("file1.txt");
        printWriter = new PrintWriter("file1.txt");
        file1 = new File("file2.txt");
        printWriter1= new PrintWriter("file2.txt");
        T = 0.01;
        Application.launch(arguments);
    }
}