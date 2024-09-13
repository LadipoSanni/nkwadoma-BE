package africa.nkwadoma.nkwadoma.infrastructure.utilities;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class ImageConverterTest {
    private ImageConverter imageConverter;

   @Test
    void convertImage(){
       String image ="";
       imageConverter = new ImageConverter();
       imageConverter.convertImage();
   }
}