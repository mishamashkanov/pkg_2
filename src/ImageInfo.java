public class ImageInfo {
    private String fileName;
    private String filePath;
    private long fileSize;
    private String format;
    private int width;
    private int height;
    private double resolution;
    private int colorDepth;
    private String compression;
    private String additionalInfo;

    // Конструкторы
    public ImageInfo() {}

    // Геттеры и сеттеры
    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }

    public long getFileSize() { return fileSize; }
    public void setFileSize(long fileSize) { this.fileSize = fileSize; }

    public String getFormat() { return format; }
    public void setFormat(String format) { this.format = format; }

    public int getWidth() { return width; }
    public void setWidth(int width) { this.width = width; }

    public int getHeight() { return height; }
    public void setHeight(int height) { this.height = height; }

    public double getResolution() { return resolution; }
    public void setResolution(double resolution) { this.resolution = resolution; }

    public int getColorDepth() { return colorDepth; }
    public void setColorDepth(int colorDepth) { this.colorDepth = colorDepth; }

    public String getCompression() { return compression; }
    public void setCompression(String compression) { this.compression = compression; }

    public String getAdditionalInfo() { return additionalInfo; }
    public void setAdditionalInfo(String additionalInfo) { this.additionalInfo = additionalInfo; }

    public String getDimensions() {
        return width + " × " + height + " px";
    }

    public String getSizeFormatted() {
        if (fileSize < 1024) {
            return fileSize + " B";
        } else if (fileSize < 1024 * 1024) {
            return String.format("%.1f KB", fileSize / 1024.0);
        } else {
            return String.format("%.1f MB", fileSize / (1024.0 * 1024.0));
        }
    }

    public String getResolutionFormatted() {
        if (resolution > 0) {
            return String.format("%.1f dpi", resolution);
        } else {
            return "Не указано";
        }
    }

    public String getColorDepthFormatted() {
        return colorDepth + " бит";
    }
}