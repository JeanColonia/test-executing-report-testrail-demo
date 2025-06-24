//package org.nitro_qa;
//
//import io.github.cdimascio.dotenv.Dotenv;
//import org.apache.pdfbox.multipdf.PDFMergerUtility;
//import org.openqa.selenium.By;
//import org.openqa.selenium.JavascriptExecutor;
//import org.openqa.selenium.WebDriver;
//import org.openqa.selenium.WebElement;
//import org.openqa.selenium.chrome.ChromeDriver;
//import org.openqa.selenium.chrome.ChromeOptions;
//import org.openqa.selenium.devtools.DevTools;
//import org.openqa.selenium.devtools.v137.page.Page;
//import org.openqa.selenium.support.ui.ExpectedConditions;
//import org.openqa.selenium.support.ui.WebDriverWait;
//
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.time.Duration;
//import java.util.ArrayList;
//import java.util.Base64;
//import java.util.List;
//import java.util.Optional;
//
//public class PDFGenerator {
//
//    public static void main(String[] args) throws IOException {
//
//        ChromeOptions chromeOptions = new ChromeOptions();
//        chromeOptions.addArguments("--headless");
//        chromeOptions.addArguments("--disable-gpu");
//        chromeOptions.addArguments("--no-sandbox");
//
//        ChromeDriver driver = new ChromeDriver(chromeOptions);
//        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
//
//        final Dotenv dotenv = Dotenv.load();
//        final String BASE_URL = dotenv.get("TESTRAIL_BASE_URL");
//        final String USERNAME = dotenv.get("TESTRAIL_USERNAME");
//        final String PASS = dotenv.get("TESTRAIL_PASSWORD");
//
//        DevTools devTools = driver.getDevTools();
//        devTools.createSession();
//
//        driver.manage().window().maximize();
//        String  textExecutionUrl = "https://alicorpdigital.testrail.io/index.php?/runs/view/1844&group_by=cases:section_id&group_order=asc&group_id=11565";
//
//        String groupId = textExecutionUrl.split("group_id=")[1];
//        groupId = "grid-".concat(groupId);
//
//        List<String> testCasePaths = new ArrayList<>();
//        try {
//            driver.get(BASE_URL);
//            PDFGenerator.Login(driver, USERNAME, PASS);
//            driver.manage().window().maximize();
//            driver.get(textExecutionUrl);
//
//            List<WebElement> tableCaseList = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.cssSelector("#"+groupId+" tbody tr[id]")));
//            List<String> caseLinks = new ArrayList<>();
//
//            for (WebElement el: tableCaseList){
//                WebElement linkToTestCase = el.findElement(By.cssSelector("td a"));
//                String fixedLinkToTestCase = linkToTestCase.getAttribute("href").replace("view", "plot").concat("&print=1");
//                caseLinks.add(fixedLinkToTestCase);
//            }
//            for (String link:caseLinks) {
//
//                driver.get(link);
//                driver.manage().window().maximize();
//                Thread.sleep(2000);
//                String testCaseName = link.split("/plot/")[1].replace("&print=1", "").trim();
//                testCaseName = "Case_"+testCaseName+".pdf";
//                testCasePaths.add(testCaseName);
//                Page.PrintToPDFResponse pdf = devTools.send(Page.printToPDF(
//                        Optional.of(false), // landscape
//                        Optional.of(false), // displayHeaderFooter
//                        Optional.of(false),  // printBackground
//                        Optional.empty(), Optional.empty(), Optional.empty(), // scale, paperWidth, paperHeight
//                        Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), // margins
//                        Optional.empty(), Optional.empty(), Optional.empty(), // pageRanges, headers, footers
//                        Optional.of(true), // preferCSSPageSize
//                        Optional.empty(), Optional.empty(),
//                        Optional.empty()
//                        // transferMode, media
//                ));
//                byte[] decodeBytes = Base64.getDecoder().decode(pdf.getData());
//
//
//                try (FileOutputStream fos = new FileOutputStream(testCaseName)){
//                    fos.write(decodeBytes);
//                    System.out.println("PDF generado "+testCaseName);
//                }catch (Exception e){
//                    System.err.println("Error al procesar la url: "+link);
//                    e.printStackTrace();
//                }
//
//            }
//        }
//        catch (Exception e){
//            System.out.println("e"+e);
//        }
//        finally {
//            driver.quit();
//            PDFGenerator.mergePDFs(testCasePaths, "All_cases.pdf");
//            PDFGenerator.deleteFiles(testCasePaths);
//
//        }
//    }
//
//    private static void Login(WebDriver driver, String username, String password){
//        WebElement usernameInput = driver.findElement(By.id("name"));
//        WebElement passInput = driver.findElement(By.id("password"));
//        WebElement logInBtn = driver.findElement(By.id("button_primary"));
//
//        usernameInput.sendKeys(username);
//        passInput.sendKeys(password);
//        logInBtn.click();
//    }
//
//    private static void mergePDFs(List<String> pdfPaths, String outputPath) throws IOException {
//        PDFMergerUtility merger = new PDFMergerUtility();
//        merger.setDestinationFileName(outputPath);
//        for (String path:pdfPaths){
//            merger.addSource(new File(path));
//        }
//        merger.mergeDocuments(null);
//    }
//
//    private static void deleteFiles(List<String> files){
//        for (String file:files){
//            new File(file).delete();
//        }
//    }
//}
