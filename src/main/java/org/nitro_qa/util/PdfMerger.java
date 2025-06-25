package org.nitro_qa.util;

import io.github.cdimascio.dotenv.Dotenv;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.v123.page.Page;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;


public class PdfMerger {

    public static byte[] generateAndMergePdfs(String textExecutionUrl) throws IOException {

        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("--headless", "--disable-gpu", "--no-sandbox", "window-size=1920,1080", "--disable-dev-shm-usage");
        ChromeDriver driver = new ChromeDriver(chromeOptions);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        JavascriptExecutor js =  (JavascriptExecutor) driver;

        final Dotenv dotenv = Dotenv.load();
        final String BASE_URL = dotenv.get("TESTRAIL_BASE_URL");
        final String USERNAME = dotenv.get("TESTRAIL_USERNAME");
        final String PASS = dotenv.get("TESTRAIL_PASSWORD");
        String groupId = "";
        String allCases = "NITR_REPORTE_CONSOLIDADO.pdf";
        DevTools devTools = driver.getDevTools();
        devTools.createSession();

        driver.manage().window().maximize();

        groupId = textExecutionUrl.split("group_id=")[1];
        groupId = "grid-".concat(groupId);

        List<String> testCasePaths = new ArrayList<>();
        try {
            driver.get(BASE_URL);
            Login(driver, USERNAME, PASS);
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("content-header-title")));
            driver.get(textExecutionUrl);
            wait.until(webDriver -> ((JavascriptExecutor) webDriver)
                    .executeScript("return document.readyState").equals("complete"));

           WebElement scrollToGroupId =  wait.until(ExpectedConditions.presenceOfElementLocated(By.id(groupId)));
           js.executeScript("arguments[0].scrollIntoView(true);", scrollToGroupId);
            List<WebElement> tableCaseList = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.cssSelector("#"+groupId+" tbody tr[id]")));
            List<String> caseLinks = new ArrayList<>();

            for (WebElement el: tableCaseList){
                WebElement linkToTestCase = el.findElement(By.cssSelector("td a"));
                String fixedLinkToTestCase = linkToTestCase.getAttribute("href").replace("view", "plot").concat("&print=1");
                caseLinks.add(fixedLinkToTestCase);
            }
            for (String link:caseLinks) {
                driver.get(link);
                wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("formatSelection")));
                String testCaseName = link.split("/plot/")[1].replace("&print=1", "").trim();
                testCaseName = "Case_"+testCaseName+".pdf";
                testCasePaths.add(testCaseName);
                Page.PrintToPDFResponse pdf = devTools.send(Page.printToPDF(
                        Optional.of(false), // landscape
                        Optional.of(false), // displayHeaderFooter
                        Optional.of(false),  // printBackground
                        Optional.empty(), Optional.empty(), Optional.empty(), // scale, paperWidth, paperHeight
                        Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), // margins
                        Optional.empty(), Optional.empty(), Optional.empty(), // pageRanges, headers, footers
                        Optional.of(true), // preferCSSPageSize
                        Optional.empty(), Optional.empty(),
                        Optional.empty()
                        // transferMode, media
                ));
                byte[] decodeBytes = Base64.getDecoder().decode(pdf.getData());
                try (FileOutputStream fos = new FileOutputStream(testCaseName)){
                    fos.write(decodeBytes);
                    System.out.println("PDF generado "+testCaseName);
                }catch (Exception e){
                    System.err.println("Error al procesar la url: "+link);
                    e.printStackTrace();
                }

            }

            mergePDFs(testCasePaths, allCases);
            deleteFiles(testCasePaths);

            File mergedFile = new File(allCases);
            byte[] mergedBytes = java.nio.file.Files.readAllBytes(mergedFile.toPath());
            mergedFile.delete();
            driver.manage().deleteAllCookies();
            return mergedBytes;

        }
        catch (Exception e){
            System.out.println("e"+e);
            File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            Files.copy(screenshot.toPath(), new File("screenshot_error.png").toPath(), StandardCopyOption.REPLACE_EXISTING);
            return new byte[0];
        }
        finally {
            driver.manage().deleteAllCookies();
            driver.quit();
        }
    }

    private static void Login(WebDriver driver, String username, String password){
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement usernameInput = wait.until(ExpectedConditions.elementToBeClickable(By.id("name")));
        WebElement passInput = wait.until(ExpectedConditions.elementToBeClickable(By.id("password")));

        usernameInput.sendKeys(username);
        passInput.sendKeys(password);
        WebElement button = wait.until(ExpectedConditions.elementToBeClickable(By.id("button_primary")));
        button.click();
    }

    private static void mergePDFs(List<String> pdfPaths, String outputPath) throws IOException {
        PDFMergerUtility merger = new PDFMergerUtility();

        merger.setDestinationFileName(outputPath);
        for (String path:pdfPaths){
            merger.addSource(new File(path));
        }
        merger.mergeDocuments(null);
    }

    private static void deleteFiles(List<String> files){
        for (String file:files){
            new File(file).delete();
        }
    }
}
