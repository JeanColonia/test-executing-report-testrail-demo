//package org.nitro_qa;
//
//import org.openqa.selenium.By;
//import org.openqa.selenium.JavascriptExecutor;
//import org.openqa.selenium.WebDriver;
//import org.openqa.selenium.WebElement;
//import org.openqa.selenium.chrome.ChromeDriver;
//
//import io.github.cdimascio.dotenv.Dotenv;
//import org.openqa.selenium.chrome.ChromeOptions;
//import org.openqa.selenium.support.ui.ExpectedCondition;
//import org.openqa.selenium.support.ui.ExpectedConditions;
//import org.openqa.selenium.support.ui.WebDriverWait;
//
//import java.time.Duration;
//import java.util.*;
//
//public class SeleniumService {
//
//
//    public static void main(String[]args) throws InterruptedException {
//
//        Map<String, Object> prefs = new HashMap<>();
//        prefs.put("printing.print_preview_sticky_settings.appState", "{\"recentDestinations\":[{\"id\":\"Save as PDF\",\"origin\":\"local\"}],\"selectedDestinationId\":\"Save as PDF\",\"version\":2}");
//
//        ChromeOptions chromeOptions = new ChromeOptions();
//        chromeOptions.setExperimentalOption("prefs", prefs);
//        chromeOptions.addArguments("--lang=en-US");
//        chromeOptions.addArguments("--kiosk-printing");
//        chromeOptions.addArguments("--disable-popup-blocking");
//        chromeOptions.addArguments("--start-maximized");
//        chromeOptions.addArguments("--user-data-dir=/tmp/chrome-profile-" + UUID.randomUUID());
//
//        WebDriver driver = new ChromeDriver(chromeOptions);
//
//
//        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
//
//        final Dotenv dotenv = Dotenv.load();
//        final String BASE_URL = dotenv.get("TESTRAIL_BASE_URL");
//        final String USERNAME = dotenv.get("TESTRAIL_USERNAME");
//        final String PASS = dotenv.get("TESTRAIL_PASSWORD");
//
//        driver.manage().window().maximize();
//        String  textExecutionUrl = "https://alicorpdigital.testrail.io/index.php?/runs/view/1844&group_by=cases:section_id&group_order=asc&group_id=11565";
//
//        String groupId = textExecutionUrl.split("group_id=")[1];
//        groupId = "grid-".concat(groupId);
//
//        System.out.println(groupId);
//        try {
//            driver.get(BASE_URL);
//            SeleniumService.Login(driver, USERNAME, PASS);
//
//            driver.get(textExecutionUrl);
//
//            List<WebElement> tableCaseList = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.cssSelector("#"+groupId+" tbody tr[id]")));
//            List<String> caseLinks = new ArrayList<>();
//
//            for (WebElement el: tableCaseList){
//                WebElement linkToTestCase = el.findElement(By.cssSelector("td a"));
//                caseLinks.add(linkToTestCase.getAttribute("href"));
//            }
//            for (String link:caseLinks) {
//              driver.get(link);
//              WebElement iconPrint = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("icon-print")));
//              iconPrint.click();
//
//              wait.until(driveer -> driveer.getWindowHandles().size() > 1 );
//
//              Set<String> windows = driver.getWindowHandles();
//              Iterator<String> it =windows.iterator();
//
//              String parentId = it.next();
//              String childId = it.next();
//
//              driver.switchTo().window(childId);
//
//           ((JavascriptExecutor) driver).executeScript("window.print()");
//
//            driver.close();
//
//            driver.switchTo().window(parentId);
//            driver.navigate().back();
//          }
//            driver.quit();
//        }
//        catch (Exception e){
//            System.out.println("e"+e);
//        }
//    }
//
//
//    private static void Login(WebDriver driver, String username, String password){
//            WebElement usernameInput = driver.findElement(By.id("name"));
//            WebElement passInput = driver.findElement(By.id("password"));
//            WebElement logInBtn = driver.findElement(By.id("button_primary"));
//
//            usernameInput.sendKeys(username);
//            passInput.sendKeys(password);
//            logInBtn.click();
//    }
//}
