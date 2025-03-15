package com.mycompany.menuprincipalbanco;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;

public class TransferenciaBancoCoolUI extends JFrame {
    private JTextField txtCuentaOrigen, txtCuentaDestino, txtMonto;
    private JButton btnTransferir, btnVolver;

    public TransferenciaBancoCoolUI() {
        setTitle("💸 Realizar Transferencia");
        setSize(450, 350);
        setLocationRelativeTo(null);
        setLayout(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JLabel lblCuentaOrigen = new JLabel("ID Cuenta Origen:");
        lblCuentaOrigen.setBounds(50, 50, 150, 25);
        add(lblCuentaOrigen);

        txtCuentaOrigen = new JTextField();
        txtCuentaOrigen.setBounds(200, 50, 180, 25);
        add(txtCuentaOrigen);

        JLabel lblCuentaDestino = new JLabel("ID Cuenta Destino:");
        lblCuentaDestino.setBounds(50, 90, 150, 25);
        add(lblCuentaDestino);

        txtCuentaDestino = new JTextField();
        txtCuentaDestino.setBounds(200, 90, 180, 25);
        add(txtCuentaDestino);

        JLabel lblMonto = new JLabel("Monto a transferir:");
        lblMonto.setBounds(50, 130, 150, 25);
        add(lblMonto);

        txtMonto = new JTextField();
        txtMonto.setBounds(200, 130, 180, 25);
        add(txtMonto);

        btnTransferir = new JButton("✅ Transferir");
        btnTransferir.setBounds(120, 180, 200, 30);
        add(btnTransferir);

        btnVolver = new JButton("⬅ Volver");
        btnVolver.setBounds(120, 230, 200, 30);
        add(btnVolver);

        btnTransferir.addActionListener((ActionEvent e) -> {
            realizarTransferencia();
        });

        btnVolver.addActionListener((e) -> {
            new MenuPrincipalBanco().setVisible(true);
            dispose();
        });
    }

    private void realizarTransferencia() {
        int idOrigen, idDestino;
        double monto;

        try {
            idOrigen = Integer.parseInt(txtCuentaOrigen.getText());
            idDestino = Integer.parseInt(txtCuentaDestino.getText());
            monto = Double.parseDouble(txtMonto.getText());

            if (idOrigen == idDestino) {
                JOptionPane.showMessageDialog(this, "❌ Las cuentas no pueden ser iguales.");
                return;
            }

            try (Connection conn = ConexionOracle.conectar()) {
                conn.setAutoCommit(false); // Transacción

                // Verificar saldo cuenta origen
                PreparedStatement ps = conn.prepareStatement("SELECT SALDO FROM CAJA_AHORRO WHERE ID_CAJA_AHORRO = ?");
                ps.setInt(1, idOrigen);
                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    double saldoOrigen = rs.getDouble("SALDO");

                    if (saldoOrigen >= monto) {
                        // Verificar que cuenta destino exista
                        PreparedStatement ps2 = conn.prepareStatement("SELECT SALDO FROM CAJA_AHORRO WHERE ID_CAJA_AHORRO = ?");
                        ps2.setInt(1, idDestino);
                        ResultSet rs2 = ps2.executeQuery();

                        if (rs2.next()) {
                            // Descontar de origen
                            PreparedStatement updOrigen = conn.prepareStatement(
                                "UPDATE CAJA_AHORRO SET SALDO = SALDO - ? WHERE ID_CAJA_AHORRO = ?");
                            updOrigen.setDouble(1, monto);
                            updOrigen.setInt(2, idOrigen);
                            updOrigen.executeUpdate();

                            // Acreditar a destino
                            PreparedStatement updDestino = conn.prepareStatement(
                                "UPDATE CAJA_AHORRO SET SALDO = SALDO + ? WHERE ID_CAJA_AHORRO = ?");
                            updDestino.setDouble(1, monto);
                            updDestino.setInt(2, idDestino);
                            updDestino.executeUpdate();

                            // Registrar transacción (salida)
                            PreparedStatement reg1 = conn.prepareStatement(
                                "INSERT INTO TRANSACCION (TIPO, MONTO, FECHA, ID_CAJA_AHORRO, ID_TARJETA, ID_ATM) " +
                                "VALUES ('transferencia_salida', ?, SYSDATE, ?, ?, ?)");
                            reg1.setDouble(1, monto);
                            reg1.setInt(2, idOrigen);
                            reg1.setInt(3, 1); // ID_TARJETA genérico
                            reg1.setInt(4, 1); // ID_ATM genérico
                            reg1.executeUpdate();

                            // Registrar transacción (ingreso)
                            PreparedStatement reg2 = conn.prepareStatement(
                                "INSERT INTO TRANSACCION (TIPO, MONTO, FECHA, ID_CAJA_AHORRO, ID_TARJETA, ID_ATM) " +
                                "VALUES ('transferencia_ingreso', ?, SYSDATE, ?, ?, ?)");
                            reg2.setDouble(1, monto);
                            reg2.setInt(2, idDestino);
                            reg2.setInt(3, 1); // ID_TARJETA genérico
                            reg2.setInt(4, 1); // ID_ATM genérico
                            reg2.executeUpdate();

                            conn.commit(); // Confirmar transacción
                            JOptionPane.showMessageDialog(this, "✅ Transferencia realizada con éxito.");

                        } else {
                            conn.rollback();
                            JOptionPane.showMessageDialog(this, "❌ La cuenta destino no existe.");
                        }
                    } else {
                        JOptionPane.showMessageDialog(this, "❌ Saldo insuficiente en cuenta origen.");
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "❌ La cuenta origen no existe.");
                }

            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "❌ Error al realizar transferencia: " + ex.getMessage());
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "❌ Ingrese valores numéricos válidos.");
        }
    }
}
