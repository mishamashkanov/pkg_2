import javax.swing.*;
import java.awt.*;

public class StatusPanel extends JPanel {
    private JLabel statusLabel;
    private JProgressBar progressBar;
    private JLabel progressLabel;

    public StatusPanel() {
        initializeComponents();
        setupLayout();
    }

    private void initializeComponents() {
        setBackground(new Color(60, 80, 120));
        setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        // МЕТКА СТАТУСА - БОЛЬШАЯ и ЧЕТКАЯ
        statusLabel = new JLabel("ГОТОВ К РАБОТЕ");
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        statusLabel.setForeground(Color.WHITE);

        // ПРОГРЕСС-БАР - ЯРКИЙ
        progressBar = new JProgressBar();
        progressBar.setVisible(false);
        progressBar.setStringPainted(true);
        progressBar.setFont(new Font("Segoe UI", Font.BOLD, 11));
        progressBar.setForeground(new Color(65, 105, 225));
        progressBar.setBackground(new Color(230, 230, 240));
        progressBar.setBorder(BorderFactory.createLineBorder(new Color(40, 60, 100), 1));

        // МЕТКА ПРОГРЕССА - ЧЕТКАЯ
        progressLabel = new JLabel("");
        progressLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        progressLabel.setForeground(new Color(200, 220, 255));
    }

    private void setupLayout() {
        setLayout(new BorderLayout(10, 0));

        add(statusLabel, BorderLayout.WEST);
        add(progressBar, BorderLayout.CENTER);
        add(progressLabel, BorderLayout.EAST);
    }

    public void updateStatus(String message, String type) {
        Color color;
        switch (type) {
            case "error":
                color = new Color(255, 100, 100);
                break;
            case "warning":
                color = new Color(255, 200, 50);
                break;
            case "success":
                color = new Color(100, 255, 100);
                break;
            case "info":
            default:
                color = Color.WHITE;
                break;
        }

        statusLabel.setForeground(color);
        statusLabel.setText("СТАТУС: " + message.toUpperCase());
    }

    public void updateProgress(int current, int total) {
        if (total > 0) {
            int percent = (current * 100) / total;
            progressBar.setVisible(true);
            progressBar.setValue(percent);
            progressBar.setString(current + " / " + total + " (" + percent + "%)");
            progressLabel.setText("ВЫПОЛНЕНО: " + current + " из " + total);
        } else {
            progressBar.setVisible(false);
            progressLabel.setText("");
        }
    }

    public void clearProgress() {
        progressBar.setVisible(false);
        progressLabel.setText("");
    }
}