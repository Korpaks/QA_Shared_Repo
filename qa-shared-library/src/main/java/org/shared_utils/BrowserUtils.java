package org.shared_utils;

import org.apache.commons.lang3.RandomStringUtils;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.interactions.MoveTargetOutOfBoundsException;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class BrowserUtils {


    /**
     * Waits for an element to be visible
     * @param element that we are waiting to be visible
     */
    public static void waitForVisibilityOf(WebElement element) {
        WebDriverWait wait = new WebDriverWait(Driver.getDriver(), Duration.ofSeconds(30));
        try {
            wait.until(ExpectedConditions.visibilityOf(element));
            waitFor(250);
        } catch (StaleElementReferenceException | IndexOutOfBoundsException e) {
            e.getSuppressed();
        }
    }


    /**
     * Waits until the element is invisible
     *
     * @param element that we are waiting to become invisible
     */
    public static void waitForInvisibility(By element) {
        try {
            Driver.getDriver().manage().timeouts().implicitlyWait(Duration.ofSeconds(3));
            WebDriverWait wait = new WebDriverWait(Driver.getDriver(), Duration.ofSeconds(90));
            wait.until(ExpectedConditions.invisibilityOfElementLocated(element));
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            Driver.getDriver().manage().timeouts().implicitlyWait(Duration.ofSeconds(3));
        }
    }


    /**
     * Scrolling to element using JavaScript executor
     *
     * @param element that we are scrolling to
     */
    public static void scrollToElementWithJS(WebElement element) {

        try {
            ((JavascriptExecutor) Driver.getDriver()).executeScript("arguments[0].scrollIntoView();", element);
            new Actions(Driver.getDriver()).moveToElement(element).build().perform();
        } catch (MoveTargetOutOfBoundsException e) {
            e.printStackTrace();
        }

    }


    /**
     * Performs a pause for a certain amount of milliseconds
     *
     * @param milliseconds how many millisecond do we need to wait
     */
    public static void waitFor(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    /**
     * This method will highlight the element
     *
     * @param element that we are looking to highlight
     */
    public static void highlight(WebElement element) {
        ((JavascriptExecutor) Driver.getDriver()).executeScript("arguments[0].setAttribute('style','background: yellow; border: 2px solid red;');", element);
        waitFor(100);
        ((JavascriptExecutor) Driver.getDriver()).executeScript("arguments[0].removeAttribute('style','background: yellow; border: 2px solid red;');", element);
    }


    /**
     * This method will recover in case of exception after unsuccessful click
     * and will try to click on element again
     *
     * @param element that method will click on
     */
    public static void retryClickWithWait(WebElement element) {
        int counter = 0;
        while (counter < 10) {
            try {
                scrollToElementWithJS(element);
                waitFor(100);
                highlight(element);
                element.click();
                //if click successful then break
                break;
            } catch (ElementNotInteractableException e) {
                e.printStackTrace();
                ++counter;
                //wait for 1/10 sec and try click again
                waitFor(100);
            } catch (StaleElementReferenceException e) {
                e.printStackTrace();
                waitFor(100);
            }
        }
    }


    /**
     * This method will click on the dropdown and try to click on the option item
     *
     * @param dropdown that we need to click and choose from
     */
    public static void clickOnDropdownAndChooseRandom(WebElement dropdown) {
        for (int i = 0; i < 10; i++)
            try {
                retryClickWithWait(dropdown);
                if (BasePage.allOptions.size() != 0) {
                    break;
                }
            } catch (ElementNotInteractableException e) {
                System.out.println("Exception caught: " + e);
                e.printStackTrace();
            }
        waitFor(250);
        int randomOption = new Random().nextInt(BasePage.allOptions.size());
        try {
            retryClickWithWait(BasePage.allOptions.get(randomOption));
        } catch (StaleElementReferenceException e) {
            e.printStackTrace();
        }
    }


    /**
     * Same method like the one above just overridden
     *
     * @param dropdown that we need to click and choose from
     * @param startNum starting point of the first element that we need to choose from
     */
    public static void clickOnDropdownAndChooseRandom(WebElement dropdown, int startNum) {
        for (int i = 0; i < 10; i++)
            try {
                retryClickWithWait(dropdown);
                if (BasePage.allOptions.size() != 0) {
                    break;
                }
            } catch (ElementNotInteractableException e) {
                System.out.println("Exception caught: " + e);
                e.printStackTrace();
            }
        waitFor(250);
        int randomOption = new Random().nextInt(BasePage.allOptions.size() - 1) + startNum;
        try {
            retryClickWithWait(BasePage.allOptions.get(randomOption));
        } catch (StaleElementReferenceException e) {
            e.printStackTrace();
        }
    }


    /**
     * This method will take two parameters, which dropdown you want to click on and which option you want to click on
     *
     * @param dropdown that we need to click and choose from
     * @param option   that we need to choose from dropdown
     */
    public static void clickOnDropdownAndChooseElement(WebElement dropdown, WebElement option) {
        waitForVisibilityOf(dropdown);
        retryClickWithWait(dropdown);
        waitFor(250);
        waitForVisibilityOf(option);
        retryClickWithWait(option);
    }


    /**
     * This method will extract text from all web element and place it into List
     *
     * @param list of Web elements that we need to turn into text
     * @return List<String>
     */
    public static List<String> getElementsText(List<WebElement> list) {
        List<String> elementsText = new ArrayList<>();
        waitFor(100);
        for (WebElement each : list) {
            try {
                elementsText.add(each.getText());
            } catch (StaleElementReferenceException e) {
                e.printStackTrace();
                System.err.println("Stale element here");
            }
        }
        return elementsText;
    }


    /**
     * This method will check if the button or element is disabled
     *
     * @param element that we want to check if it is disabled
     * @return true or false
     */
    public static Boolean isDisabled(WebElement element) {

        return element.getAttribute("class").contains("disabled");
    }


    /**
     * This method checks if the element or input is editable or not
     *
     * @param element that we want to check if it is not editable
     * @return true or false
     */
    public static Boolean isEditable(WebElement element) {

        return !Boolean.parseBoolean(element.getAttribute("disabled"));
    }



    /**
     * This method will return boolean based on if this field or dropdown is required to input
     *
     * @param element that we want to check if it is required
     * @return true or false
     */
    public static Boolean isRequired(WebElement element) {

        return Boolean.parseBoolean(element.getAttribute("aria-required"));
    }


    /**
     * This method will give you random 3-digit number
     *
     * @return random three-digit number
     */
    public static String randomThreeDigitNumber() {

        return String.valueOf(100 + new Random().nextInt(900));
    }


    /**
     * This method will give you random 4-digit number
     *
     * @return random 4 digit number
     */
    public static String randomFourDigitNumber() {
        return  String.valueOf(1000 + new Random().nextInt(9000));
    }


    /**
     * This method will generate a password based on requierments
     */
    public static String passwordGenerator() {

        String upperCaseLetters = RandomStringUtils.random(4, 65, 90, true, true);
        String lowerCaseLetters = RandomStringUtils.random(4, 97, 122, true, true);
        String numbers = RandomStringUtils.randomNumeric(4);
        String specialChar = RandomStringUtils.random(2, 33, 47, false, false);
        String totalChars = RandomStringUtils.randomAlphanumeric(2);
        String combinedChars = upperCaseLetters.concat(lowerCaseLetters)
                .concat(numbers)
                .concat(specialChar)
                .concat(totalChars);
        List<Character> pwdChars = combinedChars.chars()
                .mapToObj(c -> (char) c)
                .collect(Collectors.toList());
        Collections.shuffle(pwdChars);

        return pwdChars.stream()
                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
                .toString();
    }


    /**
     * This method will give you rgba value based on the input color
     *
     * @param color which we are looking for ex. BLUE, WHITE, etc
     * @return String color rgba value
     */
    public static String getColorValue(String color) {

        Map<String, String> allColors = new HashMap<>();
        allColors.put("WHITE", "rgba(255, 255, 255, 1)");
        allColors.put("BLUE", "rgba(27, 34, 81, 1)");
        allColors.put("RED", "rgba(145, 25, 51, 1)");
        allColors.put("PILL RED", "rgba(192, 0, 0, 1)");
        allColors.put("LIGHT RED", "rgba(246, 233, 236, 1)");
        allColors.put("YELLOW", "rgba(222, 206, 10, 1)");
        allColors.put("GREEN", "rgba(35, 154, 90, 1)");
        allColors.put("MESSAGE BLUE", "rgba(0, 74, 143, 1)");
        allColors.put("GRAY", "rgba(202, 202, 202, 1)");
        allColors.put("PILL GRAY", "rgba(224, 224, 224, 1)");

        return allColors.get(color.toUpperCase());
    }


    /**
     * This method checks if the element is checked
     *
     * @param element that we are looking for if it is checked
     * @return Boolean true of false
     */
    public static Boolean isCheckboxChecked(WebElement element) {

        return element.getAttribute("class").contains("checked");
    }


    /**
     * This method checks if the element is selected
     *
     * @param element that we are looking for if it is selected
     * @return Boolean true of false
     */
    public static Boolean isCheckboxSelected(WebElement element) {

        return element.getAttribute("class").contains("selected");
    }


    /**
     * This method will check if the file is downloaded
     *
     * @param name of the file we are looking if it is downloaded
     * @return Boolean true of false
     */
    public static boolean isFileDownloaded(String name) {
        String location = System.getProperty("user.dir") + "\\Downloads/";
        File dir = new File(location);
        File[] dirContents = dir.listFiles();

        for (int i = 0; i < Objects.requireNonNull(dirContents).length; i++) {

            if (dirContents[i].getName().equals(name)) {
                // File has been found
                System.out.println("File is downloaded: " + dirContents[i].exists());
                return true;
            }
        }
        return false;
    }


    /**
     * This method will check if the file is deleted
     *
     * @param name of the file that we are checking
     * @return true or false
     */
    public static boolean isFileDeleted(String name) {
        String location = System.getProperty("user.dir") + "\\Downloads/";
        File dir = new File(location);
        File[] dirContents = dir.listFiles();

        for (int i = 0; i < Objects.requireNonNull(dirContents).length; i++) {
            if (dirContents[i].getName().equals(name)) {
                // File has been found, it can now be deleted:
                System.out.println("File " + dirContents[i].getName() + " is deleted: " + dirContents[i].delete());
                return true;
            }
        }
        return false;
    }


    /**
     * This method will check if the file is present by checking the size of the List of Elements
     *
     * @param elements that we are looking for size
     * @return true or false
     */
    public static boolean isElementPresentBySize(List<WebElement> elements) {
        Driver.getDriver().manage().timeouts().implicitlyWait(Duration.ofSeconds(3));
        try {
            if (elements.size() != 0) {
                return true;
            }
        } catch (NoSuchElementException e) {
            e.getSuppressed();
        } finally {
            Driver.getDriver().manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        }
        return false;
    }


    /**
     * This method will check if the element is present by checking the display method
     *
     * @param element that we are looking for display
     * @return true or false
     */
    public static boolean isElementPresentByDisplayed(WebElement element) {
        Driver.getDriver().manage().timeouts().implicitlyWait(Duration.ofSeconds(1));
        try {
            if (element.isDisplayed()) {
                return true;
            }
        } catch (NoSuchElementException e) {
            e.getSuppressed();
        } finally {
            Driver.getDriver().manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        }
        return false;
    }


    /**
     * This method will take the date in format of MMMM d, yyyy example January 02, 2022
     * and turn it into LocalDate object with format of yyyy-MM-dd example 2022-01-02
     *
     * @param date that we are looking to parse
     * @return date as LocalDate class
     */
    public static LocalDate parseDate(String date) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy", Locale.ENGLISH);

        return LocalDate.parse(date, formatter);
    }


    /**
     * This method is connected to the parseDate where we wil have the date and parse it to specific
     * format, and then we will compare those two dates, if the compare gives you negative number means that
     * the date1 is before date2
     *
     * @param date1 first date that we are checking
     * @param date2 second date that we are checking
     * @return true or false
     */
    public static boolean firstDateBeforeSecondDate(LocalDate date1, LocalDate date2) {

        return date1.compareTo(date2) <= 0;
    }


    /**
     * This method is connected to the parseDate where we wil have the date and parse it to specific
     * format, and then we will compare those two dates, if the compare gives you negative number means that
     * the date1 is after date2
     *
     * @param date1 first date that we are checking
     * @param date2 second date that we are checking
     * @return true of false
     */
    public static boolean firstDateAfterSecondDate(LocalDate date1, LocalDate date2) {

        return date1.compareTo(date2) >= 0;
    }

    /**
     * This method will open up new tab in url what we set up as parameter
     *
     * @param url that this method will open
     */
    public static void openNewTabWithUrl(String url) {
        ((JavascriptExecutor) Driver.getDriver()).executeScript("window.open('" + url + "','_blank');");
    }


    /**
     * This method is using Action class to click ESC button
     */
    public static void pressButtonESC(){
        new Actions(Driver.getDriver()).sendKeys(Keys.ESCAPE).perform();
    }


    /**
     * This method is using Action class to click TAB button
     */
    public static void pressButtonTAB(){
        new Actions(Driver.getDriver()).sendKeys(Keys.TAB).perform();
    }


    /**
     * This method is using Action class to click ENTER button
     */
    public static void pressButtonENTER(){
        new Actions(Driver.getDriver()).sendKeys(Keys.ENTER).perform();
    }


    /**
     * This method is using Action class to click BACKSPACE button
     */
    public static void pressButtonBACKSPACE(){
        new Actions(Driver.getDriver()).sendKeys(Keys.BACK_SPACE).perform();
    }


    /**
     * This method is using Action class to use mouse action to hover over an element
     */
    public static void hoverOverElement(WebElement element){
        new Actions(Driver.getDriver()).moveToElement(element).perform();
    }

    /**
     * This method will check if the check button is checked/pressed or ot
     * @param element we are checking if it is checked
     * @return true or false
     */
    public static Boolean isCheckButtonPressed(WebElement element) {
        return Boolean.parseBoolean(element.getAttribute("aria-pressed"));
    }

    /**
     * This method will check the name of the inout field by checking the label of the element
     */
    public static String labelOfInput(WebElement element) {
        String elementsXPath = element.toString();
        elementsXPath = elementsXPath.substring(elementsXPath.lastIndexOf("xpath:") + 6, elementsXPath.lastIndexOf("]")).trim();
        return Driver.getDriver().findElement(By.xpath(elementsXPath + "/preceding-sibling::label")).getText();
    }

    /**
     * This method will return current line in the code if printed. Helps debugging code
     * @return line of the code
     */
    public static String printCurrentLine() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        int currentLine = 0;
        if (stackTrace.length >= 3) {
            currentLine = stackTrace[2].getLineNumber();
        }
        return String.valueOf(currentLine);
    }

    /**
     * This method will check if user is currently on module
     * @param module that we want to check
     * @return true of false
     */
    public static Boolean isUserOnModule(WebElement module) {
        return module.getAttribute("class").contains("active-link");
    }

    /**
     * This method will check if user is currently on tab
     * @param tab that we want to check
     * @return true or false
     */
    public static Boolean isUserOnTab(WebElement tab) {
        return tab.getAttribute("class").contains("active");
    }
}
