package aresbase.ui;

import aresbase.engine.*;
import aresbase.model.*;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.util.Random;

public class DashboardController {

    // ── Colors ───────────────────────────────────────────────────────────────
    private static final String BG_DARK      = "#0a0a0a";
    private static final String BG_PANEL     = "#141414";
    private static final String BG_HOVER     = "#1e1e1e";
    private static final String BORDER       = "#2a2a2a";
    private static final String ACCENT_AMBER = "#ffb74d";
    private static final String ACCENT_RED   = "#ef5350";
    private static final String ACCENT_GREEN = "#66bb6a";
    private static final String ACCENT_BLUE  = "#42a5f5";
    private static final String TEXT_PRIMARY   = "#e0e0e0";
    private static final String TEXT_SECONDARY = "#9e9e9e";
    private static final String MONO_FONT    = "monospace";

    // ── State ─────────────────────────────────────────────────────────────────
    private final SimulationEngine engine = new SimulationEngine();
    private final ObservableList<String> queueItems = FXCollections.observableArrayList();
    private final TextArea logArea        = new TextArea();
    private final VBox     vitalsBox      = new VBox(8);
    private final Label    creditsLabel   = new Label();
    private final Label    clockLabel     = new Label("T+00:00:00");
    private final Label    qDepthLabel    = new Label("0");
    private final Label    resolvedLabel  = new Label("0");
    private final Label    failedLabel    = new Label("0");
    private final Label    criticalLabel  = new Label("0");   // live CRITICAL count via TaskFilter
    private final Circle   statusDot      = new Circle(4);
    private int secondsTick  = 0;
    private int resolvedCount = 0;
    private int failedCount   = 0;
    private final Random rng = new Random();
    private ListView<String> queueListView;
    private BorderPane mainLayout;

    // ─────────────────────────────────────────────────────────────────────────
    public void start(Stage stage) {
        stage.setTitle("ARES-I STATION COMMAND");
        stage.setScene(createScene());
        stage.setResizable(false);
        stage.show();

        initializeSimulation();
        log("SYSTEM ONLINE - ARES STATION COMMAND ACTIVE", "SYS");
        refreshAll();
    }

    private Scene createScene() {
        mainLayout = new BorderPane();
        mainLayout.setStyle("-fx-background-color: " + BG_DARK + ";");

        VBox topSection = new VBox();
        topSection.getChildren().addAll(createHeader(), createStatBar());
        mainLayout.setTop(topSection);
        mainLayout.setCenter(createMainContent());
        mainLayout.setBottom(createFooter());

        return new Scene(mainLayout, 1024, 720);
    }

    // ── HEADER ────────────────────────────────────────────────────────────────
    private HBox createHeader() {
        HBox header = new HBox(20);
        header.setPadding(new Insets(12, 20, 12, 20));
        header.setAlignment(Pos.CENTER_LEFT);
        header.setStyle("-fx-background-color: " + BG_PANEL + "; -fx-border-color: " + BORDER + "; -fx-border-width: 0 0 1 0;");

        VBox logoBox = new VBox(2);
        Label logoMain = createLabel("▲ ARES-I STATION", ACCENT_AMBER, 14, true);
        Label logoSub  = createLabel("MARS COLONY COMMAND // SECTOR 7", TEXT_SECONDARY, 9, false);
        logoBox.getChildren().addAll(logoMain, logoSub);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox statusBox = new HBox(8);
        statusBox.setAlignment(Pos.CENTER);
        statusDot.setFill(Color.web(ACCENT_GREEN));
        Label statusLabel = createLabel("SYSTEMS NOMINAL", ACCENT_GREEN, 10, false);
        statusBox.getChildren().addAll(statusDot, statusLabel);

        VBox clockBox = new VBox(2);
        clockBox.setAlignment(Pos.CENTER_RIGHT);
        Label clockTitle = createLabel("MISSION ELAPSED TIME", TEXT_SECONDARY, 8, false);
        clockLabel.setStyle(createTextStyle(ACCENT_AMBER, 18, true));
        clockBox.getChildren().addAll(clockTitle, clockLabel);

        header.getChildren().addAll(logoBox, spacer, statusBox, clockBox);
        return header;
    }

