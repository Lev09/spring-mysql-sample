package netgloo;

import java.util.Map;
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

		return "http://" + WordPressIp + ":" + WordPressPort;
	}

	private String getLogin(HttpServletRequest request) {
		HttpHeaders requestHeaders = new HttpHeaders();
		
		if (request.getHeader("Cookie") != null) {
			requestHeaders.add("Cookie", request.getHeader("Cookie"));	
		}
		
		HttpEntity<String> requestEntity = new HttpEntity<String>(null, requestHeaders);

		RestTemplate restTemplate = new RestTemplate();

		ResponseEntity<String> responseEntity = restTemplate.exchange(
				getWordPressAddress() + "/wp-content/plugins/my-service/my-service.php", HttpMethod.GET, requestEntity,
				String.class);

		return responseEntity.getBody();
	}

	@Autowired
	private UserDao userDao;

	@RequestMapping("/create")
	@ResponseBody
	public String create(HttpServletRequest request, String email, String name) {
		String login = getLogin(request);

		if (login != null) {


			try {
				User user = new User(email, name);
				userDao.save(user);
			} catch (Exception ex) {
				return "Error creating the user: " + ex.toString();
			}
			return "User succesfully created!" + login;
		} else {
			return "Not logged in !";
		}
	}

	@RequestMapping("/delete")
	@ResponseBody
	public String delete(HttpServletRequest request, long id) {
		String login = getLogin(request);

		if (login != null) {
			try {
				User user = new User(id);
				userDao.delete(user);
			} catch (Exception ex) {
				return "Error deleting the user:" + ex.toString();
			}
			return "User succesfully deleted!";
		} else {
			return "Not logged in !";
		}
	}

	@RequestMapping("/get-by-email")
	@ResponseBody
	public String getByEmail(HttpServletRequest request, String email) {
		String login = getLogin(request);

		if (login != null) {
			String userId;
			try {
				User user = userDao.findByEmail(email);
				userId = String.valueOf(user.getId());
			} catch (Exception ex) {
				return "User not found";
			}
			return "The user id is: " + userId;
		} else {
			return "Not logged in !";
		}
	}

	@RequestMapping("/update")
	@ResponseBody
	public String updateUser(HttpServletRequest request, long id, String email,
			String name) {
		String login = getLogin(request);

		if (login != null) {
			try {
				User user = userDao.findOne(id);
				user.setEmail(email);
				user.setName(name);
				userDao.save(user);
			} catch (Exception ex) {
				return "Error updating the user: " + ex.toString();
			}

			return "User succesfully updated!";
		} else {
			return "Not logged in !";
		}
	}

}
