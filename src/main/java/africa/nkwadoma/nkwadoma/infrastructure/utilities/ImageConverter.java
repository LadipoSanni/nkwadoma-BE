package africa.nkwadoma.nkwadoma.infrastructure.utilities;

import africa.nkwadoma.nkwadoma.infrastructure.exceptions.ImageConverterException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;

public class ImageConverter {
    private static final String IMAGE ="image";

    public static String convertImageToBase64(MultipartFile image) throws ImageConverterException, IOException {
        if(!isImage(image))throw new ImageConverterException("The file is not an image");
        return Base64.getEncoder().encodeToString(image.getBytes());
    }

    private static boolean isImage(MultipartFile image) throws ImageConverterException {
        if(image == null) throw new ImageConverterException("Image is null");
        String fileType = image.getContentType();
        if(fileType == null || !fileType.toLowerCase().startsWith(IMAGE)) throw new ImageConverterException("This is not  valid image");
        return true;
    }
}
