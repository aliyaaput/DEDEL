import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class DedelSystemGUI {
    private JFrame frame;
    private JTextField namaField;
    private JTextField usiaField;
    private JTextField tinggiField;
    private JTextField beratField;
    private JTextArea hasilArea;
    private JButton penilaianButton;
    private JButton simpanButton;
    private JTextField cariNamaField;
    private JButton cariButton;
    private JRadioButton lakiLakiRadioButton;
    private JRadioButton perempuanRadioButton;

    public DedelSystemGUI() {
        frame = new JFrame("DEDEL - Deteksi Dini Elektronik");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 600);
        frame.setLayout(new BorderLayout());

        // Inisialisasi field input
        namaField = new JTextField(30);
        usiaField = new JTextField(30);
        tinggiField = new JTextField(30);
        beratField = new JTextField(30);
        cariNamaField = new JTextField(30);

        // Area untuk menampilkan hasil dengan border tebal
        hasilArea = new JTextArea(10, 30);
        hasilArea.setEditable(false);
        hasilArea.setBorder(new CompoundBorder(new LineBorder(Color.BLACK, 2), new EmptyBorder(10, 10, 10, 10)));
        hasilArea.setPreferredSize(new Dimension(500, 300));

        // Tombol hasil penilaian dan tombol simpan data
        penilaianButton = new JButton("Hasil Penilaian Stunting");
        simpanButton = new JButton("Simpan Data");
        cariButton = new JButton("Cari Data");

        // Panel untuk form input
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 5, 5));
        formPanel.add(new JLabel("Nama Anak:"));
        formPanel.add(namaField);
        formPanel.add(new JLabel("Usia (tahun):"));
        formPanel.add(usiaField);
        formPanel.add(new JLabel("Tinggi (cm):"));
        formPanel.add(tinggiField);
        formPanel.add(new JLabel("Berat (kg):"));
        formPanel.add(beratField);

        // Radio button untuk jenis kelamin
        lakiLakiRadioButton = new JRadioButton("Laki-laki");
        perempuanRadioButton = new JRadioButton("Perempuan");
        ButtonGroup genderGroup = new ButtonGroup();
        genderGroup.add(lakiLakiRadioButton);
        genderGroup.add(perempuanRadioButton);

        formPanel.add(new JLabel("Jenis Kelamin:"));
        formPanel.add(lakiLakiRadioButton);
        formPanel.add(new JLabel(""));
        formPanel.add(perempuanRadioButton);

        formPanel.add(new JLabel("Cari Nama:"));
        formPanel.add(cariNamaField);
        formPanel.add(penilaianButton);
        formPanel.add(simpanButton);
        formPanel.add(cariButton);

        // Menambahkan formPanel di bagian atas
        frame.add(formPanel, BorderLayout.NORTH);

        // Menempatkan area hasil di bagian tengah
        frame.add(new JScrollPane(hasilArea), BorderLayout.CENTER);

        // ActionListener untuk tombol hasil penilaian
        penilaianButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String nama = namaField.getText();
                    int usia = Integer.parseInt(usiaField.getText());
                    double tinggi = Double.parseDouble(tinggiField.getText());
                    double berat = Double.parseDouble(beratField.getText());
        
                    // Ambil nilai jenis kelamin dari tombol radio
                    String jenisKelamin = "";
                    if (lakiLakiRadioButton.isSelected()) {
                        jenisKelamin = "Laki-laki";
                    } else if (perempuanRadioButton.isSelected()) {
                        jenisKelamin = "Perempuan";
                    }
        
                    // Validasi usia (0-5 tahun)
                    if (usia < 0 || usia > 5) {
                        hasilArea.setText("Usia harus antara 0-5 tahun.");
                        return;
                    }
        
                    // Evaluasi dan tampilkan hasil
                    String penilaian = evaluasiRisikoStunting(usia, tinggi, berat, jenisKelamin);
                    String saran = saranPencegahanStunting(usia, tinggi, berat, jenisKelamin);
        
                    hasilArea.setText("Nama: " + nama + "\n" +
                                      "Usia: " + usia + " tahun\n" +
                                      "Tinggi: " + tinggi + " cm\n" +
                                      "Berat: " + berat + " kg\n" +
                                      "Jenis Kelamin: " + jenisKelamin + "\n\n" +
                                      "Hasil Penilaian:\n" + penilaian + "\n\n" +
                                      "Saran:\n" + saran);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(frame, "Input tidak valid. Mohon masukkan angka yang benar.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        

        // ActionListener untuk tombol simpan data
        simpanButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String nama = namaField.getText();
                    int usia = Integer.parseInt(usiaField.getText());
                    double tinggi = Double.parseDouble(tinggiField.getText());
                    double berat = Double.parseDouble(beratField.getText());

                    String jenisKelamin = "";
                    if (lakiLakiRadioButton.isSelected()) {
                        jenisKelamin = "Laki-laki";
                    } else if (perempuanRadioButton.isSelected()) {
                        jenisKelamin = "Perempuan";
                    }

                    simpanDataKeDatabase(nama, usia, tinggi, berat, jenisKelamin);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(frame, "Input tidak valid. Mohon masukkan angka yang benar.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // ActionListener untuk tombol cari data berdasarkan nama
        cariButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String namaCari = cariNamaField.getText();
                tampilkanData(namaCari);
            }
        });

        frame.setVisible(true);
    }

    private String evaluasiRisikoStunting(int usia, double tinggi, double berat, String jenisKelamin) {
        // Standar tinggi dan berat berdasarkan jenis kelamin dan usia
        double minTinggi = 0, maxTinggi = 0, minBerat = 0, maxBerat = 0;

        if (jenisKelamin.equals("Laki-laki")) {
            switch (usia) {
                case 1: minTinggi = 72; maxTinggi = 78; minBerat = 7.7; maxBerat = 12; break;
                case 2: minTinggi = 82; maxTinggi = 92; minBerat = 9.7; maxBerat = 15.3; break;
                case 3: minTinggi = 94; maxTinggi = 100; minBerat = 11.3; maxBerat = 18.3; break;
                case 4: minTinggi = 100; maxTinggi = 108; minBerat = 12.7; maxBerat = 21.2; break;
                case 5: minTinggi = 108; maxTinggi = 114; minBerat = 14.3; maxBerat = 24.2; break;
            }
        } else if (jenisKelamin.equals("Perempuan")) {
            switch (usia) {
                case 1: minTinggi = 70; maxTinggi = 78; minBerat = 7; maxBerat = 11.5; break;
                case 2: minTinggi = 80; maxTinggi = 92; minBerat = 9; maxBerat = 14.8; break;
                case 3: minTinggi = 92; maxTinggi = 100; minBerat = 10.8; maxBerat = 18.1; break;
                case 4: minTinggi = 100; maxTinggi = 105; minBerat = 12.3; maxBerat = 21.5; break;
                case 5: minTinggi = 106; maxTinggi = 116; minBerat = 13.7; maxBerat = 24.9; break;
            }
        }

        // Evaluasi tinggi dan berat anak
        String evaluasiTinggi = (tinggi < minTinggi) ? "Tinggi di bawah standar (Berisiko Stunting)"
                : (tinggi > maxTinggi) ? "Tinggi di atas standar" : "Tinggi normal";
        String evaluasiBerat = (berat < minBerat) ? "Berat di bawah standar (Risiko gizi kurang)"
                : (berat > maxBerat) ? "Berat di atas standar (Risiko obesitas)" : "Berat normal";

        return evaluasiTinggi + "\n" + evaluasiBerat;
    }

    // Metode untuk memberikan saran pencegahan stunting
    private String saranPencegahanStunting(int usia, double tinggi, double berat, String jenisKelamin) {
        // Tentukan saran berdasarkan status stunting dan berat badan
        String evaluasi = evaluasiRisikoStunting(usia, tinggi, berat, jenisKelamin);
        
        // Cek apakah berat badan anak di bawah standar
        boolean beratKurang = evaluasi.contains("Risiko gizi kurang");

        // Jika anak berisiko stunting
        if (evaluasi.contains("Berisiko Stunting")) {
            if (beratKurang) {
                return "1. Berikan makanan bergizi seimbang, seperti bubur dan sayuran yang dihaluskan.\n" +
                       "2. Pastikan asupan protein hewani seperti telur, daging, dan susu untuk mendukung pertumbuhan.\n" +
                       "3. Tingkatkan konsumsi makanan kaya zat besi dan vitamin A seperti sayuran hijau dan buah-buahan.\n" +
                       "4. Konsultasikan ke dokter jika tinggi dan berat tidak sesuai standar.";
            } else {
                return "1. Berikan makanan bergizi seimbang dengan asupan kalori dan protein yang cukup.\n" +
                       "2. Konsumsi makanan bergizi dari berbagai kelompok makanan.\n" +
                       "3. Lakukan pemeriksaan kesehatan rutin untuk memantau pertumbuhan.";
            }
        } else { // Jika anak tidak berisiko stunting
            if (beratKurang) {
                return "1. Pastikan anak mendapatkan cukup kalori dan protein.\n" +
                       "2. Berikan makanan yang kaya zat besi dan vitamin untuk mendukung pertumbuhan yang sehat.\n" +
                       "3. Rutin melakukan pemeriksaan kesehatan untuk memastikan perkembangan yang optimal.";
            } else {
                return "1. Tetap pastikan anak mendapatkan asupan gizi yang seimbang.\n" +
                       "2. Lanjutkan dengan pemeriksaan kesehatan rutin setiap 6 bulan.\n" +
                       "3. Ciptakan lingkungan yang mendukung pertumbuhan dengan cukup tidur dan aktivitas fisik.";
            }
        }
    }
    
    

    private void simpanDataKeDatabase(String nama, int usia, double tinggi, double berat, String jenisKelamin) {
        String url = "jdbc:mysql://localhost:3306/dedel_system";
        String user = "root";

        try {
            Connection conn = DriverManager.getConnection(url, user, "");
            String checkQuery = "SELECT * FROM data_anak WHERE nama = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
            checkStmt.setString(1, nama);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                String updateQuery = "UPDATE data_anak SET usia = ?, tinggi = ?, berat = ?, jenis_kelamin = ? WHERE nama = ?";
                PreparedStatement updateStmt = conn.prepareStatement(updateQuery);
                updateStmt.setInt(1, usia);
                updateStmt.setDouble(2, tinggi);
                updateStmt.setDouble(3, berat);
                updateStmt.setString(4, jenisKelamin);
                updateStmt.setString(5, nama);
                updateStmt.executeUpdate();
                updateStmt.close();
            } else {
                String insertQuery = "INSERT INTO data_anak (nama, usia, tinggi, berat, jenis_kelamin) VALUES (?, ?, ?, ?, ?)";
                PreparedStatement insertStmt = conn.prepareStatement(insertQuery);
                insertStmt.setString(1, nama);
                insertStmt.setInt(2, usia);
                insertStmt.setDouble(3, tinggi);
                insertStmt.setDouble(4, berat);
                insertStmt.setString(5, jenisKelamin);
                insertStmt.executeUpdate();
                insertStmt.close();
            }

            rs.close();
            checkStmt.close();
            conn.close();

            JOptionPane.showMessageDialog(frame, "Data berhasil disimpan!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(frame, "Gagal menyimpan data: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void tampilkanData(String nama) {
        String url = "jdbc:mysql://localhost:3306/dedel_system";
        String user = "root";

        try {
            Connection conn = DriverManager.getConnection(url, user, "");
            String query = "SELECT * FROM data_anak WHERE nama = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, nama);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int usia = rs.getInt("usia");
                double tinggi = rs.getDouble("tinggi");
                double berat = rs.getDouble("berat"); // Ambil berat dari database
                String jenisKelamin = rs.getString("jenis_kelamin"); // Ambil jenis kelamin dari database
            
                // Gunakan semua parameter dalam metode evaluasiRisikoStunting
                String hasil = "Nama: " + rs.getString("nama") + "\n" +
                               "Usia: " + usia + " tahun\n" +
                               "Tinggi: " + tinggi + " cm\n" +
                               "Berat: " + berat + " kg\n" +
                               "Jenis Kelamin: " + jenisKelamin + "\n\n" +
                               "Deskripsi Perhitungan: Berdasarkan usia anak, tinggi standar yang dianjurkan adalah " +
                               (75 + (usia - 1) * 6) + " cm.\n\n" +
                               "Penilaian: " + evaluasiRisikoStunting(usia, tinggi, berat, jenisKelamin) + "\n" +
                               "Saran: " + saranPencegahanStunting(usia, tinggi, berat, jenisKelamin);
                
                hasilArea.setText(hasil);
            } else {
                hasilArea.setText("Data tidak ditemukan untuk nama: " + nama);
            }
            
            // Tutup resource
            rs.close();
            stmt.close();
            conn.close();
            
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(frame, "Gagal mengambil data: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        new DedelSystemGUI();
    }
} 