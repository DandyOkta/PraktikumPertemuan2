/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package frame;

import db.Koneksi;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.util.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import model.Anggota;
import model.KeyValue;
import model.Petugas;

/**
 *
 * @author acer
 */
public class AnggotaTambahFrame extends javax.swing.JFrame {
    
    BufferedImage bImage;
    int status;
    Statement st;
    ResultSet rs;
    PreparedStatement ps;
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    String qryPetugas = "SELECT * FROM petugas ORDER BY nama_petugas";
    
    private final int SEDANG_TAMBAH = 101;
    private final int SEDANG_UBAH = 102;
    private final int IMG_WIDTH= 183;
    private final int IMG_HEIGHT= 224;
    /**
     * Creates new form AnggotaTambahFrame
     */
    public AnggotaTambahFrame() {
        initComponents();
        setLocationRelativeTo(null);
        
        eId.setText(makeId());
        eId.setEnabled(false);
        eNamaAnggota.requestFocus();
        cbSetModel(qryPetugas, "id", "nama_petugas", cbPetugas);
        status=SEDANG_TAMBAH;
    }
    
    public AnggotaTambahFrame(Anggota anggota) {
        initComponents();
        setLocationRelativeTo(null);
        
        eId.setText(anggota.getId());
        eId.setEnabled(false);
        eNamaAnggota.requestFocus();
        eNamaAnggota.setText(anggota.getNamaAnggota());
        rbJenisKelaminSetSelected(anggota.getJenisKelamin());
        jXDatePicker1.setDate(getFormattedDate(anggota.getTanggalLahir()));
        cbAgama.setSelectedItem(anggota.getAgama());
        cbSetModel(qryPetugas, "id", "nama_petugas", cbPetugas);
        cbSetSelected(anggota.getPetugas().getNamaPetugas(),cbPetugas);
        bImage = getBufferedImage(anggota.getFotoAnggota());
        lbGambar.setIcon(new ImageIcon(bImage));
        status = SEDANG_UBAH;
    }
    
    public void rbJenisKelaminSetSelected(String jenisKelamin){
        if(jenisKelamin.equals("Laki-laki"))
            rbLaki.setSelected(true);
        else
            rbPerempuan.setSelected(true);
    }
    
    public String rbJenisKelaminGetSelected(){
        if(rbLaki.isSelected())
            return "Laki-laki";
        else if(rbPerempuan.isSelected())
            return "Perempuan";
        else 
            return "";
    }
    
    public Vector getCbData(String qry, String key, String value){
        Vector v = new Vector();
        try {
            Koneksi koneksi = new Koneksi();
            Connection connection = koneksi.getConnection();
            
            st = connection.createStatement();
            rs = st.executeQuery(qry);
            while(rs.next()){
                v.addElement(new KeyValue(rs.getInt(key),
                                          rs.getString(value)));
            }
        } catch (SQLException ex) {
            System.err.println("Error getData() : "+ex);
        }
        return v;
    }
    
    public void cbSetModel(String qry, String key, String value,  JComboBox<String> jcb){
        Vector v = getCbData(qry, key, value);
        DefaultComboBoxModel model;
        model = new DefaultComboBoxModel(v);
        jcb.setModel(model);
    }
    
    public void cbSetSelected(String data, JComboBox<String> cb){
        KeyValue item = new KeyValue();
        for (int i = 0; 1 < cb.getItemCount(); i++)
        {
            cb.setSelectedIndex(i);
            item.setValue(((KeyValue)cb.getSelectedItem()).getValue());
            if (item.getValue().equalsIgnoreCase(data))
            {
                cb.setSelectedIndex(i);
                break;
            }
        }
    }
    
    public String makeId(){
        String id, idDate, idSem = null;
        Date now = new Date(System.currentTimeMillis());
        SimpleDateFormat df = new SimpleDateFormat("yyyyMM");
        idDate = df.format(now);
        id = idDate+"001";
        try {
            Koneksi koneksi = new Koneksi();
            Connection connection = koneksi.getConnection();
            
            String query = "SELECT id FROM anggota where id LIKE ? "
                         + "ORDER BY id DESC";
            ps = connection.prepareStatement(query);
            ps.setString(1, idDate+"%");
            rs = ps.executeQuery();
            
            while(rs.next()){
                idSem = rs.getString(1);
                break;
            }
        } catch (SQLException ex) {
            System.err.println("Error makeId() : "+ex);
        }
        
        if (idSem!=null){
            int angka = Integer.parseInt(idSem.substring(6, 9));
            angka++;
            id=idDate+String.format("%03d", angka);
        }
        return id;
    }
    
