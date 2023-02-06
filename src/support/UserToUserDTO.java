package support;

import model.Administrator;
import model.Customer;
import model.Salesman;
import model.User;
import model.enums.UserType;
import web.dto.CustomerDTO;
import web.dto.SalesmanDTO;
import web.dto.UserDTO;

public class UserToUserDTO implements Converter<User, UserDTO>{

	@Override
	public UserDTO convert(User user) {
		
		UserDTO userDTO = null;
		if (user instanceof Administrator)
			userDTO = new UserDTO(user.getUsername(), user.getFirstName(), user.getLastName(), user.getGender(), user.getBirthDate(), UserType.ADMINISTRATOR);
		else if (user instanceof Salesman) {
			userDTO = new SalesmanDTO(user.getUsername(), user.getFirstName(), user.getLastName(), user.getGender(), user.getBirthDate(), UserType.SALESMAN,
									  ((Salesman)user).getManifestationIDs(), ((Salesman)user).isDeleted());
					
		}
		else if (user instanceof Customer) {
			userDTO = new CustomerDTO(user.getUsername(), user.getFirstName(), user.getLastName(), user.getGender(), user.getBirthDate(), UserType.CUSTOMER,
									  ((Customer) user).getTicketIDs(), ((Customer)user).getCustomerType(), 
									  ((Customer)user).getPoints(), ((Customer)user).getCart(), 
									  ((Customer)user).isDeleted(), ((Customer)user).isBlocked(), ((Customer)user).isSuspicious());
		}
		return userDTO;
	}
}
