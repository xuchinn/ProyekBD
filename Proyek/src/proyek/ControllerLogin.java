/**
 * Controller untuk form Login user
 *
 * Nama: Xuchin Valezka
 * NRP: C14200007
 * Kelas: C
 * Kelompok: 1
 */


package proyek;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static javafx.fxml.FXMLLoader.load;

public class ControllerLogin {
    private Stage primaryStage;
    private Scene scene;
    private Parent root;

    @FXML
    TextField emailField; // textfield untuk memasukkan email user
    @FXML
    PasswordField passwordField; // field untuk memasukkan password user

    /**
     * Function untuk login
     * Terhubung dengan tombol 'Masuk'
     * @param event onAction Tombol
     */
    @FXML
    public void login(ActionEvent event) {
        Connection conn = Main.conn;
        try {
            // query untuk cek apabila user ada di DB
            PreparedStatement pstmt = conn.prepareStatement("SELECT id_user, nama FROM users WHERE email = ? AND password = encode(sha256(decode(?, 'escape')), 'base64')");
            pstmt.setString(1, emailField.getText());
            pstmt.setString(2, passwordField.getText());
            ResultSet rs = pstmt.executeQuery();
            // jika ada maka login benar, dan akan di redirect ke form profile
            if (rs.next()) {
                System.out.println("LOGIN BENAR");
                // menyimpan id user yang login
                Main.id_user = rs.getInt(1);
                // menyimpan nama user yang login
                Main.nama = rs.getString("nama");
                loggedIn(event);
            }
            // jika salah akan mengeluarkan alert
            else {
                System.out.println("LOGIN SALAH");
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Login Error");
                alert.setHeaderText("Email atau Password Salah.");
                alert.setContentText("Harap input kembali.");
                alert.show();
            }
        } catch (SQLException | IOException sqle) {
            // jika terjadi error dalam retrieve maka akan muncul alert
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Retrieve Gagal");
            alert.setHeaderText("Data tidak dapat di-retrieve.");
            alert.setContentText(sqle.toString());
            alert.show();
        }
    }

    /**
     * Function untuk redirect ke form profile
     * @param event onAction button
     * @throws IOException
     */
    @FXML
    public void loggedIn(ActionEvent event) throws IOException {
        root = load(getClass().getResource("profile.fxml"));
        primaryStage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root, 700, 500);
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(700);
        primaryStage.setMinHeight(500);
        primaryStage.show();
    }
}
