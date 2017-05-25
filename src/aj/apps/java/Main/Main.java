package aj.apps.java.Main;

import aj.apps.java.embeddedDB.dbConn;
import aj.apps.java.faceRecog.cam;
import aj.apps.java.faceRecog.cvTask;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import java.util.Objects;

/**
 * This class contains the main method, and hence initiates the application
 * @author Anuraj jain
 */

public class Main extends Application {

    private String msg;
    public static String connection = "";
    private Timeline timeline = null;

    /**
     * This is the main method i.e. provides a starting point to the application
     * <p>it loads the gui of the application using JavaFX api, by calling the launch method</p>
     * <p>this method also checks for the existence of the database</p>
     * @author Anuraj jain
     * @param args command line arguments
     */
    public static void main(String[] args) {
        dbConn.checkdb();
        launch(args);
    }

    /**
     * This method initiates the gui of the application
     * <p>This method contains the initialisation of two stages one for the
     * splash screen and other for the main application window</p>
     * @param primaryStage primary stage of the application
     * @throws Exception
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        Stage stage = primaryStage;
        stage.getIcons().addAll(
                new Image("aj/apps/resources/icons/icon16.png"),
                new Image("aj/apps/resources/icons/icon32.png"),
                new Image("aj/apps/resources/icons/icon64.png")
                );
        stage.centerOnScreen();
        Stage splashScreen = new Stage();
        splashScreen.getIcons().addAll(
                new Image("aj/apps/resources/icons/icon16.png"),
                new Image("aj/apps/resources/icons/icon32.png"),
                new Image("aj/apps/resources/icons/icon64.png")
        );

        Image image = new Image("aj/apps/resources/gif2.gif");
        ImageView imageView = new ImageView();
        imageView.setImage(image);

        VBox vbox=new VBox();
        vbox.setAlignment(Pos.CENTER);
        vbox.getChildren().add(imageView);

        Scene scene = new Scene(vbox);
        splashScreen.initStyle(StageStyle.TRANSPARENT);
        scene.setFill(Color.TRANSPARENT);
        splashScreen.setScene(scene);
        splashScreen.show();

        timeline = new Timeline(
                new KeyFrame(
                        Duration.millis(500),
                        event -> {
                            if (!Objects.equals(connection, "")) {
                                if (Objects.equals(connection, "done")) {
                                    timeline.stop();
                                    splashScreen.close();
                                    login(stage);
                                } else if (Objects.equals(connection, "failed")) {
                                    dbConn.initialise();
                                    connection = "";
                                }
                            }
                        }
                )
        );
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();

        stage.setOnCloseRequest(e ->{
            e.consume(); //<- Telling java that we are going to take care of it.
            boolean ans = popup.AlertBox("Confirm exit", "Are you sure you want to exit KeyKing");
            if (ans){
                stage.close();
            }
        });
    }

    /**
     * This methos checks for the validity of data entered in the text fields
     * @param textField TextField input
     * @param passwordField TextField input
     * @return true if data is valid, false otherwise
     */
    private boolean isValid(TextField textField, PasswordField passwordField) {
        if(textField.getText().length() == 10) {
            try {
                //Integer.parseInt(TextField.getText());
                Long.parseLong(textField.getText());
            } catch (NumberFormatException e) {
                msg = "Mobile number can have only numbers";
                System.out.println("Error: " + textField.getText() + " is not a ID");
                return false;
            }
        }else{
            msg = "length of mobile number should be 10 digits";
            return false;
        }
        return true;
    }

    /**
     * This methos checks for the validity of data entered in the text fields
     * @param name TextField input
     * @param surname TextField input
     * @param mobile TextField input
     * @param email TextField input
     * @param password TextField input
     * @param repeatPassword TextField input
     * @return true if data is valid, false otherwise
     */
    private boolean isValid(String name, String surname, String mobile, String email, String password, String repeatPassword){

        if(name.isEmpty() || surname.isEmpty() || mobile.isEmpty() || email.isEmpty() || password.isEmpty()){
            msg = "Fill in all the fields";
            return false;
        }else{
            if(Objects.equals(password, repeatPassword)) {
                if (mobile.length() == 10) {
                    try {
                        Long.parseLong(mobile);
                    } catch (NumberFormatException e) {
                        msg = "mobile number should only contain numbers";
                        return false;
                    }
                } else {
                    msg = "enter a valid mobile number";
                    return false;
                }
            }else{
                msg = "passwords doesn't match";
                return false;
            }
            msg = "details are valid...";
            return true;
        }
    }

