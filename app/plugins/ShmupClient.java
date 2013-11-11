package plugins;

import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.html.*;
import com.gargoylesoftware.htmlunit.javascript.JavaScriptEngine;
import com.gargoylesoftware.htmlunit.javascript.JavaScriptErrorListener;
import com.google.common.io.Files;

import java.io.File;
import java.io.IOException;

public class ShmupClient {

    private WebClient webClient;

    public ShmupClient() {
        webClient = new WebClient(BrowserVersion.FIREFOX_3_6);
    }

    public static void main(String[] args) throws IOException {
        ShmupClient shmupClient = new ShmupClient();
        if ((shmupClient.authenticate("anzymus", ""))) {
            shmupClient.post("http://forum.shmup.com/viewtopic.php?f=3&t=18684", "bonjour ceci est un test");
        }
    }

    private void post(String target, String message) throws IOException {
        target = target.replace("viewtopic.php?", "posting.php?mode=reply&");
        System.err.println(target);
        HtmlPage formPage = webClient.getPage(target);
        HtmlTextArea textArea = (HtmlTextArea) formPage.getElementsByName("message").get(0);
        textArea.setText(message);
        HtmlSubmitInput submit = (HtmlSubmitInput) formPage.getElementsByName("post").get(0);

        HtmlCheckBoxInput check = (HtmlCheckBoxInput) formPage.getElementsByName("disable_magic_url").get(0);
        check.setChecked(true);

        Files.write(formPage.asXml().getBytes(), new File("/tmp/pouet_form.html"));

        HtmlPage result = submit.click();
        Files.write(result.asXml().getBytes(), new File("/tmp/pouet.html"));
        webClient.closeAllWindows();
    }

    private boolean authenticate(String login, String password) throws IOException {
        HtmlPage loginPage = webClient.getPage("http://forum.shmup.com");
        HtmlForm loginForm = fillForm(loginPage, login, password);
        String contentResult = submitForm(loginForm);
        return contentResult.contains("Vous vous êtes connecté avec succès.");
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
