import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.stream.ImageInputStream;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class ImageInfoProcessor {
    private StatusPanel statusPanel;
    private ImageInfoPanel infoPanel;
    private ExecutorService executor;
    private AtomicInteger processedFiles;
    private int totalFiles;
    private volatile boolean processing = false;

    private static final Set<String> SUPPORTED_FORMATS = Set.of(
            "jpg", "jpeg", "gif", "tif", "tiff", "bmp", "png", "pcx"
    );

    public void setStatusPanel(StatusPanel statusPanel) {
        this.statusPanel = statusPanel;
    }

    public void setInfoPanel(ImageInfoPanel infoPanel) {
        this.infoPanel = infoPanel;
    }

    public void processFolder(File folder) {
        if (processing) {
            shutdown();
        }

        processing = true;
        executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        processedFiles = new AtomicInteger(0);

        List<File> imageFiles = findImageFiles(folder);
        totalFiles = imageFiles.size();

        if (totalFiles == 0) {
            updateStatus("В выбранной папке не найдено поддерживаемых изображений", "error");
            processing = false;
            return;
        }

        updateStatus("Найдено файлов: " + totalFiles + ". Начало обработки...", "info");
        infoPanel.clearTable();

        // Обновляем прогресс в GUI потоке
        SwingUtilities.invokeLater(() -> {
            if (statusPanel != null) {
                statusPanel.updateProgress(0, totalFiles);
            }
        });

        // Запускаем все задачи
        for (File file : imageFiles) {
            executor.submit(() -> processFile(file));
        }

        // Отслеживаем завершение всех задач
        executor.submit(() -> {
            executor.shutdown();
            try {
                executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
                processing = false;
                SwingUtilities.invokeLater(() -> {
                    updateStatus("Обработка завершена: " + processedFiles.get() + " файлов", "success");
                    if (statusPanel != null) {
                        statusPanel.updateProgress(totalFiles, totalFiles);
                    }
                });
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
    }

    private List<File> findImageFiles(File folder) {
        List<File> imageFiles = new ArrayList<>();
        if (folder.isDirectory()) {
            findImageFilesRecursive(folder, imageFiles);
        }
        return imageFiles;
    }

    private void findImageFilesRecursive(File folder, List<File> imageFiles) {
        File[] files = folder.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    findImageFilesRecursive(file, imageFiles);
                } else if (file.isFile() && isSupportedFormat(file)) {
                    imageFiles.add(file);
                    if (imageFiles.size() >= 100000) { // Ограничение по заданию
                        break;
                    }
                }
            }
        }
    }

    private boolean isSupportedFormat(File file) {
        String name = file.getName().toLowerCase();
        int dotIndex = name.lastIndexOf('.');
        if (dotIndex > 0) {
            String extension = name.substring(dotIndex + 1);
            return SUPPORTED_FORMATS.contains(extension);
        }
        return false;
    }

    private void processFile(File file) {
        try {
            ImageInfo info = extractImageInfo(file);
            SwingUtilities.invokeLater(() -> {
                infoPanel.addImageInfo(info);
                int processed = processedFiles.incrementAndGet();
                updateProgress(processed, totalFiles);

                if (processed % 10 == 0 || processed == totalFiles) {
                    updateStatus("Обработано " + processed + " из " + totalFiles + " файлов", "info");
                }
            });
        } catch (Exception e) {
            System.err.println("Ошибка обработки файла " + file.getName() + ": " + e.getMessage());
            SwingUtilities.invokeLater(() -> {
                updateStatus("Ошибка обработки файла: " + file.getName(), "error");
            });
            processedFiles.incrementAndGet();
            updateProgress(processedFiles.get(), totalFiles);
        }
    }

    private ImageInfo extractImageInfo(File file) throws IOException {
        ImageInfo info = new ImageInfo();
        info.setFileName(file.getName());
        info.setFileSize(file.length());
        info.setFilePath(file.getAbsolutePath());

        String format = getFileFormat(file);
        info.setFormat(format.toUpperCase());

        try {
            // Используем ImageIO для получения базовой информации
            try (ImageInputStream iis = ImageIO.createImageInputStream(file)) {
                if (iis != null) {
                    Iterator<ImageReader> readers = ImageIO.getImageReaders(iis);
                    if (readers.hasNext()) {
                        ImageReader reader = readers.next();
                        try {
                            reader.setInput(iis);

                            info.setWidth(reader.getWidth(0));
                            info.setHeight(reader.getHeight(0));

                            // Пытаемся получить метаданные для разрешения
                            try {
                                IIOMetadata metadata = reader.getImageMetadata(0);
                                if (metadata != null) {
                                    // Извлечение разрешения из метаданных
                                    double resolution = extractResolutionFromMetadata(metadata);
                                    if (resolution > 0) {
                                        info.setResolution(resolution);
                                    }
                                }
                            } catch (Exception e) {
                                // Игнорируем ошибки метаданных
                            }

                            reader.dispose();
                        } catch (Exception e) {
                            // Если ImageIO не может прочитать, пробуем ручной парсинг
                            System.err.println("ImageIO не смог прочитать файл " + file.getName() + ": " + e.getMessage());
                        }
                    }
                }
            }

            // Дополнительная информация в зависимости от формата
            try (FileInputStream fis = new FileInputStream(file)) {
                switch (format.toLowerCase()) {
                    case "jpg":
                    case "jpeg":
                        extractJpegInfo(info, fis);
                        break;
                    case "gif":
                        extractGifInfo(info, fis);
                        break;
                    case "tif":
                    case "tiff":
                        extractTiffInfo(info, fis);
                        break;
                    case "bmp":
                        extractBmpInfo(info, fis);
                        break;
                    case "png":
                        extractPngInfo(info, fis);
                        break;
                    case "pcx":
                        extractPcxInfo(info, fis);
                        break;
                }
            }

        } catch (Exception e) {
            System.err.println("Ошибка при извлечении информации из файла " + file.getName() + ": " + e.getMessage());
            throw e;
        }

        // Если разрешение не установлено, ставим значение по умолчанию
        if (info.getResolution() <= 0) {
            info.setResolution(72.0); // Стандартное разрешение
        }

        return info;
    }

    private double extractResolutionFromMetadata(IIOMetadata metadata) {
        // Базовая реализация извлечения разрешения
        // В реальном приложении нужно парсить метаданные формата
        return 0; // Возвращаем 0, если не удалось извлечь
    }

    private String getFileFormat(File file) {
        String name = file.getName().toLowerCase();
        int dotIndex = name.lastIndexOf('.');
        if (dotIndex > 0) {
            return name.substring(dotIndex + 1);
        }
        return "unknown";
    }

    private void extractJpegInfo(ImageInfo info, FileInputStream fis) throws IOException {
        byte[] buffer = new byte[8192];
        int bytesRead = fis.read(buffer);

        if (bytesRead > 0) {
            for (int i = 0; i < bytesRead - 1; i++) {
                if (buffer[i] == (byte)0xFF) {
                    int marker = buffer[i + 1] & 0xFF;
                    // SOF0-SOF15 markers
                    if (marker >= 0xC0 && marker <= 0xCF && marker != 0xC4 && marker != 0xC8 && marker != 0xCC) {
                        if (i + 8 < bytesRead) {
                            int bitsPerPixel = buffer[i + 4] & 0xFF;
                            info.setColorDepth(bitsPerPixel);
                        }
                    }
                }
            }
        }

        if (info.getColorDepth() == 0) {
            info.setColorDepth(24); // Стандартная глубина для JPEG
        }

        info.setCompression("JPEG (с потерями)");
    }

    private void extractGifInfo(ImageInfo info, FileInputStream fis) throws IOException {
        byte[] header = new byte[13];
        if (fis.read(header) == 13) {
            // Размер из заголовка GIF
            int width = ((header[7] & 0xFF) << 8) | (header[6] & 0xFF);
            int height = ((header[9] & 0xFF) << 8) | (header[8] & 0xFF);

            // Глубина цвета
            int packed = header[10] & 0xFF;
            int colorDepth = ((packed >> 4) & 0x07) + 1;
            info.setColorDepth(colorDepth);

            // Количество цветов
            int colorTableSize = 1 << ((packed & 0x07) + 1);
            info.setAdditionalInfo("Цветов в палитре: " + colorTableSize);

            info.setCompression("LZW");
        }
    }

    private void extractTiffInfo(ImageInfo info, FileInputStream fis) throws IOException {
        byte[] header = new byte[8];
        if (fis.read(header) == 8) {
            info.setCompression("Различные методы (LZW, ZIP, без сжатия)");
            info.setColorDepth(24); // Наиболее распространено
        }
    }

    private void extractBmpInfo(ImageInfo info, FileInputStream fis) throws IOException {
        byte[] header = new byte[54];
        if (fis.read(header) == 54) {
            // Размер из заголовка BMP
            int width = readInt(header, 18, true);
            int height = Math.abs(readInt(header, 22, true)); // Берем по модулю

            // Глубина цвета
            int bitsPerPixel = readShort(header, 28, true);
            info.setColorDepth(bitsPerPixel);

            // Сжатие
            int compression = readInt(header, 30, true);
            String compressionStr = getBmpCompression(compression);
            info.setCompression(compressionStr);
        }
    }

    private void extractPngInfo(ImageInfo info, FileInputStream fis) throws IOException {
        byte[] header = new byte[33]; // Достаточно для чтения IHDR
        if (fis.read(header) == 33) {
            // Проверяем сигнатуру PNG
            if (header[0] == (byte)0x89 && header[1] == 'P' && header[2] == 'N' && header[3] == 'G') {
                // Ищем блок IHDR (позиция 12-15 после сигнатуры)
                if (header[12] == 'I' && header[13] == 'H' && header[14] == 'D' && header[15] == 'R') {
                    int width = readInt(header, 16, false);
                    int height = readInt(header, 20, false);

                    int bitDepth = header[24] & 0xFF;
                    int colorType = header[25] & 0xFF;

                    int channels = getPngChannels(colorType);
                    info.setColorDepth(bitDepth * channels);

                    info.setCompression("Deflate");
                }
            }
        }
    }

    private void extractPcxInfo(ImageInfo info, FileInputStream fis) throws IOException {
        byte[] header = new byte[128];
        if (fis.read(header) == 128) {
            // Проверяем сигнатуру PCX
            if ((header[0] & 0xFF) == 10) {
                // Размер из заголовка PCX
                int xMin = readShort(header, 4, true);
                int yMin = readShort(header, 6, true);
                int xMax = readShort(header, 8, true);
                int yMax = readShort(header, 10, true);

                int width = xMax - xMin + 1;
                int height = yMax - yMin + 1;

                // Глубина цвета
                int bitsPerPixel = header[3] & 0xFF;
                int planes = header[65] & 0xFF;
                info.setColorDepth(bitsPerPixel * planes);

                info.setCompression("RLE");
            }
        }
    }

    private int readShort(byte[] data, int offset, boolean littleEndian) {
        if (littleEndian) {
            return (data[offset] & 0xFF) | ((data[offset + 1] & 0xFF) << 8);
        } else {
            return ((data[offset] & 0xFF) << 8) | (data[offset + 1] & 0xFF);
        }
    }

    private int readInt(byte[] data, int offset, boolean littleEndian) {
        if (littleEndian) {
            return (data[offset] & 0xFF) |
                    ((data[offset + 1] & 0xFF) << 8) |
                    ((data[offset + 2] & 0xFF) << 16) |
                    ((data[offset + 3] & 0xFF) << 24);
        } else {
            return ((data[offset] & 0xFF) << 24) |
                    ((data[offset + 1] & 0xFF) << 16) |
                    ((data[offset + 2] & 0xFF) << 8) |
                    (data[offset + 3] & 0xFF);
        }
    }

    private String getBmpCompression(int compression) {
        switch (compression) {
            case 0: return "BI_RGB (без сжатия)";
            case 1: return "BI_RLE8";
            case 2: return "BI_RLE4";
            case 3: return "BI_BITFIELDS";
            case 4: return "BI_JPEG";
            case 5: return "BI_PNG";
            default: return "Неизвестно (" + compression + ")";
        }
    }

    private int getPngChannels(int colorType) {
        switch (colorType) {
            case 0: return 1; // Grayscale
            case 2: return 3; // RGB
            case 3: return 1; // Indexed
            case 4: return 2; // Grayscale + Alpha
            case 6: return 4; // RGB + Alpha
            default: return 3;
        }
    }

    private void updateStatus(String message, String type) {
        if (statusPanel != null) {
            statusPanel.updateStatus(message, type);
        }
    }

    private void updateProgress(int current, int total) {
        if (statusPanel != null) {
            statusPanel.updateProgress(current, total);
        }
    }

    public void shutdown() {
        processing = false;
        if (executor != null && !executor.isShutdown()) {
            executor.shutdownNow();
            try {
                if (!executor.awaitTermination(1, TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                executor.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }
}