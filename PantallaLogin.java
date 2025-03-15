package com.mycompany.menuprincipalbanco;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;

public class PantallaLogin extends JFrame {
    private JTextField txtIdCliente, txtNombre;
    private JButton btnIngresar;

    public PantallaLogin() {
        setTitle("🔐 Login - Cajero Banco");
        setSize(400, 250);
        setLocationRelativeTo(null);
        setLayout(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JLabel lblTitulo = new JLabel("Inicio de Sesión");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitulo.setBounds(120, 20, 200, 30);
        add(lblTitulo);

        JLabel lblId = new JLabel("ID Cliente:");
        lblId.setBounds(60, 70, 100, 25);
        add(lblId);

        txtIdCliente = new JTextField();
        txtIdCliente.setBounds(160, 70, 150, 25);
        add(txtIdCliente);

        JLabel lblNombre = new JLabel("Nombre:");
        lblNombre.setBounds(60, 110, 100, 25);
        add(lblNombre);

        txtNombre = new JTextField();
        txtNombre.setBounds(160, 110, 150, 25);
        add(txtNombre);

        btnIngresar = new JButton("✅ Ingresar");
        btnIngresar.setBounds(120, 160, 150, 30);
        add(btnIngresar);

        btnIngresar.addActionListener((ActionEvent e) -> {
            validarLogin();
        });
    }

    private void validarLogin() {
        try {
            int idCliente = Integer.parseInt(txtIdCliente.getText());
            String nombre = txtNombre.getText().trim();

            try (Connection conn = ConexionOracle.conectar()) {
                PreparedStatement ps = conn.prepareStatement(
                        "SELECT * FROM CLIENTE WHERE ID_CLIENTE = ? AND LOWER(NOMBRE) = LOWER(?)");
                ps.setInt(1, idCliente);
                ps.setString(2, nombre);
                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    JOptionPane.showMessageDialog(this, "✅ Bienvenido al sistema, " + nombre + "!");
                    new MenuPrincipalBanco().setVisible(true);
                    dispose(); // Cierra login
                } else {
                    JOptionPane.showMessageDialog(this, "❌ ID o Nombre incorrecto.");
                }

            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "❌ Error en base de datos: " + ex.getMessage());
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "❌ Ingrese un ID válido.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new PantallaLogin().setVisible(true));
    }
}