    public Date getFormattedDate(String tanggal) {
        try {
            Date tanggalLahir = dateFormat.parse(tanggal);
            return tanggalLahir;
        } catch (ParseException ex) {
            System.err.println("Error Tanggal :"+ex);
            return new Date();
        }
    }
    
    public BufferedImage getBufferedImage(Blob imageBlob){
        InputStream binaryStream = null;
        BufferedImage b = null;
        try {
            binaryStream = imageBlob.getBinaryStream();
            b = ImageIO.read(binaryStream);
        } catch (SQLException | IOException ex) {
            System.err.println("Error getBufferedImage : "+ex);
        }
        return b;
    }
    
    public Blob getBlobImage(BufferedImage bi){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Blob blFile = null;
        try {
            ImageIO.write(bi, "png", baos);
            blFile = new javax.sql.rowset.serial.SerialBlob(baos.toByteArray());
        } catch (SQLException | IOException ex) {
            Logger.getLogger(AnggotaTambahFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
        return blFile;
    }
    
    private BufferedImage resizeImage(BufferedImage originalImage, int type){
        BufferedImage resizedImage = new BufferedImage(IMG_WIDTH, IMG_HEIGHT, type);
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(originalImage, 0, 0, IMG_WIDTH, IMG_HEIGHT, null);
        g.dispose();
        return resizedImage;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        fChooser = new javax.swing.JFileChooser();
        jLabel1 = new javax.swing.JLabel();
        eId = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        eNamaAnggota = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        rbLaki = new javax.swing.JRadioButton();
        rbPerempuan = new javax.swing.JRadioButton();
        jLabel4 = new javax.swing.JLabel();
        jXDatePicker1 = new org.jdesktop.swingx.JXDatePicker();
        jLabel5 = new javax.swing.JLabel();
        cbAgama = new javax.swing.JComboBox<>();
        jLabel6 = new javax.swing.JLabel();
        cbPetugas = new javax.swing.JComboBox<>();
        jLabel7 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        lbGambar = new javax.swing.JLabel();
        bPilih = new javax.swing.JButton();
        bSimpan = new javax.swing.JButton();
        bBatal = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jLabel1.setText("id");

        jLabel2.setText("Nama Anggota");

        eNamaAnggota.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                eNamaAnggotaActionPerformed(evt);
            }
        });

        jLabel3.setText("Jenis Kelamin");

        buttonGroup1.add(rbLaki);
        rbLaki.setText("Laki-laki");

        buttonGroup1.add(rbPerempuan);
        rbPerempuan.setText("Perempuan");

        jLabel4.setText("Tanggal Lahir");

        jLabel5.setText("Agama");