    /**
     * This method provide functionality for logging in the user
     * @param window primary stage of the application
     */
    void login(Stage window){
        window.setTitle("Login");
        window.setMinWidth(350);
        window.setMinHeight(350);

        Label label = new Label("Login to your account");
        label.setAlignment(Pos.CENTER);
        label.setFont(new Font("Courier New", 15.0));
        GridPane.setConstraints(label, 1,0);

        Label textLabel = new Label("Mobile Number");
        GridPane.setConstraints(textLabel, 0,1);

        TextField textField = new TextField();
        textField.setPromptText("Mobile Number");
        textField.setMaxSize(200, 1);
        GridPane.setConstraints(textField, 1,1);

        Label PassLabel = new Label("Password");
        GridPane.setConstraints(PassLabel, 0,2);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setMaxSize(200,1);
        GridPane.setConstraints(passwordField, 1,2);

        Button button = new Button("Log In");
        GridPane.setConstraints(button, 1,3);
        button.setOnAction( e-> {
            if(isValid(textField, passwordField)){
                String user = dbConn.login(textField.getText(), passwordField.getText());
                System.out.println(user);
                if (!Objects.equals(user, "")){
                    int id = dbConn.selectRecogSecurity(textField.getText());
                    if(id != 0){
                        try {
                            int getID = cam.openCam(cvTask.RECOGNISE_FACE);
                            if (id == getID){
                                if (dbConn.selectOtpSecurity(textField.getText())){
                                    if (popup.verifyOtp(textField.getText())){
                                        userArea.DisplayTable(window, textField.getText(), user);
                                    }
                                }else{
                                    userArea.DisplayTable(window, textField.getText(), user);
                                }
                            }else{
                                popup.messageBox("Face Recognition failed...");
                            }
                        } catch (Exception e1) {
                            label.setStyle("-fx-text-fill: red");
                            label.setText("there is some error...");
                            e1.printStackTrace();
                        }
                    }else {
                        if (dbConn.selectOtpSecurity(textField.getText())){
                            if (popup.verifyOtp(textField.getText())){
                                userArea.DisplayTable(window, textField.getText(), user);
                            }
                        }else{
                            userArea.DisplayTable(window, textField.getText(), user);
                        }
                    }
                }else{
                    label.setStyle("-fx-text-fill: red");
                    label.setText("wrong mobile number or password...");
                }
            }else{
                label.setStyle("-fx-text-fill: red");
                label.setText("Error: "+msg);
                System.out.println("Error: "+msg);
            }
        });

        Button register = new Button("Register");
        GridPane.setConstraints(register, 1,6);

        register.setOnAction(e-> {
            window.close();
            register(window);
        });

        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(10));
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setVgap(10);
        gridPane.setHgap(10);
        gridPane.getChildren().addAll(label, textLabel, PassLabel, textField, passwordField, button, register);

        Scene scene = new Scene(gridPane);
        scene.getStylesheets().add("aj/apps/resources/myStyle.css");
        window.setScene(scene);
        window.show();
    }

    /**
     * This method provides functionality for registering the user
     * @param window primary stage of the application
     */
    private void register(Stage window){
        window.setTitle("Register");
        window.setMinWidth(350);
        window.setMinHeight(500);

        Label label = new Label("Register your account");
        label.setAlignment(Pos.CENTER);
        label.setFont(new Font("Courier New", 15.0));
        GridPane.setConstraints(label, 1,0);

        Label firstnameLabel = new Label("Firstname");
        GridPane.setConstraints(firstnameLabel, 0,1);

        TextField name = new TextField();
        name.setPromptText("Firstname");
        name.setMaxSize(200, 5);
        GridPane.setConstraints(name, 1,1);

        Label lastnameLabel = new Label("Lastname");
        GridPane.setConstraints(lastnameLabel, 0,2);

        TextField surname = new TextField();
        surname.setPromptText("Lastname");
        surname.setMaxSize(200, 5);
        GridPane.setConstraints(surname, 1,2);

        Label mobileLabel = new Label("Mobile Number");
        GridPane.setConstraints(mobileLabel, 0,3);

        TextField mobile = new TextField();
        mobile.setPromptText("Mobile");
        mobile.setMaxSize(200, 5);
        GridPane.setConstraints(mobile, 1,3);

        Label emailLabel = new Label("Email Address");
        GridPane.setConstraints(emailLabel, 0,4);

        TextField email = new TextField();
        email.setPromptText("Email");
        email.setMaxSize(200, 5);
        GridPane.setConstraints(email, 1,4);

        Label PassLabel = new Label("Password");
        GridPane.setConstraints(PassLabel, 0,5);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setMaxSize(200,5);
        GridPane.setConstraints(passwordField, 1,5);

        Label repeatPassLabel = new Label("Repeat Password");
        GridPane.setConstraints(repeatPassLabel, 0,6);

        PasswordField repeatPassword = new PasswordField();
        repeatPassword.setPromptText("repeat password");
        repeatPassword.setMaxSize(200,5);
        GridPane.setConstraints(repeatPassword, 1,6);

        Button button = new Button("Register");
        GridPane.setConstraints(button, 1,7);
        button.setOnAction( e-> {
            if(isValid(name.getText(), surname.getText(), mobile.getText(), email.getText(), passwordField.getText(), repeatPassword.getText())){
                if(popup.verifyOtp(mobile.getText())) {
                    if (dbConn.addUser(name.getText(), surname.getText(), mobile.getText(), email.getText(), passwordField.getText())) {
                        dbConn.initialGrps(mobile.getText());
                        dbConn.addSecurity(mobile.getText(), 0, false, false);
                        System.out.println("registered...");
                        popup.messageBox("Registration Successful....");
                        window.close();
                        login(window);
                    } else {
                        popup.messageBox("Error: possible reason- someone already registered with this mobile number");
                    }
                }
            }else{
                label.setStyle("-fx-text-fill: red");
                label.setText("Error: "+msg);
                System.out.println("Error");
            }
        });

        Button login = new Button("Log In");
        GridPane.setConstraints(login, 1,10);

        login.setOnAction(e-> {
            window.close();
            login(window);
        });

        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(10));
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setVgap(10);
        gridPane.setHgap(10);
        gridPane.getChildren().addAll(label, firstnameLabel, lastnameLabel, mobileLabel, emailLabel, PassLabel, repeatPassLabel , name, surname, mobile, email ,passwordField, repeatPassword, button, login);

        Scene scene = new Scene(gridPane);
        scene.getStylesheets().add("aj/apps/resources/myStyle.css");
        window.setScene(scene);
        window.show();
    }
}