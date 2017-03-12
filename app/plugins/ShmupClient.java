package plugins;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;

import java.io.IOException;

public class ShmupClient {

    private WebClient webClient;

    public ShmupClient() {
        webClient = new WebClient();
    }

    public String getLoginById(Long id) {
        try {
            authenticate("anzymus", "xedy4bsa");
            HtmlPage page = webClient.getPage("http://forum.shmup.com/memberlist.php?mode=viewprofile&u=" + id);
            String textContent = page.getBody().getTextContent();
            return this.parse(textContent);
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

    String parse(String page) {
        return page.split("Nom d’utilisateur:")[1].split("\n")[0];
    }
}
