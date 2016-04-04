
import java.util.Objects;
import javax.swing.table.DefaultTableModel;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author FZ4432
 */
public class MaintenanceView extends javax.swing.JFrame {

    /**
     * Creates new form MaintenanceView
     */
    public MaintenanceView() {
        initComponents();
        initDeviceListView();
        
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jDeviceListTable = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Maintenance Monitor");

        jDeviceListTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Device ID", "Device Name", "Device Description", "Device Status", "Device Active Time (hh:mm:ss)", "Time Since Last Comms (hh:mm:ss)"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(jDeviceListTable);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 991, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 297, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

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
            java.util.logging.Logger.getLogger(MaintenanceView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MaintenanceView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MaintenanceView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MaintenanceView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MaintenanceView().setVisible(true);
            }
        });
    }
    
    
    public void initDeviceListView()
    {
        /*Clear the tables*/
        DefaultTableModel tm=(DefaultTableModel) jDeviceListTable.getModel();
        tm.setRowCount(0);   
    }
    
    public void InsertNewDeviceEntry(String deviceID, String deviceName, String deviceDesc, String deviceStatus, String deviceStartTime, String deviceTimeSinceLastUpdate)
    {
        DefaultTableModel tm=(DefaultTableModel) jDeviceListTable.getModel();
        Object myrow[] = { deviceID,  deviceName,  deviceDesc, deviceStatus,  deviceStartTime,  deviceTimeSinceLastUpdate};
        tm.addRow(myrow);
    }
    
    public int FindDeviceEntry(String deviceID)
    {
        int retVal = -1;
        DefaultTableModel tm=(DefaultTableModel) jDeviceListTable.getModel();
        
        for(int i=0; i<tm.getRowCount();i++)
        {
            Object viewdeviceID = tm.getValueAt(i, 0);
            if(Objects.equals(viewdeviceID, deviceID))
            {
                retVal = i;
                break;
            }
        }
        return retVal;
    }
    public void UpdateDeviceEntry(String deviceID, String deviceName, String deviceDesc, String deviceStatus, String deviceStartTime, String deviceTimeSinceLastUpdate)
    {
        DefaultTableModel tm=(DefaultTableModel) jDeviceListTable.getModel();
        int deviceRow = FindDeviceEntry(deviceID);
        
        if(deviceRow == -1)
        {
            InsertNewDeviceEntry(deviceID, deviceName, deviceDesc, deviceStatus, deviceStartTime, deviceTimeSinceLastUpdate);
        }
        else
        {
            tm.setValueAt(deviceName, deviceRow, 1);
            tm.setValueAt(deviceDesc, deviceRow, 2);
            tm.setValueAt(deviceStatus, deviceRow, 3);
            tm.setValueAt(deviceStartTime, deviceRow, 4);
            tm.setValueAt(deviceTimeSinceLastUpdate, deviceRow, 5);
        }
    }
    
    public void UpdateDeviceStatus(String deviceID, String deviceStatus, String deviceStartTime, String deviceTimeSinceLastUpdate)
    {
        DefaultTableModel tm=(DefaultTableModel) jDeviceListTable.getModel();
        int deviceRow = FindDeviceEntry(deviceID);
        
        if(deviceRow != -1)
        {
            tm.setValueAt(deviceStatus, deviceRow, 3);
            tm.setValueAt(deviceTimeSinceLastUpdate, deviceRow, 5);
            tm.setValueAt(deviceStartTime, deviceRow, 4);
        }
        jDeviceListTable.invalidate();
        tm.fireTableDataChanged(); 
        
    }
    
            

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTable jDeviceListTable;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables
}
