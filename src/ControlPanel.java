import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.io.File;

public class ControlPanel extends JPanel {
    private ImageInfoProcessor processor;
    private ImageInfoPanel infoPanel;
    private ImageInfoApp app;

    private JButton selectFolderBtn;
    private JButton stopBtn;
    private JTextField folderPathField;
    private JLabel fileCountLabel;
    private JLabel supportedFormatsLabel;

    public ControlPanel(ImageInfoProcessor processor, ImageInfoPanel infoPanel, ImageInfoApp app) {
        this.processor = processor;
        this.infoPanel = infoPanel;
        this.app = app;
        initializeComponents();
        setupLayout();
        setupListeners();
    }

    private void initializeComponents() {
        setBackground(new Color(255, 255, 255));
        setPreferredSize(new Dimension(320, 0));

        // Кнопка выбора папки - СИНЯЯ и КРУПНАЯ
        selectFolderBtn = new JButton("📁 ВЫБРАТЬ ПАПКУ");
        selectFolderBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        selectFolderBtn.setBackground(new Color(65, 105, 225));
        selectFolderBtn.setForeground(Color.WHITE);
        selectFolderBtn.setFocusPainted(false);
        selectFolderBtn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(30, 70, 180), 2),
                BorderFactory.createEmptyBorder(12, 25, 12, 25)
        ));
        selectFolderBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Кнопка остановки - КРАСНАЯ и КРУПНАЯ
        stopBtn = new JButton("⏹ ОСТАНОВИТЬ");
        stopBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        stopBtn.setBackground(new Color(220, 60, 50));
        stopBtn.setForeground(Color.WHITE);
        stopBtn.setFocusPainted(false);
        stopBtn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 40, 30), 2),
                BorderFactory.createEmptyBorder(12, 25, 12, 25)
        ));
        stopBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        stopBtn.setEnabled(false);

        // Поле пути - ЧЕТКОЕ и ЯСНОЕ
        folderPathField = new JTextField();
        folderPathField.setEditable(false);
        folderPathField.setBackground(Color.WHITE);
        folderPathField.setForeground(Color.BLACK);
        folderPathField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        folderPathField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(100, 100, 150), 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        // Метка количества файлов - КРУПНЫЙ ШРИФТ
        fileCountLabel = new JLabel("Выберите папку для анализа");
        fileCountLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        fileCountLabel.setForeground(new Color(40, 40, 80));

        // Метка поддерживаемых форматов - ОЧЕНЬ ЧЕТКАЯ
        supportedFormatsLabel = new JLabel("<html><div style='text-align: center;'>"
                + "<b style='color: #2E5E8F; font-size: 14px;'>ПОДДЕРЖИВАЕМЫЕ ФОРМАТЫ:</b><br>"
                + "<span style='color: #555555; font-size: 12px; line-height: 1.8;'>"
                + "✓ JPG / JPEG<br>"
                + "✓ GIF<br>"
                + "✓ TIF / TIFF<br>"
                + "✓ BMP<br>"
                + "✓ PNG<br>"
                + "✓ PCX</span></div></html>");
    }

    private void setupLayout() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        // Граница панели - ЧЕТКАЯ
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(100, 120, 180), 2),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        // Панель выбора папки - ВЫДЕЛЕНА
        JPanel folderPanel = new JPanel(new BorderLayout(5, 5));
        folderPanel.setBackground(new Color(240, 245, 255));
        folderPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 210, 230), 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        JLabel folderLabel = new JLabel("ПАПКА С ИЗОБРАЖЕНИЯМИ:");
        folderLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        folderLabel.setForeground(new Color(50, 70, 120));

        folderPanel.add(folderLabel, BorderLayout.NORTH);
        folderPanel.add(folderPathField, BorderLayout.CENTER);
        folderPanel.add(Box.createVerticalStrut(10), BorderLayout.SOUTH);

        // Панель статистики - ВЫДЕЛЕНА
        JPanel statsPanel = new JPanel(new BorderLayout());
        statsPanel.setBackground(new Color(240, 245, 255));
        statsPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 210, 230), 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        statsPanel.add(fileCountLabel, BorderLayout.CENTER);

        // Панель форматов - ВЫДЕЛЕНА
        JPanel formatsPanel = new JPanel(new BorderLayout());
        formatsPanel.setBackground(new Color(240, 245, 255));
        formatsPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 210, 230), 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        formatsPanel.add(supportedFormatsLabel, BorderLayout.CENTER);

        // Сборка всех компонентов
        add(folderPanel);
        add(Box.createVerticalStrut(15));
        add(statsPanel);
        add(Box.createVerticalStrut(15));
        add(formatsPanel);
        add(Box.createVerticalStrut(20));
        add(selectFolderBtn);
        add(Box.createVerticalStrut(10));
        add(stopBtn);
        add(Box.createVerticalGlue());
    }

    private void setupListeners() {
        selectFolderBtn.addActionListener(e -> selectFolder());
        stopBtn.addActionListener(e -> stopProcessing());

        // Эффекты наведения для кнопок
        selectFolderBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                selectFolderBtn.setBackground(new Color(30, 70, 180));
                selectFolderBtn.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(20, 50, 150), 3),
                        BorderFactory.createEmptyBorder(11, 24, 11, 24)
                ));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                selectFolderBtn.setBackground(new Color(65, 105, 225));
                selectFolderBtn.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(30, 70, 180), 2),
                        BorderFactory.createEmptyBorder(12, 25, 12, 25)
                ));
            }
        });

        stopBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                stopBtn.setBackground(new Color(200, 40, 30));
                stopBtn.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(170, 30, 20), 3),
                        BorderFactory.createEmptyBorder(11, 24, 11, 24)
                ));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                stopBtn.setBackground(new Color(220, 60, 50));
                stopBtn.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(180, 40, 30), 2),
                        BorderFactory.createEmptyBorder(12, 25, 12, 25)
                ));
            }
        });
    }

    private void selectFolder() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setDialogTitle("ВЫБЕРИТЕ ПАПКУ С ИЗОБРАЖЕНИЯМИ");
        fileChooser.setApproveButtonText("ВЫБРАТЬ");
        fileChooser.setApproveButtonToolTipText("Выбрать эту папку для анализа");

        // Стилизуем диалог
        if (UIManager.getLookAndFeel() != null) {
            fileChooser.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        }

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFolder = fileChooser.getSelectedFile();
            folderPathField.setText(selectedFolder.getAbsolutePath());
            fileCountLabel.setText("<html><b>СТАТУС:</b> Обработка...</html>");
            stopBtn.setEnabled(true);
            selectFolderBtn.setEnabled(false);

            // Запуск обработки
            new Thread(() -> {
                processor.processFolder(selectedFolder);
                SwingUtilities.invokeLater(() -> {
                    selectFolderBtn.setEnabled(true);
                });
            }).start();
        }
    }

    private void stopProcessing() {
        processor.shutdown();
        stopBtn.setEnabled(false);
        fileCountLabel.setText("<html><b>СТАТУС:</b> Остановлено пользователем</html>");
        app.getStatusPanel().updateStatus("Обработка остановлена", "warning");
    }

    public void updateFileCount(int count) {
        SwingUtilities.invokeLater(() -> {
            fileCountLabel.setText("<html><b>НАЙДЕНО ФАЙЛОВ:</b> " + count + "</html>");
        });
    }

    public void processingComplete(int total) {
        SwingUtilities.invokeLater(() -> {
            stopBtn.setEnabled(false);
            fileCountLabel.setText("<html><b>ОБРАБОТАНО:</b> " + total + " файлов</html>");
        });
    }
}