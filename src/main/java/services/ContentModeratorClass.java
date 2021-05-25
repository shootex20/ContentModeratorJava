/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package services;

import com.microsoft.azure.cognitiveservices.vision.contentmoderator.*;
import com.microsoft.azure.cognitiveservices.vision.contentmoderator.models.*;

import java.io.*;
import java.util.*;
import models.EvaluationData;

/**
 *
 * @author Chels
 */
public class ContentModeratorClass {
    
    /**
     * Public constructor for content moderator class.
     */
    public ContentModeratorClass()
    {
        
    }
    
    /**
     *
     * @param client the clients connection
     * @param basicText the inserted text
     * @return the screen of the object.
     * @throws IOException IO exception.
     */
    public static Screen moderateText(ContentModeratorClient client, String basicText) throws IOException {

        try (BufferedReader inputStream = new BufferedReader(new StringReader(basicText))) {
            String line;
            Screen textResults = null;
            ScreenTextOptionalParameter para = new ScreenTextOptionalParameter();

            while ((line = inputStream.readLine()) != null) {
                if (line.length() > 0) {
                    textResults = client.textModerations().screenText("text/plain", line.getBytes(), para.withClassify(Boolean.TRUE));
                }
            }
            return textResults;
        }
    }

    /**
     *
     * @param client the clients connection
     * @param resultsList the result
     * @param urlString the url
     * @return returns the list of evaluations.
     * @throws InterruptedException interruped exception
     * @throws FileNotFoundException file not found exception
     * @throws IOException IO exception
     */
    public static List<EvaluationData> moderateImages(ContentModeratorClient client, List<EvaluationData> resultsList, String urlString) throws InterruptedException, FileNotFoundException, IOException {

        /*Converts image data to byte array Stream*/
        InputStream in = new FileInputStream(urlString);

        byte[] buff = new byte[8000];

        int bytesRead = 0;

        ByteArrayOutputStream bao = new ByteArrayOutputStream();

        while ((bytesRead = in.read(buff)) != -1) {
            bao.write(buff, 0, bytesRead);
        }

        byte[] data = bao.toByteArray();

        in.close();

        bao.close();

        EvaluationData imageData = new EvaluationData();

        // Evaluate for adult and racy content.
        imageData.ImageModeration = client.imageModerations().evaluateFileInput(data,
                new EvaluateFileInputOptionalParameter().withCacheImage(true));

        resultsList.add(imageData);

        return resultsList;

    }
}
