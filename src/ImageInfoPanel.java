import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ImageInfoPanel extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private List<ImageInfo> imageInfoList;
    private JLabel countLabel;

    public ImageInfoPanel() {
        initializeComponents();
        setupLayout();
    }

    private void initializeComponents() {
        setBackground(new Color(240, 240, 245));
        imageInfoList = new ArrayList<>();

        // Модель таблицы
        String[] columns = {
                "Имя файла", "Формат", "Размер файла", "Разрешение (dpi)",
                "Размеры (пикс.)", "Глубина цвета", "Сжатие", "Доп. информация"
        };

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        setupTable();

        // Метка для отображения количества записей
        countLabel = new JLabel("Файлов: 0");
        countLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        countLabel.setForeground(new Color(60, 60, 100));
    }

    private void setupTable() {
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.setRowHeight(30);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setAutoCreateRowSorter(true);
        table.setShowGrid(true);
        table.setGridColor(new Color(200, 200, 220));

        // НАСТРОЙКА ЗАГОЛОВКА - ИСПРАВЛЯЕМ ПРОБЛЕМУ ВИДИМОСТИ
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(new Color(30, 60, 120));  // Темно-синий фон
        header.setForeground(Color.WHITE);  // Белый текст
        header.setReorderingAllowed(false);

        // Увеличиваем высоту заголовка и добавляем отступы
        header.setPreferredSize(new Dimension(header.getWidth(), 40));

        // Делаем границы заголовка более заметными
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(20, 40, 90), 2),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));

        // ЯВНЫЙ рендерер для заголовков
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);

                label.setBackground(new Color(30, 60, 120));
                label.setForeground(Color.WHITE);
                label.setFont(new Font("Segoe UI", Font.BOLD, 13));
                label.setHorizontalAlignment(SwingConstants.CENTER);
                label.setBorder(BorderFactory.createEmptyBorder(8, 5, 8, 5));
                label.setOpaque(true);

                return label;
            }
        });

        // Настройка рендерера для ячеек таблицы
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value,
                        isSelected, hasFocus, row, column);

                // Альтернативные цвета строк
                if (!isSelected) {
                    if (row % 2 == 0) {
                        c.setBackground(Color.WHITE);
                    } else {
                        c.setBackground(new Color(245, 248, 255));
                    }
                    c.setForeground(Color.BLACK);
                } else {
                    c.setBackground(new Color(200, 220, 255));
                    c.setForeground(Color.BLACK);
                }

                // Выравнивание по центру для числовых колонок
                if (column == 1 || column == 2 || column == 3 || column == 4 || column == 5) {
                    setHorizontalAlignment(SwingConstants.CENTER);
                } else {
                    setHorizontalAlignment(SwingConstants.LEFT);
                }

                // Добавляем отступы для лучшей читаемости
                setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 240)),
                        BorderFactory.createEmptyBorder(5, 10, 5, 10)
                ));

                return c;
            }
        });

        // Устанавливаем ширину колонок
        table.getColumnModel().getColumn(0).setPreferredWidth(200); // Имя файла
        table.getColumnModel().getColumn(1).setPreferredWidth(80);  // Формат
        table.getColumnModel().getColumn(2).setPreferredWidth(100); // Размер файла
        table.getColumnModel().getColumn(3).setPreferredWidth(100); // Разрешение
        table.getColumnModel().getColumn(4).setPreferredWidth(120); // Размеры
        table.getColumnModel().getColumn(5).setPreferredWidth(100); // Глубина цвета
        table.getColumnModel().getColumn(6).setPreferredWidth(150); // Сжатие
        table.getColumnModel().getColumn(7).setPreferredWidth(200); // Доп. информация
    }

    private void setupLayout() {
        setLayout(new BorderLayout(0, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Заголовок панели
        JLabel titleLabel = new JLabel("Информация о графических файлах");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(new Color(30, 50, 100));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        // Панель прокрутки с заметными границами
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(100, 120, 180), 2),
                BorderFactory.createEmptyBorder(2, 2, 2, 2)
        ));
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        // Панель с количеством записей
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(new Color(240, 240, 245));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 5));
        bottomPanel.add(countLabel, BorderLayout.WEST);

        // Панель информации о таблице
        JPanel infoPanel = new JPanel();
        infoPanel.setBackground(new Color(240, 240, 245));
        infoPanel.add(new JLabel("Нажмите на заголовок для сортировки"));
        infoPanel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        bottomPanel.add(infoPanel, BorderLayout.EAST);

        add(titleLabel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    public void addImageInfo(ImageInfo info) {
        imageInfoList.add(info);

        SwingUtilities.invokeLater(() -> {
            tableModel.addRow(new Object[]{
                    info.getFileName(),
                    info.getFormat(),
                    info.getSizeFormatted(),
                    info.getResolutionFormatted(),
                    info.getDimensions(),
                    info.getColorDepthFormatted(),
                    info.getCompression(),
                    info.getAdditionalInfo()
            });
            updateCountLabel();
        });
    }

    public void clearTable() {
        imageInfoList.clear();
        tableModel.setRowCount(0);
        updateCountLabel();
    }

    private void updateCountLabel() {
        countLabel.setText("Файлов в таблице: " + imageInfoList.size());
    }

    public List<ImageInfo> getImageInfoList() {
        return new ArrayList<>(imageInfoList);
    }
}