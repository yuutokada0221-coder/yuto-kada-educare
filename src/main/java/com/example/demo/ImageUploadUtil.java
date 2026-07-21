package com.example.demo;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

// ★画像アップロード共通のセーフガード。
// ImageIO.read(InputStream)をそのまま呼ぶと、ファイルサイズが小さくても「解凍後の解像度」が
// 巨大な画像（いわゆる展開爆弾／decompression bomb。例：数KBのPNGが65535×65535に展開され
// 数GBのメモリを食う）をそのままフルデコードしてしまう。
// 先にヘッダだけを読んで解像度を確認し、閾値を超えていればフルデコードする前に弾く。
public final class ImageUploadUtil {

    // 一般的なスマホ/一眼カメラの写真（数千万画素）にも十分な余裕を持たせた上限
    private static final long MAX_PIXELS = 40_000_000L;

    private ImageUploadUtil() {}

    public static class TooLargeException extends IOException {
        public TooLargeException(String message) { super(message); }
    }

    /** 安全であれば実データまで読み込んで返す。画像として認識できなければnull、危険サイズならTooLargeExceptionを投げる。 */
    public static BufferedImage readSafely(InputStream in) throws IOException {
        try (ImageInputStream iis = ImageIO.createImageInputStream(in)) {
            if (iis == null) return null;
            Iterator<ImageReader> readers = ImageIO.getImageReaders(iis);
            if (!readers.hasNext()) return null;

            ImageReader reader = readers.next();
            try {
                reader.setInput(iis, true, true);
                long pixels = (long) reader.getWidth(0) * (long) reader.getHeight(0);
                if (pixels > MAX_PIXELS) {
                    throw new TooLargeException("Image dimensions too large: " + pixels + " pixels");
                }
                return reader.read(0);
            } finally {
                reader.dispose();
            }
        }
    }
}
