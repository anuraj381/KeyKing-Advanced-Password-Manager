package aj.apps.java.Main;

import aj.apps.java.embeddedDB.dbConn;
import aj.apps.java.faceRecog.cam;
import aj.apps.java.faceRecog.cvTask;
import aj.apps.java.network.javaNetworking;
import aj.apps.java.network.onlineDBconn;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * This class contains methods which provides functionality for various operations and manipulations on
 * various elements in the client area, for example changing password for a particular entry in the table,
 * synchronizing data with online database, showing Alert box any many more
 *
 * <p>One thing to note here is this class contains only and all methods which requires to
 * show a popup window for taking some user input</p>
 * @author Anuraj Jain
 */
public class popup extends userArea {

    private static boolean answer;
    private static String result;
    public static String getResult = "";
    static long timeVar = System.currentTimeMillis();

    /**
     * This method display a alert box with a message and two choices YES and NO
     * @param title title for alert box
     * @param message message for the alert box
     * @return answer user chooses (yes or no)
     */
    static boolean AlertBox(String title, String message) {
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle(title);
        window.setMinWidth(250);
        window.getIcons().addAll(
                new Image("aj/apps/resources/icons/icon16.png"),
                new Image("aj/apps/resources/icons/icon32.png"),
                new Image("aj/apps/resources/icons/icon64.png")
        );
        window.centerOnScreen();

        Label label = new Label();
        label.setText(message + "\n");

        //Create YES & NO buttons
        Button yesButton = new Button("Yes");
        yesButton.setAlignment(Pos.CENTER);
        yesButton.setPadding(new Insets(5));
        Button noButton = new Button("No");
        noButton.setAlignment(Pos.CENTER);
        noButton.setPadding(new Insets(5));

        yesButton.setOnAction(e -> {
            answer = true;
            window.close();
        });
        noButton.setOnAction(e -> {
            answer = false;
            window.close();
        });

        HBox layout = new HBox(10);
        layout.getChildren().addAll(yesButton, noButton);
        layout.setAlignment(Pos.CENTER);

        VBox vBox = new VBox(10);
        vBox.getChildren().addAll(label, layout);
        vBox.setAlignment(Pos.CENTER);
        vBox.setPadding(new Insets(10));

        Scene scene = new Scene(vBox);
        scene.getStylesheets().add("aj/apps/resources/myStyle.css");
        window.setScene(scene);
        window.showAndWait();

        return answer;
    }

    /**
     * this method provides functionality for adding a new account/entry
     * @param mobile mobile number with which user logged in
     * @param selected group name in which entry to be added
     */
    static void addAccount(String mobile, String selected) {
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Create a new Account");
        window.setMinWidth(500);
        window.setMinHeight(500);
        window.getIcons().addAll(
                new Image("aj/apps/resources/icons/icon16.png"),
                new Image("aj/apps/resources/icons/icon32.png"),
                new Image("aj/apps/resources/icons/icon64.png")
        );
        window.centerOnScreen();

        Label accNameLabel = new Label("Account Name");
        GridPane.setConstraints(accNameLabel, 0, 0);

        TextField accname = new TextField();
        accname.setPromptText("Enter Account Name");
        accname.setMaxSize(200, 1);
        GridPane.setConstraints(accname, 1, 0);

        Label urlLabel = new Label("URL");
        GridPane.setConstraints(urlLabel, 0, 1);

        TextField url = new TextField();
        url.setPromptText("URL");
        url.setMaxSize(200, 1);
        GridPane.setConstraints(url, 1, 1);

        Label usernameLabel = new Label("Username");
        GridPane.setConstraints(usernameLabel, 0, 2);

        TextField username = new TextField();
        username.setPromptText("Username");
        username.setMaxSize(200, 1);
        GridPane.setConstraints(username, 1, 2);

        Label passLabel = new Label("Password");
        GridPane.setConstraints(passLabel, 0, 3);

        TextField password = new TextField();
        password.setPromptText("password");
        password.setMaxSize(200, 1);
        GridPane.setConstraints(password, 1, 3);

        CheckBox box1 = new CheckBox("Show password in the table");
        box1.setSelected(true);
        CheckBox box2 = new CheckBox("Show hint only instead of password (more secure way)");
        VBox boxes = new VBox(10);
        boxes.getChildren().addAll(box1, box2);
        GridPane.setConstraints(boxes, 1, 4);

        TextField hint = new TextField();
        hint.setPromptText("Hint");
        hint.setMaxSize(200, 1);

        Button btn = new Button("Submit");
        GridPane.setConstraints(btn, 1, 5);
        btn.setOnAction(e -> {
            if (box2.isSelected()) {
                boolean ans = isValid(accname.getText(), url.getText(), username.getText(), password.getText(), hint.getText());
                if (ans) {
                    dbConn.addAccount(mobile, accname.getText(), url.getText(), username.getText(), password.getText(), hint.getText(), true, selected);
                    System.out.println("valid details");
                    messageBox("Account details saved successfully...");
                    window.close();
                } else {
                    messageBox("fill in all the fields...");
                }
            } else {
                boolean ans = isValid(accname.getText(), url.getText(), username.getText(), password.getText(), "hint");
                if (ans) {
                    dbConn.addAccount(mobile, accname.getText(), url.getText(), username.getText(), password.getText(), "", false, selected);
                    System.out.println("valid details");
                    messageBox("Account details saved successfully...");
                    window.close();
                } else {
                    messageBox("fill in all the fields");
                }
            }
        });

        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(10));
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setVgap(10);
        gridPane.setHgap(10);
        gridPane.getChildren().addAll(accNameLabel, accname, url, urlLabel, usernameLabel, username, password, passLabel, boxes, btn);

