package plugins;

import com.gargoylesoftware.htmlunit.CookieManager;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;
import com.gargoylesoftware.htmlunit.util.Cookie;

import java.io.IOException;

public class ShmupClient {

    private WebClient webClient;

    public ShmupClient() {
        webClient = new WebClient();
    }

    public String getLoginById(Long id) {
        try {
            authenticate("anzymus", System.getProperty("shmup.password"));
            HtmlPage page = webClient.getPage("http://forum.shmup.com/memberlist.php?mode=viewprofile&u=" + id);
            return page.getBody().getTextContent().split("Consulte un profil - ")[1].split(" ")[0];
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private void authenticate(String login, String password) throws IOException {
        HtmlPage loginPage = webClient.getPage("http://forum.shmup.com");
        HtmlForm loginForm = fillForm(loginPage, login, password);
        submitForm(loginForm);
    }

    private HtmlForm fillForm(HtmlPage loginPage, String login, String password) {
        HtmlForm loginForm = loginPage.getForms().get(0);
        HtmlPasswordInput passwordTextfield = loginForm.getInputByName("password");
        passwordTextfield.setValueAttribute(password);
        HtmlTextInput loginTextfield = loginForm.getInputByName("username");
        loginTextfield.setValueAttribute(login);
        return loginForm;
    }

    private String submitForm(HtmlForm loginForm) throws IOException {
        HtmlSubmitInput submitButton = loginForm.getInputByName("login");
        HtmlPage resultPage = submitButton.click();
        return resultPage.asText();
    }

    public static void main(String[] args) throws IOException {
        // curl 'http://forum.shmup.com/posting.php?mode=reply&f=3&sid=0792e04c14a1aec2e59330b1a5b49a3c&t=18680' -H 'Cookie: phpbb3_axtcz_u=33489; phpbb3_axtcz_k=23c0adce886a4d0e; phpbb3_axtcz_sid=0792e04c14a1aec2e59330b1a5b49a3c' -H 'Origin: http://forum.shmup.com' -H 'Accept-Encoding: gzip,deflate,sdch' -H 'Accept-Language: fr-FR,fr;q=0.8,en;q=0.6' -H 'User-Agent: Mozilla/5.0 (iPhone; CPU iPhone OS 6_0 like Mac OS X) AppleWebKit/536.26 (KHTML, like Gecko) Version/6.0 Mobile/10A5376e Safari/8536.25' -H 'Content-Type: application/x-www-form-urlencoded' -H 'Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8' -H 'Cache-Control: max-age=0' -H 'Referer: http://forum.shmup.com/posting.php?mode=reply&f=3&t=18680' -H 'Connection: keep-alive' --data 'subject=Re%3A+%5BProjet%5D+Mise+a+jour+des+scores+automatique&addbbcode20=100&message=%5Bceci+est+un+test%5D&attach_sig=on&notify=on&topic_cur_post_id=437174&lastclick=1390595889&post=Envoyer&creation_time=1390595889&form_token=238a258f4f3a813a9e2b1169e4163ea7c5ee9170' --compressed

        /*
         POST /posting.php?mode=reply&f=3&sid=0792e04c14a1aec2e59330b1a5b49a3c&t=18680 HTTP/1.1
        Host: forum.shmup.com
        Connection: keep-alive
        Content-Length: 268
        Cache-Control: max-age=0
        Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,q=0.8
Origin: http://forum.shmup.com
User-Agent: Mozilla/5.0 (iPhone; CPU iPhone OS 6_0 like Mac OS X) AppleWebKit/536.26 (KHTML, like Gecko) Version/6.0 Mobile/10A5376e Safari/8536.25
Content-Type: application/x-www-form-urlencoded
Referer: http://forum.shmup.com/posting.php?mode=reply&f=3&t=18680
Accept-Encoding: gzip,deflate,sdch
Accept-Language: fr-FR,fr;q=0.8,en;q=0.6
Cookie: phpbb3_axtcz_u=33489; phpbb3_axtcz_k=23c0adce886a4d0e; phpbb3_axtcz_sid=0792e04c14a1aec2e59330b1a5b49a3c
*/

        ShmupClient shmupClient = new ShmupClient();
        CookieManager cookieManager = shmupClient.webClient.getCookieManager();
        cookieManager.addCookie(new Cookie("forum.shmup.com", "Origin", "http://forum.shmup.com"));
        cookieManager.addCookie(new Cookie("forum.shmup.com", "phpbb3_axtcz_u", "33489"));
        cookieManager.addCookie(new Cookie("forum.shmup.com", "phpbb3_axtcz_k", "23c0adce886a4d0e"));
        cookieManager.addCookie(new Cookie("forum.shmup.com", "phpbb3_axtcz_sid", "0792e04c14a1aec2e59330b1a5b49a3c"));
        HtmlPage page = shmupClient.webClient.getPage("http://forum.shmup.com/viewtopic.php?f=3&t=18680");
        String textContent = page.getTextContent();
        System.err.println(textContent);
        System.err.println(page.getBody().getTextContent());
    }

}
