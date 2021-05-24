package servlets;

import java.io.IOException;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.microsoft.azure.cognitiveservices.vision.contentmoderator.*;
import com.microsoft.azure.cognitiveservices.vision.contentmoderator.models.*;

import java.io.*;

import java.util.*;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.Part;

/**
 *
 * @author Chels
 */
@WebServlet(name = "IndexServlet", urlPatterns = {"/IndexServlet"})
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024 * 1, // 1 MB
        maxFileSize = 1024 * 1024 * 10, // 10 MB
        maxRequestSize = 1024 * 1024 * 100 // 100 MB
)
public class IndexServlet extends HttpServlet {

    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        getServletContext().getRequestDispatcher("/WEB-INF/index.jsp").forward(request, response);
    }

    private static final String subscriptionKey = "4ab4bb465e1548a0949f7f3fac7ccdd1";
    private static final String endpoint = "https://westus.api.cognitive.microsoft.com/";

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String basicText = request.getParameter("basictext");

        Part filePart = request.getPart("file");
        String fileName = filePart.getSubmittedFileName();

        ContentModeratorClient client = ContentModeratorManager.authenticate(AzureRegionBaseUrl.fromString(endpoint),
                subscriptionKey);

        List<EvaluationData> evaluationData = new ArrayList<EvaluationData>();

        if (basicText != null || basicText.isEmpty() == false) {
            long start = System.currentTimeMillis();
            Screen textResults = moderateText(client, basicText);
            request.setAttribute("message", "Text results: ");
            request.setAttribute("recreview", "Recommended Review: " + textResults.classification().reviewRecommended());
            request.setAttribute("cat1", "Category 1 Score: " + textResults.classification().category1().score());
            request.setAttribute("cat2", "Category 2 Score: " + textResults.classification().category2().score());
            request.setAttribute("cat3", "Category 3 Score: " + textResults.classification().category3().score());
            long end = System.currentTimeMillis();
            long elapsedTime = end - start;
            request.setAttribute("basicTextTimeTaken", "Time taken(In Miliseconds): " + elapsedTime);

            List<EvaluationData> resultList = new ArrayList<EvaluationData>();

            File uploadDirectory = new File(request.getSession().getServletContext().getRealPath("/WEB-INF/images"));

            if (!uploadDirectory.exists()) {
                uploadDirectory.mkdirs();
            }

            String imgURL = request.getSession().getServletContext().getRealPath("/WEB-INF/images/") + fileName;

            for (Part part : request.getParts()) {
                part.write(request.getSession().getServletContext().getRealPath("/WEB-INF/images/") + fileName);
            }
            try {

                long startImg = System.currentTimeMillis();
                resultList = moderateImages(client, resultList, imgURL);
                long endImg = System.currentTimeMillis();
                long elapsedTimeImg = endImg - startImg;
                request.setAttribute("titleImages", "Image resutls: ");
                request.setAttribute("imageTimeTaken", "Time taken(In Miliseconds): " + elapsedTimeImg);
                request.setAttribute("adultCont", "Adult Classification: " + resultList.get(0).ImageModeration.isImageAdultClassified());
                request.setAttribute("imgAdultScore", "Adult Score: " + resultList.get(0).ImageModeration.adultClassificationScore());
                request.setAttribute("racyCont", "Racy Classification: " + resultList.get(0).ImageModeration.isImageRacyClassified());
                request.setAttribute("imgRacyScore", "Racy Score: " + resultList.get(0).ImageModeration.racyClassificationScore());

                request.setAttribute("overallTime", "Overall time(In Miliseconds): " + (elapsedTimeImg + elapsedTime));
                doGet(request, response);
            } catch (InterruptedException ex) {
                Logger.getLogger(IndexServlet.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

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
