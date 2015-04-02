package netgloo;

import java.util.Map;

import javax.servlet.ServletRequest;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

@Controller
public class UserController {

		private String getWordPressAddress() {
    	Map<String, String> env = System.getenv();
      String WordPressIp = env.get("WP_PORT_80_TCP_ADDR");
      String WordPressPort = env.get("WP_PORT_80_TCP_PORT");
      
      return String.format("http://%s:%s", WordPressIp, WordPressPort);
    }

    private Cookie[] getCookies(ServletRequest req) {
        //HttpServletRequest request = (HttpServletRequest) req;
        Cookie[] cookies = ((HttpServletRequest) req).getCookies();
        
        return cookies;
    }

    private String getLogin(Cookie[] wordpressCookies) {
        HttpHeaders requestHeaders = new HttpHeaders();

        if (wordpressCookies != null) {
            for(Cookie c : wordpressCookies) {
                requestHeaders.add("Cookie", c.getName() + "=" + c.getValue());
            }
        }

        HttpEntity<String> requestEntity = new HttpEntity<String>(null, requestHeaders);
        
        RestTemplate restTemplate = new RestTemplate();
        
        ResponseEntity<String> responseEntity = restTemplate.exchange(
                getWordPressAddress(), 
                HttpMethod.GET, 
                requestEntity, 
                String.class);
        
        return responseEntity.getBody();
    }    

    @Autowired
    private UserDao userDao;

    @RequestMapping("/create")
    @ResponseBody 
    public String create(HttpServletRequest request, String email, String name) {
        String login = getLogin(getCookies(request));

        if (login != null) {
            
            System.out.println(login);
            
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
    public String delete(HttpServletRequest request, long id) {
    	String login = getLogin(getCookies(request));

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
    public String getByEmail(HttpServletRequest request, String email) {
    	String login = getLogin(getCookies(request));

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
    public String updateUser(HttpServletRequest request, long id, String email, String name) {
    	String login = getLogin(getCookies(request));

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
