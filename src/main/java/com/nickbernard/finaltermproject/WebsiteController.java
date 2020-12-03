package com.nickbernard.finaltermproject;


import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.net.URL;
import java.net.URLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import java.io.IOException;
import java.util.Optional;

import org.json.JSONObject;
import org.json.JSONArray;


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
    public ModelAndView renderHomePage() {

        ModelAndView modelAndView = getPageWithWeatherInfo();

        modelAndView.setViewName("index");

        return modelAndView;
    }


    @GetMapping("/about")
    public ModelAndView renderAboutPage() {

        ModelAndView modelAndView = getPageWithWeatherInfo();

        modelAndView.setViewName("about");

        return modelAndView;
    }


    @GetMapping("/artworks")
    public ModelAndView renderArtworksPage() {

        ModelAndView modelAndView = getPageWithWeatherInfo();

        modelAndView.setViewName("artworks");

        return modelAndView;
    }


    @GetMapping("/register")
    public ModelAndView renderRegistrationPage() {

        ModelAndView modelAndView = getPageWithWeatherInfo();

        modelAndView.setViewName("register");

        return modelAndView;
    }

    @GetMapping("/profile")
    public ModelAndView renderProfilePage() {

        // Fetch the image from database
        // add object to the view

        // return the view

        ModelAndView modelAndView = getPageWithWeatherInfo();

        modelAndView.setViewName("profile");

        return modelAndView;
    }

    ///////////
    // LOGIN //
    ///////////

    @GetMapping("/login")
    public ModelAndView renderLoginPage(@RequestParam(name = "name", required = false, defaultValue = "world") String name) {

        System.out.println("In login() controller");
        ModelAndView returnPage = getPageWithWeatherInfo();
        returnPage.setViewName("login");

        String s = new String("Login " + name);
        returnPage.addObject("loginText", s);

        return returnPage;
    }



    @PostMapping(path="/loginUserValidation")
    public ModelAndView loginUserValidation(@RequestParam("username") String username, @RequestParam("password") String password){

        ModelAndView modelAndView = getPageWithWeatherInfo();
        //modelAndView.setViewName("validating");

        User user = userRepo.findByUsername(username);

        // Test to see if that user exists in the database
        if ((user.getUsername().equals(username)) && (user.getPassword().equals(password))){

            modelAndView.setViewName("confirmed");
            // Get the rest of the stuff from the database to display on the page

        }


        return modelAndView;
    }


    // Takes and saves info from the form
    @PostMapping("/saveInfo")
    public String login(
            @RequestParam(name = "fname", required = true) String fname,
            @RequestParam(name = "lname", required = true) String lname,
            Model view) {
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
    ) {
        ModelAndView returnPage = getPageWithWeatherInfo();
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
            user.setPassword(password);
            user.setBio(bio);
            user.setImageUrl(imgSrc);
            userRepo.save(user);
            //Save this in the DB.

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            returnPage.setViewName("error");
        }

        return returnPage;
    }



    @GetMapping(path = "/all")
    public @ResponseBody
    Iterable<User> getAllUsers() {
        // This returns a JSON or XML with the users
        return userRepo.findAll();
    }

    @GetMapping(path = "/user")
    public @ResponseBody
    Optional<User> getOneUser(@RequestParam Integer id) {
        // This returns a JSON or XML with the users
        return userRepo.findById(id);
    }

    @GetMapping(path = "/userByName")
    public @ResponseBody
    User getOneUserByName(@RequestParam("username") String username) {
        return userRepo.findByUsername(username);
    }



    public ModelAndView getPageWithWeatherInfo()
    {
        ModelAndView returnPage = new ModelAndView();

        String API_KEY = "4fa8fc5c7d7981b995adddbf79e5b964";

        // City of Albany (NY) city code: 5106834
        // It will always be albany (to keep it simple)
        String weatherDataURLString = "http://api.openweathermap.org/data/2.5/weather?id=5106834&appid=" + API_KEY + "&units=imperial";

        StringBuilder weatherDataStringBuilder = new StringBuilder();

        String tempString = "";

        try {
            URL url = new URL(weatherDataURLString);
            URLConnection weatherDataConn = url.openConnection();
            BufferedReader weatherDataRead = new BufferedReader(new InputStreamReader(weatherDataConn.getInputStream()));

            String weatherDataJson = "";
            String weatherDataLine = "";

            while ((weatherDataLine = weatherDataRead.readLine()) != null) {
                weatherDataStringBuilder.append(weatherDataLine);
                weatherDataJson += weatherDataLine;
            }

            JSONObject JSONObject_weatherDataAPIResponse = new JSONObject(weatherDataJson);

            /*
            double lon = JSONObject_weatherDataAPIResponse.getDouble("lon");
            double lat = JSONObject_weatherDataAPIResponse.getDouble("lat");
            String timezone = JSONObject_weatherDataAPIResponse.getString("timezone");
            System.out.println("timezone: " + timezone);
            int timezone_offset = JSONObject_weatherDataAPIResponse.getInt("timezone_offset");
            System.out.println("timezone_offset: " + timezone_offset);
            */

            JSONArray JSONArray_weather = JSONObject_weatherDataAPIResponse.getJSONArray("weather");
            JSONObject JSONObject_weather = JSONArray_weather.getJSONObject(0);
            String weather_description = JSONObject_weather.getString("description");
            String weather_icon = JSONObject_weather.getString("icon");
            String weather_icon_path = "/img/weathericons/" + weather_icon + ".png";

            JSONObject JSONObject_main = JSONObject_weatherDataAPIResponse.getJSONObject("main");
            double temp = JSONObject_main.getDouble("temp");
            int intTemp = (int)temp;

            JSONObject JSONObject_wind = JSONObject_weatherDataAPIResponse.getJSONObject("wind");
            double wind_speed = JSONObject_wind.getDouble("speed");
            int intWind_speed = (int)wind_speed;






            returnPage.addObject("temp", intTemp);
            returnPage.addObject("description", weather_description);
            returnPage.addObject("icon", weather_icon_path);
            returnPage.addObject("wind", intWind_speed);


        } catch(IOException | JSONException e) {
            System.out.println(e.getMessage());
        }





        return returnPage;
    }
}
