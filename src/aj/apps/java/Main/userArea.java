package aj.apps.java.Main;

import aj.apps.java.embeddedDB.dbConn;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.*;
import javafx.scene.image.Image;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;

/**
 * This class contains methods for displaying and manipulating the elements
 * of the client area of the application
 * @author Anuraj Jain
 */
class userArea {

    private static TableView<details> table;
    private static ListView<String> listView;

    /**
     * This method is used to return the imageView of image path passed in the parameter
     * @param name image path
     * @return imageView
     */
    private ImageView setImage(String name){
        Image image = new Image("aj/apps/resources/icons/"+name);
        return new ImageView(image);
    }

    /**
     * This method is used to display the main client area of the application with various functionalities
     * <p>The GUI provided by this method contains Menu bar, Tool bar, Groups list,
     *  table of various account entries and a footer</p>
     * @param window primary stage of the application
     * @param mobile mobile number with which user logged in
     * @param user name of the user logged in
     */
    static void DisplayTable(Stage window, String mobile, String user){

        window.setTitle("KeyKing");

        Label footerItem = new Label();
        footerItem.setStyle("-fx-text-fill: #ffffff;");

        ToggleButton toggleButton = new ToggleButton("All");

        userArea object = new userArea();
        Button btn1 = new Button("Add Entry");
        btn1.setGraphic(object.setImage("addEntry.png"));
        Button btn2 = new Button("Delete Entry");
        btn2.setGraphic(object.setImage("deleteEntry.png"));
        btn2.setOnAction(e-> removeAcc(mobile));
        btn2.setDisable(true);
        Button btn3 = new Button("Copy Password");
        btn3.setGraphic(object.setImage("copyPassword.png"));
        btn3.setOnAction(e-> cpyPass());
        btn3.setDisable(true);
        Button btn4 = new Button("Copy Username");
        btn4.setGraphic(object.setImage("copyUsername.png"));
        btn4.setOnAction(e-> cpyUsername());
        btn4.setDisable(true);
        Button btn5 = new Button("Open URL");
        btn5.setGraphic(object.setImage("url.png"));
        btn5.setOnAction(e-> visitUrl());
        btn5.setDisable(true);
        Button btn7 = new Button("Add Group");
        btn7.setGraphic(object.setImage("addGrp.png"));
        btn7.setOnAction(e->{
            if(popup.addGrp(mobile)){
                listView.setItems(dbConn.selectGrps(mobile));
            }
        });

        //-------------------------------Menu & Action----------------------------------------------------
        //File Menu
        Menu fileMenu = new Menu("_File");
        MenuItem addGrp = new MenuItem("Add Group");
        MenuItem addEntry = new MenuItem("Add Entry");
        addEntry.setDisable(true);
        MenuItem ChngMstrPass = new MenuItem("Change Master Password");
        MenuItem logout = new MenuItem("Logout");
        MenuItem exit = new MenuItem("Exit");

        fileMenu.getItems().addAll(addGrp, addEntry, new SeparatorMenuItem(), ChngMstrPass, new SeparatorMenuItem(), logout, exit);

        addGrp.setOnAction(e->{
            if(popup.addGrp(mobile)){
                listView.setItems(dbConn.selectGrps(mobile));
            }
        });

        logout.setOnAction(e-> {
            Main obj = new Main();
            obj.login(window);
        });

        exit.setOnAction(e->{
            boolean ans = popup.AlertBox("Confirm exit", "Are you sure you want to exit KeyKing");
            if (ans){
                window.close();
            }
        });

        ChngMstrPass.setOnAction(e->popup.changeMasterPass(mobile));

        //Edit menu
        Menu editMenu = new Menu("_Edit");
        MenuItem delGrp = new MenuItem("Delete Group");
        MenuItem copyPswrd = new MenuItem("Copy Password");
        MenuItem cpyUsrname = new MenuItem("Copy Username");
        MenuItem vstUrl = new MenuItem("Visit URL");
        MenuItem chngPass = new MenuItem("Change Password");
        MenuItem rvlPass = new MenuItem("Reveal Password");
        MenuItem rmAcc = new MenuItem("Delete Entry");

        delGrp.setDisable(true);
        copyPswrd.setDisable(true);
        cpyUsrname.setDisable(true);
        vstUrl.setDisable(true);
        rvlPass.setDisable(true);
        chngPass.setDisable(true);
        rmAcc.setDisable(true);

        editMenu.getItems().addAll(delGrp, new SeparatorMenuItem(), cpyUsrname, copyPswrd, vstUrl, new SeparatorMenuItem(), rvlPass, chngPass, rmAcc);

        delGrp.setOnAction(e->{
            String selected = listView.getSelectionModel().getSelectedItem();
            if(popup.AlertBox("Delete Group", "Are you sure you want to delete group '"+selected+"' and all it's content")) {
                dbConn.deleteGrp(mobile, selected);
                listView.setItems(dbConn.selectGrps(mobile));
                table.getItems().clear();
            }
        });
        copyPswrd.setOnAction(e->cpyPass());
        cpyUsrname.setOnAction(e->cpyUsername());
        vstUrl.setOnAction(e->visitUrl());
        rvlPass.setOnAction(e->revealPass(mobile));
        chngPass.setOnAction(e->changePass(mobile));
        rmAcc.setOnAction(e->removeAcc(mobile));

        //Tools menu
        Menu tools = new Menu("_Tools");
        MenuItem passG = new MenuItem("Password Generator");
        MenuItem sync = new MenuItem("Sync");
        MenuItem Csync = new MenuItem("Clear & Sync");
        MenuItem recog = new MenuItem("Security");

        tools.getItems().addAll(passG, new SeparatorMenuItem(), sync, Csync, new SeparatorMenuItem(), recog);

        passG.setOnAction(e->{
            String length = popup.generatePass();
            System.out.println(length);
        });
        sync.setOnAction(e->synchronize(mobile, footerItem, toggleButton));
        Csync.setOnAction(e->{
            dbConn.clear(mobile);
            synchronize(mobile, footerItem, toggleButton);
        });
        recog.setOnAction(e->popup.securityTools(mobile));

        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().addAll(fileMenu, editMenu, tools);

        //-----------------------------------ToggleButton--------------------------------------------------------
        toggleButton.setSelected(true);
        toggleButton.setMaxWidth(Double.MAX_VALUE);
        toggleButton.setStyle("-fx-background-insets: 0,1,1;\n" +
                "    -fx-text-fill: #738b9a;\n" +
                "    -fx-font-weight: bold;\n" +
                "    -fx-effect: dropshadow( three-pass-box , rgba(0,0,0,0.6) , 3, 0.0 , 0 , 1 );");
        toggleButton.setOnAction(e->{
            addEntry.setDisable(true);
            delGrp.setDisable(true);
            copyPswrd.setDisable(true);
            cpyUsrname.setDisable(true);
            vstUrl.setDisable(true);
            rvlPass.setDisable(true);
            chngPass.setDisable(true);
            rmAcc.setDisable(true);
            btn1.setDisable(true);
            btn2.setDisable(true);
            btn3.setDisable(true);
            btn4.setDisable(true);
            btn5.setDisable(true);
            if(toggleButton.isSelected()){
                table.setItems(dbConn.selectAllItems(mobile));
                footerItem.setText("Total "+addFooterDetail()+" entries");
            }
            else {
                table.getItems().clear();
                footerItem.setText("Total "+addFooterDetail()+" entries");
            }
        });

        //-------------------------------------------------------------------------------------------------------
        //ListView
        listView = new ListView<>();
        listView.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        listView.getItems().addAll(dbConn.selectGrps(mobile));

        listView.addEventFilter(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent t) {
                toggleButton.setSelected(false);
                addEntry.setDisable(false);
                delGrp.setDisable(false);
                String selected = listView.getSelectionModel().getSelectedItem();
                table.setItems(getDetails(mobile, selected));
                copyPswrd.setDisable(true);
                cpyUsrname.setDisable(true);
                rvlPass.setDisable(true);
                vstUrl.setDisable(true);
                chngPass.setDisable(true);
                rmAcc.setDisable(true);
                btn1.setDisable(false);
                btn2.setDisable(true);
                btn3.setDisable(true);
                btn4.setDisable(true);
                btn5.setDisable(true);
                footerItem.setText("Total "+addFooterDetail()+" entries");
            }
        });

        //---------------------------------Table & Action---------------------------------------------------------
        TableColumn<details, String> accname = new TableColumn<>("Account Name");
        accname.setMinWidth(200);
        accname.setCellValueFactory(new PropertyValueFactory<>("accname"));

        TableColumn<details, String> url = new TableColumn<>("URL");
        url.setMinWidth(200);
        url.setCellValueFactory(new PropertyValueFactory<>("url"));

        TableColumn<details, String> username = new TableColumn<>("Username");
        username.setMinWidth(200);
        username.setCellValueFactory(new PropertyValueFactory<>("username"));

        TableColumn<details, String> password = new TableColumn<>("Password");
        password.setMinWidth(200);
        password.setCellValueFactory(new PropertyValueFactory<>("password"));

        TableColumn<details, String> hint = new TableColumn<>("Hint");
        hint.setMinWidth(200);
        hint.setCellValueFactory(new PropertyValueFactory<>("hint"));

        table = new TableView<>();
        table.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        table.setPadding(new Insets(10));
        table.setItems(dbConn.selectAllItems(mobile));
        table.getColumns().addAll(accname, url, username, password, hint);

        //-------------------------------Layouts-------------------------------------------
        final Pane leftSpacer = new Pane();
        HBox.setHgrow(
                leftSpacer,
                Priority.SOMETIMES
        );

        final Pane rightSpacer = new Pane();
        HBox.setHgrow(
                rightSpacer,
                Priority.SOMETIMES
        );

        final Pane bottomSpacer = new Pane();
        VBox.setVgrow(bottomSpacer, Priority.SOMETIMES);

        HBox toolbar = new HBox(10);
        toolbar.setPadding(new Insets(5));

        btn1.setOnAction(e-> {
            String selected = listView.getSelectionModel().getSelectedItem();
            popup.addAccount(mobile, selected);
            table.setItems(getDetails(mobile, selected));
        });
        btn1.setDisable(true);
        Button btn6 = new Button("Sync");
        btn6.setGraphic(object.setImage("sync.png"));
        btn6.setOnAction(e->synchronize(mobile, footerItem, toggleButton));
        TextField search = new TextField();
        search.setPromptText("Search");

        search.textProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println("textfield changed from " + oldValue + " to " + newValue);
            table.setItems(dbConn.search(mobile, newValue));
        });

        Label usernm = new Label(user);
        usernm.setAlignment(Pos.CENTER);
        usernm.setStyle("-fx-font-size: 20px;-fx-text-fill: #738b9a;");

        Separator separator1 = new Separator();
        separator1.setOrientation(Orientation.VERTICAL);
        separator1.setId("separator1");
        separator1.getStyleClass().add("my-separator");
        Separator separator2 = new Separator();
        separator2.setOrientation(Orientation.VERTICAL);
        separator2.setId("separator2");
        separator2.getStyleClass().add("my-separator");
        Separator separator3 = new Separator();
        separator3.setOrientation(Orientation.VERTICAL);
        separator3.setId("separator3");
        separator3.getStyleClass().add("my-separator");

        toolbar.getChildren().addAll(btn1, btn7, separator1, btn2, btn3, btn4, btn5, separator2, leftSpacer, search, rightSpacer, separator3, btn6, usernm);

        VBox vBox2 = new VBox();
        vBox2.setAlignment(Pos.CENTER);
        vBox2.getChildren().addAll(toggleButton, listView);

        HBox hBox2 = new HBox(10);
        hBox2.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        hBox2.setStyle("-fx-background-color: #D8D8D8");
        hBox2.setPadding(new Insets(10));
        hBox2.setAlignment(Pos.CENTER);
        hBox2.getChildren().addAll(vBox2, table);

        //footer
        HBox footer = new HBox();
        footer.setPadding(new Insets(5,20,5,20));
        footer.setStyle("-fx-background-color: #738b9a;"+
                        "-fx-effect: dropshadow( gaussian , rgba(255,255,255,0.5) , 0,0,0,1 );");
        footerItem.setText("Total "+addFooterDetail()+" entries");
        Label developerName = new Label("Developed By - Anuraj Jain");
        developerName.setStyle("-fx-text-fill: #ffffff;");
        footer.getChildren().addAll(footerItem, rightSpacer, developerName);

        VBox vBox = new VBox();
        vBox.getChildren().addAll(menuBar, toolbar);

        //parent layout
        BorderPane mainLayout = new BorderPane();
        mainLayout.setTop(vBox);
        mainLayout.setCenter(hBox2);
        mainLayout.setBottom(footer);

        //-----------------------------------------------------------------------------------
        addEntry.setOnAction(e -> {
            String selected = listView.getSelectionModel().getSelectedItem();
            popup.addAccount(mobile, selected);
            table.setItems(getDetails(mobile, selected));
        });

        //--------------------------------Context menu & Action------------------------------
        ContextMenu cm = new ContextMenu();
        MenuItem mi1 = new MenuItem("Copy Password");
        cm.getItems().add(mi1);
        MenuItem mi5 = new MenuItem("Copy Username");
        cm.getItems().add(mi5);
        MenuItem mi6 = new MenuItem("Open URL");
        cm.getItems().add(mi6);
        cm.getItems().add(new SeparatorMenuItem());
        MenuItem mi4 = new MenuItem("Reveal Password");
        cm.getItems().add(mi4);
        MenuItem mi2 = new MenuItem("Change Password");
        cm.getItems().add(mi2);
        MenuItem mi3 = new MenuItem("Delete Entry");
        cm.getItems().add(mi3);

        table.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent t) {
                copyPswrd.setDisable(false);
                cpyUsrname.setDisable(false);
                vstUrl.setDisable(false);
                chngPass.setDisable(false);
                rmAcc.setDisable(false);
                btn2.setDisable(false);
                btn4.setDisable(false);
                btn5.setDisable(false);
                if(t.getButton() == MouseButton.SECONDARY || t.getButton() == MouseButton.PRIMARY) {
                    if(Objects.equals(table.getSelectionModel().getSelectedItem().getPassword(), "")){
                        mi4.setDisable(false);
                        mi1.setDisable(true);
                        btn3.setDisable(true);
                        rvlPass.setDisable(false);
                        copyPswrd.setDisable(true);
                    }else {
                        mi4.setDisable(true);
                        mi1.setDisable(false);
                        btn3.setDisable(false);
                        rvlPass.setDisable(true);
                        copyPswrd.setDisable(false);
                    }
                }
                if (t.getButton() == MouseButton.SECONDARY){
                    cm.show(table , t.getScreenX() , t.getScreenY());
                }
            }
        });

        mi1.setOnAction(e-> cpyPass());
        mi2.setOnAction(e-> changePass(mobile));
        mi3.setOnAction(e-> removeAcc(mobile));
        mi4.setOnAction(e-> revealPass(mobile));
        mi5.setOnAction(e-> cpyUsername());
        mi6.setOnAction(e-> visitUrl());
        //--------------------------------------------------------------------------------------------------------

        Scene scene = new Scene(mainLayout);
        scene.getStylesheets().add("aj/apps/resources/myStyle.css");
        window.setScene(scene);
        //window.setMaximized(true);
        window.centerOnScreen();
        window.show();
    }

    /**
     * Method for adding details to the footer of client area
     * @return number of total entries in the table
     */
    private static String addFooterDetail(){
        ObservableList<details> list = table.getItems();
        int counter;
        counter = list.size();
        return String.valueOf(counter);
    }

    /**
     * Method for getting user accounts/entries details to display in the table of client area
     * @param mobile mobile number with which user logged in
     * @param selected selected group
     * @return list of entries to be displayed in the table
     */
    private static ObservableList<details> getDetails(String mobile, String selected){
        ObservableList<details> item = FXCollections.observableArrayList();
        item = dbConn.selectItem(mobile, selected);
        return item;
    }

    /**
     * provides functionality for copying password of selected entry
     */
    private static void cpyPass(){
        final Clipboard clipboard = Clipboard.getSystemClipboard();
        final ClipboardContent content = new ClipboardContent();
        details detail = table.getSelectionModel().getSelectedItem();
        content.putString(detail.getPassword());
        clipboard.setContent(content);
    }

    /**
     * provides functionality for copying username of selected entry
     */
    private static void cpyUsername(){
        final Clipboard clipboard = Clipboard.getSystemClipboard();
        final ClipboardContent content = new ClipboardContent();
        details detail = table.getSelectionModel().getSelectedItem();
        content.putString(detail.getUsername());
        clipboard.setContent(content);
    }

    /**
     * provides functionality for changing password of selected entry
     * @param mobile mobile number with which user logged in
     */
    private static void changePass(String mobile){
        details detail = table.getSelectionModel().getSelectedItem();
        String pass = popup.changePass(mobile, detail.getAccname(), detail.getUsername());
        if(!Objects.equals(pass, "")){
            detail.setPassword(pass);
            table.refresh();
        }
    }

    /**
     * provides functionality for revealing password of selected entry
     * @param mobile mobile number with which user logged in
     */
    private static void revealPass(String mobile){
        if(popup.verifyOtp(mobile)){
            details selectedItem = table.getSelectionModel().getSelectedItem();
            selectedItem.setPassword(dbConn.getPassword(mobile, selectedItem.getAccname(), selectedItem.getUsername()));
            table.refresh();
        }else{
            popup.messageBox("OTP is not verified...");
        }
    }

    /**
     * provides functionality for removing selected entry/account
     * @param mobile mobile number with which user logged in
     */
    private static void removeAcc(String mobile){
        ObservableList<details> allItems;
        details selectedItem;
        allItems = table.getItems();
        selectedItem = table.getSelectionModel().getSelectedItem();

        if(popup.AlertBox("Confirm", "Are you sure you want to delete this entry")) {
            if (dbConn.removeAcc(mobile, selectedItem.getAccname(), selectedItem.getUsername())) {
                allItems.remove(selectedItem);
                table.refresh();
                popup.messageBox("Account removed");
            } else {
                popup.messageBox("Some error occurred, try again later");
            }
        }
    }

    /**
     * provides functionality for visiting URL of selected entry
     */
    private static void visitUrl(){
        details detail = table.getSelectionModel().getSelectedItem();
        if(Desktop.isDesktopSupported()) {
            try {
                Desktop.getDesktop().browse(new URI(detail.getUrl()));
            } catch (IOException e1) {
                e1.printStackTrace();
            } catch (URISyntaxException e1) {
                e1.printStackTrace();
            }
        }
    }

    /**
     * provides functionality for synchronizing account with online database
     * @param mobile mobile number with which user logged in
     * @param footerItem label representing the number of entries in the table
     * @param toggleButton toggling button for showing or hiding all entries/accounts of user in the table
     */
    private static void synchronize(String mobile, Label footerItem, ToggleButton toggleButton){
        if(popup.sync(mobile)){
            table.setItems(dbConn.selectAllItems(mobile));
            footerItem.setText("Total "+addFooterDetail()+" entries");
            toggleButton.setSelected(true);
            popup.messageBox("Synchronisation completed successfully");
        }else {
            popup.messageBox("Synchronisation failed... \n" +
                    "Possible reasons-\n" +
                    "1. No network connection\n" +
                    "2. Master Password in online database didn't matched with yours");
        }
    }
}