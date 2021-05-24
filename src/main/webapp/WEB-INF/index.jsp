<%-- 
    Document   : index
    Created on : May 23, 2021, 10:16:13 PM
    Author     : Chels
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
    <head>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.0.1/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-+0n0xVW2eSR5OomGNYDnhzAbDsOXxcvSN1TPprVMTNDbiYZCxYbOOl7+AMvyTG2x" crossorigin="anonymous">
        <style type="text/css">
            <%@include file="indexcss.css" %>
        </style>
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.0.1/dist/js/bootstrap.bundle.min.js" integrity="sha384-gtEjrD/SeCtmISkJkNUaaKMoLD0//ElJ19smozuHV6z3Iehds+3Ulb9Bn9Plx0x4" crossorigin="anonymous"></script>
        <script src="javascript/indexJS.js"></script>
        <title>Content Moderator Tester!</title>
    </head>
    <body>
        <div class="centerme">
            <div class="card">
                <div class="card-body">



                    <h1>Content Moderator Tester</h1>
                    <br>
                    <b>This was created using Microsoft's Content Moderator Library.</b>
                    <br>
                    <b>Please ensure you upload a photo and some text!</b>
                    <br>
                    <br>
                    <h4>Basic text</h4>
                    <form method="post" enctype="multipart/form-data">
                        <textarea rows = "5" cols = "60" class="pure-input-1-2" required name="basictext" placeholder="Basic Text"></textarea>
                        <br>
                        <br>
                        <h4>Upload Photo</h4>
                        <input type="file" class="btn btn-secondary" required name="file" />
                        <br>
                        <br>
                        <input type="submit" id="submit" class="btn btn-primary" value="Submit">
                        <br>
                    </form>

                    <br>
                    <!-- Used to display results.-->
                    <h1>Results:</h1>
                    <h4>${message}</h4>
                    <p>${basicTextTimeTaken}</p>
                    <p>${recreview}</p>
                    <p>${cat1}</p>
                    <p>${cat2}</p>
                    <p>${cat3}</p>


                    <h4>${titleImages}</h4>
                    <p>${imageTimeTaken}</p>
                    <p>${adultCont}</p>
                    <p>${imgAdultScore}</p>  
                    <p>${racyCont}</p>
                    <p>${imgRacyScore}</p> 

                    <br>
                    <p>${overallTime}</p>
                </div>
            </div>
        </div>
    </body>
</html>
