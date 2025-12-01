import javax.swing.*;
import java.awt.*;

public class ImageInfoApp extends JFrame {
    private ImageInfoProcessor processor;
    private ImageInfoPanel infoPanel;
    private ControlPanel controlPanel;
    private StatusPanel statusPanel;

    public ImageInfoApp() {
        // Устанавливаем нативный LookAndFeel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        setTitle("Анализатор графических файлов");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Устанавливаем иконку приложения (если есть)
        try {
            ImageIcon icon = new ImageIcon("icon.png");
            setIconImage(icon.getImage());
        } catch (Exception e) {
            // Игнорируем если нет иконки
        }

        initializeComponents();
        setupLayout();

        pack();
        // Центрируем окно
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(1200, 700));

        // Устанавливаем, чтобы окно всегда было поверх
        setAlwaysOnTop(false);
    }

    private void initializeComponents() {
        processor = new ImageInfoProcessor();
        infoPanel = new ImageInfoPanel();
        controlPanel = new ControlPanel(processor, infoPanel, this);
        statusPanel = new StatusPanel();

        processor.setStatusPanel(statusPanel);
        processor.setInfoPanel(infoPanel);
    }

    private void setupLayout() {
        // Главный контейнер
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout(0, 0));
        contentPane.setBackground(new Color(245, 245, 250));

        // Верхняя панель с заголовком
        JPanel headerPanel = createHeaderPanel();

        // Центральная панель
        JPanel centerPanel = new JPanel(new BorderLayout(15, 0));
        centerPanel.setBackground(new Color(245, 245, 250));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        centerPanel.add(controlPanel, BorderLayout.WEST);
        centerPanel.add(infoPanel, BorderLayout.CENTER);

        contentPane.add(headerPanel, BorderLayout.NORTH);
        contentPane.add(centerPanel, BorderLayout.CENTER);
        contentPane.add(statusPanel, BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(new Color(60, 80, 120));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        // Заголовок приложения
        JLabel titleLabel = new JLabel("АНАЛИЗАТОР ГРАФИЧЕСКИХ ФАЙЛОВ");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Подзаголовок
        JLabel subtitleLabel = new JLabel("Поддержка форматов: JPG • GIF • TIF • BMP • PNG • PCX");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(200, 220, 255));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitleLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));

        headerPanel.add(titleLabel);
        headerPanel.add(subtitleLabel);

        return headerPanel;
    }

    public StatusPanel getStatusPanel() {
        return statusPanel;
    }
}