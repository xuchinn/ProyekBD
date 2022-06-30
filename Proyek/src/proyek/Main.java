/**
 * Main class untuk start app
 *
 * Nama: Xuchin Valezka
 * NRP: C14200007
 * Kelas: C
 * Kelompok: 1
 */

package proyek;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import java.sql.*;

public class Main extends Application {

    public static Connection conn; // menyimpan connection agar bisa dipanggil di class lain
    public static int id_user; // menyimpan id user yang login agar bisa dipanggil di class lain
    public static String nama; // menyimpan nama user yang login agar bisa dipanggil di class lain

    @Override
    public void start(Stage primaryStage) throws Exception{
        // setup stage / scene
        Parent root = FXMLLoader.load(getClass().getResource("login.fxml"));
        primaryStage.setTitle("NiHaoDoc"); // title app
        primaryStage.getIcons().add(new Image("\\proyek\\stethoscope.png")); // icon app
        // ukuran window
        primaryStage.setScene(new Scene(root, 700, 500));
        primaryStage.setMinWidth(700);
        primaryStage.setMinHeight(500);
        primaryStage.show();
    }


    public static void main(String[] args) {
        // cek apakah driver (library) sudah ada atau belum
        int cont = 0;
        try {
            Class.forName("org.postgresql.Driver");
            cont = 1;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        // jika sudah ada bru dikonekkan ke db
        if (cont == 1) {
            String url = "jdbc:postgresql://localhost/proyek_app?user=postgres&password=12345678&ssl=false";
            try {
               conn = DriverManager.getConnection(url);
               // jika berhasil konek bru akan menampilkan app
                launch(args);
            } catch (SQLException sqle) {
                // jika terjadi error saat konek akan muncul alert
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Connect Gagal");
                alert.setHeaderText("Tidak dapat konek ke DB.");
                alert.setContentText(sqle.toString());
                alert.show();
            }
        }
    }
}
