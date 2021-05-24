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

/**
 *
 * @author Chels
 */
public class ContentModeratorClass {
    
    private static final String subscriptionKey = "4ab4bb465e1548a0949f7f3fac7ccdd1";
    private static final String endpoint = "https://chelseylmoderator.cognitiveservices.azure.com/";
    
    
    public ContentModeratorClass()
    {
    }

   public ContentModeratorClass(String urlString, String basicText) throws IOException
    {
        ContentModeratorClient client = ContentModeratorManager.authenticate(AzureRegionBaseUrl.fromString(endpoint),
        "a75fb58f1c8942b79c6a2986dae0e711");
        // Create a List in which to store the image moderation results.
        List<EvaluationData> evaluationData = new ArrayList<EvaluationData>();

        // Moderate URL images
        moderateImages(client, evaluationData, urlString);
        
        moderateText(client, basicText);
    }

   
   public static String moderateText(ContentModeratorClient client, String basicText) throws IOException {

    try (BufferedReader inputStream = new BufferedReader(new StringReader("test"))) {
        String line;
        Screen textResults = null;

        while ((line = inputStream.readLine()) != null) {
            if (line.length() > 0) {
                textResults = client.textModerations().screenText("text/plain", line.getBytes(), null);
            }
        }
        return (String) textResults.status().description();
    }
}
   
   public static List<EvaluationData> moderateImages(ContentModeratorClient client, List<EvaluationData> resultsList, String urlString) {
        // Evaluate each line of text
        BodyModelModel url = new BodyModelModel();
        url.withDataRepresentation("URL");
        url.withValue(urlString);
        // Save to EvaluationData class for later
        EvaluationData imageData = new EvaluationData();
        imageData.ImageUrl = url.value();
        
        return resultsList;
   }
   
    public static class EvaluationData {
        // The URL of the evaluated image.
        public String ImageUrl;
        // The image moderation results.
        public Evaluate ImageModeration;
        // The text detection results.
        public OCR TextDetection;
        // The face detection results;
        public FoundFaces FaceDetection;
    }
}