        box1.setOnAction(e -> {
            boxes.getChildren().remove(hint);
            box2.setSelected(false);
        });
        box2.setOnAction(e -> {
            boxes.getChildren().add(hint);
            box1.setSelected(false);
        });

        Scene scene = new Scene(gridPane);
        scene.getStylesheets().add("aj/apps/resources/myStyle.css");
        window.setScene(scene);
        window.showAndWait();
    }

    /**
     * this method is used for validating data from text fields
     * @param accname text field input
     * @param url text field input
     * @param username text field input
     * @param password text field input
     * @param hint text field input
     * @return true if data is valid, false otherwise
     */
    private static boolean isValid(String accname, String url, String username, String password, String hint) {
        return !(accname.isEmpty() || username.isEmpty() || password.isEmpty() || hint.isEmpty());
    }

    /**
     * this method provides functionality for displaying a message to the user
     * @param msg message to shown
     */
    public static void messageBox(String msg) {
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Message");
        window.setMinWidth(250);
        window.getIcons().addAll(
                new Image("aj/apps/resources/icons/icon16.png"),
                new Image("aj/apps/resources/icons/icon32.png"),
                new Image("aj/apps/resources/icons/icon64.png")
        );
        window.centerOnScreen();

        Label label = new Label();
        label.setText(msg);

        //Create two buttons
        Button yesButton = new Button("Okay");
        yesButton.setAlignment(Pos.CENTER);
        yesButton.setPadding(new Insets(5));

        yesButton.setOnAction(e -> {
            window.close();
        });

        VBox vBox = new VBox(10);
        vBox.getChildren().addAll(label, yesButton);
        vBox.setAlignment(Pos.CENTER);
        vBox.setPadding(new Insets(10));

        Scene scene = new Scene(vBox);
        scene.getStylesheets().add("aj/apps/resources/myStyle.css");
        window.setScene(scene);
        window.showAndWait();
    }

    /**
     * this method provides functionality for changing the master password for the user
     * @param mobile mobile number with which user logged in
     */
    static void changeMasterPass(String mobile){
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Change Master Password");
        window.setMinWidth(300);
        window.setMinHeight(300);
        window.getIcons().addAll(
                new Image("aj/apps/resources/icons/icon16.png"),
                new Image("aj/apps/resources/icons/icon32.png"),
                new Image("aj/apps/resources/icons/icon64.png")
        );
        window.centerOnScreen();

        Label passLabel = new Label("Current Master Password");
        GridPane.setConstraints(passLabel, 0, 0);

        PasswordField passArea = new PasswordField();
        passArea.setPromptText("Current Master Password");
        GridPane.setConstraints(passArea, 1, 0);

        Label newPassLabel = new Label("New Master Password");
        GridPane.setConstraints(newPassLabel, 0, 1);

        PasswordField newpass = new PasswordField();
        newpass.setPromptText("New Master Password");
        GridPane.setConstraints(newpass, 1, 1);

        Label repeatPassLabel = new Label("Repeat Password");
        GridPane.setConstraints(repeatPassLabel, 0, 2);

        PasswordField repeatpass = new PasswordField();
        repeatpass.setPromptText("Repeat Password");
        GridPane.setConstraints(repeatpass, 1, 2);

        Button yesButton = new Button("Submit");
        yesButton.setAlignment(Pos.CENTER);
        yesButton.setPadding(new Insets(5));
        GridPane.setConstraints(yesButton, 1, 3);

        yesButton.setOnAction(e -> {
            if (newpass.getText().isEmpty() && repeatpass.getText().isEmpty()) {
                popup.messageBox("password field can't be empty...");
            } else {
                if (Objects.equals(newpass.getText(), repeatpass.getText())) {
                    if (verifyOtp(mobile)) {
                        if (dbConn.changeMasterPassword(mobile, passArea.getText(), newpass.getText())) {
                            popup.messageBox("your master password updated successfully, But note that password is updated for this device only.\nYou should update your master password in online database also in order to achieve effective synchronisation of data among all your devices");
                            if (popup.AlertBox("Online DB master password", "Do you want to update your master password in online database (if you did or wish to synchronise your KeyKing database online...)")){
                                if(sync(mobile)) {
                                    if (onlineDBconn.updateMasterPass(mobile, newpass.getText())) {
                                        messageBox("updated successfully, now whenever you open your KeyKing account on any device use this as your master password to sync data...");
                                    }
                                } else {
                                    popup.messageBox("Synchronisation failed... \n" +
                                            "Possible reasons-\n" +
                                            "1. No network connection\n" +
                                            "2. Master Password in online database didn't matched with yours");
                                }
                            }
                            window.close();
                        } else {
                            popup.messageBox("incorrect password");
                        }
                    }else{
                        window.close();
                    }
                } else {
                    popup.messageBox("passwords doesn't match");
                }
            }
        });

        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(10));
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setVgap(10);
        gridPane.setHgap(10);
        gridPane.getChildren().addAll(passLabel, newPassLabel, repeatPassLabel, passArea, newpass, repeatpass, yesButton);

        Scene scene = new Scene(gridPane);
        scene.getStylesheets().add("aj/apps/resources/myStyle.css");
        window.setScene(scene);
        window.showAndWait();
    }

    /**
     * this method provides functionality for changing the password for an entry/account
     * @param mobile mobile number with which user logged in
     * @param accname account/entry name
     * @param username username for entry/account
     * @return new password if successfully updates, empty string otherwise
     */
    static String changePass(String mobile, String accname, String username) {
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Change Password");
        window.setMinWidth(300);
        window.setMinHeight(300);
        window.getIcons().addAll(
                new Image("aj/apps/resources/icons/icon16.png"),
                new Image("aj/apps/resources/icons/icon32.png"),
                new Image("aj/apps/resources/icons/icon64.png")
        );
        window.centerOnScreen();

        result = "";

        Label newPassLabel = new Label("New Password");
        GridPane.setConstraints(newPassLabel, 0, 1);

        PasswordField newpass = new PasswordField();
        newpass.setPromptText("New Password");
        GridPane.setConstraints(newpass, 1, 1);

        Label repeatPassLabel = new Label("Repeat Password");
        GridPane.setConstraints(repeatPassLabel, 0, 2);

        PasswordField repeatpass = new PasswordField();
        repeatpass.setPromptText("Repeat Password");
        GridPane.setConstraints(repeatpass, 1, 2);

        Button yesButton = new Button("Submit");
        yesButton.setAlignment(Pos.CENTER);
        yesButton.setPadding(new Insets(5));
        GridPane.setConstraints(yesButton, 1, 3);

        yesButton.setOnAction(e -> {
            if (newpass.getText().isEmpty() && repeatpass.getText().isEmpty()) {
                popup.messageBox("password field can't be empty...");
                result = "";
            } else {
                if (Objects.equals(newpass.getText(), repeatpass.getText())) {
                    if (dbConn.changePassword(mobile, accname, username, newpass.getText())) {
                        popup.messageBox("password updated successfully");
                        result = newpass.getText();
                        window.close();
                    } else {
                        popup.messageBox("some error occurred, try again later...");
                        result = "";
                    }
                } else {
                    popup.messageBox("passwords doesn't match");
                    result = "";
                }
            }
        });

        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(10));
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setVgap(10);
        gridPane.setHgap(10);
        gridPane.getChildren().addAll(newPassLabel, repeatPassLabel, newpass, repeatpass, yesButton);

        Scene scene = new Scene(gridPane);
        scene.getStylesheets().add("aj/apps/resources/myStyle.css");
        window.setScene(scene);
        window.showAndWait();
        return result;
    }

    /**
     * this method provides functionality for OTP(one time password) verification
     * @param mobile mobile number with which user logged in
     * @return true if verification successful, false otherwise
     */
    static boolean verifyOtp(String mobile) {

        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("OTP verification");
        window.setMinWidth(300);
        window.setMinHeight(300);
        window.getIcons().addAll(
                new Image("aj/apps/resources/icons/icon16.png"),
                new Image("aj/apps/resources/icons/icon32.png"),
                new Image("aj/apps/resources/icons/icon64.png")
        );
        window.centerOnScreen();

        answer = false;
        int min = 100000;
        int max = 999999;
        int randomNum = ThreadLocalRandom.current().nextInt(min, max + 1);
        String otp = String.valueOf(randomNum);

        //---------------------Scene1-------------------------------------------------------
        Label labels = new Label("Sending OTP to your mobile number please wait...");
        labels.setStyle("-fx-font-size: 15px");
        final ProgressIndicator pin = new ProgressIndicator();
        pin.setStyle(" -fx-progress-color: darkblue;");
        // changing size without css
        pin.setMinWidth(100);
        pin.setMinHeight(100);
        pin.setProgress(-1.0f);

        final VBox vb = new VBox(20);
        vb.getChildren().addAll(labels, pin);
        vb.setPadding(new Insets(20));
        vb.setAlignment(Pos.CENTER);
        Scene scene1 = new Scene(vb);
        scene1.getStylesheets().add("aj/apps/resources/myStyle.css");

        //----------------------Scene2-------------------------------------------------------
        Label label = new Label();
        label.setStyle("-fx-font-size: 15px");
        if (javaNetworking.sendOtp(mobile, otp)) {
            label.setText("Enter the OTP KeyKing has just sent to your mobile phone");
        } else {
            label.setStyle("-fx-text-fill: red");
            label.setText("Error in sending the otp");
        }

        Label timeLabel = new Label();

        TextField textField = new TextField();
        textField.setPromptText("Enter OTP");
        textField.setMaxWidth(200);

        Button yesButton = new Button("Submit");
        yesButton.setAlignment(Pos.CENTER);
        yesButton.setPadding(new Insets(5));

        yesButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                System.out.println(textField.getText());
                if (Objects.equals(textField.getText(), otp)) {
                    popup.messageBox("verification successful");
                    answer = true;
                } else {
                    popup.messageBox("verification failed");
                    answer = false;
                }
                window.close();
            }
        });

        VBox vBox = new VBox(10);
        vBox.getChildren().addAll(label, timeLabel, textField, yesButton);
        vBox.setAlignment(Pos.CENTER);
        vBox.setPadding(new Insets(10));

        Scene scene2 = new Scene(vBox);
        scene2.getStylesheets().add("aj/apps/resources/myStyle.css");

        //----------------------Time Event--------------------------------------------------
        timeVar = System.currentTimeMillis() + 60000;
        GridPane.setConstraints(timeLabel, 1, 9);
        DateFormat timeFormat = new SimpleDateFormat("ss");
        final Timeline timeline = new Timeline(
                new KeyFrame(
                        Duration.millis(500),
                        event -> {
                            if (!Objects.equals(getResult, "")) {
                                if (Objects.equals(getResult, "sent")) {
                                    window.setScene(scene2);
                                    final long diff = timeVar - System.currentTimeMillis();
                                    if (diff < 0) {
                                        //  timeLabel.setText( "00:00:00" );
                                        answer = false;
                                        window.close();
                                    } else {
                                        timeLabel.setStyle("-fx-text-fill: green");
                                        timeLabel.setText("( " + timeFormat.format(diff) + " )");
                                    }
                                } else if (Objects.equals(getResult, "failed")) {
                                    answer = false;
                                    window.close();
                                }
                            } else {
                                timeVar = System.currentTimeMillis() + 60000;
                            }
                        }
                )
        );
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();

        window.setScene(scene1);
        window.showAndWait();
        return answer;
    }

    /**
     * this method provides functionality synchronization of account with online database
     * @param mobile mobile number with which user logged in
     * @return true if synchronization is successful, false otherwise
     */
    static boolean sync(String mobile) {
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Syncing");
        window.setMinWidth(300);
        window.setMinHeight(300);
        window.getIcons().addAll(
                new Image("aj/apps/resources/icons/icon16.png"),
                new Image("aj/apps/resources/icons/icon32.png"),
                new Image("aj/apps/resources/icons/icon64.png")
        );
        window.centerOnScreen();

        answer = false;
        Label labels = new Label("Syncing... please wait...");
        labels.setStyle("-fx-font-size: 15px");
        final ProgressIndicator pin = new ProgressIndicator();
        pin.setStyle(" -fx-progress-color: darkblue;");
        // changing size without css
        pin.setMinWidth(100);
        pin.setMinHeight(100);
        pin.setProgress(-1.0f);

        final VBox vb = new VBox(20);
        vb.getChildren().addAll(labels, pin);
        vb.setPadding(new Insets(20));
        vb.setAlignment(Pos.CENTER);
        Scene scene1 = new Scene(vb);
        scene1.getStylesheets().add("aj/apps/resources/myStyle.css");

        dbConn.sync(mobile);

        final Timeline timeline = new Timeline(
                new KeyFrame(
                        Duration.millis(500),
                        event -> {
                            if (Objects.equals(getResult, "done")) {
                                answer = true;
                                window.close();
                            } else if (Objects.equals(getResult, "failed")) {
                                answer = false;
                                window.close();
                            }
                        }
                )
        );
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();

        window.setScene(scene1);
        window.showAndWait();
        return answer;
    }

    /**
     * this method provides functionality generating random strong password
     * @return generates password
     */
    static String generatePass() {

        final String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        final String numbers = "1234567890";
        final String Scharacters = "!@#$%^&*(){}[]";

        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Password Generator");
        window.setMinWidth(300);
        window.setMinHeight(300);
        window.getIcons().addAll(
                new Image("aj/apps/resources/icons/icon16.png"),
                new Image("aj/apps/resources/icons/icon32.png"),
                new Image("aj/apps/resources/icons/icon64.png")
        );
        window.centerOnScreen();

        Label label = new Label("Password Length");
        GridPane.setConstraints(label, 1, 0);

        TextField txt = new TextField();
        txt.setPromptText("Enter password length");
        txt.setMaxWidth(300);
        GridPane.setConstraints(txt, 1, 1);

        TextField field = new TextField();
        field.setPromptText("Generated Password");
        field.setMaxWidth(300);
        GridPane.setConstraints(field, 1, 9);

        CheckBox box1 = new CheckBox("include characters");
        CheckBox box2 = new CheckBox("include numbers");
        CheckBox box3 = new CheckBox("include special characters");
        box1.setSelected(true);
        box2.setSelected(true);
        box3.setSelected(true);
        GridPane.setConstraints(box1, 1, 3);
        GridPane.setConstraints(box2, 1, 4);
        GridPane.setConstraints(box3, 1, 5);

        Button button = new Button("Generate");
        button.setAlignment(Pos.CENTER);
        button.setPadding(new Insets(5));
        GridPane.setConstraints(button, 1, 7);

        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String raw = "";
                if (box1.isSelected()) {
                    raw += characters;
                }
                if (box2.isSelected()) {
                    raw += numbers;
                }
                if (box3.isSelected()) {
                    raw += Scharacters;
                }

                if (txt.getText().length() != 0) {
                    if (raw.length() != 0) {
                        try {
                            int count = Integer.parseInt(txt.getText());
                            StringBuilder builder = new StringBuilder();
                            while (count-- != 0) {
                                int character = (int) (Math.random() * raw.length());
                                builder.append(raw.charAt(character));
                            }
                            field.setText(builder.toString());
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                            field.setText("password length must be a number...");
                        }
                    } else {
                        field.setText("password length can not be zero, select at least one checkbox");
                    }
                } else {
                    field.setText("choose appropriate password length");
                }
            }
        });

        txt.textProperty().addListener((observable, oldValue, newValue) -> {
            if(!newValue.isEmpty()) {
                try {
                    int get_id = Integer.parseInt(newValue);
                    if (get_id == 0) {
                        txt.clear();
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    txt.clear();
                    popup.messageBox("ID can only be integer value other than 0");
                }
            }
        });

        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(10));
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setVgap(10);
        gridPane.setHgap(10);
        gridPane.getChildren().addAll(label, txt, box1, box2, box3, button, field);

        Scene scene = new Scene(gridPane);
        scene.getStylesheets().add("aj/apps/resources/myStyle.css");
        window.setScene(scene);
        window.showAndWait();

        return field.getText();
    }

    /**
     * this method provides functionality for adding a new group for accounts/entries
     * @param mobile mobile number with which user logged in
     * @return true if group is created, false otherwise
     */
    static boolean addGrp(String mobile) {

        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Add New Group");
        window.setMinWidth(300);
        window.setMinHeight(300);
        window.getIcons().addAll(
                new Image("aj/apps/resources/icons/icon16.png"),
                new Image("aj/apps/resources/icons/icon32.png"),
                new Image("aj/apps/resources/icons/icon64.png")
        );
        window.centerOnScreen();

        answer = false;

        TextField textField = new TextField();
        textField.setPromptText("enter new group name");
        textField.setMaxWidth(200);

        Button yesButton = new Button("Submit");
        yesButton.setAlignment(Pos.CENTER);
        yesButton.setPadding(new Insets(5));

        yesButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                if (!textField.getText().isEmpty()) {
                    dbConn.addGrp(mobile, textField.getText());
                    answer = true;
                    messageBox(textField.getText() + " group is added...");
                    window.close();
                } else {
                    answer = false;
                    messageBox("name can't be empty");
                }
            }
        });

        Label label = new Label("New Group Name");

        VBox vBox = new VBox(10);
        vBox.getChildren().addAll(label, textField, yesButton);
        vBox.setAlignment(Pos.CENTER);
        vBox.setPadding(new Insets(10));

        Scene scene = new Scene(vBox);
        scene.getStylesheets().add("aj/apps/resources/myStyle.css");
        window.setScene(scene);
        window.showAndWait();
        return answer;
    }

    /**
     * This method provides functionality for adding up extra layers of security to user's KeyKing account
     * <p>this method provides two security layers -
     * 1. Face Recognition Security
     * 2. OTP(one time password) Verification Security
     * any one of these security layers or both can be enabled</p>
     * @param mobile mobile number with which user logged in
     */
    static void securityTools(String mobile) {
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Security");
        window.setMinWidth(250);
        window.getIcons().addAll(
                new Image("aj/apps/resources/icons/icon16.png"),
                new Image("aj/apps/resources/icons/icon32.png"),
                new Image("aj/apps/resources/icons/icon64.png")
        );
        window.centerOnScreen();

        Label label2 = new Label("Face Recognition Security");
        label2.setStyle("-fx-font-size: 14px; -fx-text-fill: forestgreen;");
        Label label = new Label("Enter your ID");
        GridPane.setConstraints(label, 0, 0);
        TextField textField = new TextField();
        textField.setPromptText("Enter your ID");
        textField.setMaxWidth(200);
        GridPane.setConstraints(textField, 1, 0);

        //Create two buttons
        Button yesButton = new Button("Create Dataset & Enable");
        yesButton.setAlignment(Pos.CENTER);
        yesButton.setPadding(new Insets(5));
        GridPane.setConstraints(yesButton, 1, 2);

        yesButton.setOnAction(e -> {
            if (!textField.getText().isEmpty()) {
                try {
                    cvTask.id = Integer.parseInt(textField.getText());
                    if (cam.openCam(cvTask.CREATE_DATASET) == 1) {
                        if (dbConn.modifySecurity(mobile, cvTask.id, true, true, false)) {
                            window.close();
                            popup.messageBox("Face recognition security enabled");
                        }
                    } else {
                        window.close();
                        messageBox("There is some error, try again later...");
                    }
                } catch (Exception e1) {
                    e1.printStackTrace();
                    window.close();
                    messageBox("There is some error, try again later...");
                }
            } else {
                popup.messageBox("ID cannot be zero or empty");
            }
        });

        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if(!newValue.isEmpty()) {
                try {
                    int get_id = Integer.parseInt(newValue);
                    if (get_id == 0) {
                        textField.clear();
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    textField.clear();
                    popup.messageBox("ID can only be integer value other than 0");
                }
            }
        });

        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(10));
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setVgap(10);
        gridPane.setHgap(10);
        gridPane.getChildren().addAll(label, textField, yesButton);

        Button btn = new Button("DISABLE");
        btn.setAlignment(Pos.CENTER);
        btn.setOnAction(e -> {
            //TODO: protect from condition if files deleted and database modification failed...
            File path = new File(".");
            String actPath = path.getAbsolutePath().substring(0, path.getAbsolutePath().length() - 1) + "dataSet";
            File file = new File(actPath);
            file.mkdirs();
            File list[] = file.listFiles();
            if (list.length != 0) {
                for (File aList : list) {
                    if (aList.isFile()) {
                        if (aList.exists()) {
                            if (aList.getName().startsWith(String.valueOf(dbConn.selectRecogSecurity(mobile)))) {
                                //System.out.println(aList.getName());
                                aList.delete();
                            }
                        }
                    }
                }
            }
            if (dbConn.modifySecurity(mobile, 0, true, false, false)) {
                window.close();
                popup.messageBox("Face recognition security disabled successfully");
            } else {
                window.close();
                popup.messageBox("there is some error, try again later...");
            }
        });
        VBox vBox = new VBox();
        vBox.setAlignment(Pos.CENTER);
        vBox.getChildren().add(btn);

        //----------------------- OTP Security------------------------------
        Label label1 = new Label("OTP Security");
        label1.setStyle("-fx-font-size: 14px; -fx-text-fill: forestgreen;");
        ToggleButton toggleButton = new ToggleButton();
        toggleButton.setStyle("-fx-background-radius: 30;\n" +
                "    -fx-background-insets: 0,1,1;\n" +
                "    -fx-text-fill: #738b9a;\n" +
                "    -fx-font-weight: bold;\n" +
                "    -fx-effect: dropshadow( three-pass-box , rgba(0,0,0,0.6) , 3, 0.0 , 0 , 1 );");
        if (dbConn.selectOtpSecurity(mobile)){
            toggleButton.setSelected(true);
            toggleButton.setText("ENABLED");
        }else {
            toggleButton.setSelected(false);
            toggleButton.setText("DISABLED");
        }
        VBox vBox2 = new VBox(10);
        vBox2.setAlignment(Pos.CENTER);
        vBox2.setPadding(new Insets(20));
        vBox2.getChildren().addAll(label1, toggleButton);
        toggleButton.setOnAction(e->{
            if (toggleButton.isSelected()){
                if (dbConn.modifySecurity(mobile, 0, false, false, true)){
                    toggleButton.setText("ENABLED");
                }
            }else {
                if (dbConn.modifySecurity(mobile, 0, false, false, false)){
                    toggleButton.setText("DISABLED");
                }
            }
        });

        VBox vBox1 = new VBox(10);
        vBox1.setAlignment(Pos.CENTER);
        vBox1.setPadding(new Insets(10));
        Separator separator1 = new Separator();
        separator1.setOrientation(Orientation.HORIZONTAL);
        separator1.setId("separator1");
        separator1.getStyleClass().add("my-separator");

        if (dbConn.selectRecogSecurity(mobile) != 0) {
            vBox1.getChildren().addAll(label2, vBox, separator1, vBox2);
        } else {
            vBox1.getChildren().addAll(label2, gridPane, separator1, vBox2);
        }
        Scene scene = new Scene(vBox1);
        scene.getStylesheets().add("aj/apps/resources/myStyle.css");
        window.setScene(scene);
        window.showAndWait();
    }
}