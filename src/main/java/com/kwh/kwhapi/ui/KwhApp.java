package com.kwh.kwhapi.ui;

import java.time.LocalDate;
import java.util.List;

import com.kwh.kwhapi.model.Consumo;
import com.kwh.kwhapi.model.Dispositivo;
import com.kwh.kwhapi.model.Tarifa;
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
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class KwhApp extends Application {

    private final EntityManagerFactory emf = Persistence.createEntityManagerFactory("kwhPU");
    private final EntityManager em = emf.createEntityManager();

    private ObservableList<Dispositivo> dispositivos;
    private final ObservableList<Consumo> consumos = FXCollections.observableArrayList();
    private double precioKwh = 0.0;
    private User usuarioActual = null;

    @Override
    public void start(Stage stage) {
        mostrarLogin(stage);
    }

    /** ================= LOGIN ================= **/
    private void mostrarLogin(Stage stage) {
        VBox root = new VBox(10);
        root.setPadding(new Insets(10));

        Label lblTitulo = new Label("Iniciar Sesión");
        lblTitulo.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        TextField tfUser = new TextField();
        tfUser.setPromptText("Usuario");

        PasswordField pfPass = new PasswordField();
        pfPass.setPromptText("Contraseña");

        Button btnLogin = new Button("Iniciar sesión");
        Button btnGoRegister = new Button("Registrarse");

        Label lblError = new Label();
        lblError.setTextFill(Color.RED);

        btnLogin.setOnAction(e -> {
            if (validarUsuario(tfUser.getText(), pfPass.getText())) {
                mainStage(stage);
            } else {
                lblError.setText("Usuario o contraseña incorrectos");
            }
        });

        btnGoRegister.setOnAction(e -> mostrarRegistro(stage));

        HBox botonesLoginBox = new HBox(10, btnLogin, btnGoRegister);
        botonesLoginBox.setAlignment(javafx.geometry.Pos.CENTER);

        root.getChildren().addAll(lblTitulo, tfUser, pfPass, botonesLoginBox, lblError);

        stage.setScene(new Scene(root, 300, 250));
        stage.setTitle("Login - KwhApp");
        stage.show();
    }

    /** ================= REGISTRO ================= **/
    private void mostrarRegistro(Stage stage) {
        VBox root = new VBox(10);
        root.setPadding(new Insets(15));

        Label lblTitulo = new Label("Registro de Usuario");
        lblTitulo.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        TextField tfUser = new TextField();
        tfUser.setPromptText("Nuevo usuario");

        PasswordField pfPass = new PasswordField();
        pfPass.setPromptText("Contraseña");

        Button btnRegistrar = new Button("Registrar");
        Button btnVolver = new Button("Volver al login");

        Label lblMsg = new Label();

        btnRegistrar.setOnAction(e -> {
            if (tfUser.getText().isEmpty() || pfPass.getText().isEmpty()) {
                lblMsg.setText("Por favor, completa todos los campos.");
                lblMsg.setTextFill(Color.RED);
                return;
            }

            if (usuarioExiste(tfUser.getText())) {
                lblMsg.setText("El usuario ya existe.");
                lblMsg.setTextFill(Color.RED);
                return;
            }

            registrarUsuario(tfUser.getText(), pfPass.getText());
            lblMsg.setText("Usuario registrado exitosamente. Ahora inicia sesión.");
            lblMsg.setTextFill(Color.GREEN);
        });

        btnVolver.setOnAction(e -> mostrarLogin(stage));

        HBox botonesRegistroBox = new HBox(10, btnRegistrar, btnVolver);
        botonesRegistroBox.setAlignment(javafx.geometry.Pos.CENTER);
        root.getChildren().addAll(lblTitulo, tfUser, pfPass, botonesRegistroBox, lblMsg);

        stage.setScene(new Scene(root, 300, 250));
        stage.setTitle("Registro");
        stage.show();
    }

    /** ================= PANTALLA PRINCIPAL ================= **/
    private void mainStage(Stage stage) {
        VBox root = new VBox(15);
        root.setPadding(new Insets(10));

        Label lblBienvenida = new Label("Bienvenido, " + usuarioActual.getUsername());
        lblBienvenida.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        Button btnLogout = new Button("Cerrar sesión");
        btnLogout.setOnAction(e -> {
            usuarioActual = null;
            mostrarLogin(stage);
        });


        // === Mensaje de ahorro energético ===
        Label lblMensaje = new Label("Recuerda: ahorrar energía contribuye a un planeta más sostenible y reduce tus gastos.");
        lblMensaje.setTextFill(Color.DARKGREEN);

        ComboBox<Tarifa> cbTarifa = new ComboBox<>();
cbTarifa.setPromptText("Selecciona una tarifa existente");

TextField tfPrecioKwh = new TextField();
tfPrecioKwh.setPromptText("O ingresa un precio manual");

Button btnSetPrecio = new Button("Aplicar precio");
Label lblPrecioActual = new Label("Precio actual: no definido");

// Cargar tarifas desde la base de datos
ObservableList<Tarifa> tarifas = FXCollections.observableArrayList(loadTarifas());
cbTarifa.setItems(tarifas);

// Mostrar nombre + valor
cbTarifa.setConverter(new javafx.util.StringConverter<>() {
    @Override
    public String toString(Tarifa t) {
        if (t == null) return "";
        return t.getNombre() + " ($" + t.getValorUnitario() + ")";
    }
    @Override
    public Tarifa fromString(String s) { return null; }
});

btnSetPrecio.setOnAction(e -> {
    if (cbTarifa.getValue() != null) {
        Tarifa seleccionada = cbTarifa.getValue();
        precioKwh = seleccionada.getValorUnitario();
        lblPrecioActual.setText("Tarifa seleccionada: " + seleccionada.getNombre() + " - $" + precioKwh + " por KWh");
    } else {
        try {
            precioKwh = Double.parseDouble(tfPrecioKwh.getText());
            lblPrecioActual.setText("Precio manual: $" + precioKwh + " por KWh");
        } catch (NumberFormatException ex) {
            new Alert(Alert.AlertType.ERROR, "Ingresa un número válido para el precio del KWh.").show();
        }
    }
});

HBox precioBox = new HBox(10, cbTarifa, tfPrecioKwh, btnSetPrecio, lblPrecioActual);


        // === Agregar Dispositivo ===
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

        // === Calcular consumo ===
        dispositivos = FXCollections.observableArrayList(loadDispositivos());
        ComboBox<Dispositivo> comboDispositivo = new ComboBox<>(dispositivos);
        comboDispositivo.setPromptText("Selecciona un dispositivo");

        comboDispositivo.setConverter(new javafx.util.StringConverter<>() {
            @Override
            public String toString(Dispositivo d) {
                if (d == null) return "";
                return d.getNombre() + " (" + d.getPotencia() + " kW)";
            }
            @Override
            public Dispositivo fromString(String s) { return null; }
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

    HBox topBar = new HBox(10);
    topBar.setPadding(new Insets(5));
    topBar.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
    topBar.setStyle("-fx-background-color: #f0f0f0; -fx-padding: 8;"); // opcional, solo para ver la barra
    topBar.getChildren().addAll(lblBienvenida, btnLogout);


        root.getChildren().addAll(topBar, lblMensaje, precioBox, seccionAgregar, seccionCalculo);

        stage.setScene(new Scene(root, 850, 550));
        stage.setTitle("Calculadora de Consumo Energético");
        stage.show();
    }

    /** ================= MÉTODOS DE BASE DE DATOS ================= **/
    private boolean validarUsuario(String username, String password) {
        try {
            TypedQuery<User> query = em.createQuery(
                    "SELECT u FROM User u WHERE u.username = :u AND u.password = :p", User.class);
            query.setParameter("u", username);
            query.setParameter("p", password);
            List<User> result = query.getResultList();
            if (!result.isEmpty()) {
                usuarioActual = result.get(0);
                return true;
            }
        } catch (Exception ignored) {}
        return false;
    }

    private boolean usuarioExiste(String username) {
        List<User> result = em.createQuery("SELECT u FROM User u WHERE u.username = :u", User.class)
                .setParameter("u", username)
                .getResultList();
        return !result.isEmpty();
    }

    private void registrarUsuario(String username, String password) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            User u = new User();
            u.setUsername(username);
            u.setPassword(password);
            em.persist(u);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
        }
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

    private List<Tarifa> loadTarifas() {
    try {
        return em.createQuery("SELECT t FROM Tarifa t", Tarifa.class).getResultList();
    } catch (Exception e) {
        return List.of();
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
