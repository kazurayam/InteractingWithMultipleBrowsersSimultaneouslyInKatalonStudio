import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject

import org.openqa.selenium.Dimension
import org.openqa.selenium.Point
import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver

import com.kms.katalon.core.util.KeywordUtil
import com.kms.katalon.core.webui.driver.DriverFactory
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

import flaskrtest.data.Song
import flaskrtest.data.Songs

// preparatio for ChromeDriver
String chrome_executable_path = DriverFactory.getChromeDriverPath()
System.setProperty('webdriver.chrome.driver', chrome_executable_path)

// open a Chome browser for Alice
WebDriver browser0 = new ChromeDriver()
layoutWindow(browser0, new Dimension(720, 640), new Point(0, 0))

// open a Chrome browser for Bob
WebDriver browser1 = new ChromeDriver()
layoutWindow(browser1, new Dimension(720, 640), new Point(720, 0))

// Alice logs in
login(browser0, "Alice", "AliceInTheWonderLand")

// Bob logs in
login(browser1, "Bob", "LikeARollingStone")

// Alice makes a post
post(browser0, "Alice", Songs.get(0))

// Bob makes a post
post(browser1, "Bob", Songs.get(1))

// Alice sees Bob's post

// Bob sees Alice's post

WebUI.delay(3)

// close 2 browsers
browser0.quit()
browser1.quit()


def login(WebDriver browser, String username, String password) {
	DriverFactory.changeWebDriver(browser)
	browser.navigate().to('http://127.0.0.1/')
	// ensure we are on the index page
	WebUI.verifyElementPresent(findTestObject("blog/IndexPage/h1_flaskr"), 10)
	WebUI.verifyElementPresent(findTestObject("blog/IndexPage/a_Register"), 3)
	WebUI.verifyElementPresent(findTestObject("blog/IndexPage/a_Log In"), 3)
	
	// we want to navigate to the Register page
	WebUI.click(findTestObject("blog/IndexPage/a_Register"))
	
	// make sure we are on the Register page
	WebUI.verifyElementPresent(findTestObject("auth/RegisterCredentialPage/input_Register"), 3)
	
	// we want to register a user
	WebUI.setText(findTestObject("auth/RegisterCredentialPage/input_username"), username)
	WebUI.setText(findTestObject("auth/RegisterCredentialPage/input_password"), password)
	WebUI.click(findTestObject("auth/RegisterCredentialPage/input_Register"))
	
	// check if the user is already registered
	boolean alreadyRegistered = WebUI.waitForElementPresent(findTestObject("auth/RegisterCredentialPage/div_flash"), 3)
	if (alreadyRegistered) {
		KeywordUtil.markWarning("usernamee ${username} is already registered.")
		// we are still on the Register page
		// so we want to navigate to the Log In page
		WebUI.click(findTestObject("auth/RegisterCredentialPage/a_Log In"))
	}
	
	// now we are on the Login page
	// now let us log in
	WebUI.verifyElementPresent(findTestObject("auth/LogInPage/input_Log In"), 3)
	WebUI.setText(findTestObject("auth/LogInPage/input_username"), username)
	WebUI.setText(findTestObject("auth/LogInPage/input_password"), password)
	WebUI.click(findTestObject("auth/LogInPage/input_Log In"))
	
	// now we should be are on the index page
	// make sure if he/she has successfully logged in?
	WebUI.verifyElementPresent(findTestObject("blog/IndexPage/nav_span_username", ["username": username]), 3)
}


def post(WebDriver browser, String username, Song song) {
	DriverFactory.changeWebDriver(browser)
	
	// let's start from the index page
	browser.navigate().to('http://127.0.0.1/')
	
	// we want to navigate to the CreatePost page
	WebUI.click(findTestObject("blog/IndexPage/a_New"))
	WebUI.verifyElementPresent(findTestObject("blog/CreatePostPage/button_Save"), 3)
	
	// type in the title
	String title = song.title + " --- " + song.by
	WebUI.sendKeys(findTestObject("blog/CreatePostPage/input_title"), title)
	// type in the body
	WebUI.sendKeys(findTestObject("blog/CreatePostPage/input_body"), song.lyric)
	// save the post
	WebUI.click(findTestObject("blog/CreatePostPage/button_Save"))
	
	// now we are on the index page
	// make sure that the 1st article is the song just posted by username 
	String title_of_the_latest_post = WebUI.getText(findTestObject("blog/IndexPage/latestPost_title"))
	assert title_of_the_latest_post == title
	String about_of_the_latest_post = WebUI.getText(findTestObject("blog/IndexPage/latestPost_about"))
	assert about_of_the_latest_post.contains(username)
	String body_of_the_latest_post = WebUI.getText(findTestObject("blog/IndexPage/latestPost_body"))
	assert body_of_the_latest_post == song.lyric
}

def layoutWindow(WebDriver browser, Dimension dimension, Point point) {
	browser.manage().window().setSize(dimension)
	browser.manage().window().setPosition(point)
}