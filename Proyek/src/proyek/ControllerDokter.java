/**
 * Controller untuk form menampilkan daftar dokter terlaris pada
 * suatu klinik dalam periode tertentu.
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
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.sql.*;

import static javafx.fxml.FXMLLoader.load;

public class ControllerDokter {
    private Stage primaryStage;
    private Scene scene;
    private Parent root;

    private ObservableList oblistTP; // untuk menyimpan tempat praktek di combobox

    @FXML
    ComboBox tempat_praktek; // combobox berisi nama-nama tempat praktek

    @FXML
    DatePicker tanggal_awal; // datepicker tanggal periode awal
    @FXML
    DatePicker tanggal_akhir; // datepicker tanggal periode akhir

    @FXML
    TableView tableView; // tableView daftar dokter terlaris

    /**
     * Function initialize untuk form tampilkan nama dokter
     */
    @FXML
    public void initialize() {
        Connection conn = Main.conn;
        oblistTP = FXCollections.observableArrayList();
        try {
            // retrieve daftar-daftar nama tempat praktek
            PreparedStatement pstmt = conn.prepareStatement("SELECT id_tempat_praktek, nama FROM tempat_praktek");
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                oblistTP.add(rs.getString("nama"));
            }
            // nama tempat praktek ditambahkan ke combobox
            tempat_praktek.setItems(oblistTP);
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
     * Function on action tombol kembali ke profil (tempat data pasien user)
     * Bertujuan untuk switch scene / redirect user ke form lain
     * @param event event onAction
     * @throws IOException
     */
    @FXML
    public void backToForm(ActionEvent event) throws IOException {
        root = load(getClass().getResource("profile.fxml"));
        primaryStage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root, 700, 500);
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(700);
        primaryStage.setMinHeight(500);
        primaryStage.show();
    }

    /**
     * Untuk menampilkan daftar dokter terlaris (konek dengan button 'lihat')
     */
    @FXML
    public void viewDokter() {
        Connection conn = Main.conn;
        ObservableList oblist = FXCollections.observableArrayList();
        try {
            /*
            Query untuk retrieve daftar dokter terlaris
            Filter berdasarkan nama tempat praktek, periode awal dan periode akhir
            Akan menampilkan daftar dokter yang paling banyak di book di tempat praktek x
            di antara tanggal awal dan tanggal akhir
             */
            PreparedStatement pstmt = conn.prepareStatement("SELECT d.nama nama_dokter, s.nama sp, COUNT(*) jml_appointment " +
                    "FROM appointment ap JOIN dokter d ON ap.id_dokter = d.id_dokter " +
                    "JOIN spesialisasi s ON d.id_spesialisasi = s.id_spesialisasi " +
                    "JOIN jadwal j ON ap.id_jadwal = j.id_jadwal " +
                    "JOIN tempat_praktek tp ON j.id_jadwal = tp.id_jadwal " +
                    "WHERE tp.nama = ? AND (tanggal_appointment >= ? AND tanggal_appointment <= ?) " +
                    "GROUP BY nama_dokter, sp " +
                    "ORDER BY jml_appointment DESC");
            /*
            Nama tempat praktek, tanggal awal, dan tanggal akhir
            akan diambil dari value comboBox dan Datepicker yang dipilih oleh user
             */
            pstmt.setString(1, String.valueOf(tempat_praktek.getValue()));
            pstmt.setDate(2, Date.valueOf(tanggal_awal.getValue()));
            pstmt.setDate(3, Date.valueOf(tanggal_akhir.getValue()));
            ResultSet rs = pstmt.executeQuery();

            // clear col tabel sebelum ditambahkan kolom baru untuk retrieve data yang lain
            tableView.getColumns().clear();
            // menambahkan kolom ke dalam tableView
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
            // menambahkan row ke dalam tabel (1 row 1 data)
            while(rs.next()){
                ObservableList<String> row = FXCollections.observableArrayList();
                for(int i = 1; i <= rs.getMetaData().getColumnCount(); i++){
                    row.add(rs.getString(i));
                }
                oblist.add(row);
            }

            /*
             menambahkan data yang sudah di retrieve dan dimasukkan
             ke observable list ke dalam tableView
             */
            tableView.setItems(oblist);

        } catch (SQLException sqle) {
            // jika terjadi error dalam retrieve maka akan muncul alert
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Retrieve Gagal");
            alert.setHeaderText("Data tidak dapat di-retrieve.");
            alert.setContentText(sqle.toString());
            alert.show();
        }
    }
}
