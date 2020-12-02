package com.nickbernard.finaltermproject;


import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;

@Controller
public class WebsiteController {

    @Value("${accessKey}")
    String accesskey;
    @Value("${secretKey}")
    String secretkey;
    @Value("${bucketName}")
    String bucketName;

    @Autowired
    private UserRepo userRepo;




    @GetMapping("/")
    public String renderHomePage(){
        return "index";
    }


    @GetMapping("/artworks")
    public ModelAndView renderArtworksPage(){

        return new ModelAndView("artworks");
    }


    ///////////
    // LOGIN //
    ///////////

    @GetMapping("/login")
    public ModelAndView renderLoginPage(@RequestParam(name="name", required=false, defaultValue="world") String name){

        System.out.println("In login() controller");
        ModelAndView returnPage = new ModelAndView();
        String s = new String("Login " + name);
        returnPage.addObject("loginText", s);

        return returnPage;
    }

    // Takes and saves info from the form
    @PostMapping("/saveInfo")
    public String login(
            @RequestParam(name="fname", required=true) String fname,
            @RequestParam(name="lname", required=true) String lname,
            Model view)
    {
        System.out.println(fname + " " + lname);

        ////////////////////////
        // Save to database!! //
        ////////////////////////

        view.addAttribute("name", fname + " " + lname);


        return "saveConfirmation";
    }


    @PostMapping(value = "/add")
    public ModelAndView registerNewUser(@RequestParam("Photo") MultipartFile image,
                                        @RequestParam(name = "Name") String name,
                                        @RequestParam(name = "Username") String username,
                                        @RequestParam(name = "Password") String password,
                                        @RequestParam(name = "Bio") String bio
    )
    {
        ModelAndView returnPage = new ModelAndView();
        System.out.println("description      " + name);
        System.out.println(image.getOriginalFilename());

        BasicAWSCredentials cred = new BasicAWSCredentials(accesskey, secretkey);
        // AmazonS3Client client=AmazonS3ClientBuilder.standard().withCredentials(new
        // AWSCredentialsProvider(cred)).with
        AmazonS3 client = AmazonS3ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(cred))
                .withRegion(Regions.US_EAST_1).build();

        try {
            PutObjectRequest put = new PutObjectRequest(bucketName, image.getOriginalFilename(),
                    image.getInputStream(), new ObjectMetadata()).withCannedAcl(CannedAccessControlList.PublicRead);
            client.putObject(put);

            String imgSrc = "http://" + bucketName + ".s3.amazonaws.com/" + image.getOriginalFilename();

            returnPage.setViewName("showAddedToDB");
            returnPage.addObject("name", name);
            returnPage.addObject("username", username);
            returnPage.addObject("bio", bio);
            returnPage.addObject("imgSrc", imgSrc);

            //Save this in the DB.
            User user = new User();
            user.setName(name);
            user.setUsername(username);


            //Save this in the DB.

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            returnPage.setViewName("error");
        }

        return returnPage;
    }



}