        cbAgama.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "- Pilih Agama - ", "Islam ", "Kristen Protestan", "Kristen Katolik", "Hindu", "Buddha", "Kong Hu Chu" }));

        jLabel6.setText("Petugas");

        cbPetugas.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel7.setText("Foto Anggota");

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lbGambar, javax.swing.GroupLayout.DEFAULT_SIZE, 258, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lbGambar, javax.swing.GroupLayout.DEFAULT_SIZE, 290, Short.MAX_VALUE)
        );

        bPilih.setText("Pilih Gambar");
        bPilih.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bPilihActionPerformed(evt);
            }
        });

        bSimpan.setText("Simpan");
        bSimpan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bSimpanActionPerformed(evt);
            }
        });

        bBatal.setText("Batal");
        bBatal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bBatalActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(jLabel2)
                                .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.LEADING))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel4)
                                .addGap(10, 10, 10))
                            .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.LEADING))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(18, 18, 18)
                                        .addComponent(bPilih, javax.swing.GroupLayout.PREFERRED_SIZE, 264, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 403, Short.MAX_VALUE)
                                        .addComponent(bSimpan)
                                        .addGap(18, 18, 18)
                                        .addComponent(bBatal))
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(layout.createSequentialGroup()
                                                .addGap(16, 16, 16)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                    .addGroup(layout.createSequentialGroup()
                                                        .addComponent(rbLaki)
                                                        .addGap(18, 18, 18)
                                                        .addComponent(rbPerempuan))
                                                    .addComponent(cbPetugas, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                    .addComponent(cbAgama, 0, 839, Short.MAX_VALUE)
                                                    .addComponent(jXDatePicker1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                    .addComponent(eNamaAnggota)))
                                            .addGroup(layout.createSequentialGroup()
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(eId, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                        .addGap(0, 0, Short.MAX_VALUE)))
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(eId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(25, 25, 25)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(eNamaAnggota, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(rbLaki)
                    .addComponent(rbPerempuan))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jXDatePicker1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(cbAgama, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(cbPetugas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel7)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(bPilih)
                    .addComponent(bSimpan)
                    .addComponent(bBatal))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void eNamaAnggotaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_eNamaAnggotaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_eNamaAnggotaActionPerformed

    private void bBatalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bBatalActionPerformed
        // TODO add your handling code here:
        dispose();
    }//GEN-LAST:event_bBatalActionPerformed

    private void bPilihActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bPilihActionPerformed
        // TODO add your handling code here:
        FileFilter filter = new FileNameExtensionFilter("Image Files",
                                                    "jpg", "png", "gif", "jpeg");
        fChooser.setFileFilter(filter);
        BufferedImage img = null;
        try {
            int result = fChooser.showOpenDialog(null);
            if (result == JFileChooser.APPROVE_OPTION) {
                File file = fChooser.getSelectedFile();
                img = ImageIO.read(file);
                int type = img.getType() == 0? BufferedImage.TYPE_INT_ARGB : img.getType();
                bImage = resizeImage(img, type);
                lbGambar.setIcon(new ImageIcon(bImage));
            }
        } catch (IOException e) {
            System.err.println("Error bPilih : "+e);
        }
    }//GEN-LAST:event_bPilihActionPerformed

    private void bSimpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bSimpanActionPerformed
        // TODO add your handling code here:
        Anggota anggota = new Anggota();
        anggota.setId(eId.getText());
        anggota.setNamaAnggota(eNamaAnggota.getText());
        anggota.setJenisKelamin(rbJenisKelaminGetSelected());
        anggota.setTanggalLahir(dateFormat.format(jXDatePicker1.getDate()));
        anggota.setAgama(cbAgama.getSelectedItem().toString());
        anggota.setFotoAnggota(getBlobImage(bImage));
        
        Petugas petugas = new Petugas();
        petugas.setId(((KeyValue)cbPetugas.getSelectedItem()).getKey());
        anggota.setPetugas(petugas);
        if(anggota.getNamaAnggota().equalsIgnoreCase("") ||
           anggota.getJenisKelamin().equalsIgnoreCase("") ||
           anggota.getTanggalLahir().equalsIgnoreCase("") ||
           anggota.getAgama().equalsIgnoreCase("- Pilih Agama -") ||
           anggota.getFotoAnggota()==null){
            JOptionPane.showMessageDialog(null, "Lengkapi data");
        } else {
            
            Koneksi koneksi = new Koneksi();
            Connection con = koneksi.getConnection();
            PreparedStatement ps;
            try {
                if(status==SEDANG_TAMBAH){
                    String qry = "insert into anggota values (?,?,?,?,?,?,?)";
                    
                        ps = con.prepareStatement(qry);
                        ps.setString(1, anggota.getId());
                        ps.setString(2, anggota.getNamaAnggota());
                        ps.setString(3, anggota.getJenisKelamin());
                        ps.setString(4, anggota.getTanggalLahir());
                        ps.setString(5, anggota.getAgama());
                        ps.setInt(6, anggota.getPetugas().getId());
                        ps.setBlob(7, anggota.getFotoAnggota());
                        ps.executeUpdate();
                } else {
                    String qry = "update anggota set nama_anggota = ?,"
                            + "jenis_kelamin = ?, tanggal_lahir = ?,"
                            + "agama = ?, id_petugas = ?,"
                            + "foto_anggota = ? WHERE id = ?";
                        ps = con.prepareStatement(qry);
                        ps.setString(1, anggota.getNamaAnggota());
                        ps.setString(2, anggota.getJenisKelamin());
                        ps.setString(3, anggota.getTanggalLahir());
                        ps.setString(4, anggota.getAgama());
                        ps.setInt(5, anggota.getPetugas().getId());
                        ps.setBlob(6, anggota.getFotoAnggota());
                        ps.setString(7, anggota.getId());
                        ps.executeUpdate();
                }
            } catch (SQLException ex) {
                System.err.println("Error Insert/update : "+ex);
            }
        dispose();
        } 
    }//GEN-LAST:event_bSimpanActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(AnggotaTambahFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(AnggotaTambahFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(AnggotaTambahFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(AnggotaTambahFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new AnggotaTambahFrame().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton bBatal;
    private javax.swing.JButton bPilih;
    private javax.swing.JButton bSimpan;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JComboBox<String> cbAgama;
    private javax.swing.JComboBox<String> cbPetugas;
    private javax.swing.JTextField eId;
    private javax.swing.JTextField eNamaAnggota;
    private javax.swing.JFileChooser fChooser;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private org.jdesktop.swingx.JXDatePicker jXDatePicker1;
    private javax.swing.JLabel lbGambar;
    private javax.swing.JRadioButton rbLaki;
    private javax.swing.JRadioButton rbPerempuan;
    // End of variables declaration//GEN-END:variables
}
