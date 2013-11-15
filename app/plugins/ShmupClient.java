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
            authenticate("anzymus", System.getProperty("shmup.password"));
            HtmlPage page = webClient.getPage("http://forum.shmup.com/memberlist.php?mode=viewprofile&u=" + id);
            return page.getBody().getTextContent().split("connexion \\[ ")[1].split(" \\]")[0];
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private void authenticate(String login, String password) throws IOException {
        HtmlPage loginPage = webClient.getPage("http://forum.shmup.com");
        HtmlForm loginForm = fillForm(loginPage, login, password);
        String contentResult = submitForm(loginForm);
        //return contentResult.contains("Vous vous êtes connecté avec succès.");
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

}
