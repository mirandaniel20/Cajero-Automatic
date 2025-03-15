
package com.mycompany.menuprincipalbanco;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class MenuPrincipalBanco extends JFrame {

    private JButton btnTransferencia, btnExtraccion, btnSalir;
    private JLabel lblTitulo;

    public MenuPrincipalBanco() {
        setTitle("🏦 Cajero Automático - Menú Principal");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        setLayout(null);

        getContentPane().setBackground(new Color(230, 245, 255));

        lblTitulo = new JLabel("Cajero Virtual:");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitulo.setBounds(80, 30, 300, 30);
        add(lblTitulo);

        btnTransferencia = new JButton("💸 Realizar Transferencia");
        btnTransferencia.setBounds(80, 80, 230, 40);
        btnTransferencia.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        add(btnTransferencia);

        btnExtraccion = new JButton("🏧 Realizar Extracción");
        btnExtraccion.setBounds(80, 130, 230, 40);
        btnExtraccion.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        add(btnExtraccion);

        btnSalir = new JButton("❌ Salir");
        btnSalir.setBounds(80, 190, 230, 40);
        btnSalir.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        add(btnSalir);

        // Eventos de botones
        btnTransferencia.addActionListener((ActionEvent e) -> {
            new TransferenciaBancoCoolUI().setVisible(true);
            dispose(); // Cierra este menú
        });

        btnExtraccion.addActionListener((ActionEvent e) -> {
            new PantallaExtraccion().setVisible(true);
            dispose(); // Cierra este menú
        });

        btnSalir.addActionListener((ActionEvent e) -> {
            JOptionPane.showMessageDialog(this, "Gracias por usar el cajero automático.");
            System.exit(0);
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MenuPrincipalBanco().setVisible(true));
    }
}

