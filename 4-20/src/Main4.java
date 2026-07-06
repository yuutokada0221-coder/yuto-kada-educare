import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.io.IOException;

public class Main4 {
    public static void main(String[] args) {
        Path sourceFile = Paths.get("source.txt");
        Path backupDir = Paths.get("backup");
        Path backupFile = Paths.get("backup/source.txt");
        Path archiveDir = Paths.get("archive");
        Path archiveFile = Paths.get("archive/source.txt");

        try {
            if (!Files.exists(backupDir)) {
                Files.createDirectories(backupDir);
            }
            if (!Files.exists(archiveDir)) {
                Files.createDirectories(archiveDir);
            }

            Files.copy(sourceFile, backupFile, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("ファイルが 'backup/' にコピーされました。");

            if (Files.exists(backupFile)) {
                System.out.println("コピーの確認: 成功");
            }

            Files.move(backupFile, archiveFile, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("ファイルが 'archive/' に移動されました。");

            if (Files.exists(archiveFile)) {
                System.out.println("移動の確認: 成功");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
