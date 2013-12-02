package plugins;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;
import com.google.common.io.Files;

import java.io.File;
import java.io.IOException;

public class ShmupClient {

    private WebClient webClient;

    public ShmupClient() {
        webClient = new WebClient();
    }

/*
    public static void main(String[] args) {
        new ShmupClient().getids();
    }
    public String getids() {
        try {
            StringBuilder stringBuilder = new StringBuilder();
            authenticate("anzymus", System.getProperty("shmup.password"));
            for (int i = 0; i <= 33510; i++) {
                try {
                    HtmlPage page = webClient.getPage("http://forum.shmup.com/memberlist.php?mode=viewprofile&u=" + i);
                    String login = page.getBody().getTextContent().split("Consulte un profil - ")[1].split(" ")[0];
                    String s = "INSERT INTO player (`id`, `name`, `shmup_user_id`, `created_at`, `updated_at`) VALUES (NULL, '" + login + "', '" + i + "', CURTIME(), CURTIME());";
                    System.err.println(s);
                    stringBuilder.append(s).append("\n");
                } catch (ArrayIndexOutOfBoundsException e) {
                }
            }
            Files.write(stringBuilder.toString().getBytes(), new File("/tmp/users.sql"));
            return "";
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
*/

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

}
