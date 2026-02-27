package com.alfredo.ui;

import com.alfredo.data.ActualizarEvento;
import com.alfredo.data.CrearEvento;
import com.alfredo.data.LeerEvento;
import com.alfredo.model.Imagenes;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.swing.*;

public class ManipularEventos extends DesplegarUI {
    private JTextField nombreTextField;
    private JTextArea descripcionTextArea;
    private JTextField vigenciaTextField;
    private JTextField valorTextField;
    private JTextField lugarTextField;
    public JPanel panelPrincipal;
    private JButton listoButton;
    private JButton examinarButton;
    private JButton subirButton;
    private JTextArea rutaDeArchivosTextArea;
    private JLabel subidaEstado;

    public ManipularEventos(String titulo, String id) {
        this.id = id;
        LeerEvento leerEvento = new LeerEvento();
        rutaDeArchivosTextArea.setEnabled(false);

        JFrame frameModificar = desplegarUI(panelPrincipal, titulo, JFrame.DISPOSE_ON_CLOSE);
        frameModificar.setAlwaysOnTop(true);


        if (titulo == "Modificar Evento") {

            String[] evento = leerEvento.leerEventos(id);
            nombreTextField.setText(evento[0]);
            descripcionTextArea.setText(evento[2]);
            vigenciaTextField.setText(evento[3]);
            valorTextField.setText(evento[4]);
            rutaDeArchivosTextArea.setText(evento[1]);
            lugarTextField.setText(evento[5]);

            examinarButton.addActionListener(e -> {
                String ruta = new AbrirExploradorArchivos().seleccionarArchivo();
                rutaDeArchivosTextArea.setText(ruta);
            });

            subirButton.addActionListener(e -> {
                String ruta = rutaDeArchivosTextArea.getText();

                new SubirFotos(ruta, new SubirFotos.OnUploadComplete() {
                    @Override
                    public void onSuccess(String url) {
                        rutaDeArchivosTextArea.setText(url);
                    }

                    @Override
                    public void onError(String mensaje) {
                        JOptionPane.showMessageDialog(null, "Error: " + mensaje);
                    }
                }, subirButton, listoButton).execute();
            });

            listoButton.setText(titulo);
            listoButton.addActionListener(e -> {
                nombre = nombreTextField.getText();
                descripcion = descripcionTextArea.getText();
                vigencia = vigenciaTextField.getText();
                valor = valorTextField.getText();
                fotos = rutaDeArchivosTextArea.getText();
                lugar = lugarTextField.getText();
                new ActualizarEvento(id, nombre, fotos, descripcion, vigencia, valor, lugar);
                JOptionPane.showMessageDialog(frameModificar, "Evento modificado correctamente");

            });
        } else if (titulo == "Evento:") {
            String[] evento = leerEvento.leerEventos(id);
            nombreTextField.setText(evento[0]);
            descripcionTextArea.setText(evento[2]);
            vigenciaTextField.setText(evento[3]);
            valorTextField.setText(evento[4]);
            lugarTextField.setText(evento[5]);
            rutaDeArchivosTextArea.setText(evento[1]);
            listoButton.setVisible(false);
        }
    }

    public void setId(String id) {
        this.id = id;
    }

    String id;
    String nombre = nombreTextField.getText();
    String descripcion = descripcionTextArea.getText();
    String vigencia = vigenciaTextField.getText();
    String valor = valorTextField.getText();
    String lugar = lugarTextField.getText();
    String fotos = rutaDeArchivosTextArea.getText();

    public ManipularEventos(String titulo) {

        rutaDeArchivosTextArea.setEnabled(false);

        JFrame frameModificar = desplegarUI(panelPrincipal, titulo, JFrame.DISPOSE_ON_CLOSE);
        frameModificar.setAlwaysOnTop(true);

        if (titulo == "Crear Evento") {
            examinarButton.addActionListener(e -> {
                String ruta = new AbrirExploradorArchivos().seleccionarArchivo();
                rutaDeArchivosTextArea.setText(ruta);
            });

            subirButton.addActionListener(e -> {
                String ruta = rutaDeArchivosTextArea.getText();

                new SubirFotos(ruta, new SubirFotos.OnUploadComplete() {
                    @Override
                    public void onSuccess(String url) {
                        rutaDeArchivosTextArea.setText(url);
                    }

                    @Override
                    public void onError(String mensaje) {
                        JOptionPane.showMessageDialog(null, "Error: " + mensaje);
                    }
                }, subirButton, listoButton).execute();
            });

            listoButton.setText(titulo);
            listoButton.addActionListener(e -> {
                nombre = nombreTextField.getText();
                descripcion = descripcionTextArea.getText();
                vigencia = vigenciaTextField.getText();
                valor = valorTextField.getText();
                lugar = lugarTextField.getText();
                fotos = rutaDeArchivosTextArea.getText();
                new CrearEvento(nombre, fotos, descripcion, vigencia, valor, lugar);
                JOptionPane.showMessageDialog(frameModificar, "Evento creado correctamente");
            });
        }
    }
}

