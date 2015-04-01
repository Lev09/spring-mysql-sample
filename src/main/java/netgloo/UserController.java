package netgloo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

@Controller
public class UserController {

    private String getLogin(String wordpressCookie) {
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("Cookie", "wordpressCookie=" + wordpressCookie);
        HttpEntity<String> requestEntity = new HttpEntity<String>(null, requestHeaders);
        
        RestTemplate restTemplate = new RestTemplate();
        
        ResponseEntity<String> responseEntity = restTemplate.exchange(
                "https://localhost/wp-content/plugins/my-service/my-service.php", 
                HttpMethod.GET, 
                requestEntity, 
                String.class);
        
        return responseEntity.getBody();
    }

    @Autowired
    private UserDao userDao;

    @RequestMapping("/create")
    @ResponseBody 
    public String create(@CookieValue(value = "wordpressCookie") String wordpressCookie, String email, String name) {
        String login = getLogin(wordpressCookie);

        if (login != null) {
            try {
                User user = new User(email, name);
                userDao.save(user);
            } 
            catch (Exception ex) {
                return "Error creating the user: " + ex.toString();
            }
            return "User succesfully created!";
        } 
        else {
            return "Not logged in !";
        }
    }

    @RequestMapping("/delete")
    @ResponseBody 
    public String delete(@CookieValue(value = "wordpressCookie") String wordpressCookie, long id) {
        String login = getLogin(wordpressCookie);

        if (login != null) {
            try {
                User user = new User(id);
                userDao.delete(user);
            } 
            catch (Exception ex) {
                return "Error deleting the user:" + ex.toString();
            }
            return "User succesfully deleted!";
        }
        else {
            return "Not logged in !";
        }
    }

    @RequestMapping("/get-by-email")
    @ResponseBody
    public String getByEmail(@CookieValue(value = "wordpressCookie") String wordpressCookie, String email) {
        String login = getLogin(wordpressCookie);

        if (login != null) {            
            String userId;
            try {
                User user = userDao.findByEmail(email);
                userId = String.valueOf(user.getId());
            } 
            catch (Exception ex) {
                return "User not found";
            }
            return "The user id is: " + userId;
        }
        else{
            return "Not logged in !";
        }
    }

    @RequestMapping("/update")
    @ResponseBody
    public String updateUser(@CookieValue(value = "wordpressCookie") String wordpressCookie, long id, String email, String name) {
        String login = getLogin(wordpressCookie);

        if (login != null) {
            try {
                User user = userDao.findOne(id);
                user.setEmail(email);
                user.setName(name);
                userDao.save(user);
            } 
            catch (Exception ex) {
                return "Error updating the user: " + ex.toString();
            }
            
            return "User succesfully updated!";
        }
        else{
            return "Not logged in !";
        }
    }

}
