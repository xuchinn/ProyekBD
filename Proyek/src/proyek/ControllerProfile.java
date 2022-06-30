/**
 * Controller untuk form Profile yang berisi data pasien suatu user
 * User bisa menambahkan, mengedit, atau menghapus data pasien
 *
 * Nama: Xuchin Valezka
 * NRP: C14200007
 * Kelas: C
 * Kelompok: 1
 */

package proyek;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.sql.*;

import static javafx.fxml.FXMLLoader.load;

public class ControllerProfile {
    private Stage primaryStage;
    private Scene scene;
    private Parent root;
    private ObservableList<ObservableList> oblist; // menyimpan data yang akan dimasukkan ke tabel
    private int indexClicked; // menyimpan index row yang di click

    @FXML
    Label nameLabel; // label untuk menampilkan nama user

    @FXML
    TableView tableView; // tableview untuk menampilkan daftar pasien user

    @FXML
    TextField tfNIK; // textfield untuk NIK pasien
    @FXML
    TextField tfNama; // textfield untuk nama pasien
    @FXML
    TextField tfTanggal; // textfield untuk tanggal lahir pasien
    @FXML
    TextField tfAlamat; // textfield untuk alamat pasien
    @FXML
    TextField tfGender; // textfield untuk jenis kelamin pasien
    @FXML
    TextField tfHubungan; // textfield untuk hubungan pasien dan user


    /**
     * Function initialize untuk form profile user
     */
    @FXML
    public void initialize() {
        // nama di set sesuai nama yang sudah di retrieve di form ControllerLogin saat awal login
        nameLabel.setText(Main.nama);
        oblist = FXCollections.observableArrayList();
        viewTable(); // menampilkan isi tabel daftar pasien
    }

