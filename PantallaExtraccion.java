package com.mycompany.menuprincipalbanco;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;

public class PantallaExtraccion extends JFrame {
    private JTextField txtCuenta, txtMonto;
    private JButton btnConfirmar, btnVolver;

    public PantallaExtraccion() {
        setTitle("🏧 Realizar Extracción");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setLayout(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JLabel lblCuenta = new JLabel("ID Cuenta Ahorro:");
        lblCuenta.setBounds(50, 50, 120, 25);
        add(lblCuenta);

        txtCuenta = new JTextField();
        txtCuenta.setBounds(180, 50, 150, 25);
        add(txtCuenta);

        JLabel lblMonto = new JLabel("Monto a extraer:");
        lblMonto.setBounds(50, 90, 120, 25);
        add(lblMonto);

        txtMonto = new JTextField();
        txtMonto.setBounds(180, 90, 150, 25);
        add(txtMonto);

        btnConfirmar = new JButton("✅ Confirmar Extracción");
        btnConfirmar.setBounds(80, 140, 220, 30);
        add(btnConfirmar);

        btnVolver = new JButton("⬅ Volver");
        btnVolver.setBounds(80, 190, 220, 30);
        add(btnVolver);

        // Evento extracción
        btnConfirmar.addActionListener((ActionEvent e) -> {
            realizarExtraccion();
        });

        // Volver al menú
        btnVolver.addActionListener((e) -> {
            new MenuPrincipalBanco().setVisible(true);
            dispose();
        });
    }

    private void realizarExtraccion() {
        int idCajaAhorro;
        double monto;

        try {
            idCajaAhorro = Integer.parseInt(txtCuenta.getText());
            monto = Double.parseDouble(txtMonto.getText());

            try (Connection conn = ConexionOracle.conectar()) {
                // Verificar saldo
                PreparedStatement ps = conn.prepareStatement("SELECT SALDO FROM CAJA_AHORRO WHERE ID_CAJA_AHORRO = ?");
                ps.setInt(1, idCajaAhorro);
                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    double saldoActual = rs.getDouble("SALDO");

                    if (saldoActual >= monto) {
                        // Actualizar saldo
                        PreparedStatement ps2 = conn.prepareStatement(
                                "UPDATE CAJA_AHORRO SET SALDO = SALDO - ? WHERE ID_CAJA_AHORRO = ?");
                        ps2.setDouble(1, monto);
                        ps2.setInt(2, idCajaAhorro);
                        ps2.executeUpdate();

                        // Registrar transacción (ID_TARJETA e ID_ATM puedes reemplazarlos con valores reales si lo deseas)
                        PreparedStatement ps3 = conn.prepareStatement(
                                "INSERT INTO TRANSACCION (TIPO, MONTO, FECHA, ID_CAJA_AHORRO, ID_TARJETA, ID_ATM) " +
                                "VALUES ('extraccion', ?, SYSDATE, ?, ?, ?)");
                        ps3.setDouble(1, monto);
                        ps3.setInt(2, idCajaAhorro);
                        ps3.setInt(3, 1); // ID_TARJETA genérico
                        ps3.setInt(4, 1); // ID_ATM genérico
                        ps3.executeUpdate();

                        JOptionPane.showMessageDialog(this, "✅ Extracción realizada correctamente.");
                    } else {
                        JOptionPane.showMessageDialog(this, "❌ Saldo insuficiente.");
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "❌ Cuenta no encontrada.");
                }

            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "❌ Error en la base de datos: " + ex.getMessage());
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "❌ Ingrese valores numéricos válidos.");
        }
    }
}
