package com.kwh.kwhapi.ui;

import java.time.LocalDate;
import java.util.List;

import com.kwh.kwhapi.model.Consumo;
import com.kwh.kwhapi.model.Dispositivo;
import com.kwh.kwhapi.model.User;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;
import javafx.application.Application;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class KwhApp extends Application {

       private final EntityManagerFactory emf = Persistence.createEntityManagerFactory("kwhPU");
       private final EntityManager em = emf.createEntityManager();

    private ObservableList<Dispositivo> dispositivos;
    private final ObservableList<Consumo> consumos = FXCollections.observableArrayList();

    private double precioKwh = 0.0; // Precio del kWh

    @Override
    public void start(Stage stage) {
        loginStage(stage);
    }

    private void loginStage(Stage stage) {
        VBox root = new VBox(10);
        root.setPadding(new Insets(10));

        TextField tfUser = new TextField();
        tfUser.setPromptText("Usuario");

        PasswordField pfPass = new PasswordField();
        pfPass.setPromptText("Contraseña");

        Button btnLogin = new Button("Iniciar sesión");

        Label lblError = new Label();

        btnLogin.setOnAction(e -> {
            if (validarUsuario(tfUser.getText(), pfPass.getText())) {
                mainStage(stage);
            } else {
                lblError.setText("Usuario o contraseña incorrectos");
            }
        });

        root.getChildren().addAll(tfUser, pfPass, btnLogin, lblError);

        stage.setScene(new Scene(root, 300, 200));
        stage.setTitle("Login");
        stage.show();
    }

    private void mainStage(Stage stage) {
        VBox root = new VBox(15);
        root.setPadding(new Insets(10));

        // === Mensaje de ahorro energético ===
        Label lblMensaje = new Label("Recuerda: ahorrar energía contribuye a un planeta más sostenible y reduce tus gastos.");
        lblMensaje.setTextFill(Color.DARKGREEN);

        // === Sección 1: Configurar Precio del KWh ===
        Label lblPrecio = new Label("Precio del KWh:");
        TextField tfPrecioKwh = new TextField();
        tfPrecioKwh.setPromptText("Ejemplo: 500");
        Button btnSetPrecio = new Button("Establecer precio");
        Label lblPrecioActual = new Label("Precio actual: no definido");

        btnSetPrecio.setOnAction(e -> {
            try {
                precioKwh = Double.parseDouble(tfPrecioKwh.getText());
                lblPrecioActual.setText("Precio actual: $" + precioKwh + " por KWh");
            } catch (NumberFormatException ex) {
                new Alert(Alert.AlertType.ERROR, "Ingresa un número válido para el precio del KWh.").show();
            }
        });

        HBox precioBox = new HBox(10, lblPrecio, tfPrecioKwh, btnSetPrecio, lblPrecioActual);

        // === Sección 2: Agregar Dispositivo ===
        Label lblAddDisp = new Label("Agregar nuevo dispositivo:");

        TextField tfNombreDisp = new TextField();
        tfNombreDisp.setPromptText("Nombre del dispositivo");

        TextField tfPotencia = new TextField();
        tfPotencia.setPromptText("Potencia (kW)");

        ComboBox<String> cbCategoria = new ComboBox<>();
        cbCategoria.getItems().addAll(
                "Iluminación", "Cocina", "Climatización", "Entretenimiento", 
                "Lavado", "Electrónica", "Limpieza", "Cuidado personal"
        );
        cbCategoria.setPromptText("Categoría");

        Button btnAgregarDisp = new Button("Agregar dispositivo");

        btnAgregarDisp.setOnAction(e -> {
            try {
                String nombre = tfNombreDisp.getText();
                double potencia = Double.parseDouble(tfPotencia.getText());
                String categoria = cbCategoria.getValue();

                if (nombre.isEmpty() || categoria == null) {
                    new Alert(Alert.AlertType.ERROR, "Nombre y categoría son obligatorios.").show();
                    return;
                }

                Dispositivo d = new Dispositivo();
                d.setNombre(nombre);
                d.setPotencia(potencia);
                d.setCategoria(categoria);

                guardarDispositivo(d);
                dispositivos.add(d);

                tfNombreDisp.clear();
                tfPotencia.clear();
                cbCategoria.setValue(null);

                new Alert(Alert.AlertType.INFORMATION, "Dispositivo agregado exitosamente.").show();
            } catch (NumberFormatException ex) {
                new Alert(Alert.AlertType.ERROR, "Potencia inválida. Ingresa un número.").show();
            }
        });

        HBox addDispBox = new HBox(10, tfNombreDisp, tfPotencia, cbCategoria, btnAgregarDisp);
        VBox seccionAgregar = new VBox(5, lblAddDisp, addDispBox);

        // === Sección 3: Calcular consumo ===
        dispositivos = FXCollections.observableArrayList(loadDispositivos());

        ComboBox<Dispositivo> comboDispositivo = new ComboBox<>(dispositivos);
        comboDispositivo.setPromptText("Selecciona un dispositivo");

        // Mostrar nombre y potencia
        comboDispositivo.setConverter(new javafx.util.StringConverter<Dispositivo>() {
            @Override
            public String toString(Dispositivo d) {
                if (d == null) return "";
                return d.getNombre() + " (" + d.getPotencia() + " kW)";
            }

            @Override
            public Dispositivo fromString(String string) {
                return null;
            }
        });

        TextField tfTiempo = new TextField();
        tfTiempo.setPromptText("Horas de uso");

        Button btnCalcular = new Button("Calcular consumo");

        TableView<Consumo> table = new TableView<>(consumos);

        TableColumn<Consumo, String> colDisp = new TableColumn<>("Dispositivo");
        colDisp.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().getDispositivo() != null ? data.getValue().getDispositivo().getNombre() : "N/A"));

        TableColumn<Consumo, Double> colPot = new TableColumn<>("Potencia (kW)");
        colPot.setCellValueFactory(data -> new SimpleDoubleProperty(
                data.getValue().getDispositivo() != null ? data.getValue().getDispositivo().getPotencia() : 0.0).asObject());

        TableColumn<Consumo, Double> colConsumo = new TableColumn<>("Consumo (kWh)");
        colConsumo.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getConsumo()).asObject());

        TableColumn<Consumo, Double> colTotal = new TableColumn<>("Costo ($)");
        colTotal.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getTotal()).asObject());

        table.getColumns().addAll(colDisp, colPot, colConsumo, colTotal);

        btnCalcular.setOnAction(e -> {
            Dispositivo d = comboDispositivo.getValue();
            if (d != null && !tfTiempo.getText().isEmpty()) {
                try {
                    if (precioKwh <= 0) {
                        new Alert(Alert.AlertType.WARNING, "Primero establece el precio del KWh.").show();
                        return;
                    }

                    double horas = Double.parseDouble(tfTiempo.getText());
                    double consumoCalc = d.getPotencia() * horas;
                    double total = consumoCalc * precioKwh;

                    Consumo c = new Consumo();
                    c.setConsumo(consumoCalc);
                    c.setTarifa(precioKwh);
                    c.setTotal(total);
                    c.setFecha(LocalDate.now());
                    c.setDispositivo(d);

                    guardarConsumo(c);
                    consumos.add(c);

                } catch (NumberFormatException ex) {
                    new Alert(Alert.AlertType.ERROR, "Ingresa un número válido para el tiempo.").show();
                }
            } else {
                new Alert(Alert.AlertType.WARNING, "Selecciona un dispositivo e ingresa las horas.").show();
            }
        });

        VBox seccionCalculo = new VBox(10, comboDispositivo, tfTiempo, btnCalcular, table);

        // === Estructura general ===
        root.getChildren().addAll(lblMensaje, precioBox, seccionAgregar, seccionCalculo);

        stage.setScene(new Scene(root, 800, 500));
        stage.setTitle("Calculadora de Consumo Energético");
        stage.show();
    }

    private boolean validarUsuario(String username, String password) {
        try {
            TypedQuery<User> query = em.createQuery(
                    "SELECT u FROM User u WHERE u.username = :u AND u.password = :p", User.class);
            query.setParameter("u", username);
            query.setParameter("p", password);
            List<User> result = query.getResultList();
            return !result.isEmpty();
        } catch (Exception e) {
        }
        return false;
    }

    private List<Dispositivo> loadDispositivos() {
        return em.createQuery("SELECT d FROM Dispositivo d", Dispositivo.class).getResultList();
    }

    private void guardarConsumo(Consumo c) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(c);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
        }
    }

    private void guardarDispositivo(Dispositivo d) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(d);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
        }
    }

    @Override
    public void stop() {
        em.close();
        emf.close();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
