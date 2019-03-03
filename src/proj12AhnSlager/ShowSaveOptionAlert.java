/*
 * File: ShowSaveOptionAlert.java
 * Names: Kevin Ahn, Lucas DeGraw, Jackie Hang, Kyle Slager
 * Class: CS 361
 * Project 7
 * Date: November 2, 2018
*/


package proj12AhnSlager;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;

import java.util.Optional;

public class ShowSaveOptionAlert {

    private Alert alert;
    private ButtonType yesButton;
    private ButtonType noButton;
    private ButtonType cancelButton;

    /**
     * This class is used to create an alert that asks the user
     * whether or not they want to save the file that is open
     *
     * @author  Kevin Ahn, Lucas Degraw, Jackie Hang, Kyle Slager
     * @version 1.0
     * @since   11-2-2018
     *
     */
    public ShowSaveOptionAlert() {

        // create the alert
        this.alert = new Alert(Alert.AlertType.CONFIRMATION);
        this.alert.setTitle("Save Changes?");
        this.alert.setHeaderText("Do you want to save your changes?");
        this.alert.setContentText("Your changes will be lost if you don't save them.");

        // the option buttons for the user to click
        this.yesButton = new ButtonType("Yes");
        this.noButton = new ButtonType("No");
        this.cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        this.alert.getButtonTypes().setAll(yesButton, noButton, cancelButton);
    }

    /**
     * Gets the user's decision
     *
     * @return Optional<ButtonType> the button that the user chose
     */
    public Optional<ButtonType> getUserSaveDecision() {

        return this.alert.showAndWait();
    }

    /**
     * Returns the Yes ButtonType
     *
     * @return a ButtonType
     */
    public ButtonType getYesButton() {
        return this.yesButton;
    }

    /**
     * Returns the no ButtonType
     *
     * @return a ButtonType
     */
    public ButtonType getNoButton() {
        return this.noButton;
    }

    /**
     * Returns the Cancel ButtonType
     * @return a ButtonType
     */
    public ButtonType getCancelButton() {
        return this.cancelButton;
    }
}