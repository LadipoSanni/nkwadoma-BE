package africa.nkwadoma.nkwadoma.infrastructure.utilities;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;

public class ImageConverter {
    private static final String IMAGE ="image";

    public static String convertImageToBase64(MultipartFile image) throws MeedlException, IOException {
        if(!isImage(image))throw new MeedlException("The file is not an image");
        return Base64.getEncoder().encodeToString(image.getBytes());
    }

    private static boolean isImage(MultipartFile image) throws MeedlException {
        if(image == null) throw new MeedlException("Image is null");
        String fileType = image.getContentType();
        if(fileType == null || !fileType.toLowerCase().startsWith(IMAGE)) throw new MeedlException("This is not  valid image");
        return true;
    }
}
