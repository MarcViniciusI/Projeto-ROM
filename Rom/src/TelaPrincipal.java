
public class TelaPrincipal extends javax.swing.JFrame {

    
    public TelaPrincipal() {
        initComponents();
        IconUtils.loadIcon(this);
    }

    private void initComponents() {

        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu2 = new javax.swing.JMenu();
        MnItemSemanais = new javax.swing.JMenuItem();
        MnItemFarm = new javax.swing.JMenuItem();
        jMenu1 = new javax.swing.JMenu();
        MnSobre = new javax.swing.JMenu();
        SobreDesen = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Tela Principal");

        jMenu2.setText("Gerenciamento");

        MnItemSemanais.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        MnItemSemanais.setText("Semanais");
        MnItemSemanais.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MnItemSemanaisActionPerformed(evt);
            }
        });
        jMenu2.add(MnItemSemanais);

        MnItemFarm.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        MnItemFarm.setText("Farm");
        MnItemFarm.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MnItemFarmActionPerformed(evt);
            }
        });
        jMenu2.add(MnItemFarm);

        jMenuBar1.add(jMenu2);
       
        jMenuBar1.add(jMenu1);

        MnSobre.setText("Sobre");

        SobreDesen.setText("Sobre o Desenvolvimento");
        SobreDesen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SobreDesenActionPerformed(evt);
            }
        });
        MnSobre.add(SobreDesen);

        jMenuBar1.add(MnSobre);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 277, Short.MAX_VALUE)
        );

        pack();
    }

    private void SobreDesenActionPerformed(java.awt.event.ActionEvent evt) {
        TelaDesenvolvedor sobre = new TelaDesenvolvedor();
        sobre.setVisible(true);
    }

    private void MnItemSemanaisActionPerformed(java.awt.event.ActionEvent evt) {
        TaskManager semanais = new TaskManager();
        semanais.setVisible(true);
    }

    private void MnItemFarmActionPerformed(java.awt.event.ActionEvent evt) {
       
    }

    
    public static void main(String args[]) {
        
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(TelaPrincipal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(TelaPrincipal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(TelaPrincipal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(TelaPrincipal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new TelaPrincipal().setVisible(true);
            }
        });
    }

    
    private javax.swing.JMenuItem MnItemFarm;
    private javax.swing.JMenuItem MnItemSemanais;
    private javax.swing.JMenu MnSobre;
    private javax.swing.JMenuItem SobreDesen;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    
}
