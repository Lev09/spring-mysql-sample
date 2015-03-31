package netgloo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * A class to test the interaction with MySQL database using the UserDao class.
 *
 * @author netgloo
 */
@Controller
public class UserController {

	// ==============
	// PRIVATE FIELDS
	// ==============

	private Object getAuthorizedUser() {
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.add("Cookie", "JSESSIONID=" + session.getValue());
		HttpEntity requestEntity = new HttpEntity(null, requestHeaders);
		ResponseEntity rssResponse = restTemplate
				.exchange(
						"https://localhost/wp-content/plugins/my-service/my-service.php",
						HttpMethod.GET, requestEntity, Rss.class);
		Rss rss = rssResponse.getBody();

		ObjectMapper mapper = new ObjectMapper();
		Object user = mapper.readValue(rss, Object.class);

		return user;

	}

	@Autowired
	private UserDao _userDao;

	// ==============
	// PUBLIC METHODS
	// ==============

	/**
	 * Create a new user and save it in the database.
	 * 
	 * @param email
	 *            user email
	 * @param name
	 *            use name
	 * @return a string describing if the user is succesfully created or not.
	 */
	@RequestMapping("/create")
	@ResponseBody
	public String create(String email, String name) {
		Object current_user = getAuthorizedUser();

		if (current_user.login) {

			try {
				User user = new User(email, name);
				_userDao.save(user);
			} catch (Exception ex) {
				return "Error creating the user: " + ex.toString();
			}
			return "User succesfully created!";

		} else {
			return "Not logged in !";
		}
	}

	/**
	 * Delete the user having the passed id.
	 * 
	 * @param id
	 *            the id of the user to delete
	 * @return a string describing if the user is succesfully deleted or not.
	 */
	@RequestMapping("/delete")
	@ResponseBody
	public String delete(long id) {
		Object current_user = getAuthorizedUser();

		if (current_user.login) {
			try {
				User user = new User(id);
				_userDao.delete(user);
			} catch (Exception ex) {
				return "Error deleting the user:" + ex.toString();
			}
			return "User succesfully deleted!";
		}else{
			return "Not logged in !";
		}
	}

	/**
	 * Return the id for the user having the passed email.
	 * 
	 * @param email
	 *            the email to search in the database.
	 * @return the user id or a message error if the user is not found.
	 */
	@RequestMapping("/get-by-email")
	@ResponseBody
	public String getByEmail(String email) {
		Object current_user = getAuthorizedUser();

		if (current_user.login) {			
			String userId;
			try {
				User user = _userDao.findByEmail(email);
				userId = String.valueOf(user.getId());
			} catch (Exception ex) {
				return "User not found";
			}
			return "The user id is: " + userId;
		}else{
			return "Not logged in !";
		}
	}

	/**
	 * Update the email and the name for the user in the database having the
	 * passed id.
	 * 
	 * @param id
	 *            the id for the user to update.
	 * @param email
	 *            the new email.
	 * @param name
	 *            the new name.
	 * @return a string describing if the user is succesfully updated or not.
	 */
	@RequestMapping("/update")
	@ResponseBody
	public String updateUser(long id, String email, String name) {
		Object current_user = getAuthorizedUser();

		if (current_user.login) {
			try {
				User user = _userDao.findOne(id);
				user.setEmail(email);
				user.setName(name);
				_userDao.save(user);
			} catch (Exception ex) {
				return "Error updating the user: " + ex.toString();
			}
			return "User succesfully updated!";
		}else{
			return "Not logged in !";
		}
	}

} // class UserController
