package unit;
import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.html.*;
import java.io.*;
import java.net.URLDecoder;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.stream.Collectors;
import org.apache.commons.logging.LogFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import model.User;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class Crawler {
    private static final String INPUT_EMAIL_NAME = "email";
    private static final String INPUT_PASSWORD_NAME = "pass";
    private static final String email = "l.yaruk1993@gmail.com";
    private static final String password = "14111993qw";
    private WebClient webClient;
    private WebDriver driver;

    Crawler() {
        initWebClient();
    }

    User getInfo(String userToFind) {
        try {
            LogManager.getLogManager().reset();
            System.out.println("Parsing " + userToFind);
            loginWithSelenium();

            User user = new User();
            searchFriends(userToFind, user);
            searchUserLikes(userToFind, user);
            searchUserInfo(userToFind, user);

            Writer writer = new BufferedWriter(new FileWriter(new File("_text.txt"), true));
            writer.write(user.toString());
            writer.close();

            return user;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void searchFriends(String url, User user) throws IOException {
        String toParse;
        if (url.contains("profile.php")) toParse = url + "&sk=friends";
        else toParse = url + "/friends";
        driver.get(toParse);

        if (!isElementNotDisplay("pagelet_timeline_medley_photos")) {
            while (isElementNotDisplay("pagelet_timeline_medley_photos")){
                ((JavascriptExecutor)driver).executeScript("window.scrollTo(0, document.body.scrollHeight);");
                try { Thread.sleep(500); }
                catch (InterruptedException e) { e.printStackTrace(); }
            }
        }

        WebElement pagelet_timeline_medley_friends = driver.findElement(By.id("pagelet_timeline_medley_friends"));
        List<WebElement> li1 = pagelet_timeline_medley_friends.findElements(By.tagName("li"));
        user.friendsList.addAll(li1.stream()
                .filter(li -> li.findElements(By.tagName("a")).get(0).getAttribute("href").endsWith("friends_tab"))
                .map(li -> transformRef(li.findElements(By.tagName("a")).get(0).getAttribute("href"))).collect(Collectors.toList()));
    }

    private void searchUserInfo(String url, User user) throws IOException {
        loginWithHTMLUnit();
        HtmlPage userPage = webClient.getPage(url);
        user.ref = url;

        if (userPage.asXml().contains("intro_container_id")){
            String inner = null;
            if (userPage.getElementById("intro_container_id") != null) {
                inner = userPage.getElementById("intro_container_id").asXml();
            }
            else {
                DomNodeList<DomElement> codeList = userPage.getElementsByTagName("code");
                for (DomElement de : codeList) {
                    if (de.asXml().contains("intro_container_id")) {
                        inner = de.asXml();
                        inner = inner.substring(inner.indexOf("<!-- ") + 5, inner.lastIndexOf("-->"));
                    }
                }
            }
            Document doc;
            doc = Jsoup.parse(inner);

            Elements infoItems = doc.select("ul").get(0).getElementsByTag("li");
            for (Element el :infoItems) {
                Element div = el.select("div").last();
                if (div.ownText().contains(toISO8859("Из"))) {
                    user.birthPlace = URLDecoder.decode(transformRef(div.select("a").get(0).attr("href")),"UTF-8");
                }
                if (div.ownText().contains(toISO8859("Живет в"))) {
                    user.location = URLDecoder.decode(transformRef(div.select("a").get(0).attr("href")),"UTF-8");
                }
                if (div.ownText().contains(toISO8859("Училась")) ||
                        div.ownText().contains(toISO8859("Учился"))) {
                    user.education.add(URLDecoder.decode(transformRef(div.select("a").get(0).attr("href")),"UTF-8"));
                }
                if (div.ownText().contains(toISO8859("Работала")) ||
                        div.ownText().contains(toISO8859("Работал"))) {
                    user.previousWorks.add(URLDecoder.decode(transformRef(div.select("a").get(0).attr("href")),"UTF-8"));
                }
                if (div.ownText().contains(toISO8859("Работает")) ||
                        (div.ownText().contains(toISO8859("Президент")))) {
                    user.currentWork = URLDecoder.decode(transformRef(div.select("a").get(0).attr("href")),"UTF-8");
                }
                if (div.ownText().contains(toISO8859("Подписчики:"))) {
                    String s = div.select("a").get(0).text();
                    s = s.substring(0,s.indexOf(toISO8859(" человек")));
                    user.followersCount = Integer.parseInt(changeArray(s));
                }
            }
        }
        else System.out.println("user info is hide");

        user.name = userPage.getTitleText().split("\\s")[0];
        user.secondName = userPage.getTitleText().split("\\s")[1];

        DomNodeList<HtmlElement> list = userPage.getElementsByTagName("ol").get(1).getElementsByTagName("li");
        String refer = null;
        for (HtmlElement he : list) {
            if (he.asXml().contains(toISO8859("Друзья"))){
                if (he.getElementsByTagName("span").size() > 2) {
                    refer = he.getElementsByTagName("span").get(2).getElementsByTagName("a").get(0).getTextContent();
                }
            }
        }

        if (refer != null) {
            user.friendsCount = Integer.parseInt(changeArray(refer));
        }
        else {
            DomNodeList<DomElement> codeList = userPage.getElementsByTagName("code");
            for (DomElement de : codeList) {
                if (de.asXml().contains("timelineReportContainer")) {
                    refer = de.asXml();
                    refer = refer.substring(refer.indexOf("<!-- ") + 5, refer.lastIndexOf("-->"));
                }
            }
            if (refer != null){
                Document doc = Jsoup.parse(refer);
                Elements friendsCountRef = doc.getElementsByTag("a");
                friendsCountRef.stream()
                        .filter(e -> e.attr("href").equals(url + "/friends"))
                        .filter(e -> e.ownText().length() > 0)
                        .forEach(e -> user.friendsCount = Integer.parseInt(changeArray(e.ownText())));
            }
        }
    }

    private void searchUserLikes(String url, User user) throws IOException {
        String toParse;
        if (url.contains("profile.php")) toParse = url + "&sk=likes";
        else toParse = url + "/likes";
        driver.get(toParse);

        if (!isElementNotDisplay("pagelet_timeline_medley_events")){
            while (isElementNotDisplay("pagelet_timeline_medley_events")){
                ((JavascriptExecutor)driver).executeScript("window.scrollTo(0, document.body.scrollHeight);");
                try { Thread.sleep(1000); }
                catch (InterruptedException e) { e.printStackTrace(); }
            }
        }

        WebElement pagelet_timeline_medley_friends = driver.findElement(By.id("pagelet_timeline_medley_likes"));
        List<WebElement> listOfUserLikes = pagelet_timeline_medley_friends.findElements(By.tagName("li"));
        for (WebElement li : listOfUserLikes) {
            if (user != null) {
                user.userLikes.add(transformRef(li.findElements(By.tagName("a")).get(0).getAttribute("href")));
            }
        }
        driver.quit();
    }

    private void initWebClient() {
        webClient = new WebClient(BrowserVersion.CHROME);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setJavaScriptEnabled(true);
        webClient.setAjaxController(new NicelyResynchronizingAjaxController());
        webClient.waitForBackgroundJavaScript(60000);
        webClient.setJavaScriptTimeout(99999);
        LogManager.getLogManager().reset();
        LogFactory.getFactory().setAttribute("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog");
        java.util.logging.Logger.getLogger("com.gargoylesoftware.unit").setLevel(Level.OFF);
        java.util.logging.Logger.getLogger("org.apache.commons.httpclient").setLevel(Level.OFF);
        java.util.logging.Logger.getLogger("com.gargoylesoftware.unit.javascript.StrictErrorReporter").setLevel(Level.OFF);
        java.util.logging.Logger.getLogger("com.gargoylesoftware.unit.javascript.host.ActiveXObject").setLevel(Level.OFF);
        java.util.logging.Logger.getLogger("com.gargoylesoftware.unit.javascript.host.html.HTMLDocument").setLevel(Level.OFF);
        java.util.logging.Logger.getLogger("com.gargoylesoftware.unit.html.HtmlScript").setLevel(Level.OFF);
        java.util.logging.Logger.getLogger("com.gargoylesoftware.unit.javascript.host.WindowProxy").setLevel(Level.OFF);
        java.util.logging.Logger.getLogger("org.apache").setLevel(Level.OFF);
        org.apache.log4j.Logger.getRootLogger().setLevel(org.apache.log4j.Level.OFF);
    }

    private String transformRef(String originHRef){
        if (originHRef.startsWith("https://www.facebook.com/") && originHRef.contains("profile.php?id"))
            return originHRef.substring(0,originHRef.indexOf("&"));
        if (originHRef.contains("?"))
            return originHRef.substring(0,originHRef.lastIndexOf("?"));
        else return originHRef;
    }

    private String changeArray(String refer){
        for (int i=0;i<refer.length();i++)
            if (refer.codePointAt(i) == 160) {
                char[] copy = new char[refer.length()-1];
                System.arraycopy(refer.toCharArray(), 0, copy, 0, i);
                System.arraycopy(refer.toCharArray(), i+1, copy, i, refer.length()-i-1);
                refer = new String(copy);
            }
        return refer;
    }

    private void loginWithHTMLUnit() throws IOException {
        HtmlPage loginPage = webClient.getPage("https://www.facebook.com/login.php");

        HtmlForm loginForm = null;
        for (HtmlForm tForm : loginPage.getForms()) {
            if (tForm.getId().equalsIgnoreCase("login_form")) {
                loginForm = tForm;
                break;
            }
        }

        if (loginForm != null) {
            loginForm.getInputByName(INPUT_EMAIL_NAME).setValueAttribute(email);
            loginForm.getInputByName(INPUT_PASSWORD_NAME).setValueAttribute(password);
            try {
                loginForm.getInputByName("login").click();
                System.out.println("found input");
            }
            catch (ElementNotFoundException ex){
                loginForm.getButtonByName("login").click();
                System.out.println("found button");
            }
        }
        System.out.println("login successfull");
    }

    private String toISO8859(String s) throws UnsupportedEncodingException {
        return new String(s.getBytes("ISO-8859-1"));
    }

    private boolean isElementNotDisplay(String id){
        return driver.findElements(By.id(id)).size() < 1;
    }

    private void loginWithSelenium(){
        System.setProperty("webdriver.chrome.driver","D:\\.desktop\\chrome_loads\\chromedriver.exe");
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("--start-maximized");
        chromeOptions.addArguments("--disable-notifications");
        driver = new ChromeDriver(chromeOptions);
        driver.get("https://www.facebook.com/login.php");
        driver.findElement(By.id("email")).sendKeys(email);
        driver.findElement(By.id("pass")).sendKeys(password);
        driver.findElement(By.name("login")).click();
    }
}