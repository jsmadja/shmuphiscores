package plugins;

import org.junit.Assert;
import org.junit.Test;

public class ShmupClientTest {

    private ShmupClient sc = new ShmupClient();

    @Test
    public void should_get_login() {
        String loginById = sc.getLoginById(34938L);
        Assert.assertEquals("Laudec", loginById);
    }

    @Test
    public void should_parse_page_to_find_login() {
        String page = "\n" +
                "\t\t\tRecherche avancée \n" +
                "\t\t\n" +
                "\n" +
                "\t\t\tIndex du forum Modifier la taille de la policePanneau de contrôle de l’utilisateur\n" +
                "\t\t\t\t\t\t (0 nouveau message) •\n" +
                "\t\t\t\t\tVoir vos messages\n" +
                "\t\t\t\t\t\n" +
                "\t\t\t\tFAQMembresDéconnexion [ anzymus ]\n" +
                "<!--\n" +
                "\tvar panels = new Array('profile-panel', 'contact-panel', 'statistics-panel');\n" +
                "\n" +
                "//-->\n" +
                "Consulte un profil - LaudecLaudecContacterStatistiques\n" +
                "\t\t\n" +
                "\t\t\t\n" +
                "\tLaudecNom d’utilisateur:Laudec\n" +
                "\t\tRang:Insert CoinGroupes:Utilisateurs inscrits Ajouter un ami Ajouter un ignoré\n" +
                "\n" +
                "\t\tContacter LaudecAdresse e-mail:Envoyer un e-mail à LaudecMP:Envoyer un message privé\n" +
                "\n" +
                "\n" +
                "\t\tStatistiques de l’utilisateurInscrit le:11 Mars 2017, 01:51Dernière visite:12 Mars 2017, 02:14Messages au total:3 | Rechercher les messages de l’utilisateur(0.00% de tous les messages / 1.50 messages par jour)\n" +
                "\t\t\t\tForum le plus actif:En général...(2 messages / 66.67% des messages de l’utilisateur)Sujet le plus actif:hiscores : bugs et requêtes(2 messages / 66.67% des messages de l’utilisateur)\n" +
                "\tAller vers:\n" +
                "\t\t\tSélectionner un forum------------------Les Shmups en général   Blabla Général   Tips, conseils, méthodes...   High Scores   ShmupTubeL'émulation   Actus et discussions sur l'émulation   Au secours !Fourre-tout   Bizutage !   Fanboy Inside   Au comptoir de la salle d'arcade   IRL, meetings, multi, tournois...   Petites annonces   English ShmuppingDo it yourself !   Matériel et bricolage   Homemade et créationsA propos du site...   En général...   HEY!!! Il manque un jeu!   Doléances   Section dev (Shmup V3)\n" +
                "\t\t\n" +
                "\n" +
                "\t\t\tIndex du forum Modifier la taille de la policeL’équipe • Supprimer tous les cookies du forum • Heures au format UTC + 1 heure Powered by phpBB® Forum Software © phpBB Group\n" +
                "\n" +
                "\t\tTraduction réalisée par Maël Soucaze © 2010 phpBB.fr proSpecial Design by Abdul Turan\n" +
                "\t\t\n" +
                "\t\n" +
                "  (function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){\n" +
                "  (i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),\n" +
                "  m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)\n" +
                "  })(window,document,'script','https://www.google-analytics.com/analytics.js','ga');\n" +
                "\n" +
                "  ga('create', 'UA-1837352-23', 'auto');\n" +
                "  ga('send', 'pageview');\n" +
                "\n";

        String loginById = sc.parse(page);
        Assert.assertEquals("Laudec", loginById);
    }

}
