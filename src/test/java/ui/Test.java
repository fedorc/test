package ui;

import static org.assertj.core.api.Assertions.assertThat;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

class ChromeTest {
        WebDriver driver;

        @BeforeAll
        static void setupClass() {
            WebDriverManager.chromedriver().setup();
        }

        @BeforeEach
        void setupTest() {
            driver = new ChromeDriver();
            driver.manage().window().maximize();
        }

        @AfterEach
        void teardown() {
            driver.quit();
        }

        @Test
        //test is failing somewhere in the middle due to some item doesn't have 'table' text in the title
        void TestContainsText() {
            // Exercise
            driver.get("https://www.webstaurantstore.com/");
            driver.findElement(By.id("searchval")).sendKeys("stainless work table");
            driver.findElement(By.cssSelector("button[type='submit']")).click();
            int pageNumber = 1;
            //while > is active
            while (driver.findElement(By.cssSelector("li.rc-pagination-next a")).isDisplayed()){
                System.out.println("Page# " + pageNumber);
                var searchResultElements = driver.findElements(By.cssSelector("div#product_listing>div"));
                //foreach item on the search result pages
                for (WebElement element: searchResultElements
                ) {
                    assertThat(element.findElement(By.cssSelector("div#details")).getText()).contains("Table");
                }
                driver.findElement(By.className("rc-pagination-next")).click();
                pageNumber++;
            }
        }

        private static final int MAX_RETRIES = 10;
        @Test
        void TestAddToCart() throws InterruptedException {
            // Exercise
            driver.get("https://www.webstaurantstore.com/");
            driver.findElement(By.id("searchval")).sendKeys("stainless work table");
            driver.findElement(By.cssSelector("button[type='submit']")).click();
            // click last page on the search result pagination
            driver.findElement(By.cssSelector(".rc-pagination li.rc-pagination-item:nth-last-child(3)")).click();
            //find first item in a search result from the end of the list
            driver.findElement(By.cssSelector("div#ProductBoxContainer:nth-last-child(1)"))
                    .findElement(By.cssSelector("[name='addToCartButton']")).click();
            driver.findElement(By.cssSelector("div.notification__action a[href='/viewcart.cfm']")).click();
            driver.findElement(By.cssSelector(".emptyCartButton")).click();
            driver.findElement(By.cssSelector(".ReactModalPortal footer button")).click();
            SoftAssertions softAssertions = new SoftAssertions();

            //can be moved to helpers methods to more convenient usage
            //retry for 10 seconds for page re-rendering after cart is emptied
            for (int i=0; i<=MAX_RETRIES; i++)
                try {
                    softAssertions.assertThat(driver.findElement(By.cssSelector("div.cartEmpty"))
                            .getText()).contains("Your cart is empty.");
                }catch (Exception e){
                    Thread.sleep(1000);
                    if (i == MAX_RETRIES){
                        throw e;
                    }
                }
            }
        }