    // ── FOOTER ────────────────────────────────────────────────────────────────
    private HBox createFooter() {
        HBox footer = new HBox();
        footer.setPadding(new Insets(8, 20, 8, 20));
        footer.setAlignment(Pos.CENTER_LEFT);
        footer.setStyle("-fx-background-color: " + BG_PANEL + "; -fx-border-color: " + BORDER + "; -fx-border-width: 1 0 0 0;");

        Label leftText  = createLabel("ARES-I // TEAM 8 // PP2 SPRING 2026", TEXT_SECONDARY, 8, false);
        Region spacer   = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Label rightText = createLabel("UNAUTHORIZED ACCESS PROHIBITED", TEXT_SECONDARY, 8, false);

        footer.getChildren().addAll(leftText, spacer, rightText);
        return footer;
    }

    // ── STAT BAR ──────────────────────────────────────────────────────────────
    private HBox createStatBar() {
        HBox statBar = new HBox();
        statBar.setPadding(new Insets(8, 20, 8, 20));
        statBar.setStyle("-fx-background-color: " + BG_PANEL + "; -fx-border-color: " + BORDER + "; -fx-border-width: 0 0 1 0;");

        Label statusValue = createLabel("OPERATIONAL", ACCENT_GREEN, 14, true);
        statBar.getChildren().addAll(
            createStatCard("QUEUE DEPTH",    qDepthLabel,   ACCENT_BLUE,  true),
            createStatCard("TASKS RESOLVED", resolvedLabel, ACCENT_GREEN, true),
            createStatCard("TASKS FAILED",   failedLabel,   ACCENT_RED,   true),
            // TaskFilter<ColonyTask> used here to keep live CRITICAL count in the stat bar
            createStatCard("CRITICAL",       criticalLabel, ACCENT_RED,   true),
            createStatCard("BASE STATUS",    statusValue,   ACCENT_GREEN, false)
        );
        return statBar;
    }

    private HBox createStatCard(String title, Label valueLabel, String color, boolean showBorder) {
        HBox card = new HBox();
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(5, 30, 5, 30));
        if (showBorder) card.setStyle("-fx-border-color: " + BORDER + "; -fx-border-width: 0 1 0 0;");
        HBox.setHgrow(card, Priority.ALWAYS);

