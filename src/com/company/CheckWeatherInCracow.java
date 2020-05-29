package com.company;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class CheckWeatherInCracow {

    public static void main(String[] args) {

        BufferedReader br = null;
        try {
            URL address = new URL("https://www.accuweather.com/pl/pl/krakow/274455/current-weather/274455");
            HttpURLConnection connection = (HttpURLConnection) address.openConnection();
            connection.setRequestProperty("User-Agent", "getDataFromHTMLWebsite/1.0");
            br = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            StringBuilder stringBuilder = new StringBuilder();
            String readerLine;
            while ((readerLine = br.readLine()) != null) {
                stringBuilder.append(readerLine).append("\n");
            }
            String html = stringBuilder.toString();
            Document document = Jsoup.parse(html);

            String pressure = null, cloudy = null, currentTemperature = null, averageDailyMaxTemperature = null, averageDailyMinTemperature = null;
            Elements elements = document.select("div.current-conditions-card.content-module.non-ad div.accordion-item-content.accordion-item-content div.list" );
            for (Element element : elements) {
                if (!element.select("p").get(5).equals("") && element.select("p").get(5).text().startsWith("Ciśnienie: ")) {
                    pressure = element.select("p").get(5).text();
                }
                if (!element.select("p").get(6).equals("") && element.select("p").get(6).text().startsWith("Zachmurzenie: ")) {
                    cloudy = element.select("p").get(6).text();
                }
            }
            double returnedPressure = 0.0;
            int returnedCloudy = 0;
            try {
                returnedPressure = Double.parseDouble(pressure.split(" ")[1]);
            }catch(NumberFormatException e){
                System.out.println("Given pressure is not a double format!");
                System.exit(0);
            }
           try{
               assert cloudy != null;
               returnedCloudy = Integer.parseInt(cloudy.split(" ")[1].replace("%",""));
           }catch (NumberFormatException e){
               System.out.println("Given cloudy data is not an integer format!");
               System.exit(0);
           }

            if (!document.select("p.value").first().text().equals("")) {
                currentTemperature = document.select("p.value").first().text();
            }
            elements = document.select("div.temp-history div.history.card");
            for (Element element : elements) {
                if(!element.select("div.row span").get(2).text().equals("")){
                    averageDailyMaxTemperature = element.select("div.row span").get(2).text();
                }
                if(!element.select("div.row span").get(8).text().equals("")){
                    averageDailyMinTemperature = element.select("div.row span").get(8).text();
                }
            }
            isCorrectRangeCelsiusTemperature(currentTemperature);
            isCorrectRangeCelsiusTemperature(averageDailyMaxTemperature);
            isCorrectRangeCelsiusTemperature(averageDailyMinTemperature);
            isPressureInRange(returnedPressure);
            isCloudyInRange(returnedCloudy);

            System.out.println("Our information from accuweather website about weather in Cracow:");
            System.out.println("*Maximum daily average temperature in Cracow: " + averageDailyMaxTemperature + "C");
            System.out.println("*Minimum daily average temperature in Cracow: " + averageDailyMinTemperature + "C");
            System.out.println("*Actual temperature: " + currentTemperature + "C");
            System.out.println("*Pressure: " + returnedPressure + " mbar");
            System.out.println("*Cloudy: " + returnedCloudy + "%");

        } catch (MalformedURLException e) {
            System.out.println("Wrong URL address!");
            System.exit(0);
        }
        catch (IOException e) {
            System.out.println("Error with: " + e.getMessage());
            System.exit(0);
        } catch (WrongData wrongData) {
            System.out.println("Error with: " + wrongData.getMessage());
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void isCloudyInRange(int cloudy) throws WrongData{
        if(cloudy < 0 || cloudy > 100){
            throw new WrongData("Wrong cloudy! It is out of normal cloudy range!");
        }
    }
    private static void isCorrectRangeCelsiusTemperature(String temperature) throws WrongData {

        String temp = temperature.replace("°","");
        int givenTemperature = Integer.parseInt(temp);

        if (givenTemperature < -50 || givenTemperature > 50) {
            throw new WrongData("Wrong temperature! It is not Celsius temperature!");
        }
    }

    private static void isPressureInRange(double pressure) throws WrongData {
        if (pressure < 500 || pressure > 1500) {
            throw new WrongData("Wrong pressure! It is out of normal pressure range!");
        }
         }

    }
