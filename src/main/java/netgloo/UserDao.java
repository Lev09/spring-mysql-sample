package netgloo;

import javax.transaction.Transactional;

import org.springframework.data.repository.CrudRepository;

@Transactional
public interface UserDao extends CrudRepository<User, Long> {

  /**
   * This method is not implemented and its working code will be
   * automagically generated from its signature by Spring Data JPA.
   *
   * @param email the user email.
   * @return the user having the passed email or null if no user is found.
   */
  public User findByEmail(String email);

} // class UserDao