    /**
     * Function untuk meretrieve daftar pasien
     * dan menampilkan pada tableView
     */
    public void viewTable() {
        Connection conn = Main.conn;
        PreparedStatement pstmt = null;
        try {
            /*
            Retrieve data-data pasien
            Status diberi 1 karena hanya akan menampilkan daftar pasien
            yang masih aktif
             */
            pstmt = conn.prepareStatement("SELECT nik_pasien, nama, tanggal_lahir, alamat, jenis_kelamin, hubungan FROM pasien WHERE id_user = ? AND status = 1");
            pstmt.setInt(1, Main.id_user);
            ResultSet rs = pstmt.executeQuery();

            // menambahkan kolom
            for(int i = 0; i < rs.getMetaData().getColumnCount(); i++){
                final int j = i;
                TableColumn col = new TableColumn(rs.getMetaData().getColumnName(i+1));
                col.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList,String>, ObservableValue<String>>(){
                    public ObservableValue<String> call(TableColumn.CellDataFeatures<ObservableList, String> param) {
                        return new SimpleStringProperty(param.getValue().get(j).toString());
                    }
                });

                tableView.getColumns().addAll(col);
            }

            // menambahkan row / data per pasien ke oblist
            while(rs.next()){
                ObservableList<String> row = FXCollections.observableArrayList();
                for(int i = 1; i <= rs.getMetaData().getColumnCount(); i++){
                    row.add(rs.getString(i));
                }
                oblist.add(row);
            }

            // tabel diisi dengan data yang sudah dimasukkan ke oblist
            tableView.setItems(oblist);

            // jika di doubleclick datanya maka data akan muncul di textfield
            tableView.setOnMouseClicked((MouseEvent event) -> {
                if (event.getClickCount() > 1) {
                    // menyimpan index row yang di click
                    indexClicked = tableView.getSelectionModel().getSelectedIndex();
//                    System.out.println(indexClicked);
                    // jika data ada bru akan di click dan data muncul di textfield
                    if (indexClicked >= 0) {
                        tfNIK.setText(String.valueOf(oblist.get(indexClicked).get(0)));
                        tfNama.setText(String.valueOf(oblist.get(indexClicked).get(1)));
                        tfTanggal.setText(String.valueOf(oblist.get(indexClicked).get(2)));
                        tfAlamat.setText(String.valueOf(oblist.get(indexClicked).get(3)));
                        tfGender.setText(String.valueOf(oblist.get(indexClicked).get(4)));
                        tfHubungan.setText(String.valueOf(oblist.get(indexClicked).get(5)));
                    }
                }
            });


        } catch (SQLException sqle) {
            // jika terjadi error dalam retrieve maka akan muncul alert
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Retrieve Gagal");
            alert.setHeaderText("Data tidak dapat di-retrieve.");
            alert.setContentText(sqle.toString());
            alert.show();
        }
    }

    /**
     * Function untuk sunting data pasien
     * Terhubung dengan tombol 'Sunting Pasien'
     */
    @FXML
    public void editData() {
        Connection conn = Main.conn;
        try {
            //query untuk update data pasien di DB
            PreparedStatement pstmt = conn.prepareStatement("UPDATE pasien SET nik_pasien = ?, nama = UPPER(?), tanggal_lahir = ?, alamat = UPPER(?), jenis_kelamin = UPPER(?), hubungan = UPPER(?) WHERE id_user = ? AND nik_pasien = ?");
            // data sesuai textfield
            pstmt.setString(1, tfNIK.getText());
            pstmt.setString(2, tfNama.getText());
            pstmt.setDate(3, Date.valueOf(tfTanggal.getText()));
            pstmt.setString(4, tfAlamat.getText());
            pstmt.setString(5, tfGender.getText());
            pstmt.setString(6, tfHubungan.getText());
            pstmt.setInt(7, Main.id_user);
            pstmt.setString(8, String.valueOf(oblist.get(indexClicked).get(0)));
            pstmt.execute();

            // update list dan tableview
            oblist.get(indexClicked).set(0, tfNIK.getText());
            oblist.get(indexClicked).set(1, tfNama.getText());
            oblist.get(indexClicked).set(2, tfTanggal.getText());
            oblist.get(indexClicked).set(3, tfAlamat.getText());
            oblist.get(indexClicked).set(4, tfGender.getText());
            oblist.get(indexClicked).set(5, tfHubungan.getText());
            tableView.refresh();
        } catch (SQLException sqle) {
            // jika terjadi error dalam update maka akan muncul alert
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Update Gagal");
            alert.setHeaderText("Data tidak dapat di-update.");
            alert.setContentText(sqle.toString());
            alert.show();
        }
    }

    /**
     * Function untuk menambahkan data pasien
     * terhubung dengan tombol 'Tambah Pasien'
     */
    @FXML
    public void tambahData() {
        Connection conn = Main.conn;
        try {
            // query untuk insert data pasien ke DB
            PreparedStatement pstmt = conn.prepareStatement("INSERT INTO pasien (nik_pasien, nama, tanggal_lahir, alamat, jenis_kelamin, hubungan, foto_ktp, id_user)" +
                    " VALUES (?, UPPER(?), ?, UPPER(?), ?, ?, CONCAT('/foto_ktp/', ?, '.jpg'), ?)");
            pstmt.setString(1, tfNIK.getText());
            pstmt.setString(2, tfNama.getText());
            pstmt.setDate(3, Date.valueOf(tfTanggal.getText()));
            pstmt.setString(4, tfAlamat.getText());
            pstmt.setString(5, tfGender.getText());
            pstmt.setString(6, tfHubungan.getText());
            pstmt.setString(7, tfNIK.getText());
            pstmt.setInt(8, Main.id_user);
            pstmt.execute();

            // add oblist, agar data yang ditambahkan muncul di tabel
            ObservableList<String> row = FXCollections.observableArrayList();
            row.add(tfNIK.getText());
            row.add(tfNama.getText());
            row.add(tfTanggal.getText());
            row.add(tfAlamat.getText());
            row.add(tfGender.getText());
            row.add(tfHubungan.getText());
            oblist.add(row);

        } catch (SQLException sqle) {
            // jika terjadi error dalam insert data maka akan muncul alert
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Insert Failed");
            alert.setHeaderText("Data gagal di tambahkan.");
            alert.setContentText(sqle.toString());
            alert.show();
        }
    }

    /**
     * Function untuk menghapus data pasien
     * Terhubung dengan tombol 'Hapus Pasien'
     */
    @FXML
    public void hapusData() {
        Connection conn = Main.conn;
        try {
            // cek apakah pasien sudah pernah booking
            PreparedStatement pstmt = conn.prepareStatement("SELECT 1 FROM appointment WHERE id_pasien = (SELECT id_pasien FROM pasien WHERE nik_pasien = ?)");
            pstmt.setString(1, tfNIK.getText());
            ResultSet rs = pstmt.executeQuery();
            // apabila sudah maka data hanya akan diupdate statusnya menjadi non-aktif (-1)
            if (rs.next()) {
                rs.close();
                pstmt = conn.prepareStatement("UPDATE pasien SET status = -1 WHERE nik_pasien = ? AND id_user = ?");
                pstmt.setString(1, tfNIK.getText());
                pstmt.setInt(2, Main.id_user);
                pstmt.execute();
            }
            // jika belum pernah maka data pasien dihapus dari DB
            else {
                rs.close();
                pstmt = conn.prepareStatement("DELETE FROM pasien WHERE nik_pasien = ? AND id_user = ?");
                pstmt.setString(1, tfNIK.getText());
                pstmt.setInt(2, Main.id_user);
                pstmt.execute();
            }
            // data juga dihapus dari oblist agar tidak tampil di tableview
            oblist.remove(indexClicked);

        } catch (SQLException sqle) {
            // jika terjadi error dalam delete maka akan muncul alert
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Delete Failed");
            alert.setHeaderText("Data gagal di hapus.");
            alert.setContentText(sqle.toString());
            alert.show();
        }
    }

    /**
     * Function untuk logout
     * Terhubung dengan tombol 'Keluar'
     * @param event event OnAction tombol
     * @throws IOException
     */
    @FXML
    public void logOut(ActionEvent event) throws IOException {
        // di redirect ke page login
        root = load(getClass().getResource("login.fxml"));
        primaryStage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root, 700, 500);
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(700);
        primaryStage.setMinHeight(500);
        primaryStage.show();
    }

    /**
     * Function untuk redirect ke form dokter terlaris
     * Terhubung dengan tombol 'Dokter Terlaris'
     * @param event event onAction tombol
     * @throws IOException
     */
    @FXML
    public void dokterTerlaris(ActionEvent event) throws IOException {
        root = load(getClass().getResource("dokterTerlaris.fxml"));
        primaryStage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root, 700, 500);
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(700);
        primaryStage.setMinHeight(500);
        primaryStage.show();
    }
}
