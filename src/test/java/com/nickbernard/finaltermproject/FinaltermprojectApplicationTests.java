package com.nickbernard.finaltermproject;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.ModelAndViewAssert;
import org.springframework.web.servlet.ModelAndView;

@SpringBootTest
class FinaltermprojectApplicationTests {

    WebsiteController websiteController = new WebsiteController();

    @Test
    void testWeatherDataFromAPI() {

        ModelAndView testmvc = websiteController.getPageWithWeatherInfo();

        // Test to see that the MVC object returned from this function has all of the
        // properties of weather that will need to be displayed on the main page
        ModelAndViewAssert.assertModelAttributeAvailable(testmvc, "temp");
        ModelAndViewAssert.assertModelAttributeAvailable(testmvc, "description");
        ModelAndViewAssert.assertModelAttributeAvailable(testmvc, "icon");
        ModelAndViewAssert.assertModelAttributeAvailable(testmvc, "wind");
    }


    @Test
    void testPageRendering(){

        ModelAndView homepageTestMvc = websiteController.renderHomePage();
        ModelAndViewAssert.assertViewName(homepageTestMvc, "index");
        ModelAndViewAssert.assertModelAttributeAvailable(homepageTestMvc, "temp");
        ModelAndViewAssert.assertModelAttributeAvailable(homepageTestMvc, "description");
        ModelAndViewAssert.assertModelAttributeAvailable(homepageTestMvc, "icon");
        ModelAndViewAssert.assertModelAttributeAvailable(homepageTestMvc, "wind");

        ModelAndView artworksTestMvc = websiteController.renderArtworksPage();
        ModelAndViewAssert.assertViewName(artworksTestMvc, "artworks");
        ModelAndViewAssert.assertModelAttributeAvailable(artworksTestMvc, "temp");
        ModelAndViewAssert.assertModelAttributeAvailable(artworksTestMvc, "description");
        ModelAndViewAssert.assertModelAttributeAvailable(artworksTestMvc, "icon");
        ModelAndViewAssert.assertModelAttributeAvailable(artworksTestMvc, "wind");

        ModelAndView registrationTestMvc = websiteController.renderRegistrationPage();
        ModelAndViewAssert.assertViewName(registrationTestMvc, "register");
        ModelAndViewAssert.assertModelAttributeAvailable(registrationTestMvc, "temp");
        ModelAndViewAssert.assertModelAttributeAvailable(registrationTestMvc, "description");
        ModelAndViewAssert.assertModelAttributeAvailable(registrationTestMvc, "icon");
        ModelAndViewAssert.assertModelAttributeAvailable(registrationTestMvc, "wind");

        ModelAndView profileTestMvc = websiteController.renderProfilePage();
        ModelAndViewAssert.assertViewName(profileTestMvc, "profile");
        ModelAndViewAssert.assertModelAttributeAvailable(profileTestMvc, "temp");
        ModelAndViewAssert.assertModelAttributeAvailable(profileTestMvc, "description");
        ModelAndViewAssert.assertModelAttributeAvailable(profileTestMvc, "icon");
        ModelAndViewAssert.assertModelAttributeAvailable(profileTestMvc, "wind");

    }



}
