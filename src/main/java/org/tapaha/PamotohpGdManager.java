package org.tapaha;

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.filters.Rotation;
import org.tapaha.googledrive.DriveService;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hhj on 16. 11. 23.
 */
public class PamotohpGdManager {

    public PamotohpGdManager() {}

    public static List<File> getFiles() throws IOException {
        Drive service = DriveService.getDriveService();
        List<File> result = new ArrayList<File>();

        System.out.print("Get files from google drive");

        try{
            Drive.Files.List request = service.files().list();
            do {
                System.out.print(".");
                try {
                    FileList files = request.execute();

                    result.addAll(files.getFiles());
                    request.setFields("nextPageToken, files(id, name)");
                    request.setPageSize(1000);
                    request.setPageToken(files.getNextPageToken());
                } catch(IOException e) {
                    System.out.println(e);
                }
            } while(request.getPageToken() != null && request.getPageToken().length() > 0);
        } catch(Exception e) {
            System.err.println(e);
        }
        System.out.println();

        return result;
    }

    public static File getFile(String id) throws IOException {
        Drive service = DriveService.getDriveService();

        File file = null;

        try {
            file = service.files().get(id).setFields("appProperties,contentHints,createdTime,description,fileExtension,fullFileExtension,headRevisionId,id,imageMediaMetadata,kind,md5Checksum,mimeType,name,originalFilename,parents,properties,quotaBytesUsed,size,starred,thumbnailLink,webContentLink,webViewLink,trashed,version,videoMediaMetadata").execute();
        } catch(Exception e) {
            System.err.println(e.getMessage());
        }

        return file;
    }

    public static ByteArrayOutputStream getBufferedImage(String id, double width, double height, int originalWidth, int originalHeight, int rotation) throws IOException {
        Drive service = DriveService.getDriveService();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ByteArrayOutputStream outputStream2 = new ByteArrayOutputStream();

        service.files().get(id).executeMediaAndDownloadTo(outputStream);

        if(rotation == 1) {
           Thumbnails.of(ImageIO.read(new ByteArrayInputStream(outputStream.toByteArray())))
                    .scale(height / originalHeight < 1 ? height / originalHeight : 1)
                    //.size(width, height)
                    .rotate(90)
                    .outputFormat("jpg")
                    .toOutputStream(outputStream2);
        } else {
            Thumbnails.of(ImageIO.read(new ByteArrayInputStream(outputStream.toByteArray())))
                    .scale(width / originalWidth < 1 ? width / originalWidth : 1)
                    //.size(width, height)
                    .outputFormat("jpg")
                    .toOutputStream(outputStream2);
        }

        return outputStream2;
    }
}