        VBox content = new VBox(4);
        content.setAlignment(Pos.CENTER);
        Label titleLabel = createLabel(title, TEXT_SECONDARY, 9, false);
        valueLabel.setStyle(createTextStyle(color, 16, true));
        content.getChildren().addAll(titleLabel, valueLabel);
        card.getChildren().add(content);
        return card;
    }

    // ── MAIN CONTENT ──────────────────────────────────────────────────────────
    private SplitPane createMainContent() {
        SplitPane splitPane = new SplitPane();
        splitPane.setStyle("-fx-background-color: " + BG_DARK + ";");
        splitPane.setDividerPositions(0.5);

        VBox queuePanel  = createQueuePanel();
        VBox supplyPanel = createSupplyPanel();

        VBox leftSide = new VBox(10);
        leftSide.setPadding(new Insets(10));
        leftSide.getChildren().addAll(queuePanel, supplyPanel);
        VBox.setVgrow(queuePanel, Priority.ALWAYS);

        VBox statsPanel = createStatsPanel();
        VBox logPanel   = createLogPanel();

        VBox rightSide = new VBox(10);
        rightSide.setPadding(new Insets(10));
        rightSide.getChildren().addAll(statsPanel, logPanel);
        VBox.setVgrow(logPanel, Priority.ALWAYS);

        splitPane.getItems().addAll(leftSide, rightSide);
        return splitPane;
    }

    // ── QUEUE PANEL ───────────────────────────────────────────────────────────
    private VBox createQueuePanel() {
        VBox panel = createPanel("INCOMING CRISIS QUEUE", ACCENT_RED);

        queueListView = new ListView<>(queueItems);
        queueListView.setPrefHeight(280);
        queueListView.setStyle(
            "-fx-background-color: " + BG_DARK + ";" +
            "-fx-control-inner-background: " + BG_DARK + ";" +
            "-fx-border-color: " + BORDER + ";" +
            "-fx-border-width: 1;"
        );
        queueListView.setCellFactory(lv -> new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("-fx-background-color: " + BG_DARK + ";");
                    return;
                }
                setText(item);
                String color = item.contains("CRITICAL") ? ACCENT_RED
                             : item.contains("URGENT")   ? ACCENT_AMBER
                             : ACCENT_GREEN;
                setStyle(
                    "-fx-font-family: " + MONO_FONT + ";" +
                    "-fx-font-size: 11;" +
                    "-fx-text-fill: " + color + ";" +
                    "-fx-padding: 6 8 6 8;" +
                    "-fx-background-color: " + BG_DARK + ";"
                );
            }
        });

        HBox buttonBar = new HBox();
        buttonBar.setAlignment(Pos.CENTER);
        buttonBar.setPadding(new Insets(10, 0, 0, 0));

        Button executeBtn = createButton("▶ EXECUTE NEXT TASK", ACCENT_AMBER, 180);
        executeBtn.setOnAction(e -> handleExecuteTask());
        buttonBar.getChildren().add(executeBtn);

        panel.getChildren().addAll(queueListView, buttonBar);
        return panel;
    }

    // ── VITALS PANEL ──────────────────────────────────────────────────────────
    private VBox createStatsPanel() {
        VBox panel = createPanel("COLONY RESOURCE VITALS", ACCENT_GREEN);

        HBox creditsRow = new HBox(10);
        creditsRow.setAlignment(Pos.CENTER_LEFT);
        creditsRow.setPadding(new Insets(0, 0, 10, 0));
        creditsRow.setStyle("-fx-border-color: " + BORDER + "; -fx-border-width: 0 0 1 0;");

        Label creditsTitle = createLabel("BASE CREDITS:", TEXT_SECONDARY, 11, false);
        creditsLabel.setStyle(createTextStyle(ACCENT_AMBER, 20, true));
        creditsRow.getChildren().addAll(creditsTitle, creditsLabel);

        panel.getChildren().addAll(creditsRow, vitalsBox);
        return panel;
    }

    // ── SUPPLY PANEL ──────────────────────────────────────────────────────────
    private VBox createSupplyPanel() {
        VBox panel = createPanel("CARGO REPLICATOR", ACCENT_BLUE);

        Label selectLabel = createLabel("RESOURCE TYPE:", TEXT_SECONDARY, 10, false);

        ComboBox<Resource> resourceCombo = new ComboBox<>();
        resourceCombo.getItems().addAll(Resource.values());
        resourceCombo.setValue(Resource.OXYGEN);
        resourceCombo.setMaxWidth(Double.MAX_VALUE);
        resourceCombo.setStyle(
            "-fx-background-color: " + BG_DARK + ";" +
            "-fx-text-fill: " + TEXT_PRIMARY + ";" +
            "-fx-border-color: " + BORDER + ";" +
            "-fx-font-family: " + MONO_FONT + ";" +
            "-fx-font-size: 11;"
        );

        Label infoLabel = createLabel("Select resource to view cost", TEXT_SECONDARY, 9, false);

        resourceCombo.setOnAction(e -> {
            Resource r = resourceCombo.getValue();
            int amt = engine.getRestockAmount(r);
            infoLabel.setText("Synthesize +" + amt + r.unit + " "
                + r.label.toUpperCase() + " — Cost: 50 CR");
        });

        HBox buttonBar = new HBox();
        buttonBar.setAlignment(Pos.CENTER);
        buttonBar.setPadding(new Insets(10, 0, 0, 0));

        Button synthBtn = createButton("⟳ SYNTHESIZE", ACCENT_BLUE, 180);
        synthBtn.setOnAction(e -> {
            String result = engine.restock(resourceCombo.getValue());
            log(result, result.startsWith("ERROR") ? "ERR" : "OK");
            refreshVitals();
        });
        buttonBar.getChildren().add(synthBtn);

        Separator separator = new Separator();
        separator.setStyle("-fx-background-color: " + BORDER + ";");

        Label saveTitle = createLabel("CRYO-SLEEP PROTOCOL", TEXT_SECONDARY, 9, false);

        HBox saveLoadBar = new HBox(10);
        saveLoadBar.setAlignment(Pos.CENTER);
        saveLoadBar.setPadding(new Insets(5, 0, 0, 0));

        Button saveBtn = createButton("💾 SAVE STATE", ACCENT_AMBER, 120);
        Button loadBtn = createButton("📂 LOAD STATE", ACCENT_AMBER, 120);

        saveBtn.setOnAction(e -> {
            String result = engine.saveState();
            log(result, result.startsWith("ERROR") ? "ERR" : "SYS");
        });
        loadBtn.setOnAction(e -> {
            String result = engine.loadState();
            log(result, result.startsWith("ERROR") ? "ERR" : "SYS");
            refreshAll();
        });
        saveLoadBar.getChildren().addAll(saveBtn, loadBtn);

        panel.getChildren().addAll(
            selectLabel, resourceCombo, infoLabel, buttonBar,
            separator, saveTitle, saveLoadBar
        );
        return panel;
    }

    // ── LOG PANEL ─────────────────────────────────────────────────────────────
    private VBox createLogPanel() {
        VBox panel = createPanel("SYSTEM LOG", ACCENT_AMBER);

        logArea.setEditable(false);
        logArea.setWrapText(true);
        logArea.setPrefHeight(280);
        logArea.setStyle(
            "-fx-control-inner-background: " + BG_DARK + ";" +
            "-fx-text-fill: " + ACCENT_AMBER + ";" +
            "-fx-font-family: " + MONO_FONT + ";" +
            "-fx-font-size: 10;" +
            "-fx-border-color: " + BORDER + ";" +
            "-fx-border-width: 1;"
        );
        VBox.setVgrow(logArea, Priority.ALWAYS);

        Label hintLabel = createLabel("Live feed active — scroll for history", TEXT_SECONDARY, 8, false);
        hintLabel.setAlignment(Pos.CENTER);

        panel.getChildren().addAll(logArea, hintLabel);
        return panel;
    }

    // ── PANEL HELPER ──────────────────────────────────────────────────────────
    private VBox createPanel(String title, String accentColor) {
        VBox panel = new VBox(8);
        panel.setPadding(new Insets(12));
        panel.setStyle(
            "-fx-background-color: " + BG_PANEL + ";" +
            "-fx-border-color: " + BORDER + ";" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 4;" +
            "-fx-background-radius: 4;"
        );

        Label titleLabel = createLabel(title, accentColor, 11, true);
        titleLabel.setPadding(new Insets(0, 0, 5, 0));
        titleLabel.setStyle(titleLabel.getStyle()
            + "-fx-border-color: " + accentColor + "; -fx-border-width: 0 0 1 0;");

        panel.getChildren().add(titleLabel);
        return panel;
    }

    // ── SIMULATION ────────────────────────────────────────────────────────────
    private void initializeSimulation() {
        int[] nextIn = {2 + rng.nextInt(4)};
        Timeline clock = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            secondsTick++;
            int h = secondsTick / 3600;
            int m = (secondsTick % 3600) / 60;
            int s = secondsTick % 60;
            clockLabel.setText(String.format("T+%02d:%02d:%02d", h, m, s));

            nextIn[0]--;
            if (nextIn[0] <= 0) {
                ColonyTask task = TaskGenerator.generate();
                engine.addTask(task);
                log(task.getSeverity() + " // " + task.getName()
                    + " [" + task.getTaskType() + "] -> " + task.getProcessorType(), "WARN");
                refreshQueue();
                nextIn[0] = 2 + rng.nextInt(4);
            }
        }));
        clock.setCycleCount(Timeline.INDEFINITE);
        clock.play();

        Timeline blink = new Timeline(
            new KeyFrame(Duration.seconds(1), e -> statusDot.setFill(Color.web(ACCENT_AMBER))),
            new KeyFrame(Duration.seconds(2), e -> statusDot.setFill(Color.web(ACCENT_GREEN)))
        );
        blink.setCycleCount(Timeline.INDEFINITE);
        blink.play();
    }

    // ── EXECUTE ───────────────────────────────────────────────────────────────
    private void handleExecuteTask() {
        String result = engine.executeNext();
        if (result.startsWith("ERROR")) {
            failedCount++;
            failedLabel.setText(String.valueOf(failedCount));
        } else if (result.startsWith("OK")) {
            resolvedCount++;
            resolvedLabel.setText(String.valueOf(resolvedCount));
        }
        String type = result.startsWith("ERROR") ? "ERR"
                    : result.startsWith("OK")    ? "OK"
                    : "SYS";
        int colonIdx = result.indexOf(':');
        String msg = colonIdx >= 0 ? result.substring(colonIdx + 2) : result;
        log(msg, type);
        refreshQueue();
        refreshVitals();
    }

    // ── REFRESH ───────────────────────────────────────────────────────────────
    private void refreshQueue() {
        Platform.runLater(() -> {
            queueItems.clear();
            engine.getTaskQueue().forEach(task -> {
                StringBuilder reqs = new StringBuilder();
                task.getRequirements().forEach((res, amt) -> {
                    if (reqs.length() > 0) reqs.append(" | ");
                    reqs.append(amt).append(res.unit).append(" ").append(res.label.toUpperCase());
                });
                queueItems.add(String.format("[%s] %s — %s",
                    task.getSeverity(), task.getName(), reqs));
            });
            qDepthLabel.setText(String.valueOf(queueItems.size()));

            // Use TaskFilter<ColonyTask> to count CRITICAL tasks without touching raw queue logic
            long criticalCount = engine.getQueueFilter()
                    .count(t -> t.getSeverity().equals("CRITICAL"));
            criticalLabel.setText(String.valueOf(criticalCount));
        });
    }

    private void refreshVitals() {
        Platform.runLater(() -> {
            creditsLabel.setText(String.format("%,d", engine.getResourceManager().getCredits()));
            vitalsBox.getChildren().clear();

            for (Resource r : Resource.values()) {
                int amount  = engine.getResourceManager().getAmount(r);
                double pct  = (double) amount / r.max;

                HBox row = new HBox(10);
                row.setAlignment(Pos.CENTER_LEFT);

                Label nameLabel = createLabel(r.label.toUpperCase(), TEXT_SECONDARY, 10, false);
                nameLabel.setPrefWidth(80);

                Label amountLabel = createLabel(amount + r.unit, TEXT_PRIMARY, 11, true);
                amountLabel.setPrefWidth(60);

                Label percentLabel = createLabel(String.format("%.0f%%", pct * 100), TEXT_SECONDARY, 9, false);
                percentLabel.setPrefWidth(40);

                Region spacer = new Region();
                HBox.setHgrow(spacer, Priority.ALWAYS);

                HBox progressBar = new HBox(1);
                progressBar.setMaxWidth(200);
                int segs   = 20;
                int filled = (int) (pct * segs);
                String barColor = pct > 0.6 ? ACCENT_GREEN : pct > 0.3 ? ACCENT_AMBER : ACCENT_RED;

                for (int i = 0; i < segs; i++) {
                    Rectangle seg = new Rectangle(8, 6);
                    seg.setFill(i < filled ? Color.web(barColor) : Color.web(BORDER));
                    progressBar.getChildren().add(seg);
                }

                row.getChildren().addAll(nameLabel, amountLabel, spacer, percentLabel, progressBar);
                vitalsBox.getChildren().add(row);
            }
        });
    }

    private void refreshAll() {
        refreshQueue();
        refreshVitals();
    }

    // ── LOG ───────────────────────────────────────────────────────────────────
    private void log(String message, String type) {
        Platform.runLater(() -> {
            String timestamp = String.format("%06d", secondsTick);
            String tag = switch (type) {
                case "OK"   -> "[ OK ]";
                case "ERR"  -> "[ERR!]";
                case "WARN" -> "[WARN]";
                default     -> "[SYS ]";
            };
            logArea.appendText(String.format("T+%s %s %s%n", timestamp, tag, message));
            logArea.setScrollTop(Double.MAX_VALUE);
        });
    }

    // ── HELPERS ───────────────────────────────────────────────────────────────
    private Label createLabel(String text, String color, int size, boolean bold) {
        Label label = new Label(text);
        label.setStyle(createTextStyle(color, size, bold));
        return label;
    }

    private String createTextStyle(String color, int size, boolean bold) {
        return String.format(
            "-fx-text-fill: %s; -fx-font-family: %s; -fx-font-size: %d;%s",
            color, MONO_FONT, size, bold ? " -fx-font-weight: bold;" : ""
        );
    }

    private Button createButton(String text, String color, int width) {
        Button button = new Button(text);
        button.setPrefWidth(width);
        button.setMaxWidth(width);
        button.setMinWidth(width);
        button.setStyle(
            "-fx-background-color: " + BG_PANEL + ";" +
            "-fx-text-fill: " + color + ";" +
            "-fx-border-color: " + color + ";" +
            "-fx-border-width: 1;" +
            "-fx-font-family: " + MONO_FONT + ";" +
            "-fx-font-size: 11;" +
            "-fx-font-weight: bold;" +
            "-fx-cursor: hand;" +
            "-fx-padding: 6 12 6 12;"
        );
        button.setOnMouseEntered(e -> button.setStyle(
            button.getStyle().replace("-fx-background-color: " + BG_PANEL,
                                      "-fx-background-color: " + BG_HOVER)));
        button.setOnMouseExited(e -> button.setStyle(
            button.getStyle().replace("-fx-background-color: " + BG_HOVER,
                                      "-fx-background-color: " + BG_PANEL)));
        return button;
    }
}